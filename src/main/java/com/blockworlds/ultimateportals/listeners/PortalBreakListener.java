package com.blockworlds.ultimateportals.listeners;

import com.blockworlds.ultimateportals.Portal;
import com.blockworlds.ultimateportals.PortalHandler;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.type.Piston;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

public class PortalBreakListener extends UpListener {
	
	public PortalBreakListener(Plugin plugin) {
		super(plugin);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPortalBreak(BlockBreakEvent event) {
		//TODO break the portal if owned by that UUID, otherwise cancel event
		if(event.getBlock().getBlockData() instanceof Piston) {
			Piston piston = (Piston) event.getBlock().getBlockData();
			if(piston.isExtended()) {
				Location pistonHeadLocation = event.getBlock().getRelative(piston.getFacing()).getLocation();
				Portal p = PortalHandler.getPortalWithin(pistonHeadLocation);
				if(p != null) {
					event.setCancelled(true);
					event.getPlayer().sendMessage("\u00A7cPlease delete the portal labelled " + p.getIdentifier() + "_" + p.getInstanceNum() + " before breaking the piston!");
				}
			}
		}
		UUID uuid = event.getPlayer().getUniqueId();
		Portal portal = PortalHandler.getPortalWithin(event.getBlock().getLocation());
		if(portal != null) {
			if(!portal.getOwner().toString().equals(uuid.toString())) {
				event.getPlayer().sendMessage("\u00A7cBruh. Don't be trying to break a portal that isn't yours.");
				event.setCancelled(true);
			} else {
				portal.getPortalBlocks()[1].setType(Material.AIR);
				portal.getPortalBlocks()[2].setType(Material.AIR);
				portal.unregister(true);
				event.getPlayer().sendMessage("\u00A7aBroke the portal!");
			}
		}
	}
	
}
