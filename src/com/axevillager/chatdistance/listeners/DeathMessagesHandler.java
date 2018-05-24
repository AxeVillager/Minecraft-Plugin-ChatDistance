package com.axevillager.chatdistance.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;

import static com.axevillager.chatdistance.configuration.Configuration.*;
import static com.axevillager.chatdistance.other.Utilities.*;

/**
 * DeathMessagesHandler created by AxeVillager on 2018/05/09.
 */

public class DeathMessagesHandler implements Listener {

    @EventHandler
    public void onPlayerDeathEvent(final PlayerDeathEvent event) {

        final Player victim = event.getEntity();
        final String deathMessage = "" + DEATH_MESSAGE_COLOUR + DEATH_MESSAGE_TYPOGRAPHY + event.getDeathMessage();

        if (!LOCAL_DEATH_MESSAGES) {
            event.setDeathMessage(deathMessage);
            return;
        }

        event.setDeathMessage("");

        final ArrayList<String> listOfRecipients = new ArrayList<>();
        for (final Player recipient : Bukkit.getOnlinePlayers()) {
            final double recipientDistance = recipient.getLocation().distance(victim.getLocation());
            if (canReceiveDeathMessage(recipient, victim, recipientDistance)) {
                if (recipient != victim) {
                    listOfRecipients.add((Boolean) SHOW_RECEIVED_DEATH_MESSAGE_DISTANCE_IN_CONSOLE ? recipient.getName() + " (distance: " + formatNumber(recipientDistance) + ")" : recipient.getName() + " ");
                }
                recipient.sendMessage(deathMessage);
            }
        }

        Bukkit.getConsoleSender().sendMessage(makeMessageTypography(deathMessage, ChatColor.RESET));
        if (SHOW_DEATH_MESSAGE_RECEIVERS_IN_CONSOLE) {
            Bukkit.getConsoleSender().sendMessage("The players that received the death message (" + listOfRecipients.size() + "): " + createTextList(listOfRecipients));
        }
    }

    private boolean canReceiveDeathMessage(final Player receiver, final Player victim, final double distance) {
        return receiver.getLocation().getWorld() == victim.getLocation().getWorld() && distance <= DEATH_MESSAGES_RANGE;
    }
}
