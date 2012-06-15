package org.kwince.contribs.osem.app;

import org.kwince.contribs.osem.annotations.PostOsemCreate;
import org.kwince.contribs.osem.annotations.PostOsemRead;
import org.kwince.contribs.osem.annotations.PreOsemCreate;
import org.kwince.contribs.osem.annotations.PreOsemRead;

import org.kwince.contribs.osem.model.Company;
import org.kwince.contribs.osem.util.Converter;
import org.kwince.contribs.osem.validation.Validator;

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