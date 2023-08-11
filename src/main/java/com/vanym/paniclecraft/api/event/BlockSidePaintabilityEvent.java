package com.vanym.paniclecraft.api.event;

import java.util.Objects;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

@Event.HasResult
public class BlockSidePaintabilityEvent extends BlockEvent {
    
    private final EnumFacing side;
    private final boolean paintable;
    private final boolean air;
    private final boolean liquid;
    
    public BlockSidePaintabilityEvent(
            World world,
            BlockPos pos,
            IBlockState state,
            @Nonnull EnumFacing side,
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
    public EnumFacing getSide() {
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
