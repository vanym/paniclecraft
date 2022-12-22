package com.vanym.paniclecraft.recipe;

import java.util.Arrays;
import java.util.List;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.block.BlockPaintingFrame;
import com.vanym.paniclecraft.item.ItemPainting;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipeRemovePaintingFromFrame extends ShapelessOreRecipe {
    
    protected static final List<ForgeDirection> REMOVE_ORDER =
            Arrays.asList(RecipeAddPaintingToFrame.FRONT, RecipeAddPaintingToFrame.RIGHT,
                          RecipeAddPaintingToFrame.TOP, RecipeAddPaintingToFrame.LEFT,
                          RecipeAddPaintingToFrame.BACK, RecipeAddPaintingToFrame.BOTTOM);
    
    public RecipeRemovePaintingFromFrame() {
        super(Core.instance.painting.itemPainting,
              Core.instance.painting.blockPaintingFrame.getItemWithEmptyPictures(BlockPaintingFrame.FRONT_SIDE));
    }
    
    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        if (!super.matches(inv, world)) {
            return false;
        }
        ItemStack frame = RecipeUtils.findItem(inv, Core.instance.painting.blockPaintingFrame);
        if (!frame.hasTagCompound()) {
            return false;
        }
        NBTTagCompound itemTag = frame.getTagCompound();
        for (ForgeDirection pside : REMOVE_ORDER) {
            final String TAG_PICTURE_I = BlockPaintingFrame.getPictureTag(pside);
            if (itemTag.hasKey(TAG_PICTURE_I)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack painting = super.getCraftingResult(inv);
        ItemStack frame = RecipeUtils.findItem(inv, Core.instance.painting.blockPaintingFrame);
        if (frame == null || !frame.hasTagCompound()) {
            return painting;
        }
        NBTTagCompound itemTag = frame.getTagCompound();
        NBTTagCompound pictureTag = null;
        for (ForgeDirection pside : REMOVE_ORDER) {
            final String TAG_PICTURE_I = BlockPaintingFrame.getPictureTag(pside);
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
        paintingItemTag.setTag(ItemPainting.TAG_PICTURE, pictureTag);
        return painting;
    }
    
    @SubscribeEvent
    public void itemCraftedEvent(ItemCraftedEvent event) {
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
        ItemStack frame = RecipeUtils.findItem(inv, Core.instance.painting.blockPaintingFrame);
        if (frame == null || !frame.hasTagCompound()) {
            return;
        }
        NBTTagCompound itemTag = frame.getTagCompound();
        for (ForgeDirection pside : REMOVE_ORDER) {
            final String TAG_PICTURE_I = BlockPaintingFrame.getPictureTag(pside);
            if (!itemTag.hasKey(TAG_PICTURE_I)) {
                continue;
            }
            if (frame.stackSize == 1) {
                itemTag.removeTag(TAG_PICTURE_I);
                ++frame.stackSize;
            } else if (frame.stackSize > 1) {
                ItemStack copy = frame.copy();
                copy.stackSize = 1;
                copy.getTagCompound().removeTag(TAG_PICTURE_I);
                boolean added = event.player.inventory.addItemStackToInventory(copy);
                if (!added && !world.isRemote) {
                    EntityItem entityItem = new EntityItem(
                            world,
                            event.player.posX,
                            event.player.posY,
                            event.player.posZ,
                            copy);
                    entityItem.delayBeforeCanPickup = 0;
                    world.spawnEntityInWorld(entityItem);
                }
            }
            break;
        }
    }
}