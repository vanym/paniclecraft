package com.vanym.paniclecraft.plugins.computercraft.pte;

import java.awt.Color;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.item.ItemStack;

public class TileEntityPaintingPeripheral implements IPeripheral {
    
    public final TileEntityPainting tileP;
    
    public TileEntityPaintingPeripheral(TileEntityPainting painting) {
        this.tileP = painting;
    }
    
    @Override
    public String getType() {
        return "painting";
    }
    
    @Override
    public String[] getMethodNames() {
        return new String[]{"getRow", "getPixelColor", "setPixelColor", "fill"};
    }
    
    @Override
    public Object[] callMethod(
            IComputerAccess computer,
            ILuaContext context,
            int method,
            Object[] arguments) throws LuaException, InterruptedException {
        // TODO WIP refactor it
        switch (method) {
            case 0: {
                return new Object[]{this.tileP.getPainting(this.tileP.getBlockMetadata())
                                              .getImage()
                                              .getWidth()};
            }
            case 1: {
                if (arguments.length < 2) {
                    throw new LuaException("too few arguments");
                }
                if (arguments.length > 2) {
                    throw new LuaException("too many arguments");
                }
                if (!(arguments[0] instanceof Double) || !(arguments[1] instanceof Double)) {
                    throw new LuaException("arguments must be a number");
                }
                int px = ((Double)arguments[0]).intValue();
                int py = ((Double)arguments[1]).intValue();
                if (px < 0
                    || px > this.tileP.getPainting(this.tileP.getBlockMetadata())
                                      .getImage()
                                      .getWidth()
                    || py < 0
                    || py > this.tileP.getPainting(this.tileP.getBlockMetadata())
                                      .getImage()
                                      .getWidth()) {
                    throw new LuaException("number must be from 0 to row");
                }
                Color color =
                        this.tileP.getPainting(this.tileP.getBlockMetadata())
                                  .getImage()
                                  .getPixelColor(px, py);
                return new Object[]{color.getRed(), color.getGreen(), color.getBlue()};
            }
            case 2: {
                if (arguments.length < 5) {
                    throw new LuaException("too few arguments");
                }
                if (arguments.length > 5) {
                    throw new LuaException("too many arguments");
                }
                if (!(arguments[0] instanceof Double) || !(arguments[1] instanceof Double)
                    || !(arguments[2] instanceof Double)
                    || !(arguments[3] instanceof Double)
                    || !(arguments[4] instanceof Double)) {
                    throw new LuaException("arguments must be a number");
                }
                int px = ((Double)arguments[0]).intValue();
                int py = ((Double)arguments[1]).intValue();
                if (px < 0
                    || px > this.tileP.getPainting(this.tileP.getBlockMetadata())
                                      .getImage()
                                      .getWidth()
                    || py < 0
                    || py > this.tileP.getPainting(this.tileP.getBlockMetadata())
                                      .getImage()
                                      .getWidth()) {
                    throw new LuaException("number must be from 0 to row");
                }
                Color color = null;
                try {
                    color = new Color(
                            ((Double)arguments[2]).intValue(),
                            ((Double)arguments[3]).intValue(),
                            ((Double)arguments[4]).intValue());
                } catch (IllegalArgumentException e) {
                    throw new LuaException(e.getMessage());
                } finally {
                    ItemStack itemStack = new ItemStack(Core.instance.painting.itemPaintBrush);
                    Core.instance.painting.itemPaintBrush.setColor(itemStack, color.getRGB());
                    this.tileP.getPainting(this.tileP.getBlockMetadata())
                              .usePaintingTool(itemStack, px, py);
                }
                return new Object[]{true};
            }
            case 3: {
                if (arguments.length < 3) {
                    throw new LuaException("too few arguments");
                }
                if (arguments.length > 3) {
                    throw new LuaException("too many arguments");
                }
                if (!(arguments[0] instanceof Double) || !(arguments[1] instanceof Double)
                    || !(arguments[2] instanceof Double)) {
                    throw new LuaException("arguments must be a number");
                }
                Color color = null;
                try {
                    color = new Color(
                            ((Double)arguments[0]).intValue(),
                            ((Double)arguments[1]).intValue(),
                            ((Double)arguments[2]).intValue());
                } catch (IllegalArgumentException e) {
                    throw new LuaException(e.getMessage());
                } finally {
                    this.tileP.getPainting(this.tileP.getBlockMetadata()).getImage().fill(color);
                }
                return new Object[]{true};
            }
        }
        return null;
    }
    
    @Override
    public void attach(IComputerAccess computer) {
    }
    
    @Override
    public void detach(IComputerAccess computer) {
    }
    
    @Override
    public boolean equals(IPeripheral other) {
        if (other instanceof TileEntityPaintingPeripheral) {
            return this.tileP.equals(((TileEntityPaintingPeripheral)other).tileP);
        } else {
            return false;
        }
    }
    
}
