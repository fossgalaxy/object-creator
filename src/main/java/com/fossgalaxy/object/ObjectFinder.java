package com.fossgalaxy.object;

import com.fossgalaxy.object.annotations.ObjectDef;
import com.fossgalaxy.object.annotations.ObjectDefStatic;
import com.fossgalaxy.object.annotations.Parameter;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by piers on 13/04/17.
 */
public final class ObjectFinder<T> {
    private static final Logger logger = LoggerFactory.getLogger(ObjectFinder.class);
    private final Map<Class<?>, Function<String, ?>> converters;
    private final Map<String, ObjectFactory<T>> knownFactories;
    private final Class<T> clazz;

    private boolean hasScanned;

    public ObjectFinder(Class<T> clazz) {
        this.converters = new HashMap<>();
        this.knownFactories = new HashMap<>();
        this.clazz = clazz;
        this.hasScanned = false;

        buildConverters();
    }

    private static int[] parseIntArray(String data) {
        String[] args = data.split(",");
        int[] argInt = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            argInt[i] = Integer.parseInt(args[i]);
        }
        return argInt;
    }

    private static double[] parseDoubleArray(String data) {
        String[] args = data.split(",");
        double[] argInt = new double[args.length];
        for (int i = 0; i < args.length; i++) {
            argInt[i] = Double.parseDouble(args[i]);
        }
        return argInt;
    }

    private static float[] parseFloatArray(String data) {
        String[] args = data.split(",");
        float[] argInt = new float[args.length];
        for (int i = 0; i < args.length; i++) {
            argInt[i] = Float.parseFloat(args[i]);
        }
        return argInt;
    }

    private void buildConverters() {
        converters.put(String.class, Function.identity());
        converters.put(Integer.class, Integer::parseInt);
        converters.put(int.class, Integer::parseInt);
        converters.put(Double.class, Double::parseDouble);
        converters.put(double.class, Double::parseDouble);
        converters.put(Float.class, Float::parseFloat);
        converters.put(float.class, Float::parseFloat);
        converters.put(Boolean.class, Boolean::parseBoolean);
        converters.put(boolean.class, Boolean::parseBoolean);
        converters.put(int[].class, ObjectFinder::parseIntArray);
        converters.put(double[].class, ObjectFinder::parseDoubleArray);
        converters.put(float[].class, ObjectFinder::parseFloatArray);
    }

    public <U> void addConverter(Class<U> clazz, Function<String, U> converter) {
        converters.put(clazz, converter);
    }

    public T buildObject(String name, String... args) {
        if (!hasScanned) {
            scanForObjects();
        }

        ObjectFactory<T> factory = knownFactories.get(name);
        if (factory == null) {
            throw new IllegalArgumentException("Unkown factory type: " + name);
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

    private void scanForStaticMethods(Reflections reflections){
        Set<Method> methods = reflections.getMethodsAnnotatedWith(ObjectDefStatic.class);

        for(Method method : methods){
            int modifiers = method.getModifiers();
            if(!(Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers))){
                continue;
            }

            try {
                ObjectFactory<T> factory = buildFactory(method);
                knownFactories.put(factory.name(), factory);
            }catch(IllegalArgumentException iae){
                logger.error("Failed to parse static method: " + method.getDeclaringClass() + "->" + method.getName());
            }
        }
    }

    private void scanForConstructors(Reflections reflections){
        Set<Class<? extends T>> objectClazzes = reflections.getSubTypesOf(clazz);
        // Only have subtypes - blindly add this class
        objectClazzes.add(clazz);
        for(Class<? extends T> objectClazz : objectClazzes){
            int classMods = objectClazz.getModifiers();
            if(Modifier.isAbstract(classMods) || !Modifier.isPublic(classMods)){
                continue;
            }

            try {
                ObjectFactory<T> factory = buildFactory(objectClazz);
                knownFactories.put(factory.name(), factory);
            }catch (IllegalArgumentException iae){
                logger.error("Failed to create object " + objectClazz);
            }
        }

    }

    private ObjectFactory<T> buildFactory(Method method){
        ObjectDefStatic objectBuilder = method.getDeclaredAnnotation(ObjectDefStatic.class);
        String name = objectBuilder.value();
        HashMap<Integer, Parameter> parameters = new HashMap<>();
        for(Parameter p : method.getAnnotationsByType(Parameter.class)){
            if(!parameters.containsKey(p.id())){
                parameters.put(p.id(), p);
            }
        }
        Function<String, ?>[] convertersInst = getConverters(method.getDeclaringClass(), method.getParameterTypes(), parameters);
        return new MethodFactory<>(method.getDeclaringClass(), method, convertersInst, name);
    }

    private ObjectFactory<T> buildFactory(Class<? extends T> objectClazz){
        Constructor<?> bestMatch = null;

        Constructor<?>[] constructors = objectClazz.getConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == 0 && Modifier.isPublic(constructor.getModifiers())) {
                bestMatch = constructor;
            } else {
                ObjectDef builder = constructor.getAnnotation(ObjectDef.class);
                if (builder == null) {
                    continue;
                }

                String name = builder.value().equals("") ? objectClazz.getSimpleName() : builder.value();
                bestMatch = constructor;

                HashMap<Integer, Parameter> parameters = new HashMap<>();
                for (Parameter p : constructor.getAnnotationsByType(Parameter.class)) {
                    if (!parameters.containsKey(p.id())) {
                        parameters.put(p.id(), p);
                    }
                }

                Function<String, ?>[] convertersInst = getConverters(objectClazz, constructor.getParameterTypes(), parameters);

                return new ConstructorFactory<T>(objectClazz, constructor, convertersInst, name);
            }
        }

        if (bestMatch == null) {
            throw new IllegalArgumentException("You must either annotate a constructor or provide a public no-args constructor");
        }

        return new ConstructorFactory<>(objectClazz, bestMatch, null);


    }

    private Function<String, ?>[] getConverters(Class<?> objectClazz, Class<?>[] params, HashMap<Integer, Parameter> parameters) {
        Function<String, ?>[] convertersInst = (Function[]) Array.newInstance(Function.class, params.length);
        for (int i = 0; i < params.length; i++) {
            if (parameters.containsKey(i)) {
                Parameter parameter = parameters.get(i);
                try {
                    Method methodWithThatName = objectClazz.getMethod(parameter.func(), String.class);
                    if (Modifier.isPublic(methodWithThatName.getModifiers()) && Modifier.isStatic(methodWithThatName.getModifiers())) {

                        if (!methodWithThatName.getReturnType().isAssignableFrom(params[i])) {
                            throw new IllegalArgumentException("you said params " + i + " was a " + params[i] + " but the converter wants to give me a " + methodWithThatName.getReturnType());
                        }
                        convertersInst[i] = (s) -> getConverter(methodWithThatName, s);
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            } else {
                // Try to handle enums with a default
                if (params[i].isEnum()) {
                    final Class enumClass = params[i];
                    convertersInst[i] = (s) -> Enum.valueOf(enumClass, s);

                } else {
                    convertersInst[i] = converters.get(params[i]);
                }
            }
        }

        return convertersInst;
    }

    private Object getConverter(Method methodWithThatName, String s) {
        try {
            return methodWithThatName.invoke(null, s);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
