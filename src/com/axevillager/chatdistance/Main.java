package com.axevillager.chatdistance;

import com.axevillager.chatdistance.commands.BroadcastChatChannelCommand;
import com.axevillager.chatdistance.commands.GlobalChatChannelCommand;
import com.axevillager.chatdistance.commands.OutOfContextChatChannelCommand;
import com.axevillager.chatdistance.commands.StaffChatChannelCommand;
import com.axevillager.chatdistance.listeners.ChatDistanceHandler;
import com.axevillager.chatdistance.listeners.DeathMessagesHandler;
import com.axevillager.chatdistance.listeners.JoinLeaveMessagesHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main created by AxeVillager on 2017/03/10
 */

public class Main extends JavaPlugin {

    public static JavaPlugin javaPlugin;
    private final String pluginName = this.toString();
    private final ConsoleCommandSender console = Bukkit.getConsoleSender();
    public static FileConfiguration config;

    @Override
    public void onEnable() {
        console.sendMessage(ChatColor.YELLOW + pluginName + " is being enabled...");
        setup();
        registerCommands();
        registerEvents();
        console.sendMessage(ChatColor.GREEN + pluginName + " has been enabled.");
    }

    @Override
    public void onDisable() {
        console.sendMessage(ChatColor.YELLOW + pluginName + " is being disabled...");
        console.sendMessage(ChatColor.RED + pluginName + " has been disabled.");
    }

    private void setup() {
        javaPlugin = this;
        config = this.getConfig();
        saveDefaultConfig();
    }

    private void registerCommands() {
        new StaffChatChannelCommand();
        new BroadcastChatChannelCommand();
        new GlobalChatChannelCommand();
        new OutOfContextChatChannelCommand();
    }

    private void registerEvents() {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new ChatDistanceHandler(), this);
        pluginManager.registerEvents(new DeathMessagesHandler(), this);
        pluginManager.registerEvents(new JoinLeaveMessagesHandler(), this);
    }
}
