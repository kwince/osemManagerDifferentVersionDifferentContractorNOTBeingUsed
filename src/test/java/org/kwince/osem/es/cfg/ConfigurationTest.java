package org.kwince.osem.es.cfg;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.common.joda.time.DateTime;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.junit.Test;
import org.kwince.osem.es.annotation.Document;
import org.kwince.osem.es.annotation.Id;
import org.kwince.osem.es.annotation.Property;
import org.kwince.osem.es.model.Person;
import org.kwince.osem.es.model.User;
import org.kwince.osem.es.model.common.Name;
import org.kwince.osem.exception.MappingException;
import org.kwince.osem.exception.MetadataException;
import org.kwince.osem.exception.ModificationException;

public class ConfigurationTest {
    @Test
    public void testImmutability() throws Exception {
        Configuration config = initializeConfig(null);
        try {
            config.addAnnotatedClass(Person.class);
            fail("Should throw exception because config is already initialized when build method is called.");
        } catch (ModificationException e) {
            assertEquals(String.format("%s is already initialized", Configuration.class.getSimpleName()), e.getMessage());
        }

        try {
            config.build();
            fail("Should throw exception because config is already initialized when build method is called.");
        } catch (MetadataException e) {
            fail("This type of exception should not be thrown");
        } catch (ModificationException e) {
            assertEquals(String.format("%s is already initialized", Configuration.class.getSimpleName()), e.getMessage());
        }
    }

    @Test
    public void testSettingsProperties() throws Exception {
        String settingsLocation = "configurationtest_settings.properties";
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("clustername", "testclustername");
        properties.put("pathdata", "testpathdata");

        URL url = Thread.currentThread().getContextClassLoader().getResource(".");
        File rootDir;
        try {
            rootDir = new File(url.toURI());
        } catch (URISyntaxException e) {
            rootDir = new File(url.getPath());
        }
        File file = new File(rootDir, settingsLocation);
        file.createNewFile();
        file.deleteOnExit();
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            for (String key : properties.keySet()) {
                bw.write(key + "=" + properties.get(key));
                bw.write("\n");
            }
        } finally {
            if (bw != null) {
                bw.close();
            }
        }

        // Case 1: Test properties in an existing property file
        Configuration config = initializeConfig(settingsLocation);
        Builder builder = config.getSettingsBuilder();
        assertEquals(properties, builder.internalMap());

        // Case 2: Test properties in a non-existing property file
        file.delete();
        config = new Configuration();
        config.setSettingsLocation(settingsLocation);
        config.build();
        builder = config.getSettingsBuilder();
        assertTrue(builder.internalMap().isEmpty());
    }

    @Test
    public void testDocumentExistence() throws Exception {
        Configuration config = initializeConfig(null);
        User user = new User();
        assertTrue(config.isDocument(user));
        assertTrue(config.isDocument(User.class));
        assertFalse(config.isDocument(Person.class));
        assertEquals("user_doc", config.getDocumentName(User.class));
        assertNull(config.getDocumentName(Person.class));
    }

    @Test
    public void getIdFieldTest() throws Exception {
        Configuration config = initializeConfig(null);
        Field field = config.getIdField(User.class);
        assertNotNull(field);
    }

    @Test
    public void createObjectTest() throws Exception {
        Configuration config = initializeConfig(null);
        // Case 1: Empty source map
        Map<String, Object> source = new HashMap<String, Object>();
        User user = config.createObject(User.class, source);
        assertNull(user.getUsername());
        assertNull(user.getPassword());
        assertNull(user.getName());
        assertNull(user.getAlias());

        // Case 2: No alias map, null password and middle_name
        source.put("user_name", "aramirez");
        source.put("password", null);
        DateTime now = DateTime.now();
        Date birthdate = now.toDate();
        source.put("birthdate", now.toString());
        Map<String, Object> nameMap = new HashMap<String, Object>();
        nameMap.put("first_name", "Allan");
        nameMap.put("last_name", "Ramirez");
        source.put("name", nameMap);

        user = config.createObject(User.class, source);
        assertEquals("aramirez", user.getUsername());
        assertNull(user.getPassword());
        assertEquals(birthdate, user.getBirthdate());
        assertEquals("Allan", user.getName().getFirstName());
        assertNull(user.getName().getMiddleName());
        assertEquals("Ramirez", user.getName().getLastName());
        assertNull(user.getAlias());

        // Case 3: With alias map with null values, birthdate is null
        Map<String, Object> aliasMap = new HashMap<String, Object>();
        source.put("birthdate", null);
        aliasMap.put("first_name", null);
        aliasMap.put("middle_name", null);
        aliasMap.put("last_name", null);
        source.put("name_alias", aliasMap);

        user = config.createObject(User.class, source);
        assertEquals("aramirez", user.getUsername());
        assertNull(user.getPassword());
        assertNull(user.getBirthdate());
        assertEquals("Allan", user.getName().getFirstName());
        assertNull(user.getName().getMiddleName());
        assertEquals("Ramirez", user.getName().getLastName());
        assertNull(user.getAlias().getFirstName());
        assertNull(user.getAlias().getMiddleName());
        assertNull(user.getAlias().getLastName());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createSourceMapTest() throws Exception {
        Configuration config = initializeConfig(null);
        User user = new User();
        user.setUsername("aramirez");
        user.setName(new Name("Allan", null, "Ramirez"));

        Map<String, Object> source = config.createSourceMap(user);
        assertEquals("aramirez", source.get("user_name"));
        Map<String, Object> nameMap = (Map<String, Object>) source.get("name");
        assertEquals("Allan", nameMap.get("first_name"));
        assertTrue(nameMap.containsKey("middle_name"));
        assertNull(nameMap.get("middle_name"));
        assertEquals("Ramirez", nameMap.get("last_name"));
        Map<String, Object> aliasMap = (Map<String, Object>) source.get("name_alias");
        assertNotNull(aliasMap);
        assertNull(aliasMap.get("first_name"));
        assertTrue(aliasMap.containsKey("first_name"));
        assertNull(aliasMap.get("middle_name"));
        assertTrue(aliasMap.containsKey("middle_name"));
        assertNull(aliasMap.get("last_name"));
        assertTrue(aliasMap.containsKey("last_name"));
    }

    @Test
    public void invalidDocumentTest() {
        try {
            Configuration config = new Configuration();
            config.addAnnotatedClass(NoIdDocument.class);
            config.build();
            fail("Should throw MappingException because NoIdDocument has no field annotated with @Id");
        } catch (ModificationException e) {
            fail("This type of exception should not be thrown");
        } catch (MetadataException e) {
            fail("This type of exception should not be thrown");
        } catch (MappingException e) {
            assertEquals(String.format("%s has no annotated ID", NoIdDocument.class), e.getMessage());
        }

        try {
            Configuration config = new Configuration();
            config.addAnnotatedClass(DuplicateIdDocument.class);
            config.build();
            fail("Should throw MappingException because DuplicateIdDocument has 2 field annotated with @Id");
        } catch (ModificationException e) {
            fail("This type of exception should not be thrown");
        } catch (MetadataException e) {
            fail("This type of exception should not be thrown");
        } catch (MappingException e) {
            assertEquals("Only 1 field with @Id is allowed.", e.getMessage());
        }

        try {
            Configuration config = new Configuration();
            config.addAnnotatedClass(FieldWithNoGetterSetter1.class);
            config.build();
            fail("Should throw MetadataException");
        } catch (ModificationException e) {
            fail("This type of exception should not be thrown");
        } catch (MetadataException e) {
            assertEquals("Property id has no setter or getter", e.getMessage());
        }

        try {
            Configuration config = new Configuration();
            config.addAnnotatedClass(FieldWithNoGetter1.class);
            config.build();
            fail("Should throw MetadataException");
        } catch (ModificationException e) {
            fail("This type of exception should not be thrown");
        } catch (MetadataException e) {
            assertEquals("Property id has no setter or getter", e.getMessage());
        }

        try {
            Configuration config = new Configuration();
            config.addAnnotatedClass(FieldWithNoGetterSetter2.class);
            config.build();
            fail("Should throw MetadataException");
        } catch (ModificationException e) {
            fail("This type of exception should not be thrown");
        } catch (MetadataException e) {
            assertEquals("Property field has no setter or getter", e.getMessage());
        }

        try {
            Configuration config = new Configuration();
            config.addAnnotatedClass(FieldWithNoGetterSetter3.class);
            config.build();
            fail("Should throw MetadataException");
        } catch (ModificationException e) {
            fail("This type of exception should not be thrown");
        } catch (MetadataException e) {
            assertEquals("Property field has no setter or getter", e.getMessage());
        }
    }

    private Configuration initializeConfig(String settingsLocation) throws Exception {
        Configuration config = new Configuration();
        config.addAnnotatedClass(User.class);
        config.setSettingsLocation(settingsLocation);
        config.build();
        return config;
    }

    @Document
    private static class NoIdDocument {

    }

    @SuppressWarnings("unused")
    @Document
    private static class DuplicateIdDocument {
        @Id
        String id1;
        @Id
        String id2;

        public String getId1() {
            return id1;
        }

        public void setId1(String id1) {
            this.id1 = id1;
        }

        public String getId2() {
            return id2;
        }

        public void setId2(String id2) {
            this.id2 = id2;
        }

    }

    @SuppressWarnings("unused")
    @Document
    private static class FieldWithNoGetterSetter1 {
        @Id
        private String id;
    }

    @SuppressWarnings("unused")
    @Document
    private static class FieldWithNoGetter1 {
        @Id
        private String id;

        public void setId(String id) {
            this.id = id;
        }
    }

    @SuppressWarnings("unused")
    @Document
    private static class FieldWithNoGetterSetter2 {
        @Id
        private String id;
        private String field;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

    }

    @SuppressWarnings("unused")
    @Document
    private static class FieldWithNoGetterSetter3 {
        @Id
        private String id;
        @Property
        private String field;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
