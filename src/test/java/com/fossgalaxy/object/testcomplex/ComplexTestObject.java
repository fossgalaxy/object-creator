package com.fossgalaxy.object.testcomplex;

import com.fossgalaxy.object.annotations.ObjectDef;

/**
 * Created by Piers on 06/05/2017.
 * <p>
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
    private Integer[] integerArray;
    private double[] doubleArray;
    private Double[] DoubleArray;
    private float[] floatArray;
    private Float[] FloatArray;
    private boolean[] boolArray;
    private Boolean[] booleanArray;
    private String[] stringArray;
    private long aLong;
    private Long aLongClass;
    private long[] aLongArray;
    private Long[] aLongArrayClass;
    private TestEnum testEnum;

    @ObjectDef("Complex")
    public ComplexTestObject(String string, Integer integerClass, int integer, Double doubleClass, double dbl, Float floatClass, float flt, Boolean booleanClass, boolean bool, int[] intArray, Integer[] integerArray, double[] doubleArray, Double[] doubleArray1, float[] floatArray, Float[] floatArray1, boolean[] boolArray, Boolean[] booleanArray, String[] stringArray, long aLong, Long aLongClass, long[] aLongArray, Long[] aLongArrayClass, TestEnum testEnum) {
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
        this.integerArray = integerArray;
        this.doubleArray = doubleArray;
        DoubleArray = doubleArray1;
        this.floatArray = floatArray;
        FloatArray = floatArray1;
        this.boolArray = boolArray;
        this.booleanArray = booleanArray;
        this.stringArray = stringArray;
        this.aLong = aLong;
        this.aLongClass = aLongClass;
        this.aLongArray = aLongArray;
        this.aLongArrayClass = aLongArrayClass;
        this.testEnum = testEnum;
    }


}
