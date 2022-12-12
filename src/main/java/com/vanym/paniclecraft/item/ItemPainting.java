package com.vanym.paniclecraft.item;

import java.util.List;

import com.vanym.paniclecraft.init.ModItems;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;
import com.vanym.paniclecraft.utils.Painting;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemPainting extends ItemMod3 {
    
    public static int paintingPlaceStack = 2;
    
    public ItemPainting() {
        super();
        this.setUnlocalizedName("painting");
    }
    
    @Override
    public boolean onItemUse(
            ItemStack par1ItemStack,
            EntityPlayer par2EntityPlayer,
            World par3World,
            int x,
            int y,
            int z,
            int side,
            float par8,
            float par9,
            float par10) {
        if (!par2EntityPlayer.isSneaking()) {
            TileEntity tile = par3World.getTileEntity(x, y, z);
            if (tile != null && tile instanceof TileEntityPaintingFrame) {
                TileEntityPaintingFrame tilePF = (TileEntityPaintingFrame)tile;
                if (tilePF.getPainting(side) == null) {
                    Painting pic = new Painting(tilePF);
                    if (par1ItemStack.hasTagCompound()) {
                        NBTTagCompound tag = par1ItemStack.getTagCompound();
                        if (tag.hasKey("PaintingData")) {
                            NBTTagCompound tagData = tag.getCompoundTag("PaintingData");
                            if (!tagData.hasNoTags()) {
                                pic.readFromNBT(tagData);
                                if (side == 0) {
                                    float rotF = par2EntityPlayer.rotationYaw + 45.0F;
                                    while (rotF >= 360.0F) {
                                        rotF -= 360.0F;
                                    }
                                    while (rotF < 0.0F) {
                                        rotF += 360.0F;
                                    }
                                    int rot = (int)(rotF / 90.0F);
                                    switch (rot) {
                                        case 1:
                                            pic.rotatePicLeft();
                                        break;
                                        case 2:
                                            pic.rotatePic180();
                                        break;
                                        case 3:
                                            pic.rotatePicRight();
                                        break;
                                    }
                                }
                                if (side == 1) {
                                    float rotF = par2EntityPlayer.rotationYaw + 45.0F;
                                    while (rotF >= 360.0F) {
                                        rotF -= 360.0F;
                                    }
                                    while (rotF < 0.0F) {
                                        rotF += 360.0F;
                                    }
                                    int rot = (int)(rotF / 90.0F);
                                    switch (rot) {
                                        case 1:
                                            pic.rotatePicRight();
                                        break;
                                        case 2:
                                            pic.rotatePic180();
                                        break;
                                        case 3:
                                            pic.rotatePicLeft();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    tilePF.setPainting(side, pic);
                    --par1ItemStack.stackSize;
                    return true;
                }
                return false;
            }
        }
        int sx = x;
        int sy = y;
        int sz = z;
        int i = 0;
        for(i = 0;
            (i < paintingPlaceStack) && par3World.getBlock(x, y, z) == ModItems.blockPainting
                && par3World.getBlockMetadata(x, y, z) == side
                && !par2EntityPlayer.isSneaking();
            i++) {
            switch (side) {
                case 0: {
                    float rotF = par2EntityPlayer.rotationYaw + 45.0F;
                    while (rotF >= 360.0F) {
                        rotF -= 360.0F;
                    }
                    while (rotF < 0.0F) {
                        rotF += 360.0F;
                    }
                    int rot = (int)(rotF / 90.0F);
                    switch (rot) {
                        case 0:
                            z++;
                        break;
                        case 1:
                            x--;
                        break;
                        case 2:
                            z--;
                        break;
                        case 3:
                            x++;
                        break;
                    }
                }
                break;
                case 1: {
                    float rotF = par2EntityPlayer.rotationYaw + 45.0F;
                    while (rotF >= 360.0F) {
                        rotF -= 360.0F;
                    }
                    while (rotF < 0.0F) {
                        rotF += 360.0F;
                    }
                    int rot = (int)(rotF / 90.0F);
                    switch (rot) {
                        case 0:
                            z++;
                        break;
                        case 1:
                            x--;
                        break;
                        case 2:
                            z--;
                        break;
                        case 3:
                            x++;
                        break;
                    }
                }
                break;
                case 2: {
                    float rot = (par2EntityPlayer.rotationYaw - 0.0F);
                    while (rot >= 180.0F) {
                        rot -= 360.0F;
                    }
                    while (rot <= -180.0F) {
                        rot += 360.0F;
                    }
                    if (Math.abs(par2EntityPlayer.rotationPitch) - Math.abs(rot) >= 0) {
                        if (par2EntityPlayer.rotationPitch >= 0) {
                            y--;
                        } else {
                            y++;
                        }
                    } else {
                        if (rot >= 0) {
                            x--;
                        } else {
                            x++;
                        }
                    }
                }
                break;
                case 3: {
                    float rot = (par2EntityPlayer.rotationYaw - 180.0F);
                    while (rot >= 180.0F) {
                        rot -= 360.0F;
                    }
                    while (rot <= -180.0F) {
                        rot += 360.0F;
                    }
                    if (Math.abs(par2EntityPlayer.rotationPitch) - Math.abs(rot) >= 0) {
                        if (par2EntityPlayer.rotationPitch >= 0) {
                            y--;
                        } else {
                            y++;
                        }
                    } else {
                        if (rot >= 0) {
                            x++;
                        } else {
                            x--;
                        }
                    }
                }
                break;
                case 5: {
                    float rot = (par2EntityPlayer.rotationYaw - 90.0F);
                    while (rot >= 180.0F) {
                        rot -= 360.0F;
                    }
                    while (rot <= -180.0F) {
                        rot += 360.0F;
                    }
                    if (Math.abs(par2EntityPlayer.rotationPitch) - Math.abs(rot) >= 0) {
                        if (par2EntityPlayer.rotationPitch >= 0) {
                            y--;
                        } else {
                            y++;
                        }
                    } else {
                        if (rot >= 0) {
                            z--;
                        } else {
                            z++;
                        }
                    }
                }
                break;
                case 4: {
                    float rot = (par2EntityPlayer.rotationYaw - 270.0F);
                    while (rot >= 180.0F) {
                        rot -= 360.0F;
                    }
                    while (rot <= -180.0F) {
                        rot += 360.0F;
                    }
                    if (Math.abs(par2EntityPlayer.rotationPitch) - Math.abs(rot) >= 0) {
                        if (par2EntityPlayer.rotationPitch >= 0) {
                            y--;
                        } else {
                            y++;
                        }
                    } else {
                        if (rot >= 0) {
                            z++;
                        } else {
                            z--;
                        }
                    }
                }
                break;
            }
            if (sx == x && sy == y && sz == z) {
                break;
            }
        }
        if (i == 0) {
            switch (side) {
                case 0:
                    --y;
                break;
                case 1:
                    ++y;
                break;
                case 2:
                    --z;
                break;
                case 3:
                    ++z;
                break;
                case 4:
                    --x;
                break;
                case 5:
                    ++x;
                break;
            }
        }
        if (!par2EntityPlayer.canPlayerEdit(x, y, z, side, par1ItemStack)
            || !ModItems.blockPainting.canPlaceBlockAt(par3World, x, y, z)) {
            return false;
        } else {
            par3World.setBlock(x, y, z, ModItems.blockPainting, side, 3);
            if (par1ItemStack.hasTagCompound()) {
                NBTTagCompound tag = par1ItemStack.getTagCompound();
                if (tag.hasKey("PaintingData")) {
                    NBTTagCompound tagData = tag.getCompoundTag("PaintingData");
                    if (!tagData.hasNoTags()) {
                        TileEntityPainting tileP =
                                (TileEntityPainting)par3World.getTileEntity(x, y, z);
                        tileP.getPainting(side).readFromNBT(tagData);
                        if (side == 0) {
                            float rotF = par2EntityPlayer.rotationYaw + 45.0F;
                            while (rotF >= 360.0F) {
                                rotF -= 360.0F;
                            }
                            while (rotF < 0.0F) {
                                rotF += 360.0F;
                            }
                            int rot = (int)(rotF / 90.0F);
                            switch (rot) {
                                case 1:
                                    tileP.getPainting(side).rotatePicLeft();
                                break;
                                case 2:
                                    tileP.getPainting(side).rotatePic180();
                                break;
                                case 3:
                                    tileP.getPainting(side).rotatePicRight();
                                break;
                            }
                        }
                        if (side == 1) {
                            float rotF = par2EntityPlayer.rotationYaw + 45.0F;
                            while (rotF >= 360.0F) {
                                rotF -= 360.0F;
                            }
                            while (rotF < 0.0F) {
                                rotF += 360.0F;
                            }
                            int rot = (int)(rotF / 90.0F);
                            switch (rot) {
                                case 1:
                                    tileP.getPainting(side).rotatePicRight();
                                break;
                                case 2:
                                    tileP.getPainting(side).rotatePic180();
                                break;
                                case 3:
                                    tileP.getPainting(side).rotatePicLeft();
                                break;
                            }
                        }
                    }
                }
            }
            
            --par1ItemStack.stackSize;
            
            return true;
        }
        // }
    }
    
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    @SideOnly(Side.CLIENT)
    public void addInformation(
            ItemStack par1ItemStack,
            EntityPlayer par2EntityPlayer,
            List par3List,
            boolean par4) {
        if (GuiScreen.isShiftKeyDown()) {
            if (par1ItemStack.hasTagCompound()) {
                NBTTagCompound tag = par1ItemStack.getTagCompound();
                if (tag.hasKey("PaintingData")) {
                    par3List.add(StatCollector.translateToLocal("text.paintingHaveSave").trim());
                }
            }
        }
    }
}
