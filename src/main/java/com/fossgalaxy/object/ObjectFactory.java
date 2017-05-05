package com.fossgalaxy.object;

/**
 * Created by piers on 13/04/17.
 */
public interface ObjectFactory<T> {

    T build(String[] args);
    default T build(){ return build(new String[0]);}

    default String name(){ return "";}
}
