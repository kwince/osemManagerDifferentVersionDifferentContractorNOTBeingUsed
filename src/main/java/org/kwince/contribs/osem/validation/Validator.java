package org.kwince.contribs.osem.validation;

import java.util.ArrayList;
import java.util.List;

public class Validator {
	
	public static void validate(Class<?> clazz) {

		List<Constraint> rules = getConstraints();
		for (Constraint c : rules) {
			c.check(clazz);
		}
	}
	
	private static List<Constraint> getConstraints() {
		List<Constraint> constraints = new ArrayList<Constraint>();
		
		constraints.add(new MultipleId());
		constraints.add(new NoId());
		
		return constraints;
	}
	
}
