package com.vanym.paniclecraft.item;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.GUIs;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAdvSign extends ItemMod3 {
    
    public static final String TAG_SIGN = "Sign";
    
    public ItemAdvSign() {
        this.setMaxStackSize(16);
        this.setUnlocalizedName("advanced_sign");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(
            ItemStack itemStack,
            @Nullable World world,
            List<String> list,
            ITooltipFlag flag) {
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
                    list.add(I18n.format("item.advanced_sign.showtext"));
                }
            }
        }
    }
    
    @Override
    @Nullable
    public EntityEquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EntityEquipmentSlot.HEAD;
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(
            World world,
            EntityPlayer player,
            EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.hasTagCompound() && player.isSneaking()) {
            NBTTagCompound tag = stack.getTagCompound();
            tag.removeTag(TAG_SIGN);
            if (tag.hasNoTags()) {
                stack.setTagCompound(null);
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        } else {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
    }
    
    @Override
    public EnumActionResult onItemUse(
            EntityPlayer player,
            World world,
            BlockPos pos,
            EnumHand hand,
            EnumFacing facing,
            float hitX,
            float hitY,
            float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.isSneaking()) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null) {
                NBTTagCompound signTag = null;
                if (tile instanceof TileEntitySign) {
                    TileEntitySign tileS = (TileEntitySign)tile;
                    signTag = new NBTTagCompound();
                    NBTTagList linesTag = new NBTTagList();
                    Arrays.stream(tileS.signText)
                          .map(ITextComponent::getFormattedText)
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
                    return EnumActionResult.SUCCESS;
                }
            }
        }
        if (!world.getBlockState(pos).getMaterial().isSolid()) {
            return EnumActionResult.FAIL;
        }
        pos = pos.offset(facing);
        Block block = Core.instance.advSign.blockAdvSign;
        if (!player.canPlayerEdit(pos, facing, stack)
            || !block.canPlaceBlockAt(world, pos)
            || !world.setBlockState(pos, block.getStateForPlacement(world, pos, facing,
                                                                    hitX, hitY, hitZ,
                                                                    0, player, hand),
                                    11)) {
            return EnumActionResult.FAIL;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileEntityAdvSign) {
            TileEntityAdvSign tileAS = (TileEntityAdvSign)tile;
            if (stack.hasTagCompound()) {
                NBTTagCompound tag = stack.getTagCompound();
                if (tag.hasKey(TAG_SIGN, 10)) {
                    NBTTagCompound signTag = tag.getCompoundTag(TAG_SIGN);
                    tileAS.readFromNBT(signTag, true);
                }
            }
            if (facing == EnumFacing.UP) {
                tileAS.setStick(true);
                double direction = Math.round(180.0D + player.rotationYaw);
                tileAS.setDirection(direction);
            }
            if (facing == EnumFacing.DOWN) {
                tileAS.setDirection(player.getHorizontalFacing().getHorizontalAngle());
            }
            tileAS.setEditor(player);
        }
        stack.shrink(1);
        player.openGui(Core.instance, GUIs.ADVSIGN.ordinal(),
                       world, pos.getX(), pos.getY(), pos.getZ());
        return EnumActionResult.SUCCESS;
    }
    
    public static ItemStack getSavedSign(TileEntityAdvSign tileAS) {
        ItemStack stack = new ItemStack(Core.instance.advSign.itemAdvSign);
        if (tileAS == null || tileAS.lines.stream().allMatch(String::isEmpty)) {
            return stack;
        }
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound signTag = new NBTTagCompound();
        tileAS.writeToNBT(signTag, true);
        tag.setTag(ItemAdvSign.TAG_SIGN, signTag);
        stack.setTagCompound(tag);
        return stack;
    }
}
