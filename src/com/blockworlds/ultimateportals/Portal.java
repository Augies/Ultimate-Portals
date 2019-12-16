package com.blockworlds.ultimateportals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Portals Shaped like this:
 * ■ ■ ■
 * ▦  ▦
 * ■   ■
 * ■ ■ ■
 * ▦ = Emerald Block
 * ■ = Any Other Opaque Block
 */
public class Portal {

    protected static final ConcurrentLinkedDeque<Portal> instances = new ConcurrentLinkedDeque<>();

    /** @param location The location whose portal will be returned
     * @return The location's accompanying portal, or <tt><b>null</b></tt> if there was no portal registered for the given location */
    public static Portal getPortalAt(Location location) {
        for(Portal portal : instances) {
            if(portal.location.getWorld() == location.getWorld() && portal.location.getBlockX() == location.getBlockX() && portal.location.getBlockY() == location.getBlockY() && portal.location.getBlockZ() == location.getBlockZ()) {
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

    /** @return The folder in which all portals are saved to and loaded from */
    public static File getSaveFolder() {
        File folder = new File(Main.getPlugin().getDataFolder(), "PortalData");
        folder.mkdirs();
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
                            Portal existing = getPortalAt(portal.location);
                            if(existing != null) {
                                existing.unregister();
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

    //Coordinates relate to bottom middle block
    private volatile UUID owner;//Don't store instances of Player, as the server needs to be able to remove them and re-add when necessary, and storing a stale copy can cause problems.
    private volatile Location location, destination;//Redundant information: location already contains a reference to the world, no need to store it again here(and locations with a null world generally cause problems anyway)
    private volatile String identifier;

    private transient volatile String destinationLine;//Temporary variable (only used when a portal is loaded in world a while world b is not yet loaded, so the destination is not able to initialize properly yet)

    /** @param owner The UUID of the player that created this portal
     * @param location This portal's location
     * @param identifier This portal's identifier */
    public Portal(UUID owner, Location location, String identifier){
        this.owner = owner;
        this.location = location;
        this.identifier = identifier;
    }

    protected boolean copyFrom(Portal portal) {
        if(portal != null) {
            this.owner = portal.owner;
            this.location = portal.location;
            this.identifier = portal.identifier;
            return true;
        }
        return false;
    }

    /** @return True if this portal was just registered. Will return false if this portal is already registered. */
    public boolean register() {
        if(getPortalAt(this.location) != null) {
            return false;
        }
        instances.add(this);
        return true;
    }

    /** @return True if this portal was just unregistered. Will return false if this portal was already unregistered. */
    public boolean unregister() {
        if(instances.contains(this)) {
            while(instances.remove(this)) {
            }
            return true;
        }
        return false;
    }

    /** @return True if this portal is registered */
    public boolean isRegistered() {
        return instances.contains(this);
    }

    /** @return This portal's destination location */
    public Location getDestination() {
        if(this.destination == null && this.destinationLine != null) {
            Location check = new Location(null, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
            if(fromString(check, "destination", this.destinationLine) && check.getWorld() != null && check.getBlockX() != Integer.MIN_VALUE && check.getBlockY() != Integer.MIN_VALUE && check.getBlockZ() != Integer.MIN_VALUE) {
                this.destination = check;
                this.destinationLine = null;
            }
        }
        return this.destination;
    }

    /** @return This portal's destination portal */
    public Portal getDestinationPortal() {
        return getPortalAt(this.getDestination());
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
        this.destination = destination == null ? null : destination.location;
        return this;
    }

    /** @param linkDestinationToThis Whether or not the matching destination portal should have its destination set to be this portal
     * @return True if this method successfully found and set this portal's destination portal based on its identifier. */
    public boolean linkDestinationPortalBasedOnIdentifier(boolean linkDestinationToThis) {
        Portal check = this.getDestinationPortal();
        if(check != null && check != this) {
            return false;
        }
        for(Portal portal : instances) {
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

    /** @return This portal's save file */
    public File getSaveFile() {
        return new File(getSaveFolder(), (this.owner.toString().concat("_").concat(this.identifier)).replaceAll("[^a-zA-Z0-9-_.]", "-").concat(".txt"));
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
        String lines = readFile(file);
        if(lines != null) {
            Portal reloaded = fromString(lines);
            return this.copyFrom(reloaded);
        }
        return false;
    }

    /** @return The world that this portal is located in */
    public World getWorld(){
        return this.location.getWorld();
    }

    /** @return This portal's location */
    public Location getLocation(){
        return this.location;
    }

    /** @return The identifier for this portal */
    public String getIdentifier(){
        return this.identifier;
    }

    /** @param identifier The new identifier for this portal 
     * @return This portal */
    public Portal setIdentifier(String identifier){
        this.identifier = identifier;
        return this;
    }

    /** @return The UUID of the player that created this portal */
    public UUID getOwner(){
        return this.owner;
    }

    /** @return The player that created this portal */
    public Player getOwnerPlayer(){
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("identifier=".concat(this.identifier == null ? "" : this.identifier).concat("\r\n"));
        sb.append("owner=").append(this.owner.toString()).append("\r\n");
        sb.append("location={").append(this.location.getWorld().getName()).append(",").append(this.location.getBlockX()).append(",").append(this.location.getBlockY()).append(",").append(this.location.getBlockZ()).append("}").append("\r\n");
        if(this.destination != null) {
            sb.append("destination={").append(this.destination.getWorld().getName()).append(",").append(this.destination.getBlockX()).append(",").append(this.destination.getBlockY()).append(",").append(this.destination.getBlockZ()).append("}").append("\r\n");
        } else if(this.destinationLine != null) {
            sb.append("destination=").append(this.destinationLine).append("\r\n");
        }
        return sb.toString();
    }

    private static boolean isUUID(String str) {
        try {
            UUID.fromString(str);
            return true;
        } catch(IllegalArgumentException ex) {
        }
        return false;
    }

    private static boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException ex) {
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
            
            if(param.equals("identifier")) {
                identifier = value;
            } else if(param.equals("owner")) {
                if(isUUID(value)) {
                    owner = UUID.fromString(value);
                } else {
                    Main.getPlugin().getLogger().warning("Ignoring malformed portal parameter \"owner\" with value \"".concat(value).concat("\"..."));
                }
            } else if(param.equals("location") || param.equals("destination")) {
                Location loc = param.equals("location") ? location : destination;
                if(param.equals("destination")) {
                    destinationLine = value;
                }
                if(!fromString(loc, param, value) && param.equals("location")) {
                    break;
                }
            }
        }
        if(identifier != null && owner != null && location.getWorld() != null && location.getBlockX() != Integer.MIN_VALUE && location.getBlockY() != Integer.MIN_VALUE && location.getBlockZ() != Integer.MIN_VALUE) {
            Portal portal = new Portal(owner, location, identifier);
            if(destination.getWorld() != null && destination.getBlockX() != Integer.MIN_VALUE && destination.getBlockY() != Integer.MIN_VALUE && destination.getBlockZ() != Integer.MIN_VALUE) {
                portal.setDestination(destination);
            } else {
                portal.destinationLine = destinationLine;
            }
            return portal;
        }
        return null;
    }

}
