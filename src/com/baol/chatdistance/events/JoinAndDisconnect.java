package com.baol.chatdistance.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

import static com.baol.chatdistance.other.Utilities.*;

/**
 * JoinAndDisconnect created by Børre A. Opedal Lunde on 2017/03/12
 */

public class JoinAndDisconnect implements Listener {

    private final boolean LOCAL_JOIN_AND_LEAVE_MESSAGES;
    private final double JOIN_AND_LEAVE_MESSAGES_RANGE;
    private final String JOIN_MESSAGE;
    private final String LEAVE_MESSAGE;
    private final boolean LIST_RECEIVERS;
    private final boolean RECEIVED_DISTANCE;

    /**
     * Constructor for the JoinAndDisconnect class
     */
    public JoinAndDisconnect(final JavaPlugin plugin) {

        // The configuration file
        final FileConfiguration config = plugin.getConfig();

        LOCAL_JOIN_AND_LEAVE_MESSAGES = config.getBoolean("local join/leave messages", true);
        JOIN_AND_LEAVE_MESSAGES_RANGE = config.getDouble("join/leave message range", 250);
        JOIN_MESSAGE = config.getString("join message", "&ename joined the game");
        LEAVE_MESSAGE = config.getString("leave message", "&ename left the game");
        LIST_RECEIVERS = config.getBoolean("join/leave message receivers", true);
        RECEIVED_DISTANCE = config.getBoolean("received join/leave message distance", true);
    }

    /**
     * When a player joins the server
     */
    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {

        // The player that joined the server
        final Player player = event.getPlayer();

        // The player's display name
        final String playerName = player.getDisplayName();

        // Do the join message
        joinOrLeaveMessage(player, JOIN_MESSAGE.replace("name", playerName).replace("&", "§"), "join");

        // Set the join message to nothing so that it appears to have been cancelled
        event.setJoinMessage("");
    }

    /**
     * When a player leaves the server
     */
    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {

        // The player that left the server
        final Player player = event.getPlayer();

        // The player's display name
        final String playerName = player.getDisplayName();

        // Do the leave message
        joinOrLeaveMessage(player, LEAVE_MESSAGE.replace("name", playerName).replace("&", "§"), "leave");

        // Set the leave message to nothing so that it appears to have been cancelled
        event.setQuitMessage("");
    }


    /**
     * Broadcasts locally or globally that a player joined or left the server
     */
    private void joinOrLeaveMessage(final Player player, final String message, final String type) {

        // The list of names of players that received the join/leave message
        final ArrayList<String> recipientsList = new ArrayList<>();

        // For every player on the server
        for (final Player recipient : Bukkit.getOnlinePlayers()) {

            // The recipient's distance to the player that joined/left
            final double recipientDistance = recipient.getLocation().distance(player.getLocation());

            // Check if the recipient is not the player that joined/left the server
            // and the local join and leave messages option is true and the recipient
            // and the player that joined/left are/were in the same world and the recipient's distance
            // to the player that joined/left are/were less or equal to the join and leave message range
            if (recipient != player
                    && LOCAL_JOIN_AND_LEAVE_MESSAGES
                    && recipient.getLocation().getWorld() == player.getLocation().getWorld()
                    && recipientDistance <= JOIN_AND_LEAVE_MESSAGES_RANGE) {

                // Check if the "received join/leave message distance" option is true
                if (RECEIVED_DISTANCE) {

                    // Add the recipient's name to the recipients list (with distance)
                    recipientsList.add(recipient.getName() + " (distance: " + formatNumber(recipientDistance) + ")");

                } else

                    // Add the recipient's name to the recipients list
                    recipientsList.add(recipient.getName() + " ");


                // Send the join/leave message to the recipient
                recipient.sendMessage(message);

            }

            // Otherwise... the local join and leaves messages option is false
            else {

                // Send the join/leave message to the recipient
                recipient.sendMessage(message);

            }
        }

        // Notify the console who joined/left
        Bukkit.getConsoleSender().sendMessage(makeMessageTypography(message, ChatColor.RESET));

        // Check if the local join and leave messages option is true and the "join/leave message receivers" option is true
        if (LOCAL_JOIN_AND_LEAVE_MESSAGES && LIST_RECEIVERS) {

            // Notify the console who received the join/leave message
            Bukkit.getConsoleSender().sendMessage
                    ("The players that received the " + type + " message (" + recipientsList.size() + "): " + createTextList(recipientsList));

        }
    }
}