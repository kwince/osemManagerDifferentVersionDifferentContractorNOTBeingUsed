package org.kwince.osem.es;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kwince.osem.OsemSession;
import org.kwince.osem.OsemSessionFactory;
import org.kwince.osem.es.valid.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TransportEsSessionFactoryTest {
    private static final String ES_DEFAULT_DATA_PATH = "data";
    private static Logger log = LoggerFactory.getLogger(EsOsemSessionFactoryTest.class);
    private static ClassPathXmlApplicationContext context;
    private static OsemSessionFactory sessionFactory;
    private static File esDataPath;

    @BeforeClass
    public static void setup() {
        context = new ClassPathXmlApplicationContext("applicationContext2.xml");
        sessionFactory = context.getBean("transportEsFactory", OsemSessionFactory.class);
        try {
            esDataPath = getEsDataPath();
        } catch (Exception e) {
            log.error("Error getting es data path", e);
        }
    }

    @Test
    public void performCrud() throws Exception {
        OsemSession session = sessionFactory.getCurrentSession();
        session.deleteAll(Person.class);

        Person person1 = new Person();
        person1.setName("test name1");
        Person person2 = new Person();
        person2.setName("test name2");

        Serializable id1 = session.save(person1);
        Serializable id2 = session.save(person2);
        // Generated from elastic search
        assertNotNull(id1);
        assertNotNull(id2);
        assertNotSame(id1, id2);

        // Find by json query
        String jsonQuery = "{\"prefix\" : { \"name\" : \"test\" }}";
        List<Person> list = session.findAll(Person.class, jsonQuery);
        assertEquals(2, list.size());

        session.deleteAll(Person.class);

        list = session.findAll(Person.class, jsonQuery);
        assertEquals(0, list.size());
    }

    @AfterClass
    public static void cleanup() throws Exception {
        context.close();
        try {
            FileUtils.deleteDirectory(esDataPath);
        } catch (IOException e) {
            log.error("Failed to delete node client data.path directory", e);
        }
    }

    public static File getEsDataPath() throws Exception {
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream("es_settings.properties");
            Properties props = new Properties();
            props.load(is);
            String path = props.getProperty("path.data");
            return StringUtils.isNotBlank(path) ? new File(path) : new File(ES_DEFAULT_DATA_PATH);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
}
