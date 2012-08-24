package org.kwince.osem.exception;

import org.kwince.osem.es.cfg.Configuration;

/**
 * Exception thrown if {@link Configuration} is already initialized.
 * 
 * @author Allan Ramirez (ramirezag@gmail.com)
 * @see Configuration
 */
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
