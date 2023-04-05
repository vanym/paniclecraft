package com.vanym.paniclecraft.network.message;

import com.vanym.paniclecraft.client.gui.container.GuiPaintingEditView;
import com.vanym.paniclecraft.client.gui.container.GuiPaintingView;
import com.vanym.paniclecraft.container.ContainerPaintingViewClient;
import com.vanym.paniclecraft.core.component.painting.FixedPictureSize;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageOpenPaintingView
        implements
            IMessage,
            IMessageHandler<MessageOpenPaintingView, IMessage> {
    
    int windowId, pictureWidth, pictureHeight, sizeX, sizeY;
    boolean hasAlpha, editable;
    
    public MessageOpenPaintingView() {}
    
    public MessageOpenPaintingView(int windowId,
            int pictureWidth,
            int pictureHeight,
            int sizeX,
            int sizeY,
            boolean hasAlpha,
            boolean editable) {
        this.windowId = windowId;
        this.pictureWidth = pictureWidth;
        this.pictureHeight = pictureHeight;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.hasAlpha = hasAlpha;
        this.editable = editable;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.windowId = buf.readInt();
        this.pictureWidth = buf.readInt();
        this.pictureHeight = buf.readInt();
        this.sizeX = buf.readInt();
        this.sizeY = buf.readInt();
        this.hasAlpha = buf.readBoolean();
        this.editable = buf.readBoolean();
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.windowId);
        buf.writeInt(this.pictureWidth);
        buf.writeInt(this.pictureHeight);
        buf.writeInt(this.sizeX);
        buf.writeInt(this.sizeY);
        buf.writeBoolean(this.hasAlpha);
        buf.writeBoolean(this.editable);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageOpenPaintingView message, MessageContext ctx) {
        EntityPlayer player = FMLClientHandler.instance().getClient().player;
        ContainerPaintingViewClient view = new ContainerPaintingViewClient(
                new FixedPictureSize(message.pictureWidth, message.pictureHeight),
                message.sizeX,
                message.sizeY,
                message.hasAlpha);
        GuiPaintingView gui;
        if (message.editable) {
            gui = new GuiPaintingEditView(view);
        } else {
            gui = new GuiPaintingView(view);
        }
        FMLCommonHandler.instance().showGuiScreen(gui);
        player.openContainer.windowId = message.windowId;
        return null;
    }
}
