package com.vanym.paniclecraft.client.renderer.item;

import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.block.BlockPaintingFrame;
import com.vanym.paniclecraft.client.renderer.PictureTextureCache;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.IItemRenderer;

@SideOnly(Side.CLIENT)
public class ItemRendererPaintingFrame implements IItemRenderer {
    
    protected PictureTextureCache textureCache;
    
    public ItemRendererPaintingFrame(PictureTextureCache textureCache) {
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
    public void renderItem(ItemRenderType type, ItemStack itemS, Object... data) {
        TileEntityPaintingFrame tilePF = new TileEntityPaintingFrame();
        tilePF.blockType = Core.instance.painting.blockPaintingFrame;
        int[] obtainedTextures = new int[TileEntityPaintingFrame.N];
        NBTTagCompound itemTag = itemS.getTagCompound();
        if (itemTag == null) {
            itemTag = new NBTTagCompound();
        }
        for (int i = 0; i < TileEntityPaintingFrame.N; ++i) {
            final String TAG_PICTURE_I = BlockPaintingFrame.getPictureTag(i);
            obtainedTextures[i] = -1;
            if (!itemTag.hasKey(TAG_PICTURE_I)) {
                continue;
            }
            NBTTagCompound pictureTag = itemTag.getCompoundTag(TAG_PICTURE_I);
            NBTBase imageTag = pictureTag.getTag(Picture.TAG_IMAGE);
            Picture picture = tilePF.createPicture(i);
            obtainedTextures[i] = this.textureCache.obtainTexture(imageTag);
            if (obtainedTextures[i] >= 0) {
                picture.texture = obtainedTextures[i];
                picture.imageChangeProcessed = true;
            } else if (pictureTag != null) {
                picture.readFromNBT(pictureTag);
            }
        }
        switch (type) {
            case ENTITY:
                GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            break;
            case FIRST_PERSON_MAP:
            break;
            case EQUIPPED_FIRST_PERSON:
                GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(-1.0F, 0.0F, 0.0F);
            break;
            case EQUIPPED:
                GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(0.0F, 0.0F, -1.0F);
            break;
            case INVENTORY:
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(0.0F, -0.9F, 0.0F);
            break;
            default:
            break;
        }
        Core.instance.painting.tilePaintingFrameRenderer.renderTileEntityAtItem(tilePF);
        for (int i = 0; i < TileEntityPaintingFrame.N; i++) {
            Picture picture = tilePF.getPainting(i);
            if (picture == null || obtainedTextures[i] >= 0) {
                continue;
            }
            final String TAG_PICTURE_I = BlockPaintingFrame.getPictureTag(i);
            NBTTagCompound pictureTag = itemTag.getCompoundTag(TAG_PICTURE_I);
            NBTBase imageTag = pictureTag.getTag(Picture.TAG_IMAGE);
            this.textureCache.putTexture(imageTag, picture.texture);
        }
    }
    
}
