package com.vanym.paniclecraft.recipe;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.inventory.InventoryUtils;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.item.ItemPaintingFrame;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class RecipePaintingFrameRemovePainting extends ShapelessRecipe {
    
    public RecipePaintingFrameRemovePainting(ResourceLocation id) {
        super(id, "", new ItemStack(Core.instance.painting.itemPainting),
              NonNullList.from(Ingredient.EMPTY,
                               Ingredient.fromStacks(ItemPaintingFrame.getItemWithEmptyPictures(ItemPaintingFrame.FRONT))));
    }
    
    @Override
    public boolean matches(CraftingInventory inv, World world) {
        if (!super.matches(inv, world)) {
            return false;
        }
        ItemStack frame = InventoryUtils.findItem(inv, Core.instance.painting.itemPaintingFrame);
        if (!frame.hasTag()) {
            return false;
        }
        CompoundNBT itemTag = frame.getTag();
        for (Direction pside : ItemPaintingFrame.SIDE_ORDER) {
            final String TAG_PICTURE_I = ItemPaintingFrame.getPictureTag(pside);
            if (itemTag.contains(TAG_PICTURE_I)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack painting = super.getCraftingResult(inv);
        ItemStack frame = InventoryUtils.findItem(inv, Core.instance.painting.itemPaintingFrame);
        if (!frame.hasTag()) {
            return painting;
        }
        CompoundNBT itemTag = frame.getTag();
        CompoundNBT pictureTag = null;
        for (Direction pside : ItemPaintingFrame.SIDE_ORDER) {
            final String TAG_PICTURE_I = ItemPaintingFrame.getPictureTag(pside);
            if (itemTag.contains(TAG_PICTURE_I)) {
                pictureTag = itemTag.getCompound(TAG_PICTURE_I);
                break;
            }
        }
        if (pictureTag == null || pictureTag.isEmpty()) {
            return painting;
        }
        CompoundNBT paintingItemTag = painting.getOrCreateTag();
        CompoundNBT paintingItemPictureTag = (CompoundNBT)pictureTag.copy();
        if (paintingItemPictureTag.contains(Picture.TAG_NAME)) {
            painting.setDisplayName(new StringTextComponent(
                    paintingItemPictureTag.getString(Picture.TAG_NAME)));
            paintingItemPictureTag.remove(Picture.TAG_NAME);
        }
        paintingItemTag.put(ItemPainting.TAG_PICTURE, paintingItemPictureTag);
        return painting;
    }
    
    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
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
        if (!frame.hasTag()) {
            return super.getRemainingItems(inv);
        }
        CompoundNBT itemTag = frame.getTag();
        for (Direction pside : ItemPaintingFrame.SIDE_ORDER) {
            final String TAG_PICTURE_I = ItemPaintingFrame.getPictureTag(pside);
            if (!itemTag.contains(TAG_PICTURE_I)) {
                continue;
            }
            itemTag.remove(TAG_PICTURE_I);
            if (itemTag.isEmpty()) {
                frame.setTag(null);
            }
            break;
        }
        return list;
    }
    
    @Override
    public boolean isDynamic() {
        return true;
    }
    
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Core.instance.painting.recipeTypePaintingFrameRemove;
    }
}
