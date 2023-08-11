package com.vanym.paniclecraft.api.event;

import java.util.Objects;

import javax.annotation.Nonnull;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent;

@Event.HasResult
public class BlockSidePaintabilityEvent extends BlockEvent {
    
    private final ForgeDirection side;
    private final boolean paintable;
    private final boolean air;
    private final boolean liquid;
    
    public BlockSidePaintabilityEvent(
            int x,
            int y,
            int z,
            World world,
            Block block,
            int meta,
            @Nonnull ForgeDirection side,
            boolean paintable,
            boolean air,
            boolean liquid) {
        super(x, y, z, world, block, meta);
        this.side = Objects.requireNonNull(side);
        this.paintable = paintable;
        this.air = air;
        this.liquid = liquid;
    }
    
    @Nonnull
    public ForgeDirection getSide() {
        return this.side;
    }
    
    public boolean isPaintable() {
        return this.paintable;
    }
    
    public boolean isAir() {
        return this.air;
    }
    
    public boolean isLiquid() {
        return this.liquid;
    }
}
