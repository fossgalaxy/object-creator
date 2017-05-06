package com.fossgalaxy.object;

import com.fossgalaxy.object.annotations.ObjectDef;

/**
 * Created by Piers on 06/05/2017.
 *
 * Desgined to test all the converters themselves
 */
public class ComplexTestObject {
    private String string;
    private Integer integerClass;
    private int integer;
    private Double doubleClass;
    private double dbl;
    private Float floatClass;
    private float flt;
    private Boolean booleanClass;
    private boolean bool;
    private int[] intArray;
    private double[] doubleArray;
    private float[] floatArray;

    @ObjectDef("Complex")
    public ComplexTestObject(String string, Integer integerClass, int integer, Double doubleClass, double dbl, Float floatClass, float flt, Boolean booleanClass, boolean bool, int[] intArray, double[] doubleArray, float[] floatArray) {
        this.string = string;
        this.integerClass = integerClass;
        this.integer = integer;
        this.doubleClass = doubleClass;
        this.dbl = dbl;
        this.floatClass = floatClass;
        this.flt = flt;
        this.booleanClass = booleanClass;
        this.bool = bool;
        this.intArray = intArray;
        this.doubleArray = doubleArray;
        this.floatArray = floatArray;
    }
}
