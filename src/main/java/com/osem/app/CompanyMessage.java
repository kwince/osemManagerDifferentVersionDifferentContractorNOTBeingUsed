package com.osem.app;

import org.osem.annotations.PostOsemCreate;
import org.osem.annotations.PostOsemRead;
import org.osem.annotations.PreOsemCreate;
import org.osem.annotations.PreOsemRead;

import com.osem.model.Company;
import com.osem.util.Converter;
import com.osem.validation.Validator;

public class CompanyMessage {
	
	@PreOsemCreate
	public void preCreate(Object object) {
		Company obj = (Company)object;
		Validator.validate(obj.getClass());
		System.out.println("pre create: " + obj.getName() + " " + obj.getAddress().getCity());
	}
	
	@PostOsemCreate
	public void postCreate(Object object) {
		Company obj = (Company)object;
		System.out.println("post create: company id " + Converter.convert(obj.getId()));
	}
	
	@PreOsemRead
	public void preRead(Object object) {
		System.out.println("**************** @PreOsemRead **************");
	}
	
	@PostOsemRead
	public void postRead(Object object) {
		System.out.println("**************** @PostOsemRead **************");
	}
}