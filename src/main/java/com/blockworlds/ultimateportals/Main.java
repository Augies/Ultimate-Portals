package com.blockworlds.ultimateportals;

import com.blockworlds.ultimateportals.commands.MasterCommand;
import com.blockworlds.ultimateportals.listeners.ClockUseListener;
import com.blockworlds.ultimateportals.listeners.PistonUseListener;
import com.blockworlds.ultimateportals.listeners.PortalBreakListener;
import com.blockworlds.ultimateportals.listeners.PortalEntryListener;
import com.blockworlds.ultimateportals.listeners.UpListener;
import com.blockworlds.ultimateportals.listeners.WorldLoadListener;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	private static Plugin plugin;
	
	public static Plugin getPlugin(){
		return plugin;
	}
	
	public void onEnable(){
		plugin = this;
		createListeners();
		new MasterCommand(this);
		int numLoaded = PortalHandler.loadPortalsFromFile(true, true).size();
		this.getLogger().info("Loaded ".concat(Integer.toString(numLoaded)).concat(" portal").concat(numLoaded == 1 ? "" : "s").concat(" from file successfully."));
	}
	
	public void onDisable(){
		int numSaved = PortalHandler.savePortalsToFile();
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
