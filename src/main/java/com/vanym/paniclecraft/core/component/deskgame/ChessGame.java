package com.vanym.paniclecraft.core.component.deskgame;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ChessGame {
    
    public static final byte EMPTY = 0;
    public static final byte PAWN = (byte)PieceType.PAWN.ordinal();
    public static final byte BISHOP = (byte)PieceType.BISHOP.ordinal();
    public static final byte KNIGHT = (byte)PieceType.KNIGHT.ordinal();
    public static final byte ROOK = (byte)PieceType.ROOK.ordinal();
    public static final byte ROOK_UNMOVED = (byte)(ROOK + 3);
    public static final byte QUEEN = (byte)PieceType.QUEEN.ordinal();
    public static final byte KING = (byte)PieceType.KING.ordinal();
    public static final byte KING_UNMOVED = (byte)(KING + 3);
    
    protected static final byte[] DEFDESK =
            new byte[]{+7, +3, +2, +5, +9, +2, +3, +7,
                       +1, +1, +1, +1, +1, +1, +1, +1,
                       +0, +0, +0, +0, +0, +0, +0, +0,
                       +0, +0, +0, +0, +0, +0, +0, +0,
                       +0, +0, +0, +0, +0, +0, +0, +0,
                       +0, +0, +0, +0, +0, +0, +0, +0,
                       -1, -1, -1, -1, -1, -1, -1, -1,
                       -7, -3, -2, -5, -9, -2, -3, -7};
    
    protected final byte[] desk;
    
    protected int lastFrom;
    protected int lastTo;
    protected boolean isWhiteTurn;
    
    public ChessGame() {
        this.desk = DEFDESK.clone();
        this.lastFrom = -1;
        this.lastTo = -1;
        this.isWhiteTurn = true;
    }
    
    public ChessGame(byte[] desk, byte lastFrom, byte lastTo, boolean isWhiteTurn) {
        this.desk = Arrays.copyOf(desk, DEFDESK.length);
        this.lastFrom = lastFrom;
        this.lastTo = lastTo;
        this.isWhiteTurn = isWhiteTurn;
    }
    
    protected ChessGame(ChessGame game) {
        this.desk = game.desk.clone();
        this.lastFrom = game.lastFrom;
        this.lastTo = game.lastTo;
        this.isWhiteTurn = game.isWhiteTurn;
    }
    
    public int size() {
        return this.desk.length;
    }
    
    public int lastFrom() {
        return this.lastFrom;
    }
    
    public int lastTo() {
        return this.lastTo;
    }
    
    public boolean isWhiteTurn() {
        return this.isWhiteTurn;
    }
    
    public byte getPiece(int x, int y) {
        return this.getPiece(getPos(x, y));
    }
    
    public byte getPiece(int pos) {
        return this.desk[pos];
    }
    
    protected void setPiece(int x, int y, byte piece) {
        this.setPiece(getPos(x, y), piece);
    }
    
    protected void setPiece(int pos, byte piece) {
        this.desk[pos] = piece;
    }
    
    protected boolean isEmpty(int x, int y) {
        return this.isEmpty(getPos(x, y));
    }
    
    protected boolean isEmpty(int pos) {
        return this.desk[pos] == 0;
    }
    
    public boolean canMove(int from, int to) {
        return this.isSide(from, this.isWhiteTurn)
            && this.canMoveCheckless(from, to)
            && !this.isCheckAfterMove(from, to, this.isWhiteTurn);
    }
    
    protected boolean isCheck() {
        return this.isCheck(this.isWhiteTurn);
    }
    
    protected boolean isCheck(boolean forWhite) {
        int kingPos;
        try {
            kingPos = IntStream.range(0, this.desk.length)
                               .filter(i-> {
                                   byte piece = this.getPiece(i);
                                   return forWhite ? (piece == KING || piece == KING_UNMOVED)
                                                   : (piece == -KING || piece == -KING_UNMOVED);
                               })
                               .findAny()
                               .getAsInt();
        } catch (NoSuchElementException e) {
            return false;
        }
        return this.isAttackedBy(kingPos, !forWhite);
    }
    
    protected boolean isCheckAfterMove(int from, int to, boolean forWhite) {
        ChessGame next = new ChessGame(this);
        next.moveCheckless(from, to, (byte)PAWN);
        return next.isCheck(forWhite);
    }
    
    protected boolean isStuck() {
        return !IntStream.range(0, this.desk.length).anyMatch(this::canMove);
    }
    
    protected boolean canMove(int from) {
        return this.isSide(from, this.isWhiteTurn)
            && IntStream.range(0, this.desk.length).anyMatch(i->this.canMove(from, i));
    }
    
    protected boolean isAttackedBy(int x, int y, boolean white) {
        return this.isAttackedBy(getPos(x, y), white);
    }
    
    protected boolean isAttackedBy(int to, boolean white) {
        return IntStream.range(0, this.desk.length)
                        .filter(i->this.isSide(i, white))
                        .anyMatch(i->this.canMoveCheckless(i, to));
    }
    
    protected boolean canMoveCheckless(int from, int to) {
        byte fromP = this.getPiece(from);
        boolean fromW = fromP > 0;
        byte fromA = (byte)Math.abs(fromP);
        int fromD = fromW ? 1 : -1;
        int fromS = fromW ? 0 : 7;
        int fromX = getX(from);
        int fromY = getY(from);
        int toX = getX(to);
        int toY = getY(to);
        int offsetX = toX - fromX;
        int offsetY = toY - fromY;
        int signumX = Integer.signum(offsetX);
        int signumY = Integer.signum(offsetY);
        if (fromP == 0 || from == to) {
            return false;
        }
        if (fromA == PAWN) {
            if (toX == fromX && toY == fromY + fromD && this.isEmpty(toX, toY)) {
                return true;
            }
            if (toX == fromX && fromY == fromS + fromD
                && toY == fromY + fromD * 2
                && this.isEmpty(toX, fromY + fromD)
                && this.isEmpty(toX, fromY + fromD * 2)) {
                return true;
            }
            if (toY == fromY + fromD && Math.abs(offsetX) == 1) {
                if (this.isSide(toX, toY, !fromW)) {
                    return true;
                } else if (this.getPiece(toX, toY - fromD) == -PAWN * fromD
                    && this.lastFrom == getPos(toX, toY + fromD)
                    && this.lastTo == getPos(toX, toY - fromD)) {
                    return true;
                }
            }
        }
        if (((fromA == BISHOP || fromA == QUEEN) && Math.abs(offsetX) == Math.abs(offsetY))
            || ((fromA == ROOK || fromA == ROOK_UNMOVED || fromA == QUEEN)
                && Math.abs(signumX) != Math.abs(signumY))) {
            int x = fromX + signumX, y = fromY + signumY;
            for (; this.isEmpty(x, y); x += signumX, y += signumY) {
                if (x == toX && y == toY) {
                    return true;
                }
            }
            if (x == toX && y == toY && this.isSide(x, y, !fromW)) {
                return true;
            }
        }
        if (fromA == KNIGHT
            && Math.abs(offsetX) * Math.abs(offsetY) == 2
            && !this.isSide(toX, toY, fromW)) {
            return true;
        }
        if ((fromA == KING || fromA == KING_UNMOVED)
            && Math.abs(offsetX) <= 1
            && Math.abs(offsetY) <= 1
            && !this.isSide(toX, toY, fromW)) {
            return true;
        }
        if (fromA == KING_UNMOVED
            && toY == fromS
            && Math.abs(offsetX) == 2
            && this.getPiece(signumX > 0 ? 7 : 0, fromS) == ROOK_UNMOVED * fromD
            && this.isEmpty(toX, toY)
            && this.isEmpty(toX - signumX, toY)
            && (signumX > 0 || this.isEmpty(toX + signumX, toY))
            && !this.isAttackedBy(toX - signumX, toY, !fromW)
            && !this.isAttackedBy(fromX, fromY, !fromW)) {
            return true;
        }
        return false;
    }
    
    protected Move moveCheckless(int from, int to, byte promotion) {
        byte fromP = this.getPiece(from);
        boolean fromW = fromP > 0;
        byte fromA = (byte)Math.abs(fromP);
        int fromD = fromW ? 1 : -1;
        int fromS = fromW ? 0 : 7;
        int fromX = getX(from);
        int fromY = getY(from);
        int toX = getX(to);
        int toY = getY(to);
        int offsetX = toX - fromX;
        int offsetY = toY - fromY;
        boolean kill = false;
        boolean promo = false;
        byte piece = (byte)((fromA > 6 ? fromA - 3 : fromA) * fromD);
        if (fromA == KING_UNMOVED) {
            if (to == getPos(6, fromS)) {
                this.setPiece(5, fromS, (byte)(ROOK * fromD));
                this.setPiece(7, fromS, EMPTY);
            } else if (to == getPos(2, fromS)) {
                this.setPiece(3, fromS, (byte)(ROOK * fromD));
                this.setPiece(0, fromS, EMPTY);
            }
        } else if (fromA == PAWN) {
            if (toY == fromS + fromD * 7) {
                promo = true;
                piece = (byte)(Math.abs(promotion) * fromD);
            }
            if (offsetY == fromD
                && Math.abs(offsetX) == 1
                && this.isEmpty(toX, toY)) {
                this.setPiece(fromX + offsetX, fromY, EMPTY);
                kill = true;
            }
        }
        kill = kill || this.isSide(to, !fromW);
        this.setPiece(to, piece);
        this.setPiece(from, EMPTY);
        this.lastFrom = from;
        this.lastTo = to;
        this.isWhiteTurn = !this.isWhiteTurn;
        return new Move(from, to, fromP, kill, promo ? piece : 0);
    }
    
    public Move move(int from, int to, byte promotion) {
        byte fromP = this.getPiece(from);
        int toY = getY(to);
        byte promotionA = (byte)Math.abs(promotion);
        if (((fromP == PAWN && toY == 7)
            || (fromP == -PAWN && toY == 0))
            && promotionA != KNIGHT
            && promotionA != BISHOP
            && promotionA != ROOK
            && promotionA != QUEEN) {
            return null;
        }
        if (!this.canMove(from, to)) {
            return null;
        }
        Move move = this.moveCheckless(from, to, promotion);
        boolean check = false, mate = false;
        if (this.isCheck()) {
            check = true;
            if (this.isStuck()) {
                mate = true;
            }
        }
        return new Move(move, check, mate);
    }
    
    public Move move(Move move) {
        return this.move(move.from, move.to, move.promotion);
    }
    
    protected boolean isSide(int x, int y, boolean white) {
        return this.isSide(getPos(x, y), white);
    }
    
    protected boolean isSide(int pos, boolean white) {
        byte t = this.getPiece(pos);
        return white ? t > 0 : t < 0;
    }
    
    public boolean isCurrentSide(int pos) {
        return this.isSide(pos, this.isWhiteTurn);
    }
    
    protected static int getPos(int x, int y) {
        return y * 8 + x;
    }
    
    protected static int getX(int pos) {
        return pos % 8;
    }
    
    protected static int getY(int pos) {
        return pos / 8;
    }
    
    protected static int parseSquare(String square) {
        char xc = Character.toLowerCase(square.charAt(0));
        char yc = square.charAt(1);
        int x = xc - 'a';
        int y = yc - '1';
        return getPos(x, y);
    }
    
    protected static String squareName(int pos) {
        return squareName(getX(pos), getY(pos));
    }
    
    protected static String squareName(int x, int y) {
        StringBuilder sb = new StringBuilder();
        sb.append((char)('a' + x));
        sb.append((char)('1' + y));
        return sb.toString();
    }
    
    protected static enum PieceType {
        NONE,
        PAWN("", "\u2659", "\u265F", "\u1FA05"),
        BISHOP("B", "С", "\u2657", "\u265D", "\u1FA03"),
        KNIGHT("N", "К", "\u2658", "\u265E", "\u1FA04"),
        ROOK("R", "Л", "\u2656", "\u265C", "\u1FA02"),
        QUEEN("Q", "Ф", "\u2655", "\u265B", "\u1FA01"),
        KING("K", "Кр", "\u2654", "\u265A", "\u1FA00");
        
        public List<String> names;
        
        PieceType(String... names) {
            this.names = Arrays.asList(names);
        }
        
        @Override
        public String toString() {
            if (this.names.isEmpty()) {
                return "";
            }
            return this.names.get(0);
        }
        
        public String getRegex() {
            StringBuilder sb = new StringBuilder();
            sb.append("(?:");
            sb.append(String.join("|", this.names));
            sb.append(")");
            return sb.toString();
        }
        
        public static PieceType getPieceType(byte type) {
            type = (byte)Math.abs(type);
            if (type > 6) {
                type -= 3;
            }
            return values()[type % 7];
        }
        
        public static PieceType getPieceType(String type) {
            return Arrays.stream(values())
                         .sorted(Comparator.reverseOrder())
                         .filter(p->p.names.stream().anyMatch(type::equals))
                         .findFirst()
                         .get();
        }
    }
    
    public static class Move {
        
        protected static final Pattern PATTERN =
                Pattern.compile(String.format("^(%s)([a-hA-H][1-8])([-xX]?)([a-hA-H][1-8])[/=]?(%s)?([+#])?$",
                                              String.join("|",
                                                          Stream.of(PieceType.PAWN,
                                                                    PieceType.BISHOP,
                                                                    PieceType.KNIGHT,
                                                                    PieceType.ROOK,
                                                                    PieceType.QUEEN,
                                                                    PieceType.KING)
                                                                .map(PieceType::getRegex)
                                                                .collect(Collectors.toList())),
                                              String.join("|",
                                                          Stream.of(PieceType.BISHOP,
                                                                    PieceType.KNIGHT,
                                                                    PieceType.ROOK,
                                                                    PieceType.QUEEN)
                                                                .map(PieceType::getRegex)
                                                                .collect(Collectors.toList()))));
        protected static final Pattern CASTLING = Pattern.compile("^[0oO]-[0oO](-[0oO])?$");
        
        public final int from;
        public final int to;
        public final byte type;
        public final boolean kill;
        public final byte promotion;
        public final boolean check;
        public final boolean mate;
        
        public Move(String move) throws IllegalArgumentException {
            this(move, null);
        }
        
        public Move(String move, Boolean white) throws IllegalArgumentException {
            Matcher m = PATTERN.matcher(move);
            Matcher castl;
            boolean check = false, mate = false;
            if (m.matches()) {
                PieceType t = PieceType.getPieceType(m.group(1));
                this.type = (byte)t.ordinal();
                this.from = parseSquare(m.group(2));
                this.kill = "x".equalsIgnoreCase(m.group(3));
                this.to = parseSquare(m.group(4));
                String g5 = m.group(5);
                if (g5 != null) {
                    PieceType p = PieceType.getPieceType(g5);
                    this.promotion = (byte)p.ordinal();
                } else {
                    this.promotion = 0;
                }
                String g6 = m.group(6);
                if (g6 != null) {
                    if ("#".equals(g6)) {
                        check = true;
                        mate = true;
                    } else if ("+".equals(g6)) {
                        check = true;
                    }
                }
            } else if ((castl = CASTLING.matcher(move)).matches()) {
                if (white == null) {
                    throw new IllegalArgumentException(
                            "Can't parse castling without side specified");
                }
                this.type = (byte)PieceType.KING.ordinal();
                this.kill = false;
                this.promotion = 0;
                int y = white ? 0 : 7;
                this.from = getPos(4, y);
                if (castl.groupCount() > 0) { // long
                    this.to = getPos(2, y);
                } else { // short
                    this.to = getPos(6, y);
                }
            } else {
                throw new IllegalArgumentException(String.format("Illegal move: %s", move));
            }
            this.check = check;
            this.mate = mate;
        }
        
        public Move(int from, int to) {
            this(from, to, (byte)0);
        }
        
        public Move(int from, int to, byte type) {
            this(from, to, type, false);
        }
        
        public Move(int from, int to, byte type, boolean kill) {
            this(from, to, type, kill, (byte)0);
        }
        
        public Move(int from, int to, byte type, byte promotion) {
            this(from, to, type, false, promotion);
        }
        
        public Move(int from, int to, byte type, boolean kill, byte promotion) {
            this(from, to, type, kill, promotion, false, false);
        }
        
        public Move(Move move, boolean check, boolean mate) {
            this(move.from, move.to, move.type, move.kill, move.promotion, check, mate);
        }
        
        public Move(int from,
                int to,
                byte type,
                boolean kill,
                byte promotion,
                boolean check,
                boolean mate) {
            this.from = from;
            this.to = to;
            this.type = type;
            this.kill = kill;
            this.promotion = promotion;
            this.check = check;
            this.mate = mate;
        }
        
        @Override
        public String toString() {
            return this.toString(true);
        }
        
        public String toString(boolean castling) {
            PieceType t = PieceType.getPieceType(this.type);
            int xdelta;
            if (castling && t == PieceType.KING
                && (xdelta = Math.abs(getX(this.from) - getX(this.to))) > 1) {
                if (xdelta == 2) {
                    return "0-0";
                } else if (xdelta == 3) {
                    return "0-0-0";
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append(t.toString());
            sb.append(squareName(this.from));
            if (this.kill) {
                sb.append('x');
            }
            sb.append(squareName(this.to));
            PieceType p = PieceType.getPieceType(this.promotion);
            if (p != PieceType.NONE) {
                sb.append(p.toString());
            }
            if (this.mate) {
                sb.append('#');
            } else if (this.check) {
                sb.append('+');
            }
            return sb.toString();
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.from, this.to, this.type, this.kill,
                                this.promotion, this.check, this.mate);
        }
        
        @Override
        public boolean equals(Object o) {
            return (o instanceof Move) && this.equals((Move)o);
        }
        
        public boolean equals(Move m) {
            return m != null
                && this.from == m.from
                && this.to == m.to
                && this.type == m.type
                && this.kill == m.kill
                && this.promotion == m.promotion
                && this.check == m.check
                && this.mate == m.mate;
        }
    }
}
