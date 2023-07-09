package com.vanym.paniclecraft.core.component.painting;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.mojang.blaze3d.platform.TextureUtil;
import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.IPaintingTool.PaintingToolType;
import com.vanym.paniclecraft.utils.ColorUtils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ByteArrayNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public class Picture implements IPictureSize {
    
    protected IPictureHolder holder;
    protected boolean hasAlpha = false;
    
    protected boolean editable = true;
    protected String name;
    
    protected Image image;
    
    protected byte[] packed;
    protected int packedWidth;
    protected int packedHeight;
    
    @OnlyIn(Dist.CLIENT)
    public Integer texture;
    // unused on server side
    public boolean imageChangeProcessed = false;
    
    public Picture(IPictureSize size) {
        this(size, false);
    }
    
    public Picture(IPictureSize size, boolean hasAlpha) {
        this(null, hasAlpha, size);
    }
    
    public Picture(boolean hasAlpha) {
        this((IPictureHolder)null, hasAlpha);
    }
    
    public Picture(IPictureHolder holder) {
        this(holder, false);
    }
    
    public Picture(IPictureHolder holder, boolean hasAlpha) {
        this(holder, hasAlpha, holder != null ? holder.getDefaultSize() : new FixedPictureSize(1));
    }
    
    protected Picture(IPictureHolder holder, boolean hasAlpha, IPictureSize size) {
        this.holder = holder;
        this.hasAlpha = hasAlpha;
        this.setSize(size);
    }
    
    public Picture(Image image) {
        this(null, image);
    }
    
    public Picture(IPictureHolder holder, Image image) {
        this.holder = holder;
        this.hasAlpha = image.hasAlpha();
        this.image = image;
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
        if ((toolType == PaintingToolType.BRUSH || toolType == PaintingToolType.REMOVER)
            && this.holder != null) {
            Color color;
            if (toolType == PaintingToolType.REMOVER) {
                if (this.hasAlpha) {
                    color = new Color(0, 0, 0, 0);
                } else {
                    color = Core.instance.painting.DEFAULT_COLOR;
                }
            } else {
                color = tool.getPaintingToolColor(itemStack);
            }
            double radius = tool.getPaintingToolRadius(itemStack, this);
            Set<Picture> changedSet = new HashSet<>();
            boolean changed = this.setNeighborsPixelsColor(x, y, radius, color, changedSet);
            for (Picture picture : changedSet) {
                picture.imageChanged();
                picture.update();
            }
            return changed;
        } else if (toolType == PaintingToolType.FILLER && this.holder != null) {
            Color color = tool.getPaintingToolColor(itemStack);
            if (this.fill(color)) {
                return true;
            }
        } else if (toolType == PaintingToolType.COLORPICKER) {
            if (!(item instanceof IColorizeable)) {
                return false;
            }
            IColorizeable colorizeable = (IColorizeable)item;
            Color color = this.getPixelColor(x, y);
            if (color.getAlpha() == 0) {
                return false;
            }
            colorizeable.setColor(itemStack, ColorUtils.getAlphaless(color));
            return true;
        }
        return false;
    }
    
    protected boolean setNeighborsPixelsColor(
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
                changed |= this.setNeighborsPixelColor(x + ix, y + iy, color, changedSet);
                if (iy > 0) {
                    changed |= this.setNeighborsPixelColor(x + ix, y - iy, color, changedSet);
                }
                if (ix > 0) {
                    changed |= this.setNeighborsPixelColor(x - ix, y + iy, color, changedSet);
                    if (iy > 0) {
                        changed |= this.setNeighborsPixelColor(x - ix, y - iy, color, changedSet);
                    }
                }
            }
        }
        return changed;
    }
    
    protected boolean setNeighborsPixelColor(int x, int y, Color color, Set<Picture> changedSet) {
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
        Picture picture = this.getNeighborPicture(nx, ny);
        if (!this.canEdit(picture)) {
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
    
    public boolean setPixelColor(int x, int y, Color color) {
        if (this.isEditableBy(this) && this.setMyPixelColor(x, y, color)) {
            this.imageChanged();
            this.update();
            return true;
        }
        return false;
    }
    
    public boolean fill(Color color) {
        if (this.isEditableBy(this)
            && this.unpack()
            && this.image.fill(color)) {
            // packed become outdated, removing it
            this.packed = null;
            this.imageChanged();
            this.update();
            return true;
        }
        return false;
    }
    
    public boolean addPicture(int x, int y, Picture input) {
        if (!this.isEditable() || !this.unpack() || !input.unpack()) {
            return false;
        }
        if (this.image.addImage(x, y, input.image)) {
            this.packed = null;
            this.imageChanged();
            this.update();
            return true;
        }
        return false;
    }
    
    public boolean resize(int width, int height) {
        if (!this.isEditable()) {
            return false;
        }
        if (width == this.getWidth() && height == this.getHeight()) {
            return true;
        }
        if (!this.unpack()) {
            return false;
        }
        Image old = this.image;
        this.setSize(width, height);
        this.image.addImage(0, 0, old);
        this.imageChanged();
        this.update();
        return true;
    }
    
    public Picture getNeighborPicture(int offsetX, int offsetY) {
        if (offsetX == 0 && offsetY == 0) {
            return this;
        }
        return this.holder.getNeighborPicture(offsetX, offsetY);
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean isEditable() {
        return this.editable && this.name == null;
    }
    
    public boolean isEditableBy(Picture picture) {
        if (!this.isEditable() || !this.isSameSize(picture)) {
            return false;
        }
        return true;
    }
    
    public boolean canEdit(Picture picture) {
        return picture != null && picture.isEditableBy(this);
    }
    
    public boolean isSameSize(Picture picture) {
        return (this.getWidth() == picture.getWidth()) && (this.getHeight() == picture.getHeight());
    }
    
    public boolean isEmpty() {
        if (!this.hasAlpha || !this.unpack()) {
            return false;
        }
        return this.image.isEmpty();
    }
    
    protected void imageChanged() {
        this.imageChangeProcessed = false;
    }
    
    protected void update() {
        if (this.holder != null) {
            this.holder.update();
        }
    }
    
    public boolean hasAlpha() {
        return this.hasAlpha;
    }
    
    @Override
    public int getWidth() {
        if (this.image != null) {
            return this.image.getWidth();
        } else if (this.packed != null) {
            return this.packedWidth;
        } else {
            return 1;
        }
    }
    
    @Override
    public int getHeight() {
        if (this.image != null) {
            return this.image.getHeight();
        } else if (this.packed != null) {
            return this.packedHeight;
        } else {
            return 1;
        }
    }
    
    protected int getSize() {
        return this.getWidth() * this.getHeight() * Image.getPixelSize(this.hasAlpha);
    }
    
    @OnlyIn(Dist.CLIENT)
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
            ByteBuffer buffer = ImageUtils.readImageToDirectByteBuffer(in, this.hasAlpha);
            if (buffer.capacity() != this.getSize()) {
                return null;
            }
            return buffer;
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
        Image image = ImageUtils.readImage(in, this.hasAlpha);
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
    
    protected void setSize(IPictureSize size) {
        this.setSize(size.getWidth(), size.getHeight());
    }
    
    protected void setSize(int width, int height) {
        this.image = new Image(width, height, this.hasAlpha);
        if (!this.hasAlpha) {
            this.image.fill(Core.instance.painting.DEFAULT_COLOR);
        }
        this.packed = null;
    }
    
    public boolean rotate(int rot) {
        if ((rot == 0) || !this.unpack()) {
            return false;
        }
        switch (rot) {
            case 1:
                this.image.rotate90();
            break;
            case 2:
                this.image.rotate180();
            break;
            case 3:
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
    
    public void setName(String name) {
        if (name.isEmpty()) {
            name = null;
        }
        this.name = name;
    }
    
    @Override
    public String toString() {
        return String.format("Picture[width=%d, height=%d, alpha=%s, name=%s, editable=%s, packed=%s, raw=%s, holder=%s]",
                             this.getWidth(), this.getHeight(), this.hasAlpha,
                             this.name == null ? null : String.format("\"%s\"", this.name),
                             this.isEditable(), this.packed == null ? null : this.packed.length,
                             this.image == null ? null : this.image.getData().length, this.holder);
    }
    
    public static final String TAG_EDITABLE = "Editable";
    public static final String TAG_NAME = "Name";
    public static final String TAG_IMAGE = "Image";
    public static final String TAG_IMAGE_WIDTH = "Width";
    public static final String TAG_IMAGE_HEIGHT = "Height";
    public static final String TAG_IMAGE_RAWDATA = "Raw";
    public static final String TAG_IMAGE_PACKED = "Packed";
    
    public void writeToNBT(CompoundNBT nbtTag) {
        nbtTag.putBoolean(TAG_EDITABLE, this.editable);
        if (this.name != null) {
            nbtTag.putString(TAG_NAME, this.name);
        }
        CompoundNBT nbtImageTag = new CompoundNBT();
        this.writeImageToNBT(nbtImageTag);
        if (!nbtImageTag.isEmpty()) {
            nbtTag.put(TAG_IMAGE, nbtImageTag);
        }
    }
    
    public void readFromNBT(CompoundNBT nbtTag) {
        if (nbtTag.contains(TAG_EDITABLE)) {
            this.editable = nbtTag.getBoolean(TAG_EDITABLE);
        }
        if (nbtTag.contains(TAG_NAME)) {
            this.setName(nbtTag.getString(TAG_NAME));
        } else {
            this.name = null;
        }
        if (nbtTag.contains(TAG_IMAGE)) {
            INBT nbtImage = nbtTag.get(TAG_IMAGE);
            this.readImageFromNBT(nbtImage);
        }
    }
    
    public void writeImageToNBT(CompoundNBT nbtImageTag) {
        if (this.pack()) {
            nbtImageTag.putByteArray(TAG_IMAGE_PACKED, this.packed);
            nbtImageTag.putInt(TAG_IMAGE_WIDTH, this.packedWidth);
            nbtImageTag.putInt(TAG_IMAGE_HEIGHT, this.packedHeight);
        } else if (this.image != null) {
            nbtImageTag.putInt(TAG_IMAGE_WIDTH, this.image.getWidth());
            nbtImageTag.putInt(TAG_IMAGE_HEIGHT, this.image.getHeight());
            nbtImageTag.putByteArray(TAG_IMAGE_RAWDATA, this.image.getData());
        }
    }
    
    public void readImageFromNBT(INBT nbtImage) {
        int width = 0;
        int height = 0;
        byte[] packed = null;
        byte[] raw = null;
        if (nbtImage instanceof ByteArrayNBT) {
            ByteArrayNBT nbtImageBytes = (ByteArrayNBT)nbtImage;
            packed = nbtImageBytes.getByteArray();
        } else if (nbtImage instanceof CompoundNBT) {
            CompoundNBT nbtImageTag = (CompoundNBT)nbtImage;
            width = nbtImageTag.getInt(TAG_IMAGE_WIDTH);
            height = nbtImageTag.getInt(TAG_IMAGE_HEIGHT);
            INBT nbtImageRaw = nbtImageTag.get(TAG_IMAGE_RAWDATA);
            if (nbtImageRaw instanceof ByteArrayNBT) {
                ByteArrayNBT nbtImageRawBytes = (ByteArrayNBT)nbtImageRaw;
                raw = nbtImageRawBytes.getByteArray();
            }
            INBT nbtImagePacked = nbtImageTag.get(TAG_IMAGE_PACKED);
            if (nbtImagePacked instanceof ByteArrayNBT) {
                ByteArrayNBT nbtImagePackedBytes = (ByteArrayNBT)nbtImagePacked;
                packed = nbtImagePackedBytes.getByteArray();
            }
        } else if (nbtImage instanceof NumberNBT) {
            NumberNBT nbtPrim = (NumberNBT)nbtImage;
            int rowSize = nbtPrim.getInt();
            width = rowSize;
            height = rowSize;
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
                Image image = ImageUtils.readImage(in, this.hasAlpha);
                if (image != null) {
                    this.image = image;
                    this.packed = packed;
                    this.packedWidth = image.getWidth();
                    this.packedHeight = image.getHeight();
                    this.imageChanged();
                }
            }
        } else if (width > 0 && height > 0) {
            this.packed = null;
            if (raw != null) {
                this.image = new Image(width, height, raw, this.hasAlpha);
            } else {
                this.setSize(width, height);
            }
            this.imageChanged();
        }
    }
    
    public void unload() {
        if (EffectiveSide.get().isClient()) {
            this.unloadClient();
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    protected void unloadClient() {
        if (this.texture != null) {
            TextureUtil.releaseTextureId(this.texture);
            this.texture = null;
        }
    }
    
    public static Picture mergeH(Picture... subs) {
        if (subs.length == 0) {
            return null;
        }
        Picture picture = subs[0];
        for (int i = 1; i < subs.length; ++i) {
            Picture sub = subs[i];
            picture = mergeH(picture, sub);
        }
        return picture;
    }
    
    public static Picture mergeH(Picture first, Picture second) {
        return merge(first, second, false);
    }
    
    public static Picture mergeV(Picture... subs) {
        if (subs.length == 0) {
            return null;
        }
        Picture picture = subs[0];
        for (int i = 1; i < subs.length; ++i) {
            Picture sub = subs[i];
            picture = mergeV(picture, sub);
        }
        return picture;
    }
    
    public static Picture mergeV(Picture first, Picture second) {
        return merge(first, second, true);
    }
    
    public static Picture merge(Picture[][] pictures) {
        return mergeV(Arrays.asList(pictures)
                            .stream()
                            .map(Picture::mergeH)
                            .toArray(Picture[]::new));
    }
    
    protected static Picture merge(Picture first, Picture second, boolean vertically) {
        int firstWidth = first.getWidth();
        int firstHeight = first.getHeight();
        int secondWidth = second.getWidth();
        int secondHeight = second.getHeight();
        int width;
        int height;
        int secondOffsetX;
        int secondOffsetY;
        if (vertically) {
            width = Math.max(firstWidth, secondWidth);
            height = firstHeight + secondHeight;
            secondOffsetX = 0;
            secondOffsetY = firstHeight;
        } else {
            width = firstWidth + secondWidth;
            height = Math.max(firstHeight, secondHeight);
            secondOffsetX = firstWidth;
            secondOffsetY = 0;
        }
        Picture picture = new Picture(
                new FixedPictureSize(width, height),
                first.hasAlpha || second.hasAlpha);
        if (first.unpack()) {
            for (int y = 0; y < firstHeight; ++y) {
                for (int x = 0; x < firstWidth; ++x) {
                    picture.image.setPixelColor(x, y, first.image.getPixelColor(x, y));
                }
            }
        }
        if (second.unpack()) {
            for (int y = 0; y < secondHeight; ++y) {
                for (int x = 0; x < secondWidth; ++x) {
                    picture.image.setPixelColor(secondOffsetX + x, secondOffsetY + y,
                                                second.image.getPixelColor(x, y));
                }
            }
        }
        return picture;
    }
}
