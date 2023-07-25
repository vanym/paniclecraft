package com.vanym.paniclecraft.plugins.computercraft;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;
import com.vanym.paniclecraft.utils.WorldUtils;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PaintingFramePeripheral extends PicturePeripheral {
    
    protected final ISidePictureProvider sideProvider;
    protected Direction pside;
    
    public PaintingFramePeripheral(ISidePictureProvider sideProvider) {
        this(sideProvider, null);
    }
    
    public PaintingFramePeripheral(ISidePictureProvider sideProvider, Direction pside) {
        this.sideProvider = sideProvider;
        this.pside = pside;
    }
    
    @Override
    public String getType() {
        return "paintingframe";
    }
    
    @PeripheralMethod(31)
    protected Object getAvailableSides() {
        return Arrays.stream(Direction.values())
                     .collect(Collectors.toMap(f->f.getIndex() + 1, f->f.getName2()));
    }
    
    @PeripheralMethod(32)
    protected String getCurrentSide() {
        return this.pside != null ? this.pside.getName2() : "unknown";
    }
    
    @PeripheralMethod(33)
    protected void setSide(String name) throws LuaException, InterruptedException {
        try {
            this.pside = Arrays.stream(Direction.values())
                               .filter(f->f.getName2().equalsIgnoreCase(name))
                               .findAny()
                               .get();
        } catch (NoSuchElementException e) {
            throw new LuaException("invalid side");
        }
    }
    
    @PeripheralMethod(value = 14, mainThread = true)
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
    
    public static IPeripheral getPeripheral(World world, BlockPos pos, Direction side) {
        Direction pside = side.getOpposite();
        return WorldUtils.getTileEntity(world, pos, TileEntityPaintingFrame.class)
                         .map(tile->new PaintingFramePeripheral(tile, pside))
                         .orElse(null);
    }
}
