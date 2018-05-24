package com.axevillager.chatdistance.player;

import com.axevillager.chatdistance.commands.Permissions;
import com.axevillager.chatdistance.configuration.Configuration;
import com.axevillager.chatdistance.message.ExpressiveMessage;
import com.axevillager.chatdistance.message.NormalMessage;
import com.axevillager.chatdistance.message.ObscureMessage;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.axevillager.chatdistance.other.Utilities.*;

/**
 * CustomPlayer created by AxeVillager on 2018/04/28.
 */

public class CustomPlayer {

    private final static ArrayList<CustomPlayer> CUSTOM_PLAYERS = new ArrayList<>();
    private UUID uniqueId;
    private Player player;
    private boolean inGlobalMessagesChannel;
    private boolean inOutOfContextChannel;
    private boolean inBroadcastChannel;
    private boolean inStaffChannel;

    public CustomPlayer(final UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.player = Bukkit.getPlayer(uniqueId);
        CUSTOM_PLAYERS.add(this);
    }

    public static CustomPlayer getCustomPlayer(final UUID uniqueId) {
        for (final CustomPlayer customPlayer : getCustomPlayers())
            if (customPlayer.getUniqueId().equals(uniqueId))
                return customPlayer;
        return null;
    }

    public static List<CustomPlayer> getCustomPlayers() {
        return ImmutableList.copyOf(CUSTOM_PLAYERS);
    }

    public void remove() {
        CUSTOM_PLAYERS.remove(this);
    }

    private UUID getUniqueId() {
        return uniqueId;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean hasPermission(final Permissions permission) {
        return player.hasPermission(permission.getPermission());
    }

    public boolean hasEnergyToShout(final int shoutLevel) {
        final int hungerLoss = Configuration.SHOUT_HUNGER_LOSS * shoutLevel;
        return hungerLoss < player.getFoodLevel();
    }

    public boolean canLoseHunger() {
        return player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE;
    }

    public void setInBroadcastChannel(final boolean value) {
        inBroadcastChannel = value;
    }

    public boolean isInBroadcastChannel() {
        return inBroadcastChannel;
    }

    public void setInGlobalMessagesChannel(final boolean value) {
        inGlobalMessagesChannel = value;
    }

    public boolean isInGlobalMessagesChannel() {
        return inGlobalMessagesChannel;
    }

    public void setInOutOfContextMessagesChannel(final boolean value) {
        inOutOfContextChannel = value;
    }

    public boolean isInOutOfContextMessagesChannel() {
        return inOutOfContextChannel;
    }

    public void setInStaffChannel(final boolean value) {
        inStaffChannel = value;
    }

    public boolean isInStaffChannel() {
        return inStaffChannel;
    }

    public void sendStaffMessage(final String message) {
        final NormalMessage staffMessage = new NormalMessage(message, Configuration.STAFF_CHAT_FORMAT, this, getStaff());
        staffMessage.send();
    }

    public void sendBroadcastMessage(final String message) {
        final NormalMessage broadcastMessage = new NormalMessage(message, Configuration.BROADCAST_CHAT_FORMAT, this, getCustomPlayers());
        broadcastMessage.send();
    }

    public void sendGlobalMessage(final String message) {
        final NormalMessage globalMessage = new NormalMessage(message, Configuration.GLOBAL_CHAT_FORMAT, this, getCustomPlayers());
        globalMessage.send();
    }

    public void sendOutOfContextMessage(final ExpressiveMessage expressiveMessage) {
        sendObscureMessage(expressiveMessage, Configuration.OUT_OF_CONTEXT_CHAT_FORMAT);
    }

    public void sendObscureMessage(final ExpressiveMessage expressiveMessage, final String format) {
        final String messageForSender = formatMessage(format, player, expressiveMessage.getExpressiveMessage());
        player.sendMessage(messageForSender);
        sendSenderInformationToConsole(expressiveMessage, messageForSender);

        for (final CustomPlayer customPlayer : getCustomPlayers()) {
            if (customPlayer.equals(this)) {
                continue;
            }
            final double receiverDistance = player.getLocation().distance(customPlayer.getPlayer().getLocation());
            if (receiverDistance <= expressiveMessage.getChatRange()) {
                final ObscureMessage obscureMessage = new ObscureMessage(expressiveMessage.getExpressiveMessage(), this, customPlayer, expressiveMessage.getChatRange(), receiverDistance);
                final String messageForReceiver = formatMessage(format, player, obscureMessage.getObscureMessage());
                customPlayer.getPlayer().sendMessage(messageForReceiver);

                if (Configuration.SHOW_MESSAGE_RECEIVED_IN_CONSOLE) {
                    sendReceiverInformationToConsole(obscureMessage);
                }
            }
        }
    }

    private void sendSenderInformationToConsole(final ExpressiveMessage expressiveMessage, final String messageForSender) {
        final String cleanMessage = makeMessageTypography(messageForSender, ChatColor.RESET);
        final int whisperLevel = expressiveMessage.getWhisperLevel();
        final int shoutLevel = expressiveMessage.getShoutLevel();
        final List<String> listOfInformation = new ArrayList<>();
        final String information;
        if (Configuration.SHOW_WHISPER_AND_SHOUT_LEVELS_IN_CONSOLE) {
            if (whisperLevel > 0 && hasPermission(Permissions.WHISPER)) {
                listOfInformation.add("whisper (" + whisperLevel + ")");
            }
            if (shoutLevel > 0 && hasPermission(Permissions.SHOUT)) {
                listOfInformation.add("shout (" + shoutLevel + ")");
            }
        }
        if (Configuration.SHOW_SENDER_CHAT_RANGE_IN_CONSOLE) {
            listOfInformation.add("chat range: " + formatNumber(expressiveMessage.getChatRange()));
        }
        if (!listOfInformation.isEmpty()) {
            information = "(" + createTextList(listOfInformation) + ") " + cleanMessage;
        } else {
            information = cleanMessage;
        }
        Bukkit.getConsoleSender().sendMessage(information);
    }

    private void sendReceiverInformationToConsole(final ObscureMessage obscureMessage) {
        final String cleanMessage = makeMessageTypography(obscureMessage.getObscureMessage(), ChatColor.RESET);
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("- ");
        if (Configuration.SHOW_RECEIVERS_DISTANCE_TO_SENDER_IN_CONSOLE) {
            stringBuilder.append("(distance: ").append(formatNumber(obscureMessage.getDistance())).append(") ");
        }
        stringBuilder.append(obscureMessage.getReceiver().getPlayer().getName()).append(" received: ").append(cleanMessage);
        Bukkit.getConsoleSender().sendMessage(stringBuilder.toString());
    }
}
