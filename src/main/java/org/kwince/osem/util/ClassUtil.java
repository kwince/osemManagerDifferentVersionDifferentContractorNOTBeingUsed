package org.kwince.osem.util;

import java.lang.reflect.Field;

import org.kwince.osem.exception.MappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ClassUtil {
    INSTANCE;

    private Logger log = LoggerFactory.getLogger(getClass());

    public String getStringValue(Field field, Object object) {
        Class<?> cls = object.getClass();
        try {
            Object result = field.get(object).toString();
            if (result != null) {
                return result.toString();
            } else {
                throw new MappingException(String.format("Id field %s has no value.", cls.getName() + field.getName()));
            }
        } catch (IllegalAccessException e) {
            log.error("Exception when invoking getters for " + cls.getName() + "." + field.getName(), e);
            throw new MappingException(String.format("Exception when invoking getters for %s", cls.getName() + field.getName()), e);
        }
    }

}
