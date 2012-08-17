package org.kwince.osem.exception;

public class MappingException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = -5465873477702408802L;

    public MappingException(String message) {
        super("Invalid mapping. " + message);
    }

    public MappingException(String message, Throwable e) {
        super("Invalid mapping. " + message, e);
    }
}
