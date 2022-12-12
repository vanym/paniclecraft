package com.vanym.paniclecraft.network;

import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.network.message.MessageAdvSignChange;
import com.vanym.paniclecraft.network.message.MessageCannonChange;
import com.vanym.paniclecraft.network.message.MessageCannonSet;
import com.vanym.paniclecraft.network.message.MessageChessChoose;
import com.vanym.paniclecraft.network.message.MessageChessMove;
import com.vanym.paniclecraft.network.message.MessageChessNewGame;
import com.vanym.paniclecraft.network.message.MessagePaintBrushUse;
import com.vanym.paniclecraft.network.message.MessagePaletteChange;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE =
            NetworkRegistry.INSTANCE.newSimpleChannel(DEF.MOD_ID);
    
    public static void init() {
        INSTANCE.registerMessage(MessagePaintBrushUse.class, MessagePaintBrushUse.class, 0,
                                 Side.SERVER);
        INSTANCE.registerMessage(MessagePaletteChange.class, MessagePaletteChange.class, 1,
                                 Side.SERVER);
        INSTANCE.registerMessage(MessageAdvSignChange.class, MessageAdvSignChange.class, 2,
                                 Side.SERVER);
        INSTANCE.registerMessage(MessageCannonChange.class, MessageCannonChange.class, 3,
                                 Side.SERVER);
        INSTANCE.registerMessage(MessageCannonSet.class, MessageCannonSet.class, 4, Side.SERVER);
        INSTANCE.registerMessage(MessageChessMove.class, MessageChessMove.class, 5, Side.SERVER);
        INSTANCE.registerMessage(MessageChessChoose.class, MessageChessChoose.class, 6,
                                 Side.SERVER);
        INSTANCE.registerMessage(MessageChessNewGame.class, MessageChessNewGame.class, 7,
                                 Side.SERVER);
    }
}
