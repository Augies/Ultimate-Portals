package com.blockworlds.ultimateportals;

import com.blockworlds.ultimateportals.listeners.ClockUseListener;
import com.blockworlds.ultimateportals.listeners.WorldLoadListener;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static volatile ClockUseListener clockUseListener = null;
    private static volatile WorldLoadListener worldLoadListener = null;

    private static Main plugin;

    public static Main getPlugin() {
        return plugin;
    }

    public void onEnable(){
        plugin = this;
        this.getServer().getPluginManager().registerEvents(clockUseListener = new ClockUseListener(), this);
        this.getServer().getPluginManager().registerEvents(worldLoadListener = new WorldLoadListener(), this);
    }

    public void onDisable(){
        HandlerList.unregisterAll(this);//unregisters all listeners for the specified plugin
        clockUseListener = null;
        worldLoadListener = null;
    }
    
}
