package com.fossgalaxy.object.exceptions;

/**
 * Created by piers on 05/05/17.
 */
public class IncorrectFunctionName extends RuntimeException {
    public IncorrectFunctionName() {
    }

    public IncorrectFunctionName(String s) {
        super(s);
    }

    public IncorrectFunctionName(String s, Throwable throwable) {
        super(s, throwable);
    }

    public IncorrectFunctionName(Throwable throwable) {
        super(throwable);
    }

    public IncorrectFunctionName(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
