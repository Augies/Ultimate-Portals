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
        Block targetBlock = player.getTargetBlockExact(1);
        while(targetBlock.getY()>=player.getLocation().getBlockY()){
            targetBlock = targetBlock.getRelative(BlockFace.DOWN);
        }
        if(Portal.getPortalAt(targetBlock.getLocation())!=null){
            player.sendMessage("Portal Found");
            Portal portal = Portal.getPortalAt(targetBlock.getLocation());
            if(portal!=null && portal.getDestination()!=null){
                event.setCancelled(true);
                Location destination = portal.getDestinationPortal().getLocation();
                Block destinationBlock = destination.getWorld().getBlockAt(destination).getRelative(portal.getDestinationPortal().getPortalFacing().getOppositeFace(), 2);
                player.teleport(destinationBlock.getRelative(BlockFace.UP).getLocation());
            }
            event.setCancelled(true);
        }
//        Location portalBlockLocation = event.getFrom().clone();
//        portalBlockLocation.setY(Math.round(portalBlockLocation.getBlockY()-1));
//        portalBlockLocation.setX(Math.round(portalBlockLocation.getBlockX()));
//        portalBlockLocation.setZ(Math.round(portalBlockLocation.getBlockZ()));
//        if(Portal.getPortalAt(portalBlockLocation)!=null){
//            player.sendMessage("Portal found");
//            Portal portal = Portal.getPortalAt(portalBlockLocation);
//            if (portal != null && portal.getDestination() != null) {
//                Location destination = portal.getDestinationPortal().getLocation();
//                Block destinationBlock = destination.getWorld().getBlockAt(destination).getRelative(portal.getDestinationPortal().getPortalFacing());
//                player.teleport(destinationBlock.getRelative(BlockFace.UP).getLocation());
//            }else{
//                player.sendMessage("Destination: " + Boolean.toString(portal.getDestination()!=null));
//            }
//        }
//        player.sendMessage(portalBlockLocation.toString());
//        player.sendMessage(event.getFrom().toString());
//        Location local = event.getFrom();
//        local.setX(local.getBlockX());
//        local.setY(local.getBlockY());
//        local.setZ(local.getBlockZ());
//        player.sendMessage(local.toString());
//        event.setCancelled(true);
    }

//    public static Location getPortalLocation(Location entryLocation){
//        if()
//    }
}
