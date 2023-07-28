package com.vanym.paniclecraft.tileentity;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.IPictureHolder;
import com.vanym.paniclecraft.core.component.painting.IPictureSize;
import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;

public abstract class TileEntityPaintingContainer extends TileEntityBase
        implements
            ISidePictureProvider {
    
    public abstract void onWorldUnload();
    
    protected abstract class PictureHolder implements IPictureHolder {
        
        @Override
        public IPictureSize getDefaultSize() {
            return Core.instance.painting.config.paintingDefaultSize;
        }
        
        @Override
        public ISidePictureProvider getProvider() {
            return TileEntityPaintingContainer.this;
        }
        
        @Override
        public boolean isProviderSyncRequired() {
            return true;
        }
        
        @Override
        public void update() {
            TileEntityPaintingContainer.this.safeMarkForUpdate();
        }
    }
}
