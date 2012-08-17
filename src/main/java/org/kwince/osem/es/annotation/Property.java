package org.kwince.osem.es.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Is used to specify the mapped column for a property or field. This is
 * optional. By default, all fields which are not marked as @Transient, not a
 * subclass of java Collection and not an instance of Array in a document class
 * is mapped.
 * 
 * <blockquote>
 * 
 * <pre>
 *    Example 1:
 * 
 *    &#064;Property(name="DESC")
 *    private String description;
 * 
 * </pre>
 * 
 * </blockquote>
 * 
 * @author Allan Ramirez (ramirezag@gmail.com)
 * 
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface Property {

    /**
     * (Optional) The name of the property.
     */
    String name() default "";

}
