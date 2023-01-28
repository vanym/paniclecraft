package com.vanym.paniclecraft.plugins.computercraft;

import com.vanym.paniclecraft.core.component.painting.Picture;

import dan200.computercraft.api.peripheral.IPeripheral;

public class PaintingPeripheral extends PicturePeripheral {
    
    protected final Picture picture;
    
    public PaintingPeripheral(Picture picture) {
        this.picture = picture;
    }
    
    @Override
    public String getType() {
        return "painting";
    }
    
    @Override
    public boolean equals(IPeripheral other) {
        if (other != null && other instanceof PaintingPeripheral) {
            PaintingPeripheral pp = (PaintingPeripheral)other;
            return this.picture.equals(pp.picture);
        }
        return false;
    }
    
    @Override
    protected Picture getPicture() {
        return this.picture;
    }
    
}
