package org.kwince.osem.es;

import org.elasticsearch.client.Client;
import org.kwince.osem.OsemSessionFactory;
import org.kwince.osem.es.cfg.Configuration;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

/**
 * Interface for elastic search session management.
 * <p/>
 * The internal state of a {@link OsemSessionFactory} is immutable. Once it is
 * created this internal state is set. This internal state includes all of the
 * metadata about Object/Document Mapping.
 * <p/>
 * 
 * @author Allan Ramirez (ramirezag@gmail.com)
 * 
 */
public interface EsOsemSessionFactory extends OsemSessionFactory {
    /**
     * Immutable configuration managed by this factory
     * 
     * @return configuration managed by this factory
     */
    Configuration getConfiguration();

    /**
     * Elastic Search {@link Client} managed by this factory. A client provides
     * a one stop interface for performing actions/operations against the
     * cluster.
     * 
     * @return elastic search client.
     * @see Client
     */
    Client getClient();

    /**
     * Required index name to index. In ES, indices are database equivalent in
     * RDBMS system.
     * 
     * @param indexName
     */
    void setIndexName(String indexName);

    /**
     * Specify packages to search for autodetection of your document classes in
     * the classpath. This is analogous to Spring's component-scan feature (
     * {@link ClassPathBeanDefinitionScanner} ).
     * 
     * @param packagesToScan
     */
    void setPackagesToScan(String[] packagesToScan);

    /**
     * Set location of the property file that contains common properties that
     * could be used in both Node Client and Transport Client. Eg. cluster.name,
     * index.number_of_shards etc.
     * 
     * @param settingsLocation
     *            Defaults to META-INF/es_settings.properties
     */
    void setSettingsLocation(String settingsLocation);
}
