package org.kwince.contribs.osem.util;

import org.bson.types.ObjectId;

public class Converter {
	
	public static String convert(Object object) {
		String result = null;
		Class<?> clazz = object.getClass();
		if (String.class.isAssignableFrom(clazz) 
			|| Integer.class.isAssignableFrom(clazz) || int.class.isAssignableFrom(clazz)
			|| Short.class.isAssignableFrom(clazz) || short.class.isAssignableFrom(clazz)
			|| Long.class.isAssignableFrom(clazz) || long.class.isAssignableFrom(clazz)) {
		
			result = String.valueOf(object);
		}
		else if (ObjectId.class.isAssignableFrom(clazz)) {
			ObjectId objId = (ObjectId)object;
			result = objId.toStringMongod();
		}
		else {
			try {
				throw new Exception("Value of Id is unacceptable");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result;
	}

}

