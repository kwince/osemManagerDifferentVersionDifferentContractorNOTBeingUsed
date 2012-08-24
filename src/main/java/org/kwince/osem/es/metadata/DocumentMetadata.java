package org.kwince.osem.es.metadata;

import java.lang.reflect.Field;

/**
 * Represents and holds information about a document class.
 * 
 * @author Allan Ramirez (ramirezag@gmail.com)
 * 
 */
public class DocumentMetadata extends PropertyMetadata {
    private Field idField;

    public DocumentMetadata(Class<?> clazz) {
        setClazz(clazz);
        updateMethodMap();
    }

    public void setField(Field field) {
        throw new UnsupportedOperationException("DocumentMetadata is a representation of a document class and not document property");
    }

    public Field getField() {
        throw new UnsupportedOperationException("DocumentMetadata is a representation of a document class and not document property");
    }

    public Field getIdField() {
        return idField;
    }

    public void setIdField(Field idField) {
        this.idField = idField;
    }

    @Override
    public String toString() {
        return "Document class: " + getClazz().getName();
    }

}
