package com.blockworlds.ultimateportals.listeners;

import com.blockworlds.ultimateportals.Portal;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class PortalBreakListener extends UpListener{

    public PortalBreakListener(Plugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPortalBreak(BlockBreakEvent event) {
        //TODO break the portal if owned by that UUID, otherwise cancel event
        UUID uuid = event.getPlayer().getUniqueId();
        Portal portal = Portal.getPortalWithin(event.getBlock().getLocation());
        if (portal != null) {
            if (!portal.getOwner().toString().equals(uuid.toString())) {
                event.getPlayer().sendMessage("Bruh. Don't be trying to break a portal that isn't yours.");
                event.setCancelled(true);
            } else {
                portal.getPortalBlocks()[1].setType(Material.AIR);
                portal.getPortalBlocks()[2].setType(Material.AIR);
                portal.unregister(true);
                event.getPlayer().sendMessage("Broke the portal!");
            }
        }
    }
}
