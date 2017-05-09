package com.fossgalaxy.object.exceptions;

/**
 * Created by Piers on 06/05/2017.
 */
public class ObjectCreatorException extends RuntimeException {

    /**
     *
     * @param message Message explaining what went wrong
     */
    public ObjectCreatorException(String message) {
        super(message);
    }
}
