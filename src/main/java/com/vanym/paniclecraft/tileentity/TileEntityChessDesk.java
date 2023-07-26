package com.vanym.paniclecraft.tileentity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.gui.GuiChess;
import com.vanym.paniclecraft.core.component.deskgame.ChessGame;
import com.vanym.paniclecraft.utils.GeometryUtils;
import com.vanym.paniclecraft.utils.SideUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public class TileEntityChessDesk extends TileEntityBase {
    
    public static final String IN_MOD_ID = "chess_desk";
    public static final ResourceLocation ID = new ResourceLocation(DEF.MOD_ID, IN_MOD_ID);
    
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
    
    public TileEntityChessDesk() {
        super(Core.instance.deskgame.tileEntityChessDesk);
    }
    
    @Override
    public CompoundNBT write(CompoundNBT nbtTag) {
        super.write(nbtTag);
        ListNBT list = new ListNBT();
        this.writeMoves(list);
        nbtTag.put(TAG_MOVES, list);
        return nbtTag;
    }
    
    @Override
    public void read(CompoundNBT nbtTag) {
        super.read(nbtTag);
        ListNBT list = nbtTag.getList(TAG_MOVES, 10);
        this.readMoves(list);
    }
    
    public void writeMoves(ListNBT listTag) {
        SideUtils.runSync(this.world != null && !this.world.isRemote,
                          this, ()->this.writeMovesAsync(listTag));
    }
    
    protected void writeMovesAsync(ListNBT listTag) {
        this.imoves.stream()
                   .map(Move::serializeNBT)
                   .forEachOrdered(listTag::add);
    }
    
    public void readMoves(ListNBT listTag) {
        SideUtils.runSync(this.world != null && !this.world.isRemote,
                          this, ()->this.readMovesAsync(listTag));
    }
    
    protected void readMovesAsync(ListNBT listTag) {
        this.game = new ChessGame();
        this.imoves.clear();
        for (int i = 0; i < listTag.size(); i++) {
            CompoundNBT tag = listTag.getCompound(i);
            Move wrap = new Move();
            wrap.deserializeNBT(tag, i % 2 == 0);
            ChessGame.Move move;
            if (wrap.move == null || (move = this.game.move(wrap.move)) == null) {
                return;
            }
            this.imoves.add(new Move(wrap, move));
        }
    }
    
    @Override
    public void onDataPacket(NetworkManager manager, SUpdateTileEntityPacket packet) {
        super.onDataPacket(manager, packet);
        if (EffectiveSide.get().isClient()) {
            this.updateGui();
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    protected void updateGui() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.currentScreen instanceof GuiChess) {
            GuiChess guiChess = (GuiChess)mc.currentScreen;
            guiChess.updateGui(this);
        }
    }
    
    public void resetGame() {
        this.game = new ChessGame();
        this.imoves.clear();
        this.sendEvent("chess_reset");
    }
    
    public boolean move(ChessGame.Move move, PlayerEntity player) {
        return this.move(move, player.getName().getString(), player.getUniqueID());
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
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return GeometryUtils.setMaxY(GeometryUtils.getFullBlockBox(), 0.5D).offset(this.pos);
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 16384.0D;
    }
    
    public static class Move implements INBTSerializable<CompoundNBT> {
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
        
        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT nbtTag = new CompoundNBT();
            nbtTag.putString(TAG_MOVE, this.move.toString(false));
            CompoundNBT playerTag = new CompoundNBT();
            if (this.playerUUID != null) {
                playerTag.putLong(TAG_PLAYERUUIDMOST, this.playerUUID.getMostSignificantBits());
                playerTag.putLong(TAG_PLAYERUUIDLEAST, this.playerUUID.getLeastSignificantBits());
            }
            if (this.playerName != null) {
                playerTag.putString(TAG_PLAYERNAME, this.playerName);
            }
            nbtTag.put(TAG_PLAYER, playerTag);
            return nbtTag;
        }
        
        @Override
        public void deserializeNBT(CompoundNBT nbtTag) {
            this.deserializeNBT(nbtTag, null);
        }
        
        public void deserializeNBT(CompoundNBT nbtTag, Boolean white) {
            try {
                String str = nbtTag.getString(TAG_MOVE);
                this.move = new ChessGame.Move(str, white);
            } catch (IllegalArgumentException e) {
                return;
            }
            if (nbtTag.contains(TAG_PLAYER, 10)) {
                CompoundNBT playerTag = nbtTag.getCompound(TAG_PLAYER);
                if (playerTag.contains(TAG_PLAYERUUID, 8)) {
                    this.playerUUID = UUID.fromString(playerTag.getString(TAG_PLAYERUUID));
                } else if (playerTag.contains(TAG_PLAYERUUIDMOST, 4)
                    && playerTag.contains(TAG_PLAYERUUIDLEAST, 4)) {
                    this.playerUUID = new UUID(
                            playerTag.getLong(TAG_PLAYERUUIDMOST),
                            playerTag.getLong(TAG_PLAYERUUIDLEAST));
                }
                if (playerTag.contains(TAG_PLAYERNAME, 8)) {
                    this.playerName = playerTag.getString(TAG_PLAYERNAME);
                }
            }
        }
    }
}
