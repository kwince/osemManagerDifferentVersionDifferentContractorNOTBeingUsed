package org.kwince.osem.es.cfg;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.common.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kwince.osem.es.model.User;
import org.kwince.osem.es.model.common.Name;

public class ConfigurationTest {
    private Configuration config;

    @Before
    public void onSetup() throws Exception {
        config = new Configuration();
        config.addAnnotatedClass(User.class);
        config.build();
    }

    @Test
    public void isDocumentTest() {
        User user = new User();
        assertTrue(config.isDocument(user));
        assertTrue(config.isDocument(User.class));
    }

    @Test
    public void getDocumentNameTest() {
        assertEquals("user_doc", config.getDocumentName(User.class));
    }

    @Test
    public void getIdFieldTest() throws Exception {
        Field field = config.getIdField(User.class);
        assertNotNull(field);
    }

    @Test
    public void createObjectTest() {
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
    public void createSourceMapTest() {
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

}
