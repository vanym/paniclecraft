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
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class PaintingFramePeripheral extends PicturePeripheral {
    
    protected final ISidePictureProvider sideProvider;
    protected final ThreadLocal<ForgeDirection> pside;
    
    public PaintingFramePeripheral(ISidePictureProvider sideProvider) {
        this(sideProvider, null);
    }
    
    public PaintingFramePeripheral(ISidePictureProvider sideProvider, ForgeDirection pside) {
        this.sideProvider = sideProvider;
        if (pside == null) {
            pside = ForgeDirection.UNKNOWN;
        }
        ForgeDirection defpside = pside;
        this.pside = ThreadLocal.withInitial(()->defpside);
    }
    
    @Override
    public String getType() {
        return "paintingframe";
    }
    
    @PeripheralMethod(31)
    protected Object getAvailableSides() {
        return Arrays.stream(ForgeDirection.VALID_DIRECTIONS)
                     .collect(Collectors.toMap(f->f.ordinal() + 1, f->f.toString().toLowerCase()));
    }
    
    @PeripheralMethod(32)
    protected String getCurrentSide() {
        return this.pside.get().toString().toLowerCase();
    }
    
    @PeripheralMethod(33)
    protected void setSide(String name) throws LuaException, InterruptedException {
        try {
            this.pside.set(Arrays.stream(ForgeDirection.VALID_DIRECTIONS)
                                 .filter(f->f.toString().equalsIgnoreCase(name))
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
        ForgeDirection pside = this.pside.get();
        if (pside == ForgeDirection.UNKNOWN) {
            return null;
        }
        return this.sideProvider.getPicture(pside.ordinal());
    }
    
    @Override
    protected Object syncObject() {
        return this.sideProvider;
    }
    
    public static IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
        ForgeDirection pside = ForgeDirection.getOrientation(side).getOpposite();
        return WorldUtils.getTileEntity(world, x, y, z, TileEntityPaintingFrame.class)
                         .map(tile->new PaintingFramePeripheral(tile, pside))
                         .orElse(null);
    }
}
