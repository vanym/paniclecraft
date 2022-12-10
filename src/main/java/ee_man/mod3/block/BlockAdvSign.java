package ee_man.mod3.block;

import java.util.Random;

import net.minecraft.block.BlockSign;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ee_man.mod3.init.ModItems;
import ee_man.mod3.tileentity.TileEntityAdvSign;

public class BlockAdvSign extends BlockSign{
	
	public BlockAdvSign(boolean isPost){
		super(TileEntityAdvSign.class, isPost);
		this.setHardness(1.0F);
		this.setBlockName("advSign");
	}
	
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_){
		return ModItems.itemAdvSign;
	}
	
	@SideOnly(Side.CLIENT)
	public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_){
		return ModItems.itemAdvSign;
	}
	
	@SideOnly(Side.CLIENT)
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z){
		TileEntityAdvSign tile = (TileEntityAdvSign)world.getTileEntity(x, y, z);
		ItemStack itemS = new ItemStack(ModItems.itemAdvSign);
		if(tile == null)
			return itemS;
		NBTTagCompound var1 = new NBTTagCompound();
		itemS.setTagCompound(var1);
		var1.setString("SignText", tile.signText);
		return itemS;
	}
}
