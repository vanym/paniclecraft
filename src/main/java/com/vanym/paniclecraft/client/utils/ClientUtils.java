package com.vanym.paniclecraft.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientUtils {
    
    public static boolean isMe(Entity entity) {
        Minecraft mc = Minecraft.getMinecraft();
        return entity == mc.player;
    }
}
