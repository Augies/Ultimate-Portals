package com.blockworlds.ultimateportals.listeners;

import com.blockworlds.ultimateportals.Portal;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.UUID;

public class PortalBreakListener {

    public void onPortalBreak(BlockBreakEvent event){
        //TODO break the portal if owned by that UUID, otherwise cancel event
        UUID uuid = event.getPlayer().getUniqueId();
        Portal portal = Portal.getPortalWithin(event.getBlock().getLocation());
        if(portal.getOwner()!=uuid){
            event.getPlayer().sendMessage("Bruh. Don't be trying to break a portal that isn't yours.");
            event.setCancelled(true);
        }else{
            portal.getPortalBlocks()[1].setType(Material.AIR);
            portal.getPortalBlocks()[2].setType(Material.AIR);
            event.getPlayer().sendMessage("Broke the portal!");
        }
    }
}
