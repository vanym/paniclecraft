package com.vanym.paniclecraft.block;

import java.util.Random;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockSign;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class BlockAdvSign extends BlockSign {
    
    public BlockAdvSign(boolean isPost) {
        super(TileEntityAdvSign.class, isPost);
        this.setHardness(1.0F);
        this.setBlockName("advSign");
    }
    
    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return Core.instance.advSign.itemAdvSign;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_) {
        return Core.instance.advSign.itemAdvSign;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        TileEntityAdvSign tile = (TileEntityAdvSign)world.getTileEntity(x, y, z);
        ItemStack itemS = new ItemStack(Core.instance.advSign.itemAdvSign);
        if (tile == null) {
            return itemS;
        }
        NBTTagCompound var1 = new NBTTagCompound();
        itemS.setTagCompound(var1);
        var1.setString("SignText", tile.signText);
        return itemS;
    }
}
