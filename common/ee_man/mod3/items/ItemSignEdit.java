package ee_man.mod3.items;

import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;

import ee_man.mod3.Core;
import ee_man.mod3.tileEntity.TileEntityAdvSign;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.world.World;

public class ItemSignEdit extends ItemMod3{
	
	public ItemSignEdit(int par1, int par2){
		super(par1);
		this.setMaxStackSize(1);
		this.setMaxDamage(par2);
	}
	
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10){
		if(!par2EntityPlayer.isSneaking()){
			TileEntity tileEntity = par3World.getBlockTileEntity(par4, par5, par6);
			if(tileEntity instanceof TileEntitySign && par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack)){
				TileEntitySign signEntity = (TileEntitySign)tileEntity;
				this.setEditableSign(signEntity);
				if(signEntity.isEditable()){
					par2EntityPlayer.displayGUIEditSign(signEntity);
					par1ItemStack.damageItem(1, par2EntityPlayer);
					return true;
				}
				else{
					par2EntityPlayer.addChatMessage("Can't edit this sign (maybe bug)");
					return false;
				}
			}
			else
				if(tileEntity instanceof TileEntityAdvSign && ((TileEntityAdvSign)tileEntity).canBeEdit() && par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack)){
					TileEntityAdvSign signEntity = (TileEntityAdvSign)tileEntity;
					signEntity.setEditable(true);
					par2EntityPlayer.openGui(Core.instance, 0, par3World, par4, par5, par6);
					par1ItemStack.damageItem(1, par2EntityPlayer);
					return true;
				}
				else
					return false;
		}
		else{
			int id = par3World.getBlockId(par4, par5, par6);
			if((id == Block.signPost.blockID || id == Core.blockAdvSignPost.blockID) && par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack)){
				int md = par3World.getBlockMetadata(par4, par5, par6);
				md++;
				if(md == 16)
					md = 0;
				if(par3World.setBlockMetadataWithNotify(par4, par5, par6, md, 2)){
					par1ItemStack.damageItem(1, par2EntityPlayer);
					return true;
				}
				else
					return false;
			}
			else
				if((id == Block.signWall.blockID || id == Core.blockAdvSignWall.blockID) && par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack)){
					int md = par3World.getBlockMetadata(par4, par5, par6);
					boolean needDem = true;
					switch(md){
						case 2:
							if(par3World.getBlockMaterial(par4 - 1, par5, par6).isSolid())
								par3World.setBlockMetadataWithNotify(par4, par5, par6, 5, 2);
							else
								if(par3World.getBlockMaterial(par4, par5, par6 - 1).isSolid())
									par3World.setBlockMetadataWithNotify(par4, par5, par6, 3, 2);
								else
									if(par3World.getBlockMaterial(par4 + 1, par5, par6).isSolid())
										par3World.setBlockMetadataWithNotify(par4, par5, par6, 4, 2);
									else
										needDem = false;
						break;
						case 5:
							if(par3World.getBlockMaterial(par4, par5, par6 - 1).isSolid())
								par3World.setBlockMetadataWithNotify(par4, par5, par6, 3, 2);
							else
								if(par3World.getBlockMaterial(par4 + 1, par5, par6).isSolid())
									par3World.setBlockMetadataWithNotify(par4, par5, par6, 4, 2);
								else
									if(par3World.getBlockMaterial(par4, par5, par6 + 1).isSolid())
										par3World.setBlockMetadataWithNotify(par4, par5, par6, 2, 2);
									else
										needDem = false;
						break;
						case 3:
							if(par3World.getBlockMaterial(par4 + 1, par5, par6).isSolid())
								par3World.setBlockMetadataWithNotify(par4, par5, par6, 4, 2);
							else
								if(par3World.getBlockMaterial(par4, par5, par6 + 1).isSolid())
									par3World.setBlockMetadataWithNotify(par4, par5, par6, 2, 2);
								else
									if(par3World.getBlockMaterial(par4 - 1, par5, par6).isSolid())
										par3World.setBlockMetadataWithNotify(par4, par5, par6, 5, 2);
									else
										needDem = false;
						break;
						case 4:
							if(par3World.getBlockMaterial(par4, par5, par6 + 1).isSolid())
								par3World.setBlockMetadataWithNotify(par4, par5, par6, 2, 2);
							else
								if(par3World.getBlockMaterial(par4 - 1, par5, par6).isSolid())
									par3World.setBlockMetadataWithNotify(par4, par5, par6, 5, 2);
								else
									if(par3World.getBlockMaterial(par4, par5, par6 - 1).isSolid())
										par3World.setBlockMetadataWithNotify(par4, par5, par6, 3, 2);
									else
										needDem = false;
						break;
					}
					if(needDem)
						par1ItemStack.damageItem(1, par2EntityPlayer);
					return true;
				}
				else
					return false;
		}
	}
	
	public float getStrVsBlock(ItemStack par1ItemStack, Block par2Block){
		return par2Block != null && (par2Block == Block.signPost || par2Block == Block.signWall || par2Block == Core.blockAdvSignPost || par2Block == Core.blockAdvSignWall) ? 4.0F : 1.0F;
	}
	
	public boolean onBlockDestroyed(ItemStack par1ItemStack, World par2World, int par3, int par4, int par5, int par6, EntityLiving par7EntityLiving){
		int block_id = par2World.getBlockId(par4, par5, par6);
		if(block_id == Block.signPost.blockID || block_id == Block.signWall.blockID || block_id == Core.blockAdvSignWall.blockID || block_id == Core.blockAdvSignPost.blockID){
			par1ItemStack.damageItem(1, par7EntityLiving);
			return true;
		}
		else
			return false;
	}
	
	private void setEditableSign(TileEntitySign par1){
		try{
			ReflectionHelper.setPrivateValue(TileEntitySign.class, par1, true, 2);
		} catch(UnableToAccessFieldException e){
			
		}
	}
	
	public boolean isItemTool(ItemStack par1ItemStack){
		return true;
	}
}
