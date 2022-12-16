package com.vanym.paniclecraft.plugins.computercraft.t.p;

import java.awt.Color;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPaintBrush;
import com.vanym.paniclecraft.item.ItemPalette;
import com.vanym.paniclecraft.utils.MainUtils;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.ForgeDirection;

public class PeripheralPaintBrush implements IPeripheral {
    
    private final ITurtleAccess turtle;
    
    public PeripheralPaintBrush(ITurtleAccess par1Turtle) {
        this.turtle = par1Turtle;
    }
    
    @Override
    public String getType() {
        return "paintBrush";
    }
    
    @Override
    public String[] getMethodNames() {
        return new String[]{"getRow", "getRowUp", "getRowDown", "useBrush", "useBrushUp",
                            "useBrushDown", "getBrushColor", "setBrushColor"};
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
                Picture tileP = this.findPainting(-1);
                if (tileP == null) {
                    throw new LuaException("cat\'t find painting");
                }
                return new Object[]{tileP.getWidth()};
            }
            case 1: {
                Picture tileP = this.findPainting(1);
                if (tileP == null) {
                    throw new LuaException("cat\'t find painting");
                }
                return new Object[]{tileP.getWidth()};
            }
            case 2: {
                Picture tileP = this.findPainting(0);
                if (tileP == null) {
                    throw new LuaException("cat\'t find painting");
                }
                return new Object[]{tileP.getWidth()};
            }
            case 3: {
                Picture tileP = this.findPainting(-1);
                if (tileP == null) {
                    throw new LuaException("cat\'t find painting");
                }
                ItemStack is =
                        this.turtle.getInventory().getStackInSlot(this.turtle.getSelectedSlot());
                if (is == null || !(is.getItem() instanceof ItemPaintBrush)) {
                    throw new LuaException("cat\'t find brush");
                }
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
                if (px < 0 || px > tileP.getWidth()
                    || py < 0
                    || py > tileP.getHeight()) {
                    throw new LuaException("number must be from 0 to row");
                }
                tileP.usePaintingTool(is, px, py);
                return new Object[]{true};
            }
            case 4: {
                Picture tileP = this.findPainting(1);
                if (tileP == null) {
                    throw new LuaException("cat\'t find painting");
                }
                ItemStack is =
                        this.turtle.getInventory().getStackInSlot(this.turtle.getSelectedSlot());
                if (is == null || !(is.getItem() instanceof ItemPaintBrush)) {
                    throw new LuaException("cat\'t find brush");
                }
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
                if (px < 0 || px > tileP.getWidth()
                    || py < 0
                    || py > tileP.getHeight()) {
                    throw new LuaException("number must be from 0 to row");
                }
                switch (this.turtle.getDirection()) {
                    case 2:
                        px = tileP.getWidth() - 1 - px;
                        py = tileP.getHeight() - 1 - py;
                    break;
                    case 3:
                    break;
                    case 4:
                        px = px + py;
                        py = px - py;
                        px = px - py;
                        py = tileP.getHeight() - 1 - py;
                    break;
                    case 5:
                        px = px + py;
                        py = px - py;
                        px = px - py;
                        px = tileP.getWidth() - 1 - px;
                    break;
                }
                tileP.usePaintingTool(is, px, py);
                return new Object[]{true};
            }
            case 5: {
                Picture tileP = this.findPainting(0);
                if (tileP == null) {
                    throw new LuaException("cat\'t find painting");
                }
                ItemStack is =
                        this.turtle.getInventory().getStackInSlot(this.turtle.getSelectedSlot());
                if (is == null || !(is.getItem() instanceof ItemPaintBrush)) {
                    throw new LuaException("cat\'t find brush");
                }
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
                if (px < 0 || px > tileP.getWidth()
                    || py < 0
                    || py > tileP.getHeight()) {
                    throw new LuaException("number must be from 0 to row");
                }
                switch (this.turtle.getDirection()) {
                    case 2:
                        px = tileP.getWidth() - 1 - px;
                        py = tileP.getHeight() - 1 - py;
                    break;
                    case 3:
                    break;
                    case 4:
                        px = px + py;
                        py = px - py;
                        px = px - py;
                        px = tileP.getWidth() - 1 - px;
                    break;
                    case 5:
                        px = px + py;
                        py = px - py;
                        px = px - py;
                        py = tileP.getHeight() - 1 - py;
                    break;
                }
                tileP.usePaintingTool(is, px, py);
                return new Object[]{true};
            }
            case 6: {
                ItemStack is =
                        this.turtle.getInventory().getStackInSlot(this.turtle.getSelectedSlot());
                if (is == null || !(is.getItem() instanceof ItemPaintBrush)) {
                    throw new LuaException("cat\'t find brush");
                }
                Color colors =
                        MainUtils.getColorFromInt(Core.instance.painting.itemPaintBrush.getColor(is));
                return new Object[]{colors.getRed(), colors.getGreen(), colors.getBlue()};
            }
            case 7: {
                IInventory ti = this.turtle.getInventory();
                for (int i = 0; i < ti.getSizeInventory(); i++) {
                    ItemStack ps = ti.getStackInSlot(i);
                    if (ps != null && ps.getItem() instanceof ItemPalette) {
                        ItemStack is = ti.getStackInSlot(this.turtle.getSelectedSlot());
                        if (is == null || !(is.getItem() instanceof ItemPaintBrush)) {
                            throw new LuaException("cat\'t find brush");
                        }
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
                        int red = ((Double)arguments[0]).intValue();
                        int green = ((Double)arguments[1]).intValue();
                        int blue = ((Double)arguments[2]).intValue();
                        if (red < 0 || red >= 256 || green < 0 || green >= 256) {
                            throw new LuaException("number must be from 0 to 255");
                        }
                        if (blue < 0 || blue >= 256) {
                            throw new LuaException("number must be from 0 to 255");
                        }
                        Core.instance.painting.itemPaintBrush.setColor(is,
                                                                       MainUtils.getIntFromRGB(red,
                                                                                               green,
                                                                                               blue));
                        return new Object[]{true};
                    }
                }
                throw new LuaException("cat\'t find palette");
            }
        }
        return null;
    }
    
    public Picture findPainting(int side) {
        if (side < 0) {
            side = this.turtle.getDirection();
        }
        ChunkCoordinates pos = this.turtle.getPosition();
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        TileEntity tile = this.turtle.getWorld()
                                     .getTileEntity(pos.posX + dir.offsetX, pos.posY + dir.offsetY,
                                                    pos.posZ + dir.offsetZ);
        if (tile == null || !(tile instanceof ISidePictureProvider)) {
            return null;
        }
        ISidePictureProvider tileP = (ISidePictureProvider)tile;
        return tileP.getPainting(dir.getOpposite().ordinal());
    }
    
    @Override
    public void attach(IComputerAccess computer) {
    }
    
    @Override
    public void detach(IComputerAccess computer) {
    }
    
    @Override
    public boolean equals(IPeripheral other) {
        return super.equals((Object)other);
    }
    
}
