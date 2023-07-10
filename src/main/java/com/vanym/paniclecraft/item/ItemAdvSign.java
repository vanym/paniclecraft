package com.vanym.paniclecraft.item;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.GUIs;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;
import com.vanym.paniclecraft.utils.ItemUtils;

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
        this.setRegistryName("advanced_sign");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(
            ItemStack stack,
            @Nullable World world,
            List<String> list,
            ITooltipFlag flag) {
        getLines(stack).ifPresent(lines-> {
            if (GuiScreen.isShiftKeyDown()) {
                IntStream.range(0, lines.tagCount())
                         .mapToObj(lines::getStringTagAt)
                         .forEachOrdered(list::add);
            } else {
                list.add(I18n.format(this.getUnlocalizedName() +
                    ".showtext"));
            }
        });
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
        if (getSign(stack).isPresent() && player.isSneaking()) {
            removeSign(stack);
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
                    putSign(stack, signTag);
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
        if (tile instanceof TileEntityAdvSign) {
            TileEntityAdvSign tileAS = (TileEntityAdvSign)tile;
            getSign(stack).ifPresent(signTag->tileAS.readFromNBT(signTag, true));
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
    
    protected static Optional<NBTTagList> getLines(ItemStack stack) {
        return getSign(stack).filter(tag->tag.hasKey(TileEntityAdvSign.TAG_LINES, 9))
                             .map(tag->tag.getTagList(TileEntityAdvSign.TAG_LINES, 8));
    }
}
