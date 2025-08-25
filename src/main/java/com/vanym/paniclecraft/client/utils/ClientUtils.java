package com.vanym.paniclecraft.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientUtils {
    
    public static boolean isMe(Entity entity) {
        Minecraft mc = Minecraft.getInstance();
        return entity == mc.player;
    }
}
