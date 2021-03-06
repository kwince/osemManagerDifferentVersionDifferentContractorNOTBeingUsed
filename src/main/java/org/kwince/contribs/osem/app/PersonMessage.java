package org.kwince.contribs.osem.app;

import org.bson.types.ObjectId;
import org.kwince.contribs.osem.annotations.PostOsemCreate;
import org.kwince.contribs.osem.annotations.PostOsemRead;
import org.kwince.contribs.osem.annotations.PostOsemUpdate;
import org.kwince.contribs.osem.annotations.PreOsemCreate;
import org.kwince.contribs.osem.annotations.PreOsemRead;
import org.kwince.contribs.osem.annotations.PreOsemUpdate;

import org.kwince.contribs.osem.model.Address;
import org.kwince.contribs.osem.model.Person;
import org.kwince.contribs.osem.util.Converter;

public class PersonMessage {
	
	String id;
	
	@PreOsemCreate
	public void preCreate(Object object) {
		System.out.println("**************** @PreOsemCreate ********************************");
		Person obj = (Person)object;
		
		Address a = new Address("A Rd.", "Dallas", "TX", "75001");
	    obj.setAddress(a);
	    
	    obj.setFirstName("Brett");
	    obj.setLastName("Schuchert");
	    
	    id = ObjectId.get().toStringMongod();
	    obj.setId(id);
	    
	    System.out.println(id);
	    System.out.println("pre create: " + obj.getFirstName() + " " + obj.getLastName());
	}
	
	@PostOsemCreate
	public void postCreate(Object object) {
		System.out.println("**************** @PostOsemCreate ********************************");
		Person obj = (Person)object;
		System.out.println("post create: " + Converter.convert(obj.getId()));
		if (id.equals(obj.getId())) {
			System.out.println("This's OK");
		}
		else {
			System.out.println("This's not Okay");
		}
	}
	
	@PreOsemRead
	public void preRead(Object object) {
		System.out.println("**************** @PreOsemRead **********************************");
		System.out.println("I want to get a person who has id == " + id);
	}
	
	@PostOsemRead
	public void postRead(Object object) {
		System.out.println("**************** @PostOsemRead **********************************");
		String _id = ((Person)object).getId();
		System.out.println("The Person that I get has id == " + _id);
		System.out.println(id);
		if (id.equals(id)) {
			System.out.println("This's OK");
		}
		else {
			System.out.println("This's not Okay");
		}
	}
	
	@PreOsemUpdate
	public void preUpdate(Object object) {
		System.out.println("**************** @PreOsemUpdate **********************************");
		System.out.println("I want to get a person who has id == " + id);
	}
	
	@PostOsemUpdate
	public void postUpdate(Object object) {
		System.out.println("**************** @PostOsemRead **********************************");
		String _id = ((Person)object).getId();
		System.out.println("The Person that I get has id == " + _id);
		System.out.println(id);
		if (id.equals(id)) {
			System.out.println("This's OK");
		}
		else {
			System.out.println("This's not Okay");
		}
	}
}