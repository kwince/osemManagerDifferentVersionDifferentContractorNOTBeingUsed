package org.kwince.osem.es;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.elasticsearch.client.Client;
import org.junit.Test;
import org.kwince.osem.es.cfg.Configuration;
import org.kwince.osem.es.model.User;

public class TestAbstractOsemSessionFactory {
    private String indexName = "test_index";
    private AbstractOsemSessionFactory sessionFactory;

    @Test
    public void successbuildWithDocumentPathTest() throws Exception {
        sessionFactory = new AbstractOsemSessionFactory() {
            @Override
            public Client getClient() {
                return mock(Client.class);
            }

            @Override
            protected void initEsConfig() throws Exception {
            }
        };
        // Set properties
        sessionFactory.setIndexName(indexName);
        sessionFactory.setPackagesToScan(new String[] { "org.kwince.osem.**.model" });
        // After properties set
        sessionFactory.afterPropertiesSet();

        // Case 1: Test sessions
        OsemSessionImpl session = (OsemSessionImpl) sessionFactory.getCurrentSession();
        assertEquals(indexName, session.getIndexName());
        assertEquals(session, sessionFactory.getCurrentSession());

        sessionFactory.removeCurrentSession();
        assertNull(sessionFactory.threadLocal.get());

        // Case 2: Test configuration
        Configuration config = sessionFactory.getConfiguration();
        assertTrue(config.isDocument(User.class));
    }

    @Test
    public void successbuildWithNoDocumentPathTest() throws Exception {
        sessionFactory = new AbstractOsemSessionFactory() {
            @Override
            public Client getClient() {
                return mock(Client.class);
            }

            @Override
            protected void initEsConfig() throws Exception {
            }
        };
        // Set properties
        sessionFactory.setIndexName(indexName);
        // After properties set
        sessionFactory.afterPropertiesSet();

        OsemSessionImpl session = (OsemSessionImpl) sessionFactory.getCurrentSession();
        assertEquals(indexName, session.getIndexName());
        assertEquals(session, sessionFactory.getCurrentSession());

        sessionFactory.removeCurrentSession();
        assertNull(sessionFactory.threadLocal.get());

        // Case 2: Test configuration
        Configuration config = sessionFactory.getConfiguration();
        assertFalse(config.isDocument(User.class));
    }
}
