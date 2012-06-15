package org.kwince.contribs.osem.dao;

import java.util.List;

public interface BasicDao<K, E> {
    E create(E entity);
    E read(K id);
    E update(E entity);
    boolean delete(K id);
    
    List<E> find(String query, Class<?> clazz);
	List<E> findAll();
}