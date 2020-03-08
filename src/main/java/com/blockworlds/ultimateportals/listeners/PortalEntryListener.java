package com.blockworlds.ultimateportals.listeners;

import com.blockworlds.ultimateportals.Portal;
import com.blockworlds.ultimateportals.PortalHandler;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class PortalEntryListener extends UpListener {
	
	public PortalEntryListener(Plugin plugin) {
		super(plugin);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPortalEntry(PlayerPortalEvent event) {
		//TODO test how this works with nether portals. May throw an exception like IOOB or something.
		Player player = event.getPlayer();
		Location loc = event.getFrom();
		loc.setY(loc.getY() - 1);
		if(getNearestPortal(loc) != null){
			Portal portal = getNearestPortal(event.getFrom());
			Location destination = portal.getDestinationPortal().getLocation();
			if(destination.getWorld() != null) {
				Block destinationBlock = destination.getWorld().getBlockAt(destination).getRelative(portal.getDestinationPortal().getPortalFacing().getOppositeFace(), 2);
				player.teleport(destinationBlock.getRelative(BlockFace.UP).getLocation());
				player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 100, 1);
				event.setCancelled(true);
			}
		}
	}
	
	public static ArrayList<Block> getNearbyPortalBlocks(Location loc) {
		Location blockLoc = loc.clone();
		blockLoc.setX(blockLoc.getBlockX());
		blockLoc.setZ(blockLoc.getBlockZ());
		assert blockLoc.getWorld() != null;
		Block blockThere = blockLoc.getWorld().getBlockAt(blockLoc);
		ArrayList<Block> portalBlocks = new ArrayList<>();
		for(int x = -1; x <= 1; x++){
			for(int z = -1; z<= 1; z++){
				Block forBlock = blockThere.getRelative(x, 0, z);
				if(PortalHandler.getPortalAt(forBlock.getLocation()) != null){
					portalBlocks.add(forBlock);
				}
			}
		}
		return portalBlocks;
	}
	
	public static Portal getNearestPortal(Location loc){
		ArrayList<Block> portalBlocks = getNearbyPortalBlocks(loc);
		if(portalBlocks.size() > 0) {
			double distance = 10;
			Block nearestBlock = portalBlocks.get(0);
			for(Block i : portalBlocks) {
				double thisDist = Math.sqrt(Math.pow((loc.getX() - i.getX()), 2) + Math.pow((loc.getZ() - i.getZ()), 2));
				distance = Math.min(distance, thisDist);
				if(distance == thisDist) {
					nearestBlock = i;
				}
			}
			return PortalHandler.getPortalAt(nearestBlock.getLocation());
		}
		return null;
	}
	
}
