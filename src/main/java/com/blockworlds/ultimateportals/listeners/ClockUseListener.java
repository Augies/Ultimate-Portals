package com.blockworlds.ultimateportals.listeners;

import com.blockworlds.ultimateportals.Portal;
import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Orientable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import static com.blockworlds.ultimateportals.PortalHandler.*;

public class ClockUseListener extends UpListener {

    /** Constructor for Bukkit Listener */
    public ClockUseListener(Plugin plugin) {
        super(plugin);
    }

    /** For the sake of my sanity, you must click the right block of it in order to create a portal
     *
     * @param event the playerInteractEvent */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerClockUse(PlayerInteractEvent event){
        if(event.getItem()!=null && event.getItem().getItemMeta()!= null && !event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("Clock") && event.getClickedBlock()!=null && event.getItem().getType()==Material.CLOCK && event.getClickedBlock().getType()==Material.EMERALD_BLOCK && isCardinalDirection(event.getBlockFace())){
            Player player = event.getPlayer();
            Block clickedBlock = event.getClickedBlock();
            BlockFace portalFacing = event.getBlockFace().getOppositeFace();
            if(isValidPortal(portalFacing, clickedBlock)){
                Location location = getPortalBlock(portalFacing, clickedBlock).getLocation();
                location.setDirection(portalFacing.getDirection());
                String name = event.getItem().getItemMeta().getDisplayName();
                Portal portal = new Portal(player.getUniqueId(), location, name, portalFacing, Portal.getNumberOf(name)+1);
                if(!portal.register()) {
                    player.sendMessage("\u00A7cA portal already exists at this location.");
                    portal.unregister(false);
                    return;
                }
                Block[] portalBlocks = new Block[2];
                portalBlocks[0]= clickedBlock.getRelative(getLogicDirection(portalFacing));
                portalBlocks[1]= clickedBlock.getRelative(getLogicDirection(portalFacing)).getRelative(BlockFace.DOWN);
                portalBlocks[0].setType(Material.NETHER_PORTAL);
                portalBlocks[1].setType(Material.NETHER_PORTAL);

                //Fixes Portal Orientation
                Orientable[] orientable = new Orientable[2];
                orientable[0] = (Orientable)portalBlocks[0].getBlockData();
                orientable[1] = (Orientable)portalBlocks[1].getBlockData();
                if(portalFacing==BlockFace.NORTH || portalFacing==BlockFace.SOUTH){
                    orientable[0].setAxis(Axis.X);
                    orientable[1].setAxis(Axis.X);
                }else{
                    orientable[0].setAxis(Axis.Z);
                    orientable[1].setAxis(Axis.Z);
                }
                portalBlocks[0].setBlockData(orientable[0]);
                portalBlocks[1].setBlockData(orientable[1]);
                player.sendMessage("\u00A7aSuccessfully created a portal named \"".concat(portal.getIdentifier()).concat("\u00A7r\u00A7a\" at the selected location."));
                //TODO maybe tell the player that they can now link this portal with another one somehow?
            }
        }
    }
}
