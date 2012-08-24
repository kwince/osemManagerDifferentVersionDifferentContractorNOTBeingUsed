package org.kwince.osem.es.it;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.io.Serializable;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kwince.osem.OsemSession;
import org.kwince.osem.OsemSessionFactory;
import org.kwince.osem.es.TransportClientOsemManagerFactoryBean;
import org.kwince.osem.es.valid.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Integration test for {@link TransportClientOsemManagerFactoryBean}. Bean
 * "nodeEsSessionFactory" in applicationContext2.xml serves as external Elastic
 * Search Client. The bean that we are using in this test ("transportEsFactory")
 * connects to the Node hosted by "nodeEsSessionFactory".
 * <p>
 * Note that this class merely demonstrates how to configure a
 * <tt>TransportClientOsemManagerFactoryBean</tt>. For integration test that
 * involves comprehensive CRUD operations test cases, please see
 * {@link EsOsemSessionFactoryIT}.
 * 
 * @author Allan Ramirez (ramirezag@gmail.com)
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/applicationContext2.xml", loader = EsContextLoader.class)
public class TransportEsSessionFactoryIT {
    @Autowired
    @Qualifier("transportEsFactory")
    private OsemSessionFactory sessionFactory;

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

}
