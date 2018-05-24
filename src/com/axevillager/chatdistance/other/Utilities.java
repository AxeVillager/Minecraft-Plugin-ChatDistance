package com.axevillager.chatdistance.other;

import com.axevillager.chatdistance.commands.Permissions;
import com.axevillager.chatdistance.configuration.Configuration;
import com.axevillager.chatdistance.player.CustomPlayer;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Utilities created by AxeVillager on 2017/03/12
 */

public class Utilities {

    // Turn a list into a text list (string): "potato, carrot, onion".
    public static String createTextList(final List<String> arrayList) {
        final StringBuilder builder = new StringBuilder();
        final String separator = ", ";
        for (final String item : arrayList) {
            builder.append(item).append(separator);
        }
        return builder.length() == 0 ? "" : builder.substring(0, builder.length() - separator.length());
    }

    public static String formatNumber(final double number) {
        final DecimalFormat decimalFormat = new DecimalFormat("#.#");
        decimalFormat.setGroupingSize(3);
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setRoundingMode(RoundingMode.CEILING);
        decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        return decimalFormat.format(number);
    }


    // Make a message where every letter is in the same typography, for example italic or bold.
    public static String makeMessageTypography(final String message, final ChatColor typography) {
        final StringBuilder builder = new StringBuilder();
        builder.append(typography);

        int ignoreCharacterIndex = 0;
        for (int j = 0; j < message.length(); j++) {
            final char currentChar = message.charAt(j);
            final char ignoreCharacter = message.charAt(ignoreCharacterIndex);

            if (currentChar == '§') {
                ignoreCharacterIndex = j + 1;
            } else if (currentChar != ignoreCharacter) {
                builder.append(typography);
            }

            builder.append(currentChar);
        }
        return new String (builder);
    }

    public static ChatColor getChatColor(final String colourName, final ChatColor defaultColour) {
        try {
            return ChatColor.valueOf(colourName);
        }
        catch (Exception e) {
            Bukkit.getLogger().warning(colourName + " is not a valid chat colour. Using the respective default chat colour (" + defaultColour.name() + ").");
            return defaultColour;
        }
    }

    // Make a message with formatting where & is only replaced with § when there is a chat/format code.
    public static String formatChatCodes(final String message) {
        final char[] messageArray = message.toCharArray();
        final int messageArrayLength = messageArray.length;
        final char[] formatCharacters = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f', 'k', 'l', 'm', 'n', 'o', 'r',
                'A', 'B', 'C', 'D', 'E', 'F', 'K', 'L', 'M', 'N', 'O', 'R'};
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < messageArrayLength; i++) {
            char currentCharacter = messageArray[i];
            if (currentCharacter == '&') {
                final char nextCharacter = messageArray[i + 1];
                for (final char formatCharacter : formatCharacters) {
                    if (nextCharacter == formatCharacter) {
                        currentCharacter = '§';
                    }
                }
            }
            builder.append(currentCharacter);
        }
        return new String (builder);
    }

    public static int countCharacterAtEndOfMessage(final String message, final char character) {
        final int messageLength = message.length();
        int counter = 0;
        for (int i = 0; i < messageLength; i++) {
            int lastCharacter = messageLength - (i + 1);
            if (message.charAt(lastCharacter) == character && messageLength > 1) {
                counter++;
            }
        }
        return counter;
    }

    public static int countCharacterInMessage(final String message, final char character) {
        int counter = 0;
        for (final char currentCharacter : message.toCharArray())
            if (currentCharacter == character)
                counter++;
        return counter;
    }

    public static int countWhisperSymbols(final String message) {
        int parenthesisesCounter = 0;
        int otherWhisperSymbolCounter = 0;

        for (int i = 0; i < message.length(); i++) {
            final char currentCharacter = message.charAt(i);
            int indexAtEnd = message.length() - (i - otherWhisperSymbolCounter + 1); // Ignores other whisper symbols in the beginning of the message influencing the position of the ending parenthesis.
            final char endCharacter = message.charAt(indexAtEnd);

            if (currentCharacter == '(' && endCharacter == ')' && Configuration.WHISPER_WITH_PARENTHESISES) {
                parenthesisesCounter++;
            } else if (currentCharacter == Configuration.OTHER_WHISPER_SYMBOL && Configuration.WHISPER_WITH_OTHER_SYMBOL) {
                otherWhisperSymbolCounter++;
            }
        }
        return parenthesisesCounter + otherWhisperSymbolCounter;
    }

    public static String stripMessageFromAmountOfWhisperSymbols(final String message, final int amount) {
        final StringBuilder builder = new StringBuilder();
        final int messageLength = message.length();
        int amountOfWhisperSymbolsToRemoveLeft = amount;
        final char otherWhisperSymbol = Configuration.OTHER_WHISPER_SYMBOL;

        for (int i = 0; i < messageLength; i++) {
            final char currentCharacter = message.charAt(i);
            if (currentCharacter == otherWhisperSymbol && Configuration.WHISPER_WITH_OTHER_SYMBOL) {
                amountOfWhisperSymbolsToRemoveLeft--;
            }
            if (! (i < amount && (currentCharacter == '(' || currentCharacter == otherWhisperSymbol) || (i > (messageLength - amountOfWhisperSymbolsToRemoveLeft - 1) && currentCharacter == ')' && Configuration.WHISPER_WITH_PARENTHESISES))) {
                builder.append(currentCharacter);
            }
        }
        return new String (builder);
    }

    public static boolean messageIsEmpty(final String message) {
        int countSymbol = 0;
        for (final char letter : message.toCharArray()) {
            if (letter == '§') {
                countSymbol++;
            }
        }
        return countSymbol << 1 >= message.length();
    }

    public static boolean messageStartsWithSpace(final String message) {
        if (message.startsWith(" ")) {
            return true;
        }
        return message.startsWith("§") && message.length() > 2 && message.charAt(2) == ' ';
    }

    public static String translateChatFormat(final String format) {
        return format.replace("name", "%1$s").replace("message", "%2$s").replace("&", "§");
    }

    public static String formatMessage(final String format, final Player player, final String message) {
        return String.format(format, player.getDisplayName(), message);
    }

    public static List<CustomPlayer> getStaff() {
        final List<CustomPlayer> list = new ArrayList<>();
        for (final CustomPlayer customPlayer : CustomPlayer.getCustomPlayers()) {
            if (customPlayer.hasPermission(Permissions.STAFF_MESSAGE)) {
                list.add(customPlayer);
            }
        }
        return ImmutableList.copyOf(list);
    }
}
