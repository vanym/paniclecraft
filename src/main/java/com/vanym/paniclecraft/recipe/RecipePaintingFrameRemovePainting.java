package com.vanym.paniclecraft.recipe;

import java.util.Arrays;
import java.util.Optional;

import com.vanym.paniclecraft.Core;
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
    
    protected final EnumFacing[] removeOrder;
    
    public RecipePaintingFrameRemovePainting() {
        this(ItemPaintingFrame.SideName.stream()
                                       .map(ItemPaintingFrame.SideName::getSide)
                                       .toArray(EnumFacing[]::new));
    }
    
    protected RecipePaintingFrameRemovePainting(EnumFacing[] removeOrder) {
        super(Core.instance.painting.itemPainting,
              ItemPaintingFrame.getItemWithEmptyPictures(ItemPaintingFrame.SideName.FRONT.getSide()));
        this.removeOrder = Arrays.copyOf(removeOrder, removeOrder.length);
    }
    
    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        if (!super.matches(inv, world)) {
            return false;
        }
        ItemStack frame = InventoryUtils.findItem(inv, Core.instance.painting.itemPaintingFrame);
        return Arrays.stream(this.removeOrder)
                     .map(side->ItemPaintingFrame.getPictureTag(frame, side))
                     .anyMatch(Optional::isPresent);
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack painting = super.getCraftingResult(inv);
        ItemStack frame = InventoryUtils.findItem(inv, Core.instance.painting.itemPaintingFrame);
        if (!frame.hasTagCompound()) {
            return painting;
        }
        NBTTagCompound pictureTag = Arrays.stream(this.removeOrder)
                                          .map(side->ItemPaintingFrame.getPictureTag(frame, side))
                                          .filter(Optional::isPresent)
                                          .map(Optional::get)
                                          .findFirst()
                                          .orElse(null);
        if (pictureTag == null || pictureTag.hasNoTags()) {
            return painting;
        }
        ItemPainting.putPictureTag(painting, (NBTTagCompound)pictureTag.copy());
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
        for (EnumFacing side : this.removeOrder) {
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
}
