package com.axevillager.chatdistance.commands;

import com.axevillager.chatdistance.message.ExpressiveMessage;
import com.axevillager.chatdistance.player.CustomPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * OutOfContextChatChannelCommand created by AxeVillager on 2018/05/09.
 */

public class OutOfContextChatChannelCommand extends AbstractCommand {

    public OutOfContextChatChannelCommand() {
        super(Permissions.OOC_MESSAGE.getCommandName(), Permissions.OOC_MESSAGE.getPermission(), false, true);
    }

    @Override
    public void execute(CommandSender sender, CustomPlayer customPlayer, String[] args) {

        if (args.length == 0) {
            if (customPlayer.isInOutOfContextMessagesChannel()) {
                customPlayer.setInOutOfContextMessagesChannel(false);
                sender.sendMessage(ChatColor.RED + "OOC chat messages has been disabled.");
            } else {
                customPlayer.setInOutOfContextMessagesChannel(true);
                sender.sendMessage(ChatColor.DARK_GREEN + "OOC chat messages has been enabled.");
            }
            return;
        }

        final StringBuilder stringBuilder = new StringBuilder();
        for (final String word : args) {
            stringBuilder.append(word).append(" ");
        }
        final String message = new String(stringBuilder).trim();
        final ExpressiveMessage expressiveMessage = new ExpressiveMessage(message, customPlayer);
        customPlayer.sendOutOfContextMessage(expressiveMessage);
    }
}
