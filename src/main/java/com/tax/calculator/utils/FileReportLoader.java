package com.tax.calculator.utils;

import java.io.File;
import java.nio.file.Path;


public final class FileReportLoader {

    public static File load(String path) {
        File brokerFile = Path.of(path).toFile();
        if (!brokerFile.exists() || !brokerFile.isFile()) {
            throw new IllegalArgumentException("File not found: " + path);
        }

        System.out.println("File loaded: " + brokerFile.getAbsolutePath());
        return brokerFile;
    }

}
