package com.vanym.paniclecraft.client.renderer.item;

import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.client.renderer.PictureTextureCache;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.IItemRenderer;

@SideOnly(Side.CLIENT)
public class ItemRendererPainting implements IItemRenderer {
    
    protected PictureTextureCache textureCache;
    
    public ItemRendererPainting(PictureTextureCache textureCache) {
        this.textureCache = textureCache;
    }
    
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }
    
    @Override
    public boolean shouldUseRenderHelper(
            ItemRenderType type,
            ItemStack item,
            ItemRendererHelper helper) {
        return true;
    }
    
    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        TileEntityPainting tilePainting = new TileEntityPainting();
        tilePainting.blockType = Core.instance.painting.blockPainting;
        tilePainting.blockMetadata = 3;
        Picture picture = tilePainting.getPainting(3);
        NBTTagCompound nbtPictureTag = null;
        if (item.hasTagCompound()) {
            NBTTagCompound itemTag = item.getTagCompound();
            if (itemTag.hasKey(ItemPainting.TAG_PICTURE)) {
                nbtPictureTag = itemTag.getCompoundTag(ItemPainting.TAG_PICTURE);
            }
        }
        NBTBase nbtImageTag = null;
        if (nbtPictureTag != null && !nbtPictureTag.hasNoTags()) {
            nbtImageTag = nbtPictureTag.getTag(Picture.TAG_IMAGE);
        }
        int obtainedTexture = this.textureCache.obtainTexture(nbtImageTag);
        if (obtainedTexture >= 0) {
            picture.texture = obtainedTexture;
            picture.imageChangeProcessed = true;
        } else if (nbtPictureTag != null) {
            picture.readFromNBT(nbtPictureTag);
        }
        if (type.equals(ItemRenderType.ENTITY)) {
            GL11.glTranslatef(-0.25F, -0.18F, 0.0F);
            float var11 = 0.55F;
            GL11.glScalef(var11, var11, var11);
        } else if (type.equals(ItemRenderType.EQUIPPED)
            || type.equals(ItemRenderType.EQUIPPED_FIRST_PERSON)) {
            tilePainting.blockMetadata = 4;
            GL11.glTranslatef(-0.6F, 0.6F, 0.0F);
        } else if (type.equals(ItemRenderType.INVENTORY)) {
            float var12 = 1.2F;
            GL11.glScalef(var12, var12, var12);
            GL11.glTranslatef(0.0F, -0.18F, 0.42F);
        } else if (type.equals(ItemRenderType.FIRST_PERSON_MAP)) {
            
        }
        Core.instance.painting.tilePaintingRenderer.renderTileEntityAtItem(tilePainting);
        if (obtainedTexture < 0) {
            this.textureCache.putTexture(nbtImageTag, picture.texture);
        }
    }
    
}
