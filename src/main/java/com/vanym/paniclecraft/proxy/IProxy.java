package com.vanym.paniclecraft.proxy;

import net.minecraftforge.common.config.Configuration;

public interface IProxy{
	public void preInit(Configuration config);
	
	public void init(Configuration config);
	
	public void postInit(Configuration config);
}
