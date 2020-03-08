package com.blockworlds.ultimateportals;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class PortalHandler {
	
	public static final ConcurrentLinkedDeque<Portal> instances = new ConcurrentLinkedDeque<>();
	
	/** @param location The location whose portal will be returned
	 * @return The location's accompanying portal, or <tt><b>null</b></tt> if there was no portal registered for the given location */
	public static Portal getPortalAt(Location location) {
		for(Portal portal : instances) {
			if(portal.getLocation().getWorld() == location.getWorld() && portal.getLocation().getBlockX() == location.getBlockX() && portal.getLocation().getBlockY() == location.getBlockY() && portal.getLocation().getBlockZ() == location.getBlockZ()) {
				return portal;
			}
		}
		return null;
	}
	
	/** @param world The world whose portals will be returned
	 * @return A list containing all portals whose locations are within the given world */
	public static List<Portal> getPortalsWithin(World world) {
		List<Portal> list = new ArrayList<>();
		for(Portal portal : instances) {
			if(portal.getWorld() == world) {
				list.add(portal);
			}
		}
		return list;
	}
	
	public static boolean isValidPortal(BlockFace portalFacing, Block clickedBlock){
		if(isValidColumn(clickedBlock)){
			BlockFace logicDirection = getLogicDirection(portalFacing);
			if(isValidColumn(clickedBlock.getRelative(logicDirection, 2))) {
				Block floorCenter = clickedBlock.getRelative(logicDirection, 1).getRelative(BlockFace.DOWN, 2);
				return isValidFloor(floorCenter, portalFacing) && isValidFloor(floorCenter.getRelative(BlockFace.UP, 3), portalFacing) && isValidCenter(floorCenter);
			}
		}
		return false;
	}
	
	public static Block getPortalBlock(BlockFace portalFacing, Block clickedBlock){
		BlockFace logicDirection = getLogicDirection(portalFacing);
		return clickedBlock.getRelative(logicDirection, 1).getRelative(BlockFace.DOWN, 2);
	}
	
	public static BlockFace getLogicDirection(BlockFace  portalFacing){
		BlockFace logicDirection;
		switch(portalFacing){
			case NORTH:
				logicDirection = BlockFace.EAST;
				break;
			case SOUTH:
				logicDirection = BlockFace.WEST;
				break;
			case EAST:
				logicDirection = BlockFace.SOUTH;
				break;
			case WEST:
				logicDirection = BlockFace.NORTH;
				break;
			default:
				throw new IllegalStateException("Unexpected value: " + portalFacing);
		}
		return logicDirection;
	}
	
	public static boolean isValidFloor(Block portalBlock, BlockFace portalFacing){
		BlockFace logicDirection = getLogicDirection(portalFacing);
		Block[] blocks = new Block[3];
		blocks[1] = portalBlock;
		blocks[0] = portalBlock.getRelative(logicDirection.getOppositeFace());
		blocks[2] = portalBlock.getRelative(logicDirection);
		return areValidBlocks(blocks);
	}
	
	public static boolean isValidColumn(Block emeraldBlock){
		Block[] blocks = new Block[3];
		blocks[0] = emeraldBlock.getRelative(BlockFace.DOWN);
		blocks[1] = emeraldBlock;
		blocks[2] = emeraldBlock.getRelative(BlockFace.UP);
		return areValidBlocks(blocks) && blocks[1].getType()==Material.EMERALD_BLOCK;
	}
	
	public static boolean areValidBlocks(Block[] blocks){
		for(Block block : blocks){
			if(!isValidBlock(block)){
				return false;
			}
		}
		return true;
	}
	
	public static boolean isValidBlock(Block block){
		return !(block.isLiquid() || block.isPassable());
	}
	
	public static boolean isCardinalDirection(BlockFace blockFace){
		return (blockFace == BlockFace.NORTH || blockFace == BlockFace.SOUTH || blockFace == BlockFace.EAST || blockFace == BlockFace.WEST);
	}
	
	public static boolean isValidCenter(Block floorCenter){
		Block b = floorCenter.getRelative(BlockFace.UP);
		Block upB = b.getRelative(BlockFace.UP);
		return (b.getType()== Material.AIR || b.isLiquid()) && (upB.getType()==Material.AIR || upB.isLiquid());
	}
	
	//==[Data loading/saving]==============================================================================================================
	
	/** @return The folder in which all portals are saved to and loaded from */
	public static File getSaveFolder() {
		File folder = new File(Main.getPlugin().getDataFolder(), "PortalData");
		if(!folder.mkdirs()){
			Main.getPlugin().getLogger().warning("Error creating save folder");
		}
		return folder;
	}
	
	protected static String readFile(File file) {
		try(FileInputStream in = new FileInputStream(file)) {
			int read;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while((read = in.read()) != -1) {
				baos.write(read);
			}
			byte[] data = baos.toByteArray();
			return new String(data, 0, data.length, StandardCharsets.ISO_8859_1);
		} catch(IOException ex) {
			Main.getPlugin().getLogger().log(Level.WARNING, "Failed to load portal from file: \"".concat(file.getAbsolutePath()).concat("\""), ex);
			return null;
		}
	}
	
	/** @return The number of portals that saved successfully */
	public static int savePortalsToFile() {
		try {
			FileUtils.cleanDirectory(getSaveFolder());
		}catch(IOException e){
			Main.getPlugin().getLogger().log(Level.WARNING, "Failed to delete old portals from file when saving new ones!", e);
		}
		int numSaved = 0;
		for(Portal portal : instances) {
			numSaved += portal.save() ? 1 : 0;
		}
		return numSaved;
	}
	
	/** @param overwriteExisting Whether or not existing portals should be unregistered if a new portal conflicts with it(based on location)
	 * @param registerNew Whether or not the new portals should be registered
	 * @return A list of brand-new portals that were successfully loaded from file. */
	public static List<Portal> loadPortalsFromFile(boolean overwriteExisting, boolean registerNew) {
		List<Portal> list = new ArrayList<>();
		File folder = getSaveFolder();
		String[] fileNames = folder.list();
		if(fileNames != null) {//This can sometimes happen (usually when there are no read permissions) ...
			for(String fileName : fileNames) {
				File file = new File(folder, fileName);
				String lines = readFile(file);
				if(lines != null) {
					Portal portal = fromString(lines);
					if(portal != null) {
						list.add(portal);
						if(overwriteExisting) {
							Portal existing = getPortalAt(portal.getLocation());
							if(existing != null) {
								existing.unregister(true);
							}
						}
						if(registerNew) {
							portal.register();
						}
					}
				}
			}
		}
		return list;
	}
	
	/** @param overwriteExisting Whether or not existing portals should be unregistered prior to the new conflicting portal's registration
	 * @return A list of brand-new registered portals that were successfully loaded from file */
	public static List<Portal> loadPortalsFromFile(boolean overwriteExisting) {
		return loadPortalsFromFile(overwriteExisting, true);
	}
	
	/** @return A list of brand-new registered portals that were successfully loaded from file */
	public static List<Portal> loadPortalsFromFile() {
		return loadPortalsFromFile(false);
	}
	
	private static boolean isUUID(String str) {
		try {
			UUID.fromString(str);
			return true;
		} catch(IllegalArgumentException ex) {
			Main.getPlugin().getLogger().log(Level.WARNING, String.format("Invalid UUID caught %s", str), ex);
		}
		return false;
	}
	
	private static boolean isInt(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch(NumberFormatException ex) {
			Main.getPlugin().getLogger().log(Level.WARNING, String.format("String is not an integer! %s"), ex);
		}
		return false;
	}
	
	protected static boolean fromString(Location loc, String param, String value) {
		if(value.startsWith("{") && value.endsWith("}")) {
			value = value.substring(1, value.length() - 1);
			if(value.contains(",") && value.indexOf(",") != value.lastIndexOf(",")) {
				String[] locData = value.split(Pattern.quote(","));
				if(locData.length == 4) {
					loc.setWorld(Bukkit.getServer().getWorld(locData[0]));
					if(isInt(locData[1])) {
						loc.setX(Integer.parseInt(locData[1]));
					} else {
						Main.getPlugin().getLogger().warning("Ignoring malformed portal parameter \"".concat(param).concat("\" with value \"").concat(value).concat("\" (Corrupted X axis data)"));
						return false;
					}
					if(isInt(locData[2])) {
						loc.setY(Integer.parseInt(locData[2]));
					} else {
						Main.getPlugin().getLogger().warning("Ignoring malformed portal parameter \"".concat(param).concat("\" with value \"").concat(value).concat("\" (Corrupted Y axis data)"));
						return false;
					}
					if(isInt(locData[3])) {
						loc.setZ(Integer.parseInt(locData[3]));
					} else {
						Main.getPlugin().getLogger().warning("Ignoring malformed portal parameter \"".concat(param).concat("\" with value \"").concat(value).concat("\" (Corrupted Z axis data)"));
						return false;
					}
				} else {
					Main.getPlugin().getLogger().warning("Ignoring malformed portal parameter \"".concat(param).concat("\" with value \"").concat(value).concat("\" (Incorrect number of location data parameters. E.g. {world,x,y,z})"));
					return false;
				}
			} else {
				Main.getPlugin().getLogger().warning("Ignoring malformed portal parameter \"".concat(param).concat("\" with value \"").concat(value).concat("\" (Missing/corrupted location data)"));
				return false;
			}
		} else {
			Main.getPlugin().getLogger().warning("Ignoring malformed portal parameter \"".concat(param).concat("\" with value \"").concat(value).concat("\" (Missing/unbalanced curly brackets)"));
			return false;
		}
		return true;
	}
	
	/** @param lines The lines read from a portal's save file
	 * @return A new unregistered portal if the given lines contained valid portal data */
	public static Portal fromString(String lines) {
		String[] split = lines.split(Pattern.quote("\n"));
		
		String identifier = null;
		UUID owner = null;
		Location location = new Location(null, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
		Location destination = new Location(null, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
		String destinationLine = null;
		BlockFace portalFacing = null;
		int instanceNum = -1;
		
		for(String line : split) {
			line = line.endsWith("\r") ? line.substring(0, line.length() - 1) : line;
			line = line.contains("#") ? line.substring(0, line.indexOf("#")) : line;
			if(line.trim().isEmpty()) {
				continue;
			}
			String[] params = line.split(Pattern.quote("="));
			String param = params.length >= 1 ? params[0] : "";
			String value = "";
			for(int i = 1; i < params.length; i++) {
				value = value.concat(params[i]).concat(i + 1 == params.length ? "" : "=");
			}
			
			switch(param) {
				case "identifier":
					identifier = value;
					break;
				case "owner":
					if (isUUID(value)) {
						owner = UUID.fromString(value);
					} else {
						Main.getPlugin().getLogger().warning("Ignoring malformed portal parameter \"owner\" with value \"".concat(value).concat("\"..."));
					}
					break;
				case "location":
				case "destination":
					Location loc = param.equals("location") ? location : destination;
					if(param.equals("destination")) {
						destinationLine = value;
					}
					if(!fromString(loc, param, value) && param.equals("location")) {
						continue;
					}
					break;
				case "portalFacing":
					portalFacing = BlockFace.valueOf(value);
					break;
				case "instanceNum":
					instanceNum = Integer.parseInt(value);
					break;
				default:
					break;
			}
		}
		if(identifier != null && owner != null && location.getWorld() != null && location.getBlockX() != Integer.MIN_VALUE && location.getBlockY() != Integer.MIN_VALUE && location.getBlockZ() != Integer.MIN_VALUE && portalFacing != null && instanceNum != -1) {
			Portal portal = new Portal(owner, location, identifier, portalFacing, instanceNum);
			if(destination.getWorld() != null && destination.getBlockX() != Integer.MIN_VALUE && destination.getBlockY() != Integer.MIN_VALUE && destination.getBlockZ() != Integer.MIN_VALUE) {
				portal.setDestination(destination);
			} else {
				portal.destinationLine = destinationLine;
			}
			return portal;
		}
		return null;
	}
	
	public static Portal getPortalWithin(Location location){
		List<Portal> portals = getPortalsWithin(location.getWorld());
		for(Portal i : portals){
			Block[] blocks = i.getPortalBlocks();
			for(Block j : blocks){
				Location jloc = j.getLocation();
				if(jloc.getBlockX() == location.getBlockX() && location.getBlockY()==jloc.getBlockY() && location.getBlockZ() == jloc.getBlockZ()){
					return i;
				}
			}
		}
		return null;
	}
	
	public static Portal getPortal(String identifier, int instanceNum){
		for(Portal portal : instances){
			if(portal.getIdentifier().equals(identifier) && portal.getInstanceNum()==instanceNum){
				return portal;
			}
		}
		return null;
	}
	
}
