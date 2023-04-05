package com.vanym.paniclecraft.client.renderer.tileentity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.renderer.model.ModelChessBishop;
import com.vanym.paniclecraft.client.renderer.model.ModelChessDesk;
import com.vanym.paniclecraft.client.renderer.model.ModelChessKing;
import com.vanym.paniclecraft.client.renderer.model.ModelChessKnight;
import com.vanym.paniclecraft.client.renderer.model.ModelChessPawn;
import com.vanym.paniclecraft.client.renderer.model.ModelChessQueen;
import com.vanym.paniclecraft.client.renderer.model.ModelChessRook;
import com.vanym.paniclecraft.core.component.deskgame.ChessGame;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class TileEntityChessDeskRenderer extends TileEntitySpecialRenderer {
    
    protected static final ResourceLocation TEXTURE =
            new ResourceLocation(DEF.MOD_ID, "textures/models/chess_desk.png");
    protected static final ResourceLocation TEXTURE_W =
            new ResourceLocation(DEF.MOD_ID, "textures/models/chess_w.png");
    protected static final ResourceLocation TEXTURE_B =
            new ResourceLocation(DEF.MOD_ID, "textures/models/chess_b.png");
    
    protected ModelChessPawn pawn = new ModelChessPawn();
    protected ModelChessBishop bishop = new ModelChessBishop();
    protected ModelChessKnight knight = new ModelChessKnight();
    protected ModelChessRook rook = new ModelChessRook();
    protected ModelChessQueen queen = new ModelChessQueen();
    protected ModelChessKing king = new ModelChessKing();
    protected ModelChessDesk desk = new ModelChessDesk();
    
    public void renderTileEntityAt(
            TileEntityChessDesk tileCD,
            double x,
            double y,
            double z,
            float f) {
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        GL11.glTranslatef(0.0F, 0.5F, 0.0F);
        GL11.glRotatef(90.0F * tileCD.getBlockMetadata(), 0.0F, 1.0F, 0.0F);
        float scale = 0.0625F;
        this.bindTexture(TEXTURE);
        this.desk.render(scale);
        GL11.glTranslatef(0.5F - 0.0625F, -0.25F + 0.05F, 0.5F - 0.0625F);
        float scalePiece = 0.25F;
        ChessGame game = tileCD.getGame();
        for (int i = 0; i < game.size(); ++i) {
            int px = i % 8;
            int py = i / 8;
            byte piece = game.getPiece(i);
            byte pieceA = (byte)Math.abs(piece);
            GL11.glPushMatrix();
            GL11.glTranslatef(-0.125F * px, 0.0F, -0.125F * py);
            if (piece > 0) {
                this.bindTexture(TEXTURE_W);
            } else if (piece < 0) {
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                this.bindTexture(TEXTURE_B);
            }
            GL11.glScalef(scale, scale, scale);
            if (pieceA == ChessGame.PAWN) {
                this.pawn.render(scalePiece);
            } else if (pieceA == ChessGame.BISHOP) {
                this.bishop.render(scalePiece);
            } else if (pieceA == ChessGame.KNIGHT) {
                this.knight.render(scalePiece);
            } else if (pieceA == ChessGame.ROOK || pieceA == ChessGame.ROOK_UNMOVED) {
                this.rook.render(scalePiece);
            } else if (pieceA == ChessGame.QUEEN) {
                this.queen.render(scalePiece);
            } else if (pieceA == ChessGame.KING || pieceA == ChessGame.KING_UNMOVED) {
                this.king.render(scalePiece);
            }
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
    }
    
    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f) {
        this.renderTileEntityAt((TileEntityChessDesk)tile, x, y, z, f);
    }
}
