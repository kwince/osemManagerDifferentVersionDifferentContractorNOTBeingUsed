package org.kwince.osem.es;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kwince.osem.OsemSession;
import org.kwince.osem.OsemSessionFactory;
import org.kwince.osem.es.valid.model.NoDocumentAnnotation;
import org.kwince.osem.es.valid.model.Person;
import org.kwince.osem.es.valid.model.User;
import org.kwince.osem.es.valid.model.common.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Tests all CRUD operations performed on an Elastic Search Client (Node Client)
 * created by NodeClientOsemSessionFactoryBean.
 * <p>
 * This class showcases how to use the api of this project. Feel free to add
 * more test cases here in case there are scenarios missed.
 * </p>
 * 
 * @author Allan Ramirez (ramirezag@gmail.com)
 * 
 */
public class EsOsemSessionFactoryTest {
    private static final String ES_DEFAULT_DATA_PATH = "data";
    private static Logger log = LoggerFactory.getLogger(EsOsemSessionFactoryTest.class);
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
    public void performCrud1Test() throws Exception {
        OsemSession session = sessionFactory.getCurrentSession();

        String id1 = "aramirez";

        User user = saveUser(id1);

        assertEquals(1, session.count(User.class));

        User savedUser = session.find(User.class, id1);

        assertEquals(user.getUsername(), savedUser.getUsername());
        assertEquals(user.getName(), savedUser.getName());
        assertEquals(user.getAlias(), savedUser.getAlias());

        session.delete(User.class, id1);

        assertEquals(0, session.count(User.class));

        savedUser = session.find(User.class, id1);
        assertNull(savedUser);
    }

    @Test
    public void performCrud2Test() throws Exception {
        OsemSession session = sessionFactory.getCurrentSession();

        String id1 = "aramirez1";
        String id2 = "aramirez2";
        String id3 = "aramirez3";
        String id4 = "aramirez4";

        // Case 1: Verify saved users
        User user1 = saveUser(id1);
        User user2 = saveUser(id2);
        User user3 = saveUser(id3);

        assertEquals(3, session.count(User.class));
        assertEquals(user1, session.find(User.class, id1));
        assertEquals(user2, session.find(User.class, id2));
        assertEquals(user3, session.find(User.class, id3));

        // Case 2: Verify updated user
        User newUser1 = session.find(User.class, id1);
        newUser1.setName(new Name("Cloud", "Xavier", "Ramirez"));
        newUser1.setPassword("changedpassword");
        session.saveOrUpdate(newUser1);

        User actualUser = session.find(User.class, id1);
        assertEquals(newUser1, actualUser);
        assertNotSame(user1, actualUser);

        // Case 3: Test findall method
        User user4 = saveUser(id4);
        assertEquals(4, session.count(User.class));
        Map<String, User> userMap = new HashMap<String, User>();
        userMap.put(id1, newUser1);
        userMap.put(id2, user2);
        userMap.put(id3, user3);
        userMap.put(id4, user4);

        List<User> users = session.findAll(User.class);
        assertEquals(4, users.size());
        for (User user : users) {
            assertEquals(userMap.get(user.getUsername()), user);
        }

        // Remove user4
        userMap.remove(id4);
        session.delete(User.class, id4);
        users = session.findAll(User.class);
        assertEquals(3, users.size());
        for (User user : users) {
            assertEquals(userMap.get(user.getUsername()), user);
        }

        // Case 4: Test deleteall method
        session.deleteAll(User.class);

        assertEquals(0, session.count(User.class));
    }

    @Test
    public void performSaveWithoutIdTest() throws Exception {
        OsemSession session = sessionFactory.getCurrentSession();
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

    @Test
    public void performCrudOnNoDocumentAnnotationClass() {
        OsemSession session = sessionFactory.getCurrentSession();
        NoDocumentAnnotation object = new NoDocumentAnnotation();
        object.setProperty1("value1");
        object.setProperty2("value2");

        // Case 1: Test save
        try {
            session.save(object);
            fail("Should throw exception because " + NoDocumentAnnotation.class.getName() + " is not annotated with @Document.");
        } catch (Exception e) {
            // Expected
            assertEquals(NoDocumentAnnotation.class + " is not a document.", e.getMessage());
        }

        // Case 2: Test find all
        try {
            session.findAll(NoDocumentAnnotation.class);
            fail("Should throw exception because " + NoDocumentAnnotation.class.getName() + " is not annotated with @Document.");
        } catch (Exception e) {
            // Expected
            assertEquals(NoDocumentAnnotation.class + " is not a document.", e.getMessage());
        }

        // Case 3: Test delete all
        try {
            session.deleteAll(NoDocumentAnnotation.class);
            fail("Should throw exception because " + NoDocumentAnnotation.class.getName() + " is not annotated with @Document.");
        } catch (Exception e) {
            // Expected
            assertEquals(NoDocumentAnnotation.class + " is not a document.", e.getMessage());
        }
    }

    private User saveUser(String username) throws Exception {
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        Date birthdate = new Date();
        user.setBirthdate(birthdate);
        user.setName(new Name("Allan", "Gallano", "Ramirez"));
        user.setAlias(new Name("A", null, "R"));

        OsemSession session = sessionFactory.getCurrentSession();
        session.save(user);

        return user;
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
