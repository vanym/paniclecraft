package com.vanym.paniclecraft.item;

import java.util.List;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemChessDesk extends ItemBlock {
    
    public static final String TAG_MOVES = TileEntityChessDesk.TAG_MOVES;
    
    public ItemChessDesk(Block block) {
        super(block);
    }
    
    @Override
    public boolean placeBlockAt(
            ItemStack stack,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z,
            int side,
            float hitX,
            float hitY,
            float hitZ,
            int metadata) {
        int rot = MathHelper.floor_double((player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, rot);
    }
    
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    @SideOnly(Side.CLIENT)
    public void addInformation(
            ItemStack stack,
            EntityPlayer player,
            List list,
            boolean advancedItemTooltips) {
        if (GuiScreen.isShiftKeyDown() || advancedItemTooltips) {
            if (stack.hasTagCompound()) {
                NBTTagCompound tag = stack.getTagCompound();
                if (tag.hasKey(TAG_MOVES)) {
                    list.add(StatCollector.translateToLocal("text.chessGameHaveSave"));
                }
            }
        }
    }
    
    public static ItemStack getSavedDesk(TileEntityChessDesk tileCD) {
        ItemStack stack = new ItemStack(Core.instance.deskgame.itemChessDesk);
        if (tileCD == null) {
            return stack;
        }
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        tileCD.writeMovesToNBT(list);
        tag.setTag(TAG_MOVES, list);
        stack.setTagCompound(tag);
        return stack;
    }
}
