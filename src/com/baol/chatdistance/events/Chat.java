package com.baol.chatdistance.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.baol.chatdistance.other.Utilities.*;

/**
 * Chat created by AxeVillager on 2017/03/10
 */

public class Chat implements Listener {

    private final String CHAT_FORMAT;
    private final double OBSCURE_CHAT_RANGE_DIVISOR;
    private final double OBSCURE_CHAT_PERCENTAGE_AMPLIFIER;
    private final boolean USE_WHISPER_PARENTHESISES;
    private final String WHISPER_SYMBOL;
    private final boolean USE_WHISPER_SYMBOL;
    private final boolean USE_WHISPER_ITALICS;
    private final double WHISPER_CHAT_RANGE_DECREASE;
    private final int MAX_WHISPER_LEVEL;
    private final double CHAT_RANGE;
    private final boolean SHOUT_BOLD;
    private final boolean SHOUT_EXCLAMATION_ONLY_AT_END;
    private final int SHOUT_HUNGER_LOSS;
    private final String SHOUT_TOO_LOW_HUNGER_MESSAGE;
    private final double SHOUT_RANGE_INCREASE;
    private final int MAX_SHOUT_LEVEL;
    private final String GLOBAL_MESSAGE_PREFIX;
    private final String GLOBAL_CHAT_FORMAT;
    private final boolean SHOW_SENDER_CHAT_RANGE;
    private final boolean SHOW_WHISPER_AND_SHOUT_LEVELS;
    private final boolean SHOW_MESSAGE_RECEIVED;
    private final boolean SHOW_RECEIVER_DISTANCE;


    /**
     * Constructor
     */
    public Chat(final JavaPlugin plugin) {

        final FileConfiguration config = plugin.getConfig();
        CHAT_FORMAT = config.getString("chat format", "&fname&7: message").replace("name", "%1$s").replace("message", "%2$s").replace("&", "§");
        OBSCURE_CHAT_RANGE_DIVISOR = config.getDouble("obscure chat range divisor", 2.5);
        OBSCURE_CHAT_PERCENTAGE_AMPLIFIER = config.getDouble("percentage amplifier", 1.75);
        USE_WHISPER_PARENTHESISES = config.getBoolean("whisper with parenthesises", true);
        WHISPER_SYMBOL = config.getString("whisper symbol", "~");
        USE_WHISPER_SYMBOL = config.getBoolean("whisper with symbol", true);
        USE_WHISPER_ITALICS = config.getBoolean("whisper italic", true);
        WHISPER_CHAT_RANGE_DECREASE = config.getDouble("whisper chat range decrease", 30);
        MAX_WHISPER_LEVEL = config.getInt("whisper max level", 2);
        CHAT_RANGE = config.getDouble("chat range", 50);
        SHOUT_BOLD = config.getBoolean("shout bold", true);
        SHOUT_EXCLAMATION_ONLY_AT_END = plugin.getConfig().getBoolean("exclamation marks at the end", false);
        SHOUT_HUNGER_LOSS = config.getInt("shout hunger loss", 2);
        SHOUT_TOO_LOW_HUNGER_MESSAGE = config.getString("too low food level to shout", "&cYour food level is too low to shout!").replace("&", "§");
        SHOUT_RANGE_INCREASE = config.getDouble("shout chat range increase", 20);
        MAX_SHOUT_LEVEL = config.getInt("shout max level", 4);
        GLOBAL_MESSAGE_PREFIX = config.getString("global prefix", "global:");
        GLOBAL_CHAT_FORMAT = config.getString("global chat format", "&fname &e(global)&7: message").replace("name", "%1$s").replace("message", "%2$s").replace("&", "§");
        SHOW_SENDER_CHAT_RANGE = config.getBoolean("sender chat range", true);
        SHOW_WHISPER_AND_SHOUT_LEVELS = config.getBoolean("whisper and shout levels", true);
        SHOW_MESSAGE_RECEIVED = config.getBoolean("message received", false);
        SHOW_RECEIVER_DISTANCE = config.getBoolean("receiver distance", false);
    }


    /**
     * When any player sends a message in the chat
     */
    @EventHandler
    private void onPlayerChat(final AsyncPlayerChatEvent event) {


        /*
         * Format the message correctly where '&' is only replaced
         * with '§' when the '&' is followed by a formatting code
         * letter and the sender has permission to format chat messages.
         */

        final Player sender = event.getPlayer();
        String message = event.getMessage();
        if (sender.hasPermission("chatdistance.formatting")) message = chatCodeFormat(message);




        /*
         * Cancel the chat event if the message contains more or
         * equal to half its size of the chat formatting symbol (§).
         */

        int countSymbol = 0;
        for (final char letter : message.toCharArray()) {
            if (letter == '§') countSymbol++;
        }
        if (countSymbol << 1 >= message.length()) {
            event.setCancelled(true);
            return;
        }




        /*
         * Globally deliver the message and cancel the regular chat event
         * if the sender has permission to send global messages and the
         * message starts with the global message prefix.
         */

        if (sender.hasPermission("chatdistance.global") && message.startsWith(GLOBAL_MESSAGE_PREFIX)) {
            globalMessage(sender,message.substring(GLOBAL_MESSAGE_PREFIX.length()));
            event.setCancelled(true);
            return;
        }




        /*
         * The default chat range
         */

        double chatRange = CHAT_RANGE;




        /*
         *  Turn the regular chat message into a shout message.
         *
         * The shouting level is indicated by the amount of exclamation marks
         * in or at the end of the chat message - depending on the value of
         * the option in the configuration. The value can not exceed the
         * max shouting level.
         *
         * If the sender has permission to shout the console will be given
         * information of the shout level if the option for doing so is turned
         * on in the configuration. The player only loses hunger if he/she is
         * in survival or adventure mode. The hunger loss is decided by
         * the shouting level multiplied with the default factor in the
         * configuration - the shout is cancelled if the player doesn't
         * have high enough food level.
         *
         * The shouting chat message increases the range of the message
         * respectively with its shouting level and if shouting in bold
         * text is turned on in the configuration the chat message will
         * appear in bold text.
         */

        final ArrayList<String> senderInfo = new ArrayList<>();
        final int shoutLevel = Math.min(SHOUT_EXCLAMATION_ONLY_AT_END
                ? countCharEnd(message, '!')
                : countChar(message, '!'), MAX_SHOUT_LEVEL);

        if (shoutLevel > 0 && sender.hasPermission("chatdistance.shout")) {
            if (SHOW_WHISPER_AND_SHOUT_LEVELS) senderInfo.add("shout (" + shoutLevel + ")");
            if (sender.getGameMode() == GameMode.SURVIVAL
                    || sender.getGameMode() == GameMode.ADVENTURE) {

                int hungerLoss = SHOUT_HUNGER_LOSS * shoutLevel;
                int senderHunger = sender.getFoodLevel();

                if (hungerLoss > senderHunger) {
                    sender.sendMessage(SHOUT_TOO_LOW_HUNGER_MESSAGE);
                    event.setCancelled(true);
                    return;
                }
                sender.setFoodLevel(sender.getFoodLevel() - (SHOUT_HUNGER_LOSS * shoutLevel));
            }
            chatRange += (SHOUT_RANGE_INCREASE * shoutLevel);
            if (SHOUT_BOLD) message = makeMessageTypography(message, ChatColor.BOLD);
        }




        /*
         * Turn the regular chat message into a whisper message.
         *
         * The whispering level is indicated by the amount of
         * parenthesis nests around the message or/and the
         * amount of own defined whisper symbols in the start
         * of the message. The value can not exceed the max
         * whispering level.
         *
         * After a permission check and if the option to show
         * whisper and shout levels to the console is enabled
         * then the whisper level will be shown to the console.
         *
         * The chat range is decreased by the respective
         * amount according to the whisper range decrease
         * multiplied with the whisper level.
         *
         * If the option to show whispering in italic text
         * is enabled then the chat message will appear
         * in italics and the whispering symbols will
         * be removed from the message.
         *
         * Cancel the message if the message contains nothing
         * but the whispering symbols.
         */
        final int whisperLevel = Math.min(countWhisperSymbols(message,WHISPER_SYMBOL, USE_WHISPER_PARENTHESISES, USE_WHISPER_SYMBOL), MAX_WHISPER_LEVEL);
        if (whisperLevel > 0 && sender.hasPermission("chatdistance.whisper")) {
            if (SHOW_WHISPER_AND_SHOUT_LEVELS) senderInfo.add("whisper (" + whisperLevel + ")");
            chatRange -= WHISPER_CHAT_RANGE_DECREASE * whisperLevel;
            if (chatRange < 1) chatRange = 1;
            if (USE_WHISPER_ITALICS) {
                message = makeMessageTypography(stripMessage(message, WHISPER_SYMBOL, whisperLevel, USE_WHISPER_PARENTHESISES, USE_WHISPER_SYMBOL), ChatColor.ITALIC);
                if (message.length() <= 2) {
                    event.setCancelled(true);
                    return;
                }
            }
        }


        // Check if the message starts with the chat format symbol and the supposedly first letter
        // in the message is space and the first letter is space... then remove the space
        if (message.startsWith("§") && message.length() > 2 && message.charAt(2) == ' ') message = message.replaceFirst(" ", "");


        // Check if the "show sender's chat range" option is true... then add the chat range to the sender information
        if (SHOW_SENDER_CHAT_RANGE) senderInfo.add("chat range: " + formatNumber(chatRange));


        // The message that is sent (example... Axe_Villager: Hello!)
        final String sentMessage = String.format(CHAT_FORMAT, sender.getDisplayName(), message);


        // Give information to the console who sends the message and what the message is (if enabled)
        Bukkit.getConsoleSender().sendMessage(senderInfo.size() > 0 ? "(" + createTextList(senderInfo) + ") " + makeMessageTypography(sentMessage, ChatColor.RESET)
                : makeMessageTypography(sentMessage, ChatColor.RESET));


        // The array list that will contain information of the received message
        final ArrayList<String> receiverInfo = new ArrayList<>();

        // For every recipient in the chat event
        for (final Player recipient : event.getRecipients()) {

            // Check if the sender is the recipient
            if (sender.equals(recipient)) {

                // Deliver the message to the sender in its pure form
                specificMessage(recipient, sender, message, receiverInfo);

            }

            // Otherwise... check if the sender and recipient are in the same world
            else if (sender.getWorld().equals(recipient.getWorld())) {

                // The distance between the sender and the recipient
                final double recipientDistance = sender.getLocation().distance(recipient.getLocation());

                // Check if the "show receiver's distance to the sender" option is true
                if (SHOW_RECEIVER_DISTANCE) {

                    // Add the distance to the receiver information
                    receiverInfo.add("distance: " + formatNumber(recipientDistance));

                }

                // Check if the recipient is inside the chat range
                if (recipientDistance <= chatRange) {

                    // The noise range
                    final double noiseRange = chatRange / OBSCURE_CHAT_RANGE_DIVISOR;

                    // Deliver the potentially obscure message to the recipient
                    specificMessage(recipient, sender, obscureMessage(message, chatRange, noiseRange, recipientDistance), receiverInfo);

                }

            }

            // Clear the receiver information
            receiverInfo.clear();

        }

        // Cancel the normal chat event
        event.setCancelled(true);

    }


    /**
     * Create an obscure message based on the recipients' distance to the sender
     */
    private String obscureMessage(final String message, final double chatRange,
                                  final double noiseRange, final double playerDistance) {

        // The player distance inside the noise range
        final double noiseDistance = playerDistance - (chatRange - noiseRange);

        // Amplified percentage to make the message more readable when obscure
        final double percentage = noiseDistance / (noiseRange * OBSCURE_CHAT_PERCENTAGE_AMPLIFIER);

        // Build the eventual message result
        final StringBuilder result = new StringBuilder();

        // Check if the player's distance is smaller or equal to the max chat range
        if (playerDistance <= chatRange) {

            // The position of the character to be ignored from being obscure
            int ignore = 0;

            // The message as a character array
            char[] ma = message.toCharArray();

            // For every character in the message
            for (int i = 0; i < ma.length; i++) {

                // Random number between 0 and 1
                final double rnd = Math.random();

                char c = ma[i];

                // Check if the character is the chat format symbol '§'... then set the character
                // position to the ignore value so that chat formatting is never removed
                if (c == '§') ignore = i + 1;

                // Check if the random number is smaller or equal to the percentage and
                // the character is not the chat format symbol '§' so that chat formatting is never removed and
                // the character is not at the position of the character to be ignored
                if (rnd <= percentage && c != '§' && c != ma[ignore]) {

                    // Set the character to space
                    c = ' ';
                }

                // Build the message result by adding every character to the message
                result.append(c);

            }

        }

        // Return the message result
        return new String (result);
    }


    /**
     * Globally deliver a message from a sender
     */
    private void globalMessage(final Player sender, String message) {

        // Doesn't work properly (as command) with '/' as prefix, because it doesn't go through a chat event

        // Check if the message starts with a space
        if (message.startsWith(" ")) {

            // Remove the space
            message = message.substring(1);

        }

        // Format the message according to the global chat format
        message = String.format(GLOBAL_CHAT_FORMAT, sender.getDisplayName(), message);

        // For every player on the server
        for (final Player player : Bukkit.getOnlinePlayers()) {

            // Send the message to the player
            player.sendMessage(message);

        }

        // Show the global message to the console
        Bukkit.getConsoleSender().sendMessage("(global) " + makeMessageTypography(message, ChatColor.RESET));

    }


    /**
     * Deliver a message to a specified recipient from a specific sender
     */
    private void specificMessage(final Player recipient, final Player sender, final String message, final ArrayList<String> info) {

        // Create a new chat event
        final AsyncPlayerChatEvent event = createNewAsyncPlayerChatEvent(recipient, sender, message, info);

        // Call the chat event
        chatEvent(event);

    }


    /**
     * Call the chat event
     */
    private void chatEvent(final AsyncPlayerChatEvent event) {

        // Check if the event is cancelled
        if (event.isCancelled()) {
            Bukkit.getLogger().info("Ignoring chat event! - Cancelled by another plugin.");
            return;
        }

        // Format the new message
        final String message = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());

        // Send the message to every recipient
        for (final Player recipient : event.getRecipients()) recipient.sendMessage(message);

    }


    /**
     * Create a new async player chat event
     */
    private AsyncPlayerChatEvent createNewAsyncPlayerChatEvent(final Player recipient, final Player sender, final String message, final ArrayList<String> info) {

        // The received message
        final String receivedMessage = String.format(CHAT_FORMAT, sender.getDisplayName(), message);

        // Check if the "message received" option is true and the sender is not the recipient
        if (SHOW_MESSAGE_RECEIVED && sender != recipient) {

            // Notify the console with information of who is the receiver and such (if enabled)
            Bukkit.getConsoleSender().sendMessage(info.size() > 0 ? "- (" + createTextList(info) + ") " + recipient.getName() + " received; " + makeMessageTypography(receivedMessage, ChatColor.RESET)
                    : "- " + recipient.getName() + " received; " + makeMessageTypography(receivedMessage, ChatColor.RESET));

        }

        Set<Player> receiverSet = new HashSet<>();

        //receiverSet.add(recipient);

        // The new chat event (async, who, message, receivers)
        final AsyncPlayerChatEvent newChatEvent = new AsyncPlayerChatEvent(true, sender, message, receiverSet);

        // Remove every recipient from the chat event
        newChatEvent.getRecipients().clear();

        // Add the specific recipient so the message can be based on his or her distance to the sender
        newChatEvent.getRecipients().add(recipient);

        // Set the format of the chat event to the chat format in the config
        newChatEvent.setFormat(CHAT_FORMAT);

        // Return the new async player chat event
        return newChatEvent;
    }


    /**
     * Before the eventual command is executed (processed)
     */
    @EventHandler
    private void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {

        // Check if the global prefix is nothing
        if (GLOBAL_MESSAGE_PREFIX == null) {

            // Stop
            return;

        }

        // Check if the message starts with the global prefix
        if (event.getMessage().startsWith(GLOBAL_MESSAGE_PREFIX)) {

            // Globally deliver the message without the global message prefix
            globalMessage(event.getPlayer(), event.getMessage().substring(GLOBAL_MESSAGE_PREFIX.length()));

            // Cancel the command preprocess event
            event.setCancelled(true);

        }

    }
}