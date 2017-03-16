package com.baol.chatdistance;

import com.baol.chatdistance.events.Chat;
import com.baol.chatdistance.events.Death;
import com.baol.chatdistance.events.JoinAndDisconnect;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main created by AxeVillager on 2017/03/10
 */

public class Main extends JavaPlugin {

    /**
     * When the plugin is enabled
     */
    public void onEnable() {

        // Save the default config
        saveDefaultConfig();

        // The plugin manager
        final PluginManager pm = Bukkit.getPluginManager();

        // Register events
        pm.registerEvents(new Chat(this), this);
        pm.registerEvents(new JoinAndDisconnect(this), this);
        pm.registerEvents(new Death(this), this);

        // Notify the console the plugin has been enabled
        Bukkit.getConsoleSender().sendMessage("§2" + this + " has been enabled!");
    }


    /**
     * When the plugin is disabled
     */
    public void onDisable() {

        // Notify the console the plugin has been disabled
        Bukkit.getConsoleSender().sendMessage("§c" + this + " has been disabled!");

    }

}
