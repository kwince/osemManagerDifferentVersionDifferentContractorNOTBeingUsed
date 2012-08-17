package org.kwince.osem;

import java.io.Serializable;
import java.util.List;

import org.kwince.osem.es.exception.DocumentExistsException;

/**
 * Interface used to interact with search or document stores to perform CRUD
 * operations to entities or documents.
 * 
 * <p>
 * This interface is inspired from {@link javax.persistence.documentManager} in
 * fact almost all methods here are copied from documentManager interface
 * 
 * @author Allan Ramirez (ramirezag@gmail.com)
 * 
 */
public interface OsemSession extends Serializable {

    /**
     * Get the total of entities identified by class name
     * 
     * @param clazz
     *            class to count
     * @return total entities
     */
    <T> int count(Class<T> clazz);

    /**
     * Delete document or entity.
     * 
     * @param clazz
     *            document class
     * @param id
     *            document identifier
     */
    <T> void delete(Class<T> clazz, Serializable id);

    /**
     * Delete all documents identified by class.
     * 
     * @param clazz
     */
    void deleteAll(Class<?> clazz);

    /**
     * Save an instance to document store.
     * 
     * 
     * @param document
     *            document instance
     * @return id
     * @throws DocumentExistsException
     *             if the document identified by id already exists.
     */
    Serializable save(Object document) throws DocumentExistsException;

    /**
     * Save an instance to document store.
     * 
     * @param document
     *            document insta
     */
    void saveOrUpdate(Object document);

    /**
     * Find by primary key. Search for an document of the specified class and
     * primary key.
     * 
     * @param documentClass
     *            document class
     * @param primaryKey
     *            primary key
     * @return the found document instance or null if the document does not
     *         exist
     */
    <T> T find(Class<T> documentClass, Serializable primaryKey);

    /**
     * Search all documents of the specified class
     * 
     * @param documentClass
     * @return list of all entities or documents
     */
    <T> List<T> findAll(Class<T> documentClass);

    /**
     * Search all documents of the specified class that matches the query
     * 
     * @param documentClass
     * @param query
     * @return list of all entities or documents
     */
    <T> List<T> findAll(Class<T> documentClass, String query);
}
