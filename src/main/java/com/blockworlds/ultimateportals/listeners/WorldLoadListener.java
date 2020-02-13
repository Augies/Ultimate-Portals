package com.blockworlds.ultimateportals.listeners;

import com.blockworlds.ultimateportals.Main;
import com.blockworlds.ultimateportals.Portal;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

/** @author Brian_Entei */
public class WorldLoadListener extends UpListener {
	
	/** Constructor for Bukkit Listener */
	public WorldLoadListener(Plugin plugin) {
		super(plugin);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldLoadEvent(WorldLoadEvent event) {
		World world = event.getWorld();
		int numRegistered = 0;
		for(Portal check : Portal.loadPortalsFromFile(false, false)) {
			if(!check.isRegistered() && check.getLocation().getWorld() == world) {
				numRegistered += check.register() ? 1 : 0;
			}
		}
		if(numRegistered > 0) {
			Main.getPlugin().getLogger().info("Successfully loaded ".concat(Integer.toString(numRegistered)).concat(" portal").concat(numRegistered == 1 ? "" : "s").concat(" from file for world: \"").concat(world.getName()).concat("\""));
		} else {
			Main.getPlugin().getLogger().log(Level.FINE, "No portals loaded for world \"".concat(world.getName()).concat("\"."));
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldLoadEvent(WorldUnloadEvent event) {
		World world = event.getWorld();
		int numUnregistered = 0;
		for(Portal portal : Portal.getPortalsWithin(world)) {
			if(portal.save()) {
				numUnregistered += portal.unregister(false) ? 1 : 0;
			} else {
				Main.getPlugin().getLogger().warning("Failed to save portal \"".concat(portal.getIdentifier()).concat("\" at \"").concat(portal.getLocation().toVector().toString()).concat("\"! The world that it was in (\"").concat(world.getName()).concat("\") has unloaded, so this portal's data may be lost!"));
			}
		}
		if(numUnregistered > 0) {
			Main.getPlugin().getLogger().info("Successfully saved ".concat(Integer.toString(numUnregistered)).concat(" portal").concat(numUnregistered == 1 ? "" : "s").concat(" to file for world: \"").concat(world.getName()).concat("\""));
		} else {
			Main.getPlugin().getLogger().log(Level.FINE, "No portals saved for world \"".concat(world.getName()).concat("\"."));
		}
	}
	
}
