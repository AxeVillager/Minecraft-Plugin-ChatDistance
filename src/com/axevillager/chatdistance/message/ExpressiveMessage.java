package com.axevillager.chatdistance.message;

import com.axevillager.chatdistance.commands.Permissions;
import com.axevillager.chatdistance.configuration.Configuration;
import com.axevillager.chatdistance.player.CustomPlayer;
import org.bukkit.ChatColor;

import static com.axevillager.chatdistance.other.Utilities.*;

/**
 * ExpressiveMessage created by AxeVillager on 2018/05/21.
 */

public class ExpressiveMessage {

    private final String originalMessage;
    private final String expressiveMessage;
    private final CustomPlayer sender;
    private final int whisperLevel;
    private final int shoutLevel;
    private final double chatRange;

    public ExpressiveMessage(final String originalMessage, final CustomPlayer sender) {
        this.originalMessage = originalMessage;
        this.sender = sender;
        whisperLevel = calculateWhisperLevel();
        shoutLevel = calculateShoutLevel();
        chatRange = calculateChatRange();
        expressiveMessage = createExpressiveMessage();
    }

    private int calculateWhisperLevel() {
        return Math.min(countWhisperSymbols(originalMessage), Configuration.MAX_WHISPER_LEVEL);
    }

    private int calculateShoutLevel() {
        int shoutLevel;
        if (Configuration.ONLY_COUNT_EXCLAMATION_MARKS_AT_THE_END) {
            shoutLevel = countCharacterAtEndOfMessage(originalMessage, '!');
        } else {
            shoutLevel = countCharacterInMessage(originalMessage, '!');
        }
        return Math.min(shoutLevel, Configuration.MAX_SHOUT_LEVEL);
    }

    private double calculateChatRange() {
        double range = Configuration.CHAT_RANGE;
        range -= Configuration.WHISPER_CHAT_RANGE_DECREASE * whisperLevel;
        range += Configuration.SHOUT_CHAT_RANGE_INCREASE * shoutLevel;
        if (range < 1) {
            range = 1;
        }
        return range;
    }

    private String createExpressiveMessage() {
        String message = originalMessage;
        if (whisperLevel > 0 && sender.hasPermission(Permissions.WHISPER)) {
            if (Configuration.SHOW_WHISPERING_IN_ITALICS) {
                final String strippedMessage = stripMessageFromAmountOfWhisperSymbols(message, whisperLevel);
                message = makeMessageTypography(strippedMessage, ChatColor.ITALIC);
            }
        }
        if (shoutLevel > 0 && sender.hasPermission(Permissions.SHOUT)) {
            if (Configuration.SHOW_SHOUTING_IN_BOLD) {
                message = makeMessageTypography(message, ChatColor.BOLD);
            }
        }
        if (messageStartsWithSpace(message)) {
            message = message.replaceFirst(" ", "");
        }
        return message;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

    public String getExpressiveMessage() {
        return expressiveMessage;
    }

    public CustomPlayer getSender() {
        return sender;
    }

    public int getWhisperLevel() {
        return whisperLevel;
    }

    public int getShoutLevel() {
        return shoutLevel;
    }

    public double getChatRange() {
        return chatRange;
    }
}
