package org.kwince.osem.es;

import org.elasticsearch.client.Client;
import org.kwince.osem.OsemSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public abstract class AbstractOsemSessionFactoryBean extends AbstractOsemSessionFactory implements FactoryBean<OsemSessionFactory>,
        InitializingBean, DisposableBean {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected Client client;

    @Override
    public OsemSessionFactory getObject() throws Exception {
        return this;
    }

    @Override
    public Class<?> getObjectType() {
        return OsemSessionFactory.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() throws Exception {
        if (client != null) {
            try {
                client.close();
            } catch (Exception e) {
                logger.error("Error closing client.", e);
            }
        }
    }

    public final Client getClient() {
        return client;
    }

}
