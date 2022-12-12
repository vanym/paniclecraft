package com.vanym.paniclecraft.client.renderer.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.ForgeVersion;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.vanym.paniclecraft.client.ClientProxy;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

@SideOnly(Side.CLIENT)
public class ItemRendererChessDesk implements IItemRenderer{
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type){
		return true;
	}
	
	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper){
		return true;
	}
	
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data){
		TileEntityChessDesk tileChessDesk = new TileEntityChessDesk();
		tileChessDesk.blockMetadata = 0;
		if(type.equals(ItemRenderType.ENTITY)){
			GL11.glTranslatef(-0.25F, -0.2F, -0.25F);
			float var11 = 0.55F;
			GL11.glScalef(var11, var11, var11);
		}
		else
			if(type.equals(ItemRenderType.EQUIPPED) || (ForgeVersion.getBuildVersion() >= 687 ? type.equals(ItemRenderType.EQUIPPED_FIRST_PERSON) : false)){
				GL11.glTranslatef(0.0F, 0.7F, -0.2F);
			}
			else
				if(type.equals(ItemRenderType.INVENTORY)){
					tileChessDesk.blockMetadata = 2;
					float var12 = 1.07F;
					GL11.glScalef(var12, var12, var12);
				}
				else
					if(type.equals(ItemRenderType.FIRST_PERSON_MAP)){
						
					}
		if(item.hasTagCompound()){
			NBTTagCompound tag = item.getTagCompound();
			if(tag.hasKey("ChessData"))
				tileChessDesk.readFromNBT(tag.getCompoundTag("ChessData"));
		}
		ClientProxy.tileChessDeskRenderer.renderTileEntityAt(tileChessDesk, 0, 0, 0, 0);
	}
}
