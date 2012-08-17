package org.kwince.osem.es;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest.OpType;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.engine.DocumentAlreadyExistsException;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WrapperQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.kwince.osem.OsemSession;
import org.kwince.osem.es.cfg.Configuration;
import org.kwince.osem.es.exception.DocumentExistsException;
import org.kwince.osem.es.metadata.DocumentMetadata;
import org.kwince.osem.exception.MappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * OsemSession implementation that uses elastic search as the underlying
 * document database.
 * 
 * @author Allan Ramirez (ramirezag@gmail.com)
 * 
 */
public class OsemSessionImpl implements OsemSession {
    /**
     * 
     */
    private static final long serialVersionUID = 8901893954322582711L;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private Configuration config;
    private Client client;

    private String indexName;

    public OsemSessionImpl(EsOsemSessionFactory sessionFactory, String indexName) {
        Assert.notNull(sessionFactory);
        Assert.notNull(sessionFactory.getClient());
        Assert.hasLength(indexName);
        this.client = sessionFactory.getClient();
        this.config = sessionFactory.getConfiguration();
        this.indexName = indexName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kwince.osem.OsemSession#count(java.lang.Class)
     */
    public <T> int count(Class<T> documentClass) {
        if (!config.isDocument(documentClass)) {
            throw new MappingException(String.format("%s is not an document.", documentClass));
        }
        log.info("Fetching {} total records.", getType(documentClass));
        CountResponse count = client.prepareCount(indexName).setTypes(getType(documentClass)).execute().actionGet();
        return Long.valueOf(count.count()).intValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kwince.osem.OsemSession#delete(java.lang.Class,
     * java.io.Serializable)
     */
    public <T> void delete(Class<T> documentClass, Serializable id) {
        if (!config.isDocument(documentClass)) {
            throw new MappingException(String.format("%s is not an document.", documentClass));
        }
        log.info("Deleting record in {} type with id {}", getType(documentClass), id);
        client.prepareDelete(indexName, getType(documentClass), id.toString()).setRefresh(true).execute().actionGet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kwince.osem.OsemSession#deleteAll(java.lang.Class)
     */
    public void deleteAll(Class<?> documentClass) {
        if (!config.isDocument(documentClass)) {
            throw new MappingException(String.format("%s is not an document.", documentClass));
        }
        log.info("Deleting all records in {} type", getType(documentClass));
        client.prepareDeleteByQuery(indexName).setTypes(getType(documentClass)).setQuery(QueryBuilders.matchAllQuery()).execute()
                .actionGet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kwince.osem.OsemSession#find(java.lang.Class,
     * java.io.Serializable)
     */
    public <T> T find(Class<T> documentClass, Serializable primaryKey) {
        if (!config.isDocument(documentClass)) {
            throw new MappingException(String.format("%s is not an document.", documentClass));
        }
        log.info("Getting record in {} with id {}", getType(documentClass), primaryKey);
        GetResponse response = client.prepareGet(indexName, getType(documentClass), primaryKey.toString()).execute().actionGet();
        try {
            Map<String, Object> sourceMap = response.getSource();
            return response.exists() ? config.createObject(documentClass, sourceMap) : null;
        } catch (Exception e) {
            throw new MappingException("Failed to convert json source to " + documentClass.getName(), e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kwince.osem.OsemSession#findAll(java.lang.Class)
     */
    public <T> List<T> findAll(Class<T> documentClass) {
        if (!config.isDocument(documentClass)) {
            throw new MappingException(String.format("%s is not an document.", documentClass));
        }
        SearchResponse response = client.prepareSearch(indexName).setTypes(getType(documentClass)).execute().actionGet();
        List<T> results = new ArrayList<T>();
        for (SearchHit hit : response.getHits()) {
            results.add(config.createObject(documentClass, hit.getSource()));
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kwince.osem.OsemSession#findAll(java.lang.Class,
     * java.lang.String)
     */
    public <T> List<T> findAll(Class<T> documentClass, String jsonQuery) {
        WrapperQueryBuilder wrapper = QueryBuilders.wrapperQuery(jsonQuery);
        SearchResponse response = client.prepareSearch().setIndices(indexName).setTypes(getType(documentClass)).setQuery(wrapper)
                .execute().actionGet();
        List<T> results = new ArrayList<T>();
        for (SearchHit hit : response.getHits()) {
            results.add(config.createObject(documentClass, hit.getSource()));
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kwince.osem.OsemSession#save(java.lang.Object)
     */
    public String save(Object document) throws DocumentExistsException {
        if (!config.isDocument(document)) {
            throw new MappingException(String.format("%s is not an document.", document.getClass()));
        }
        String id = getIdValue(document);
        String type = getType(document.getClass());

        log.info("Saving {} with id {}", type, id);
        try {
            Map<String, Object> sourceMap = config.createSourceMap(document);
            log.info("Saving {} with id {}", type, id);
            IndexRequestBuilder request = client.prepareIndex(indexName, type);
            if (StringUtils.isNotBlank(id)) {
                request = request.setId(id);
            }
            IndexResponse response = request.setOpType(OpType.CREATE).setRefresh(true).setSource(sourceMap).execute().actionGet();
            return response.getId();
        } catch (DocumentAlreadyExistsException e) {
            String message = String.format("%s with id %s already exists.", type, id);
            log.error(message, e);
            throw new DocumentExistsException(message);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kwince.osem.OsemSession#saveOrUpdate(java.lang.Object)
     */
    public void saveOrUpdate(Object document) {
        if (!config.isDocument(document)) {
            throw new MappingException(String.format("%s is not an document.", document.getClass()));
        }
        String id = getIdValue(document);
        String type = getType(document.getClass());
        log.info("Save or update {} with id {}", type, id);
        Map<String, Object> sourceMap = config.createSourceMap(document);
        IndexRequestBuilder request = client.prepareIndex(indexName, type);
        if (StringUtils.isNotBlank(id)) {
            request = request.setId(id);
        }
        request.setRefresh(true).setSource(sourceMap).execute().actionGet();
    }

    protected String getIndexName() {
        return indexName;
    }

    protected <T> String getType(Class<T> clazz) throws MappingException {
        return config.getDocumentName(clazz);
    }

    private String getIdValue(Object document) throws MappingException {
        Class<?> cls = document.getClass();
        DocumentMetadata metadata = config.getDocument(cls);
        if (document != null && metadata.getIdField() != null) {
            Field idField = metadata.getIdField();
            try {
                Method method = metadata.getGetter(idField);
                Object value = method.invoke(document);
                if (String.class == idField.getType()) {
                    // Elastic Search automatically generates hash id if null
                    return (String) value;
                } else {
                    if (Number.class.isAssignableFrom(ClassUtils.primitiveToWrapper(idField.getType()))) {
                        Long longVal = Long.valueOf(value.toString());
                        if (longVal > 0) {
                            return longVal.toString();
                        } else {
                            throw new MappingException("Number id field values must be more than 0.");
                        }
                    } else {
                        if (value == null) {
                            throw new MappingException(String.format("Id field %s has no value.", cls.getName() + idField.getName()));
                        }
                        return value.toString();
                    }
                }
            } catch (Exception e) {
                log.error("Exception when invoking getters for " + cls.getName() + "." + idField.getName(), e);
                throw new MappingException(String.format("Exception when invoking getters for %s", cls.getName() + idField.getName()), e);
            }
        } else {
            throw new MappingException(String.format("%s has no annotated id.", cls.getName()));
        }
    }

}
