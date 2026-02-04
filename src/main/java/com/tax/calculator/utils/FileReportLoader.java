package com.tax.calculator.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;


@Slf4j
public final class FileReportLoader {

    private static final String BROKER_REPORT = "broker.report";
    private static final String RATES_FILE = "rates.file";

    public static String getRatesPath() {
        var property = SystemPropertyUtils.getPath(RATES_FILE);
        return FileReportLoader.getAbsolutePath(property);
    }

    public static String getBrokerReport() {
        var property = SystemPropertyUtils.getPath(BROKER_REPORT);
        return FileReportLoader.getAbsolutePath(property);
    }

    public static File load(String path) {
        File brokerFile = Path.of(path).toFile();

        log.info("File loaded: {}", brokerFile.getAbsolutePath());
        return brokerFile;
    }

    private static String getAbsolutePath(String path) {
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
