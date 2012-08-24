package org.kwince.osem.exception;

import org.kwince.osem.es.metadata.PropertyMetadata;

/**
 * Exception thrown when Metadata related errors occur. An example is when field
 * in {@link PropertyMetadata} class has no setter and getter.
 * 
 * @author Allan Ramirez (ramirezag@gmail.com)
 * @see PropertyMetadata#addProperty(PropertyMetadata)
 */
public class MetadataException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -2072592554367968291L;

    public MetadataException(String message) {
        super(message);
    }

    public MetadataException(String message, Throwable e) {
        super(message, e);
    }
}
