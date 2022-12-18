package com.vanym.paniclecraft.item;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.block.BlockPaintingContainer;
import com.vanym.paniclecraft.core.component.painting.IPaintingTool;
import com.vanym.paniclecraft.core.component.painting.PaintingSide;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.network.message.MessagePaintBrushUse;
import com.vanym.paniclecraft.utils.MainUtils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ItemPaintBrush extends ItemMod3 implements IPaintingTool {
    public static final int DEFAULT_COLOR_RGB = 200;
    public static final int DEFAULT_COLOR =
            MainUtils.getIntFromRGB(DEFAULT_COLOR_RGB, DEFAULT_COLOR_RGB, DEFAULT_COLOR_RGB);
    
    public static final String TAG_RADIUS = "Radius";
    
    protected static final double MAX_RADIUS = 256.0D;
    protected static final DecimalFormat NUMBER_FORMATTER = new DecimalFormat("#.##");
    
    @SideOnly(Side.CLIENT)
    public IIcon big;
    @SideOnly(Side.CLIENT)
    public IIcon big_overlay;
    @SideOnly(Side.CLIENT)
    public IIcon small;
    @SideOnly(Side.CLIENT)
    public IIcon small_overlay;
    @SideOnly(Side.CLIENT)
    public IIcon fill;
    @SideOnly(Side.CLIENT)
    public IIcon fill_overlay;
    
    @SideOnly(Side.CLIENT)
    protected Set<MessagePaintBrushUse> brushUseMessages = new HashSet<>();
    
    public static double brushRadiusRound = 3.5D;
    
    public ItemPaintBrush() {
        super();
        this.setUnlocalizedName("paintBrush");
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
    
    @Override
    public boolean isItemTool(ItemStack par1ItemStack) {
        return true;
    }
    
    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        if (!FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        MessagePaintBrushUse mes = makeBrushUseMessage(mc.theWorld, mc.objectMouseOver);
        if (mes != null) {
            this.brushUseMessages.add(mes);
        }
        this.flashBrushUseMessages();
    }
    
    @Override
    public void onPlayerStoppedUsing(
            ItemStack itemStack,
            World world,
            EntityPlayer player,
            int count) {
        if (!FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            return;
        }
        this.flashBrushUseMessages();
    }
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void renderWorldLast(net.minecraftforge.client.event.RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        ItemStack itemStack = mc.thePlayer.getItemInUse();
        if (itemStack != null && itemStack.getItem() instanceof ItemPaintBrush) {
            MessagePaintBrushUse mes = makeBrushUseMessage(mc.theWorld, mc.objectMouseOver);
            if (mes != null) {
                this.brushUseMessages.add(mes);
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void flashBrushUseMessages() {
        for (MessagePaintBrushUse mes : this.brushUseMessages) {
            Core.instance.network.sendToServer(mes);
        }
        this.brushUseMessages.clear();
    }
    
    @SideOnly(Side.CLIENT)
    public static MessagePaintBrushUse makeBrushUseMessage(
            IBlockAccess world,
            MovingObjectPosition target) {
        Picture picture = BlockPaintingContainer.getPicture(world, target);
        if (picture == null) {
            return null;
        }
        int x = target.blockX;
        int y = target.blockY;
        int z = target.blockZ;
        PaintingSide pside = PaintingSide.getSize(target.sideHit);
        Vec3 inBlock = MainUtils.getInBlockVec(target);
        Vec3 inPainting = pside.toPaintingCoords(inBlock);
        int px = (int)(inPainting.xCoord * picture.getWidth());
        int py = (int)(inPainting.yCoord * picture.getHeight());
        MessagePaintBrushUse message =
                new MessagePaintBrushUse(x, y, z, px, py, (byte)pside.ordinal());
        return message;
    }
    
    @Override
    public int getMaxItemUseDuration(ItemStack itemstack) {
        return 72000;
    }
    
    @Override
    public boolean onItemUse(
            ItemStack itemStack,
            EntityPlayer entityPlayer,
            World world,
            int x,
            int y,
            int z,
            int side,
            float ibx,
            float iby,
            float ibz) {
        Picture picture = BlockPaintingContainer.getPicture(world, x, y, z, side);
        if (picture != null) {
            entityPlayer.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
            if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
                this.brushUseMessages.clear();
            }
        }
        return false;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
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
            NBTTagCompound itemTag = itemStack.getTagCompound();
            if (itemTag.hasKey(TAG_RADIUS)) {
                double radius = this.getPaintingToolRadius(itemStack, null);
                StringBuilder sb = new StringBuilder();
                sb.append(StatCollector.translateToLocal(this.getUnlocalizedName() + ".radius"));
                sb.append(": ");
                sb.append(NUMBER_FORMATTER.format(radius));
                list.add(sb.toString());
            }
        }
        if (GuiScreen.isShiftKeyDown()) {
            Color rgbColor = MainUtils.getColorFromInt(this.getColor(itemStack));
            list.add("R: \u00a7c" + rgbColor.getRed());
            list.add("G: \u00a7a" + rgbColor.getGreen());
            list.add("B: \u00a79" + rgbColor.getBlue());
        }
    }
    
    @Override
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
        if (par2 > 0) {
            return 16777215;
        } else {
            int var3 = this.getColor(par1ItemStack);
            
            if (var3 < 0) {
                var3 = 16777215;
            }
            
            return var3;
        }
    }
    
    public int getColor(ItemStack par1ItemStack) {
        NBTTagCompound var2 = par1ItemStack.getTagCompound();
        if (var2 == null) {
            return ItemPaintBrush.DEFAULT_COLOR;
        } else {
            return var2 == null ? ItemPaintBrush.DEFAULT_COLOR
                                : (var2.hasKey("color") ? var2.getInteger("color")
                                                        : ItemPaintBrush.DEFAULT_COLOR);
        }
    }
    
    public void removeColor(ItemStack par1ItemStack) {
        NBTTagCompound var2 = par1ItemStack.getTagCompound();
        if (var2 != null) {
            if (var2.hasKey("color")) {
                var2.removeTag("color");
            }
        }
    }
    
    public boolean hasColor(ItemStack par1ItemStack) {
        return par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("color");
    }
    
    public void setColor(ItemStack par1ItemStack, int par2) {
        NBTTagCompound var3 = par1ItemStack.getTagCompound();
        
        if (var3 == null) {
            var3 = new NBTTagCompound();
            par1ItemStack.setTagCompound(var3);
        }
        
        var3.setInteger("color", par2);
        
    }
    
    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return this.getUnlocalizedName() + itemStack.getItemDamage();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int par1, int par2) {
        switch (par1) {
            default:
            case 0:
                return (par2 != 1 ? this.big : this.big_overlay);
            case 1:
                return (par2 != 1 ? this.small : this.small_overlay);
            case 2:
                return (par2 != 1 ? this.fill : this.fill_overlay);
        }
    }
    
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs creativetabs, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
        list.add(new ItemStack(item, 1, 2));
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        // @formatter:off
        this.big = iconRegister.registerIcon(DEF.MOD_ID + ":" + this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1) + "_big");
        this.big_overlay = iconRegister.registerIcon(DEF.MOD_ID + ":" + this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1) + "_big_overlay");
        this.small = iconRegister.registerIcon(DEF.MOD_ID + ":" + this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1) + "_small");
        this.small_overlay = iconRegister.registerIcon(DEF.MOD_ID + ":" + this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1) + "_small_overlay");
        this.fill = iconRegister.registerIcon(DEF.MOD_ID + ":" + this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1) + "_fill");
        this.fill_overlay = iconRegister.registerIcon(DEF.MOD_ID + ":" + this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1) + "_fill_overlay");
        // @formatter:on
    }
    
    @Override
    public boolean isFull3D() {
        return true;
    }
    
    @Override
    public PaintingToolType getPaintingToolType(ItemStack itemStack) {
        switch (itemStack.getItemDamage()) {
            case 0:
            case 1:
                return PaintingToolType.BRUSH;
            case 2:
                return PaintingToolType.FILLER;
        }
        return PaintingToolType.NONE;
    }
    
    @Override
    public Color getPaintingToolColor(ItemStack itemStack) {
        return new Color(this.getColor(itemStack));
    }
    
    @Override
    public double getPaintingToolRadius(ItemStack itemStack, Picture picture) {
        if (itemStack.hasTagCompound()) {
            NBTTagCompound itemTag = itemStack.getTagCompound();
            if (itemTag.hasKey(TAG_RADIUS)) {
                double radius = itemTag.getDouble(TAG_RADIUS);
                return Math.min(MAX_RADIUS, radius);
            }
        }
        if (itemStack.getItemDamage() == 0) {
            return brushRadiusRound;
        }
        return 0.1D;
    }
}
