package com.blockworlds.ultimateportals.listeners;

import com.blockworlds.ultimateportals.Portal;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class PistonUseListener extends UpListener {

    public PistonUseListener(Plugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPistonUse(BlockPistonExtendEvent event){
        List<Block> pushedBlocks = event.getBlocks();
        for(Block block : pushedBlocks){
            if(Portal.getPortalWithin(block.getLocation())!=null){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPistonUse(BlockPistonRetractEvent event){
        if(Portal.getPortalWithin(event.getBlock().getRelative(event.getDirection()).getLocation())!=null){
            event.setCancelled(true);
        }
        List<Block> blocks = event.getBlocks();
        for(Block block : blocks){
            if(Portal.getPortalWithin(block.getLocation())!=null){
                event.setCancelled(true);
            }
        }
    }
}
