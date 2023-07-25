package com.vanym.paniclecraft.recipe;

import java.util.Arrays;
import java.util.Optional;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.inventory.InventoryUtils;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.item.ItemPaintingFrame;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipePaintingFrameRemovePainting extends ShapelessOreRecipe {
    
    protected final ForgeDirection[] removeOrder;
    
    public RecipePaintingFrameRemovePainting() {
        this(ItemPaintingFrame.SideName.stream()
                                       .map(ItemPaintingFrame.SideName::getSide)
                                       .toArray(ForgeDirection[]::new));
    }
    
    protected RecipePaintingFrameRemovePainting(ForgeDirection[] removeOrder) {
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
        if (frame == null || !frame.hasTagCompound()) {
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
        ItemStack frame = InventoryUtils.findItem(inv, Core.instance.painting.itemPaintingFrame);
        Arrays.stream(this.removeOrder)
              .filter(side->ItemPaintingFrame.getPictureTag(frame, side).isPresent())
              .findFirst()
              .ifPresent(pside->removePainting(event.player, frame, pside));
    }
    
    protected static void removePainting(
            EntityPlayer player,
            ItemStack frame,
            ForgeDirection pside) {
        if (frame.stackSize == 1) {
            ItemPaintingFrame.removePictureTag(frame, pside);
            ++frame.stackSize;
        } else if (frame.stackSize > 1) {
            ItemStack copy = frame.copy();
            copy.stackSize = 1;
            ItemPaintingFrame.removePictureTag(copy, pside);
            boolean added = player.inventory.addItemStackToInventory(copy);
            if (!added) {
                player.dropPlayerItemWithRandomChoice(copy, false);
            }
        }
    }
}
