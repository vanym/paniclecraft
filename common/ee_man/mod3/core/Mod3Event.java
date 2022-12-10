package ee_man.mod3.core;

import ee_man.mod3.tileEntity.TileEntityAdvSign;
import ee_man.mod3.tileEntity.TileEntityCannon;
import ee_man.mod3.tileEntity.TileEntityChessDesk;
import ee_man.mod3.tileEntity.TileEntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

public class Mod3Event extends Event{
	
	@Cancelable
	public static class OnAdvSignPacket extends Mod3Event{
		public String textToSet;
		
		public byte red;
		
		public byte green;
		
		public byte blue;
		
		public final TileEntityAdvSign tileAdvSign;
		
		public final EntityPlayer player;
		
		public OnAdvSignPacket(TileEntityAdvSign par1TileAdvSign, String par2TextToSet, byte par3Red, byte par4Green, byte par5Blue, EntityPlayer par6Player){
			tileAdvSign = par1TileAdvSign;
			textToSet = par2TextToSet;
			red = par3Red;
			green = par4Green;
			blue = par5Blue;
			player = par6Player;
		}
	}
	
	@Cancelable
	public static class OnPaintBrushPacket extends Mod3Event{
		public int x;
		
		public int y;
		
		public TileEntityPainting tileP;
		
		public final EntityPlayer player;
		
		public ItemStack itemStack;
		
		public OnPaintBrushPacket(TileEntityPainting par1TilePaintBlock, EntityPlayer par2Player, int par3x, int par4y, ItemStack par5is){
			tileP = par1TilePaintBlock;
			player = par2Player;
			x = par3x;
			y = par4y;
			itemStack = par5is;
		}
	}
	
	@Cancelable
	public static class OnChessPacket extends Mod3Event{
		public TileEntityChessDesk tileChessDesk;
		
		public final EntityPlayer player;
		
		protected OnChessPacket(TileEntityChessDesk par1ChessDesk, EntityPlayer par2Player){
			tileChessDesk = par1ChessDesk;
			player = par2Player;
		}
		
		@Cancelable
		public static class MakeMove extends OnChessPacket{
			public byte from;
			
			public byte to;
			
			public MakeMove(TileEntityChessDesk par1ChessDesk, EntityPlayer par2Player, byte par3From, byte par4To){
				super(par1ChessDesk, par2Player);
				from = par3From;
				to = par4To;
			}
		}
		
		@Cancelable
		public static class MakeChoose extends OnChessPacket{
			public byte choose;
			
			public MakeChoose(TileEntityChessDesk par1ChessDesk, EntityPlayer par2Player, byte par3Choose){
				super(par1ChessDesk, par2Player);
				choose = par3Choose;
			}
		}
		
		@Cancelable
		public static class ResetGame extends OnChessPacket{
			
			public String whitePlayer;
			
			public String blackPlayer;
			
			public ResetGame(TileEntityChessDesk par1ChessDesk, EntityPlayer par2Player, String par3Str, String par4Str){
				super(par1ChessDesk, par2Player);
				whitePlayer = par3Str;
				blackPlayer = par4Str;
			}
			
		}
	}
	
	@Cancelable
	public static class OnCannonPacket extends Mod3Event{
		public TileEntityCannon tileCannon;
		
		public final EntityPlayer player;
		
		public int action;
		
		public OnCannonPacket(TileEntityCannon par1TileCannon, EntityPlayer par2Player, int par3Action){
			tileCannon = par1TileCannon;
			player = par2Player;
			action = par3Action;
		}
	}
}
