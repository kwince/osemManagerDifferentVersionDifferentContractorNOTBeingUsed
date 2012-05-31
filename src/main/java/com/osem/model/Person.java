package com.osem.model;

import org.osem.annotations.EventListener;
import org.osem.annotations.Id;

import com.osem.app.PersonMessage;

@EventListener(PersonMessage.class)
public class Person {
    
	@Id
	private String id;
	
    private String firstName;
    private String middleInitial;
    private String lastName;
    
    private Address address;
 
    public Person() {
    }
 
    public Person(final String fn, final String mi, final String ln, final Address address) {
        setFirstName(fn);
        setMiddleInitial(mi);
        setLastName(ln);
        setAddress(address);
    }
 
    public String getFirstName() {
        return firstName;
    }
 
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }
 
    public String getLastName() {
        return lastName;
    }
 
    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }
 
    public String getMiddleInitial() {
    	return middleInitial;
    }
 
    public void setMiddleInitial(final String middleInitial) {
        this.middleInitial = middleInitial;
    }
 
    public final Address getAddress() {
        return address;
    }
 
    public final void setAddress(final Address address) {
        this.address = address;
    }

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}