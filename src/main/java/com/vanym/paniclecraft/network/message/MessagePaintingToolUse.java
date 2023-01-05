package com.vanym.paniclecraft.network.message;

import java.util.Objects;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.block.BlockPaintingContainer;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.entity.EntityPaintOnBlock;
import com.vanym.paniclecraft.item.ItemPaintingTool;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MessagePaintingToolUse
        implements
            IMessage,
            IMessageHandler<MessagePaintingToolUse, IMessage> {
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
    
    @Override
    public IMessage onMessage(MessagePaintingToolUse message, MessageContext ctx) {
        EntityPlayer playerEntity = ctx.getServerHandler().playerEntity;
        if (playerEntity.getHeldItem() == null) {
            return null;
        }
        ItemStack heldItem = playerEntity.getHeldItem();
        if (!(heldItem.getItem() instanceof ItemPaintingTool)) {
            return null;
        }
        World world = playerEntity.worldObj;
        Picture picture = null;
        if (message.tile) {
            picture = BlockPaintingContainer.getPicture(world, message.x, message.y, message.z,
                                                        message.side);
        } else if (Core.instance.painting.config.allowPaintOnBlock) {
            picture = EntityPaintOnBlock.getOrCreateEntityPicture(world, message.x,
                                                                  message.y,
                                                                  message.z,
                                                                  message.side);
        }
        if (picture == null || !playerEntity.canPlayerEdit(message.x, message.y, message.z,
                                                           message.side, heldItem)) {
            return null;
        }
        picture.usePaintingTool(heldItem, message.px, message.py);
        return null;
    }
}
