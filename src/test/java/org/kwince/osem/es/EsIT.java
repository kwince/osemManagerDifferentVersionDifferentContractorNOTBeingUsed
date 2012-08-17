package org.kwince.osem.es;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kwince.osem.OsemSession;
import org.kwince.osem.OsemSessionFactory;
import org.kwince.osem.es.model.User;
import org.kwince.osem.es.model.common.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class EsIT {
    private static final String ES_DEFAULT_DATA_PATH = "data";
    private static Logger log = LoggerFactory.getLogger(EsIT.class);
    private static ClassPathXmlApplicationContext context;
    private static OsemSessionFactory sessionFactory;
    private static File esDataPath;

    @BeforeClass
    public static void setup() {
        context = new ClassPathXmlApplicationContext("applicationContext.xml");
        sessionFactory = context.getBean("nodeEsSessionFactory", OsemSessionFactory.class);
        try {
            esDataPath = getEsDataPath();
        } catch (Exception e) {
            log.error("Error getting es data path", e);
        }
    }

    @Test
    public void performCrud() throws Exception {
        String id = "aramirez";
        User user = new User();
        user.setUsername(id);
        user.setPassword("password");
        user.setName(new Name("Allan", "Gallano", "Ramirez"));
        user.setAlias(new Name("A", null, "R"));
        OsemSession session = sessionFactory.getCurrentSession();
        session.save(user);
        User savedUser = session.find(User.class, id);

        assertEquals(user.getUsername(), savedUser.getUsername());
        assertEquals(user.getName(), savedUser.getName());
        assertEquals(user.getAlias(), savedUser.getAlias());

        session.delete(User.class, id);

        savedUser = session.find(User.class, id);
        assertNull(savedUser);
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
