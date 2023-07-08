package com.vanym.paniclecraft.client.renderer.item;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.client.renderer.PictureTextureCache;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityPaintingFrameRenderer;
import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPaintingFrame;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;

import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemRendererPaintingFrame extends ItemStackTileEntityRenderer {
    
    public final TileEntityPaintingFrameRenderer paintingFrameTileRenderer;
    
    protected PictureTextureCache textureCache;
    
    public ItemRendererPaintingFrame(PictureTextureCache textureCache) {
        this.textureCache = textureCache;
        this.paintingFrameTileRenderer = new TileEntityPaintingFrameRenderer();
        this.paintingFrameTileRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
    }
    
    @Override
    public void renderByItem(ItemStack stack) {
        TileEntityPaintingFrame tilePF = new TileEntityPaintingFrame();
        int[] obtainedTextures = new int[ISidePictureProvider.N];
        CompoundNBT itemTag = stack.getTag();
        if (itemTag == null) {
            itemTag = new CompoundNBT();
        }
        for (int i = 0; i < ISidePictureProvider.N; ++i) {
            final String TAG_PICTURE_I = ItemPaintingFrame.getPictureTag(i);
            obtainedTextures[i] = -1;
            if (!itemTag.contains(TAG_PICTURE_I)) {
                continue;
            }
            CompoundNBT pictureTag = itemTag.getCompound(TAG_PICTURE_I);
            INBT imageTag = pictureTag.get(Picture.TAG_IMAGE);
            Picture picture = tilePF.createPicture(i);
            obtainedTextures[i] = this.textureCache.obtainTexture(imageTag);
            if (obtainedTextures[i] >= 0) {
                picture.texture = obtainedTextures[i];
                picture.imageChangeProcessed = true;
            } else if (pictureTag != null) {
                picture.readFromNBT(pictureTag);
            }
        }
        this.paintingFrameTileRenderer.renderAtItem(tilePF);
        for (int i = 0; i < ISidePictureProvider.N; i++) {
            Picture picture = tilePF.getPicture(i);
            if (picture == null || obtainedTextures[i] >= 0) {
                continue;
            }
            final String TAG_PICTURE_I = ItemPaintingFrame.getPictureTag(i);
            CompoundNBT pictureTag = itemTag.getCompound(TAG_PICTURE_I);
            INBT imageTag = pictureTag.get(Picture.TAG_IMAGE);
            this.textureCache.putTexture(imageTag, picture.texture);
        }
    }
    
    public static ItemRendererPaintingFrame create() {
        return new ItemRendererPaintingFrame(Core.instance.painting.textureCache);
    }
}
