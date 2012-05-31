package com.osem.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.osem.dao.OsemManager;
import com.osem.dao.OsemMangerFactory;

public class CallbackTest {
	
	OsemMangerFactory factory;
	OsemManager osem;
    Employee emp;
    String id;
    String name;
    
    @Before
    public void setUp() {
    	factory = new OsemMangerFactory();
    	osem = factory.createOsemManager();
    	emp = new Employee();
    	id = String.valueOf(new Date().getTime());
    	name = "Mary";
    	// prepare to read, update and delete
    	emp.setId(id);
    	emp.setName(name);
        osem.create(emp);
        resetStatus();
    }
    
    @After
    public void cleanUp() {
    	System.out.println("======================================= clean up");
    	if (id!=null) {
    		emp.setId(id);
            osem.delete(emp);
        }
    	
    	factory.close();
    	System.out.println("======================================= cleaned up");
    }
    
    @Test
    public void PRE_READ_TEST() throws Exception
    {
    	assertFalse(Callback.preRead);
        Employee result = (Employee) osem.read(emp);
        assertTrue(Callback.preRead);
        
        Assert.assertEquals(id, result.getId());
        Assert.assertEquals(name, result.getName());
        
        System.out.println(">>>>>>>>> Success - test 'PRE_READ_TEST' <<<<<<<<<");
    }
    
    @Test
    public void POST_READ_TEST() throws Exception
    {
    	assertFalse(Callback.postRead);
    	emp.setId(id + "123");
    	Employee result = (Employee) osem.read(emp);
        assertTrue(Callback.postRead);
        
        Assert.assertNull(result);
        
        System.out.println(">>>>>>>>> Success - test 'POST_READ_TEST' <<<<<<<<<");
    }
    
    @Test
    public void PRE_UPDATE_TEST() throws Exception
    {
    	assertFalse(Callback.preUpdate);
        emp.setName("Thatcher");
        Employee result = (Employee) osem.update(emp);
        assertTrue(Callback.preUpdate);
        
        Assert.assertNotNull(result);
        Assert.assertEquals(id, result.getId());
        Assert.assertEquals("Thatcher", result.getName());
        
        System.out.println(">>>>>>>>> Success - test 'PRE_UPDATE_TEST' <<<<<<<<<");
    }
    
    @Test
    public void POST_UPDATE_TEST() throws Exception
    {
    	assertFalse(Callback.postUpdate);
    	emp.setId(id + "123");
        emp.setName("Thatcher II");
        Employee result = (Employee) osem.update(emp);
        assertTrue(Callback.postUpdate);
        
        Assert.assertNull(result);
        
        System.out.println(">>>>>>>>> Success - test 'POST_UPDATE_TEST' <<<<<<<<<");
    }
    
    @Test
    public void PRE_DELETE_TEST() throws Exception
    {
    	assertFalse(Callback.preDelete);
        boolean result = osem.delete(emp);
        assertTrue(Callback.preDelete);
        
        assertTrue(result);
        
        System.out.println(">>>>>>>>> Success - test 'PRE_DELETE_TEST' <<<<<<<<<");
    }
    
    @Test
    public void POST_DELETE_TEST() throws Exception
    {
    	assertFalse(Callback.postDelete);
    	emp.setId(id + "123");
    	boolean result = osem.delete(emp);
        assertTrue(Callback.postDelete);
        assertFalse(result);
        
        System.out.println(">>>>>>>>> Success - test 'POST_DELETE_TEST' <<<<<<<<<");
    }
    
    void resetStatus() {
    	Callback.preRead = false;
    	Callback.postRead = false;
        
    	Callback.preUpdate = false;
    	Callback.postUpdate = false;
            	
    	Callback.preDelete = false;
    	Callback.postDelete = false;
    }
}
