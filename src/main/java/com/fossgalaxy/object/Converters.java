package com.fossgalaxy.object;

import java.util.Arrays;

/**
 * Created by Piers on 06/05/2017.
 * <p>
 * Helper class to store some methods for converting more complex
 * java objects
 */
class Converters {
    // Hiding any public constructor
    private Converters(){}

    static int[] parseIntArray(String data) {
        String[] args = data.split(",");
        int[] argInt = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            argInt[i] = Integer.parseInt(args[i]);
        }
        return argInt;
    }

    static Integer[] parseIntegerArray(String data) {
        return Arrays.
                stream(data.split(","))
                .map(Integer::parseInt)
                .toArray(Integer[]::new);
    }

    static Long[] parseLongClassArray(String data) {
        return Arrays
                .stream(data.split(","))
                .map(Long::parseLong)
                .toArray(Long[]::new);
    }

    static long[] parseLongArray(String data) {
        String[] args = data.split(",");
        long[] results = new long[args.length];
        for (int i = 0; i < args.length; i++) {
            results[i] = Long.parseLong(args[i]);
        }
        return results;
    }


    static double[] parseDoubleArray(String data) {
        String[] args = data.split(",");
        double[] argInt = new double[args.length];
        for (int i = 0; i < args.length; i++) {
            argInt[i] = Double.parseDouble(args[i]);
        }
        return argInt;
    }

    static Double[] parseDoubleClassArray(String data) {
        return Arrays
                .stream(data.split(","))
                .map(Double::parseDouble)
                .toArray(Double[]::new);
    }

    static float[] parseFloatArray(String data) {
        String[] args = data.split(",");
        float[] argInt = new float[args.length];
        for (int i = 0; i < args.length; i++) {
            argInt[i] = Float.parseFloat(args[i]);
        }
        return argInt;
    }

    static Float[] parseFloatClassArray(String data) {
        return Arrays
                .stream(data.split(","))
                .map(Float::parseFloat)
                .toArray(Float[]::new);
    }

    static boolean[] parseBooleanArray(String data) {
        String[] args = data.split(",");
        boolean[] result = new boolean[args.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Boolean.parseBoolean(args[i]);
        }
        return result;
    }

    static Boolean[] parseBooleanClassArray(String data) {
        return Arrays
                .stream(data.split(","))
                .map(Boolean::parseBoolean)
                .toArray(Boolean[]::new);
    }

    static String[] parseStringArray(String data) {
        return Arrays
                .stream(data.split(","))
                .toArray(String[]::new);
    }
}
