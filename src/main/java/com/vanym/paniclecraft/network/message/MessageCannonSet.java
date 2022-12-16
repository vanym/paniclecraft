package com.vanym.paniclecraft.network.message;

import com.vanym.paniclecraft.container.ContainerCannon;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class MessageCannonSet implements IMessage, IMessageHandler<MessageCannonSet, IMessage> {
    
    byte bt;
    double to;
    
    public MessageCannonSet() {}
    
    public MessageCannonSet(byte parBt, double parTo) {
        this.bt = parBt;
        this.to = parTo;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.bt = buf.readByte();
        this.to = buf.readDouble();
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.bt);
        buf.writeDouble(this.to);
    }
    
    @Override
    public IMessage onMessage(MessageCannonSet message, MessageContext ctx) {
        EntityPlayer playerEntity = ctx.getServerHandler().playerEntity;
        if (playerEntity.openContainer instanceof ContainerCannon) {
            ContainerCannon containerCannon = (ContainerCannon)playerEntity.openContainer;
            TileEntityCannon tileCannon = containerCannon.tileCannon;
            switch (message.bt) {
                case 0: {
                    while (message.to >= 360) {
                        message.to -= 360;
                    }
                    
                    while (message.to < 0) {
                        message.to += 360;
                    }
                    tileCannon.setDirection(message.to);
                }
                break;
                case 1: {
                    if (message.to <= 90 && message.to >= -90) {
                        tileCannon.setHeight(message.to);
                    }
                }
                break;
                case 2: {
                    if (message.to >= 0 && message.to <= tileCannon.maxStrength) {
                        tileCannon.setStrength(message.to);
                    }
                }
                break;
            }
            tileCannon.getWorldObj()
                      .markBlockForUpdate(tileCannon.xCoord, tileCannon.yCoord, tileCannon.zCoord);
        }
        return null;
    }
}
