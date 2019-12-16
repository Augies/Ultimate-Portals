package com.blockworlds.ultimateportals.listeners;

import com.blockworlds.ultimateportals.Portal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import static com.blockworlds.ultimateportals.PortalHandler.*;

public class ClockUseListener implements Listener {
    /**
     * For the sake of my sanity, you must click the right block of it in order to create a portal
     * @param event the playerInteractEvent
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerClockUse(PlayerInteractEvent event){
        if(event.getItem()!=null && event.getItem().getItemMeta()!= null && !event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("Clock") && event.getClickedBlock()!=null && event.getItem().getType()==Material.CLOCK && event.getClickedBlock().getType()==Material.EMERALD_BLOCK && isCardinalDirection(event.getBlockFace())){
            Player player = event.getPlayer();
            BlockFace portalFacing = event.getBlockFace().getOppositeFace();
            if(isValidPortal(portalFacing, event.getClickedBlock())){
                Location location = getPortalBlock(portalFacing, event.getClickedBlock()).getLocation();
                if(!hasPortal(location)) {
                    location.setDirection(portalFacing.getDirection());
                    Portal portal = new Portal(event.getPlayer(), location, event.getClickedBlock().getWorld(), event.getItem().getItemMeta().getDisplayName());
                    getPortalBlock(portalFacing, event.getClickedBlock()).getRelative(BlockFace.UP).setType(Material.WATER);
                    getPortalBlock(portalFacing, event.getClickedBlock()).getRelative(BlockFace.UP, 2).setType(Material.WATER);
                }
            }
        }
    }
}
