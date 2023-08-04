package com.vanym.paniclecraft.tileentity;

import java.awt.Color;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.block.BlockAdvSign;
import com.vanym.paniclecraft.core.component.advsign.AdvSignForm;
import com.vanym.paniclecraft.core.component.advsign.AdvSignText;
import com.vanym.paniclecraft.utils.NumberUtils;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityAdvSign extends TileEntityBase {
    
    public static final String IN_MOD_ID = "advanced_sign";
    public static final ResourceLocation ID = new ResourceLocation(DEF.MOD_ID, IN_MOD_ID);
    
    protected final AdvSignText frontText = new AdvSignText();
    protected final AdvSignText backText = new AdvSignText();
    
    protected Color standColor = Color.WHITE;
    
    protected double direction = 0.0D;
    protected AdvSignForm form = AdvSignForm.WALL;
    
    protected UUID editor = null;
    
    public static final String TAG_FRONTTEXT = "FrontText";
    public static final String TAG_BACKTEXT = "BackText";
    public static final String TAG_STANDCOLOR = "StandColor";
    
    protected static final String TAG_DIRECTION = "Direction";
    protected static final String TAG_FORM = "Form";
    
    public TileEntityAdvSign() {
        super(Core.instance.advSign.tileEntityAdvSign);
    }
    
    @Override
    public CompoundNBT write(CompoundNBT nbtTag) {
        return this.write(nbtTag, false);
    }
    
    public CompoundNBT write(CompoundNBT nbtTag, boolean toStack) {
        nbtTag.put(TAG_FRONTTEXT, this.frontText.serializeNBT());
        nbtTag.put(TAG_BACKTEXT, this.backText.serializeNBT());
        nbtTag.putInt(TAG_STANDCOLOR, this.standColor.getRGB());
        if (toStack) {
            return nbtTag;
        }
        super.write(nbtTag);
        nbtTag.putDouble(TAG_DIRECTION, this.direction);
        nbtTag.putInt(TAG_FORM, this.form.getIndex());
        return nbtTag;
    }
    
    @Override
    public void read(CompoundNBT nbtTag) {
        this.read(nbtTag, false);
    }
    
    public void read(CompoundNBT nbtTag, boolean fromStack) {
        if (nbtTag.contains(TAG_FRONTTEXT, 10)) {
            this.frontText.deserializeNBT(nbtTag.getCompound(TAG_FRONTTEXT));
        }
        if (nbtTag.contains(TAG_BACKTEXT, 10)) {
            this.backText.deserializeNBT(nbtTag.getCompound(TAG_BACKTEXT));
        }
        this.standColor = new Color(nbtTag.getInt(TAG_STANDCOLOR), true);
        if (fromStack) {
            return;
        }
        super.read(nbtTag);
        this.setDirection(nbtTag.getDouble(TAG_DIRECTION));
        this.setForm(AdvSignForm.byIndex(nbtTag.getInt(TAG_FORM)));
    }
    
    @Override
    public void markForUpdate() {
        this.markDirty();
        if (this.world != null) {
            BlockState state = this.getBlockState();
            BlockState actual = state;
            if (state.getBlock() instanceof BlockAdvSign) {
                actual = state.with(BlockAdvSign.FORM, this.form)
                              .with(BlockAdvSign.ROTATION,
                                    Math.abs((int)Math.round(this.getDirection() / 22.5D)) % 16);
            }
            if (state != actual) {
                this.world.setBlockState(this.pos, actual);
            } else {
                this.world.notifyBlockUpdate(this.pos, state, actual, 3);
            }
        }
    }
    
    public AdvSignText getFront() {
        return this.frontText;
    }
    
    public AdvSignText getBack() {
        return this.backText;
    }
    
    public AdvSignText getSide(boolean front) {
        return front ? this.getFront() : this.getBack();
    }
    
    public void setStandColor(Color color) {
        Objects.requireNonNull(color);
        this.standColor = color;
    }
    
    public Color getStandColor() {
        return this.standColor;
    }
    
    public void setForm(AdvSignForm form) {
        this.form = Objects.requireNonNull(form);
    }
    
    public AdvSignForm getForm() {
        return this.form;
    }
    
    public void setDirection(double direction) {
        direction = NumberUtils.finite(direction);
        direction = MathHelper.wrapDegrees(direction);
        if (direction < 0) {
            direction += 360.0D;
        }
        this.direction = direction;
    }
    
    public double getDirection() {
        return this.direction;
    }
    
    public void setEditor(UUID editor) {
        this.editor = editor;
    }
    
    public void resetEditor() {
        this.editor = null;
    }
    
    public boolean hasEditor() {
        return this.editor == null;
    }
    
    public boolean isEditor(UUID player) {
        return this.editor != null && this.editor.equals(player);
    }
    
    @Override
    public boolean onlyOpsCanSetNbt() {
        return true;
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
    
    public static boolean isValidTag(CompoundNBT signTag) {
        return new Color(signTag.getInt(TAG_STANDCOLOR), true).getAlpha() == 0xff
            && Stream.of(TAG_FRONTTEXT, TAG_BACKTEXT)
                     .map(signTag::getCompound)
                     .map(AdvSignText::new)
                     .allMatch(AdvSignText::isValid);
    }
}
