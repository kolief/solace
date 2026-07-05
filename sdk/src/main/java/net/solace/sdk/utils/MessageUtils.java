package net.solace.sdk.utils;

import java.awt.Color;
import net.runelite.api.ChatMessageType;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.QueuedMessage;
import net.solace.api.Static;
import net.solace.sdk.game.Game;
import net.solace.sdk.game.GameThread;

public class MessageUtils {
    public static void addMessage(String message, ChatColorType colorType, ChatMessageType messageType) {
        if (Game.isLoggedIn()) {
            String chatMessage = new ChatMessageBuilder().append(colorType).append(message).build();
            GameThread.invokeAndWait(() -> Static.getChatMessageManager().queue(QueuedMessage.builder().type(messageType).runeLiteFormattedMessage(chatMessage).build()));
        }
    }

    public static void addMessage(String message, Color color, ChatMessageType messageType) {
        if (Game.isLoggedIn()) {
            String chatMessage = new ChatMessageBuilder().append(color, message).build();
            GameThread.invokeAndWait(() -> Static.getChatMessageManager().queue(QueuedMessage.builder().type(messageType).runeLiteFormattedMessage(chatMessage).build()));
        }
    }

    public static void addMessage(String message, ChatMessageType messageType) {
        MessageUtils.addMessage(message, ChatColorType.HIGHLIGHT, messageType);
    }

    public static void addMessage(String message, ChatColorType colorType) {
        MessageUtils.addMessage(message, colorType, ChatMessageType.CONSOLE);
    }

    public static void addMessage(String message, Color color) {
        MessageUtils.addMessage(message, color, ChatMessageType.CONSOLE);
    }

    public static void addMessage(String message) {
        MessageUtils.addMessage(message, ChatColorType.HIGHLIGHT, ChatMessageType.CONSOLE);
    }
}

