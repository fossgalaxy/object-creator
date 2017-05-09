package com.fossgalaxy.object.exceptions;

/**
 * Created by piers on 05/05/17.
 */
public class TypeMismatchException extends ObjectCreatorException {

    /**
     *
     * @param message Message explaining what went wrong
     */
    public TypeMismatchException(String message) {
        super(message);
    }

}
