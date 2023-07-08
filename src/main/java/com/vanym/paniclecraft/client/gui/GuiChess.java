package com.vanym.paniclecraft.client.gui;

import java.util.Arrays;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.core.component.deskgame.ChessGame;
import com.vanym.paniclecraft.network.message.MessageChessMove;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiChess extends Screen {
    
    protected static final ResourceLocation BUTTONS_TEXTURE =
            new ResourceLocation(DEF.MOD_ID, "textures/guis/chess_buttons.png");
    
    protected final GuiSquareButton[] fieldButtons = new GuiSquareButton[64];
    protected final GuiChooseButton[] chooseButtons = new GuiChooseButton[4];
    
    protected final TileEntityChessDesk tileChess;
    
    protected int select = -1;
    
    public GuiChess(TileEntityChessDesk tile) {
        super(NarratorChatListener.field_216868_a);
        this.tileChess = tile;
    }
    
    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int left = centerX - 80;
        int bottom = centerY + 60;
        for (int i = 0; i < this.fieldButtons.length; ++i) {
            int x = i % 8;
            int y = i / 8;
            this.addButton(this.fieldButtons[i] =
                    new GuiSquareButton(i, left + 20 * x, bottom - 20 * y));
        }
        this.addButton(this.chooseButtons[0] = new GuiChooseButton(ChessGame.KNIGHT));
        this.addButton(this.chooseButtons[1] = new GuiChooseButton(ChessGame.BISHOP));
        this.addButton(this.chooseButtons[2] = new GuiChooseButton(ChessGame.ROOK));
        this.addButton(this.chooseButtons[3] = new GuiChooseButton(ChessGame.QUEEN));
        this.updateButtons();
    }
    
    protected void actionPerformed(Button button) {
        ChessGame game = this.tileChess.getGame();
        if (button instanceof GuiSquareButton) {
            GuiSquareButton buttonSquare = (GuiSquareButton)button;
            int y = buttonSquare.pos / 8;
            if (buttonSquare.pos == this.select) {
                this.select = -1;
                this.updateButtons();
            } else if (this.select == -1 || game.isCurrentSide(buttonSquare.pos)) {
                this.select = buttonSquare.pos;
                this.updateButtons();
            } else {
                byte fromP = game.getPiece(this.select);
                byte fromA = (byte)Math.abs(fromP);
                boolean fromW = fromP > 0;
                if (fromA == ChessGame.PAWN && (y == 0 || y == 7)) {
                    this.addChoose(fromW, buttonSquare.pos);
                } else {
                    ChessGame.Move move = new ChessGame.Move(this.select, buttonSquare.pos);
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
        Core.instance.network.sendToServer(new MessageChessMove(this.tileChess.getPos(), move));
    }
    
    protected void updateButtons() {
        ChessGame game = this.tileChess.getGame();
        for (int i = 0; i < this.fieldButtons.length; ++i) {
            GuiSquareButton button = this.fieldButtons[i];
            button.active = (this.select == i)
                || game.isCurrentSide(button.pos)
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
            chooseButton.y = top;
            chooseButton.x = left + i * 20;
        }
    }
    
    @Override
    public void render(int mouseX, int mouseY, float renderPartialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, renderPartialTicks);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    @Override
    public boolean charTyped(char character, int key) {
        if (character == 3 /* Ctrl+c */) {
            this.movesCopy();
            return true;
        }
        if (key == this.minecraft.gameSettings.keyBindInventory.getKey().getKeyCode()) {
            key = 1;
        }
        return super.charTyped(character, key);
    }
    
    @Override
    public void tick() {
        if ((this.tileChess.getWorld()
                           .getTileEntity(this.tileChess.getPos()) == null)
            || this.minecraft.player.getDistanceSq(new Vec3d(
                    this.tileChess.getPos()).add(0.5D, 0.5D, 0.5D)) > 64.0D) {
            this.charTyped((char)0, 1); // close
        }
    }
    
    protected void movesCopy() {
        String moves = this.getMovesString();
        if (!moves.isEmpty()) {
            this.minecraft.keyboardListener.setClipboardString(moves);
            ITextComponent message = new TranslationTextComponent("chess.export.copy.success");
            this.minecraft.ingameGUI.getChatGUI().printChatMessage(message);
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
        
        public final int pos;
        
        public GuiSquareButton(int pos, int x, int y) {
            super(x, y);
            this.pos = pos;
            this.active = false;
        }
        
        @Override
        protected byte getPiece() {
            return GuiChess.this.tileChess.getGame().getPiece(this.pos);
        }
        
        @Override
        protected int getYImage(boolean hovered) {
            if (this.active && hovered) {
                return 1;
            }
            ChessGame game = GuiChess.this.tileChess.getGame();
            boolean lastFrom = (this.pos == game.lastFrom());
            boolean lastTo = (this.pos == game.lastTo());
            boolean selected = (this.pos == GuiChess.this.select);
            if (selected) {
                return 3;
            } else if (this.active) {
                if (lastFrom) {
                    return 6;
                } else if (lastTo) {
                    return 7;
                } else if (GuiChess.this.select != -1 && !game.isCurrentSide(this.pos)) {
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
        
        public GuiChooseButton(byte piece) {
            super(0, 0);
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
    
    protected abstract class GuiChessButton extends Button {
        
        public GuiChessButton(int x, int y) {
            super(x, y, 20, 20, "", GuiChess.this::actionPerformed);
        }
        
        protected abstract byte getPiece();
        
        @Override
        protected int getYImage(boolean hovered) {
            if (!this.active) {
                return 0;
            } else if (hovered) {
                return 1;
            }
            return 2;
        }
        
        @Override
        public void renderButton(int mouseX, int mouseY, float partialTicks) {
            if (!this.visible) {
                return;
            }
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.getTextureManager().bindTexture(BUTTONS_TEXTURE);
            int mode = this.getYImage(this.isHovered());
            this.blit(this.x, this.y,
                      mode * this.width, 0,
                      this.width, this.height);
            byte piece = this.getPiece();
            byte pieceA = (byte)Math.abs(piece);
            if (piece != ChessGame.EMPTY && pieceA <= ChessGame.KING_UNMOVED) {
                boolean pieceW = piece > 0;
                if (pieceA > 6) {
                    pieceA -= 3;
                }
                this.blit(this.x, this.y, pieceA * this.width,
                          (pieceW ? this.height : this.height * 2),
                          this.width, this.height);
            }
        }
    }
}
