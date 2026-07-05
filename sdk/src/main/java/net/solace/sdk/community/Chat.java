package net.solace.sdk.community;

import net.solace.sdk.game.Client;

public class Chat {
    public static void sendPublic(String message) {
        Chat.send(message, 0, 0, false, -1);
    }

    public static void send(String message, int modes, int clanType, boolean useTarget, int target) {
        Client.runScript(5517, message, modes, clanType, useTarget, target);
    }
}

