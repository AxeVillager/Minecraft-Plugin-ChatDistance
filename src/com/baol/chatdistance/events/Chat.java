package com.baol.chatdistance.events;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

import static com.baol.chatdistance.other.Other.*;

/**
 * Chat created by Børre A. Opedal Lunde on 2017/03/10
 */

@SuppressWarnings("deprecation")
public class Chat implements Listener {

    private final String CHAT_FORMAT;
    private final double OBSCURE_CHAT_RANGE_DIVISOR;
    private final double PERCENTAGE_AMPLIFIER;
    private final boolean WHISPER_PARENTHESISES;
    private final boolean WHISPER_TILDE;
    private final boolean WHISPER_ITALICS;
    private final double WHISPER_CHAT_RANGE_DECREASE;
    private final int MAX_WHISPER_LEVEL;
    private final double TALK_CHAT_RANGE;
    private final boolean SHOUT_BOLD;
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

        // The chat format from the config
        CHAT_FORMAT = plugin.getConfig().getString("chat format", "&fname&7: message").replace("name", "%1$s").replace("message", "%2$s").replace("&", "§");

        // The obscure chat range divisor value from the config
        OBSCURE_CHAT_RANGE_DIVISOR = plugin.getConfig().getDouble("obscure chat range divisor", 2.5);

        // The percentage amplifier value from the config
        PERCENTAGE_AMPLIFIER = plugin.getConfig().getDouble("percentage amplifier", 1.75);

        // The "use parenthesises" to whisper value in the config
        WHISPER_PARENTHESISES = plugin.getConfig().getBoolean("whisper with parenthesises", true);

        // The "use tilde" to whisper value in the config
        WHISPER_TILDE = plugin.getConfig().getBoolean("whisper with tilde", true);

        // The italics whisper value from the config
        WHISPER_ITALICS = plugin.getConfig().getBoolean("whisper italic", true);

        // The whisper chat range decrease from the config
        WHISPER_CHAT_RANGE_DECREASE = plugin.getConfig().getDouble("whisper chat range decrease", 30);

        // The max whisper level from the config
        MAX_WHISPER_LEVEL = plugin.getConfig().getInt("whisper max level", 2);

        // The talk chat range from the config
        TALK_CHAT_RANGE = plugin.getConfig().getDouble("chat range", 50);

        // The bold shout value from the config
        SHOUT_BOLD = plugin.getConfig().getBoolean("shout bold", true);

        // The shout hunger loss from the config
        SHOUT_HUNGER_LOSS = plugin.getConfig().getInt("shout hunger loss", 2);

        // The error message when you can't shout because of too low food level from the config
        SHOUT_TOO_LOW_HUNGER_MESSAGE = plugin.getConfig().getString("too low food level to shout", "&cYour food level is too low to shout!").replace("&", "§");

        // The shout chat range increase from the config
        SHOUT_RANGE_INCREASE = plugin.getConfig().getDouble("shout chat range increase", 20);

        // The max shout level from the config
        MAX_SHOUT_LEVEL = plugin.getConfig().getInt("shout max level", 4);

        // The global message prefix from the config
        GLOBAL_MESSAGE_PREFIX = plugin.getConfig().getString("global prefix", "global:");

        // The global chat format from the config
        GLOBAL_CHAT_FORMAT = plugin.getConfig().getString("global chat format", "&fname &e(global)&7: message").replace("name", "%1$s").replace("message", "%2$s").replace("&", "§");

        // Show the sender's chat range value from the config
        SENDER_CHAT_RANGE = plugin.getConfig().getBoolean("sender chat range", true);

        // Show the sender's whisper or shout levels from the config
        WHISPER_AND_SHOUT_LEVELS = plugin.getConfig().getBoolean("whisper and shout levels", true);

        // Show the messages received value from the config
        MESSAGE_RECEIVED = plugin.getConfig().getBoolean("message received", false);

        // Show the receiver's distance to the sender value from the config
        RECEIVER_DISTANCE = plugin.getConfig().getBoolean("receiver distance", false);
    }


    /**
     * When any player sends a message in the chat
     */
    @EventHandler
    private void onPlayerChat(final PlayerChatEvent event) {

        // The sender
        final Player sender = event.getPlayer();

        // The message
        String message = event.getMessage();

        // The message in an array of characters
        char[] messageArray = message.toCharArray();

        // Check if the sender has permission to use chat formatting codes
        if (sender.hasPermission("chatdistance.formatting")) {

            // Make the message formatted correctly where & is only replaced with § when there is a formatting code
            message = makeFormatted(message);

        }


        // The amount of chat format symbols (§) in the message
        double countSymbol = 0;

        // For every letter in the message
        for (char letter : messageArray) {


            // Check if the letter is the chat format symbol
            if (letter == '§') {

                // Add one to the counter
                countSymbol++;

            }

        }

        // Check if the format symbols are more or equal to half the message
        if (countSymbol >= (double) message.length()/2) {

            // Cancel the message
            event.setCancelled(true);

            // Stop
            return;

        }


        // Sender information
        final ArrayList<String> senderInfo = new ArrayList<>();


        // Check if the sender has permission to send a global message
        if (sender.hasPermission("chatdistance.global")) {

            // Check if the message starts with the global prefix
            if (message.startsWith(GLOBAL_MESSAGE_PREFIX)) {

                // Globally deliver the message
                globalMessage(sender, message.substring(GLOBAL_MESSAGE_PREFIX.length()));

                // Cancel the normal chat event
                event.setCancelled(true);

                // Stop
                return;
            }

        }


        // The talk range
        double chatRange = TALK_CHAT_RANGE;

        // The amount of exclamation marks the message ends with
        int exclamationMarks = countExclamationMarks(message);

        // Set the exclamation marks value equal to the max value if exceeded
        exclamationMarks = Math.min(exclamationMarks, MAX_SHOUT_LEVEL);


        // Check if the amount of exclamation marks are greater than zero and the sender has permission to shout
        if (exclamationMarks > 0 && sender.hasPermission("chatdistance.shout")) {

            // Check if the "show whisper and shout levels" option is true
            if (WHISPER_AND_SHOUT_LEVELS) {

                // Add shout level to the information
                senderInfo.add("shout (" + exclamationMarks + ")");

            }

            // Check if the sender is in survival or adventure mode
            if (sender.getGameMode() == GameMode.SURVIVAL || sender.getGameMode() == GameMode.ADVENTURE) {

                // The hunger loss
                int hungerLoss = SHOUT_HUNGER_LOSS * exclamationMarks;

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
                sender.setFoodLevel(sender.getFoodLevel() - (SHOUT_HUNGER_LOSS * exclamationMarks));

            }


            // Increase the speaking range based on the power of the shout
            chatRange += (SHOUT_RANGE_INCREASE * exclamationMarks);

            // Check if the bold chat when shouting is enabled
            if (SHOUT_BOLD) {

                // Make the message bold on every letter
                message = makeCompletelyBold(message);

            }

        }


        // The amount of parenthesis nests around the message
        int parenthesisNests = countParenthesisNests(message);

        // Set the parenthesis nests value equal to the max value if exceeded
        parenthesisNests = Math.min(parenthesisNests, MAX_WHISPER_LEVEL);


        // The amount of whisper symbols in the beginning of the message
        int whisperSymbols = countWhisperSymbols(message);

        // Set the whisper symbols value equal to the max value if exceeded
        whisperSymbols = Math.min(whisperSymbols, MAX_WHISPER_LEVEL);

        // Check if the parenthesis nests are greater than zero and parenthesis is turned on OR whisper symbols are greater than zero and whisper symbols is turned on AND sender has permission to whisper
        if (((parenthesisNests > 0 && WHISPER_PARENTHESISES) || (whisperSymbols > 0
                && WHISPER_TILDE)) && sender.hasPermission("chatdistance.whisper")) {

            // The whisper level
            final int whisperLevel = parenthesisNests + whisperSymbols;

            // Check if the "show whisper and shout levels" option is true
            if (WHISPER_AND_SHOUT_LEVELS) {

                // Add whisper level to the information
                senderInfo.add("whisper (" + whisperLevel + ")");

            }

            // Decrease the chat range
            chatRange -= (WHISPER_CHAT_RANGE_DECREASE * whisperLevel);


            // Check if the chat range is smaller than one
            if (chatRange < 1) {

                // Set the chat range equal to one
                chatRange = 1;

            }

            // Check if italics when whispering is enabled
            if (WHISPER_ITALICS) {

                // Check if the amount of parenthesis nests are greater than 0 and the "whisper parenthesises" option is true
                if (parenthesisNests > 0 && WHISPER_PARENTHESISES) {
                    // Remove the parenthesises surrounding the message
                    message = message.substring(parenthesisNests, message.length() - parenthesisNests);

                }

                // Otherwise... check if the amount of whisper symbols (~) are greater than 0 and the "whisper tilde" option is true
                else if (whisperSymbols > 0 && WHISPER_TILDE) {

                    // Remove the whisper symbols in the beginning of the message
                    message = message.substring(whisperSymbols);

                }

                // Make the message italic on every letter
                message = makeCompletelyItalic(message);
            }

        }


        // Check if the supposedly first letter in the message is space
        if (message.length() > 2) {

            // Check if the message starts with the chat format symbol
            if (message.startsWith("§")) {

                // Check if the first letter (if the message starts with chat formatting) is space
                if (message.charAt(2) == ' ') {

                    // Remove the space
                    message = message.replaceFirst(" ", "");

                }

            }

        }


        // Check if the "show sender's chat range" option is true
        if (SENDER_CHAT_RANGE) {

            // Add the chat range to the information
            senderInfo.add("chat range: " + formatNumber(chatRange));

        }


        // The message that is sent (example... Axe_Villager: Hello!)
        final String sentMessage = String.format(CHAT_FORMAT, sender.getDisplayName(), message);

        // Check if there is any information to show
        if (senderInfo.size() > 0) {

            // Give information to the console who sends the message and what the message is
            Bukkit.getConsoleSender().sendMessage("(" + createTextList(senderInfo) + ") " + makeCompletelyReset(sentMessage));


        } else {

            // Give information to the console who sends the message and what the message is (no sender info)
            Bukkit.getConsoleSender().sendMessage(makeCompletelyReset(sentMessage));

        }


        // For every recipient in the chat event
        for (final Player recipient : event.getRecipients()) {

            // Array List for the receiver's info
            final ArrayList<String> receiverInfo = new ArrayList<>();

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
     * Counts the amount of exclamation marks in the end of a sentence
     */
    private int countExclamationMarks(String message) {

        // The amount of exclamation marks
        int exclamationMarks = 0;

        // While the message length is greater than one and the message ends with an exclamation mark
        while (message.length() > 1 && message.endsWith("!")) {

            // Remove one exclamation mark from the message
            message = message.substring(0, message.length() - 1);

            // Add one to the amount of exclamation marks
            exclamationMarks++;

        }

        // Return the amount of exclamation marks
        return exclamationMarks;
    }


    /**
     * Counts the amount of whisper symbols in the beginning of the message
     */
    private int countWhisperSymbols(String message) {

        // The amount of whisper symbols
        int whisperSymbols = 0;

        // While the message length is greater than one and the message starts with the whisper symbol
        while (message.length() > 1 && message.startsWith("~")) {

            // Remove the first letter in the message
            message = message.substring(1);

            // Add one to the whisper symbols
            whisperSymbols++;

        }

        // Return the amount of whisper symbols
        return whisperSymbols;
    }


    /**
     * Counts the amount of parenthesis nests around the message
     */
    private int countParenthesisNests(String message) {

        // The amount of parenthesis nests
        int parenthesisNests = 0;


        // While the message length is greater than two and the message starts and ends with a parenthesis
        while (message.length() > 2 && message.startsWith("(") && message.endsWith(")")) {

            // Remove one parenthesis nest from the message
            message = message.substring(1, message.length() - 1);

            // Add one to the amount of parenthesis nests
            parenthesisNests++;

        }

        // Return the amount of parenthesis nests
        return parenthesisNests;
    }


    /**
     * Create an obscure message based on the recipients' distance to the sender
     */
    private String obscureMessage(final String message, final double chatRange,
                                  final double noiseRange, final double playerDistance) {

        // The player distance inside the noise range
        final double noiseDistance = playerDistance - (chatRange - noiseRange);

        // Amplified percentage to make the message more readable when obscure
        final double percentage = (noiseDistance / noiseRange) / PERCENTAGE_AMPLIFIER;

        // Build the eventual message result
        final StringBuilder result = new StringBuilder();

        // Count position of the character
        int i = 0;

        // The position of the character to be ignored from being obscure
        int ignore = 0;

        // Check if the player's distance is smaller or equal to the max chat range
        if (playerDistance <= chatRange) {

            // The message as a character array
            char[] messageArray = message.toCharArray();

            // For every character in the message
            for (char character : messageArray) {

                // Add one to i for every letter
                i++;

                // Random number between 0 and 1
                final double rnd = Math.random();

                // Check if the character is the chat format symbol '§'
                if (character == '§') {

                    // The character position to ignore  so that chat formatting is never removed
                    ignore = i;

                }

                // Check if the random number is smaller or equal to the percentage
                if (rnd <= percentage) {

                    // Check if the character is not the chat format symbol '§' so that chat formatting is never removed
                    if (character != '§') {

                        // Check if the character is not at the position of the character to be ignored
                        if (character != messageArray[ignore]) {

                            // Set the character to space
                            character = ' ';

                        }

                    }

                }

                // Build the message result by adding every character to the message
                result.append(character);

            }

        }

        // Return the message result
        return new String (result);
    }


    /**
     * Globally deliver a message from a sender
     */
    private void globalMessage(final Player sender, String message) {

        // Doesn't work properly with /g for example, because it doesn't go through a chat event

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
        Bukkit.getConsoleSender().sendMessage("(global) " + makeCompletelyReset(message));

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
     * Call chat event
     */
    private void chatEvent(final PlayerChatEvent event) {

        // Check if the event is cancelled
        if (event.isCancelled()) {

            // Notify the console
            Bukkit.getLogger().info("Ignoring chat event! Cancelled by another plugin: " + event);

            // Stop
            return;

        }

        // Format the new message
        final String message = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());

        // Send the message to every recipient
        for (final Player recipient : event.getRecipients()) recipient.sendMessage(message);

    }


    /**
     * Create the new chat event
     */
    private PlayerChatEvent createNewChatEvent(final Player recipient, final Player sender, final String message, final ArrayList<String> info) {

        // The received message
        final String received = String.format(CHAT_FORMAT, sender.getDisplayName(), message);

        // Check if the "message received" option is true
        if (MESSAGE_RECEIVED) {

            // Check if the sender is not the recipient
            if (sender != recipient) {

                // Check if there is any information
                if (info.size() > 0) {

                    // Notify the console (when there is info)
                    Bukkit.getConsoleSender().sendMessage("- (" + createTextList(info) + ") " + recipient.getName() + " received; " + makeCompletelyReset(received));

                } else {

                    // Notify the console (when there is no info)
                    Bukkit.getConsoleSender().sendMessage("- " + recipient.getName() + " received; " + makeCompletelyReset(received));

                }

            }

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