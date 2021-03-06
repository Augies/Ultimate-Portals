package com.blockworlds.ultimateportals;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class PortalHandler {
    public static boolean isValidPortal(BlockFace portalFacing, Block clickedBlock){
        if(isValidColumn(clickedBlock)){
            BlockFace logicDirection = getLogicDirection(portalFacing);
            if(isValidColumn(clickedBlock.getRelative(logicDirection, 2))) {
                Block floorCenter = clickedBlock.getRelative(logicDirection, 1).getRelative(BlockFace.DOWN, 2);
                return isValidFloor(floorCenter, portalFacing) && isValidFloor(floorCenter.getRelative(BlockFace.UP, 3), portalFacing);
            }
        }
        return false;
    }

    public static Block getPortalBlock(BlockFace portalFacing, Block clickedBlock){
        BlockFace logicDirection = getLogicDirection(portalFacing);
        return clickedBlock.getRelative(logicDirection, 1).getRelative(BlockFace.DOWN, 2);
    }

    public static BlockFace getLogicDirection(BlockFace  portalFacing){
        BlockFace logicDirection;
        switch(portalFacing){
            case NORTH:
                logicDirection = BlockFace.EAST;
                break;
            case SOUTH:
                logicDirection = BlockFace.WEST;
                break;
            case EAST:
                logicDirection = BlockFace.SOUTH;
                break;
            case WEST:
                logicDirection = BlockFace.NORTH;
                break;
            //$CASES-OMITTED$
            default:
                throw new IllegalStateException("Unexpected value: " + portalFacing);
        }
        return logicDirection;
    }

    public static boolean isValidFloor(Block portalBlock, BlockFace portalFacing){
        BlockFace logicDirection = getLogicDirection(portalFacing);
        return portalBlock.getRelative(logicDirection).getType().isBlock() && portalBlock.getType().isBlock() && portalBlock.getRelative(logicDirection.getOppositeFace()).getType().isBlock();
    }

    public static boolean isValidColumn(Block emeraldBlock){
        return emeraldBlock.getType() == Material.EMERALD_BLOCK && emeraldBlock.getRelative(BlockFace.UP).getType().isBlock() && emeraldBlock.getRelative(BlockFace.UP).getType().isBlock() && emeraldBlock.getRelative(BlockFace.DOWN, 2).getType().isBlock();
    }

    public static boolean isCardinalDirection(BlockFace blockFace){
        return (blockFace == BlockFace.NORTH || blockFace == BlockFace.SOUTH || blockFace == BlockFace.EAST || blockFace == BlockFace.WEST);
    }

}
