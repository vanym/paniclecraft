package com.vanym.paniclecraft.network.message;

import com.vanym.paniclecraft.container.ContainerCannon;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class MessageCannonSet implements IMessage, IMessageHandler<MessageCannonSet, IMessage> {
    
    public static enum Field {
        DIRECTION {
            @Override
            public void set(TileEntityCannon tileCannon, double data) {
                tileCannon.setDirection(data);
            }
        },
        HEIGHT {
            @Override
            public void set(TileEntityCannon tileCannon, double data) {
                tileCannon.setHeight(data);
            }
        },
        STRENGTH {
            @Override
            public void set(TileEntityCannon tileCannon, double data) {
                tileCannon.setStrength(data);
            }
        };
        
        public abstract void set(TileEntityCannon tileCannon, double data);
    }
    
    Field field;
    double data;
    
    public MessageCannonSet() {}
    
    public MessageCannonSet(Field field, double data) {
        this.field = field;
        this.data = data;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        byte fieldByte = buf.readByte();
        if (fieldByte >= 0 && fieldByte < Field.values().length) {
            this.field = Field.values()[fieldByte];
        } else {
            this.field = null;
        }
        this.data = buf.readDouble();
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        if (this.field != null) {
            buf.writeByte(this.field.ordinal());
        } else {
            buf.writeByte(-1);
        }
        buf.writeDouble(this.data);
    }
    
    @Override
    public IMessage onMessage(MessageCannonSet message, MessageContext ctx) {
        EntityPlayer playerEntity = ctx.getServerHandler().playerEntity;
        if (playerEntity.openContainer instanceof ContainerCannon) {
            ContainerCannon containerCannon = (ContainerCannon)playerEntity.openContainer;
            TileEntityCannon tileCannon = containerCannon.cannon;
            if (message.field != null) {
                message.field.set(tileCannon, message.data);
                tileCannon.markForUpdate();
            }
        }
        return null;
    }
}
