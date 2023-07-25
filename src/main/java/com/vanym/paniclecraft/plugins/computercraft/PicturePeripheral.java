package com.vanym.paniclecraft.plugins.computercraft;

import java.awt.Color;

import com.vanym.paniclecraft.core.component.painting.Picture;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;

public abstract class PicturePeripheral extends PeripheralBase {
    
    public PicturePeripheral() {}
    
    @PeripheralMethod(value = 0, mainThread = true)
    protected String getName() throws LuaException, InterruptedException {
        return this.findPicture().getName();
    }
    
    @PeripheralMethod(value = 1, mainThread = true)
    protected boolean isEditable() throws LuaException, InterruptedException {
        return this.findPicture().isEditable();
    }
    
    @PeripheralMethod(value = 2, mainThread = true)
    protected int getWidth() throws LuaException, InterruptedException {
        return this.findPicture().getWidth();
    }
    
    @PeripheralMethod(value = 3, mainThread = true)
    protected int getHeight() throws LuaException, InterruptedException {
        return this.findPicture().getHeight();
    }
    
    @PeripheralMethod(value = 11, mainThread = true)
    protected Object[] getPixelColor(int px, int py) throws LuaException, InterruptedException {
        Picture picture = this.findPicture();
        checkPicturePixelCoords(picture, px, py);
        Color color = picture.getPixelColor(px, py);
        return new Object[]{color.getRed(), color.getGreen(), color.getBlue()};
    }
    
    @PeripheralMethod(value = 12, mainThread = true)
    protected boolean setPixelColor(int px, int py, int red, int green, int blue)
            throws LuaException, InterruptedException {
        Picture picture = this.findPicture();
        checkPictureEditable(picture);
        checkPicturePixelCoords(picture, px, py);
        Color color = getColor(red, green, blue);
        picture.setPixelColor(px, py, color);
        return true;
    }
    
    @PeripheralMethod(value = 13, mainThread = true)
    protected boolean fill(int red, int green, int blue) throws LuaException, InterruptedException {
        Picture picture = this.findPicture();
        checkPictureEditable(picture);
        Color color = getColor(red, green, blue);
        picture.fill(color);
        return true;
    }
    
    @Override
    public void attach(IComputerAccess computer) {}
    
    @Override
    public void detach(IComputerAccess computer) {}
    
    protected abstract Picture getPicture();
    
    protected Picture findPicture() throws LuaException {
        Picture picture = this.getPicture();
        if (picture == null) {
            throw new LuaException("cannot find picture");
        }
        return picture;
    }
    
    public static Color getColor(int red, int green, int blue) throws LuaException {
        if (red < 0 || red >= 256) {
            throw new LuaException("red must be from 0 to 255");
        }
        if (green < 0 || green >= 256) {
            throw new LuaException("green must be from 0 to 255");
        }
        if (blue < 0 || blue >= 256) {
            throw new LuaException("blue must be from 0 to 255");
        }
        return new Color(red, green, blue);
    }
    
    public static void checkPictureEditable(Picture picture) throws LuaException {
        if (!picture.isEditable()) {
            throw new LuaException("picture is not editable");
        }
    }
    
    public static void checkPicturePixelCoords(Picture picture, int px, int py)
            throws LuaException {
        if (px < 0 || px >= picture.getWidth()) {
            throw new LuaException("x must be from 0 to width");
        }
        if (py < 0 || py >= picture.getHeight()) {
            throw new LuaException("y must be from 0 to height");
        }
    }
}
