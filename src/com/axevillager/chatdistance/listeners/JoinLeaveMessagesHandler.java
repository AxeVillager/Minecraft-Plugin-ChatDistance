package com.axevillager.chatdistance.listeners;

import com.axevillager.chatdistance.player.CustomPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

import static com.axevillager.chatdistance.configuration.Configuration.*;
import static com.axevillager.chatdistance.other.Utilities.*;
import static com.axevillager.chatdistance.player.CustomPlayer.getCustomPlayer;

/**
 * JoinLeaveMessagesHandler created by AxeVillager on 2018/05/09.
 */

public class JoinLeaveMessagesHandler implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (getCustomPlayer(player.getUniqueId()) == null) {
            new CustomPlayer(player.getUniqueId());
        }

        final String message = JOIN_MESSAGE.replace("name", player.getName()).replace("&", "§");

        if (LOCAL_JOIN_AND_LEAVE_MESSAGES) {
            event.setJoinMessage("");
            sendJoinOrLeaveMessage(player, message, "join");
            return;
        }

        event.setJoinMessage(message);
    }

    @EventHandler
    public void onPlayerQuitEvent(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        final CustomPlayer customPlayer = getCustomPlayer(player.getUniqueId());
        if (customPlayer != null) {
            customPlayer.remove();
        }

        final String message = LEAVE_MESSAGE.replace("name", player.getName()).replace("&", "§");

        if (LOCAL_JOIN_AND_LEAVE_MESSAGES) {
            event.setQuitMessage("");
            sendJoinOrLeaveMessage(player, message, "leave");
            return;
        }

        event.setQuitMessage(message);
    }

    private void sendJoinOrLeaveMessage(final Player player, String message, final String type) {
        final ArrayList<String> recipientsList = new ArrayList<>();
        for (final Player recipient : Bukkit.getOnlinePlayers()) {
            final double recipientDistance = recipient.getLocation().distance(player.getLocation());
            if (recipient != player) {
                if (recipient.getLocation().getWorld() == player.getLocation().getWorld() && recipientDistance <= JOIN_AND_LEAVE_MESSAGES_RANGE) {

                    recipientsList.add((Boolean) SHOW_RECEIVED_JOIN_AND_LEAVE_MESSAGE_DISTANCE_IN_CONSOLE
                            ? recipient.getName() + " (distance: " + formatNumber(recipientDistance) + ")"
                            : recipient.getName() + " ");

                    recipient.sendMessage(message);
                }
            }
        }

        Bukkit.getConsoleSender().sendMessage(makeMessageTypography(message, ChatColor.RESET));

        if (SHOW_JOIN_AND_LEAVE_MESSAGE_RECEIVERS_IN_CONSOLE) {
            Bukkit.getConsoleSender().sendMessage("The players that received the " + type + " message (" + recipientsList.size() + "): " + createTextList(recipientsList));
        }
    }
}
