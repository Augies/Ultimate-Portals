package com.blockworlds.ultimateportals;

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
    //Coordinates relate to bottom middle block
    private final Player owner;
    private final Location location;
    private final World world;
    private String identifier;

    public Portal(Player owner, Location location, World world, String identifier){
        this.owner = owner;
        this.location = location;
        this.world = world;
        this.identifier = identifier;
    }

    public World getWorld(){
        return this.world;
    }

    public Location getLocation(){
        return this.location;
    }

    public String getIdentifier(){
        return this.identifier;
    }

    public void setIdentifier(String identifier){
        this.identifier = identifier;
    }

    public Player getOwner(){
        return this.owner;
    }
}
