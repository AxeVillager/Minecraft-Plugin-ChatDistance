package com.axevillager.chatdistance.commands;

import com.axevillager.chatdistance.Main;
import com.axevillager.chatdistance.player.CustomPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * AbstractCommand created by AxeVillager on 2018/05/08.
 */

public abstract class AbstractCommand implements CommandExecutor {

    private final String commandName;
    private final String permission;
    private final boolean canConsoleUse;
    private final boolean mustBeCustomPlayer;
    private CommandSender sender;
    private CustomPlayer customPlayer;

    AbstractCommand(final String commandName, final String permission, final boolean canConsoleUse, final boolean mustBeCustomPlayer) {
        this.commandName = commandName;
        this.permission = permission;
        this.canConsoleUse = canConsoleUse;
        this.mustBeCustomPlayer = mustBeCustomPlayer;
        Main.javaPlugin.getCommand(commandName).setExecutor(this);
    }

    public abstract void execute(final CommandSender sender, final CustomPlayer customPlayer, final String[] args);

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        this.sender = sender;

        if (!cmd.getName().equalsIgnoreCase(commandName)) {
            return true;
        }

        if (!this.sender.hasPermission(this.permission)) {
            sendError("You don't have permission to perform the " + commandName + " command.");
            return true;
        }

        if (!this.canConsoleUse && !(sender instanceof Player)) {
            sendError("Only players may perform the " + commandName + " command.");
            return true;
        }

        if (this.mustBeCustomPlayer) {
            if (!(sender instanceof Player)) {
                sendError("You must be a custom player to perform the " + commandName + " command.");
                return true;
            }

            final UUID uuid = ((Player) sender).getUniqueId();
            CustomPlayer customPlayer = CustomPlayer.getCustomPlayer(uuid);

            if (customPlayer == null) {
                customPlayer = new CustomPlayer(uuid);
            }

            this.customPlayer = customPlayer;
        }

        execute(sender, customPlayer, args);
        return true;
    }

    private void sendError(final String message) {
        sender.sendMessage(ChatColor.RED + message);
    }

    String getCommandName() {
        return commandName;
    }

    String getPermission() {
        return permission;
    }

    boolean canConsoleUse() {
        return canConsoleUse;
    }
}
