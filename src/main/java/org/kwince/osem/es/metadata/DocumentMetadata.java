package org.kwince.osem.es.metadata;

import java.lang.reflect.Field;

public class DocumentMetadata extends PropertyMetadata {
    private Field idField;

    public DocumentMetadata(Class<?> clazz) {
        setClazz(clazz);
        updateMethodMap();
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
