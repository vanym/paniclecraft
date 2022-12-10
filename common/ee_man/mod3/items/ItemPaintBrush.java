package ee_man.mod3.items;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import ee_man.mod3.DefaultProperties;
import ee_man.mod3.tileEntity.TileEntityPainting;
import ee_man.mod3.utils.MainUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.Icon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemPaintBrush extends ItemMod3{
	public static final int DEFAULT_COLOR_RGB = 200;
	public static final int DEFAULT_COLOR = MainUtils.getIntFromRGB(DEFAULT_COLOR_RGB, DEFAULT_COLOR_RGB, DEFAULT_COLOR_RGB);
	
	@SideOnly(Side.CLIENT)
	private Icon big;
	@SideOnly(Side.CLIENT)
	private Icon big_overlay;
	@SideOnly(Side.CLIENT)
	private Icon small;
	@SideOnly(Side.CLIENT)
	private Icon small_overlay;
	public static int[][] noDrawPixels;
	public static int brushRadius = 3;
	public static int paintRow = 16;
	
	public ItemPaintBrush(int par1){
		super(par1);
		this.setMaxStackSize(1);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}
	
	public boolean isItemTool(ItemStack par1ItemStack){
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public void onUsingItemTick(ItemStack stack, EntityPlayer player, int count){
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.objectMouseOver != null)
			if(mc.objectMouseOver.typeOfHit == EnumMovingObjectType.TILE){
				int x = mc.objectMouseOver.blockX;
				int y = mc.objectMouseOver.blockY;
				int z = mc.objectMouseOver.blockZ;
				int s = mc.objectMouseOver.sideHit;
				Vec3 vec = mc.objectMouseOver.hitVec;
				TileEntity tile = mc.theWorld.getBlockTileEntity(x, y, z);
				if(tile != null && tile instanceof TileEntityPainting && tile.getBlockMetadata() == s){
					TileEntityPainting tileP = (TileEntityPainting)tile;
					float f = (float)vec.xCoord - (float)x;
					float f1 = (float)vec.yCoord - (float)y;
					float f2 = (float)vec.zCoord - (float)z;
					int px = getXuse(tileP.Row, s, f, f1, f2);
					int py = getYuse(tileP.Row, s, f, f1, f2);
					
					NetClientHandler nh = mc.getNetHandler();
					if(nh != null){
						ByteArrayOutputStream bytes = new ByteArrayOutputStream();
						DataOutputStream data = new DataOutputStream(bytes);
						try{
							data.writeByte(2);
							data.writeInt(x);
							data.writeInt(y);
							data.writeInt(z);
							data.writeByte(px);
							data.writeByte(py);
						} catch(IOException e){
							e.printStackTrace();
						}
						Packet250CustomPayload packet = new Packet250CustomPayload();
						packet.channel = DefaultProperties.MOD_ID;
						packet.data = bytes.toByteArray();
						packet.length = packet.data.length;
						packet.isChunkDataPacket = false;
						nh.addToSendQueue(packet);
					}
					return;
				}
			}
		player.stopUsingItem();
	}
	
	@SideOnly(Side.CLIENT)
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10){
		if(FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT)
			return false;
		TileEntity tile = par3World.getBlockTileEntity(par4, par5, par6);
		if(tile != null && tile instanceof TileEntityPainting && tile.getBlockMetadata() == par7)
			par2EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
		return false;
	}
	
	public void usePaintBrush(ItemStack par1ItemStack, TileEntityPainting tileP, int x, int y){
		int var3 = getColorFromItemStack(par1ItemStack, 0);
		int[] color = MainUtils.getRGBFromInt(var3);
		
		ArrayList<int[]> updateList = new ArrayList<int[]>();
		
		if(par1ItemStack.getItemDamage() == 0){
			setPixelsColor(tileP, color, x, y, updateList);
		}
		else
			setPixelColor(tileP, color, x, y, updateList);
		Iterator<int[]> updateIterator = updateList.iterator();
		while(updateIterator.hasNext()){
			int[] coords = updateIterator.next();
			tileP.worldObj.markBlockForUpdate(coords[0], coords[1], coords[2]);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses(){
		return true;
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4){
		if(GuiScreen.isShiftKeyDown()){
			int[] rgbColor = MainUtils.getRGBFromInt(this.getColor(par1ItemStack));
			par3List.add("R: \u00a7c" + rgbColor[0]);
			par3List.add("G: \u00a7a" + rgbColor[1]);
			par3List.add("B: \u00a79" + rgbColor[2]);
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
		return(!par1ItemStack.hasTagCompound() ? false : par1ItemStack.getTagCompound().hasKey("color"));
	}
	
	public void setColor(ItemStack par1ItemStack, int par2){
		NBTTagCompound var3 = par1ItemStack.getTagCompound();
		
		if(var3 == null){
			var3 = new NBTTagCompound();
			par1ItemStack.setTagCompound(var3);
		}
		
		var3.setInteger("color", par2);
		
	}
	
	@SideOnly(Side.CLIENT)
	public Icon getIconFromDamageForRenderPass(int par1, int par2){
		return(par1 == 1 ? (par2 != 1 ? this.small : this.small_overlay) : (par2 != 1 ? this.big : this.big_overlay));
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister){
		this.big = iconRegister.registerIcon(DefaultProperties.TEXTURE_ID + ":" + this.getUnlocalizedName() + "_big");
		this.big_overlay = iconRegister.registerIcon(DefaultProperties.TEXTURE_ID + ":" + this.getUnlocalizedName() + "_big_overlay");
		this.small = iconRegister.registerIcon(DefaultProperties.TEXTURE_ID + ":" + this.getUnlocalizedName() + "_small");
		this.small_overlay = iconRegister.registerIcon(DefaultProperties.TEXTURE_ID + ":" + this.getUnlocalizedName() + "_small_overlay");
	}
	
	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, @SuppressWarnings("rawtypes") List par3List){
		for(int i = 0; i < 2; ++i){
			par3List.add(new ItemStack(par1, 1, i));
		}
	}
	
	public boolean isFull3D(){
		return true;
	}
	
	public static void setNoDrawPixels(String par1){
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
	
	public static int getXuse(int Row, int par1, float par2, float par3, float par4){
		int var1 = (int)(par2 * Row);
		// int var2 = (int)(par3 * Row);
		int var3 = (int)(par4 * Row);
		
		switch(par1){
			case 2:
				return (Row - 1) - var1;
			case 3:
				return var1;
			case 4:
				return var3;
			case 5:
				return (Row - 1) - var3;
			default:
				return -1;
		}
	}
	
	public static int getYuse(int Row, int par1, float par2, float par3, float par4){// int
																						// var3
																						// =
																						// new
																						// BigDecimal(par4
																						// *
																						// Row).toBigInteger().intValue();
		// int var1 = (int)(par2 * Row);
		int var2 = (int)(par3 * Row);
		// int var3 = (int)(par4 * Row);
		switch(par1){
			case 2:
				return (Row - 1) - var2;
			case 3:
				return (Row - 1) - var2;
			case 4:
				return (Row - 1) - var2;
			case 5:
				return (Row - 1) - var2;
			default:
				return -1;
		}
	}
	
	public static void setPixelsColor(TileEntityPainting par1, int[] color, int x, int y, ArrayList<int[]> updateList){
		for(int i = -par1.BrushRadius; i <= par1.BrushRadius; i++){
			for(int j = -par1.BrushRadius; j <= par1.BrushRadius; j++){
				boolean var3 = true;
				for(int k = 0; k < par1.NoDrawPixels[0].length; k++){
					if(Math.abs(i) == par1.NoDrawPixels[0][k] && Math.abs(j) == par1.NoDrawPixels[1][k])
						var3 = false;
				}
				if(var3)
					setPixelColor_a(par1, color, x + i, y + j, updateList);
			}
		}
	}
	
	public static void setPixelColor_a(TileEntityPainting par1, int[] color, int x, int y, ArrayList<int[]> updateList){
		int vx = x;
		int vy = y;
		int md = par1.getBlockMetadata();
		int xCoord = par1.xCoord;
		int yCoord = par1.yCoord;
		int zCoord = par1.zCoord;
		while(vx < 0){
			vx = par1.Row + vx;
			switch(md){
				case 3:
					xCoord--;
				break;
				case 2:
					xCoord++;
				break;
				case 4:
					zCoord--;
				break;
				case 5:
					zCoord++;
				break;
			}
		}
		while(vx > par1.Row - 1){
			vx = vx - par1.Row;
			switch(md){
				case 3:
					xCoord++;
				break;
				case 2:
					xCoord--;
				break;
				case 4:
					zCoord++;
				break;
				case 5:
					zCoord--;
				break;
			}
		}
		while(vy < 0){
			vy = par1.Row + vy;
			yCoord++;
		}
		while(vy > par1.Row - 1){
			vy = vy - par1.Row;
			yCoord--;
		}
		TileEntity var1 = par1.worldObj.getBlockTileEntity(xCoord, yCoord, zCoord);
		if(var1 instanceof TileEntityPainting && ((TileEntityPainting)var1).Row == par1.Row && var1.getBlockMetadata() == par1.getBlockMetadata()){
			setPixelColor((TileEntityPainting)var1, color, vx, vy, updateList);
		}
		
	}
	
	public static void setPixelColor(TileEntityPainting par1, int[] color, int x, int y, ArrayList<int[]> updateList){
		int var1 = (par1.Row * y + x) * 3;
		boolean need_to_add = false;
		if(par1.pic[var1 + 0] != (byte)(color[0])){
			par1.pic[var1 + 0] = (byte)(color[0]);
			need_to_add = true;
		}
		if(par1.pic[var1 + 1] != (byte)(color[1])){
			par1.pic[var1 + 1] = (byte)(color[1]);
			need_to_add = true;
		}
		if(par1.pic[var1 + 2] != (byte)(color[2])){
			par1.pic[var1 + 2] = (byte)(color[2]);
			need_to_add = true;
		}
		Iterator<int[]> updateIterator = updateList.iterator();
		while(updateIterator.hasNext() && need_to_add){
			int[] coords = updateIterator.next();
			if(coords[0] == par1.xCoord && coords[1] == par1.yCoord && coords[2] == par1.zCoord)
				need_to_add = false;
		}
		if(need_to_add)
			updateList.add(new int[]{par1.xCoord, par1.yCoord, par1.zCoord});
	}
}
