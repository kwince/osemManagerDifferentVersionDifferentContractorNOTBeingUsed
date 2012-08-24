package org.kwince.osem.es.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * * Specifies the id property in Elastic Search
 * (http://localhost:9300/twitter/{indexName}/{id}).
 * 
 * <blockquote>
 * 
 * <pre>
 *   Example:
 * 
 *   &#064;Id
 *   private Long id;
 * </pre>
 * 
 * </blockquote>
 * 
 * @author Allan Ramirez (ramirezag@gmail.com)
 * 
 */
@Target({ FIELD })
@Retention(RUNTIME)
public @interface Id {
}
