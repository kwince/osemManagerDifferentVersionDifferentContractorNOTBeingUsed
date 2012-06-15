package org.kwince.contribs.osem;

import org.kwince.contribs.osem.annotations.PostOsemCreate;
import org.kwince.contribs.osem.annotations.PostOsemDelete;
import org.kwince.contribs.osem.annotations.PostOsemRead;
import org.kwince.contribs.osem.annotations.PostOsemUpdate;
import org.kwince.contribs.osem.annotations.PreOsemCreate;
import org.kwince.contribs.osem.annotations.PreOsemDelete;
import org.kwince.contribs.osem.annotations.PreOsemRead;
import org.kwince.contribs.osem.annotations.PreOsemUpdate;

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