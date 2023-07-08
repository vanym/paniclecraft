package com.vanym.paniclecraft.core.component.painting;

public interface IPictureSize {
    
    public int getWidth();
    
    public int getHeight();
    
    public static boolean equals(IPictureSize first, IPictureSize second) {
        if (first == null || second == null) {
            return false;
        }
        return first.getWidth() == second.getWidth() && first.getHeight() == second.getHeight();
    }
}
