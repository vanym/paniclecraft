package com.vanym.paniclecraft.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.MatrixUtils;
import com.vanym.paniclecraft.inventory.InventoryUtils;
import com.vanym.paniclecraft.item.ItemPaintingFrame;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class RecipePaintingFrameAddPainting extends RecipeRegister.ShapedOreRecipe {
    
    protected final EnumFacing side;
    
    protected RecipePaintingFrameAddPainting(EnumFacing pside, int offsetX, int offsetY) {
        super(ItemPaintingFrame.getItemWithEmptyPictures(pside),
              getRecipe(offsetX, offsetY));
        this.side = pside;
    }
    
    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        if (!super.matches(inv, world)) {
            return false;
        }
        ItemStack frame = InventoryUtils.findItem(inv, Core.instance.painting.itemPaintingFrame);
        if (frame.hasTagCompound()) {
            NBTTagCompound itemTag = frame.getTagCompound();
            if (itemTag.hasKey(ItemPaintingFrame.getPictureTag(this.side))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack frame = super.getCraftingResult(inv);
        ItemStack inputFrame =
                InventoryUtils.findItem(inv, Core.instance.painting.itemPaintingFrame);
        ItemStack painting = InventoryUtils.findItem(inv, Core.instance.painting.itemPainting);
        if (inputFrame.hasTagCompound()) {
            frame.setTagCompound((NBTTagCompound)inputFrame.getTagCompound().copy());
        }
        RecipeUtils.addPainting(frame, painting, this.side);
        return frame;
    }
    
    @Override
    public boolean isDynamic() {
        return true;
    }
    
    protected static Object[] getRecipe(int offsetX, int offsetY) {
        ArrayList<Object> list = new ArrayList<>();
        list.add(false); // mirrored
        int sizeX = Math.abs(offsetX) + 1;
        int sizeY = Math.abs(offsetY) + 1;
        byte[] input = new byte[sizeX * sizeY];
        Arrays.fill(input, (byte)' ');
        input[input.length - 1] = 'p';
        input[0] = 'f';
        if (offsetX < 0) {
            MatrixUtils.flipH(input, sizeX, 1);
        }
        if (offsetY < 0) {
            MatrixUtils.flipV(input, sizeX, 1);
        }
        for (int y = 0; y < sizeY; ++y) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < sizeX; x++) {
                sb.append((char)input[y * sizeX + x]);
            }
            list.add(sb.toString());
        }
        list.add(Character.valueOf('f'));
        list.add(new ItemStack(Core.instance.painting.itemPaintingFrame));
        list.add(Character.valueOf('p'));
        list.add(new ItemStack(Core.instance.painting.itemPainting));
        return list.toArray();
    }
    
    public static List<IRecipe> createAllVariants() {
        return Arrays.asList(create(ItemPaintingFrame.FRONT, -1, +1),
                             create(ItemPaintingFrame.BACK, +1, -1),
                             create(ItemPaintingFrame.LEFT, -1, -1),
                             create(ItemPaintingFrame.RIGHT, +1, +1),
                             create(ItemPaintingFrame.BOTTOM, +0, +1),
                             create(ItemPaintingFrame.TOP, +0, -1));
    }
    
    protected static IRecipe create(EnumFacing pside, int offsetX, int offsetY) {
        RecipePaintingFrameAddPainting recipe =
                new RecipePaintingFrameAddPainting(pside, offsetX, offsetY);
        RecipeRegister.flowRegistryName(recipe, "%s_add_painting_" + pside);
        return recipe;
    }
}
