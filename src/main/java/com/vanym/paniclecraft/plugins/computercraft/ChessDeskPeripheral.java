package com.vanym.paniclecraft.plugins.computercraft;

import java.util.Map;

import com.vanym.paniclecraft.core.component.deskgame.ChessGame;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public class ChessDeskPeripheral extends PeripheralBase {
    
    protected final TileEntityChessDesk desk;
    
    public ChessDeskPeripheral(TileEntityChessDesk desk) {
        this.desk = desk;
    }
    
    @Override
    public String getType() {
        return "chess";
    }
    
    @PeripheralMethod(1)
    protected Map<Integer, String> getMoves() {
        return toLuaArray(this.desk.moves.stream().map(m->m.move.toString())::iterator);
    }
    
    @PeripheralMethod(11)
    protected boolean move(String move) {
        try {
            ChessGame.Move gameMove = new ChessGame.Move(move, this.desk.getGame().isWhiteTurn());
            return this.desk.move(gameMove, "ComputerCraft", null);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    @Override
    public void attach(IComputerAccess computer) {
        this.desk.listeners.add(new ComputerEventListener(computer));
    }
    
    @Override
    public void detach(IComputerAccess computer) {
        this.desk.listeners.remove(new ComputerEventListener(computer));
    }
    
    @Override
    public boolean equals(IPeripheral other) {
        if (other instanceof ChessDeskPeripheral) {
            return this.desk.equals(((ChessDeskPeripheral)other).desk);
        } else {
            return false;
        }
    }
}
