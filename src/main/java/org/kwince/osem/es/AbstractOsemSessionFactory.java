package org.kwince.osem.es;

import java.io.IOException;

import org.elasticsearch.client.Client;
import org.kwince.osem.OsemSession;
import org.kwince.osem.es.annotation.Document;
import org.kwince.osem.es.cfg.Configuration;
import org.kwince.osem.exception.MappingException;
import org.kwince.osem.exception.ModificationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public abstract class AbstractOsemSessionFactory implements EsOsemSessionFactory, InitializingBean {
    private static final String RESOURCE_PATTERN = "/**/*.class";
    private static final String DEFAULT_ES_PROPERTY_LOCATION = "META-INF/es_settings.properties";
    protected ThreadLocal<OsemSession> threadLocal = new ThreadLocal<OsemSession>();

    /**
     * Required indexName. In ES, indices are table equivalent in RDBMS system
     */
    protected String indexName;

    private String settingsLocation = DEFAULT_ES_PROPERTY_LOCATION;

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private TypeFilter documentTypeFilter = new AnnotationTypeFilter(Document.class, false);

    private Configuration config;

    private String[] packagesToScan;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.kwince.osem.es.EsOsemSessionFactory#setIndexName(java.lang.String)
     */
    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.kwince.osem.es.EsOsemSessionFactory#setSettingsLocation(java.lang
     * .String)
     */
    public void setSettingsLocation(String settingsLocation) {
        this.settingsLocation = settingsLocation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.kwince.osem.es.EsOsemSessionFactory#setPackagesToScan(java.lang.String
     * [])
     */
    public void setPackagesToScan(String[] packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kwince.osem.es.EsOsemSessionFactory#getConfiguration()
     */
    public Configuration getConfiguration() {
        return config;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        Assert.hasLength(indexName);
        config = new Configuration();
        config.setSettingsLocation(settingsLocation);
        scanPackages(config);
        getConfiguration().build();
        initEsConfig();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kwince.osem.OsemSessionFactory#getCurrentSession()
     */
    public OsemSession getCurrentSession() {
        OsemSession session = threadLocal.get();
        if (session == null) {
            session = new OsemSessionImpl(this, indexName);
            threadLocal.set(session);
        }
        return session;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kwince.osem.OsemSessionFactory#removeCurrentSession()
     */
    public void removeCurrentSession() {
        threadLocal.remove();
    }

    protected void scanPackages(Configuration config) {
        if (this.packagesToScan != null) {
            try {
                for (String pkg : this.packagesToScan) {
                    String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(pkg)
                            + RESOURCE_PATTERN;
                    Resource[] resources = this.resourcePatternResolver.getResources(pattern);
                    MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);
                    for (Resource resource : resources) {
                        if (resource.isReadable()) {
                            MetadataReader reader = readerFactory.getMetadataReader(resource);
                            String className = reader.getClassMetadata().getClassName();
                            if (documentTypeFilter.match(reader, readerFactory)) {
                                config.addAnnotatedClass(this.resourcePatternResolver.getClassLoader().loadClass(className));
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                throw new MappingException("Failed to load annotated classes from classpath", ex);
            } catch (ClassNotFoundException ex) {
                throw new MappingException("Failed to load annotated classes from classpath", ex);
            } catch (ModificationException ex) {
                throw new MappingException("Failed to load annotated classes from classpath", ex);
            }
        }
    }

    /**
     * Additional configuration that subclasses wants to perform after
     * {@link Configuration} is completely initialized. This is the perfect
     * place for initializing {@link Client} type that subclasses want's to use.
     * 
     * @throws Exception
     */
    protected abstract void initEsConfig() throws Exception;

}
