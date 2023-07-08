package com.vanym.paniclecraft.client.utils;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ImageSelection implements Transferable {
    
    protected final Image img;
    
    public ImageSelection(Image img) {
        this.img = img;
    }
    
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DataFlavor.imageFlavor};
    }
    
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return DataFlavor.imageFlavor.equals(flavor);
    }
    
    @Override
    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {
        if (!this.isDataFlavorSupported(flavor)) {
            throw new UnsupportedFlavorException(flavor);
        }
        return this.img;
    }
}
