package com.vanym.paniclecraft.core.component.painting;

public interface IPictureHolder {
    
    public IPictureSize getDefaultSize();
    
    public Picture getNeighborPicture(int offsetX, int offsetY);
    
    public void update();
}
