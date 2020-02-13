package com.blockworlds.ultimateportals.listeners;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public abstract class UpListener implements Listener {

    private static ArrayList<UpListener> listeners = new ArrayList<>();
    private Plugin plugin;

    public UpListener(Plugin plugin){
        this.plugin = plugin;
        this.register();
        listeners.add(this);
    }

    public void register(){
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static ArrayList<UpListener> getlisteners(){
        return listeners;
    }
}
