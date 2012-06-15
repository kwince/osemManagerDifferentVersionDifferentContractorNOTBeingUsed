package org.kwince.contribs.osem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kwince.contribs.osem.annotations.Id;

import org.kwince.contribs.osem.dao.OsemManager;
import org.kwince.contribs.osem.dao.OsemMangerFactory;
import org.kwince.contribs.osem.exceptions.OsemException;

public class IndexTest {
	
	OsemMangerFactory factory;
	OsemManager osem;
	String id;
	Employee emp;
	
	@Before
    public void initialize() {
		factory = new OsemMangerFactory();
		osem = factory.createOsemManager();
		
		id = String.valueOf(new Date().getTime());
		emp = new Employee("John");
		
		emp.setId(id);
        osem.create(emp);
    }
	
	@After
    public void cleanUp() {
		
		if (id!=null) {
    		emp.setId(id);
            osem.delete(emp);
        }
    	
    	factory.close();
    }
	
	@Test
	public void INDEX_EXIST() {
		final String expectedError = null;
		String actualError = null;
    	
		String query = "{\"match_all\": {}}";
	    List<Object> list = null;
	    
		try {
			list = osem.find(query, Employee.class);
		} catch (OsemException actualException) {
			actualError = actualException.getMessage();
		}
		
		assertEquals(expectedError, actualError);
		assertNotNull(list);
		System.out.println(">>>>>>>>> Success - test 'INDEX_EXIST_2' <<<<<<<<<");
    }
	
	@Test
	public void INDEX_EXIST_2() {
		final String expectedError = null;
		String actualError = null;
		
		Employee emp2 = new Employee();
		try {
			emp2 = (Employee) osem.read(emp);
		} catch (OsemException actualException) {
			actualError = actualException.getMessage();
		}
		
		assertEquals(expectedError, actualError);
		assertNotNull(emp2);
		System.out.println(">>>>>>>>> Success - test 'INDEX_EXIST_2' <<<<<<<<<");
    }
		
	@Test
	public void INDEX_NOT_EXIST() {
		final String expectedError = "Invalid type. This type does not exist";
		String actualError = null;
    	
		String query = "{\"match_all\": {}}";
	    List<Object> list = null;
	    
		try {
			list = osem.find(query, Car.class);
		} catch (OsemException actualException) {
			actualError = actualException.getMessage();
		}
		
		assertEquals(expectedError, actualError);
		assertNull(list);
		System.out.println(">>>>>>>>> Success - test 'INDEX_NOT_EXIST' <<<<<<<<<");
    }
	
	public class Car {
		@Id
		private String id;
		private String name;
		
		public Car() {
		}
		
		public Car(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}

}