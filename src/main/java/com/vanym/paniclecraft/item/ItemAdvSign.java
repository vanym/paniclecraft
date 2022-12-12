package com.vanym.paniclecraft.item;

import java.util.Arrays;
import java.util.List;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.GUIs;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemAdvSign extends ItemMod3 {
    
    public Block wall;
    
    public Block post;
    
    public ItemAdvSign(Block par2Post, Block par3Wall) {
        this.setMaxStackSize(16);
        this.setUnlocalizedName("advSign");
        this.post = par2Post;
        this.wall = par3Wall;
    }
    
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    @SideOnly(Side.CLIENT)
    public void addInformation(
            ItemStack par1ItemStack,
            EntityPlayer par2EntityPlayer,
            List par3List,
            boolean par4) {
        if (par1ItemStack.hasTagCompound()) {
            NBTTagCompound tag = par1ItemStack.getTagCompound();
            if (tag.hasKey("SignText")) {
                if (GuiScreen.isShiftKeyDown()) {
                    String text = tag.getString("SignText");
                    String[] textAr =
                            text.split(TileEntityAdvSign.separator,
                                       (text + '\u0000').split(TileEntityAdvSign.separator).length);
                    par3List.addAll(Arrays.asList(textAr));
                } else {
                    par3List.add(StatCollector.translateToLocal("text.pressShiftToSeeText").trim());
                }
            }
        }
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3) {
        if (par1ItemStack.hasTagCompound() && par3.isSneaking()) {
            NBTTagCompound tag = par1ItemStack.getTagCompound();
            if (tag.hasKey("SignText")) {
                tag.removeTag("SignText");
            }
            if (tag.hasKey("red")) {
                tag.removeTag("red");
            }
            if (tag.hasKey("green")) {
                tag.removeTag("green");
            }
            if (tag.hasKey("blue")) {
                tag.removeTag("blue");
            }
            if (tag.hasNoTags()) {
                par1ItemStack.setTagCompound(null);
            }
        }
        return par1ItemStack;
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
        if (!par2EntityPlayer.isSneaking()) {
            TileEntity tile = par3World.getTileEntity(par4, par5, par6);
            if (tile != null) {
                if (tile instanceof TileEntitySign) {
                    TileEntitySign tileS = (TileEntitySign)tile;
                    if (!par1ItemStack.hasTagCompound()) {
                        NBTTagCompound var1 = new NBTTagCompound();
                        par1ItemStack.setTagCompound(var1);
                    }
                    NBTTagCompound tag = par1ItemStack.getTagCompound();
                    tag.setString("SignText",
                                  tileS.signText[0] + TileEntityAdvSign.separator +
                                              tileS.signText[1] + TileEntityAdvSign.separator +
                                              tileS.signText[2] + TileEntityAdvSign.separator +
                                              tileS.signText[3]);
                    tag.setByte("red", (byte)127);
                    tag.setByte("green", (byte)127);
                    tag.setByte("blue", (byte)127);
                    return true;
                } else if (tile instanceof TileEntityAdvSign) {
                    TileEntityAdvSign tileAS = (TileEntityAdvSign)tile;
                    if (!par1ItemStack.hasTagCompound()) {
                        NBTTagCompound var1 = new NBTTagCompound();
                        par1ItemStack.setTagCompound(var1);
                    }
                    NBTTagCompound tag = par1ItemStack.getTagCompound();
                    tag.setString("SignText", tileAS.signText);
                    tag.setByte("red", tileAS.red);
                    tag.setByte("green", tileAS.green);
                    tag.setByte("blue", tileAS.blue);
                    return true;
                }
            }
        }
        if (par7 == 0) {
            return false;
        } else if (!par3World.getBlock(par4, par5, par6).getMaterial().isSolid()) {
            return false;
        } else {
            if (par7 == 1) {
                ++par5;
            }
            
            if (par7 == 2) {
                --par6;
            }
            
            if (par7 == 3) {
                ++par6;
            }
            
            if (par7 == 4) {
                --par4;
            }
            
            if (par7 == 5) {
                ++par4;
            }
            
            if (!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack)) {
                return false;
            } else if (!this.post.canPlaceBlockAt(par3World, par4, par5, par6)) {
                return false;
            } else {
                if (par7 == 1) {
                    int var11 = MathHelper.floor_double((double)((par2EntityPlayer.rotationYaw +
                                                                  180.0F) *
                                                                 16.0F / 360.0F) +
                                                        0.5D)
                                & 15;
                    par3World.setBlock(par4, par5, par6, this.post, var11, 3);
                } else {
                    par3World.setBlock(par4, par5, par6, this.wall, par7, 3);
                }
                
                --par1ItemStack.stackSize;
                TileEntityAdvSign var12 =
                        (TileEntityAdvSign)par3World.getTileEntity(par4, par5, par6);
                
                if (var12 != null) {
                    if (par1ItemStack.hasTagCompound()) {
                        NBTTagCompound tag = par1ItemStack.getTagCompound();
                        if (tag.hasKey("SignText")) {
                            var12.signText = tag.getString("SignText");
                        }
                        if (tag.hasKey("red")) {
                            var12.red = tag.getByte("red");
                        }
                        if (tag.hasKey("green")) {
                            var12.green = tag.getByte("green");
                        }
                        if (tag.hasKey("blue")) {
                            var12.blue = tag.getByte("blue");
                        }
                    }
                    par2EntityPlayer.openGui(Core.instance, GUIs.ADVSIGN.ordinal(), par3World, par4,
                                             par5, par6);
                }
                
                return true;
            }
        }
    }
}
