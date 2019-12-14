package com.blockworlds.ultimateportals.listeners;

import com.blockworlds.ultimateportals.Portal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import static com.blockworlds.ultimateportals.PortalHandler.*;

public class ClockUseListener implements Listener {

    /** Constructor for Bukkit Listener */
    public ClockUseListener() {
    }

    /** For the sake of my sanity, you must click the right block of it in order to create a portal
     * 
     * @param event the playerInteractEvent */
    @EventHandler(priority = EventPriority.NORMAL)
    public static void onPlayerClockUse(PlayerInteractEvent event){
        if(event.getItem()!=null && event.getItem().getItemMeta()!= null && !event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("Clock") && event.getClickedBlock()!=null && event.getItem().getType()==Material.CLOCK && event.getClickedBlock().getType()==Material.EMERALD_BLOCK && isCardinalDirection(event.getBlockFace())){
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            BlockFace portalFacing = event.getBlockFace().getOppositeFace();
            if(isValidPortal(portalFacing, block)){
                Location location = getPortalBlock(portalFacing, block).getLocation();
                location.setDirection(portalFacing.getDirection());
                Portal portal = new Portal(player.getUniqueId(), location, event.getItem().getItemMeta().getDisplayName());
                if(!portal.register()) {
                    player.sendMessage("\u00A7cA portal already exists at this location.");
                    return;
                }
                player.sendMessage("\u00A7aSuccessfully created a portal named \"".concat(portal.getIdentifier()).concat("\u00A7r\u00A7a\" at the selected location."));
                //TODO maybe tell the player that they can now link this portal with another one somehow?
                //if(portal.linkDestinationPortalBasedOnIdentifier(true/false)) {player.sendMessage("\u00A7aFound and set a matching portal for this portal!");}
            }
        }
    }
    
}
