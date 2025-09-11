package com.vanym.paniclecraft.client.renderer.item;

import java.util.stream.IntStream;

import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.client.renderer.PictureTextureCache;
import com.vanym.paniclecraft.client.renderer.TextureHolder;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityPaintingFrameRenderer;
import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPaintingFrame;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.IItemRenderer;

@SideOnly(Side.CLIENT)
public class ItemRendererPaintingFrame implements IItemRenderer {
    
    public final TileEntityPaintingFrameRenderer paintingFrameTileRenderer;
    
    protected PictureTextureCache textureCache;
    
    public ItemRendererPaintingFrame(PictureTextureCache textureCache) {
        this.textureCache = textureCache;
        this.paintingFrameTileRenderer = new TileEntityPaintingFrameRenderer();
        this.paintingFrameTileRenderer.func_147497_a(TileEntityRendererDispatcher.instance);
    }
    
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        if (!Core.instance.painting.clientConfig.renderPaintingFrameItem) {
            return false;
        }
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
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        TileEntityPaintingFrame tilePF = new TileEntityPaintingFrame();
        tilePF.blockType = Core.instance.painting.blockPaintingFrame;
        NBTTagCompound[] tags = IntStream.range(0, ISidePictureProvider.N)
                                         .mapToObj(i->ItemPaintingFrame.getPictureTag(stack, i))
                                         .map(o->o.orElse(null))
                                         .toArray(NBTTagCompound[]::new);
        for (int i = 0; i < ISidePictureProvider.N; ++i) {
            if (tags[i] == null) {
                continue;
            }
            NBTTagCompound pictureTag = tags[i];
            NBTBase imageTag = pictureTag.getTag(Picture.TAG_IMAGE);
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
                try {
                    EntityLivingBase entity = (EntityLivingBase)data[1];
                    if (stack != entity.getEquipmentInSlot(4)) {
                        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glTranslatef(-1.0F, 0.0F, 0.0F);
                        break;
                    }
                } catch (IndexOutOfBoundsException | NullPointerException | ClassCastException e) {
                }
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
        this.paintingFrameTileRenderer.renderTileEntityAtItem(tilePF);
    }
    
}
