package com.vanym.paniclecraft.plugins.computercraft;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;
import com.vanym.paniclecraft.core.component.painting.Picture;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.util.EnumFacing;

public class PaintingFramePeripheral extends PicturePeripheral {
    
    protected final ISidePictureProvider sideProvider;
    protected EnumFacing pside;
    
    public PaintingFramePeripheral(ISidePictureProvider sideProvider) {
        this(sideProvider, null);
    }
    
    public PaintingFramePeripheral(ISidePictureProvider sideProvider, EnumFacing pside) {
        this.sideProvider = sideProvider;
        this.pside = pside;
    }
    
    @Override
    public String getType() {
        return "paintingframe";
    }
    
    @PeripheralMethod(31)
    protected Object getAvailableSides() {
        return Arrays.stream(EnumFacing.VALUES)
                     .collect(Collectors.toMap(f->f.getIndex() + 1, f->f.getName2()));
    }
    
    @PeripheralMethod(32)
    protected String getCurrentSide() {
        return this.pside != null ? this.pside.getName2() : "unknown";
    }
    
    @PeripheralMethod(33)
    protected void setSide(String name) throws LuaException, InterruptedException {
        try {
            this.pside = Arrays.stream(EnumFacing.VALUES)
                               .filter(f->f.getName2().equalsIgnoreCase(name))
                               .findAny()
                               .get();
        } catch (NoSuchElementException e) {
            throw new LuaException("invalid side");
        }
    }
    
    @PeripheralMethod(14)
    protected boolean hasPicture() {
        return this.getPicture() != null;
    }
    
    @Override
    public boolean equals(IPeripheral other) {
        if (other != null && other instanceof PaintingFramePeripheral) {
            PaintingFramePeripheral pfp = (PaintingFramePeripheral)other;
            return this.sideProvider.equals(pfp.sideProvider);
        }
        return false;
    }
    
    @Override
    protected Picture getPicture() {
        if (this.pside == null) {
            return null;
        }
        return this.sideProvider.getPicture(this.pside.getIndex());
    }
    
}
