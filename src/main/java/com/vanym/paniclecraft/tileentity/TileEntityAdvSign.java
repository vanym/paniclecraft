package com.vanym.paniclecraft.tileentity;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;

public class TileEntityAdvSign extends TileEntityBase {
    
    public static final int MAX_LINES = 32;
    public static final int MIN_LINES = 1;
    
    public static final List<String> DEFAULT_LINES = Collections.nCopies(5, "");
    
    public final List<String> lines = new ArrayList<>(DEFAULT_LINES);
    
    protected Color color = Color.WHITE;
    
    protected double direction = 0.0D;
    protected boolean onStick = false;
    
    protected EntityPlayer editor = null;
    
    public static final String TAG_LINES = "Lines";
    public static final String TAG_COLOR = "Color";
    
    protected static final String TAG_DIRECTION = "Direction";
    protected static final String TAG_ONSTICK = "OnStick";
    
    @Override
    public void writeToNBT(NBTTagCompound nbtTag) {
        this.writeToNBT(nbtTag, false);
    }
    
    public void writeToNBT(NBTTagCompound nbtTag, boolean toStack) {
        NBTTagList linesTag = new NBTTagList();
        this.lines.stream().map(NBTTagString::new).forEachOrdered(linesTag::appendTag);
        nbtTag.setTag(TAG_LINES, linesTag);
        nbtTag.setInteger(TAG_COLOR, this.color.getRGB());
        if (toStack) {
            return;
        }
        super.writeToNBT(nbtTag);
        nbtTag.setDouble(TAG_DIRECTION, this.direction);
        nbtTag.setBoolean(TAG_ONSTICK, this.onStick);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbtTag) {
        this.readFromNBT(nbtTag, false);
    }
    
    public void readFromNBT(NBTTagCompound nbtTag, boolean fromStack) {
        this.color = new Color(nbtTag.getInteger(TAG_COLOR), true);
        this.lines.clear();
        NBTTagList linesTag = nbtTag.getTagList(TAG_LINES, 8);
        for (int i = 0; i < linesTag.tagCount(); ++i) {
            this.lines.add(linesTag.getStringTagAt(i));
        }
        if (fromStack) {
            return;
        }
        super.readFromNBT(nbtTag);
        this.setDirection(nbtTag.getDouble(TAG_DIRECTION));
        this.onStick = nbtTag.getBoolean(TAG_ONSTICK);
    }
    
    public void setColor(Color color) {
        Objects.requireNonNull(color);
        this.color = color;
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public void setStick(boolean stick) {
        this.onStick = stick;
    }
    
    public boolean onStick() {
        return this.onStick;
    }
    
    public void setDirection(double direction) {
        direction = MathHelper.wrapAngleTo180_double(direction);
        if (direction < 0) {
            direction += 360.0D;
        }
        this.direction = direction;
    }
    
    public double getDirection() {
        return this.direction;
    }
    
    public void setEditor(EntityPlayer editor) {
        this.editor = editor;
    }
    
    public void resetEditor() {
        this.setEditor(null);
    }
    
    public boolean isEditor(EntityPlayer player) {
        return this.editor != null && this.editor == player;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox((double)this.xCoord - 0.0F, (double)this.yCoord + 0.0F,
                                            (double)this.zCoord - 0.0F, (double)this.xCoord + 1.0F,
                                            (double)this.yCoord + 1.0F, (double)this.zCoord + 1.0F);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 16384.0D;
    }
}
