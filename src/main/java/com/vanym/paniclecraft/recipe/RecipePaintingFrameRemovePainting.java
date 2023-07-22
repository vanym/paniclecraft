package com.vanym.paniclecraft.recipe;

import java.util.Arrays;
import java.util.Optional;

import com.vanym.paniclecraft.Core;
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
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class RecipePaintingFrameRemovePainting extends ShapelessRecipe {
    
    public RecipePaintingFrameRemovePainting(ResourceLocation id) {
        super(id, "", new ItemStack(Core.instance.painting.itemPainting),
              NonNullList.from(Ingredient.EMPTY,
                               Optional.of(ItemPaintingFrame.SideName.FRONT)
                                       .map(ItemPaintingFrame.SideName::getSide)
                                       .map(ItemPaintingFrame::getItemWithEmptyPictures)
                                       .map(Ingredient::fromStacks)
                                       .get()));
    }
    
    @Override
    public boolean matches(CraftingInventory inv, World world) {
        if (!super.matches(inv, world)) {
            return false;
        }
        ItemStack frame = InventoryUtils.findItem(inv, Core.instance.painting.itemPaintingFrame);
        return Arrays.stream(Direction.values())
                     .map(side->ItemPaintingFrame.getPictureTag(frame, side))
                     .anyMatch(Optional::isPresent);
    }
    
    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack painting = super.getCraftingResult(inv);
        ItemStack frame = InventoryUtils.findItem(inv, Core.instance.painting.itemPaintingFrame);
        if (!frame.hasTag()) {
            return painting;
        }
        CompoundNBT pictureTag =
                ItemPaintingFrame.SideName.stream()
                                          .map(ItemPaintingFrame.SideName::getSide)
                                          .map(side->ItemPaintingFrame.getPictureTag(frame, side))
                                          .filter(Optional::isPresent)
                                          .map(Optional::get)
                                          .findFirst()
                                          .orElse(null);
        if (pictureTag == null || pictureTag.isEmpty()) {
            return painting;
        }
        ItemPainting.putPictureTag(painting, pictureTag.copy());
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
        for (ItemPaintingFrame.SideName name : ItemPaintingFrame.SideName.values()) {
            if (ItemPaintingFrame.removePictureTag(frame, name.getSide()).isPresent()) {
                break;
            }
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
