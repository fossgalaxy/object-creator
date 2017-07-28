package com.fossgalaxy.object.annotations;

import java.lang.annotation.*;

/**
 * Created by piers on 13/04/17.
 *
 * Annotation used to indicate a converter that isn't installed but should be used for
 * this type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
@Repeatable(ParameterHolder.class)
public @interface Parameter {
    /**
     * Specify the index of the parameter that this annotation refers to
     * @return int the zero based index
     */
    int id();

    /**
     * The local name of the function within the same class as this annotation that converts
     * a string to the required type.
     * @return The local name
     */
    String func() default "";

    /**
     * An optional description
     * @return The description
     */
    String description() default "";
}
