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
                return isValidFloor(floorCenter, portalFacing) && isValidFloor(floorCenter.getRelative(BlockFace.UP, 3), portalFacing) && isValidCenter(floorCenter);
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
            default:
                throw new IllegalStateException("Unexpected value: " + portalFacing);
        }
        return logicDirection;
    }

    public static boolean isValidFloor(Block portalBlock, BlockFace portalFacing){
        BlockFace logicDirection = getLogicDirection(portalFacing);
        Block[] blocks = new Block[3];
        blocks[1] = portalBlock;
        blocks[0] = portalBlock.getRelative(logicDirection.getOppositeFace());
        blocks[2] = portalBlock.getRelative(logicDirection);
        return areValidBlocks(blocks);
    }

    public static boolean isValidColumn(Block emeraldBlock){
        Block[] blocks = new Block[3];
        blocks[0] = emeraldBlock.getRelative(BlockFace.DOWN);
        blocks[1] = emeraldBlock;
        blocks[2] = emeraldBlock.getRelative(BlockFace.UP);
        return areValidBlocks(blocks) && blocks[1].getType()==Material.EMERALD_BLOCK;
    }

    public static boolean areValidBlocks(Block[] blocks){
        for(Block block : blocks){
            if(!isValidBlock(block)){
                return false;
            }
        }
        return true;
    }

    public static boolean isValidBlock(Block block){
        return !(block.isLiquid() || block.isPassable());
    }

    public static boolean isCardinalDirection(BlockFace blockFace){
        return (blockFace == BlockFace.NORTH || blockFace == BlockFace.SOUTH || blockFace == BlockFace.EAST || blockFace == BlockFace.WEST);
    }

    public static boolean isValidCenter(Block floorCenter){
        Block b = floorCenter.getRelative(BlockFace.UP);
        Block upB = b.getRelative(BlockFace.UP);
        return (b.getType()== Material.AIR || b.isLiquid()) && (upB.getType()==Material.AIR || upB.isLiquid());
    }
}
