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

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public OsemSessionFactory getObject() throws Exception {
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class<?> getObjectType() {
        return OsemSessionFactory.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    public boolean isSingleton() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    public void destroy() throws Exception {
        if (client != null) {
            try {
                client.close();
            } catch (Exception e) {
                logger.error("Error closing client.", e);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kwince.osem.es.EsOsemSessionFactory#getClient()
     */
    public final Client getClient() {
        return client;
    }

}
