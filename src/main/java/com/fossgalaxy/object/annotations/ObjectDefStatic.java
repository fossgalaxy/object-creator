package com.fossgalaxy.object.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation to provide a creation key to a static method.
 * Must be used on a public static method returning the correct type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ObjectDefStatic {
    /**
     * The value that this annotation assigns to the object to use as its creation key
     * @return String the value that is the creation key
     */
    String value();
}
