package com.vanym.paniclecraft.item;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.vanym.paniclecraft.block.BlockPaintingFrame;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemPaintingFrame extends ItemBlock {
    
    public ItemPaintingFrame(Block block) {
        super(block);
    }
    
    @Override
    public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
        if (armorType == 0) {
            return true;
        }
        return super.isValidArmor(stack, armorType, entity);
    }
    
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    @SideOnly(Side.CLIENT)
    public void addInformation(
            ItemStack itemStack,
            EntityPlayer entityPlayer,
            List list,
            boolean advancedItemTooltips) {
        if (itemStack.hasTagCompound()) {
            Map<String, Integer> map = new TreeMap<>();
            NBTTagCompound itemTag = itemStack.getTagCompound();
            for (int i = 0; i < TileEntityPaintingFrame.N; ++i) {
                final String TAG_PICTURE_I = BlockPaintingFrame.getPictureTag(i);
                if (!itemTag.hasKey(TAG_PICTURE_I)) {
                    continue;
                }
                NBTTagCompound pictureTag = itemTag.getCompoundTag(TAG_PICTURE_I);
                String info;
                if (pictureTag.hasNoTags()) {
                    info = "";
                } else {
                    info = ItemPainting.pictureInformation(pictureTag);
                }
                map.put(info, map.getOrDefault(info, 0) + 1);
            }
            map.forEach((info, count)-> {
                StringBuilder sb = new StringBuilder();
                sb.append(info);
                sb.append("×");
                sb.append(count);
                list.add(sb.toString());
            });
        }
    }
}
