package com.vanym.paniclecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
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

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
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
    
    public TileEntityChessDeskRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }
    
    @Override
    public void render(
            TileEntityChessDesk tileCD,
            float partialTicks,
            MatrixStack ms,
            IRenderTypeBuffer buffer,
            int combinedLight,
            int combinedOverlay) {
        ms.pushPose();
        ms.translate(0.5F, 0.5F, 0.5F);
        ms.mulPose(Vector3f.XP.rotationDegrees(180.0F));
        ms.translate(0.0F, 0.5F, 0.0F);
        if (tileCD.hasLevel()) {
            ms.mulPose(Vector3f.YP.rotationDegrees(tileCD.getBlockState()
                                                         .getValue(BlockChessDesk.FACING)
                                                         .toYRot()));
        }
        IVertexBuilder vertexer = buffer.getBuffer(this.desk.renderType(TEXTURE));
        this.desk.renderToBuffer(ms, vertexer, combinedLight, combinedOverlay,
                                 1.0F, 1.0F, 1.0F, 1.0F);
        ms.translate(0.5F - 0.0625F, -0.25F + 0.05F, 0.5F - 0.0625F);
        float scalePiece = 0.25F;
        ChessGame game = tileCD.getGame();
        int size = game.size();
        for (int i = 0; i < size; ++i) {
            int px = i % 8;
            int py = i / 8;
            byte piece = game.getPiece(i);
            byte pieceA = (byte)Math.abs(piece);
            ms.pushPose();
            ms.translate(-0.125F * px, 0.0F, -0.125F * py);
            IVertexBuilder vertexerP;
            if (piece < 0) {
                ms.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                vertexerP = buffer.getBuffer(this.pawn.renderType(TEXTURE_B));
            } else {
                vertexerP = buffer.getBuffer(this.pawn.renderType(TEXTURE_W));
            }
            ms.scale(scalePiece, scalePiece, scalePiece);
            if (pieceA == ChessGame.PAWN) {
                this.pawn.renderToBuffer(ms, vertexerP, combinedLight, combinedOverlay,
                                         1.0F, 1.0F, 1.0F, 1.0F);
            } else if (pieceA == ChessGame.BISHOP) {
                this.bishop.renderToBuffer(ms, vertexerP, combinedLight, combinedOverlay,
                                           1.0F, 1.0F, 1.0F, 1.0F);
            } else if (pieceA == ChessGame.KNIGHT) {
                this.knight.renderToBuffer(ms, vertexerP, combinedLight, combinedOverlay,
                                           1.0F, 1.0F, 1.0F, 1.0F);
            } else if (pieceA == ChessGame.ROOK || pieceA == ChessGame.ROOK_UNMOVED) {
                this.rook.renderToBuffer(ms, vertexerP, combinedLight, combinedOverlay,
                                         1.0F, 1.0F, 1.0F, 1.0F);
            } else if (pieceA == ChessGame.QUEEN) {
                this.queen.renderToBuffer(ms, vertexerP, combinedLight, combinedOverlay,
                                          1.0F, 1.0F, 1.0F, 1.0F);
            } else if (pieceA == ChessGame.KING || pieceA == ChessGame.KING_UNMOVED) {
                this.king.renderToBuffer(ms, vertexerP, combinedLight, combinedOverlay,
                                         1.0F, 1.0F, 1.0F, 1.0F);
            }
            ms.popPose();
        }
        ms.popPose();
    }
}
