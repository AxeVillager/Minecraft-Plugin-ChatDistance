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

public class Utilities {

    /**
     * Join a list together to a nice string, example: potato, carrot, onion
     */
    public static String createTextList(final ArrayList<String> l) {

        // A new string builder
        final StringBuilder sb = new StringBuilder();

        // The separator
        final String s = ", ";

        // For every item in the list
        for (final String item : l) {

            // Add the item and a separator
            sb.append(item).append(s);

        }

        // Create a string from the string builder
        final String sl = sb.toString();

        // Check if the length of the string is zero
        if (sl.length() == 0) {

            // Return nothing
            return "";

        }

        // Return the string apart from the last separator
        return sl.substring(0, sl.length() - s.length());
    }


    /**
     * Return a formatted number according to English number formatting
     */
    public static String formatNumber(final double d) {

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
        return df.format(d);

    }


    /**
     * Make a message where every letter is in the same typography, for example italic or bold
     */
    public static String makeMessageTypography(final String m, final ChatColor cc) {

        final StringBuilder sm = new StringBuilder();
        sm.append(cc);
        int i = 0;
        for (int j = 0; j < m.length(); j++) {
            if (m.charAt(j) == '§') {
                i = j;
            } else if (m.charAt(j) != '§') {
                if (m.charAt(j) != m.charAt(i) || m.charAt(i) == 0) sm.append(cc);
            }
            sm.append(m.charAt(j));
        }
        return new String (sm);
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
            Bukkit.getConsoleSender().sendMessage(colourName + " is not a valid chat colour. Using the respective default chat colour!");

            // Return the default chat colour
            return defaultColour;

        }

    }


    /**
     * Make a message with correct formatting, where & is only replaced with § when there is a chat/format code
     */
    public static String makeFormatted(final String m) {

        // The message in an array of characters
        char[] ma = m.toCharArray();

        // The possible formatting letters in an array
        final char[] f = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f', 'k', 'l', 'm', 'n', 'o', 'r'};

        // The new message that will be built
        final StringBuilder sm = new StringBuilder();

        // For every letter in the message
        for (int i = 0; i < ma.length; i++) {

            // Check if the letter is '&'
            if (ma[i] == '&') {

                // For every letter in the formatting letters array
                for (final char fl : f) {

                    // Check if the message has another letter after the current one
                    if (ma.length > i + 1) {

                        // Check if the next letter is a formatting letter
                        if (ma[i + 1] == fl) {

                            // Replace the '&' with a '§' (chat formatting symbol)
                            ma[i] = '§';

                        }

                    }

                }

            }

            // Append all the letters to form the new message
            sm.append(ma[i]);

        }

        // Return the new message as string
        return new String (sm);

    }


    /**
     * Counts the amount of exclamation marks in the end of a sentence
     */
    public static int countExclamationMarks(String m) {

        int i = 0;
        for (int j = 0; j < m.length(); j++) {
            int k = m.length() - (j + 1);
            if (m.charAt(k) == '!' && m.length() > 1) {
                i++;
            } else {
                return i;
            }
        }
        return i;
    }


    /**
     * Counts the amount of whisper symbols in the beginning of the message
     */
    public static int countCharacter(final String m, final char c) {

        int i = 0;
        for (int j = 0; j < m.length(); j++) {
            if (m.charAt(j) == c && m.length() > 1 + j) {
                i++;
            } else {
                return i;
            }
        }
        return i;
    }


    /**
     * Counts the amount of parenthesis nests around the message
     */
    public static int countParenthesisNests(final String m) {

        int i = 0;
        for (int j = 0; j < m.length(); j++) {
            int k = m.length() - (j + 1);
            if (m.charAt(j) == '(' && m.charAt(k) == ')' && m.length() > 2 + (j << 1)) {
                i++;
            } else {
                return i;
            }
        }
        return i;
    }

}