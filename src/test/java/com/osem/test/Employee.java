package com.osem.test;

import org.osem.annotations.EventListener;
import org.osem.annotations.Id;

@EventListener(Callback.class)
public class Employee {
    @Id
	private String id;
	private String name;
		
	public Employee(String name) { this.name = name; }

	public Employee() { }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
		
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
}