package com.vanym.paniclecraft.core.component.painting;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Optional;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.IPaintingTool.PaintingToolType;
import com.vanym.paniclecraft.utils.ColorUtils;
import com.vanym.paniclecraft.utils.SideUtils;

import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Picture implements IPictureSize, INBTSerializable<NBTTagCompound> {
    
    protected final IPictureHolder holder;
    protected final boolean hasAlpha;
    
    protected boolean editable = true;
    protected String name;
    
    protected Image image;
    
    protected byte[] packed;
    protected int packedWidth;
    protected int packedHeight;
    
    @SideOnly(Side.CLIENT)
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
    
    // synchronized inside
    public boolean usePaintingTool(ItemStack stack, int x, int y) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        if (!(item instanceof IPaintingTool)) {
            return false;
        }
        IPaintingTool tool = (IPaintingTool)item;
        PaintingToolType toolType = tool.getPaintingToolType(stack);
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
                color = tool.getPaintingToolColor(stack);
            }
            IPictureSize size = new FixedPictureSize(this);
            double radius = tool.getPaintingToolRadius(stack, size);
            return this.setNeighborsPixelsColor(x, y, radius, color, size);
        } else if (toolType == PaintingToolType.FILLER && this.holder != null) {
            Color color = tool.getPaintingToolColor(stack);
            if (SideUtils.callSync(this.syncObject(), ()->this.fill(color))) {
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
            colorizeable.setColor(stack, ColorUtils.getAlphaless(color));
            return true;
        }
        return false;
    }
    
    protected boolean setNeighborsPixelsColor(
            int px,
            int py,
            double radius,
            Color color,
            IPictureSize size) {
        if (radius == 0.0D) {
            return false;
        }
        int width = size.getWidth();
        int height = size.getHeight();
        int rad = (int)Math.ceil(radius);
        int maxNX = rad / width + 1;
        int maxNY = rad / height + 1;
        boolean changed = false;
        for (int nx = 0; nx <= maxNX; nx++) {
            for (int ny = 0; ny <= maxNY; ny++) {
                changed |= this.setNeighborPixelsColor(nx, ny, px, py,
                                                       radius, color, size);
                if (ny > 0) {
                    changed |= this.setNeighborPixelsColor(nx, -ny, px, py,
                                                           radius, color, size);
                }
                if (nx > 0) {
                    changed |= this.setNeighborPixelsColor(-nx, ny, px, py,
                                                           radius, color, size);
                    if (ny > 0) {
                        changed |= this.setNeighborPixelsColor(-nx, -ny, px, py,
                                                               radius, color, size);
                    }
                }
            }
        }
        return changed;
    }
    
    protected boolean setNeighborPixelsColor(
            int nx,
            int ny,
            int px,
            int py,
            double radius,
            Color color,
            IPictureSize size) {
        int width = size.getWidth();
        int height = size.getHeight();
        int mx = px + nx * -width;
        int my = py + ny * -height;
        // check if no overlap exist
        int dx = Math.max(0, Math.min(mx, width - 1)) - mx;
        int dy = Math.max(0, Math.min(my, height - 1)) - my;
        if (dx * dx + dy * dy >= radius * radius) {
            return false;
        }
        Picture picture = this.getNeighborPicture(nx, ny);
        if (picture == null) {
            return false;
        }
        return SideUtils.callSync(picture.syncObject(), ()->picture.isEditableBy(size)
            && picture.setMyPixelsColor(mx, my, radius, color));
    }
    
    protected boolean setMyPixelsColor(
            int px,
            int py,
            double radius,
            Color color) {
        int width = this.getWidth();
        int height = this.getHeight();
        int rad = (int)Math.ceil(radius);
        int minX = Math.max(px - rad, 0);
        int maxX = Math.min(px + rad, width - 1);
        int minY = Math.max(py - rad, 0);
        int maxY = Math.min(py + rad, height - 1);
        boolean changed = false;
        for (int ix = minX, offsetX = ix - px; ix <= maxX; ix++, offsetX++) {
            for (int iy = minY, offsetY = iy - py; iy <= maxY; iy++, offsetY++) {
                if (offsetX * offsetX + offsetY * offsetY >= radius * radius) {
                    continue;
                }
                changed |= this.setMyPixelColor(ix, iy, color);
            }
        }
        if (changed) {
            this.imageChanged();
            this.update();
        }
        return changed;
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
        if (this.isEditable() && this.setMyPixelColor(x, y, color)) {
            this.imageChanged();
            this.update();
            return true;
        }
        return false;
    }
    
    public boolean fill(Color color) {
        if (this.isEditable()
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
    
    public IPictureHolder getHolder() {
        return this.holder;
    }
    
    public Object syncObject() {
        return Optional.of(this)
                       .map(Picture::getHolder)
                       .filter(IPictureHolder::isProviderSyncRequired)
                       .map(IPictureHolder::getProvider)
                       .orElse(null);
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
    
    public boolean isEditableBy(IPictureSize picture) {
        return this.isEditable() && this.isSameSize(picture);
    }
    
    public boolean canEdit(Picture picture) {
        return picture != null && picture.isEditableBy(this);
    }
    
    public boolean isSameSize(IPictureSize picture) {
        return IPictureSize.equals(this, picture);
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
    
    protected int getDataSize() {
        return this.getWidth() * this.getHeight() * Image.getPixelSize(this.hasAlpha);
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
            ByteBuffer buffer = ImageUtils.readImageToDirectByteBuffer(in, this.hasAlpha);
            if (buffer.capacity() != this.getDataSize()) {
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
    
    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        nbtTag.setBoolean(TAG_EDITABLE, this.editable);
        if (this.name != null) {
            nbtTag.setString(TAG_NAME, this.name);
        }
        NBTTagCompound nbtImageTag = new NBTTagCompound();
        this.writeImage(nbtImageTag);
        if (!nbtImageTag.hasNoTags()) {
            nbtTag.setTag(TAG_IMAGE, nbtImageTag);
        }
        return nbtTag;
    }
    
    @Override
    public void deserializeNBT(NBTTagCompound nbtTag) {
        if (nbtTag.hasKey(TAG_EDITABLE)) {
            this.editable = nbtTag.getBoolean(TAG_EDITABLE);
        }
        if (nbtTag.hasKey(TAG_NAME)) {
            this.setName(nbtTag.getString(TAG_NAME));
        } else {
            this.name = null;
        }
        if (nbtTag.hasKey(TAG_IMAGE)) {
            NBTBase nbtImage = nbtTag.getTag(TAG_IMAGE);
            this.readImage(nbtImage);
        }
    }
    
    protected void writeImage(NBTTagCompound nbtImageTag) {
        if (this.pack()) {
            nbtImageTag.setByteArray(TAG_IMAGE_PACKED, this.packed);
            nbtImageTag.setInteger(TAG_IMAGE_WIDTH, this.packedWidth);
            nbtImageTag.setInteger(TAG_IMAGE_HEIGHT, this.packedHeight);
        } else if (this.image != null) {
            nbtImageTag.setInteger(TAG_IMAGE_WIDTH, this.image.getWidth());
            nbtImageTag.setInteger(TAG_IMAGE_HEIGHT, this.image.getHeight());
            nbtImageTag.setByteArray(TAG_IMAGE_RAWDATA, this.image.getData());
        }
    }
    
    protected void readImage(NBTBase nbtImage) {
        int width = 0;
        int height = 0;
        byte[] packed = null;
        byte[] raw = null;
        if (nbtImage instanceof NBTTagByteArray) {
            NBTTagByteArray nbtImageBytes = (NBTTagByteArray)nbtImage;
            packed = nbtImageBytes.getByteArray();
        } else if (nbtImage instanceof NBTTagCompound) {
            NBTTagCompound nbtImageTag = (NBTTagCompound)nbtImage;
            width = nbtImageTag.getInteger(TAG_IMAGE_WIDTH);
            height = nbtImageTag.getInteger(TAG_IMAGE_HEIGHT);
            NBTBase nbtImageRaw = nbtImageTag.getTag(TAG_IMAGE_RAWDATA);
            if (nbtImageRaw instanceof NBTTagByteArray) {
                NBTTagByteArray nbtImageRawBytes = (NBTTagByteArray)nbtImageRaw;
                raw = nbtImageRawBytes.getByteArray();
            }
            NBTBase nbtImagePacked = nbtImageTag.getTag(TAG_IMAGE_PACKED);
            if (nbtImagePacked instanceof NBTTagByteArray) {
                NBTTagByteArray nbtImagePackedBytes = (NBTTagByteArray)nbtImagePacked;
                packed = nbtImagePackedBytes.getByteArray();
            }
        } else if (nbtImage instanceof NBTPrimitive) {
            NBTPrimitive nbtPrim = (NBTPrimitive)nbtImage;
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
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            this.unloadClient();
        }
    }
    
    @SideOnly(Side.CLIENT)
    protected void unloadClient() {
        if (this.texture != null) {
            TextureUtil.deleteTexture(this.texture);
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
