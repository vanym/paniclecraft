package com.vanym.paniclecraft.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.block.BlockPaintingFrame;
import com.vanym.paniclecraft.core.component.painting.MatrixUtils;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RecipeAddPaintingToFrame extends ShapedOreRecipe {
    
    protected final ForgeDirection side;
    
    protected RecipeAddPaintingToFrame(ForgeDirection pside, int offsetX, int offsetY) {
        super(Core.instance.painting.blockPaintingFrame.getItemWithEmptyPictures(pside),
              getRecipe(offsetX, offsetY));
        this.side = pside;
    }
    
    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        if (!super.matches(inv, world)) {
            return false;
        }
        ItemStack frame = RecipeUtils.findItem(inv, Core.instance.painting.blockPaintingFrame);
        if (frame.hasTagCompound()) {
            NBTTagCompound itemTag = frame.getTagCompound();
            if (itemTag.hasKey(BlockPaintingFrame.getPictureTag(this.side))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack frame = super.getCraftingResult(inv);
        ItemStack inputFrame = RecipeUtils.findItem(inv, Core.instance.painting.blockPaintingFrame);
        ItemStack painting = RecipeUtils.findItem(inv, Core.instance.painting.itemPainting);
        if (inputFrame.hasTagCompound()) {
            frame.setTagCompound((NBTTagCompound)inputFrame.getTagCompound().copy());
        }
        RecipeUtils.addPainting(frame, painting, this.side);
        return frame;
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
        list.addAll(Arrays.asList(Character.valueOf('f'),
                                  Core.instance.painting.blockPaintingFrame));
        list.addAll(Arrays.asList(Character.valueOf('p'),
                                  Core.instance.painting.itemPainting));
        return list.toArray();
    }
    
    public static List<IRecipe> createAllVariants() {
        ForgeDirection front = BlockPaintingFrame.FRONT_SIDE;
        ForgeDirection left = front.getRotation(ForgeDirection.UP);
        ForgeDirection back = left.getRotation(ForgeDirection.UP);
        ForgeDirection right = back.getRotation(ForgeDirection.UP);
        return Arrays.asList(new RecipeAddPaintingToFrame(front, -1, +1),
                             new RecipeAddPaintingToFrame(back, +1, -1),
                             new RecipeAddPaintingToFrame(left, -1, -1),
                             new RecipeAddPaintingToFrame(right, +1, +1),
                             new RecipeAddPaintingToFrame(ForgeDirection.DOWN, +0, +1),
                             new RecipeAddPaintingToFrame(ForgeDirection.UP, +0, -1));
    }
}
