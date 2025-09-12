package com.vanym.paniclecraft.client.renderer.item;

import java.util.stream.IntStream;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.client.renderer.PictureTextureCache;
import com.vanym.paniclecraft.client.renderer.TextureHolder;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityPaintingFrameRenderer;
import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPaintingFrame;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;

import net.minecraft.client.renderer.IRenderTypeBuffer;
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
        this.paintingFrameTileRenderer =
                new TileEntityPaintingFrameRenderer(TileEntityRendererDispatcher.instance);
    }
    
    @Override
    public void renderByItem(
            ItemStack stack,
            MatrixStack ms,
            IRenderTypeBuffer buffers,
            int light,
            int overlay) {
        TileEntityPaintingFrame tilePF = new TileEntityPaintingFrame();
        CompoundNBT[] tags = IntStream.range(0, ISidePictureProvider.N)
                                      .mapToObj(i->ItemPaintingFrame.getPictureTag(stack, i))
                                      .map(o->o.orElse(null))
                                      .toArray(CompoundNBT[]::new);
        for (int i = 0; i < ISidePictureProvider.N; ++i) {
            if (tags[i] == null) {
                continue;
            }
            CompoundNBT pictureTag = tags[i];
            INBT imageTag = pictureTag.get(Picture.TAG_IMAGE);
            Picture picture = tilePF.createPicture(i);
            TextureHolder obtainedTexture = this.textureCache.obtain(imageTag);
            if (obtainedTexture != null) {
                picture.setTexture(obtainedTexture);
                picture.imageChangeProcessed = true;
            } else if (pictureTag != null) {
                this.textureCache.put(imageTag, picture.getTexture());
                picture.deserializeNBT(pictureTag);
            }
        }
        this.paintingFrameTileRenderer.renderByItem(tilePF, ms, buffers, light, overlay);
    }
    
    public static ItemRendererPaintingFrame create() {
        return new ItemRendererPaintingFrame(Core.instance.painting.textureCache);
    }
}
