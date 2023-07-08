package com.vanym.paniclecraft.tileentity;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
    
    protected PlayerEntity editor = null;
    
    public static final String TAG_LINES = "Lines";
    public static final String TAG_STANDCOLOR = "StandColor";
    public static final String TAG_TEXTCOLOR = "TextColor";
    
    protected static final String TAG_DIRECTION = "Direction";
    protected static final String TAG_ONSTICK = "OnStick";
    
    public TileEntityAdvSign() {
        super(Core.instance.advSign.tileEntityAdvSign);
    }
    
    @Override
    public CompoundNBT write(CompoundNBT nbtTag) {
        return this.write(nbtTag, false);
    }
    
    public CompoundNBT write(CompoundNBT nbtTag, boolean toStack) {
        ListNBT linesTag = new ListNBT();
        this.lines.stream().map(StringNBT::new).forEachOrdered(linesTag::add);
        nbtTag.put(TAG_LINES, linesTag);
        nbtTag.putInt(TAG_STANDCOLOR, this.standColor.getRGB());
        nbtTag.putInt(TAG_TEXTCOLOR, this.textColor.getRGB());
        if (toStack) {
            return nbtTag;
        }
        super.write(nbtTag);
        nbtTag.putDouble(TAG_DIRECTION, this.direction);
        nbtTag.putBoolean(TAG_ONSTICK, this.onStick);
        return nbtTag;
    }
    
    @Override
    public void read(CompoundNBT nbtTag) {
        this.read(nbtTag, false);
    }
    
    public void read(CompoundNBT nbtTag, boolean fromStack) {
        this.standColor = new Color(nbtTag.getInt(TAG_STANDCOLOR), true);
        this.textColor = new Color(nbtTag.getInt(TAG_TEXTCOLOR), true);
        this.lines.clear();
        ListNBT linesTag = nbtTag.getList(TAG_LINES, 8);
        for (int i = 0; i < linesTag.size(); ++i) {
            this.lines.add(linesTag.getString(i));
        }
        if (fromStack) {
            return;
        }
        super.read(nbtTag);
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
    
    public void setEditor(PlayerEntity editor) {
        this.editor = editor;
    }
    
    public void resetEditor() {
        this.setEditor(null);
    }
    
    public boolean isEditor(PlayerEntity player) {
        return this.editor != null && this.editor == player;
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.pos).grow(0.25D);
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 16384.0D;
    }
}
