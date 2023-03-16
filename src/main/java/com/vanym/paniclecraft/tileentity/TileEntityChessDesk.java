package com.vanym.paniclecraft.tileentity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

import com.vanym.paniclecraft.client.gui.GuiChess;
import com.vanym.paniclecraft.core.component.deskgame.ChessGame;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityChessDesk extends TileEntityBase {
    
    protected ChessGame game = new ChessGame();
    protected final List<Move> imoves = new ArrayList<>();
    
    public final List<Move> moves = Collections.unmodifiableList(this.imoves);
    
    public final Set<BiConsumer<String, Object[]>> listeners = new HashSet<>();
    
    public static final String TAG_MOVES = "Moves";
    public static final String TAG_MOVE = "Move";
    public static final String TAG_PLAYER = "Player";
    public static final String TAG_PLAYERNAME = "Name";
    public static final String TAG_PLAYERUUID = "UUID";
    public static final String TAG_PLAYERUUIDMOST = "UUIDMost";
    public static final String TAG_PLAYERUUIDLEAST = "UUIDLeast";
    
    @Override
    public void writeToNBT(NBTTagCompound nbtTag) {
        super.writeToNBT(nbtTag);
        NBTTagList list = new NBTTagList();
        this.writeMovesToNBT(list);
        nbtTag.setTag(TAG_MOVES, list);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbtTag) {
        super.readFromNBT(nbtTag);
        NBTTagList list = nbtTag.getTagList(TAG_MOVES, 10);
        this.readMovesFromNBT(list);
    }
    
    public void writeMovesToNBT(NBTTagList listTag) {
        for (Move move : this.imoves) {
            NBTTagCompound moveTag = new NBTTagCompound();
            move.writeToNBT(moveTag);
            listTag.appendTag(moveTag);
        }
    }
    
    public void readMovesFromNBT(NBTTagList listTag) {
        this.game = new ChessGame();
        this.imoves.clear();
        for (int i = 0; i < listTag.tagCount(); i++) {
            NBTTagCompound tag = listTag.getCompoundTagAt(i);
            Move wrap = new Move();
            wrap.readFromNBT(tag, i % 2 == 0);
            ChessGame.Move move;
            if (wrap.move == null || (move = this.game.move(wrap.move)) == null) {
                return;
            }
            this.imoves.add(new Move(wrap, move));
        }
    }
    
    @Override
    public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet) {
        super.onDataPacket(manager, packet);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            this.updateGui();
        }
    }
    
    @SideOnly(Side.CLIENT)
    protected void updateGui() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen instanceof GuiChess) {
            GuiChess guiChess = (GuiChess)mc.currentScreen;
            guiChess.updateGui(this);
        }
    }
    
    public void resetGame() {
        this.game = new ChessGame();
        this.imoves.clear();
        this.markForUpdate();
        this.sendEvent("chess_reset");
    }
    
    public boolean move(ChessGame.Move move, EntityPlayer player) {
        return this.move(move, player.getCommandSenderName(), player.getUniqueID());
    }
    
    public boolean move(ChessGame.Move move, String playerName, UUID playerUUID) {
        if (this.imoves.size() >= 0xFFFF) {
            return false;
        }
        move = this.game.move(move);
        if (move == null) {
            return false;
        }
        this.imoves.add(new Move(move, playerUUID, playerName));
        this.markForUpdate();
        this.sendEvent("chess_move", move.toString(), this.imoves.size());
        return true;
    }
    
    public ChessGame getGame() {
        return this.game;
    }
    
    protected void sendEvent(String name, Object... args) {
        this.listeners.forEach(b->b.accept(name, args));
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB box = this.getBlockType()
                                .getCollisionBoundingBoxFromPool(this.worldObj, this.xCoord,
                                                                 this.yCoord, this.zCoord);
        box.maxY += 0.5D;
        return box;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 16384.0D;
    }
    
    public static class Move {
        public ChessGame.Move move;
        public UUID playerUUID;
        public String playerName;
        
        public Move() {}
        
        public Move(ChessGame.Move move, UUID playerUUID, String playerName) {
            this.move = move;
            this.playerUUID = playerUUID;
            this.playerName = playerName;
        }
        
        public Move(Move wrap, ChessGame.Move move) {
            this.move = move;
            this.playerUUID = wrap.playerUUID;
            this.playerName = wrap.playerName;
        }
        
        public void writeToNBT(NBTTagCompound nbtTag) {
            nbtTag.setString(TAG_MOVE, this.move.toString(false));
            NBTTagCompound playerTag = new NBTTagCompound();
            if (this.playerUUID != null) {
                playerTag.setLong(TAG_PLAYERUUIDMOST, this.playerUUID.getMostSignificantBits());
                playerTag.setLong(TAG_PLAYERUUIDLEAST, this.playerUUID.getLeastSignificantBits());
            }
            if (this.playerName != null) {
                playerTag.setString(TAG_PLAYERNAME, this.playerName);
            }
            nbtTag.setTag(TAG_PLAYER, playerTag);
        }
        
        public void readFromNBT(NBTTagCompound nbtTag, boolean white) {
            try {
                String str = nbtTag.getString(TAG_MOVE);
                this.move = new ChessGame.Move(str, white);
            } catch (IllegalArgumentException e) {
                return;
            }
            if (nbtTag.hasKey(TAG_PLAYER, 10)) {
                NBTTagCompound playerTag = nbtTag.getCompoundTag(TAG_PLAYER);
                if (playerTag.hasKey(TAG_PLAYERUUID, 8)) {
                    this.playerUUID = UUID.fromString(playerTag.getString(TAG_PLAYERUUID));
                } else if (playerTag.hasKey(TAG_PLAYERUUIDMOST, 4)
                    && playerTag.hasKey(TAG_PLAYERUUIDLEAST, 4)) {
                    this.playerUUID = new UUID(
                            playerTag.getLong(TAG_PLAYERUUIDMOST),
                            playerTag.getLong(TAG_PLAYERUUIDLEAST));
                }
                if (playerTag.hasKey(TAG_PLAYERNAME, 8)) {
                    this.playerName = playerTag.getString(TAG_PLAYERNAME);
                }
            }
        }
    }
}
