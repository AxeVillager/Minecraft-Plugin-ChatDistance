package com.axevillager.chatdistance.commands;

import com.axevillager.chatdistance.player.CustomPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * GlobalChatChannelCommand created by AxeVillager on 2018/05/09.
 */

public class GlobalChatChannelCommand extends AbstractCommand {

    public GlobalChatChannelCommand() {
        super(Permissions.GLOBAL_MESSAGE.getCommandName(), Permissions.GLOBAL_MESSAGE.getPermission(), false, true);
    }

    @Override
    public void execute(CommandSender sender, CustomPlayer customPlayer, String[] args) {

        if (args.length == 0) {
            if (customPlayer.isInGlobalMessagesChannel()) {
                customPlayer.setInGlobalMessagesChannel(false);
                sender.sendMessage(ChatColor.RED + "Global chat messages has been disabled.");
            } else {
                customPlayer.setInGlobalMessagesChannel(true);
                sender.sendMessage(ChatColor.DARK_GREEN + "Global chat messages has been enabled.");
            }
            return;
        }

        final StringBuilder stringBuilder = new StringBuilder();
        for (final String word : args) {
            stringBuilder.append(word).append(" ");
        }
        final String message = new String(stringBuilder).trim();
        customPlayer.sendGlobalMessage(message);
    }
}