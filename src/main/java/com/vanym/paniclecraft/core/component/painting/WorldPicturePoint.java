package com.vanym.paniclecraft.core.component.painting;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class WorldPicturePoint {
    
    public final WorldPictureProvider provider;
    public final World world;
    public final BlockPos pos;
    public final int side;
    
    public WorldPicturePoint(WorldPictureProvider provider,
            World world,
            BlockPos pos,
            int side) {
        this.provider = provider;
        this.world = world;
        this.pos = pos;
        this.side = side;
    }
    
    public Picture getOrCreatePicture() {
        return this.provider.getOrCreatePicture(this.world, this.pos, this.side);
    }
    
    public Picture getPicture() {
        return this.provider.getPicture(this.world, this.pos, this.side);
    }
    
    public WorldPicturePoint getNeighborPoint(int xOffset, int yOffset) {
        PaintingSide pside = PaintingSide.getSide(this.side);
        BlockPos pos = this.pos.offset(pside.axes.xDir, xOffset).offset(pside.axes.yDir, yOffset);
        return new WorldPicturePoint(this.provider, this.world, pos, this.side);
    }
}
