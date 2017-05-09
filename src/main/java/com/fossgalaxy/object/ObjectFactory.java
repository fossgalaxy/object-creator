package com.fossgalaxy.object;

/**
 * Internal objects that create objects for us
 */
interface ObjectFactory<T> {

    T build(String[] args);
    default T build(){ return build(new String[0]);}

    default String name(){ return "";}
}
