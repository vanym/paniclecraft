package com.vanym.paniclecraft.recipe;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.item.ItemPaintingFrame;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipePaintingFrameRemovePainting extends ShapelessOreRecipe {
    
    public RecipePaintingFrameRemovePainting() {
        super(Core.instance.painting.itemPainting,
              ItemPaintingFrame.getItemWithEmptyPictures(ItemPaintingFrame.FRONT));
    }
    
    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        if (!super.matches(inv, world)) {
            return false;
        }
        ItemStack frame = RecipeUtils.findItem(inv, Core.instance.painting.itemPaintingFrame);
        if (!frame.hasTagCompound()) {
            return false;
        }
        NBTTagCompound itemTag = frame.getTagCompound();
        for (ForgeDirection pside : ItemPaintingFrame.SIDE_ORDER) {
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
        ItemStack frame = RecipeUtils.findItem(inv, Core.instance.painting.itemPaintingFrame);
        if (frame == null || !frame.hasTagCompound()) {
            return painting;
        }
        NBTTagCompound itemTag = frame.getTagCompound();
        NBTTagCompound pictureTag = null;
        for (ForgeDirection pside : ItemPaintingFrame.SIDE_ORDER) {
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
    
    @SubscribeEvent
    public void itemCraftedEvent(ItemCraftedEvent event) {
        // Can't use (without dirty hacks) 'ContainerItem' because of RecipePaintingFrameAddPainting
        if (event.player == null) {
            return;
        }
        World world = event.player.getEntityWorld();
        if (!(event.craftMatrix instanceof InventoryCrafting)) {
            return;
        }
        InventoryCrafting inv = (InventoryCrafting)event.craftMatrix;
        if (!this.matches(inv, world)) {
            return;
        }
        ItemStack frame = RecipeUtils.findItem(inv, Core.instance.painting.itemPaintingFrame);
        if (frame == null || !frame.hasTagCompound()) {
            return;
        }
        NBTTagCompound itemTag = frame.getTagCompound();
        for (ForgeDirection pside : ItemPaintingFrame.SIDE_ORDER) {
            final String TAG_PICTURE_I = ItemPaintingFrame.getPictureTag(pside);
            if (!itemTag.hasKey(TAG_PICTURE_I)) {
                continue;
            }
            if (frame.stackSize == 1) {
                itemTag.removeTag(TAG_PICTURE_I);
                if (itemTag.hasNoTags()) {
                    frame.setTagCompound(null);
                }
                ++frame.stackSize;
            } else if (frame.stackSize > 1) {
                ItemStack copy = frame.copy();
                copy.stackSize = 1;
                NBTTagCompound copyItemTag = copy.getTagCompound();
                copyItemTag.removeTag(TAG_PICTURE_I);
                if (copyItemTag.hasNoTags()) {
                    copy.setTagCompound(null);
                }
                boolean added = event.player.inventory.addItemStackToInventory(copy);
                if (!added) {
                    event.player.dropPlayerItemWithRandomChoice(copy, false);
                }
            }
            break;
        }
    }
}
