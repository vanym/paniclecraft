package com.vanym.paniclecraft.network.message;

import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageAdvSignChange {
    
    protected final CompoundNBT tag;
    
    public MessageAdvSignChange(TileEntityAdvSign sign) {
        sign.write(this.tag = new CompoundNBT());
    }
    
    public MessageAdvSignChange(CompoundNBT tag) {
        this.tag = tag;
    }
    
    public static void encode(MessageAdvSignChange message, PacketBuffer buf) {
        buf.writeCompoundTag(message.tag);
    }
    
    public static MessageAdvSignChange decode(PacketBuffer buf) {
        CompoundNBT tag = buf.readCompoundTag();
        return new MessageAdvSignChange(tag);
    }
    
    public static void handleInWorld(MessageAdvSignChange message, NetworkEvent.Context ctx) {
        if (message.tag == null) {
            return;
        }
        int x = message.tag.getInt("x");
        int y = message.tag.getInt("y");
        int z = message.tag.getInt("z");
        if (!TileEntityAdvSign.isValidTag(message.tag)) {
            return;
        }
        TileEntity tile = ctx.getSender().world.getTileEntity(new BlockPos(x, y, z));
        if (tile instanceof TileEntityAdvSign) {
            TileEntityAdvSign tileAS = (TileEntityAdvSign)tile;
            if (tileAS.isEditor(ctx.getSender().getUniqueID())) {
                tileAS.resetEditor();
            } else {
                return;
            }
            tileAS.read(message.tag);
            tileAS.markForUpdate();
        }
    }
}
