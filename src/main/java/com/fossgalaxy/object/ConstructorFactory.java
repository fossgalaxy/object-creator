package com.fossgalaxy.object;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.function.Function;

/**
 * Created by piers on 05/05/17.
 */
class ConstructorFactory<T> implements ObjectFactory<T> {
    private final Class<? extends T> clazz;
    private final Constructor<?> constructor;
    private final Function<String, ?>[] converters;
    private final String name;

    public ConstructorFactory(Class<? extends T> clazz, Constructor<?> constructor, Function<String, ?>[] converters) {
        this(clazz, constructor, converters, clazz.getSimpleName());
    }

    public ConstructorFactory(Class<? extends T> clazz, Constructor<?> constructor, Function<String, ?>[] converters, String name) {
        this.clazz = clazz;
        this.constructor = constructor;
        this.converters = converters;
        this.name = name;
    }

    @Override
    public T build(String[] args) {
        Object[] params = new Object[0];
        if (converters != null) {
            if (converters.length != args.length) {
                throw new IllegalArgumentException("incorrect argument count to build class:"+clazz+", given: "+ Arrays.toString(args));
            }

            params = new Object[converters.length];
            for (int i = 0; i < params.length; i++) {
                params[i] = converters[i].apply(args[i]);
            }
        }

        try {
            return (T) constructor.newInstance(params);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String name() {
        return name;
    }
}
