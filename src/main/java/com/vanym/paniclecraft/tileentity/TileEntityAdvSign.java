package com.vanym.paniclecraft.tileentity;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.vanym.paniclecraft.DEF;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityAdvSign extends TileEntityBase {
    
    public static final String IN_MOD_ID = "advanced_sign";
    public static final ResourceLocation ID = new ResourceLocation(DEF.MOD_ID, IN_MOD_ID);
    
    public static final int MAX_LINES = 32;
    public static final int MIN_LINES = 1;
    
    public static final List<String> DEFAULT_LINES = Collections.nCopies(5, "");
    
    public final List<String> lines = new ArrayList<>(DEFAULT_LINES);
    
    protected Color standColor = Color.WHITE;
    protected Color textColor = Color.BLACK;
    
    protected double direction = 0.0D;
    protected boolean onStick = false;
    
    protected EntityPlayer editor = null;
    
    public static final String TAG_LINES = "Lines";
    public static final String TAG_STANDCOLOR = "StandColor";
    public static final String TAG_TEXTCOLOR = "TextColor";
    
    protected static final String TAG_DIRECTION = "Direction";
    protected static final String TAG_ONSTICK = "OnStick";
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTag) {
        return this.writeToNBT(nbtTag, false);
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTag, boolean toStack) {
        NBTTagList linesTag = new NBTTagList();
        this.lines.stream().map(NBTTagString::new).forEachOrdered(linesTag::appendTag);
        nbtTag.setTag(TAG_LINES, linesTag);
        nbtTag.setInteger(TAG_STANDCOLOR, this.standColor.getRGB());
        nbtTag.setInteger(TAG_TEXTCOLOR, this.textColor.getRGB());
        if (toStack) {
            return nbtTag;
        }
        super.writeToNBT(nbtTag);
        nbtTag.setDouble(TAG_DIRECTION, this.direction);
        nbtTag.setBoolean(TAG_ONSTICK, this.onStick);
        return nbtTag;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbtTag) {
        this.readFromNBT(nbtTag, false);
    }
    
    public void readFromNBT(NBTTagCompound nbtTag, boolean fromStack) {
        this.standColor = new Color(nbtTag.getInteger(TAG_STANDCOLOR), true);
        this.textColor = new Color(nbtTag.getInteger(TAG_TEXTCOLOR), true);
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
    
    public void setStandColor(Color color) {
        Objects.requireNonNull(color);
        this.standColor = color;
    }
    
    public Color getStandColor() {
        return this.standColor;
    }
    
    public void setTextColor(Color color) {
        Objects.requireNonNull(color);
        this.textColor = color;
    }
    
    public Color getTextColor() {
        return this.textColor;
    }
    
    public void setStick(boolean stick) {
        this.onStick = stick;
    }
    
    public boolean onStick() {
        return this.onStick;
    }
    
    public void setDirection(double direction) {
        direction = MathHelper.wrapDegrees(direction);
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
        return new AxisAlignedBB(this.pos);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 16384.0D;
    }
}
