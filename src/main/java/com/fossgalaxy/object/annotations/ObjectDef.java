package com.fossgalaxy.object.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to provide a creation key to a constructor on a class.
 * Must be used on a public constructor of a public non abstract class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface ObjectDef {
    /**
     * The value that this annotation assigns to the object to use as its creation key
     * @return String the value that is the creation key
     */
    String value();
}
