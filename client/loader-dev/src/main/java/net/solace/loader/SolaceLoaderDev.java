package net.solace.loader;

import net.runelite.client.RuneLite;

public class SolaceLoaderDev {
    public static void main(String[] args) throws Exception {
        RuneLite.main(new String[]{"--debug", "--developer-mode"});
        SolaceLoader.inject();
    }
}
