package net.runelite.api;

/**
 * Stub for runelite-api versions before EntityOps was published.
 * Official solace-api 0.0.4 references this type from newer injected-client APIs.
 */
public interface EntityOps {
    int MAX_OPS = 0;

    String getOp(int index);

    int getNumSubOps(int opIndex);

    int getSubID(int opIndex, int subOpIndex);

    String getSubOp(int opIndex, int subOpIndex);
}
