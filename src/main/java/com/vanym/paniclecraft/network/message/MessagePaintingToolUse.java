package com.vanym.paniclecraft.network.message;

import java.util.Objects;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.item.ItemPaintingTool;
import com.vanym.paniclecraft.network.InWorldHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePaintingToolUse implements IMessage {
    
    int x, y, z, px, py;
    byte side;
    boolean tile;
    
    public MessagePaintingToolUse() {}
    
    public MessagePaintingToolUse(int x, int y, int z, int px, int py, byte side, boolean tile) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.px = px;
        this.py = py;
        this.side = side;
        this.tile = tile;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y, this.z, this.px, this.py, this.side, this.tile);
    }
    
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof MessagePaintingToolUse) && this.equals((MessagePaintingToolUse)obj);
    }
    
    public boolean equals(MessagePaintingToolUse mes) {
        return mes != null
            && this.x == mes.x
            && this.y == mes.y
            && this.z == mes.z
            && this.px == mes.px
            && this.py == mes.py
            && this.side == mes.side
            && this.tile == mes.tile;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.px = buf.readInt();
        this.py = buf.readInt();
        this.side = buf.readByte();
        this.tile = buf.readBoolean();
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.px);
        buf.writeInt(this.py);
        buf.writeByte(this.side);
        buf.writeBoolean(this.tile);
    }
    
    public static class Handler extends InWorldHandler<MessagePaintingToolUse> {
        
        @Override
        public void onMessageInWorld(MessagePaintingToolUse message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().player;
            ItemStack heldItem = player.getActiveItemStack();
            if (!(heldItem.getItem() instanceof ItemPaintingTool)) {
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
                picture = provider.getOrCreatePicture(world, message.x, message.y, message.z,
                                                      message.side);
            }
            if (picture == null
                || !player.canPlayerEdit(new BlockPos(message.x, message.y, message.z),
                                         EnumFacing.getFront(message.side), heldItem)) {
                return;
            }
            picture.usePaintingTool(heldItem, message.px, message.py);
        }
    }
}
