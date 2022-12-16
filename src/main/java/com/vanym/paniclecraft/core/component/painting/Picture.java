package com.vanym.paniclecraft.core.component.painting;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
    
    protected byte[] packed;
    protected int packedWidth;
    protected int packedHeight;
    
    @SideOnly(Side.CLIENT)
    public int texture = -1;
    // unused on server side
    public boolean imageChangeProcessed = false;
    
    public Picture(IPictureHolder holder) {
        this.holder = holder;
        this.setSize(16, 16);
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
                picture.imageChanged();
                picture.update();
            }
        } else if (toolType == PaintingToolType.FILLER) {
            if (color != null && this.isEditableBy(this)) {
                if (this.image.fill(color)) {
                    this.packed = null;
                    this.imageChanged();
                    this.update();
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
        int width = this.getWidth();
        int height = this.getHeight();
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
        if (picture.setMyPixelColor(sx, sy, color)) {
            changedSet.add(picture);
            return true;
        }
        return false;
    }
    
    protected boolean setMyPixelColor(int x, int y, Color color) {
        if (!this.unpack()) {
            return false;
        }
        if (this.image.setPixelColor(x, y, color)) {
            // packed become outdated, removing it
            this.packed = null;
            return true;
        }
        return false;
    }
    
    public boolean isEditableBy(Picture picture) {
        if (!this.editable || !this.isSameSize(picture)) {
            return false;
        }
        // TODO brush check
        return true;
    }
    
    public boolean isSameSize(Picture picture) {
        return (this.getWidth() == picture.getWidth()) && (this.getHeight() == picture.getHeight());
    }
    
    protected void imageChanged() {
        this.imageChangeProcessed = false;
    }
    
    protected void update() {
        this.holder.update();
    }
    
    protected Image getImage() {
        return this.image;
    }
    
    public int getWidth() {
        if (this.image != null) {
            return this.image.getWidth();
        } else if (this.packed != null) {
            return this.packedWidth;
        } else {
            return 0;
        }
    }
    
    public int getHeight() {
        if (this.image != null) {
            return this.image.getHeight();
        } else if (this.packed != null) {
            return this.packedHeight;
        } else {
            return 0;
        }
    }
    
    @SideOnly(Side.CLIENT)
    public ByteBuffer getImageAsDirectByteBuffer() {
        if (this.image != null) {
            byte[] data = this.image.getData();
            ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
            buffer.order(ByteOrder.nativeOrder());
            buffer.clear();
            buffer.put(data);
            buffer.flip();
            return buffer;
        }
        if (this.packed == null) {
            return null;
        }
        ByteArrayInputStream in = new ByteArrayInputStream(this.packed);
        try {
            return ImageUtils.readImageToDirectByteBuffer(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    protected boolean unpack() {
        if (this.image != null) {
            return true;
        }
        if (this.packed == null) {
            return false;
        }
        ByteArrayInputStream in = new ByteArrayInputStream(this.packed);
        Image image = ImageUtils.readImage(in);
        if (image != null && this.packedWidth == image.getWidth()
            && this.packedHeight == image.getHeight()) {
            this.image = image;
            return true;
        } else {
            // packed invalid, removing it
            this.packed = null;
            return false;
        }
    }
    
    protected boolean pack() {
        if (this.packed != null) {
            return true;
        }
        if (this.image == null) {
            return false;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean ok = ImageUtils.writePng(this.image, out);
        if (!ok) {
            return false;
        }
        this.packed = out.toByteArray();
        this.packedWidth = this.image.getWidth();
        this.packedHeight = this.image.getHeight();
        return true;
    }
    
    public Color getPixelColor(int px, int py) {
        if (!this.unpack()) {
            return Core.instance.painting.DEFAULT_COLOR;
        }
        return this.image.getPixelColor(px, py);
    }
    
    protected void setSize(int width, int height) {
        this.image = new Image(width, height);
        this.image.fill(Core.instance.painting.DEFAULT_COLOR);
        this.packed = null;
    }
    
    protected boolean rotate(int angle) {
        if (!this.unpack()) {
            return false;
        }
        switch (angle) {
            case 90:
                this.image.rotate90();
            break;
            case 180:
                this.image.rotate180();
            break;
            case 270:
                this.image.rotate270();
            break;
            default:
                return false;
        }
        // packed become outdated, removing it
        this.packed = null;
        this.imageChanged();
        this.update();
        return true;
    }
    
    public boolean rotate90() {
        return this.rotate(90);
    }
    
    public boolean rotate180() {
        return this.rotate(180);
    }
    
    public boolean rotate270() {
        return this.rotate(270);
    }
    
    protected static final String TAG_EDITABLE = "Editable";
    protected static final String TAG_IMAGE = "Image";
    protected static final String TAG_IMAGE_WIDTH = "Width";
    protected static final String TAG_IMAGE_HEIGHT = "Height";
    protected static final String TAG_IMAGE_RAWDATA = "Raw";
    protected static final String TAG_IMAGE_PACKED = "Packed";
    
    public void writeToNBT(NBTTagCompound nbtTag) {
        nbtTag.setBoolean(TAG_EDITABLE, this.editable);
        NBTTagCompound nbtImageTag = new NBTTagCompound();
        if (this.pack()) {
            nbtImageTag.setByteArray(TAG_IMAGE_PACKED, this.packed);
            nbtImageTag.setInteger(TAG_IMAGE_WIDTH, this.packedWidth);
            nbtImageTag.setInteger(TAG_IMAGE_HEIGHT, this.packedHeight);
        } else if (this.image != null) {
            nbtImageTag.setInteger(TAG_IMAGE_WIDTH, this.image.getWidth());
            nbtImageTag.setInteger(TAG_IMAGE_HEIGHT, this.image.getHeight());
            nbtImageTag.setByteArray(TAG_IMAGE_RAWDATA, this.image.getData());
        }
        if (!nbtImageTag.hasNoTags()) {
            nbtTag.setTag(TAG_IMAGE, nbtImageTag);
        }
    }
    
    public void readFromNBT(NBTTagCompound nbtTag) {
        if (nbtTag.hasKey(TAG_EDITABLE)) {
            this.editable = nbtTag.getBoolean(TAG_EDITABLE);
        }
        int width = 0;
        int height = 0;
        byte[] packed = null;
        byte[] raw = null;
        if (nbtTag.hasKey(TAG_IMAGE)) {
            NBTBase nbtImage = nbtTag.getTag(TAG_IMAGE);
            if (nbtImage instanceof NBTTagByteArray) {
                NBTTagByteArray nbtImageBytes = (NBTTagByteArray)nbtImage;
                packed = nbtImageBytes.func_150292_c();
            } else if (nbtImage instanceof NBTTagCompound) {
                NBTTagCompound nbtImageTag = (NBTTagCompound)nbtImage;
                width = nbtImageTag.getInteger(TAG_IMAGE_WIDTH);
                height = nbtImageTag.getInteger(TAG_IMAGE_HEIGHT);
                NBTBase nbtImageRaw = nbtImageTag.getTag(TAG_IMAGE_RAWDATA);
                if (nbtImageRaw instanceof NBTTagByteArray) {
                    NBTTagByteArray nbtImageRawBytes = (NBTTagByteArray)nbtImageRaw;
                    raw = nbtImageRawBytes.func_150292_c();
                }
                NBTBase nbtImagePacked = nbtImageTag.getTag(TAG_IMAGE_PACKED);
                if (nbtImagePacked instanceof NBTTagByteArray) {
                    NBTTagByteArray nbtImagePackedBytes = (NBTTagByteArray)nbtImagePacked;
                    packed = nbtImagePackedBytes.func_150292_c();
                }
            } else if (nbtImage instanceof NBTBase.NBTPrimitive) {
                NBTBase.NBTPrimitive nbtPrim = (NBTPrimitive)nbtImage;
                int rowSize = nbtPrim.func_150287_d();
                width = rowSize;
                height = rowSize;
            }
        }
        if (packed != null) {
            if (width > 0 && height > 0) {
                this.packed = packed;
                this.packedWidth = width;
                this.packedHeight = height;
                this.image = null;
                this.imageChanged();
            } else {
                ByteArrayInputStream in = new ByteArrayInputStream(packed);
                Image image = ImageUtils.readImage(in);
                if (image != null) {
                    this.image = image;
                    this.packed = packed;
                    this.packedWidth = image.getWidth();
                    this.packedHeight = image.getHeight();
                    this.imageChanged();
                }
            }
        } else if (width > 0 && height > 0) {
            if (raw != null) {
                this.image = new Image(width, height, raw);
            } else {
                this.setSize(width, height);
            }
            this.imageChanged();
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
