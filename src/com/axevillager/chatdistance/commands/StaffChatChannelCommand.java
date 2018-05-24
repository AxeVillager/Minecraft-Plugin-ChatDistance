package com.axevillager.chatdistance.commands;

import com.axevillager.chatdistance.player.CustomPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * StaffChatChannelCommand created by AxeVillager on 2018/05/21.
 */

public class StaffChatChannelCommand extends AbstractCommand {

    public StaffChatChannelCommand() {
        super(Permissions.STAFF_MESSAGE.getCommandName(), Permissions.STAFF_MESSAGE.getPermission(), false, true);
    }

    @Override
    public void execute(CommandSender sender, CustomPlayer customPlayer, String[] args) {

        if (args.length == 0) {
            if (customPlayer.isInStaffChannel()) {
                customPlayer.setInStaffChannel(false);
                sender.sendMessage(ChatColor.RED + "Staff chat messages has been disabled.");
            } else {
                customPlayer.setInStaffChannel(true);
                sender.sendMessage(ChatColor.DARK_GREEN + "Staff chat messages has been enabled.");
            }
            return;
        }

        final StringBuilder stringBuilder = new StringBuilder();
        for (final String word : args) {
            stringBuilder.append(word).append(" ");
        }
        final String message = new String(stringBuilder).trim();
        customPlayer.sendStaffMessage(message);
    }
}
