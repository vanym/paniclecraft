package com.vanym.paniclecraft.recipe;

import java.util.ArrayList;
import java.util.Collections;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.FixedPictureSize;
import com.vanym.paniclecraft.core.component.painting.IPictureSize;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RecipePaintingCombine extends RecipeRegister.ShapedOreRecipe {
    
    protected int sizeX;
    protected int sizeY;
    
    public RecipePaintingCombine(int sizeX, int sizeY) {
        super(getItemStack(sizeX, sizeY), getRecipe(sizeX, sizeY));
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }
    
    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        if (!super.matches(inv, world)) {
            return false;
        }
        Picture[][] pictures = getAsPictures(this.getItemMatrix(inv));
        int sizeHeight = 0;
        for (int y = 0; y < this.sizeY; ++y) {
            int height = pictures[y][0].getHeight();
            for (int x = 1; x < this.sizeX; ++x) {
                if (height != pictures[y][x].getHeight()) {
                    return false;
                }
            }
            sizeHeight += height;
        }
        if (sizeHeight > Core.instance.painting.config.paintingMaxCraftableHeight) {
            return false;
        }
        int sizeWidth = 0;
        for (int x = 0; x < this.sizeX; ++x) {
            int width = pictures[0][x].getWidth();
            for (int y = 1; y < this.sizeY; ++y) {
                if (width != pictures[y][x].getWidth()) {
                    return false;
                }
            }
            sizeWidth += width;
        }
        if (sizeWidth > Core.instance.painting.config.paintingMaxCraftableWidth) {
            return false;
        }
        return true;
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        Picture[][] pictures = getAsPictures(this.getItemMatrix(inv));
        Picture picture = Picture.merge(pictures);
        return ItemPainting.getPictureAsItem(picture);
    }
    
    protected ItemStack[][] getItemMatrix(InventoryCrafting inv) {
        int offsetX = 0, offsetY = 0;
        while (true) {
            ItemStack stack = inv.getStackInRowAndColumn(offsetX, offsetY);
            if (!stack.isEmpty()) {
                break;
            }
            switch (Integer.compare(offsetX, offsetY)) {
                case -1:
                    ++offsetX;
                    // fallthrough
                case 1:
                    // swap
                    int tmp = offsetX;
                    offsetX = offsetY;
                    offsetY = tmp;
                break;
                case 0:
                    ++offsetX;
                    offsetY = 0;
                break;
            }
        }
        ItemStack[][] output = new ItemStack[this.sizeY][this.sizeX];
        for (int y = 0; y < output.length; y++) {
            ItemStack[] row = output[y];
            for (int x = 0; x < row.length; x++) {
                row[x] = inv.getStackInRowAndColumn(x + offsetX, y + offsetY);
            }
        }
        return output;
    }
    
    @Override
    public boolean isDynamic() {
        return false; // we want to show this recipe
    }
    
    protected static Picture[][] getAsPictures(ItemStack[][] stacks) {
        Picture[][] sizes = new Picture[stacks.length][];
        for (int y = 0; y < stacks.length; ++y) {
            ItemStack[] substacks = stacks[y];
            Picture[] subsizes = sizes[y] = new Picture[substacks.length];
            for (int x = 0; x < substacks.length; ++x) {
                Picture picture = new Picture(Core.instance.painting.config.paintingDefaultSize);
                ItemPainting.fillPicture(picture, substacks[x]);
                subsizes[x] = picture;
            }
        }
        return sizes;
    }
    
    protected static IPictureSize getDummySize() {
        return new FixedPictureSize(16);
    }
    
    protected static ItemStack getItemStack(int sizeX, int sizeY) {
        IPictureSize size = getDummySize();
        return ItemPainting.getSizedItem(sizeX * size.getWidth(), sizeY * size.getHeight());
    }
    
    protected static Object[] getRecipe(int sizeX, int sizeY) {
        IPictureSize size = getDummySize();
        ArrayList<Object> list = new ArrayList<>();
        list.add(false); // mirrored
        list.addAll(Collections.nCopies(sizeY, String.join("", Collections.nCopies(sizeX, "p"))));
        list.add(Character.valueOf('p'));
        list.add(ItemPainting.getSizedItem(size));
        return list.toArray();
    }
}
