package org.kwince.contribs.osem.dao;

import java.util.List;

import org.elasticsearch.client.Client;
import org.kwince.contribs.osem.annotations.PostOsemCreate;
import org.kwince.contribs.osem.annotations.PostOsemDelete;
import org.kwince.contribs.osem.annotations.PostOsemRead;
import org.kwince.contribs.osem.annotations.PostOsemUpdate;
import org.kwince.contribs.osem.annotations.PreOsemCreate;
import org.kwince.contribs.osem.annotations.PreOsemDelete;
import org.kwince.contribs.osem.annotations.PreOsemRead;
import org.kwince.contribs.osem.annotations.PreOsemUpdate;
import org.kwince.contribs.osem.event.EventDispatcher;

import org.kwince.contribs.osem.exceptions.OsemException;
import org.kwince.contribs.osem.util.ReflectionUtil;
import org.kwince.contribs.osem.validation.Validator;

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