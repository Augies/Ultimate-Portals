package com.blockworlds.ultimateportals;

import static com.blockworlds.ultimateportals.PortalHandler.getLogicDirection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

/**
 * Portals Shaped like this:
 * ■ ■ ■
 * ▦  ▦
 * ■   ■
 * ■ ■ ■
 * ▦ = Emerald Block
 * ■ = Any Other Opaque Block
 * Nether Portal frame forms within the frame upon portal creation.
 */
public class Portal {
	
	//Coordinates relate to bottom middle block
	private volatile UUID owner;//Don't store instances of Player, as the server needs to be able to remove them and re-add when necessary, and storing a stale copy can cause problems.
	private volatile Location location, destination;//Redundant information: location already contains a reference to the world, no need to store it again here(and locations with a null world generally cause problems anyway)
	private volatile String identifier;
	private volatile BlockFace portalFacing; //For my sanity, as location.getDirection() cannot be cast to a blockface
	private volatile int instanceNum; //the instance number of the portal. If it's the second portal created with the same identifier, it'd be instance 2
	
	protected transient volatile String destinationLine;//Temporary variable (only used when a portal is loaded in world a while world b is not yet loaded, so the destination is not able to initialize properly yet)
	
	/** @param owner The UUID of the player that created this portal
	 * @param location This portal's location
	 * @param identifier This portal's identifier 
	 * @param portalFacing The direction this portal will be facing
	 * @param instanceNum The instance number of this portal */
	public Portal(UUID owner, Location location, String identifier, BlockFace portalFacing, int instanceNum){
		this.owner = owner;
		this.location = location;
		this.identifier = identifier;
		this.portalFacing = portalFacing;
		this.instanceNum = instanceNum;
	}
	
	protected boolean copyFrom(Portal portal) {
		if(portal != null) {
			this.owner = portal.owner;
			this.location = portal.location;
			this.identifier = portal.identifier;
			this.portalFacing = portal.portalFacing;
			this.instanceNum = portal.instanceNum;
			return true;
		}
		return false;
	}
	
	/** @return True if this portal was just registered. Will return false if this portal is already registered. */
	public boolean register() {
		Block[] portalBlocks = this.getPortalBlocks();
		for(Block i : portalBlocks){
			if(PortalHandler.getPortalWithin(i.getLocation()) != null){
				return false;
			}
		}
		if(PortalHandler.getPortalAt(this.location) != null) {
			return false;
		}
		PortalHandler.instances.add(this);
		return true;
	}
	
	/** @return True if this portal was just unregistered. Will return false if this portal was already unregistered. */
	public boolean unregister(boolean fixInstanceNums) {
		if(PortalHandler.instances.contains(this)) {
			try {
				this.getLocation().getWorld().getBlockAt(this.getLocation()).getRelative(BlockFace.UP).setType(Material.AIR);
				this.getLocation().getWorld().getBlockAt(this.getLocation()).getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.AIR);
			} catch (NullPointerException e) {
				Main.getPlugin().getLogger().log(Level.WARNING, "World was null when unregistering portal " + this.identifier + "_" + this.instanceNum + ".", e);
			}
			while(PortalHandler.instances.remove(this)) {
				if(fixInstanceNums) {
					for(Portal i : PortalHandler.instances) {
						if(i.getInstanceNum() > this.getInstanceNum() && i.identifier.equals(this.identifier)) {
							i.instanceNum--;
						}
					}
				}
			}
			return true;
		}
		return false;
	}
	
	/** @return True if this portal is registered */
	public boolean isRegistered() {
		return PortalHandler.instances.contains(this);
	}
	
	/** @return This portal's destination location */
	public Location getDestination() {
		if(this.instanceNum == 1){
			return PortalHandler.getPortal(this.identifier, 2) == null ? null : PortalHandler.getPortal(this.identifier, 2).getLocation();
		}
		return PortalHandler.getPortal(this.identifier, this.instanceNum+1) == null ? PortalHandler.getPortal(this.identifier, 1).getLocation() : PortalHandler.getPortal(this.identifier, this.instanceNum+1).getLocation();
	}
	
	/** @return This portal's destination portal */
	public Portal getDestinationPortal() {
		return PortalHandler.getPortalAt(this.getDestination());
	}
	
	/** @param destination The destination location to set
	 * @return This portal */
	public Portal setDestination(Location destination) {
		this.destination = destination;
		return this;
	}
	
	/** @param destination The destination location to set
	 * @return This portal */
	public Portal setDestinationPortal(Portal destination) {
		this.destination = destination.getLocation() == null ? null : destination.location;
		return this;
	}
	
	/** @param linkDestinationToThis Whether or not the matching destination portal should have its destination set to be this portal
	 * @return True if this method successfully found and set this portal's destination portal based on its identifier. */
	public boolean linkDestinationPortalBasedOnIdentifier(boolean linkDestinationToThis) {
		Portal check = this.getDestinationPortal();
		if(check != null && check != this) {
			return false;
		}
		for(Portal portal : PortalHandler.instances) {
			if(ChatColor.stripColor(portal.identifier).equals(ChatColor.stripColor(this.identifier))) {
				this.setDestinationPortal(portal);
				if(linkDestinationToThis) {
					portal.setDestinationPortal(this);
				}
				return portal != check;
			}
		}
		return false;
	}
	
	/** @param identifier The identifier to check for
	 * @return The number of loaded portals with identifiers matching the one given */
	public static int getNumberOf(String identifier){
		int num = 0;
		for(Portal portal : PortalHandler.instances){
			if(ChatColor.stripColor(portal.identifier).equals(ChatColor.stripColor(identifier))){
				num++;
			}
		}
		return num;
	}
	
	/** @return This portal's save file */
	public File getSaveFile() {
		return new File(PortalHandler.getSaveFolder(), this.identifier.concat("_").concat(String.valueOf(this.instanceNum)));
	}
	
	/** @return True if this portal saved its data to file successfully */
	public boolean save() {
		try(PrintWriter pr = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.getSaveFile()), StandardCharsets.ISO_8859_1), true)) {
			pr.print(this.toString());
			pr.flush();
		} catch(IOException ex) {
			Main.getPlugin().getLogger().log(Level.WARNING, "Failed to save portal to file!", ex);
			return false;
		}
		return true;
	}
	
	/** @return True if this portal successfully reloaded its save data from file */
	public boolean reload() {
		File file = this.getSaveFile();
		if(!file.exists()) {
			return false;
		}
		String lines = PortalHandler.readFile(file);
		if(lines != null) {
			Portal reloaded = PortalHandler.fromString(lines);
			return this.copyFrom(reloaded);
		}
		return false;
	}
	
	/** @return The world that this portal is located in */
	public World getWorld() {
		return this.location.getWorld();
	}
	
	/** @return This portal's location */
	public Location getLocation() {
		return this.location;
	}
	
	/** @return The identifier for this portal */
	public String getIdentifier() {
		return this.identifier;
	}
	
	/** @return the direction the portal faces */
	public BlockFace getPortalFacing() {
		return this.portalFacing;
	}
	
	/** @param identifier The new identifier for this portal 
	 * @return This portal */
	public Portal setIdentifier(String identifier) {
		this.identifier = identifier;
		return this;
	}
	
	/** @return The UUID of the player that created this portal */
	public UUID getOwner() {
		return this.owner;
	}

	/** @return The player that created this portal */
	public Player getOwnerPlayer() {
		return Bukkit.getServer().getPlayer(this.owner);
	}
	
	/** @param player The UUID of the player who will now own this portal
	 * @return This portal */
	public Portal setOwner(UUID player) {
		this.owner = player;
		return this;
	}

	/** @param player The player who will now own this portal
	 * @return This portal */
	public Portal setOwner(Player player) {
		return this.setOwner(player.getUniqueId());
	}
	
	public int getInstanceNum() {
		return this.instanceNum;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("identifier=".concat(this.identifier == null ? "" : this.identifier).concat("\r\n"));
		sb.append("owner=").append(this.owner.toString()).append("\r\n");
		sb.append("location={").append(this.location.getWorld().getName()).append(",").append(this.location.getBlockX()).append(",").append(this.location.getBlockY()).append(",").append(this.location.getBlockZ()).append("}").append("\r\n");
		sb.append("portalFacing=").append(this.portalFacing.name()).append("\r\n");
		sb.append("instanceNum=").append(this.instanceNum).append("\r\n");
		if(this.destination != null) {
			sb.append("destination={").append(this.destination.getWorld().getName()).append(",").append(this.destination.getBlockX()).append(",").append(this.destination.getBlockY()).append(",").append(this.destination.getBlockZ()).append("}").append("\r\n");
		} else if(this.destinationLine != null) {
			System.out.println("destination for " + this.identifier + "_" + this.instanceNum + " is null");
			sb.append("destination=").append(this.destinationLine).append("\r\n");
		}else{
			System.out.println("destinationLine for " + this.identifier + "_" + this.instanceNum + " is null");
		}
		return sb.toString();
	}
	
	/** 0 = portal's identifying block 1-3 = other blocks in that column from bottom to top
	 * 4-7 = left column from bottom to top
	 * 8-11 = right column from bottom to top
	 * Gets the portal's blocks
	 * 
	 * @return both water blocks of a portal */
	public Block[] getPortalBlocks() {
		//TODO better looking code? Not manually setting each portion of array. lol.
		
		//Could use an ArrayList instead and then return list.toArray(new Block[list.size()]); - Brian
		//Although, you're referencing blocks that have already been set in the array, so this is probably fine for now.
		//This will be re-worked anyway when I implement the dynamic portal frame detection anyway, right?
		
		
		Block[] blocks = new Block[12];
		BlockFace logicDirection = getLogicDirection(this.portalFacing);
		blocks[0] = this.location.getBlock();
		blocks[1] = this.location.getBlock().getRelative(BlockFace.UP);
		blocks[2] = blocks[1].getRelative(BlockFace.UP);
		blocks[3] = blocks[2].getRelative(BlockFace.UP);
		blocks[4] = blocks[0].getRelative(logicDirection.getOppositeFace());
		blocks[5] = blocks[4].getRelative(BlockFace.UP);
		blocks[6] = blocks[5].getRelative(BlockFace.UP);
		blocks[7] = blocks[6].getRelative(BlockFace.UP);
		blocks[8] = blocks[0].getRelative(logicDirection);
		blocks[9] = blocks[8].getRelative(BlockFace.UP);
		blocks[10] = blocks[9].getRelative(BlockFace.UP);
		blocks[11] = blocks[10].getRelative(BlockFace.UP);
		return blocks;
	}
	
}
