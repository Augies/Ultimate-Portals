package com.blockworlds.ultimateportals.listeners;

import com.blockworlds.ultimateportals.Portal;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.plugin.Plugin;

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
}
