package com.blockworlds.ultimateportals;

import com.blockworlds.ultimateportals.listeners.ClockUseListener;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static volatile ClockUseListener clockUseListener = null;

    public void onEnable(){
        clockUseListener = new ClockUseListener();
        this.getServer().getPluginManager().registerEvents(clockUseListener, this);
    }

    public void onDisable(){
        HandlerList.unregisterAll(clockUseListener);
        clockUseListener = null;
    }
}
