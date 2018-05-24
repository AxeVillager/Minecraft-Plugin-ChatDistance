package com.axevillager.chatdistance.configuration;

import com.axevillager.chatdistance.Main;
import org.bukkit.ChatColor;

import static com.axevillager.chatdistance.other.Utilities.getChatColor;
import static com.axevillager.chatdistance.other.Utilities.translateChatFormat;

/**
 * Configuration created by AxeVillager on 2018/04/25.
 */

public class Configuration {

    public final static String STAFF_CHAT_FORMAT = translateChatFormat(Main.config.getString("staff chat format", "&fname &cSTAFF&7: message"));
    public final static String BROADCAST_CHAT_FORMAT = translateChatFormat(Main.config.getString("broadcast chat format", "&4Broadcast&7: &cmessage"));
    public final static String GLOBAL_CHAT_FORMAT = translateChatFormat(Main.config.getString("global chat format", "&fname &eglobal&7: message"));
    public final static String OUT_OF_CONTEXT_CHAT_FORMAT = translateChatFormat(Main.config.getString("out of context chat format", "&fname &eOOC&7: message"));
    public final static String OBSCURE_CHAT_FORMAT = translateChatFormat(Main.config.getString("chat format", "&fname&7: message"));
    public final static double OBSCURE_CHAT_RANGE_DIVISOR = Main.config.getDouble("obscure chat range divisor", 2.5);
    public final static double OBSCURE_CHAT_PERCENTAGE_AMPLIFIER = Main.config.getDouble("obscure chat percentage amplifier", 1.75);
    public final static double CHAT_RANGE = Main.config.getDouble("chat range", 60);
    public final static boolean WHISPER_WITH_PARENTHESISES = Main.config.getBoolean("whisper with parenthesises", true);
    public final static char OTHER_WHISPER_SYMBOL = Main.config.getString("other whisper symbol", "~").charAt(0);
    public final static boolean WHISPER_WITH_OTHER_SYMBOL = Main.config.getBoolean("whisper with other symbol", true);
    public final static boolean SHOW_WHISPERING_IN_ITALICS = Main.config.getBoolean("show whispering in italics", true);
    public final static double WHISPER_CHAT_RANGE_DECREASE = Main.config.getDouble("whisper chat range decrease", 25);
    public final static int MAX_WHISPER_LEVEL = Main.config.getInt("max whisper level", 2);
    public final static boolean SHOW_SHOUTING_IN_BOLD = Main.config.getBoolean("show shouting in bold", true);
    public final static boolean ONLY_COUNT_EXCLAMATION_MARKS_AT_THE_END = Main.config.getBoolean("only count exclamation marks at the end of the sentence", false);
    public final static int SHOUT_HUNGER_LOSS = Main.config.getInt("shout hunger loss", 2);
    public final static double SHOUT_CHAT_RANGE_INCREASE = Main.config.getDouble("shout chat range increase", 30);
    public final static int MAX_SHOUT_LEVEL = Main.config.getInt("max shout level", 4);
    public final static String TOO_LOW_FOOD_LEVEL_TO_SHOUT_MESSAGE = translateChatFormat(Main.config.getString("too low food level to shout message", "&cYour food level is too low to shout!"));

    public final static boolean LOCAL_JOIN_AND_LEAVE_MESSAGES = Main.config.getBoolean("use local join/leave messages", true);
    public final static double JOIN_AND_LEAVE_MESSAGES_RANGE = Main.config.getDouble("join/leave messages range", 250);
    public final static String JOIN_MESSAGE = Main.config.getString("join message", "&7&oname joined...");
    public final static String LEAVE_MESSAGE = Main.config.getString("leave message", "&7&oname left...");

    public final static boolean LOCAL_DEATH_MESSAGES = Main.config.getBoolean("use local death messages", true);
    public final static double DEATH_MESSAGES_RANGE = Main.config.getDouble("death messages range", 250);
    public final static ChatColor DEATH_MESSAGE_COLOUR = getChatColor(Main.config.getString("death messages colour"), ChatColor.GRAY);
    public final static ChatColor DEATH_MESSAGE_TYPOGRAPHY = getChatColor(Main.config.getString("death messages typography"), ChatColor.ITALIC);

    public final static boolean SHOW_SENDER_CHAT_RANGE_IN_CONSOLE = Main.config.getBoolean("show senders chat range in console", true);
    public final static boolean SHOW_WHISPER_AND_SHOUT_LEVELS_IN_CONSOLE = Main.config.getBoolean("show whisper and shout levels in console", true);
    public final static boolean SHOW_MESSAGE_RECEIVED_IN_CONSOLE = Main.config.getBoolean("show message received in console", true);
    public final static boolean SHOW_RECEIVERS_DISTANCE_TO_SENDER_IN_CONSOLE = Main.config.getBoolean("show receivers distance to sender in console", true);
    public final static boolean SHOW_DEATH_MESSAGE_RECEIVERS_IN_CONSOLE = Main.config.getBoolean("show death message receivers in console", true);
    public final static boolean SHOW_RECEIVED_DEATH_MESSAGE_DISTANCE_IN_CONSOLE = Main.config.getBoolean("show received death message distance in console", true);
    public final static boolean SHOW_JOIN_AND_LEAVE_MESSAGE_RECEIVERS_IN_CONSOLE = Main.config.getBoolean("show join/leave message receivers in console", true);
    public final static boolean SHOW_RECEIVED_JOIN_AND_LEAVE_MESSAGE_DISTANCE_IN_CONSOLE = Main.config.getBoolean("show received join/leave message distance in console", true);
}