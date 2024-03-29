package com.vanym.paniclecraft.client.renderer.tileentity;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.block.BlockChessDesk;
import com.vanym.paniclecraft.client.renderer.model.ModelChessBishop;
import com.vanym.paniclecraft.client.renderer.model.ModelChessDesk;
import com.vanym.paniclecraft.client.renderer.model.ModelChessKing;
import com.vanym.paniclecraft.client.renderer.model.ModelChessKnight;
import com.vanym.paniclecraft.client.renderer.model.ModelChessPawn;
import com.vanym.paniclecraft.client.renderer.model.ModelChessQueen;
import com.vanym.paniclecraft.client.renderer.model.ModelChessRook;
import com.vanym.paniclecraft.core.component.deskgame.ChessGame;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityChessDeskRenderer extends TileEntityRenderer<TileEntityChessDesk> {
    
    protected static final ResourceLocation TEXTURE =
            new ResourceLocation(DEF.MOD_ID, "textures/entity/chess_desk.png");
    protected static final ResourceLocation TEXTURE_W =
            new ResourceLocation(DEF.MOD_ID, "textures/entity/chess_w.png");
    protected static final ResourceLocation TEXTURE_B =
            new ResourceLocation(DEF.MOD_ID, "textures/entity/chess_b.png");
    
    protected ModelChessPawn pawn = new ModelChessPawn();
    protected ModelChessBishop bishop = new ModelChessBishop();
    protected ModelChessKnight knight = new ModelChessKnight();
    protected ModelChessRook rook = new ModelChessRook();
    protected ModelChessQueen queen = new ModelChessQueen();
    protected ModelChessKing king = new ModelChessKing();
    protected ModelChessDesk desk = new ModelChessDesk();
    
    @Override
    public void render(
            TileEntityChessDesk tileCD,
            double x,
            double y,
            double z,
            float partialTicks,
            int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
        GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translatef(0.0F, 0.5F, 0.0F);
        if (tileCD.hasWorld()) {
            GlStateManager.rotatef(tileCD.getBlockState()
                                         .get(BlockChessDesk.FACING)
                                         .getHorizontalAngle(),
                                   0.0F, 1.0F, 0.0F);
        }
        float scale = 0.0625F;
        if (destroyStage >= 0) {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(4.0F, 2.0F, 1.0F);
            GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        } else {
            this.bindTexture(TEXTURE);
        }
        this.desk.render(scale);
        GlStateManager.translatef(0.5F - 0.0625F, -0.25F + 0.05F, 0.5F - 0.0625F);
        float scalePiece = 0.25F;
        ChessGame game = tileCD.getGame();
        int size = destroyStage < 0 ? game.size() : 0;
        for (int i = 0; i < size; ++i) {
            int px = i % 8;
            int py = i / 8;
            byte piece = game.getPiece(i);
            byte pieceA = (byte)Math.abs(piece);
            GlStateManager.pushMatrix();
            GlStateManager.translatef(-0.125F * px, 0.0F, -0.125F * py);
            if (piece > 0) {
                this.bindTexture(TEXTURE_W);
            } else if (piece < 0) {
                GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
                this.bindTexture(TEXTURE_B);
            }
            GlStateManager.scalef(scale, scale, scale);
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
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
        if (destroyStage >= 0) {
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }
    }
}
