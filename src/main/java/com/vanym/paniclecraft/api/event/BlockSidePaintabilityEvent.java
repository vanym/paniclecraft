package com.vanym.paniclecraft.api.event;

import java.util.Objects;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;

@Event.HasResult
public class BlockSidePaintabilityEvent extends BlockEvent {
    
    private final Direction side;
    private final boolean paintable;
    private final boolean air;
    private final boolean liquid;
    
    public BlockSidePaintabilityEvent(
            IWorld world,
            BlockPos pos,
            BlockState state,
            @Nonnull Direction side,
            boolean paintable,
            boolean air,
            boolean liquid) {
        super(world, pos, state);
        this.side = Objects.requireNonNull(side);
        this.paintable = paintable;
        this.air = air;
        this.liquid = liquid;
    }
    
    @Nonnull
    public Direction getSide() {
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
