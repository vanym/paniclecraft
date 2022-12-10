package ee_man.mod3.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.MinecraftForge;
import ee_man.mod3.tileEntity.TileEntityAdvSign;
import ee_man.mod3.tileEntity.TileEntityCannon;
import ee_man.mod3.tileEntity.TileEntityChessDesk;
import ee_man.mod3.tileEntity.TileEntityPainting;

public class Mod3Hooks{
	
	public static Mod3Event.OnAdvSignPacket OnAdvSignPacket(TileEntityAdvSign par1TileAdvSign, String par2TextToSet, byte par3Red, byte par4Green, byte par5Blue, EntityPlayer par6Player){
		Mod3Event.OnAdvSignPacket event = new Mod3Event.OnAdvSignPacket(par1TileAdvSign, par2TextToSet, par3Red, par4Green, par5Blue, par6Player);
		MinecraftForge.EVENT_BUS.post(event);
		return event;
	}
	
	public static Mod3Event.OnPaintBrushPacket OnPaintBrushPacket(TileEntityPainting par1TilePaintBlock, EntityPlayer par2Player, int par3x, int par4y, ItemStack par5is){
		Mod3Event.OnPaintBrushPacket event = new Mod3Event.OnPaintBrushPacket(par1TilePaintBlock, par2Player, par3x, par4y, par5is);
		MinecraftForge.EVENT_BUS.post(event);
		return event;
	}
	
	public static Mod3Event.OnChessPacket.MakeMove OnChessPacketMakeMove(TileEntityChessDesk par1ChessDesk, EntityPlayer par2Player, byte par3From, byte par4To){
		Mod3Event.OnChessPacket.MakeMove event = new Mod3Event.OnChessPacket.MakeMove(par1ChessDesk, par2Player, par3From, par4To);
		MinecraftForge.EVENT_BUS.post(event);
		return event;
	}
	
	public static Mod3Event.OnChessPacket.MakeChoose OnChessPacketMakeChoose(TileEntityChessDesk par1ChessDesk, EntityPlayer par2Player, byte par3Choose){
		Mod3Event.OnChessPacket.MakeChoose event = new Mod3Event.OnChessPacket.MakeChoose(par1ChessDesk, par2Player, par3Choose);
		MinecraftForge.EVENT_BUS.post(event);
		return event;
	}
	
	public static Mod3Event.OnChessPacket.ResetGame OnChessPacketResetGame(TileEntityChessDesk par1ChessDesk, EntityPlayer par2Player, String par3Str, String par4Str){
		Mod3Event.OnChessPacket.ResetGame event = new Mod3Event.OnChessPacket.ResetGame(par1ChessDesk, par2Player, par3Str, par4Str);
		MinecraftForge.EVENT_BUS.post(event);
		return event;
	}
	
	public static Mod3Event.OnCannonPacket OnCannonPacket(TileEntityCannon par1TileCannon, EntityPlayer par2Player, int par3Action){
		Mod3Event.OnCannonPacket event = new Mod3Event.OnCannonPacket(par1TileCannon, par2Player, par3Action);
		MinecraftForge.EVENT_BUS.post(event);
		return event;
	}
}
