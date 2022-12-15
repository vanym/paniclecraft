package com.vanym.paniclecraft.core.component.painting;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.IPaintingTool.PaintingToolType;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTBase.NBTPrimitive;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;

public class Picture {
    
    protected IPictureHolder holder;
    
    protected boolean editable = true;
    
    protected Image image;
    
    @SideOnly(Side.CLIENT)
    public int texture = -1;
    // unused on server side
    public boolean imageChangeProcessed = false;
    
    public Picture(IPictureHolder holder) {
        this.holder = holder;
        this.setSize(16, 16);
    }
    
    public Image getImage() {
        return this.image;
    }
    
    public void setSize(int width, int height) {
        this.image = new Image(width, height);
        this.image.fill(Core.instance.painting.DEFAULT_COLOR);
    }
    
    public boolean usePaintingTool(ItemStack itemStack, int x, int y) {
        if (itemStack == null) {
            return false;
        }
        Item item = itemStack.getItem();
        if (!(item instanceof IPaintingTool)) {
            return false;
        }
        IPaintingTool tool = (IPaintingTool)item;
        PaintingToolType toolType = tool.getPaintingToolType(itemStack);
        if (toolType == PaintingToolType.NONE) {
            return false;
        }
        Color color = tool.getPaintingToolColor(itemStack);
        if (toolType == PaintingToolType.BRUSH) {
            double radius = tool.getPaintingToolRadius(itemStack, this);
            Set<Picture> changedSet = new HashSet<>();
            this.setPixelsColor(x, y, radius, color, changedSet);
            for (Picture picture : changedSet) {
                picture.changed();
            }
        } else if (toolType == PaintingToolType.FILLER) {
            if (color != null && this.isEditableBy(this)) {
                if (this.image.fill(color)) {
                    this.changed();
                }
            }
        }
        return false;
    }
    
    protected boolean setPixelsColor(
            int x,
            int y,
            double radius,
            Color color,
            Set<Picture> changedSet) {
        if (radius == 0.0D) {
            return false;
        }
        int max = (int)Math.ceil(radius);
        boolean changed = false;
        for (int ix = 0; ix <= max; ix++) {
            for (int iy = 0; iy <= max; iy++) {
                if (ix * ix + iy * iy >= radius * radius) {
                    continue;
                }
                changed |= this.setPixelColor(x + ix, y + iy, color, changedSet);
                if (iy > 0) {
                    changed |= this.setPixelColor(x + ix, y - iy, color, changedSet);
                }
                if (ix > 0) {
                    changed |= this.setPixelColor(x - ix, y + iy, color, changedSet);
                    if (iy > 0) {
                        changed |= this.setPixelColor(x - ix, y - iy, color, changedSet);
                    }
                }
            }
        }
        return changed;
    }
    
    protected boolean setPixelColor(int x, int y, Color color, Set<Picture> changedSet) {
        int width = this.image.getWidth();
        int height = this.image.getHeight();
        int nx = x / width;
        int ny = y / height;
        int sx = x % width;
        int sy = y % height;
        if (sx < 0) {
            sx = width + sx;
            --nx;
        }
        if (sy < 0) {
            sy = height + sy;
            --ny;
        }
        Picture picture = this.holder.getNeighborPicture(nx, ny);
        if (picture == null || !picture.isEditableBy(this)) {
            return false;
        }
        if (picture.image.setPixelColor(sx, sy, color)) {
            changedSet.add(picture);
            return true;
        }
        return false;
    }
    
    public boolean isEditableBy(Picture picture) {
        if (!this.editable || !this.image.isSameSize(picture.image)) {
            return false;
        }
        // TODO brush check
        return true;
    }
    
    protected void changed() {
        this.imageChangeProcessed = false;
        this.holder.update();
    }
    
    protected static final String TAG_EDITABLE = "Editable";
    protected static final String TAG_IMAGE = "Image";
    protected static final String TAG_IMAGE_WIDTH = "w";
    protected static final String TAG_IMAGE_HEIGHT = "h";
    protected static final String TAG_IMAGE_RAWDATA = "raw";
    
    public void writeToNBT(NBTTagCompound nbtTag) {
        nbtTag.setBoolean(TAG_EDITABLE, this.editable);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean ok = ImageUtils.writePng(this.image, out);
        byte[] png = out.toByteArray();
        if (ok && png.length < (this.image.data.length + 16)) {
            nbtTag.setByteArray(TAG_IMAGE, png);
        } else {
            NBTTagCompound nbtImageTag = new NBTTagCompound();
            nbtImageTag.setInteger(TAG_IMAGE_WIDTH, this.image.getWidth());
            nbtImageTag.setInteger(TAG_IMAGE_HEIGHT, this.image.getHeight());
            nbtImageTag.setByteArray(TAG_IMAGE_RAWDATA, this.image.getData());
            nbtTag.setTag(TAG_IMAGE, nbtImageTag);
        }
    }
    
    public void readFromNBT(NBTTagCompound nbtTag) {
        if (nbtTag.hasKey(TAG_EDITABLE)) {
            this.editable = nbtTag.getBoolean(TAG_EDITABLE);
        }
        // TODO remove migrate hack
        if (nbtTag.hasKey("PicPng")) {
            nbtTag.setTag(TAG_IMAGE, nbtTag.getTag("PicPng"));
        }
        if (nbtTag.hasKey(TAG_IMAGE)) {
            NBTBase nbtImage = nbtTag.getTag(TAG_IMAGE);
            if (nbtImage instanceof NBTTagByteArray) {
                NBTTagByteArray nbtImageBytes = (NBTTagByteArray)nbtImage;
                byte[] img = nbtImageBytes.func_150292_c();
                ByteArrayInputStream in = new ByteArrayInputStream(img);
                Image image = ImageUtils.readImage(in);
                if (image != null) {
                    this.image = image;
                }
            } else if (nbtImage instanceof NBTTagCompound) {
                NBTTagCompound nbtImageTag = (NBTTagCompound)nbtImage;
                int width = nbtImageTag.getInteger(TAG_IMAGE_WIDTH);
                int height = nbtImageTag.getInteger(TAG_IMAGE_HEIGHT);
                NBTBase nbtImageRaw = nbtImageTag.getTag(TAG_IMAGE_RAWDATA);
                if (nbtImageRaw instanceof NBTTagByteArray) {
                    NBTTagByteArray nbtImageRawBytes = (NBTTagByteArray)nbtImageRaw;
                    byte[] data = nbtImageRawBytes.func_150292_c();
                    this.image = new Image(width, height, data);
                } else {
                    this.setSize(width, height);
                }
            } else if (nbtImage instanceof NBTBase.NBTPrimitive) {
                NBTBase.NBTPrimitive nbtPrim = (NBTPrimitive)nbtImage;
                int rowSize = nbtPrim.func_150287_d();
                this.setSize(rowSize, rowSize);
            }
            this.imageChangeProcessed = false;
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void invalidate() {
        if (this.texture >= 0) {
            com.vanym.paniclecraft.client.ClientProxy.deleteTexture(this.texture);
            this.texture = -1;
        }
    }
}
