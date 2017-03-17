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
     * When the plugin is enabled...
     *
     * Save the default configuration, register
     * all the events and tell the console the plugin
     * has been enabled.
     */

    public void onEnable() {

        saveDefaultConfig();
        final PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new Chat(this), this);
        pm.registerEvents(new JoinAndDisconnect(this), this);
        pm.registerEvents(new Death(this), this);
        Bukkit.getConsoleSender().sendMessage("§2" + this + " has been enabled!");
    }




    /**
     * When the plugin is disabled...
     *
     * Tell the console the plugin has been disabled.
     */

    public void onDisable() {

        Bukkit.getConsoleSender().sendMessage("§c" + this + " has been disabled!");
    }
}
