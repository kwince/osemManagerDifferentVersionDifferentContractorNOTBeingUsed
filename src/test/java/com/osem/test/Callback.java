package com.osem.test;

import org.osem.annotations.PostOsemCreate;
import org.osem.annotations.PostOsemDelete;
import org.osem.annotations.PostOsemRead;
import org.osem.annotations.PostOsemUpdate;
import org.osem.annotations.PreOsemCreate;
import org.osem.annotations.PreOsemDelete;
import org.osem.annotations.PreOsemRead;
import org.osem.annotations.PreOsemUpdate;

public class Callback {
	
	public static boolean preCreate = false;
    public static boolean postCreate = false;
    	
    public static boolean preUpdate = false;
    public static boolean postUpdate = false;
    
    public static boolean preRead = false;
	public static boolean postRead = false;
        
	public static boolean preDelete = false;
    public static boolean postDelete = false;
        
    @PreOsemCreate
    public void preCreate(Object object) {
    	preCreate = true;
    }
    	
    @PostOsemCreate
    public void postCreate(Object object) {
    	postCreate = true;
    }
    
    @PreOsemRead
    public void preRead(Object object) {
       	preRead = true;
    }
    	
    @PostOsemRead
    public void postRead(Object object) {
    	postRead = true;
    }
    
    @PreOsemUpdate
    public void preUpdate(Object object) {
       	preUpdate = true;
    }
    	
    @PostOsemUpdate
    public void postUpdate(Object object) {
    	postUpdate = true;
    }
        
    @PreOsemDelete
    public void preDelete(Object object) {
    	preDelete = true;
    }
    	
    @PostOsemDelete
    public void postDelete(Object object) {
    	postDelete = true;
    }
}