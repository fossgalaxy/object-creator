package com.fossgalaxy.object;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;

/**
 * Created by piers on 05/05/17.
 */
class MethodFactory<T> implements ObjectFactory<T> {
private static final Logger logger = LoggerFactory.getLogger(MethodFactory.class);
    private final Class<?> clazz;
    private final Method method;
    private final Function<String, ?>[] converters;
    private final String name;

    MethodFactory(Class<?> clazz, Method method, Function<String, ?>[] converters) {
        this(clazz, method, converters, clazz.getSimpleName());
    }

    MethodFactory(Class<?> clazz, Method method, Function<String, ?>[] converters, String name) {
        this.clazz = clazz;
        this.method = method;
        this.converters = converters;
        this.name = name;
    }

    @Override
    public T build(String[] args) {
        Object[] params = new Object[0];
        if (converters != null) {
            if (converters.length != args.length) {
                throw new IllegalArgumentException("Incorrect argument count to build class: " + clazz + ", given " + Arrays.toString(args));
            }

            params = new Object[converters.length];
            for (int i = 0; i < params.length; i++) {
                params[i] = converters[i].apply(args[i]);
            }
        }

        try {
            return (T) method.invoke(null, params);
        } catch (IllegalAccessException e) {
            logger.error("Couldn't access method", e);
        } catch (InvocationTargetException e) {
            logger.error("", e);
        }
        return null;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("Object Factory for: %s - %s, %s", clazz.getSimpleName(), method, Arrays.toString(converters));
    }
}
