package com.vanym.paniclecraft.utils;

public class CheckersDesk{
	
	public byte[] desk;
	
	public boolean isWhiteTurn;
	
	public static final byte[] getDefDesk(){
		return new byte[]{+1, +1, +1, +1, +1, +1, +1, +1,
						  +0, +1, +0, +1, +0, +1, +0, +1,
						  -1, +0, -1, +0, -1, +0, -1, +0,
						  -1, -1, -1, -1, -1, -1, -1, -1};
	}
	
	
}
