package com.vanym.paniclecraft.block;

import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IWithCustomStateMapper {
    
    @SideOnly(Side.CLIENT)
    public IStateMapper getStateMapper();
}
