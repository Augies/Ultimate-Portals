package com.blockworlds.ultimateportals;

import com.blockworlds.ultimateportals.commands.MasterCommand;
import com.blockworlds.ultimateportals.listeners.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Main extends JavaPlugin {
    private static Plugin plugin;

    public static Plugin getPlugin(){
        return plugin;
    }

    public void onEnable(){
        plugin = this;
        createListeners();
        new MasterCommand(this);
        int numLoaded = Portal.loadPortalsFromFile(true, true).size();
        this.getLogger().info("Loaded ".concat(Integer.toString(numLoaded)).concat(" portal").concat(numLoaded == 1 ? "" : "s").concat(" from file successfully."));
    }

    public void onDisable(){
        int numSaved = Portal.savePortalsToFile();
        this.getLogger().info("Saved ".concat(Integer.toString(numSaved)).concat(" portal").concat(numSaved == 1 ? "" : "s").concat(" to file successfully."));
        HandlerList.unregisterAll(this);//unregisters all listeners for the specified plugin
        deleteListeners();
    }

    private void createListeners(){
        new ClockUseListener(this);
        new WorldLoadListener(this);
        new PortalBreakListener(this);
        new PortalEntryListener(this);
        new PistonUseListener(this);
    }

    private void deleteListeners(){
        UpListener.getlisteners().forEach((n) -> n=null);
        UpListener.getlisteners().clear();
    }
}
