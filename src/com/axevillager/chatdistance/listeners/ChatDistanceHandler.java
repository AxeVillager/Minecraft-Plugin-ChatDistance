package com.axevillager.chatdistance.listeners;

import com.axevillager.chatdistance.message.ExpressiveMessage;
import com.axevillager.chatdistance.player.CustomPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static com.axevillager.chatdistance.commands.Permissions.FORMATTING;
import static com.axevillager.chatdistance.commands.Permissions.SHOUT;
import static com.axevillager.chatdistance.configuration.Configuration.*;
import static com.axevillager.chatdistance.other.Utilities.formatChatCodes;
import static com.axevillager.chatdistance.other.Utilities.messageIsEmpty;
import static com.axevillager.chatdistance.player.CustomPlayer.getCustomPlayer;

/**
 * ChatDistanceHandler created by AxeVillager on 2018/04/25.
 */

public class ChatDistanceHandler implements Listener {

    @EventHandler
    public void onAsyncPlayerChatEvent(final AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        final Player player = event.getPlayer();
        CustomPlayer customPlayer = getCustomPlayer(player.getUniqueId());

        if (event.isCancelled()) {
            return;
        }

        event.setCancelled(true);

        if (customPlayer == null) {
            customPlayer = new CustomPlayer(player.getUniqueId());
        }

        if (customPlayer.hasPermission(FORMATTING)) {
            message = formatChatCodes(message);
        }

        if (messageIsEmpty(message)) {
            return;
        }

        if (customPlayer.isInStaffChannel()) {
            customPlayer.sendStaffMessage(message);
            return;
        }

        if (customPlayer.isInBroadcastChannel()) {
            customPlayer.sendBroadcastMessage(message);
            return;
        }

        if (customPlayer.isInGlobalMessagesChannel()) {
            customPlayer.sendGlobalMessage(message);
            return;
        }

        final ExpressiveMessage expressiveMessage = new ExpressiveMessage(message, customPlayer);
        final int shoutLevel = expressiveMessage.getShoutLevel();
        final int hunger = player.getFoodLevel();
        if (shoutLevel > 0 && customPlayer.hasPermission(SHOUT)) {
            if (customPlayer.canLoseHunger()) {
                final int hungerLoss = SHOUT_HUNGER_LOSS * shoutLevel;
                if (!customPlayer.hasEnergyToShout(shoutLevel)) {
                    player.sendMessage(TOO_LOW_FOOD_LEVEL_TO_SHOUT_MESSAGE);
                    return;
                }
                player.setFoodLevel(hunger - hungerLoss);
            }
        }

        if (customPlayer.isInOutOfContextMessagesChannel()) {
            customPlayer.sendOutOfContextMessage(expressiveMessage);
            return;
        }

        customPlayer.sendObscureMessage(expressiveMessage, OBSCURE_CHAT_FORMAT);
    }
}
