package com.vanym.paniclecraft.network.message;

import java.util.Objects;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.item.ItemPaintingTool;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessagePaintingToolUse {
    
    protected final BlockPos pos;
    protected final int px, py;
    protected final byte side;
    protected final boolean tile;
    
    public MessagePaintingToolUse(BlockPos pos, int px, int py, byte side, boolean tile) {
        this.pos = pos.toImmutable();
        this.px = px;
        this.py = py;
        this.side = side;
        this.tile = tile;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.pos, this.px, this.py, this.side, this.tile);
    }
    
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof MessagePaintingToolUse) && this.equals((MessagePaintingToolUse)obj);
    }
    
    public boolean equals(MessagePaintingToolUse mes) {
        return mes != null
            && this.pos != null
            && this.pos.equals(mes.pos)
            && this.px == mes.px
            && this.py == mes.py
            && this.side == mes.side
            && this.tile == mes.tile;
    }
    
    public static void encode(MessagePaintingToolUse message, PacketBuffer buf) {
        buf.writeBlockPos(message.pos);
        buf.writeInt(message.px);
        buf.writeInt(message.py);
        buf.writeByte(message.side);
        buf.writeBoolean(message.tile);
    }
    
    public static MessagePaintingToolUse decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        int px = buf.readInt();
        int py = buf.readInt();
        byte side = buf.readByte();
        boolean tile = buf.readBoolean();
        return new MessagePaintingToolUse(pos, px, py, side, tile);
    }
    
    public static void handleInWorld(MessagePaintingToolUse message, NetworkEvent.Context ctx) {
        ServerPlayerEntity player = ctx.getSender();
        ItemStack heldItem = player.getActiveItemStack();
        if (!ItemPaintingTool.class.isInstance(heldItem.getItem())) {
            return;
        }
        World world = player.world;
        Picture picture = null;
        WorldPictureProvider provider = null;
        if (message.tile) {
            provider = WorldPictureProvider.ANYTILE;
        } else if (Core.instance.painting.config.allowPaintOnBlock) {
            provider = WorldPictureProvider.PAINTONBLOCK;
        }
        if (provider != null) {
            picture = provider.getOrCreatePicture(world, message.pos, message.side);
        }
        if (picture == null
            || !player.canPlayerEdit(message.pos, Direction.byIndex(message.side), heldItem)) {
            return;
        }
        picture.usePaintingTool(heldItem, message.px, message.py);
    }
}
