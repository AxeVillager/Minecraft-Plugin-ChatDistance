package com.baol.chatdistance.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

import static com.baol.chatdistance.other.Other.*;

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

        LOCAL_JOIN_AND_LEAVE_MESSAGES = plugin.getConfig().getBoolean("local join/leave messages", true);
        JOIN_AND_LEAVE_MESSAGES_RANGE = plugin.getConfig().getDouble("join/leave message range", 250);
        JOIN_MESSAGE = plugin.getConfig().getString("join message", "&ename joined the game");
        LEAVE_MESSAGE = plugin.getConfig().getString("leave message", "&ename left the game");
        LIST_RECEIVERS = plugin.getConfig().getBoolean("join/leave message receivers", true);
        RECEIVED_DISTANCE = plugin.getConfig().getBoolean("received join/leave message distance", true);
    }

    /**
     * When a player joins the server
     */
    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {

        // The player that joins the server
        final Player player = event.getPlayer();

        // The player's display name
        final String playerName = player.getDisplayName();

        // Set the original join message to nothing so that it appears to have been canceled
        event.setJoinMessage("");

        // The join message
        final String joinMessage = JOIN_MESSAGE.replace("name", playerName).replace("&", "§");

        // The list of names of players that received the join message
        final ArrayList<String> recipientsList = new ArrayList<>();

        // For every player on the server
        for (final Player recipient : Bukkit.getOnlinePlayers()) {

            // The recipient's distance to the player that logged in
            final double recipientDistance = recipient.getLocation().distance(player.getLocation());

            // Check if the recipient is not the player that joins the server
            if (recipient != player) {

                // Check if the local join and leave messages option is true
                if (LOCAL_JOIN_AND_LEAVE_MESSAGES) {

                    // Check if the recipient and the player that logged in are in the same world
                    if (recipient.getLocation().getWorld() == player.getLocation().getWorld()) {

                        // Check if the recipient's distance to the player that logged in is less or equal to the join and leave message range
                        if (recipientDistance <= JOIN_AND_LEAVE_MESSAGES_RANGE) {

                            // Check if the "received join/leave message distance" option is true
                            if (RECEIVED_DISTANCE) {

                                // Add the recipient's name to the recipients list (with distance)
                                recipientsList.add(recipient.getName() + " (distance: " + formatNumber(recipientDistance) + ") ");

                            } else {

                                // Add the recipient's name to the recipients list
                                recipientsList.add(recipient.getName() + " ");

                            }

                            // Send the join message to the recipient
                            recipient.sendMessage(joinMessage);

                        }

                    }

                }

                // Otherwise... the local join and leaves messages option is false
                else {

                    // Send the join message to the recipient
                    recipient.sendMessage(joinMessage);

                }

            }

        }

        // Notify the console who joined
        Bukkit.getConsoleSender().sendMessage(makeCompletelyReset(joinMessage));


        // Check if the local join and leave messages option is true
        if (LOCAL_JOIN_AND_LEAVE_MESSAGES) {

            // Check if the "join/leave message receivers" option is true
            if (LIST_RECEIVERS) {

                // Notify the console who received the join message
                Bukkit.getConsoleSender().sendMessage("Players that received the join message (" + recipientsList.size() + "): " + createTextList(recipientsList));

            }

        }

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

        // Set the original leave message to nothing so that it appears to have been canceled
        event.setQuitMessage("");

        // The leave message
        final String leaveMessage = LEAVE_MESSAGE.replace("name", playerName).replace("&", "§");

        // The list of names of players that received the leave message
        final ArrayList<String> recipientsList = new ArrayList<>();

        // For every player on the server
        for (final Player recipient : Bukkit.getOnlinePlayers()) {

            // The recipient's distance to the player that left
            final double recipientDistance = recipient.getLocation().distance(player.getLocation());

            // Check if the recipient is not the player that left the server
            if (recipient != player) {

                // Check if the local join and leave messages option is true
                if (LOCAL_JOIN_AND_LEAVE_MESSAGES) {

                    // Check if the recipient and the player that left were in the same world
                    if (recipient.getLocation().getWorld() == player.getLocation().getWorld()) {

                        // Check if the recipient's distance to the player that left were less or equal to the join and leave message range
                        if (recipientDistance <= JOIN_AND_LEAVE_MESSAGES_RANGE) {

                            // Check if the "received join/leave message distance" option is true
                            if (RECEIVED_DISTANCE) {

                                // Add the recipient's name to the recipients list (with distance)
                                recipientsList.add(recipient.getName() + " (distance: " + formatNumber(recipientDistance) + ") ");

                            } else {

                                // Add the recipient's name to the recipients list
                                recipientsList.add(recipient.getName() + " ");

                            }

                            // Send the leave message to the recipient
                            recipient.sendMessage(leaveMessage);

                        }

                    }

                }

                // Otherwise... the local join and leaves messages option is false
                else {

                    // Send the leave message to the recipient
                    recipient.sendMessage(leaveMessage);

                }

            }

        }

        // Notify the console who left
        Bukkit.getConsoleSender().sendMessage(makeCompletelyReset(leaveMessage));


        // Check if the local join and leave messages option is true
        if (LOCAL_JOIN_AND_LEAVE_MESSAGES) {

            // Check if the "join/leave message receivers" option is true
            if (LIST_RECEIVERS) {

                // Notify the console who received the join message
                Bukkit.getConsoleSender().sendMessage("Players that received the leave message (" + recipientsList.size() + "): " + createTextList(recipientsList));

            }
        }

    }

}