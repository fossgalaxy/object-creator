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
import org.reflections.util.FilterBuilder;
import org.reflections.vfs.Vfs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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
    public static final String PARAM_START = "[";
    public static final String PARAM_END = "]";
    public static final String PARAM_SEPARATOR = ":";
    private static final Logger logger = LoggerFactory.getLogger(ObjectFinder.class);
    private final Map<Class<?>, Function<String, ?>> converters;
    private final Map<String, Map<Integer, ObjectFactory<T>>> knownFactories;
    private final Map<String, List<RuntimeException>> exceptions;
    private final Class<T> clazz;
    private final String paramStart;
    private final String paramEnd;
    private final String paramSeparator;
    private final FilterBuilder filterBuilder;
    private final boolean cache;
    private final String cacheFilename;
    private boolean hasScanned;

    private ObjectFinder(Class<T> clazz, String paramStart, String paramSeparator, String paramEnd, boolean lazyScan, FilterBuilder filterBuilder, boolean cache, String cacheFilename) {
        this.converters = new HashMap<>();
        this.knownFactories = new HashMap<>();
        this.clazz = clazz;
        this.hasScanned = false;
        this.exceptions = new HashMap<>();
        this.paramStart = paramStart;
        this.paramSeparator = paramSeparator;
        this.paramEnd = paramEnd;
        this.filterBuilder = filterBuilder;
        this.cache = cache;
        this.cacheFilename = cacheFilename;

        buildConverters();
        if (!lazyScan) {
            scanForObjects();
        }
    }

    private String[] splitArgs(String args) {
        int opens = 0;
        ArrayList<String> partsFound = new ArrayList<>();

        StringBuilder currentParam = new StringBuilder("");

        for (int index = 0; index < args.length(); index++) {
            char c = args.charAt(index);
            // handle params open/close
            if (c == paramStart.charAt(0)) {
                opens++;
            } else if (c == paramEnd.charAt(0)) {
                opens--;
            } else if (c == paramSeparator.charAt(0) && opens == 0) {
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
        addConverter(String.class, Function.identity());
        addConverter(String[].class, Converters::parseStringArray);
        addConverter(Integer.class, Integer::parseInt);
        addConverter(int.class, Integer::parseInt);
        addConverter(int[].class, Converters::parseIntArray);
        addConverter(Integer[].class, Converters::parseIntegerArray);
        addConverter(Long.class, Long::parseLong);
        addConverter(long.class, Long::parseLong);
        addConverter(long[].class, Converters::parseLongArray);
        addConverter(Long[].class, Converters::parseLongClassArray);
        addConverter(Double.class, Double::parseDouble);
        addConverter(double.class, Double::parseDouble);
        addConverter(double[].class, Converters::parseDoubleArray);
        addConverter(Double[].class, Converters::parseDoubleClassArray);
        addConverter(Float.class, Float::parseFloat);
        addConverter(float.class, Float::parseFloat);
        addConverter(float[].class, Converters::parseFloatArray);
        addConverter(Float[].class, Converters::parseFloatClassArray);
        addConverter(Boolean.class, Boolean::parseBoolean);
        addConverter(boolean.class, Boolean::parseBoolean);
        addConverter(boolean[].class, Converters::parseBooleanArray);
        addConverter(Boolean[].class, Converters::parseBooleanClassArray);
        addConverter(clazz, this::buildObject);
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
        if (data.contains(paramStart) && data.contains(paramEnd)) {
            String args = data.substring(data.indexOf(paramStart) + 1, data.lastIndexOf(paramEnd));
            String[] splitArgs = splitArgs(args);
            String firstPart = data.substring(0, data.indexOf(paramStart));
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

        Map<Integer, ObjectFactory<T>> factoryMap = knownFactories.get(name);


        if (factoryMap == null) {
            throw new IllegalArgumentException("Unknown factory type: " + name);
        }
        ObjectFactory<T> factory = factoryMap.get(args.length);

        if (factory == null) {
            throw new IllegalArgumentException("Unknown number of parameters for: " + name + " - " + args.length);
        }

        return factory.build(args);
    }

    private void scanForObjects() {
        if (hasScanned) {
            return;
        }
        Reflections reflections;
        System.out.println(Vfs.getDefaultUrlTypes());
        File file = new File(cacheFilename);
        if (cache && file.exists()) {
            // Does cache file exist?
            reflections = new Reflections().collect(file);
        } else {
            reflections = new Reflections(new ConfigurationBuilder()
                    .setUrls(ClasspathHelper.forJavaClassPath())
                    .setScanners(new MethodAnnotationsScanner(), new SubTypesScanner(), new TypeAnnotationsScanner())
                    .setExpandSuperTypes(false)
                    .filterInputsBy(filterBuilder)
            );

            // Save the metadata!
            if (cache) {
                reflections.save(cacheFilename);
            }
        }

        scanForConstructors(reflections);
        scanForStaticMethods(reflections);


        hasScanned = true;
    }

    private void scanForStaticMethods(Reflections reflections) {
        Set<Method> methods = reflections.getMethodsAnnotatedWith(ObjectDefStatic.class);

        for (Method method : methods) {
            int modifiers = method.getModifiers();
            ObjectDefStatic objectBuilder = method.getDeclaredAnnotation(ObjectDefStatic.class);
            String name = objectBuilder.value();
            if (!(Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers))) {

                if (!Modifier.isStatic(modifiers)) {
                    logException(name, new NonStaticMethodAnnotatedException("Method: " + method.getName() + " was annotated but wasn't static"));
                }
                if (!Modifier.isPublic(modifiers)) {
                    logException(name, new NonPublicMethodAnnotatedException("Method: " + method.getName() + " was annotated but wasn't public"));
                }


                continue;
            }
            if (!method.getReturnType().isAssignableFrom(clazz)) {
                logger.warn("Found: {}.{} annotated as {} but was returning {}", method.getDeclaringClass(), method.getName(), name, method.getReturnType());
                continue;
            }
            logger.info("Found: {}.{} annotated as {} ", method.getDeclaringClass(), method.getName(), name);

            try {
                ObjectFactory<T> factory = buildFactory(method);
                addFactory(factory);
            } catch (IllegalArgumentException iae) {
                logger.error("Failed to parse static method: " + method.getDeclaringClass() + "->" + method.getName());
            }
        }
    }

    private void addFactory(ObjectFactory<T> factory) {
        Map<Integer, ObjectFactory<T>> factories = knownFactories.computeIfAbsent(factory.name(), x -> new HashMap<>());
        String key = factory.name();
        if(factories.containsKey(factory.getNumberOfArguments())){
            logger.error("Found duplicate key and number of arguments: ",  key);
        }else {
            factories.put(factory.getNumberOfArguments(), factory);
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
                addFactory(factory);

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

    /**
     * Builder object for an ObjectFinder
     *
     * @param <T> The type of objects you intend to build
     */
    public static class Builder<T> {
        private final Class<T> clazz;
        private String paramStart = PARAM_START;
        private String paramSeparator = PARAM_SEPARATOR;
        private String paramEnd = PARAM_END;
        private boolean lazyScan = true;
        private Collection<String> packagesToScan = new ArrayList<>();
        private boolean cache = false;
        private String cacheFilename = "";

        /**
         * Constructor for the builder - takes the only required parameter
         *
         * @param clazz The Class of the type that the ObjectFinder will build
         */
        public Builder(Class<T> clazz) {
            this.clazz = clazz;
        }


        /**
         * Sets the start parameter for the input Strings
         *
         * @param paramStart The character to use instead. Should be of length 1
         * @return Builder object for call chaining
         */
        public Builder<T> setParamStart(String paramStart) {
            if (paramStart.length() != 1) throw new IllegalArgumentException("Length of paramStart must be 1");
            this.paramStart = paramStart;
            return this;
        }

        /**
         * Sets the parameter separator for the input Strings
         *
         * @param paramSeparator The character to use instead. Should be of length 1
         * @return Builder object for call chaining
         */
        public Builder<T> setParamSeparator(String paramSeparator) {
            if (paramSeparator.length() != 1) throw new IllegalArgumentException("Length of paramSeparator must be 1");
            this.paramSeparator = paramSeparator;
            return this;
        }

        /**
         * Sets the end parameter marker for the input Strings
         *
         * @param paramEnd The character to use instead. Should be of length 1
         * @return Builder object for call chaining
         */
        public Builder<T> setParamEnd(String paramEnd) {
            if (paramEnd.length() != 1) throw new IllegalArgumentException("Length of paramEnd must be 1");
            this.paramEnd = paramEnd;
            return this;
        }

        public Builder<T> scanNow() {
            this.lazyScan = false;
            return this;
        }

        public Builder<T> addPackage(String packageToAdd) {
            packagesToScan.add(packageToAdd);
            return this;
        }

        public Builder<T> addPackage(String... packagesToAdd) {
            this.packagesToScan.addAll(Arrays.asList(packagesToAdd));
            return this;
        }

        public Builder<T> setCache(String filename) {
            this.cache = true;
            this.cacheFilename = filename;
            return this;
        }

        /**
         * Builds and returns the ObjectFinder of type <T>
         *
         * @return The ObjectFinder
         */
        public ObjectFinder<T> build() {
            FilterBuilder filterBuilder = new FilterBuilder();
            if (!packagesToScan.isEmpty()) {
                packagesToScan.forEach(filterBuilder::includePackage);
            }
            return new ObjectFinder<>(clazz, paramStart, paramSeparator, paramEnd, lazyScan, filterBuilder, cache, cacheFilename);
        }
    }
}
