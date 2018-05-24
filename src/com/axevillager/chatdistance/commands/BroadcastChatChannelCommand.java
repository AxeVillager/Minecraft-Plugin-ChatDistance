package com.axevillager.chatdistance.commands;

import com.axevillager.chatdistance.player.CustomPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import static com.axevillager.chatdistance.commands.Permissions.BROADCAST_MESSAGE;

/**
 * BroadcastChatChannelCommand created by AxeVillager on 2018/05/10.
 */

public class BroadcastChatChannelCommand extends AbstractCommand {

    public BroadcastChatChannelCommand() {
        super(BROADCAST_MESSAGE.getCommandName(), BROADCAST_MESSAGE.getPermission(), false, true);
    }

    @Override
    public void execute(CommandSender sender, CustomPlayer customPlayer, String[] args) {

        if (args.length == 0) {
            if (customPlayer.isInBroadcastChannel()) {
                customPlayer.setInBroadcastChannel(false);
                sender.sendMessage(ChatColor.RED + "Broadcast chat messages has been disabled.");
            } else {
                customPlayer.setInBroadcastChannel(true);
                sender.sendMessage(ChatColor.DARK_GREEN + "Broadcast chat messages has been enabled.");
            }
            return;
        }

        final StringBuilder stringBuilder = new StringBuilder();
        for (final String word : args) {
            stringBuilder.append(word).append(" ");
        }
        final String message = new String(stringBuilder).trim();
        customPlayer.sendBroadcastMessage(message);
    }
}