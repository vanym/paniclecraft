package com.vanym.paniclecraft.recipe;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.inventory.InventoryUtils;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.item.ItemPaintingFrame;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class RecipePaintingFrameRemovePainting extends RecipeRegister.ShapelessOreRecipe {
    
    public RecipePaintingFrameRemovePainting() {
        super(Core.instance.painting.itemPainting,
              ItemPaintingFrame.getItemWithEmptyPictures(ItemPaintingFrame.FRONT));
    }
    
    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        if (!super.matches(inv, world)) {
            return false;
        }
        ItemStack frame = InventoryUtils.findItem(inv, Core.instance.painting.itemPaintingFrame);
        if (!frame.hasTagCompound()) {
            return false;
        }
        NBTTagCompound itemTag = frame.getTagCompound();
        for (EnumFacing pside : ItemPaintingFrame.SIDE_ORDER) {
            final String TAG_PICTURE_I = ItemPaintingFrame.getPictureTag(pside);
            if (itemTag.hasKey(TAG_PICTURE_I)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack painting = super.getCraftingResult(inv);
        ItemStack frame = InventoryUtils.findItem(inv, Core.instance.painting.itemPaintingFrame);
        if (!frame.hasTagCompound()) {
            return painting;
        }
        NBTTagCompound itemTag = frame.getTagCompound();
        NBTTagCompound pictureTag = null;
        for (EnumFacing pside : ItemPaintingFrame.SIDE_ORDER) {
            final String TAG_PICTURE_I = ItemPaintingFrame.getPictureTag(pside);
            if (itemTag.hasKey(TAG_PICTURE_I)) {
                pictureTag = itemTag.getCompoundTag(TAG_PICTURE_I);
                break;
            }
        }
        if (pictureTag == null || pictureTag.hasNoTags()) {
            return painting;
        }
        if (!painting.hasTagCompound()) {
            painting.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound paintingItemTag = painting.getTagCompound();
        NBTTagCompound paintingItemPictureTag = (NBTTagCompound)pictureTag.copy();
        if (paintingItemPictureTag.hasKey(Picture.TAG_NAME)) {
            painting.setStackDisplayName(paintingItemPictureTag.getString(Picture.TAG_NAME));
            paintingItemPictureTag.removeTag(Picture.TAG_NAME);
        }
        paintingItemTag.setTag(ItemPainting.TAG_PICTURE, paintingItemPictureTag);
        return painting;
    }
    
    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> list =
                NonNullList.<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        ItemStack frame = ItemStack.EMPTY;
        for (int i = 0; i < list.size(); ++i) {
            ItemStack slot = inv.getStackInSlot(i);
            Item item = slot.getItem();
            if (item == Core.instance.painting.itemPaintingFrame) {
                ItemStack stack = slot.copy();
                stack.setCount(1);
                frame = stack;
                list.set(i, frame);
                continue;
            }
            list.set(i, ForgeHooks.getContainerItem(slot));
        }
        if (!frame.hasTagCompound()) {
            return super.getRemainingItems(inv);
        }
        NBTTagCompound itemTag = frame.getTagCompound();
        for (EnumFacing pside : ItemPaintingFrame.SIDE_ORDER) {
            final String TAG_PICTURE_I = ItemPaintingFrame.getPictureTag(pside);
            if (!itemTag.hasKey(TAG_PICTURE_I)) {
                continue;
            }
            itemTag.removeTag(TAG_PICTURE_I);
            if (itemTag.hasNoTags()) {
                frame.setTagCompound(null);
            }
            break;
        }
        return list;
    }
    
    @Override
    public boolean isDynamic() {
        return true;
    }
}
