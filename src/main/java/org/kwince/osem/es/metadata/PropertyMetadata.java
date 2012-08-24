package org.kwince.osem.es.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kwince.osem.exception.MetadataException;

/**
 * Represents and holds information about a property in a document.
 * <p>
 * Eg. <br>
 * <code>
 * &#064;Document
 * public class TestClass {
 * ...
 *  &#064;Property
 *  private String property1;
 *  
 *  public void setProperty1(String property1)...
 *  public String getProperty1()...
 * }
 * </code> <br>
 * In the above example, an instance of PropertyMetadata will be created that
 * represents property1 field or property
 * 
 * @author Allan Ramirez (ramirezag@gmail.com)
 * 
 */
public class PropertyMetadata {
    private Field field;
    private List<PropertyMetadata> properties;
    private Map<String, Method> methodMap = new HashMap<String, Method>();
    private Class<?> clazz;
    private String name;

    /**
     * Set the {@link Field} object
     * 
     * @param field
     */
    public void setField(Field field) {
        this.field = field;
        setClazz(field.getType());
        updateMethodMap();
    }

    /**
     * Set the {@link Field} object
     * 
     * @return field
     */
    public Field getField() {
        return field;
    }

    /**
     * Get the list of property metadata of this property.
     * 
     * @return list of property metadata of this property
     */
    public List<PropertyMetadata> getProperties() {
        return properties != null ? Collections.unmodifiableList(properties) : null;
    }

    public void addProperty(PropertyMetadata property) throws MetadataException {
        if (getSetter(property.getField()) == null || getGetter(property.getField()) == null) {
            throw new MetadataException("Property " + property.getName() + " has no setter or getter");
        }
        if (properties == null) {
            properties = new ArrayList<PropertyMetadata>();
        }
        properties.add(property);
    }

    /**
     * Get the class of this property. This could be primitive or user defined
     * classes.
     * 
     * @return class
     */
    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Method getGetter(Field field) {
        String fieldName = field.getName();
        String capitalizedFieldName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        Method method = methodMap.get("get" + capitalizedFieldName);
        if (method == null) {
            method = methodMap.get("is" + capitalizedFieldName);
        }
        return method;
    }

    public Method getSetter(Field field) {
        String fieldName = field.getName();
        String capitalizedFieldName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        return methodMap.get("set" + capitalizedFieldName);
    }

    protected void updateMethodMap() {
        methodMap.clear();
        for (Method method : getClazz().getMethods()) {
            methodMap.put(method.getName(), method);
        }
    }

    @Override
    public String toString() {
        return "Property Name: " + name + " - Type: " + clazz.getName();
    }
}
