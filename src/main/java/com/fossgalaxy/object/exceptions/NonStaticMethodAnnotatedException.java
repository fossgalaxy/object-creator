package com.fossgalaxy.object.exceptions;

/**
 * Created by Piers on 06/05/2017.
 */
public class NonStaticMethodAnnotatedException extends ObjectCreatorException {
    /**
     *
     * @param message Message explaining what went wrong
     */
    public NonStaticMethodAnnotatedException(String message) {
        super(message);
    }
}
