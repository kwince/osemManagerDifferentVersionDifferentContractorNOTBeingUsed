package org.kwince.osem.exception;

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
