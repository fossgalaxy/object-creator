package com.fossgalaxy.object.exceptions;

/**
 * Created by piers on 05/05/17.
 */
public class IncorrectFunctionName extends ObjectCreatorException {
    /**
     *
     * @param message Message explaining what went wrong
     */
    public IncorrectFunctionName(String s) {
        super(s);
    }

}
