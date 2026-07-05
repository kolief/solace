package net.solace.loader;

import net.runelite.client.RuneLite;

/** IDE / development entry point with RuneLite debug flags. */
public final class SolaceLoaderDev {
    private SolaceLoaderDev() {
    }

    public static void main(String[] args) throws Exception {
        RuneLite.main(new String[]{"--debug", "--developer-mode"});
        SolaceLoader.inject();
    }
}
