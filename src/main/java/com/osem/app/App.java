package com.osem.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.osem.dao.OsemManager;
import com.osem.dao.OsemMangerFactory;
import com.osem.model.Address;
import com.osem.model.Company;
import com.osem.model.Document;
import com.osem.model.Person;

public class App 
{
    public static void main( String[] args )
    {
        try {
// Testing permissions
//        	OsemMangerFactory factory = new OsemMangerFactory("my_osem", "elasticsearch");
        	OsemMangerFactory factory = new OsemMangerFactory();
        	OsemManager osem = factory.createOsemManager();
        	
        	Person p1 = new Person();
    	    osem.create(p1);
    	    osem.read(p1);
    	    
//    	    String query = "{\"term\":{\"id\":\"4f759feae4b06b71b9f96a89\"}}";
    	    String query = "{\"match_all\": {}}";
    	    
    	    System.out.println(query);
    	    List<Object> list = osem.find(query, Person.class);
    	    for(Object o : list) {
    	    	Person p = (Person)o;
    	    	System.out.println(p.getFirstName() + " " + p.getAddress().getCity());
    	    }
    	    
    		final Company c1 = new Company();
            c1.setName("The Company");
            c1.setAddress(new Address("D Rd.", "Paris", "TX", "77382"));
            c1.setId(ObjectId.get());
            osem.create(c1);
            
            List<Person> people = new ArrayList<Person>();
            people.add(p1);
            people.add(p1);
            c1.setEmployees(people);
            osem.create(c1);
            
            p1.setFirstName(p1.getFirstName() + " update" + new Date().getTime());
            osem.update(p1);
            
            p1.setId("4f75a640e4b0926fcd15fb43");
//            osem.delete(p1);
            factory.close();
    		System.in.read();
		
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    static void test1() throws Exception {
    	
    	String json = "{\"name\": \"my_osem\", \"vendor\": \"elasticsearch\",\"clientType\": \"node\"," +
    		      "\"nodeClient\":{\"node.client\":false,\"cluster.name\":\"elasticsearch\",\"node.local\":false}," +
    		      "\"transportClient\":{\"host\": \"localhost\", \"port\": 9300,\"cluster.name\": \"elasticsearch\"," + 
    		      "\"client.transport.sniff\": true}}";  
    	
    	OsemMangerFactory factory = new OsemMangerFactory(json);
    	OsemManager osem = factory.createOsemManager();
    	Document doc = new Document();
        doc.setName("myName");
        doc.setVendor("elasticsearch");
        
        doc = (Document) osem.create(doc);
    }
  
    
}
