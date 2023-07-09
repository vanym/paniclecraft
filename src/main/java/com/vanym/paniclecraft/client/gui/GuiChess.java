package com.vanym.paniclecraft.client.gui;

import java.util.Arrays;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.core.component.deskgame.ChessGame;
import com.vanym.paniclecraft.network.message.MessageChessMove;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiChess extends GuiScreen {
    
    protected static final ResourceLocation BUTTONS_TEXTURE =
            new ResourceLocation(DEF.MOD_ID, "textures/gui/chess_buttons.png");
    
    protected final GuiSquareButton[] fieldButtons = new GuiSquareButton[64];
    protected final GuiChooseButton[] chooseButtons = new GuiChooseButton[4];
    
    protected final TileEntityChessDesk tileChess;
    
    protected int select = -1;
    
    public GuiChess(TileEntityChessDesk tile) {
        this.tileChess = tile;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int left = centerX - 80;
        int bottom = centerY + 60;
        this.buttonList.clear();
        for (int i = 0; i < this.fieldButtons.length; ++i) {
            int x = i % 8;
            int y = i / 8;
            this.buttonList.add(this.fieldButtons[i] =
                    new GuiSquareButton(i, left + 20 * x, bottom - 20 * y));
        }
        this.buttonList.add(this.chooseButtons[0] = new GuiChooseButton(70, ChessGame.KNIGHT));
        this.buttonList.add(this.chooseButtons[1] = new GuiChooseButton(71, ChessGame.BISHOP));
        this.buttonList.add(this.chooseButtons[2] = new GuiChooseButton(72, ChessGame.ROOK));
        this.buttonList.add(this.chooseButtons[3] = new GuiChooseButton(73, ChessGame.QUEEN));
        this.updateButtons();
    }
    
    @Override
    public void actionPerformed(GuiButton button) {
        ChessGame game = this.tileChess.getGame();
        if (button instanceof GuiSquareButton) {
            int y = button.id / 8;
            if (button.id == this.select) {
                this.select = -1;
                this.updateButtons();
            } else if (this.select == -1 || game.isCurrentSide(button.id)) {
                this.select = button.id;
                this.updateButtons();
            } else {
                byte fromP = game.getPiece(this.select);
                byte fromA = (byte)Math.abs(fromP);
                boolean fromW = fromP > 0;
                if (fromA == ChessGame.PAWN && (y == 0 || y == 7)) {
                    this.addChoose(fromW, button.id);
                } else {
                    ChessGame.Move move = new ChessGame.Move(this.select, button.id);
                    this.sendMove(move);
                }
            }
        } else if (button instanceof GuiChooseButton) {
            GuiChooseButton buttonChoose = (GuiChooseButton)button;
            byte fromP = game.getPiece(this.select);
            ChessGame.Move move = new ChessGame.Move(
                    this.select,
                    buttonChoose.chooseSelect,
                    fromP,
                    buttonChoose.getPiece());
            this.sendMove(move);
        }
    }
    
    protected void sendMove(ChessGame.Move move) {
        Core.instance.network.sendToServer(new MessageChessMove(
                this.tileChess.xCoord,
                this.tileChess.yCoord,
                this.tileChess.zCoord,
                move));
    }
    
    protected void updateButtons() {
        ChessGame game = this.tileChess.getGame();
        for (int i = 0; i < this.fieldButtons.length; ++i) {
            GuiSquareButton button = this.fieldButtons[i];
            button.enabled = (this.select == i)
                || game.isCurrentSide(button.id)
                || (this.select != -1 && game.canMove(this.select, i));
        }
        Arrays.stream(this.chooseButtons).forEach(b->b.visible = false);
    }
    
    public void updateGui(TileEntityChessDesk tile) {
        if (this.tileChess == tile) {
            this.select = -1;
            this.updateButtons();
        }
    }
    
    protected void addChoose(boolean white, int id) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int x = id % 8;
        int left = centerX - 110 + x * 20;
        int top = white ? centerY - 100 : centerY + 80;
        for (int i = 0; i < this.chooseButtons.length; ++i) {
            GuiChooseButton chooseButton = this.chooseButtons[i];
            chooseButton.chooseSelect = id;
            chooseButton.white = white;
            chooseButton.visible = true;
            chooseButton.yPosition = top;
            chooseButton.xPosition = left + i * 20;
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float renderPartialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, renderPartialTicks);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    @Override
    protected void keyTyped(char character, int key) {
        if (character == 3 /* Ctrl+c */) {
            this.movesCopy();
            return;
        }
        if (key == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            key = 1;
        }
        super.keyTyped(character, key);
    }
    
    @Override
    public void updateScreen() {
        if ((this.tileChess.getWorldObj()
                           .getTileEntity(this.tileChess.xCoord, this.tileChess.yCoord,
                                          this.tileChess.zCoord) == null)
            || this.mc.thePlayer.getDistanceSq(this.tileChess.xCoord + 0.5D,
                                               this.tileChess.yCoord + 0.5D,
                                               this.tileChess.zCoord + 0.5D) > 64.0D) {
            this.keyTyped((char)0, 1); // close
        }
    }
    
    protected void movesCopy() {
        String moves = this.getMovesString();
        if (!moves.isEmpty()) {
            GuiScreen.setClipboardString(moves);
            IChatComponent message = new ChatComponentTranslation("chess.export.copy.success");
            this.mc.ingameGUI.getChatGUI().printChatMessage(message);
        }
    }
    
    protected String getMovesString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.tileChess.moves.size(); ++i) {
            TileEntityChessDesk.Move m = this.tileChess.moves.get(i);
            if (i % 2 == 0) {
                int line = i / 2;
                if (line != 0) {
                    sb.append(System.lineSeparator());
                }
                sb.append(line + 1);
                sb.append('.');
            }
            sb.append(' ');
            sb.append(m.move.toString());
        }
        return sb.toString();
    }
    
    protected class GuiSquareButton extends GuiChessButton {
        
        public GuiSquareButton(int id, int x, int y) {
            super(id, x, y);
            this.enabled = false;
        }
        
        @Override
        protected byte getPiece() {
            return GuiChess.this.tileChess.getGame().getPiece(this.id);
        }
        
        @Override
        public int getHoverState(boolean hovered) {
            if (this.enabled && hovered) {
                return 1;
            }
            ChessGame game = GuiChess.this.tileChess.getGame();
            boolean lastFrom = (this.id == game.lastFrom());
            boolean lastTo = (this.id == game.lastTo());
            boolean selected = (this.id == GuiChess.this.select);
            if (selected) {
                return 3;
            } else if (this.enabled) {
                if (lastFrom) {
                    return 6;
                } else if (lastTo) {
                    return 7;
                } else if (GuiChess.this.select != -1 && !game.isCurrentSide(this.id)) {
                    return 2;
                } else {
                    return 0;
                }
            } else {
                if (lastFrom) {
                    return 4;
                } else if (lastTo) {
                    return 5;
                } else {
                    return 0;
                }
            }
        }
    }
    
    protected class GuiChooseButton extends GuiChessButton {
        
        protected final byte piece;
        
        protected boolean white;
        protected int chooseSelect = -1;
        
        public GuiChooseButton(int id, byte piece) {
            super(id, 0, 0);
            this.piece = piece;
            this.visible = false;
        }
        
        @Override
        protected byte getPiece() {
            if (this.white) {
                return this.piece;
            } else {
                return (byte)-this.piece;
            }
        }
    }
    
    protected static abstract class GuiChessButton extends GuiButton {
        
        public GuiChessButton(int id, int x, int y) {
            super(id, x, y, 20, 20, "");
        }
        
        protected abstract byte getPiece();
        
        @Override
        public int getHoverState(boolean hovered) {
            if (!this.enabled) {
                return 0;
            } else if (hovered) {
                return 1;
            }
            return 2;
        }
        
        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (!this.visible) {
                return;
            }
            mc.renderEngine.bindTexture(BUTTONS_TEXTURE);
            this.field_146123_n = mouseX >= this.xPosition
                && mouseY >= this.yPosition
                && mouseX < this.xPosition + this.width
                && mouseY < this.yPosition + this.height;
            int mode = this.getHoverState(this.field_146123_n);
            this.drawTexturedModalRect(this.xPosition, this.yPosition,
                                       mode * this.width, 0,
                                       this.width, this.height);
            byte piece = this.getPiece();
            byte pieceA = (byte)Math.abs(piece);
            if (piece != ChessGame.EMPTY && pieceA <= ChessGame.KING_UNMOVED) {
                boolean pieceW = piece > 0;
                if (pieceA > 6) {
                    pieceA -= 3;
                }
                this.drawTexturedModalRect(this.xPosition, this.yPosition, pieceA * this.width,
                                           (pieceW ? this.height : this.height * 2),
                                           this.width, this.height);
            }
        }
    }
}
