package com.vanym.paniclecraft.network.message;

import java.awt.Color;

import com.vanym.paniclecraft.container.ContainerPalette;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessagePaletteSetColor {
    
    public final Color color;
    
    public MessagePaletteSetColor(Color color) {
        this.color = color;
    }
    
    public static void encode(MessagePaletteSetColor message, PacketBuffer buf) {
        buf.writeInt(message.color.getRGB());
    }
    
    public static MessagePaletteSetColor decode(PacketBuffer buf) {
        return new MessagePaletteSetColor(new Color(buf.readInt(), true));
    }
    
    public static void handleInWorld(MessagePaletteSetColor message, NetworkEvent.Context ctx) {
        PlayerEntity playerEntity = ctx.getSender();
        if (playerEntity.openContainer instanceof ContainerPalette) {
            ContainerPalette palette = (ContainerPalette)playerEntity.openContainer;
            palette.setColor(message.color);
        }
    }
}
