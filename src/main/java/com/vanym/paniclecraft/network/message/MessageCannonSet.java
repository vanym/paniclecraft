package com.vanym.paniclecraft.network.message;

import com.vanym.paniclecraft.container.ContainerCannon;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageCannonSet {
    
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
    
    public final Field field;
    public final double value;
    
    public MessageCannonSet(Field field, double value) {
        this.field = field;
        this.value = value;
    }
    
    public static void encode(MessageCannonSet message, PacketBuffer buf) {
        if (message.field != null) {
            buf.writeByte(message.field.ordinal());
        } else {
            buf.writeByte(-1);
        }
        buf.writeDouble(message.value);
    }
    
    public static MessageCannonSet decode(PacketBuffer buf) {
        byte fieldByte = buf.readByte();
        Field field;
        if (fieldByte >= 0 && fieldByte < Field.values().length) {
            field = Field.values()[fieldByte];
        } else {
            field = null;
        }
        double value = buf.readDouble();
        return new MessageCannonSet(field, value);
    }
    
    public static void handleInWorld(MessageCannonSet message, NetworkEvent.Context ctx) {
        PlayerEntity playerEntity = ctx.getSender();
        if (playerEntity.openContainer instanceof ContainerCannon) {
            ContainerCannon containerCannon = (ContainerCannon)playerEntity.openContainer;
            TileEntityCannon tileCannon = containerCannon.cannon;
            if (message.field != null) {
                synchronized (tileCannon) {
                    message.field.set(tileCannon, message.value);
                }
                tileCannon.markForUpdate();
            }
        }
    }
}
