package com.baol.chatdistance.other;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Other created by Børre A. Opedal Lunde on 2017/03/12
 */

public class Other {

    /**
     * Join a list together to a nice string, example: potato, carrot, onion
     */
    public static String createTextList(final ArrayList<String> list) {

        // A new string builder
        final StringBuilder sb = new StringBuilder();

        // The separator
        final String separator = ", ";

        // For every item in the list
        for (final String item : list) {

            // Add the item and a separator
            sb.append(item).append(separator);

        }

        // Create a string from the string builder
        final String s = sb.toString();

        // Check if the length of the string is zero
        if (s.length() == 0) {

            // Return nothing
            return "";

        }

        // Return the string apart from the last separator
        return s.substring(0, s.length() - separator.length());
    }


    /**
     * Return a formatted number according to English number formatting
     */
    public static String formatNumber(final double number) {

        // A new decimal format so we can format the recipient's distance neatly with only one decimal number
        final DecimalFormat df = new DecimalFormat("#.#");

        // Set grouping to 3 (ex. 1,074)
        df.setGroupingSize(3);

        // Turn on grouping
        df.setGroupingUsed(true);

        // Round the number to the ceiling
        df.setRoundingMode(RoundingMode.CEILING);

        // Use the English decimal format symbols
        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));

        // Return the formatted number
        return df.format(number);

    }


    /**
     * Make a message where every letter is in bold text
     */
    public static String makeCompletelyBold(String message) {

        // Build the new bold message
        final StringBuilder boldMessage = new StringBuilder();

        // Add bold to the beginning of the message
        boldMessage.append(ChatColor.BOLD);

        // Position of every letter
        int i = 0;

        // The letter position to be ignored
        int ignore = 0;

        // The message as a character array
        char[] messageArray = message.toCharArray();

        // For every letter in the message
        for (final char letter : messageArray) {

            // Add one to i for every letter
            i++;

            // Check if the letter is the chat format symbol '§'
            if (letter == '§') {

                // The letter position to ignore so that chat formatting is never messed with
                ignore = i;

            }

            // Check if the letter is '§'
            if (letter != '§') {

                // Check if the letter is not at the position of the character to be ignored
                if (letter != messageArray[ignore] || messageArray[ignore] == 0) {

                    // Append the bold chat colour to the message
                    boldMessage.append(ChatColor.BOLD);

                }

            }

            // Append the letter to the message
            boldMessage.append(letter);

        }

        // Return the new bold message
        return new String (boldMessage);
    }


    /**
     * Make a message where every letter is in italic text
     */
    public static String makeCompletelyItalic(String message) {

        // Build the new italic message
        final StringBuilder italicMessage = new StringBuilder();

        // Add italic in the beginning of the message
        italicMessage.append(ChatColor.ITALIC);

        // Position of every letter
        int i = 0;

        // The letter position to be ignored
        int ignore = 0;

        // The message as a character array
        char[] messageArray = message.toCharArray();

        // For every letter in the message
        for (final char letter : messageArray) {

            // Add one to i for every letter
            i++;

            // Check if the letter is the chat format symbol '§'
            if (letter == '§') {

                // The letter position to ignore so that chat formatting is never messed with
                ignore = i;

            }

            // Check if the letter is '§'
            if (letter != '§') {

                // Check if the letter is not at the position of the character to be ignored
                if (letter != messageArray[ignore] || messageArray[ignore] == 0) {

                    // Append the italic chat colour to the message
                    italicMessage.append(ChatColor.ITALIC);

                }

            }

            // Append the letter to the message
            italicMessage.append(letter);

        }

        // Return the new bold message
        return new String (italicMessage);
    }


    /**
     * Make a message where every letter's format is reset
     */
    public static String makeCompletelyReset(String message) {

        // Build the new reset message
        final StringBuilder resetMessage = new StringBuilder();

        // Add reset in the beginning of the message
        resetMessage.append(ChatColor.RESET);

        // Position of every letter
        int i = 0;

        // The letter position to be ignored
        int ignore = 0;

        // The message as a character array
        char[] messageArray = message.toCharArray();

        // For every letter in the message
        for (final char letter : messageArray) {

            // Add one to i for every letter
            i++;

            // Check if the letter is the chat format symbol '§'
            if (letter == '§') {

                // The letter position to ignore so that chat formatting is never messed with
                ignore = i;

            }

            // Check if the letter is '§'
            if (letter != '§') {

                // Check if the letter is not at the position of the character to be ignored
                if (letter != messageArray[ignore] || messageArray[ignore] == 0) {

                    // Append the reset chat format to the message
                    resetMessage.append(ChatColor.RESET);

                }

            }

            // Append the letter to the message
            resetMessage.append(letter);

        }

        // Debug message
        // Bukkit.broadcastMessage(new String (resetMessage).replace("§", "$"));

        // Return the new reset message
        return new String (resetMessage);
    }


    /**
     * Return a chat colour by its name
     */
    public static ChatColor getChatColor(final String colourName, final ChatColor defaultColour) {

        // Try to...
        try {

            // Return the chat colour value of the colour name
            return ChatColor.valueOf(colourName);

        }

        // Catch any exception...
        catch (Exception e) {

            // Notify the console the chat colour is invalid
            Bukkit.getConsoleSender().sendMessage(colourName + " is not a valid chat colour. Using default chat colour!");

            // Return the default chat colour
            return defaultColour;

        }

    }


    /**
     * Make a message with correct formatting, where & is only replaced with § when there is a chat/format code
     */
    public static String makeFormatted(String message) {

        // The message in an array of characters
        char[] messageArray = message.toCharArray();

        // The possible formatting letters in an array
        final char[] formattingLetters = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f', 'k', 'l', 'm', 'n', 'o', 'r'};

        // The new message that will be built
        final StringBuilder symbolsMessage = new StringBuilder();

        // For every letter in the message
        for (int i = 0; i < messageArray.length; i++) {

            // Check if the letter is '&'
            if (messageArray[i] == '&') {

                // For every letter in the formatting letters array
                for (final char formattingLetter : formattingLetters) {

                    // Check if the message has another letter after the current one
                    if (messageArray.length > i + 1) {

                        // Check if the next letter is a formatting letter
                        if (messageArray[i + 1] == formattingLetter) {

                            // Replace the '&' with a '§' (chat formatting symbol)
                            messageArray[i] = '§';

                        }

                    }

                }

            }

            // Append all the letters to form the new message
            symbolsMessage.append(messageArray[i]);

        }

        // Return the new message as string
        return new String (symbolsMessage);

    }

}