package net.solace.loader.commons;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public final class Directories {
    private static final Path SOLACE_TEMP_DIR;
    public static final Path SOLACE_DIR;
    public static final Path SOLACE_CACHE_DIR;
    public static final Path CURRENT_TEMP_DIR;
    public static final Path EXTERNALPLUGIN_DIR;

    static {
        SOLACE_DIR = Paths.get(System.getProperty("user.home"), ".solace");
        SOLACE_CACHE_DIR = SOLACE_DIR.resolve("cache");
        SOLACE_TEMP_DIR = SOLACE_CACHE_DIR.resolve("temp");
        CURRENT_TEMP_DIR = SOLACE_TEMP_DIR.resolve(String.valueOf(ProcessHandle.current().pid()));
        EXTERNALPLUGIN_DIR = SOLACE_DIR.resolve("plugins");

        try {
            Files.createDirectories(CURRENT_TEMP_DIR);
        } catch (IOException e) {
            log.error("Error creating temporary directory", e);
        }
    }
}
