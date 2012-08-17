package org.kwince.osem.es;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.kwince.osem.OsemSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public abstract class AbstractOsemSessionFactoryBean extends AbstractOsemSessionFactory implements FactoryBean<OsemSessionFactory>,
        InitializingBean, DisposableBean {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected String settingsLocation = "es_settings.properties";
    protected Client client;

    public void setSettingsLocation(String settingsLocation) {
        this.settingsLocation = settingsLocation;
    }

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

    protected Builder buildConfiguredPropertySettings() {
        InputStream is = null;
        try {
            Builder result = ImmutableSettings.settingsBuilder();
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(settingsLocation);
            if (is != null) {
                result = result.loadFromStream(settingsLocation, is);
            }
            return result;
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
}
