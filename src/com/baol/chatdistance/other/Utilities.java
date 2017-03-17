package com.baol.chatdistance.other;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Utilities created by AxeVillager on 2017/03/12
 */

public class Utilities {

    /**
     * Join a list together to a nice string, example: potato, carrot, onion
     */
    public static String createTextList(final ArrayList<String> al) {

        final StringBuilder builder = new StringBuilder();
        final String sep = ", ";
        for (final String item : al) builder.append(item).append(sep);
        return builder.length() == 0 ? "" : builder.substring(0, builder.length() - sep.length());
    }


    /**
     * Return a formatted number according to English number formatting
     */
    public static String formatNumber(final double d) {

        final DecimalFormat df = new DecimalFormat("#.#");
        df.setGroupingSize(3);
        df.setGroupingUsed(true);
        df.setRoundingMode(RoundingMode.CEILING);
        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        return df.format(d);

    }


    /**
     * Make a message where every letter is in the same typography, for example italic or bold
     */
    public static String makeMessageTypography(final String m, final ChatColor cc) {

        final StringBuilder sm = new StringBuilder();
        sm.append(cc);
        // The position of the character to be ignored (the char after §)
        int i = 0;

        for (int j = 0; j < m.length(); j++) {
            if (m.charAt(j) == '§') {
                i = j + 1;
            } else if (m.charAt(j) != '§' && ((m.charAt(j) != m.charAt(i) || m.charAt(i) == 0))) sm.append(cc);
            sm.append(m.charAt(j));
        }
        return new String (sm);
    }


    /**
     * Return a chat colour by its name
     */
    public static ChatColor getChatColor(final String colourName, final ChatColor defaultColour) {

        try {
            return ChatColor.valueOf(colourName);
        }
        catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(
                    colourName + " is not a valid chat colour. Using the respective default chat colour!");
            return defaultColour;
        }
    }


    /**
     * Make a message with correct formatting, where & is only replaced with § when there is a chat/format code
     */
    public static String chatCodeFormat(final String m) {

        char[] ma = m.toCharArray();
        final char[] f = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f', 'k', 'l', 'm', 'n', 'o', 'r',
                'A', 'B', 'C', 'D', 'E', 'F', 'K', 'L', 'M', 'N', 'O', 'R'};
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < ma.length; i++) {
            if (ma[i] == '&') {
                for (final char fl : f) {
                    if (ma.length > i + 1 && ma[i + 1] == fl) ma[i] = '§';
                }
            }
            sb.append(ma[i]);
        }
        return new String (sb);
    }


    /**
     * Counts the amount of a specific character at the end of a string
     */
    public static int countCharEnd(final String s, final char c) {

        int i = 0;
        for (int j = 0; j < s.length(); j++) {
            int k = s.length() - (j + 1);
            if (s.charAt(k) == c && s.length() > 1) {
                i++;
            } else {
                return i;
            }
        }
        return i;
    }


    /**
     * Counts the amount of a specific character in the message
     */
    public static int countChar(final String s, final char c) {

        int i = 0;
        for (final char ch : s.toCharArray())
            if (ch == c)
                i++;
        return i;
    }


    /**
     * Return the amount of whispering symbols in the string
     */
    public static int countWhisperSymbols(final String s, final String sym, final boolean bPar, final boolean bSym) {

        final char c = sym.charAt(0);
        int countPar = 0;
        int countSym = 0;

        for (int i = 0; i < s.length(); i++) {
            int j = s.length() - (i - countSym + 1);
            if (s.charAt(i) == '(' && s.charAt(j) == ')' && bPar) {
                countPar++;
            } else if (s.charAt(i) == c && bSym) {
                countSym++;
            } else {
                return countPar + countSym;
            }
        }
        return countPar + countSym;
    }


    /**
     * Return a string that has been stripped from its whisper symbols
     */
    public static String stripMessage(final String s, final String sym, final int i, final boolean bPar, final boolean bSym) {

        final char c = sym.charAt(0);
        final StringBuilder sb = new StringBuilder();
        int k = i;

        for (int j = 0; j < s.length(); j++) {
            if (s.charAt(j) == c && bSym) k--;
            if (!((j < i  && (s.charAt(j) == '(' || s.charAt(j) == c)) || (j > (s.length() - k - 1) && s.charAt(j) == ')' && bPar))) {
                sb.append(s.charAt(j));
            }
        }
        return new String (sb);
    }
}