package org.kwince.osem.es;

import org.elasticsearch.client.Client;
import org.kwince.osem.OsemSessionFactory;
import org.kwince.osem.es.cfg.Configuration;

public interface EsOsemSessionFactory extends OsemSessionFactory {
    Configuration getConfiguration();

    Client getClient();
}
