package com.blockworlds.ultimateportals;

import com.blockworlds.ultimateportals.commands.MasterCommand;
import com.blockworlds.ultimateportals.listeners.ClockUseListener;
import com.blockworlds.ultimateportals.listeners.PortalBreakListener;
import com.blockworlds.ultimateportals.listeners.PortalEntryListener;
import com.blockworlds.ultimateportals.listeners.WorldLoadListener;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    //TODO Disable use of portals as nether portals
    //TODO make portal face the correct direction
    //TODO unit testing

    private static volatile ClockUseListener clockUseListener = null;
    private static volatile WorldLoadListener worldLoadListener = null;
    private static volatile PortalBreakListener portalBreakListener = null;
    private static volatile PortalEntryListener portalEntryListener = null;

    private static Main plugin;

    public static Main getPlugin() {
        return plugin;
    }

    public void onEnable(){
        plugin = this;
        this.getCommand("ultimateportals").setExecutor(new MasterCommand());
        this.getServer().getPluginManager().registerEvents(portalEntryListener = new PortalEntryListener(), this);
        this.getServer().getPluginManager().registerEvents(clockUseListener = new ClockUseListener(), this);
        this.getServer().getPluginManager().registerEvents(worldLoadListener = new WorldLoadListener(), this);
        this.getServer().getPluginManager().registerEvents(portalBreakListener = new PortalBreakListener(), this);
        int numLoaded = Portal.loadPortalsFromFile(true, true).size();
        this.getLogger().info("Loaded ".concat(Integer.toString(numLoaded)).concat(" portal").concat(numLoaded == 1 ? "" : "s").concat(" from file successfully."));
    }

    public void onDisable(){
        int numSaved = Portal.savePortalsToFile();
        this.getLogger().info("Saved ".concat(Integer.toString(numSaved)).concat(" portal").concat(numSaved == 1 ? "" : "s").concat(" to file successfully."));
        HandlerList.unregisterAll(this);//unregisters all listeners for the specified plugin
        portalEntryListener = null;
        clockUseListener = null;
        worldLoadListener = null;
        portalBreakListener = null;
    }
    
}
