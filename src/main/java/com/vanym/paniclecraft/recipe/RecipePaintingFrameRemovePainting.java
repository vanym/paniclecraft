package com.vanym.paniclecraft.recipe;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class RecipePaintingFrameRemovePainting extends ShapelessRecipe {
    
    protected final Direction[] removeOrder;
    
    public RecipePaintingFrameRemovePainting(ResourceLocation id, Direction[] removeOrder) {
        this(id, removeOrder, removeOrder.length > 0 ? removeOrder[0] : null);
    }
    
    protected RecipePaintingFrameRemovePainting(
            ResourceLocation id,
            Direction[] removeOrder,
            Direction first) {
        super(id, "", new ItemStack(Core.instance.painting.itemPainting),
              NonNullList.from(Ingredient.EMPTY,
                               Optional.of(ItemPaintingFrame.getItemWithEmptyPictures(first))
                                       .map(Ingredient::fromStacks)
                                       .get()));
        this.removeOrder = Arrays.copyOf(removeOrder, removeOrder.length);
    }
    
    @Override
    public boolean matches(CraftingInventory inv, World world) {
        if (!super.matches(inv, world)) {
            return false;
        }
        ItemStack frame = InventoryUtils.findItem(inv, Core.instance.painting.itemPaintingFrame);
        return Arrays.stream(this.removeOrder)
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
        CompoundNBT pictureTag = Arrays.stream(this.removeOrder)
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
        for (Direction side : this.removeOrder) {
            if (ItemPaintingFrame.removePictureTag(frame, side).isPresent()) {
                break;
            }
        }
        return list;
    }
    
    @Override
    public boolean isDynamic() {
        return false; // we want to show this recipe
    }
    
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Core.instance.painting.recipeTypePaintingFrameRemove;
    }
    
    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
            implements
                IRecipeSerializer<RecipePaintingFrameRemovePainting> {
        
        @Override
        public RecipePaintingFrameRemovePainting read(ResourceLocation recipeId, JsonObject json) {
            JsonArray order = JSONUtils.getJsonArray(json, "order");
            Direction[] removeOrder = IntStream.range(0, order.size())
                                               .limit(18)
                                               .mapToObj(i->RecipeUtils.getSide(order, "order", i))
                                               .toArray(Direction[]::new);
            return new RecipePaintingFrameRemovePainting(recipeId, removeOrder);
        }
        
        @Override
        public RecipePaintingFrameRemovePainting read(
                ResourceLocation recipeId,
                PacketBuffer buffer) {
            return new RecipePaintingFrameRemovePainting(
                    recipeId,
                    Arrays.stream(buffer.readVarIntArray(18))
                          .mapToObj(Direction::byIndex)
                          .toArray(Direction[]::new));
        }
        
        @Override
        public void write(PacketBuffer buffer, RecipePaintingFrameRemovePainting recipe) {
            buffer.writeVarIntArray(Arrays.stream(recipe.removeOrder)
                                          .mapToInt(Direction::getIndex)
                                          .toArray());
        }
    }
}
