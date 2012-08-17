package org.kwince.osem.es.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Is used to exclude property from mapping
 * 
 * <blockquote>
 * 
 * <pre>
 *    Example:
 * 
 *    &#064;Transient
 *    private String strAge;
 * 
 * </pre>
 * 
 * </blockquote>
 * 
 * @author Allan Ramirez (ramirezag@gmail.com)
 * 
 */
@Target({ FIELD })
@Retention(RUNTIME)
public @interface Transient {

}
