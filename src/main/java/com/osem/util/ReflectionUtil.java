package com.osem.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.osem.annotations.Id;

import com.osem.exceptions.OsemException;

public class ReflectionUtil
{
	public static Field[] getAnnotatedFileds(Class<?> clazz, Class<? extends Annotation> annotationClass) {
		
		Field[] allFields = clazz.getDeclaredFields();
		List<Field> annotatedFields = new LinkedList<Field>();

		for (Field field : allFields) {
			if(field.isAnnotationPresent(annotationClass))
				annotatedFields.add(field);
		}

		return annotatedFields.toArray(new Field[annotatedFields.size()]);
	}
	
	public static String getId(Object object) throws Exception {
		Object ret = null;
		Method method = null;
		Class<?> clazz = object.getClass();
		
		Field[] fields = getAnnotatedFileds(clazz, Id.class);
		method = clazz.getMethod("get" + WordUtils.capitalize(fields[0].getName()), new Class[] {});
		ret = method.invoke(object, new Object[] {});
		
		if (ret==null) {
			throw new RuntimeException("Not accept Id equals null");
		}
		
		String id = Converter.convert(ret);
		if (id.length()==0) {
			throw new RuntimeException("Not accept Id equals empty");
		}
		
		return id;
	}
	
	@SuppressWarnings("rawtypes")
	public static void loadClass(String fullyQualifiedClassName) {
		Class clazz = null;
		
		try {
			clazz = Class.forName("fullyQualifiedClassName");
		} catch (ClassNotFoundException e) {
			throw new OsemException("Unable to find " + fullyQualifiedClassName);
		}
		
		try {
			clazz.newInstance();
		} catch (Exception e) {
			throw new OsemException("Unable to load " + fullyQualifiedClassName);
		}
	}
}
