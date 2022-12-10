package ee_man.mod3.client.renderer.item;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import ee_man.mod3.Core;
import ee_man.mod3.tileEntity.TileEntityAdvSign;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.ForgeVersion;

@SideOnly(Side.CLIENT)
public class ItemRendererAdvSign implements IItemRenderer{
	
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
		TileEntityAdvSign tileAdvSign = new TileEntityAdvSign();
		tileAdvSign.blockType = Core.blockAdvSignPost;
		tileAdvSign.blockMetadata = 12;
		tileAdvSign.signText = null;
		if(item.hasTagCompound()){
			NBTTagCompound tag = item.getTagCompound();
			if(tag.hasKey("SignText"))
				tileAdvSign.signText = tag.getString("SignText");
			if(tag.hasKey("red"))
				tileAdvSign.red = tag.getByte("red");
			if(tag.hasKey("green"))
				tileAdvSign.green = tag.getByte("green");
			if(tag.hasKey("blue"))
				tileAdvSign.blue = tag.getByte("blue");
		}
		if(type.equals(ItemRenderType.ENTITY)){
			GL11.glTranslatef(-0.25F, -0.2F, -0.25F);
			float var11 = 0.55F;
			GL11.glScalef(var11, var11, var11);
		}
		else
			if(type.equals(ItemRenderType.EQUIPPED) || (ForgeVersion.getBuildVersion() >= 687 ? type.equals(ItemRenderType.EQUIPPED_FIRST_PERSON) : false)){
				tileAdvSign.blockMetadata = 6;
			}
			else
				if(type.equals(ItemRenderType.INVENTORY)){
					tileAdvSign.blockMetadata = 14;
					float var12 = 1.3F;
					GL11.glScalef(var12, var12, var12);
					GL11.glTranslatef(0.0F, -0.2F, 0.0F);
				}
				else
					if(type.equals(ItemRenderType.FIRST_PERSON_MAP)){
						
					}
		TileEntityRenderer.instance.renderTileEntityAt(tileAdvSign, 0, 0, 0, 0);
	}
	
}
