package com.fossgalaxy.object.exceptions;

/**
 * Created by piers on 08/05/17.
 */
public class NoConverterInstalledException extends ObjectCreatorException {
    /**
     *
     * @param message Message explaining what went wrong
     */
    public NoConverterInstalledException(String message) {
        super(message);
    }
}
