package com.fossgalaxy.object;

import com.fossgalaxy.object.annotations.ObjectDef;
import com.fossgalaxy.object.annotations.ObjectDefStatic;
import com.fossgalaxy.object.annotations.Parameter;
import com.fossgalaxy.object.exceptions.*;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;

/**
 * Created by piers on 13/04/17.
 * <p>
 * Object that allows creation of objects from strings at runtime
 *
 * @param <T> The type of object that you wish to instantiate
 */
public final class ObjectFinder<T> {
    private static final Logger logger = LoggerFactory.getLogger(ObjectFinder.class);
    private static final String PARAM_START = "[";
    private static final String PARAM_END = "]";
    private static final String PARAM_SEPARATOR = ":";
    private final Map<Class<?>, Function<String, ?>> converters;
    private final Map<String, ObjectFactory<T>> knownFactories;
    private final Map<String, List<RuntimeException>> exceptions;
    private final Class<T> clazz;
    private boolean hasScanned;

    /**
     * Constructor for an ObjectFinder
     *
     * @param clazz The class of the type and supertype of objects you wish to build
     */
    public ObjectFinder(Class<T> clazz) {
        this.converters = new HashMap<>();
        this.knownFactories = new HashMap<>();
        this.clazz = clazz;
        this.hasScanned = false;
        this.exceptions = new HashMap<>();

        buildConverters();
    }

    private static String[] splitArgs(String args) {
        int opens = 0;
        ArrayList<String> partsFound = new ArrayList<>();

        StringBuilder currentParam = new StringBuilder("");

        for (int index = 0; index < args.length(); index++) {
            char c = args.charAt(index);
            // handle params open/close
            if (c == PARAM_START.charAt(0)) {
                opens++;
            } else if (c == PARAM_END.charAt(0)) {
                opens--;
            } else if (c == PARAM_SEPARATOR.charAt(0) && opens == 0) {
                partsFound.add(currentParam.toString());
                currentParam.setLength(0);
                continue;
            }
            currentParam.append(Character.toString(c));
        }
        if (!"".equals(currentParam.toString())) {
            partsFound.add(currentParam.toString());
        }
        return partsFound.toArray(new String[partsFound.size()]);
    }

    private void buildConverters() {
        converters.put(String.class, Function.identity());
        converters.put(String[].class, Converters::parseStringArray);

        converters.put(Integer.class, Integer::parseInt);
        converters.put(int.class, Integer::parseInt);
        converters.put(int[].class, Converters::parseIntArray);
        converters.put(Integer[].class, Converters::parseIntegerArray);

        converters.put(Long.class, Long::parseLong);
        converters.put(long.class, Long::parseLong);
        converters.put(long[].class, Converters::parseLongArray);
        converters.put(Long[].class, Converters::parseLongClassArray);

        converters.put(Double.class, Double::parseDouble);
        converters.put(double.class, Double::parseDouble);
        converters.put(double[].class, Converters::parseDoubleArray);
        converters.put(Double[].class, Converters::parseDoubleClassArray);

        converters.put(Float.class, Float::parseFloat);
        converters.put(float.class, Float::parseFloat);
        converters.put(float[].class, Converters::parseFloatArray);
        converters.put(Float[].class, Converters::parseFloatClassArray);


        converters.put(Boolean.class, Boolean::parseBoolean);
        converters.put(boolean.class, Boolean::parseBoolean);
        converters.put(boolean[].class, Converters::parseBooleanArray);
        converters.put(Boolean[].class, Converters::parseBooleanClassArray);

        converters.put(clazz, this::buildObject);
    }

    /**
     * Add a custom converter to the unit
     *
     * @param clazz     the class of the object that this converter creates
     * @param converter The Function that performs the converting
     * @param <U>       The Type of the object that this converter creates
     */
    public <U> void addConverter(Class<U> clazz, Function<String, U> converter) {
        converters.put(clazz, converter);
    }

    /**
     * Build an object from a string
     *
     * @param data The string containing the object definition
     * @return The object of type <T>
     */
    public T buildObject(String data) {
        if (data.contains(PARAM_START) && data.contains(PARAM_END)) {
            String args = data.substring(data.indexOf(PARAM_START) + 1, data.lastIndexOf(PARAM_END));
            String[] splitArgs = splitArgs(args);
            String firstPart = data.substring(0, data.indexOf(PARAM_START));
            return buildObject(firstPart, splitArgs);
        }
        // Necessary for no args to have a zero length array of String as args
        return buildObject(data, new String[0]);
    }

    /**
     * Build an object from the name, and individual arguments
     *
     * @param name The name of the object from the definition
     * @param args The individual arguments
     * @return The Object that was built
     */
    public T buildObject(String name, String... args) {
        if (!hasScanned) {
            scanForObjects();
        }

        if (exceptions.containsKey(name)) {
            for (RuntimeException e : exceptions.get(name)) {
                throw e;
            }
        }

        ObjectFactory<T> factory = knownFactories.get(name);
        if (factory == null) {
            throw new IllegalArgumentException("Unknown factory type: " + name);
        }

        return factory.build(args);
    }

    private void scanForObjects() {
        if (hasScanned) {
            return;
        }

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forJavaClassPath())
                .setScanners(new MethodAnnotationsScanner(), new SubTypesScanner(), new TypeAnnotationsScanner())
                .setExpandSuperTypes(false)
        );

        scanForConstructors(reflections);
        scanForStaticMethods(reflections);

        hasScanned = true;
    }

    private void scanForStaticMethods(Reflections reflections) {
        Set<Method> methods = reflections.getMethodsAnnotatedWith(ObjectDefStatic.class);

        for (Method method : methods) {
            int modifiers = method.getModifiers();
            if (!(Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers))) {
                ObjectDefStatic objectBuilder = method.getDeclaredAnnotation(ObjectDefStatic.class);
                String name = objectBuilder.value();
                logger.info("Found: {}.{} annotated as {} ", method.getDeclaringClass(), method.getName(), name);
                if (!Modifier.isStatic(modifiers)) {
                    logException(name, new NonStaticMethodAnnotatedException("Method: " + method.getName() + " was annotated but wasn't static"));
                }
                if (!Modifier.isPublic(modifiers)) {
                    logException(name, new NonPublicMethodAnnotatedException("Method: " + method.getName() + " was annotated but wasn't public"));
                }
                continue;
            }

            try {
                ObjectFactory<T> factory = buildFactory(method);
                knownFactories.put(factory.name(), factory);
            } catch (IllegalArgumentException iae) {
                logger.error("Failed to parse static method: " + method.getDeclaringClass() + "->" + method.getName());
            }
        }
    }

    private void scanForConstructors(Reflections reflections) {
        Set<Class<? extends T>> objectClazzes = reflections.getSubTypesOf(clazz);
        // Only have subtypes - blindly add this class
        objectClazzes.add(clazz);
        for (Class<? extends T> objectClazz : objectClazzes) {
            int classMods = objectClazz.getModifiers();
            if (Modifier.isAbstract(classMods) || !Modifier.isPublic(classMods)) {
                continue;
            }

            logger.info("Found: {}", objectClazz.getCanonicalName());

            try {
                ObjectFactory<T> factory = buildFactory(objectClazz);
                knownFactories.put(factory.name(), factory);
            } catch (IllegalArgumentException iae) {
                logger.error("Failed to create object " + objectClazz);
            }
        }

    }

    private ObjectFactory<T> buildFactory(Method method) {
        ObjectDefStatic objectBuilder = method.getDeclaredAnnotation(ObjectDefStatic.class);
        String name = objectBuilder.value();
        HashMap<Integer, Parameter> parameters = getParameterMap(method.getAnnotationsByType(Parameter.class));
        Function<String, ?>[] convertersInst = getConverters(method.getDeclaringClass(), method.getParameterTypes(), parameters, name);
        return new MethodFactory<>(method.getDeclaringClass(), method, convertersInst, name);
    }

    private ObjectFactory<T> buildFactory(Class<? extends T> objectClazz) {
        Constructor<?> bestMatch = null;

        Constructor<?>[] constructors = objectClazz.getConstructors();
        for (Constructor<?> constructor : constructors) {
            ObjectDef builder = constructor.getAnnotation(ObjectDef.class);
            if (builder == null) {
                if (constructor.getParameterCount() == 0 && Modifier.isPublic(constructor.getModifiers())) {
                    bestMatch = constructor;
                }
            } else {
                String name = "".equals(builder.value()) ? objectClazz.getSimpleName() : builder.value();

                HashMap<Integer, Parameter> parameters = getParameterMap(constructor.getAnnotationsByType(Parameter.class));

                Function<String, ?>[] convertersInst = getConverters(objectClazz, constructor.getParameterTypes(), parameters, name);

                return new ConstructorFactory<>(objectClazz, constructor, convertersInst, name);
            }
        }


        if (bestMatch == null) {
            throw new IllegalArgumentException("You must either annotate a constructor or provide a public no-args constructor");
        }
        return new ConstructorFactory<>(objectClazz, bestMatch, null);
    }

    private HashMap<Integer, Parameter> getParameterMap(Parameter[] parameters) {
        HashMap<Integer, Parameter> parameterMap = new HashMap<>();
        for (Parameter p : parameters) {
            if (!parameterMap.containsKey(p.id())) {
                parameterMap.put(p.id(), p);
            }
        }
        return parameterMap;
    }

    private void logException(String name, RuntimeException exception) {
        exceptions.computeIfAbsent(name, x -> new ArrayList<>()).add(exception);
    }

    private Function<String, ?>[] getConverters(Class<?> objectClazz, Class<?>[] params, HashMap<Integer, Parameter> parameters, String name) {
        Function<String, ?>[] convertersInst = (Function[]) Array.newInstance(Function.class, params.length);
        for (int i = 0; i < params.length; i++) {
            if (parameters.containsKey(i)) {
                convertersInst[i] = handleParameter(objectClazz, params[i], name, parameters.get(i));
            } else {
                convertersInst[i] = handleConverter(params[i], name);
            }
        }
        return convertersInst;
    }

    private Function<String, ?> handleConverter(Class<?> param, String name) {
        if (param.isEnum()) {
            final Class enumClass = param;
            return s -> Enum.valueOf(enumClass, s);

        } else {
            if (converters.containsKey(param)) {
                return converters.get(param);
            } else {
                logException(name,
                        new NoConverterInstalledException("There wasn't a converter installed for type: " + param.getSimpleName())
                );
            }
        }
        return null;
    }

    private Function<String, ?> handleParameter(Class<?> objectClazz, Class<?> param, String name, Parameter parameter) {
        try {
            Method methodWithThatName = objectClazz.getMethod(parameter.func(), String.class);
            if (Modifier.isPublic(methodWithThatName.getModifiers()) && Modifier.isStatic(methodWithThatName.getModifiers())) {
                if (!methodWithThatName.getReturnType().isAssignableFrom(param)) {
                    logException(name,
                            new TypeMismatchException("you said param was a " + param + " but the converter wants to give me a " + methodWithThatName.getReturnType())
                    );
                }
                return s -> getConverter(methodWithThatName, s);
            }
        } catch (NoSuchMethodException e) {
            logException(name,
                    new IncorrectFunctionName("No function: " + parameter.func() + " in class: " + objectClazz.getSimpleName())
            );
        }

        return null;
    }

    private Object getConverter(Method methodWithThatName, String s) {
        try {
            return methodWithThatName.invoke(null, s);
        } catch (IllegalAccessException e) {
            logger.error("Couldn't access converter", e);
        } catch (InvocationTargetException e) {
            logger.error("", e);
        }
        return null;
    }

    /**
     * Gets the set of classes that we support as converters
     *
     * @return Set of Class that we support
     */
    public Set<Class<?>> getInstalledConverters() {
        return converters.keySet();
    }

    /**
     * Gets the set of IDs that you can ask this object to build
     *
     * @return Set of String that we support as IDs
     */
    public Set<String> getBuildableObjects() {
        return knownFactories.keySet();
    }
}
