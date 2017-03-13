package com.baol.chatdistance;

import com.baol.chatdistance.events.Chat;
import com.baol.chatdistance.events.Death;
import com.baol.chatdistance.events.JoinAndDisconnect;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main created by Børre A. Opedal Lunde on 2017/03/10
 */

public class Main extends JavaPlugin {

    /**
     * When the plugin is enabled
     */
    public void onEnable() {

        // Save the default config
        saveDefaultConfig();

        // Notify the console the plugin has been enabled
        Bukkit.getConsoleSender().sendMessage("§a" + this + " has been enabled!");

        // Register events
        Bukkit.getPluginManager().registerEvents(new Chat(this), this);
        Bukkit.getPluginManager().registerEvents(new JoinAndDisconnect(this), this);
        Bukkit.getPluginManager().registerEvents(new Death(this), this);
    }


    /**
     * When the plugin is disabled
     */
    public void onDisable() {

        // Notify the console the plugin has been disabled
        Bukkit.getConsoleSender().sendMessage("§c" + this + " has been disabled!");

    }

}