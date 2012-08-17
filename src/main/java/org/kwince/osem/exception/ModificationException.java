package org.kwince.osem.exception;

public class ModificationException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -7599588942424860692L;

    public ModificationException(String message) {
        super(message);
    }

    public ModificationException(String message, Throwable e) {
        super(message, e);
    }
}
