package com.tax.calculator.utils;

public final class SystemPropertyUtils {

    public static String getPath(String name) {
        var value = System.getProperty(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required JVM property: -D" + name + "=<path>");
        }

        return value;
    }
}
