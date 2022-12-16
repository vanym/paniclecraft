package com.vanym.paniclecraft.network.message;

import com.vanym.paniclecraft.container.ContainerCannon;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class MessageCannonChange
        implements
            IMessage,
            IMessageHandler<MessageCannonChange, IMessage> {
    
    byte bt;
    
    public MessageCannonChange() {}
    
    public MessageCannonChange(byte parBt) {
        this.bt = parBt;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.bt = buf.readByte();
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.bt);
    }
    
    @Override
    public IMessage onMessage(MessageCannonChange message, MessageContext ctx) {
        EntityPlayer playerEntity = ctx.getServerHandler().playerEntity;
        if (playerEntity.openContainer instanceof ContainerCannon) {
            ContainerCannon containerCannon = (ContainerCannon)playerEntity.openContainer;
            TileEntityCannon tileCannon = containerCannon.tileCannon;
            switch (message.bt) {
                case 0:
                    tileCannon.direction--;
                    while (tileCannon.direction < 0) {
                        tileCannon.direction += 360;
                    }
                break;
                case 1:
                    tileCannon.direction++;
                    while (tileCannon.direction >= 360) {
                        tileCannon.direction -= 360;
                    }
                break;
                case 2:
                    if (tileCannon.height > -90) {
                        tileCannon.height--;
                    }
                break;
                case 3:
                    if (tileCannon.height < 90) {
                        tileCannon.height++;
                    }
                break;
                case 4:
                    tileCannon.strength -= 0.1;
                    if (tileCannon.strength < 0) {
                        tileCannon.strength = 0;
                    }
                break;
                case 5:
                    tileCannon.strength += 0.1;
                    if (tileCannon.strength > tileCannon.maxStrength) {
                        tileCannon.strength = tileCannon.maxStrength;
                    }
                break;
                case 0 + 6:
                    tileCannon.direction -= 10;
                    while (tileCannon.direction < 0) {
                        tileCannon.direction += 360;
                    }
                break;
                case 1 + 6:
                    tileCannon.direction += 10;
                    while (tileCannon.direction >= 360) {
                        tileCannon.direction -= 360;
                    }
                break;
                case 2 + 6:
                    tileCannon.height -= 10;
                    if (tileCannon.height < -90) {
                        tileCannon.height = -90;
                    }
                break;
                case 3 + 6:
                    tileCannon.height += 10;
                    if (tileCannon.height > 90) {
                        tileCannon.height = 90;
                    }
                break;
                case 4 + 6:
                    tileCannon.strength -= 0.5;
                    if (tileCannon.strength < 0) {
                        tileCannon.strength = 0;
                    }
                break;
                case 5 + 6:
                    tileCannon.strength += 0.5;
                    if (tileCannon.strength > tileCannon.maxStrength) {
                        tileCannon.strength = tileCannon.maxStrength;
                    }
                break;
                case 0 + 12:
                    tileCannon.direction -= 45;
                    while (tileCannon.direction < 0) {
                        tileCannon.direction += 360;
                    }
                break;
                case 1 + 12:
                    tileCannon.direction += 45;
                    while (tileCannon.direction >= 360) {
                        tileCannon.direction -= 360;
                    }
                break;
                case 2 + 12:
                    tileCannon.height -= 45;
                    if (tileCannon.height < -90) {
                        tileCannon.height = -90;
                    }
                break;
                case 3 + 12:
                    tileCannon.height += 45;
                    if (tileCannon.height > 90) {
                        tileCannon.height = 90;
                    }
                break;
                case 4 + 12:
                    tileCannon.strength -= 1;
                    if (tileCannon.strength < 0) {
                        tileCannon.strength = 0;
                    }
                break;
                case 5 + 12:
                    tileCannon.strength += 1;
                    if (tileCannon.strength > tileCannon.maxStrength) {
                        tileCannon.strength = tileCannon.maxStrength;
                    }
                break;
            }
            tileCannon.vector = null;
            tileCannon.getWorldObj()
                      .markBlockForUpdate(tileCannon.xCoord, tileCannon.yCoord, tileCannon.zCoord);
        }
        return null;
    }
}
