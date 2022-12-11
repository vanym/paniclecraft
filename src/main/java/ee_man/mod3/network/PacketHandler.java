package ee_man.mod3.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import ee_man.mod3.DEF;
import ee_man.mod3.network.message.MessageAdvSignChange;
import ee_man.mod3.network.message.MessageCannonChange;
import ee_man.mod3.network.message.MessageCannonSet;
import ee_man.mod3.network.message.MessageChessChoose;
import ee_man.mod3.network.message.MessageChessMove;
import ee_man.mod3.network.message.MessageChessNewGame;
import ee_man.mod3.network.message.MessagePaintBrushUse;
import ee_man.mod3.network.message.MessagePaletteChange;

public class PacketHandler{
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(DEF.MOD_ID);
	
	public static void init(){
		INSTANCE.registerMessage(MessagePaintBrushUse.class, MessagePaintBrushUse.class, 0, Side.SERVER);
		INSTANCE.registerMessage(MessagePaletteChange.class, MessagePaletteChange.class, 1, Side.SERVER);
		INSTANCE.registerMessage(MessageAdvSignChange.class, MessageAdvSignChange.class, 2, Side.SERVER);
		INSTANCE.registerMessage(MessageCannonChange.class, MessageCannonChange.class, 3, Side.SERVER);
		INSTANCE.registerMessage(MessageCannonSet.class, MessageCannonSet.class, 4, Side.SERVER);
		INSTANCE.registerMessage(MessageChessMove.class, MessageChessMove.class, 5, Side.SERVER);
		INSTANCE.registerMessage(MessageChessChoose.class, MessageChessChoose.class, 6, Side.SERVER);
		INSTANCE.registerMessage(MessageChessNewGame.class, MessageChessNewGame.class, 7, Side.SERVER);
	}
}
