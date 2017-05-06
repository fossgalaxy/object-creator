package com.fossgalaxy.object.exceptions;

/**
 * Created by Piers on 06/05/2017.
 */
public class NonStaticMethodAnnotatedException extends ObjectCreatorException {
    public NonStaticMethodAnnotatedException(String message) {
        super(message);
    }
}
