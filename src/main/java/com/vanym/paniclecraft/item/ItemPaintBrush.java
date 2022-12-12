package com.vanym.paniclecraft.item;

import java.awt.Color;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.network.PacketHandler;
import com.vanym.paniclecraft.network.message.MessagePaintBrushUse;
import com.vanym.paniclecraft.utils.ISidePaintingProvider;
import com.vanym.paniclecraft.utils.MainUtils;

public class ItemPaintBrush extends ItemMod3{
	public static final int DEFAULT_COLOR_RGB = 200;
	public static final int DEFAULT_COLOR = MainUtils.getIntFromRGB(DEFAULT_COLOR_RGB, DEFAULT_COLOR_RGB, DEFAULT_COLOR_RGB);
	
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
	public static int[][] noDrawPixels;
	public static int brushRadiusSquare = 3;
	public static double brushRadiusRound = 3.5D;
	
	public ItemPaintBrush(){
		super();
		this.setUnlocalizedName("paintBrush");
		this.setMaxStackSize(1);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}
	
	public boolean isItemTool(ItemStack par1ItemStack){
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count){
		if(FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT)
			return;
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.objectMouseOver != null)
			if(mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK){
				int x = mc.objectMouseOver.blockX;
				int y = mc.objectMouseOver.blockY;
				int z = mc.objectMouseOver.blockZ;
				int s = mc.objectMouseOver.sideHit;
				Vec3 vec = mc.objectMouseOver.hitVec;
				TileEntity tile = mc.theWorld.getTileEntity(x, y, z);
				if(tile != null && tile instanceof ISidePaintingProvider && ((ISidePaintingProvider)tile).getPainting(s) != null){
					ISidePaintingProvider tileP = (ISidePaintingProvider)tile;
					float f = (float)vec.xCoord - (float)x;
					float f1 = (float)vec.yCoord - (float)y;
					float f2 = (float)vec.zCoord - (float)z;
					int px = getXuse(tileP.getPainting(s).getRow(), s, f, f1, f2);
					int py = getYuse(tileP.getPainting(s).getRow(), s, f, f1, f2);
					PacketHandler.INSTANCE.sendToServer(new MessagePaintBrushUse(x, y, z, px, py, (byte)s));
					return;
				}
			}
	}
	
	public int getMaxItemUseDuration(ItemStack itemstack){
		return 72000;
	}
	
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10){
		TileEntity tile = par3World.getTileEntity(par4, par5, par6);
		if(tile != null && tile instanceof ISidePaintingProvider && ((ISidePaintingProvider)tile).getPainting(par7) != null){
			par2EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
			// TileEntityPainting tileP = (TileEntityPainting)tile;
			// int px = getXuse(tileP.getRow(), par7, par8, par9, par10);
			// int py = getYuse(tileP.getRow(), par7, par8, par9, par10);
			// if(!par3World.isRemote){
			// this.usePaintBrush(par1ItemStack, tileP, px, py);
			// }
		}
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses(){
		return true;
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4){
		if(GuiScreen.isShiftKeyDown()){
			Color rgbColor = MainUtils.getColorFromInt(this.getColor(par1ItemStack));
			par3List.add("R: \u00a7c" + rgbColor.getRed());
			par3List.add("G: \u00a7a" + rgbColor.getGreen());
			par3List.add("B: \u00a79" + rgbColor.getBlue());
		}
	}
	
	public int getColorFromItemStack(ItemStack par1ItemStack, int par2){
		if(par2 > 0){
			return 16777215;
		}
		else{
			int var3 = this.getColor(par1ItemStack);
			
			if(var3 < 0){
				var3 = 16777215;
			}
			
			return var3;
		}
	}
	
	public int getColor(ItemStack par1ItemStack){
		NBTTagCompound var2 = par1ItemStack.getTagCompound();
		if(var2 == null){
			return ItemPaintBrush.DEFAULT_COLOR;
		}
		else{
			return var2 == null ? ItemPaintBrush.DEFAULT_COLOR : (var2.hasKey("color") ? var2.getInteger("color") : ItemPaintBrush.DEFAULT_COLOR);
		}
	}
	
	public void removeColor(ItemStack par1ItemStack){
		NBTTagCompound var2 = par1ItemStack.getTagCompound();
		if(var2 != null){
			if(var2.hasKey("color")){
				var2.removeTag("color");
			}
		}
	}
	
	public boolean hasColor(ItemStack par1ItemStack){
		return par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("color");
	}
	
	public void setColor(ItemStack par1ItemStack, int par2){
		NBTTagCompound var3 = par1ItemStack.getTagCompound();
		
		if(var3 == null){
			var3 = new NBTTagCompound();
			par1ItemStack.setTagCompound(var3);
		}
		
		var3.setInteger("color", par2);
		
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemStack){
		return getUnlocalizedName() + itemStack.getItemDamage();
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int par1, int par2){
		switch(par1){
			default:
			case 0:
				return(par2 != 1 ? this.big : this.big_overlay);
			case 1:
				return(par2 != 1 ? this.small : this.small_overlay);
			case 2:
				return(par2 != 1 ? this.fill : this.fill_overlay);
		}
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creativetabs, List list){
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
		list.add(new ItemStack(item, 1, 2));
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister){
		this.big = iconRegister.registerIcon(DEF.MOD_ID + ":" + this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1) + "_big");
		this.big_overlay = iconRegister.registerIcon(DEF.MOD_ID + ":" + this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1) + "_big_overlay");
		this.small = iconRegister.registerIcon(DEF.MOD_ID + ":" + this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1) + "_small");
		this.small_overlay = iconRegister.registerIcon(DEF.MOD_ID + ":" + this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1) + "_small_overlay");
		this.fill = iconRegister.registerIcon(DEF.MOD_ID + ":" + this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1) + "_fill");
		this.fill_overlay = iconRegister.registerIcon(DEF.MOD_ID + ":" + this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1) + "_fill_overlay");
	}
	
	public boolean isFull3D(){
		return true;
	}
	
	public static void setNoDrawPixels(String par1){
		if(!par1.isEmpty()){
			String[] var1 = par1.split(",");
			int[][] var3 = new int[2][var1.length];
			for(int i = 0; i < var1.length; i++){
				try{
					String[] var2 = var1[i].split(";");
					var3[0][i] = Integer.parseInt(var2[0]);
					var3[1][i] = Integer.parseInt(var2[1]);
				} catch(Exception ex){
					ex.printStackTrace();
				}
			}
			noDrawPixels = var3;
		}
		else
			noDrawPixels = new int[2][0];
	}
	
	public static int getXuse(int row, int par1, float par2, float par3, float par4){
		int var1 = (int)(par2 * row);
		// int var2 = (int)(par3 * row);
		int var3 = (int)(par4 * row);
		
		switch(par1){
			case 0:
				return (row - 1) - var1;
			case 1:
				return (row - 1) - var1;
			case 2:
				return (row - 1) - var1;
			case 3:
				return var1;
			case 4:
				return var3;
			case 5:
				return (row - 1) - var3;
			default:
				return -1;
		}
	}
	
	public static int getYuse(int row, int par1, float par2, float par3, float par4){
		// int var1 = (int)(par2 * row);
		int var2 = (int)(par3 * row);
		int var3 = (int)(par4 * row);
		switch(par1){
			case 0:
				return var3;
			case 1:
				return (row - 1) - var3;
			case 2:
				return (row - 1) - var2;
			case 3:
				return (row - 1) - var2;
			case 4:
				return (row - 1) - var2;
			case 5:
				return (row - 1) - var2;
			default:
				return -1;
		}
	}
}