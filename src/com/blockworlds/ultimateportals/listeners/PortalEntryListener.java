package com.blockworlds.ultimateportals.listeners;

import com.blockworlds.ultimateportals.Portal;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class PortalEntryListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public static void onPortalEntry(PlayerPortalEvent event){

        Player player = event.getPlayer();
        Location portalBlockLocation = event.getFrom();
        portalBlockLocation.setY(Math.round(portalBlockLocation.getY()-1));
        portalBlockLocation.setX(Math.round(portalBlockLocation.getX()));
        portalBlockLocation.setZ(Math.round(portalBlockLocation.getZ()));
        if(Portal.getPortalAt(portalBlockLocation)!=null){
            player.sendMessage("Portal found");
            Portal portal = Portal.getPortalAt(portalBlockLocation);
            if (portal != null && portal.getDestination() != null) {
                Location destination = portal.getDestinationPortal().getLocation();
                Block destinationBlock = destination.getWorld().getBlockAt(destination).getRelative(portal.getDestinationPortal().getPortalFacing());
                player.teleport(destinationBlock.getRelative(BlockFace.UP).getLocation());
            }else{
                player.sendMessage("Destination: " + Boolean.toString(portal.getDestination()!=null));
            }
        }else{
           player.sendMessage(portalBlockLocation.toString());
           for(Portal i : Portal.instances){
               player.sendMessage(i.getLocation().toString());
           }
        }
        event.setCancelled(true);
    }
}
