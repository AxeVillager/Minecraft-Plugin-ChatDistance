package com.baol.chatdistance.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

import static com.baol.chatdistance.other.Utilities.*;

/**
 * Chat created by AxeVillager on 2017/03/10
 */

@SuppressWarnings("deprecation")
public class Chat implements Listener {

    private final String CHAT_FORMAT;
    private final double OBSCURE_CHAT_RANGE_DIVISOR;
    private final double PERCENTAGE_AMPLIFIER;
    private final boolean USE_WHISPER_PARENTHESISES;
    private final String WHISPER_SYMBOL;
    private final boolean USE_WHISPER_SYMBOL;
    private final boolean WHISPER_ITALICS;
    private final double WHISPER_CHAT_RANGE_DECREASE;
    private final int MAX_WHISPER_LEVEL;
    private final double TALK_CHAT_RANGE;
    private final boolean SHOUT_BOLD;
    private final boolean SHOUT_ONLY_AT_END;
    private final int SHOUT_HUNGER_LOSS;
    private final String SHOUT_TOO_LOW_HUNGER_MESSAGE;
    private final double SHOUT_RANGE_INCREASE;
    private final int MAX_SHOUT_LEVEL;
    private final String GLOBAL_MESSAGE_PREFIX;
    private final String GLOBAL_CHAT_FORMAT;
    private final boolean SENDER_CHAT_RANGE;
    private final boolean WHISPER_AND_SHOUT_LEVELS;
    private final boolean MESSAGE_RECEIVED;
    private final boolean RECEIVER_DISTANCE;


    /**
     * Constructor
     */
    public Chat(final JavaPlugin plugin) {

        // The configuration file
        final FileConfiguration config = plugin.getConfig();

        // The chat format from the config
        CHAT_FORMAT = config.getString("chat format", "&fname&7: message").replace("name", "%1$s").replace("message", "%2$s").replace("&", "§");

        // The obscure chat range divisor value from the config
        OBSCURE_CHAT_RANGE_DIVISOR = config.getDouble("obscure chat range divisor", 2.5);

        // The percentage amplifier value from the config
        PERCENTAGE_AMPLIFIER = config.getDouble("percentage amplifier", 1.75);

        // The "use parenthesises" to whisper value in the config
        USE_WHISPER_PARENTHESISES = config.getBoolean("whisper with parenthesises", true);

        // The own defined whisper symbol, default '~'
        WHISPER_SYMBOL = config.getString("whisper symbol", "~");

        // The "use tilde" to whisper value in the config
        USE_WHISPER_SYMBOL = config.getBoolean("whisper with symbol", true);

        // The italics whisper value from the config
        WHISPER_ITALICS = config.getBoolean("whisper italic", true);

        // The whisper chat range decrease from the config
        WHISPER_CHAT_RANGE_DECREASE = config.getDouble("whisper chat range decrease", 30);

        // The max whisper level from the config
        MAX_WHISPER_LEVEL = config.getInt("whisper max level", 2);

        // The talk chat range from the config
        TALK_CHAT_RANGE = config.getDouble("chat range", 50);

        // The bold shout value from the config
        SHOUT_BOLD = config.getBoolean("shout bold", true);

        // The value that indicates whether exclamations in or at the end of the message counts.
        SHOUT_ONLY_AT_END = plugin.getConfig().getBoolean("exclamation marks at the end", false);

        // The shout hunger loss from the config
        SHOUT_HUNGER_LOSS = config.getInt("shout hunger loss", 2);

        // The error message when you can't shout because of too low food level from the config
        SHOUT_TOO_LOW_HUNGER_MESSAGE = config.getString("too low food level to shout", "&cYour food level is too low to shout!").replace("&", "§");

        // The shout chat range increase from the config
        SHOUT_RANGE_INCREASE = config.getDouble("shout chat range increase", 20);

        // The max shout level from the config
        MAX_SHOUT_LEVEL = config.getInt("shout max level", 4);

        // The global message prefix from the config
        GLOBAL_MESSAGE_PREFIX = config.getString("global prefix", "global:");

        // The global chat format from the config
        GLOBAL_CHAT_FORMAT = config.getString("global chat format", "&fname &e(global)&7: message").replace("name", "%1$s").replace("message", "%2$s").replace("&", "§");

        // Show the sender's chat range value from the config
        SENDER_CHAT_RANGE = config.getBoolean("sender chat range", true);

        // Show the sender's whisper or shout levels from the config
        WHISPER_AND_SHOUT_LEVELS = config.getBoolean("whisper and shout levels", true);

        // Show the messages received value from the config
        MESSAGE_RECEIVED = config.getBoolean("message received", false);

        // Show the receiver's distance to the sender value from the config
        RECEIVER_DISTANCE = config.getBoolean("receiver distance", false);
    }


    /**
     * When any player sends a message in the chat
     */
    @EventHandler
    private void onPlayerChat(final PlayerChatEvent event) {

        // Array List for the sender' information
        final ArrayList<String> senderInfo = new ArrayList<>();

        // Array List for the receiver's information
        final ArrayList<String> receiverInfo = new ArrayList<>();

        // The sender
        final Player sender = event.getPlayer();

        // The message
        String message = event.getMessage();

        // Check if the sender has permission to use chat formatting codes
        if (sender.hasPermission("chatdistance.formatting"))

            // Make the message formatted correctly where & is only replaced with § when there is a formatting code
            message = makeFormatted(message);


        // The amount of chat format symbols (§) in the message
        int countSymbol = 0;

        // For every letter in the message
        for (char letter : message.toCharArray()) {

            // Check if the letter is the chat format symbol -> Add one to the counter
            if (letter == '§') countSymbol++;

        }

        // Check if the format symbols are more or equal to half the message
        if (countSymbol << 1 >= message.length()) {

            // Cancel the message
            event.setCancelled(true);

            // Stop
            return;

        }


        // Check if the sender has permission to send a global message and the message starts with the global prefix
        if (sender.hasPermission("chatdistance.global") && message.startsWith(GLOBAL_MESSAGE_PREFIX)) {

            // Globally deliver the message
            globalMessage(sender, message.substring(GLOBAL_MESSAGE_PREFIX.length()));

            // Cancel the normal chat event
            event.setCancelled(true);

            // Stop
            return;

        }


        // The talk range
        double chatRange = TALK_CHAT_RANGE;

        // Set the exclamation marks value equal to the max value if exceeded
        final int shoutLevel = Math.min(SHOUT_ONLY_AT_END ? countCharEnd(message, '!') : countChar(message, '!'), MAX_SHOUT_LEVEL);


        // Check if the amount of exclamation marks are greater than zero and the sender has permission to shout
        if (shoutLevel > 0 && sender.hasPermission("chatdistance.shout")) {

            // Check if the "show whisper and shout levels" option is true -> add shout level to the sender's information
            if (WHISPER_AND_SHOUT_LEVELS) senderInfo.add("shout (" + shoutLevel + ")");

            // Check if the sender is in survival or adventure mode
            if (sender.getGameMode() == GameMode.SURVIVAL || sender.getGameMode() == GameMode.ADVENTURE) {

                // The hunger loss
                int hungerLoss = SHOUT_HUNGER_LOSS * shoutLevel;

                // The sender's food level
                int senderHunger = sender.getFoodLevel();

                // Check if the hunger loss is greater than the sender's food level
                if (hungerLoss > senderHunger) {

                    // Warn the player he doesn't have high enough food level to shout
                    sender.sendMessage(SHOUT_TOO_LOW_HUNGER_MESSAGE);

                    // Cancel the event
                    event.setCancelled(true);

                    // Stop
                    return;

                }

                // Set the hunger level of the player equal to the respective loss
                sender.setFoodLevel(sender.getFoodLevel() - (SHOUT_HUNGER_LOSS * shoutLevel));

            }


            // Increase the speaking range based on the power of the shout
            chatRange += (SHOUT_RANGE_INCREASE * shoutLevel);

            // Check if the bold chat when shouting is enabled
            if (SHOUT_BOLD)

                // Make the message bold on every letter
                message = makeMessageTypography(message, ChatColor.BOLD);

        }

        // Set the whisper symbols value equal to the max value if exceeded
        final int whisperLevel = Math.min(countWhisperSymbols(message, WHISPER_SYMBOL, USE_WHISPER_PARENTHESISES, USE_WHISPER_SYMBOL), MAX_WHISPER_LEVEL);


        // Check if the message is a whispering message and the sender has permission to whisper
        if (whisperLevel > 0 && sender.hasPermission("chatdistance.whisper")) {

            // Check if the "show whisper and shout levels" option is true -> Add whisper level to the information
            if (WHISPER_AND_SHOUT_LEVELS) senderInfo.add("whisper (" + whisperLevel + ")");

            // Decrease the chat range
            chatRange -= WHISPER_CHAT_RANGE_DECREASE * whisperLevel;

            // Check if the chat range is smaller than one -> Set the chat range equal to one
            if (chatRange < 1) chatRange = 1;

            // Check if "italics when whispering" is enabled
            if (WHISPER_ITALICS) {

                // Make the message italic on every letter and remove the whispering symbols
                message = makeMessageTypography(stripMessage(message, WHISPER_SYMBOL, whisperLevel, USE_WHISPER_PARENTHESISES, USE_WHISPER_SYMBOL), ChatColor.ITALIC);

                // Check if the length of the message is less or equal to two (nothing but chat format)
                if (message.length() <= 2) {

                    // Cancel the chat event
                    event.setCancelled(true);

                    // Stop
                    return;

                }
            }
        }


        // Check if the message starts with the chat format symbol and the supposedly first letter
        // in the message is space and the first letter is space... then remove the space
        if (message.startsWith("§") && message.length() > 2 && message.charAt(2) == ' ') message = message.replaceFirst(" ", "");


        // Check if the "show sender's chat range" option is true... then add the chat range to the sender information
        if (SENDER_CHAT_RANGE) senderInfo.add("chat range: " + formatNumber(chatRange));


        // The message that is sent (example... Axe_Villager: Hello!)
        final String sentMessage = String.format(CHAT_FORMAT, sender.getDisplayName(), message);


        // Give information to the console who sends the message and what the message is (if enabled)
        Bukkit.getConsoleSender().sendMessage(senderInfo.size() > 0 ? "(" + createTextList(senderInfo) + ") " + makeMessageTypography(sentMessage, ChatColor.RESET)
                : makeMessageTypography(sentMessage, ChatColor.RESET));



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
                if (RECEIVER_DISTANCE) {

                    // Add the distance to the receiver information
                    receiverInfo.add("distance: " + formatNumber(recipientDistance));

                }

                // Check if the distance is smaller or equal to the hearing range
                if (recipientDistance <= chatRange) {

                    // The noise range
                    final double noiseRange = chatRange / OBSCURE_CHAT_RANGE_DIVISOR;

                    // Deliver the potentially obscure message to the recipient
                    specificMessage(recipient, sender, obscureMessage(message, chatRange, noiseRange, recipientDistance), receiverInfo);

                }

            }

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
        final double percentage = noiseDistance / (noiseRange * PERCENTAGE_AMPLIFIER);

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
        final PlayerChatEvent event = createNewChatEvent(recipient, sender, message, info);

        // Call the chat event
        chatEvent(event);

    }


    /**
     * Call the chat event
     */
    private void chatEvent(final PlayerChatEvent event) {

        // Check if the event is cancelled
        if (event.isCancelled()) {

            // Notify the console
            Bukkit.getLogger().info("Ignoring chat event! - Cancelled by another plugin.");

            // Stop
            return;

        }

        // Format the new message
        final String message = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());

        // Send the message to every recipient
        for (final Player recipient : event.getRecipients()) recipient.sendMessage(message);

    }


    /**
     * Create a new chat event
     */
    private PlayerChatEvent createNewChatEvent(final Player recipient, final Player sender, final String message, final ArrayList<String> info) {

        // The received message
        final String received = String.format(CHAT_FORMAT, sender.getDisplayName(), message);

        // Check if the "message received" option is true and the sender is not the recipient
        if (MESSAGE_RECEIVED && sender != recipient) {

                // Notify the console with information of who is the receiver and such (if enabled)
                Bukkit.getConsoleSender().sendMessage(info.size() > 0 ? "- (" + createTextList(info) + ") " + recipient.getName() + " received; " + makeMessageTypography(received, ChatColor.RESET)
                        : "- " + recipient.getName() + " received; " + makeMessageTypography(received, ChatColor.RESET));

        }

        // The new chat event
        final PlayerChatEvent newChatEvent = new PlayerChatEvent(sender, message);

        // Remove every recipient from the chat event
        newChatEvent.getRecipients().clear();

        // Add the specific recipient so the message can be based on his or her distance to the sender
        newChatEvent.getRecipients().add(recipient);

        // Set the format of the chat event to the chat format in the config
        newChatEvent.setFormat(CHAT_FORMAT);

        // Return the new chat event
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