package com.vanym.paniclecraft.tileentity;

import java.awt.Color;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.core.component.advsign.AdvSignForm;
import com.vanym.paniclecraft.core.component.advsign.AdvSignText;
import com.vanym.paniclecraft.core.component.advsign.FormattingUtils;
import com.vanym.paniclecraft.utils.GeometryUtils;
import com.vanym.paniclecraft.utils.NumberUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

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
    
    @Override
    public void writeToNBT(NBTTagCompound nbtTag) {
        this.writeToNBT(nbtTag, false);
    }
    
    public void writeToNBT(NBTTagCompound nbtTag, boolean toStack) {
        nbtTag.setTag(TAG_FRONTTEXT, this.frontText.serializeNBT());
        nbtTag.setTag(TAG_BACKTEXT, this.backText.serializeNBT());
        nbtTag.setInteger(TAG_STANDCOLOR, this.standColor.getRGB());
        if (toStack) {
            return;
        }
        super.writeToNBT(nbtTag);
        nbtTag.setDouble(TAG_DIRECTION, this.direction);
        nbtTag.setInteger(TAG_FORM, this.form.getIndex());
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbtTag) {
        this.readFromNBT(nbtTag, false);
    }
    
    public void readFromNBT(NBTTagCompound nbtTag, boolean fromStack) {
        if (nbtTag.hasKey(TAG_FRONTTEXT, 10)) {
            this.frontText.deserializeNBT(nbtTag.getCompoundTag(TAG_FRONTTEXT));
        }
        if (nbtTag.hasKey(TAG_BACKTEXT, 10)) {
            this.backText.deserializeNBT(nbtTag.getCompoundTag(TAG_BACKTEXT));
        }
        this.standColor = new Color(nbtTag.getInteger(TAG_STANDCOLOR), true);
        // backwards compatibility with 2.7.0.0
        if (!nbtTag.hasKey(TAG_FRONTTEXT)
            && nbtTag.hasKey("Lines", 9)
            && nbtTag.hasKey("TextColor", 3)) {
            List<IChatComponent> lines = this.frontText.getLines();
            lines.clear();
            NBTTagList linesTag = nbtTag.getTagList("Lines", 8);
            IntStream.range(0, linesTag.tagCount())
                     .mapToObj(linesTag::getStringTagAt)
                     .map(FormattingUtils::parseLine)
                     .forEachOrdered(lines::add);
            this.frontText.setTextColor(new Color(nbtTag.getInteger("TextColor"), true));
        }
        if (fromStack) {
            return;
        }
        super.readFromNBT(nbtTag);
        this.setDirection(nbtTag.getDouble(TAG_DIRECTION));
        this.setForm(AdvSignForm.byIndex(nbtTag.getInteger(TAG_FORM)));
        // backwards compatibility with 2.7.0.0
        if (!nbtTag.hasKey(TAG_FORM) && nbtTag.hasKey("OnStick")) {
            this.setForm(nbtTag.getBoolean("OnStick") ? AdvSignForm.STICK_DOWN : AdvSignForm.WALL);
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
        direction = MathHelper.wrapAngleTo180_double(direction);
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
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return GeometryUtils.getFullBlockBox()
                            .expand(0.25D, 0.25D, 0.25D)
                            .getOffsetBoundingBox(this.xCoord, this.yCoord, this.zCoord);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 16384.0D;
    }
    
    public static boolean isValidTag(NBTTagCompound signTag) {
        return new Color(signTag.getInteger(TAG_STANDCOLOR), true).getAlpha() == 0xff
            && Stream.of(TAG_FRONTTEXT, TAG_BACKTEXT)
                     .map(signTag::getCompoundTag)
                     .map(AdvSignText::new)
                     .allMatch(AdvSignText::isValid);
    }
}
