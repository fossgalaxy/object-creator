package com.fossgalaxy.object.exceptions;

/**
 * Created by piers on 05/05/17.
 */
public class TypeMismatchException extends RuntimeException {

    public TypeMismatchException() {
    }

    public TypeMismatchException(String s) {
        super(s);
    }

    public TypeMismatchException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public TypeMismatchException(Throwable throwable) {
        super(throwable);
    }

    public TypeMismatchException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
