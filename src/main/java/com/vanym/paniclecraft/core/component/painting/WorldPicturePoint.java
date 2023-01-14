package com.vanym.paniclecraft.core.component.painting;

import net.minecraft.world.World;

public final class WorldPicturePoint {
    
    public final WorldPictureProvider provider;
    public final World world;
    public final int x;
    public final int y;
    public final int z;
    public final int side;
    
    public WorldPicturePoint(WorldPictureProvider provider,
            World world,
            int x,
            int y,
            int z,
            int side) {
        this.provider = provider;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.side = side;
    }
    
    public Picture getPicture() {
        return this.provider.getPicture(this.world, this.x, this.y, this.z, this.side);
    }
    
    public WorldPicturePoint getNeighborPoint(int xOffset, int yOffset) {
        PaintingSide pside = PaintingSide.getSide(this.side);
        int x = this.x + pside.xDir.offsetX * xOffset + pside.yDir.offsetX * yOffset;
        int y = this.y + pside.xDir.offsetY * xOffset + pside.yDir.offsetY * yOffset;
        int z = this.z + pside.xDir.offsetZ * xOffset + pside.yDir.offsetZ * yOffset;
        return new WorldPicturePoint(this.provider, this.world, x, y, z, this.side);
    }
}
