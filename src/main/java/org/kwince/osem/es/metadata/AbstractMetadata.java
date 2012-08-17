package org.kwince.osem.es.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMetadata {
    private Map<String, Method> methodMap = new HashMap<String, Method>();
    private Class<?> clazz;
    private String name;

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

}
