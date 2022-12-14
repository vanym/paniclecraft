package com.vanym.paniclecraft.item;

import java.util.List;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemChessDesk extends ItemMod3 {
    
    public ItemChessDesk() {
        super();
        // this.setMaxStackSize(1);
        this.setUnlocalizedName("chessDesk");
    }
    
    @Override
    public boolean onItemUse(
            ItemStack par1ItemStack,
            EntityPlayer par2EntityPlayer,
            World par3World,
            int par4,
            int par5,
            int par6,
            int par7,
            float par8,
            float par9,
            float par10) {
        switch (par7) {
            case 0:
                --par5;
            break;
            case 1:
                ++par5;
            break;
            case 2:
                --par6;
            break;
            case 3:
                ++par6;
            break;
            case 4:
                --par4;
            break;
            case 5:
                ++par4;
            break;
        }
        if (!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack)
            || !Core.instance.deskgame.blockChessDesk.canPlaceBlockAt(par3World, par4, par5,
                                                                      par6)) {
            return false;
        } else {
            --par1ItemStack.stackSize;
            int var11 =
                    MathHelper.floor_double((double)(par2EntityPlayer.rotationYaw * 4.0F / 360.0F) +
                                            0.5D)
                        & 3;
            par3World.setBlock(par4, par5, par6, Core.instance.deskgame.blockChessDesk, var11, 3);
            TileEntityChessDesk tile =
                    (TileEntityChessDesk)par3World.getTileEntity(par4, par5, par6);
            if (par1ItemStack.hasTagCompound()) {
                NBTTagCompound var1 = par1ItemStack.getTagCompound();
                if (var1.hasKey("ChessData")) {
                    NBTTagCompound tagData = var1.getCompoundTag("ChessData");
                    if (!tagData.hasNoTags()) {
                        tagData.setInteger("x", par4);
                        tagData.setInteger("y", par5);
                        tagData.setInteger("z", par6);
                        tile.readFromNBT(tagData);
                    }
                }
            }
            return true;
        }
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
                if (tag.hasKey("ChessData")) {
                    par3List.add(StatCollector.translateToLocal("text.chessGameHaveSave").trim());
                }
            }
        }
    }
}
