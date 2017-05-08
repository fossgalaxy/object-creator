package com.fossgalaxy.object;

import static com.fossgalaxy.object.ObjectFinder.PARAM_END;
import static com.fossgalaxy.object.ObjectFinder.PARAM_SEPARATOR;
import static com.fossgalaxy.object.ObjectFinder.PARAM_START;

public class ObjectFinderBuilder<T> {
    private final Class<T> clazz;
    private T[] array = null;
    private String paramStart = PARAM_START;
    private String paramSeparator = PARAM_SEPARATOR;
    private String paramEnd = PARAM_END;

    public ObjectFinderBuilder(Class<T> clazz) {
        this.clazz = clazz;
    }

    public ObjectFinderBuilder setArray(T[] array) {
        this.array = array;
        return this;
    }

    public ObjectFinderBuilder setParamStart(String paramStart) {
        this.paramStart = paramStart;
        return this;
    }

    public ObjectFinderBuilder setParamSeparator(String paramSeparator) {
        this.paramSeparator = paramSeparator;
        return this;
    }

    public ObjectFinderBuilder setParamEnd(String paramEnd) {
        this.paramEnd = paramEnd;
        return this;
    }

    public ObjectFinder<T> build() {
        return new ObjectFinder<>(clazz, array, paramStart, paramSeparator, paramEnd);
    }
}