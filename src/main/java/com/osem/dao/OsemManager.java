package com.osem.dao;

import java.util.List;

import org.elasticsearch.client.Client;
import org.osem.annotations.PostOsemCreate;
import org.osem.annotations.PostOsemDelete;
import org.osem.annotations.PostOsemRead;
import org.osem.annotations.PostOsemUpdate;
import org.osem.annotations.PreOsemCreate;
import org.osem.annotations.PreOsemDelete;
import org.osem.annotations.PreOsemRead;
import org.osem.annotations.PreOsemUpdate;
import org.osem.event.EventDispatcher;

import com.osem.exceptions.OsemException;
import com.osem.util.ReflectionUtil;
import com.osem.validation.Validator;

public class OsemManager extends PersistenceService<Object> implements BasicDao<Object, Object> {
	
	EventDispatcher disp = EventDispatcher.getEventDispatcher();
	
	OsemManager (Client client) {
		this.client = client;
	}

	@Override
	public Object create(Object entity) {
		Object result = null;
		register(entity.getClass());
		disp.publish(PreOsemCreate.class, entity.getClass(), entity);
		Validator.validate(clazz);
		try {
			String id = ReflectionUtil.getId(entity);
			result = mapping(id, entity);
			disp.publish(PostOsemCreate.class, entity.getClass(), result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public Object read(Object entity) {
		Object result = null;
		register(entity.getClass());
		disp.publish(PreOsemRead.class, entity.getClass(), entity);
		Validator.validate(clazz);
		if (!checkIndex()) {
			throw new OsemException("Invalid type. This type does not exist");
		}
		
		try {
			String id = ReflectionUtil.getId(entity);
			result = read(id);
			disp.publish(PostOsemRead.class, entity.getClass(), result);
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return result;
	}

	@Override
	public Object update(Object entity) {
		Object result = null;
		register(entity.getClass());
		disp.publish(PreOsemUpdate.class, entity.getClass(), entity);
		Validator.validate(clazz);
		if (!checkIndex()) {
			throw new OsemException("Invalid type. This type does not exist");
		}
		
		try {
			String id = ReflectionUtil.getId(entity);
			result = update(id, entity);
			disp.publish(PostOsemUpdate.class, entity.getClass(), result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public boolean delete(Object entity) {
		boolean result = false;
		register(entity.getClass());
		disp.publish(PreOsemDelete.class, entity.getClass(), entity);
		Validator.validate(clazz);
		if (!checkIndex()) {
			throw new OsemException("Invalid type. This type does not exist");
		}
		
		try {
			String id = ReflectionUtil.getId(entity);
			result = delete(id);
			if (result) {
				disp.publish(PostOsemDelete.class, entity.getClass(), entity);
			}
			else {
				disp.publish(PostOsemDelete.class, entity.getClass(), null);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public List<Object> find(String query, Class<?> clazz) {
		this.clazz = clazz;
		if (!checkIndex()) {
			throw new OsemException("Invalid type. This type does not exist");
		}
		
		try {
			return query(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public List<Object> findAll() {
		return null;
	}

	private void register(Class<?> clazz) {
		this.clazz = clazz;
		disp.register(this.clazz);
	}
	
}