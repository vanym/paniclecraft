package com.vanym.paniclecraft.item;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.GUIs;
import com.vanym.paniclecraft.core.component.advsign.AdvSignText;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;
import com.vanym.paniclecraft.utils.ItemUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemAdvSign extends ItemMod3 {
    
    public static final String TAG_SIGN = "Sign";
    
    public ItemAdvSign() {
        this.setMaxStackSize(16);
        this.setRegistryName("advanced_sign");
    }
    
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    @SideOnly(Side.CLIENT)
    public void addInformation(
            ItemStack stack,
            EntityPlayer player,
            List list,
            boolean advancedItemTooltips) {
        getSide(stack, !GuiScreen.isCtrlKeyDown()).map(AdvSignText::getLines).ifPresent(lines-> {
            if (GuiScreen.isShiftKeyDown()) {
                lines.stream().map(IChatComponent::getUnformattedText).forEachOrdered(list::add);
            } else {
                list.add(StatCollector.translateToLocal(this.getUnlocalizedName() +
                    ".showtext"));
            }
        });
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.isSneaking()) {
            removeSign(stack);
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
                    AdvSignText text = new AdvSignText();
                    List<IChatComponent> lines = text.getLines();
                    Arrays.stream(tileS.signText)
                          .map(ChatComponentText::new)
                          .forEachOrdered(lines::add);
                    signTag.setTag(TileEntityAdvSign.TAG_FRONTTEXT, text.serializeNBT());
                } else if (tile instanceof TileEntityAdvSign) {
                    TileEntityAdvSign tileAS = (TileEntityAdvSign)tile;
                    signTag = new NBTTagCompound();
                    tileAS.writeToNBT(signTag, true);
                }
                if (signTag != null) {
                    if (TileEntityAdvSign.isValidTag(signTag)) {
                        putSign(stack, signTag);
                    }
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
        if (tile instanceof TileEntityAdvSign) {
            TileEntityAdvSign tileAS = (TileEntityAdvSign)tile;
            getSign(stack).filter(TileEntityAdvSign::isValidTag)
                          .ifPresent(signTag->tileAS.readFromNBT(signTag, true));
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
    
    public static ItemStack getSavedSign(TileEntityAdvSign tileAS) {
        ItemStack stack = new ItemStack(Core.instance.advSign.itemAdvSign);
        if (tileAS == null || (tileAS.getFront().isEmpty() && tileAS.getBack().isEmpty())) {
            return stack;
        }
        NBTTagCompound signTag = new NBTTagCompound();
        tileAS.writeToNBT(signTag, true);
        putSign(stack, signTag);
        return stack;
    }
    
    protected static void putSign(ItemStack stack, NBTTagCompound tag) {
        ItemUtils.getOrCreateTag(stack).setTag(TAG_SIGN, tag);
    }
    
    protected static void removeSign(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        tag.removeTag(TAG_SIGN);
        ItemUtils.cleanTag(stack);
    }
    
    public static Optional<NBTTagCompound> getSign(ItemStack stack) {
        return ItemUtils.getTag(stack)
                        .filter(tag->tag.hasKey(TAG_SIGN, 10))
                        .map(tag->tag.getCompoundTag(TAG_SIGN));
    }
    
    protected static Optional<AdvSignText> getSide(ItemStack stack, boolean front) {
        String TAG_TEXT = front ? TileEntityAdvSign.TAG_FRONTTEXT : TileEntityAdvSign.TAG_BACKTEXT;
        return getSign(stack).filter(tag->tag.hasKey(TAG_TEXT, 10))
                             .map(tag->tag.getCompoundTag(TAG_TEXT))
                             .map(AdvSignText::new);
    }
}
