package com.vanym.paniclecraft.network.message;

import com.vanym.paniclecraft.container.ContainerCannon;
import com.vanym.paniclecraft.network.InWorldHandler;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageCannonSet implements IMessage {
    
    public static enum Field {
        DIRECTION {
            @Override
            public void set(TileEntityCannon tileCannon, double value) {
                tileCannon.setDirection(value);
            }
        },
        HEIGHT {
            @Override
            public void set(TileEntityCannon tileCannon, double value) {
                tileCannon.setHeight(value);
            }
        },
        STRENGTH {
            @Override
            public void set(TileEntityCannon tileCannon, double value) {
                tileCannon.setStrength(value);
            }
        };
        
        public abstract void set(TileEntityCannon tileCannon, double value);
        
        public final MessageCannonSet message(double value) {
            return new MessageCannonSet(this, value);
        }
    }
    
    Field field;
    double value;
    
    public MessageCannonSet() {}
    
    public MessageCannonSet(Field field, double value) {
        this.field = field;
        this.value = value;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        byte fieldByte = buf.readByte();
        if (fieldByte >= 0 && fieldByte < Field.values().length) {
            this.field = Field.values()[fieldByte];
        } else {
            this.field = null;
        }
        this.value = buf.readDouble();
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        if (this.field != null) {
            buf.writeByte(this.field.ordinal());
        } else {
            buf.writeByte(-1);
        }
        buf.writeDouble(this.value);
    }
    
    public static class Handler extends InWorldHandler<MessageCannonSet> {
        
        @Override
        public void onMessageInWorld(MessageCannonSet message, MessageContext ctx) {
            EntityPlayer playerEntity = ctx.getServerHandler().player;
            if (playerEntity.openContainer instanceof ContainerCannon) {
                ContainerCannon containerCannon = (ContainerCannon)playerEntity.openContainer;
                TileEntityCannon tileCannon = containerCannon.cannon;
                if (message.field != null) {
                    message.field.set(tileCannon, message.value);
                    tileCannon.markForUpdate();
                }
            }
        }
    }
}
