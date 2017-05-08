package com.fossgalaxy.object;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Piers on 06/05/2017.
 * <p>
 * Helper class to store some methods for converting more complex
 * java objects
 */
class Converters {
    static int[] parseIntArray(String data) {
        String[] args = data.split(",");
        int[] argInt = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            argInt[i] = Integer.parseInt(args[i]);
        }
        return argInt;
    }

    static List<Integer> parseIntegerList(String data) {
        return Arrays.
                stream(data.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    static Integer[] parseIntegerArray(String data) {
        return Arrays.
                stream(data.split(","))
                .map(Integer::parseInt)
                .toArray(Integer[]::new);
    }

    static double[] parseDoubleArray(String data) {
        String[] args = data.split(",");
        double[] argInt = new double[args.length];
        for (int i = 0; i < args.length; i++) {
            argInt[i] = Double.parseDouble(args[i]);
        }
        return argInt;
    }

    static List<Double> parseDoubleList(String data) {
        return Arrays
                .stream(data.split(","))
                .map(Double::parseDouble)
                .collect(Collectors.toList());
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

    static List<Float> parseFloatList(String data) {
        return Arrays
                .stream(data.split(","))
                .map(Float::parseFloat)
                .collect(Collectors.toList());
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

    static List<Boolean> parseBooleanList(String data) {
        return Arrays
                .stream(data.split(","))
                .map(Boolean::parseBoolean)
                .collect(Collectors.toList());
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

    static List<String> parseStringList(String data) {
        return Arrays
                .stream(data.split(","))
                .collect(Collectors.toList());
    }
}
