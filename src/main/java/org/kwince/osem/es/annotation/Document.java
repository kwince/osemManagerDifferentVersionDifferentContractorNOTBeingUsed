package org.kwince.osem.es.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that the class is document which means this will be persisted in
 * Elastic Search document store.
 * 
 * @author Allan Ramirez (ramirezag@gmail.com)
 * 
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Document {

    /**
     * The name of a document. Defaults to the unqualified name of the document
     * class. This name is used to refer to the type which refers to table name
     * in Elastic Search.
     */
    String name() default "";
}
