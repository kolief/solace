package net.solace.rscache;

import java.io.File;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Constants {
    private static final File RUNELITE_DIR = new File(System.getProperty("user.home"), ".runelite");
    private static final File JAGEXCACHE_DIR = new File(RUNELITE_DIR, "jagexcache");
    private static final File OLDSCHOOL_DIR = new File(JAGEXCACHE_DIR, "oldschool");
    private static final File SOLACE_DIR = new File(System.getProperty("user.home"), ".solace");
    public static final File OSRS_CACHE_DIR = new File(OLDSCHOOL_DIR, "LIVE");
    public static final File DUMP_DIR = new File(SOLACE_DIR, "cache-dump");
    public static final File DEFAULT_PROJECT_DIR = new File(System.getProperty("user.home") + "/IdeaProjects/solace-loader");
    public static final int OPENRS2_CACHE_VERSION = 2364;

    static {
        DUMP_DIR.mkdirs();
    }
}
