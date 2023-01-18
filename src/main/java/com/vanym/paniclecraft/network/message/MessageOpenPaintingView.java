package com.vanym.paniclecraft.network.message;

import com.vanym.paniclecraft.client.gui.container.GuiPaintingView;
import com.vanym.paniclecraft.container.ContainerPaintingViewClient;
import com.vanym.paniclecraft.core.component.painting.FixedPictureSize;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class MessageOpenPaintingView
        implements
            IMessage,
            IMessageHandler<MessageOpenPaintingView, IMessage> {
    
    int windowId, pictureWidth, pictureHeight, sizeX, sizeY;
    boolean hasAlpha;
    
    public MessageOpenPaintingView() {}
    
    public MessageOpenPaintingView(int windowId,
            int pictureWidth,
            int pictureHeight,
            int sizeX,
            int sizeY,
            boolean hasAlpha) {
        this.windowId = windowId;
        this.pictureWidth = pictureWidth;
        this.pictureHeight = pictureHeight;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.hasAlpha = hasAlpha;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.windowId = buf.readInt();
        this.pictureWidth = buf.readInt();
        this.pictureHeight = buf.readInt();
        this.sizeX = buf.readInt();
        this.sizeY = buf.readInt();
        this.hasAlpha = buf.readBoolean();
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.windowId);
        buf.writeInt(this.pictureWidth);
        buf.writeInt(this.pictureHeight);
        buf.writeInt(this.sizeX);
        buf.writeInt(this.sizeY);
        buf.writeBoolean(this.hasAlpha);
    }
    
    @Override
    public IMessage onMessage(MessageOpenPaintingView message, MessageContext ctx) {
        EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
        FMLCommonHandler.instance()
                        .showGuiScreen(new GuiPaintingView(
                                new ContainerPaintingViewClient(
                                        new FixedPictureSize(
                                                message.pictureWidth,
                                                message.pictureHeight),
                                        message.sizeX,
                                        message.sizeY,
                                        message.hasAlpha)));
        player.openContainer.windowId = message.windowId;
        return null;
    }
}
