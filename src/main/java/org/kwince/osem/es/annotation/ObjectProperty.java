package org.kwince.osem.es.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Allan Ramirez (ramirezag@gmail.com)
 * 
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface ObjectProperty {
    /**
     * (Optional) The name of the property.
     */
    String name() default "";
}
