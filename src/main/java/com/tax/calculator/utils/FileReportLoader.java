package com.tax.calculator.utils;

import java.io.File;
import java.nio.file.Path;


public final class FileReportLoader {


    public static File load(String path) {
        File brokerFile = Path.of(path).toFile();

        System.out.println("File loaded: " + brokerFile.getAbsolutePath());
        return brokerFile;
    }

    public static String getAbsolutePath(String path) {
        var file = Path.of(path).normalize().toFile();

        if (!file.exists()) {
            throw new IllegalArgumentException("File not found: " + path);
        }

        if (!file.isFile()) {
            throw new IllegalArgumentException("Not a file: " + path);
        }

        return file.getAbsolutePath();
    }

}
