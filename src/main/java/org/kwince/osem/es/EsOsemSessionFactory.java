package org.kwince.osem.es;

import org.elasticsearch.client.Client;
import org.kwince.osem.OsemSessionFactory;
import org.kwince.osem.es.cfg.Configuration;

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
     * Elastic Search {@link Client} managed by this factory.
     * 
     * @return
     */
    Client getClient();
}
