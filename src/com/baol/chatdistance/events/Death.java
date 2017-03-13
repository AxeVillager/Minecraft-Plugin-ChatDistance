package com.baol.chatdistance.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

import static com.baol.chatdistance.other.Other.*;

/**
 * Death created by Børre A. Opedal Lunde on 2017/03/12
 */

public class Death implements Listener {

    private final boolean LOCAL_DEATH_MESSAGES;
    private final double DEATH_MESSAGES_RANGE;
    private final ChatColor DEATH_MESSAGE_COLOUR;
    private final ChatColor DEATH_MESSAGE_TYPOGRAPHY;
    private final boolean LIST_RECEIVERS;
    private final boolean RECEIVED_DISTANCE;

    /**
     * Constructor for the Death class
     */
    public Death(final JavaPlugin plugin) {

        LOCAL_DEATH_MESSAGES = plugin.getConfig().getBoolean("local death messages", true);
        DEATH_MESSAGES_RANGE = plugin.getConfig().getDouble("death messages range", 250);
        DEATH_MESSAGE_COLOUR = getChatColor(plugin.getConfig().getString("death messages colour", "WHITE"), ChatColor.WHITE);
        DEATH_MESSAGE_TYPOGRAPHY = getChatColor(plugin.getConfig().getString("death messages typography", "RESET"), ChatColor.RESET);
        LIST_RECEIVERS = plugin.getConfig().getBoolean("death message receivers", true);
        RECEIVED_DISTANCE = plugin.getConfig().getBoolean("received death message distance", true);
    }


    /**
     * When a player dies
     */
    @EventHandler
    public void onDeath(final PlayerDeathEvent event) {

        // The player that dies
        final Player player = event.getEntity();

        // The new death message
        final String deathMessage = "" + DEATH_MESSAGE_COLOUR + DEATH_MESSAGE_TYPOGRAPHY + event.getDeathMessage();

        // Set the original death message to nothing so that it appears to have been canceled
        event.setDeathMessage("");

        // The list of names of players that received the death message
        final ArrayList<String> recipientsList = new ArrayList<>();

        // For every player on the server
        for (final Player recipient : Bukkit.getOnlinePlayers()) {

            // The recipient's distance to the player that dies
            final double recipientDistance = recipient.getLocation().distance(player.getLocation());

            // Check if local death messages are turned on
            if (LOCAL_DEATH_MESSAGES) {

                // Check if the recipient and the player that dies are in the same world
                if (recipient.getLocation().getWorld() == player.getLocation().getWorld()) {

                    // Check if the recipient's distance to the sender is less or equal to the death messages range
                    if (recipientDistance <= DEATH_MESSAGES_RANGE) {

                        // Check if the recipient is not the player that dies
                        if (recipient != player) {

                            // Check if the "death message receivers distance" option is true
                            if (RECEIVED_DISTANCE) {

                                // Add the recipient to the recipients list (with distance)
                                recipientsList.add(recipient.getName() + " (distance: " + formatNumber(recipientDistance) + ") ");

                            } else {

                                // Add the recipient to the recipients list
                                recipientsList.add(recipient.getName() + " ");

                            }

                        }

                        // Send the death message to the recipient
                        recipient.sendMessage(deathMessage);

                    }

                }

            }

            // Otherwise... local death messages are turned off
            else {

                // Send the death message to the recipient
                recipient.sendMessage(deathMessage);

            }

        }

        // Notify the console who died and how
        Bukkit.getConsoleSender().sendMessage(makeCompletelyReset(deathMessage));


        // Check if the local death messages option is true
        if (LOCAL_DEATH_MESSAGES) {

            // Check if the "list death message receivers" option is true
            if (LIST_RECEIVERS) {

                // Notify the console who received the death message
                Bukkit.getConsoleSender().sendMessage("Players that received the death message (" + recipientsList.size() + "): " + createTextList(recipientsList));

            }

        }

    }

}