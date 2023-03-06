package com.vanym.paniclecraft.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    protected List<Move> moves = new ArrayList<>();
    
    public static final String TAG_MOVES = "Moves";
    public static final String TAG_MOVE = "Move";
    public static final String TAG_PLAYER = "Player";
    public static final String TAG_PLAYERUUID = "UUID";
    public static final String TAG_PLAYERNAME = "Name";
    
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
        for (Move move : this.moves) {
            NBTTagCompound moveTag = new NBTTagCompound();
            move.writeToNBT(moveTag);
            listTag.appendTag(moveTag);
        }
    }
    
    public void readMovesFromNBT(NBTTagList listTag) {
        this.resetGame();
        for (int i = 0; i < listTag.tagCount(); i++) {
            NBTTagCompound tag = listTag.getCompoundTagAt(i);
            Move move = new Move();
            move.readFromNBT(tag, i % 2 == 0);
            if (move.move == null || this.game.move(move.move) == null) {
                return;
            }
            this.moves.add(move);
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
        this.moves.clear();
    }
    
    public boolean move(EntityPlayer player, ChessGame.Move move) {
        move = this.game.move(move);
        if (move == null) {
            return false;
        }
        this.moves.add(new Move(move, player.getUniqueID(), player.getCommandSenderName()));
        return true;
    }
    
    public ChessGame getGame() {
        return this.game;
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
    
    protected static class Move {
        public ChessGame.Move move;
        public UUID playerUUID;
        public String playerName;
        
        public Move() {}
        
        public Move(ChessGame.Move move, UUID playerUUID, String playerName) {
            this.move = move;
            this.playerUUID = playerUUID;
            this.playerName = playerName;
        }
        
        public void writeToNBT(NBTTagCompound nbtTag) {
            nbtTag.setString(TAG_MOVE, this.move.toString(false));
            NBTTagCompound playerTag = new NBTTagCompound();
            if (this.playerUUID != null) {
                playerTag.setString(TAG_PLAYERUUID, this.playerUUID.toString());
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
                }
                if (playerTag.hasKey(TAG_PLAYERNAME, 8)) {
                    this.playerName = playerTag.getString(TAG_PLAYERNAME);
                }
            }
        }
    }
}
