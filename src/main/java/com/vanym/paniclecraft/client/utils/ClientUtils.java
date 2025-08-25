package com.vanym.paniclecraft.client.utils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ClientUtils {
    
    public static boolean isMe(Entity entity) {
        Minecraft mc = Minecraft.getMinecraft();
        return entity == mc.thePlayer;
    }
}
