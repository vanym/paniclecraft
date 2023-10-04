package com.vanym.paniclecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockMod3 extends ItemBlock {
    
    public ItemBlockMod3(Block block) {
        super(block);
    }
    
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return this.block.getLocalizedName();
    }
    
    protected boolean onBlockPlaced(
            BlockPos pos,
            World world,
            EntityPlayer player,
            ItemStack stack,
            IBlockState state) {
        return setTileEntityNBT(world, player, pos, stack);
    }
    
    @Override
    public boolean placeBlockAt(
            ItemStack stack,
            EntityPlayer player,
            World world,
            BlockPos pos,
            EnumFacing side,
            float hitX,
            float hitY,
            float hitZ,
            IBlockState newState) {
        // same as super but with onBlockPlaced call
        if (!world.setBlockState(pos, newState, 11)) {
            return false;
        }
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == this.block) {
            this.onBlockPlaced(pos, world, player, stack, state);
            this.block.onBlockPlacedBy(world, pos, state, player, stack);
            if (player instanceof EntityPlayerMP) {
                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
            }
        }
        return true;
    }
}
