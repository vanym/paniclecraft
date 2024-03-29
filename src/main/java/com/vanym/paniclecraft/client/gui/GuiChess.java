package com.vanym.paniclecraft.client.gui;

import java.util.Arrays;

import org.lwjgl.glfw.GLFW;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.core.component.deskgame.ChessGame;
import com.vanym.paniclecraft.network.message.MessageChessMove;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiChess extends Screen {
    
    protected static final ResourceLocation BUTTONS_TEXTURE =
            new ResourceLocation(DEF.MOD_ID, "textures/gui/chess_buttons.png");
    
    protected final SquareButton[] fieldButtons = new SquareButton[64];
    protected final ChooseButton[] chooseButtons = new ChooseButton[4];
    
    protected final TileEntityChessDesk chessdesk;
    
    protected int select = -1;
    
    public GuiChess(TileEntityChessDesk tile) {
        super(NarratorChatListener.field_216868_a);
        this.chessdesk = tile;
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
            this.fieldButtons[i] = new SquareButton(i, left + 20 * x, bottom - 20 * y);
        }
        this.chooseButtons[0] = new ChooseButton(ChessGame.KNIGHT);
        this.chooseButtons[1] = new ChooseButton(ChessGame.BISHOP);
        this.chooseButtons[2] = new ChooseButton(ChessGame.ROOK);
        this.chooseButtons[3] = new ChooseButton(ChessGame.QUEEN);
        Arrays.stream(this.fieldButtons).forEachOrdered(this::addButton);
        Arrays.stream(this.chooseButtons).forEachOrdered(this::addButton);
        this.updateButtons();
    }
    
    protected void sendMove(ChessGame.Move move) {
        Core.instance.network.sendToServer(new MessageChessMove(this.chessdesk.getPos(), move));
    }
    
    protected void updateButtons() {
        ChessGame game = this.chessdesk.getGame();
        for (int i = 0; i < this.fieldButtons.length; ++i) {
            SquareButton button = this.fieldButtons[i];
            button.active = (this.select == i)
                || game.isCurrentSide(button.pos)
                || (this.select != -1 && game.canMove(this.select, i));
        }
        Arrays.stream(this.chooseButtons).forEach(b->b.visible = false);
    }
    
    public void update(TileEntityChessDesk tile) {
        if (this.chessdesk == tile) {
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
            ChooseButton chooseButton = this.chooseButtons[i];
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
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        InputMappings.Input inputCode = InputMappings.getInputByCode(key, scanCode);
        if (this.minecraft.gameSettings.keyBindInventory.isActiveAndMatches(inputCode)) {
            key = GLFW.GLFW_KEY_ESCAPE;
        }
        if (super.keyPressed(key, scanCode, modifiers)) {
            return true;
        }
        if (Screen.isCopy(key)) {
            this.movesCopy();
            return true;
        }
        return false;
    }
    
    @Override
    public void tick() {
        if ((this.chessdesk.getWorld()
                           .getTileEntity(this.chessdesk.getPos()) == null)
            || this.minecraft.player.getDistanceSq(new Vec3d(
                    this.chessdesk.getPos()).add(0.5D, 0.5D, 0.5D)) > 64.0D) {
            this.onClose();
        }
    }
    
    protected void movesCopy() {
        String moves = this.getMovesString();
        if (!moves.isEmpty()) {
            GuiUtils.setClipboardString(moves);
            ITextComponent message = new TranslationTextComponent(
                    String.format("chat.%s.chess.export.copy.success", DEF.MOD_ID));
            this.minecraft.ingameGUI.getChatGUI().printChatMessage(message);
        }
    }
    
    protected String getMovesString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.chessdesk.moves.size(); ++i) {
            TileEntityChessDesk.Move m = this.chessdesk.moves.get(i);
            if (i % 2 == 0) {
                int line = i / 2;
                if (line != 0) {
                    sb.append(' ');
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
    
    protected class SquareButton extends ChessButton {
        
        public final int pos;
        
        public SquareButton(int pos, int x, int y) {
            super(x, y);
            this.pos = pos;
            this.active = false;
        }
        
        @Override
        protected byte getPiece() {
            return GuiChess.this.chessdesk.getGame().getPiece(this.pos);
        }
        
        @Override
        protected int getYImage(boolean hovered) {
            if (this.active && hovered) {
                return 1;
            }
            ChessGame game = GuiChess.this.chessdesk.getGame();
            int sel = GuiChess.this.select;
            boolean lastFrom = (this.pos == game.lastFrom());
            boolean lastTo = (this.pos == game.lastTo());
            boolean selected = (this.pos == sel);
            if (selected) {
                return 3;
            } else if (this.active) {
                if (lastFrom) {
                    return 6;
                } else if (lastTo) {
                    return 7;
                } else if (sel != -1 && !game.isCurrentSide(this.pos)) {
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
        
        @Override
        public void onPress() {
            ChessGame game = GuiChess.this.chessdesk.getGame();
            int y = this.pos / 8;
            if (this.pos == GuiChess.this.select) {
                GuiChess.this.select = -1;
                GuiChess.this.updateButtons();
            } else if (GuiChess.this.select == -1 || game.isCurrentSide(this.pos)) {
                GuiChess.this.select = this.pos;
                GuiChess.this.updateButtons();
            } else {
                byte fromP = game.getPiece(GuiChess.this.select);
                byte fromA = (byte)Math.abs(fromP);
                boolean fromW = fromP > 0;
                if (fromA == ChessGame.PAWN && (y == 0 || y == 7)) {
                    GuiChess.this.addChoose(fromW, this.pos);
                } else {
                    ChessGame.Move move = new ChessGame.Move(GuiChess.this.select, this.pos);
                    GuiChess.this.sendMove(move);
                }
            }
        }
    }
    
    protected class ChooseButton extends ChessButton {
        
        protected final byte piece;
        
        protected boolean white;
        protected int chooseSelect = -1;
        
        public ChooseButton(byte piece) {
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
        
        @Override
        public void onPress() {
            ChessGame game = GuiChess.this.chessdesk.getGame();
            int sel = GuiChess.this.select;
            byte fromP = game.getPiece(sel);
            ChessGame.Move move =
                    new ChessGame.Move(sel, this.chooseSelect, fromP, this.getPiece());
            GuiChess.this.sendMove(move);
        }
    }
    
    protected static abstract class ChessButton extends AbstractButton {
        
        public ChessButton(int x, int y) {
            super(x, y, 20, 20, "");
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
