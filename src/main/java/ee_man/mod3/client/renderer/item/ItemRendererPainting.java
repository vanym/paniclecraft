package ee_man.mod3.client.renderer.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ee_man.mod3.init.ModItems;
import ee_man.mod3.proxy.ClientProxy;
import ee_man.mod3.tileentity.TileEntityPainting;

@SideOnly(Side.CLIENT)
public class ItemRendererPainting implements IItemRenderer{
	
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
		TileEntityPainting tilePainting = new TileEntityPainting();
		tilePainting.blockType = ModItems.blockPainting;
		tilePainting.blockMetadata = 3;
		tilePainting.getPainting(3).setRow(1);
		if(item.hasTagCompound()){
			NBTTagCompound tag = item.getTagCompound();
			if(tag.hasKey("PaintingData")){
				NBTTagCompound tagData = tag.getCompoundTag("PaintingData");
				if(!tagData.hasNoTags())
					tilePainting.getPainting(3).readFromNBT(tagData);
			}
		}
		if(type.equals(ItemRenderType.ENTITY)){
			GL11.glTranslatef(-0.25F, -0.18F, 0.0F);
			float var11 = 0.55F;
			GL11.glScalef(var11, var11, var11);
		}
		else
			if(type.equals(ItemRenderType.EQUIPPED) || type.equals(ItemRenderType.EQUIPPED_FIRST_PERSON)){
				tilePainting.blockMetadata = 4;
				GL11.glTranslatef(-0.6F, 0.6F, 0.0F);
			}
			else
				if(type.equals(ItemRenderType.INVENTORY)){
					float var12 = 1.2F;
					GL11.glScalef(var12, var12, var12);
					GL11.glTranslatef(0.0F, -0.18F, 0.42F);
				}
				else
					if(type.equals(ItemRenderType.FIRST_PERSON_MAP)){
						
					}
		ClientProxy.tilePaintingRenderer.renderTileEntityAt(tilePainting, 0, 0, 0, 0);
		if(tilePainting.getPainting(tilePainting.getBlockMetadata()).texID >= 0){
			ClientProxy.deleteTexture(tilePainting.getPainting(tilePainting.getBlockMetadata()).texID);
			tilePainting.getPainting(tilePainting.getBlockMetadata()).texID = -1;
		}
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	}
	
}
