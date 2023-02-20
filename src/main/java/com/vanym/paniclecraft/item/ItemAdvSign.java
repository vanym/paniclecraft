package com.vanym.paniclecraft.item;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.GUIs;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemAdvSign extends ItemMod3 {
    
    public static final String TAG_SIGN = "Sign";
    
    public ItemAdvSign() {
        this.setMaxStackSize(16);
        this.setUnlocalizedName("advSign");
    }
    
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    @SideOnly(Side.CLIENT)
    public void addInformation(
            ItemStack itemStack,
            EntityPlayer entityPlayer,
            List list,
            boolean advancedItemTooltips) {
        if (itemStack.hasTagCompound()) {
            NBTTagCompound tag = itemStack.getTagCompound();
            if (tag.hasKey(TAG_SIGN, 10)) {
                if (GuiScreen.isShiftKeyDown()) {
                    NBTTagCompound signTag = tag.getCompoundTag(TAG_SIGN);
                    NBTTagList tagLines = signTag.getTagList(TileEntityAdvSign.TAG_LINES, 8);
                    IntStream.range(0, tagLines.tagCount())
                             .mapToObj(tagLines::getStringTagAt)
                             .forEachOrdered(list::add);
                } else {
                    list.add(StatCollector.translateToLocal("text.pressShiftToSeeText"));
                }
            }
        }
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (stack.hasTagCompound() && player.isSneaking()) {
            NBTTagCompound tag = stack.getTagCompound();
            tag.removeTag(TAG_SIGN);
            if (tag.hasNoTags()) {
                stack.setTagCompound(null);
            }
        }
        return stack;
    }
    
    @Override
    public boolean onItemUse(
            ItemStack stack,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z,
            int side,
            float hitX,
            float hitY,
            float hitZ) {
        if (!player.isSneaking()) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile != null) {
                NBTTagCompound signTag = null;
                if (tile instanceof TileEntitySign) {
                    TileEntitySign tileS = (TileEntitySign)tile;
                    signTag = new NBTTagCompound();
                    NBTTagList linesTag = new NBTTagList();
                    Arrays.stream(tileS.signText)
                          .map(NBTTagString::new)
                          .forEachOrdered(linesTag::appendTag);
                    signTag.setTag(TileEntityAdvSign.TAG_LINES, linesTag);
                } else if (tile instanceof TileEntityAdvSign) {
                    TileEntityAdvSign tileAS = (TileEntityAdvSign)tile;
                    signTag = new NBTTagCompound();
                    tileAS.writeToNBT(signTag, true);
                }
                if (signTag != null) {
                    if (!stack.hasTagCompound()) {
                        stack.setTagCompound(new NBTTagCompound());
                    }
                    NBTTagCompound tag = stack.getTagCompound();
                    tag.setTag(TAG_SIGN, signTag);
                    return true;
                }
            }
        }
        if (!world.getBlock(x, y, z).getMaterial().isSolid()) {
            return false;
        }
        ForgeDirection pside = ForgeDirection.getOrientation(side);
        x += pside.offsetX;
        y += pside.offsetY;
        z += pside.offsetZ;
        if (!player.canPlayerEdit(x, y, z, side, stack)
            || !Core.instance.advSign.blockAdvSign.canPlaceBlockAt(world, x, y, z)
            || !world.setBlock(x, y, z, Core.instance.advSign.blockAdvSign, side, 3)) {
            return false;
        }
        --stack.stackSize;
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null && tile instanceof TileEntityAdvSign) {
            TileEntityAdvSign tileAS = (TileEntityAdvSign)tile;
            if (stack.hasTagCompound()) {
                NBTTagCompound tag = stack.getTagCompound();
                if (tag.hasKey(TAG_SIGN, 10)) {
                    NBTTagCompound signTag = tag.getCompoundTag(TAG_SIGN);
                    tileAS.readFromNBT(signTag, true);
                }
            }
            if (pside == ForgeDirection.UP) {
                tileAS.setStick(true);
                double direction = Math.round(180.0D + player.rotationYaw);
                tileAS.setDirection(direction);
            }
            if (pside == ForgeDirection.DOWN) {
                int rot = MathHelper.floor_double((player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
                tileAS.setDirection(rot * 90.0D);
            }
            tileAS.setEditor(player);
            player.openGui(Core.instance, GUIs.ADVSIGN.ordinal(), world, x, y, z);
        }
        return true;
    }
}
