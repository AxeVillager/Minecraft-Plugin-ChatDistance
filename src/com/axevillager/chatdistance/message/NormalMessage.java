package com.axevillager.chatdistance.message;

import com.axevillager.chatdistance.other.Utilities;
import com.axevillager.chatdistance.player.CustomPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.List;

/**
 * NormalMessage created by AxeVillager on 2018/05/21.
 */

public class NormalMessage {

    private final List<CustomPlayer> receivers;
    private final String fullMessage;

    public NormalMessage(final String message, final String format, final CustomPlayer from, final List<CustomPlayer> receivers) {
        this.receivers = receivers;
        fullMessage = Utilities.formatMessage(format, from.getPlayer(), message);
    }

    public void send() {
        sendToCustomPlayers();
        sendToConsole();
    }

    private void sendToCustomPlayers() {
        for (final CustomPlayer customPlayer : receivers) {
            customPlayer.getPlayer().sendMessage(fullMessage);
        }
    }

    private void sendToConsole() {
        Bukkit.getConsoleSender().sendMessage(Utilities.makeMessageTypography(fullMessage, ChatColor.RESET));
    }
}
