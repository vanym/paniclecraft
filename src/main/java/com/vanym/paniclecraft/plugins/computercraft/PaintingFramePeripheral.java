package com.vanym.paniclecraft.plugins.computercraft;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
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
    protected final ThreadLocal<Direction> pside;
    
    public PaintingFramePeripheral(ISidePictureProvider sideProvider) {
        this(sideProvider, null);
    }
    
    public PaintingFramePeripheral(ISidePictureProvider sideProvider, Direction pside) {
        this.sideProvider = sideProvider;
        this.pside = ThreadLocal.withInitial(()->pside);
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
        return Optional.ofNullable(this.pside.get()).map(Direction::getName2).orElse("unknown");
    }
    
    @PeripheralMethod(33)
    protected void setSide(String name) throws LuaException, InterruptedException {
        try {
            this.pside.set(Arrays.stream(Direction.values())
                                 .filter(f->f.getName2().equalsIgnoreCase(name))
                                 .findAny()
                                 .get());
        } catch (NoSuchElementException e) {
            throw new LuaException("invalid side");
        }
    }
    
    @PeripheralMethod(14)
    protected boolean hasPicture() {
        synchronized (this.syncObject()) {
            return this.getPicture() != null;
        }
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
        return Optional.ofNullable(this.pside.get())
                       .map(Direction::getIndex)
                       .map(this.sideProvider::getPicture)
                       .orElse(null);
    }
    
    @Override
    protected Object syncObject() {
        return this.sideProvider;
    }
    
    public static IPeripheral getPeripheral(World world, BlockPos pos, Direction side) {
        Direction pside = side.getOpposite();
        return WorldUtils.getTileEntity(world, pos, TileEntityPaintingFrame.class)
                         .map(tile->new PaintingFramePeripheral(tile, pside))
                         .orElse(null);
    }
}
