package org.kwince.osem.exception;

/**
 * Generic exception when there's something wrong upon reading the metadata of a
 * given class or when the class of an object to persist or read is not a
 * document class.
 * 
 * @author Allan Ramirez (ramirezag@gmail.com)
 * 
 */
public class MappingException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = -5465873477702408802L;

    public MappingException(String message) {
        super(message);
    }

    public MappingException(String message, Throwable e) {
        super(message, e);
    }
}
