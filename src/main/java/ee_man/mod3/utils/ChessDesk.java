package ee_man.mod3.utils;

public class ChessDesk{
	
	public byte[] desk;
	
	public byte lastFrom;
	
	public byte lastTo;
	
	public boolean isWhiteTurn;
	
	public static final byte[] getDefDesk(){
		return new byte[]{+7, +3, +2, +5, +9, +2, +3, +7,
						  +1, +1, +1, +1, +1, +1, +1, +1,
						  +0, +0, +0, +0, +0, +0, +0, +0,
						  +0, +0, +0, +0, +0, +0, +0, +0,
						  +0, +0, +0, +0, +0, +0, +0, +0,
						  +0, +0, +0, +0, +0, +0, +0, +0,
						  -1, -1, -1, -1, -1, -1, -1, -1,
						  -7, -3, -2, -5, -9, -2, -3, -7};
	}
	
	public ChessDesk(){
		desk = getDefDesk();
		lastFrom = -1;
		lastTo = -1;
		isWhiteTurn = true;
	}
	
	public ChessDesk(ChessDesk par1){
		desk = par1.desk.clone();
		lastFrom = par1.lastFrom;
		lastTo = par1.lastTo;
		isWhiteTurn = par1.isWhiteTurn;
	}
	
	public ChessDesk(byte[] deskG, byte lastFromG, byte lastToG, boolean isWhiteTurnG){
		desk = deskG;
		lastFrom = lastFromG;
		lastTo = lastToG;
		isWhiteTurn = isWhiteTurnG;
	}
	
	public boolean isEmpty(int x, int y){
		return isEmpty(getFromXY(x, y));
	}
	
	public boolean isEmpty(int loc){
		if(loc < 0 || loc >= desk.length)
			return false;
		return desk[loc] == 0;
	}
	
	public boolean isColor(int x, int y, boolean bool){
		return isColor(getFromXY(x, y), bool);
	}
	
	public boolean isColor(int loc, boolean bool){
		if(loc < 0 || loc >= desk.length)
			return false;
		return(bool ? desk[loc] > 0 : desk[loc] < 0);
	}
	
	public byte getType(int x, int y){
		return getType(getFromXY(x, y));
	}
	
	public byte getType(int loc){
		if(loc < 0 || loc >= desk.length)
			return 0;
		return desk[loc];
	}
	
	public static int getX(int par1){
		return par1 % 8;
	}
	
	public static int getY(int par1){
		
		return (par1 - (par1 % 8)) / 8;
	}
	
	public static int getFromXY(int x, int y){
		return 8 * y + x;
	}
	
	public boolean canGoTo(byte from, byte to){
		if(this.canGoTo_a(from, to))
			return isLegal(from, to);
		else
			return false;
	}
	
	public boolean isLegal(byte from, byte to){
		ChessDesk bufDesk = new ChessDesk(this);
		bufDesk.make(from, to);
		int king;
		if(this.isWhiteTurn)
			for(king = 0; king < bufDesk.desk.length; king++){
				if(bufDesk.desk[king] == 6)
					break;
				if(bufDesk.desk[king] == 9){
					bufDesk.desk[king] = 6;
					break;
				}
			}
		else
			for(king = 0; king < bufDesk.desk.length; king++){
				if(bufDesk.desk[king] == -6)
					break;
				if(bufDesk.desk[king] == -9){
					bufDesk.desk[king] = -6;
					break;
				}
			}
		return !bufDesk.canColorGoTo(king, !this.isWhiteTurn);
	}
	
	public boolean canColorGoTo(int x, int y, boolean color){
		return canColorGoTo(getFromXY(x, y), color);
	}
	
	public boolean canColorGoTo(int to, boolean color){
		for(int i = 0; i < this.desk.length; i++){
			if(this.canGoTo_a(i, to) && this.isColor(i, color))
				return true;
		}
		return false;
	}
	
	public boolean canGoTo_a(int from, int to){
		byte typeF = this.desk[from];
		int fromX = getX(from);
		int fromY = getY(from);
		byte typeT = this.desk[to];
		int toX = getX(to);
		int toY = getY(to);
		if((typeF > 0 && typeT > 0) || (typeF < 0 && typeT < 0))
			return false;
		if(typeF == 1){
			if(toX == fromX && this.isEmpty(toX, toY) && toY == fromY + 1)
				return true;
			if(toX == fromX && this.isEmpty(toX, toY) && this.isEmpty(toX, fromY + 1) && toY == fromY + 2 && fromY == 1)
				return true;
			if(toX == fromX - 1 && this.isColor(toX, toY, false) && toY == fromY + 1)
				return true;
			if(toX == fromX + 1 && this.isColor(toX, toY, false) && toY == fromY + 1)
				return true;
			if(toX == fromX - 1 && this.getType(toX, toY - 1) == -1 && this.lastTo == getFromXY(toX, toY - 1) && this.lastFrom == getFromXY(toX, toY + 1) && toY == fromY + 1)
				return true;
			if(toX == fromX + 1 && this.getType(toX, toY - 1) == -1 && this.lastTo == getFromXY(toX, toY - 1) && this.lastFrom == getFromXY(toX, toY + 1) && toY == fromY + 1)
				return true;
		}
		if(typeF == -1){
			if(toX == fromX && this.isEmpty(toX, toY) && toY == fromY - 1)
				return true;
			if(toX == fromX && this.isEmpty(toX, toY) && this.isEmpty(toX, fromY - 1) && toY == fromY - 2 && fromY == 6)
				return true;
			if(toX == fromX - 1 && this.isColor(toX, toY, true) && toY == fromY - 1)
				return true;
			if(toX == fromX + 1 && this.isColor(toX, toY, true) && toY == fromY - 1)
				return true;
			if(toX == fromX - 1 && this.getType(toX, toY + 1) == 1 && this.lastTo == getFromXY(toX, toY + 1) && this.lastFrom == getFromXY(toX, toY - 1) && toY == fromY - 1)
				return true;
			if(toX == fromX + 1 && this.getType(toX, toY + 1) == 1 && this.lastTo == getFromXY(toX, toY + 1) && this.lastFrom == getFromXY(toX, toY - 1) && toY == fromY - 1)
				return true;
		}
		boolean color = typeF > 0;
		if(Math.abs(typeF) == 2 || Math.abs(typeF) == 5){
			int ieb = fromX;
			int jeb = fromY;
			while(this.isEmpty(++ieb, ++jeb)){
				if(toX == ieb && toY == jeb)
					return true;
			}
			if(this.isColor(ieb, jeb, !color) && toX == ieb && toY == jeb)
				return true;
			
			ieb = fromX;
			jeb = fromY;
			while(this.isEmpty(--ieb, --jeb)){
				if(toX == ieb && toY == jeb)
					return true;
			}
			if(this.isColor(ieb, jeb, !color) && toX == ieb && toY == jeb)
				return true;
			
			ieb = fromX;
			jeb = fromY;
			while(this.isEmpty(--ieb, ++jeb)){
				if(toX == ieb && toY == jeb)
					return true;
			}
			if(this.isColor(ieb, jeb, !color) && toX == ieb && toY == jeb)
				return true;
			
			ieb = fromX;
			jeb = fromY;
			while(this.isEmpty(++ieb, --jeb)){
				if(toX == ieb && toY == jeb)
					return true;
			}
			if(this.isColor(ieb, jeb, !color) && toX == ieb && toY == jeb)
				return true;
		}
		if(Math.abs(typeF) == 4 || Math.abs(typeF) == 5 || Math.abs(typeF) == 7){
			int ieb = fromX;
			int jeb = fromY;
			while(this.isEmpty(ieb, ++jeb)){
				if(toX == ieb && toY == jeb)
					return true;
			}
			if(this.isColor(ieb, jeb, !color) && toX == ieb && toY == jeb)
				return true;
			
			ieb = fromX;
			jeb = fromY;
			while(this.isEmpty(ieb, --jeb)){
				if(toX == ieb && toY == jeb)
					return true;
			}
			if(this.isColor(ieb, jeb, !color) && toX == ieb && toY == jeb)
				return true;
			
			ieb = fromX;
			jeb = fromY;
			while(this.isEmpty(--ieb, jeb)){
				if(toX == ieb && toY == jeb)
					return true;
			}
			if(this.isColor(ieb, jeb, !color) && toX == ieb && toY == jeb)
				return true;
			
			ieb = fromX;
			jeb = fromY;
			while(this.isEmpty(++ieb, jeb)){
				if(toX == ieb && toY == jeb)
					return true;
			}
			if(this.isColor(ieb, jeb, !color) && toX == ieb && toY == jeb)
				return true;
		}
		if(Math.abs(typeF) == 3){
			if(fromX + 2 == toX && fromY + 1 == toY && (this.isEmpty(toX, toY) || this.isColor(toX, toY, !color)))
				return true;
			if(fromX - 2 == toX && fromY + 1 == toY && (this.isEmpty(toX, toY) || this.isColor(toX, toY, !color)))
				return true;
			if(fromX + 2 == toX && fromY - 1 == toY && (this.isEmpty(toX, toY) || this.isColor(toX, toY, !color)))
				return true;
			if(fromX - 2 == toX && fromY - 1 == toY && (this.isEmpty(toX, toY) || this.isColor(toX, toY, !color)))
				return true;
			if(fromX + 1 == toX && fromY + 2 == toY && (this.isEmpty(toX, toY) || this.isColor(toX, toY, !color)))
				return true;
			if(fromX - 1 == toX && fromY + 2 == toY && (this.isEmpty(toX, toY) || this.isColor(toX, toY, !color)))
				return true;
			if(fromX + 1 == toX && fromY - 2 == toY && (this.isEmpty(toX, toY) || this.isColor(toX, toY, !color)))
				return true;
			if(fromX - 1 == toX && fromY - 2 == toY && (this.isEmpty(toX, toY) || this.isColor(toX, toY, !color)))
				return true;
		}
		if(typeF == 9){
			if(toX == 6 && toY == 0 && this.getType(7, 0) == 7 && this.isEmpty(toX, toY) && this.isEmpty(toX - 1, toY) && !this.canColorGoTo(toX - 1, toY, false) && !this.canColorGoTo(fromX, fromY, false))
				return true;
			if(toX == 2 && toY == 0 && this.getType(0, 0) == 7 && this.isEmpty(toX, toY) && this.isEmpty(toX + 1, toY) && !this.canColorGoTo(toX + 1, toY, false) && !this.canColorGoTo(fromX, fromY, false))
				return true;
		}
		if(typeF == -9){
			if(toX == 6 && toY == 7 && this.getType(7, 7) == -7 && this.isEmpty(toX, toY) && this.isEmpty(toX - 1, toY) && !this.canColorGoTo(toX - 1, toY, true) && !this.canColorGoTo(fromX, fromY, false))
				return true;
			if(toX == 2 && toY == 7 && this.getType(0, 7) == -7 && this.isEmpty(toX, toY) && this.isEmpty(toX + 1, toY) && !this.canColorGoTo(toX + 1, toY, true) && !this.canColorGoTo(fromX, fromY, false))
				return true;
		}
		if(Math.abs(typeF) == 6 || Math.abs(typeF) == 9){
			if(fromX + 1 == toX && fromY == toY && (this.isEmpty(toX, toY) || this.isColor(toX, toY, !color)))
				return true;
			if(fromX - 1 == toX && fromY == toY && (this.isEmpty(toX, toY) || this.isColor(toX, toY, !color)))
				return true;
			if(fromX + 1 == toX && fromY + 1 == toY && (this.isEmpty(toX, toY) || this.isColor(toX, toY, !color)))
				return true;
			if(fromX - 1 == toX && fromY + 1 == toY && (this.isEmpty(toX, toY) || this.isColor(toX, toY, !color)))
				return true;
			if(fromX + 1 == toX && fromY - 1 == toY && (this.isEmpty(toX, toY) || this.isColor(toX, toY, !color)))
				return true;
			if(fromX - 1 == toX && fromY - 1 == toY && (this.isEmpty(toX, toY) || this.isColor(toX, toY, !color)))
				return true;
			if(fromX == toX && fromY + 1 == toY && (this.isEmpty(toX, toY) || this.isColor(toX, toY, !color)))
				return true;
			if(fromX == toX && fromY - 1 == toY && (this.isEmpty(toX, toY) || this.isColor(toX, toY, !color)))
				return true;
		}
		return false;
	}
	
	public void make(byte from, byte to){
		byte type = this.getType(from);
		if(type == 9 && to == getFromXY(6, 0)){
			this.desk[getFromXY(5, 0)] = 4;
			
			this.desk[getFromXY(7, 0)] = 0;
		}
		else
			if(type == 9 && to == getFromXY(2, 0)){
				this.desk[getFromXY(3, 0)] = 4;
				this.desk[getFromXY(0, 0)] = 0;
			}
			else
				if(type == -9 && to == getFromXY(6, 7)){
					this.desk[getFromXY(5, 7)] = -4;
					this.desk[getFromXY(7, 7)] = 0;
				}
				else
					if(type == -9 && to == getFromXY(2, 7)){
						this.desk[getFromXY(3, 7)] = -4;
						this.desk[getFromXY(0, 7)] = 0;
					}
					else
						if(type == 1 && getX(from) + 1 == getX(to) && getY(from) + 1 == getY(to) && this.isEmpty(to)){
							this.desk[from + 1] = 0;
						}
						else
							if(type == 1 && getX(from) - 1 == getX(to) && getY(from) + 1 == getY(to) && this.isEmpty(to)){
								this.desk[from - 1] = 0;
							}
							else
								if(type == -1 && getX(from) + 1 == getX(to) && getY(from) - 1 == getY(to) && this.isEmpty(to)){
									this.desk[from + 1] = 0;
								}
								else
									if(type == -1 && getX(from) - 1 == getX(to) && getY(from) - 1 == getY(to) && this.isEmpty(to)){
										this.desk[from - 1] = 0;
									}
		if(type > 6)
			type -= 3;
		if(type < -6)
			type += 3;
		this.desk[to] = type;
		this.desk[from] = 0;
		this.lastFrom = from;
		this.lastTo = to;
		this.isWhiteTurn = !this.isWhiteTurn;
	}
	
	public int needChoose(){
		for(int i = 0; i < 8; i++){
			if(this.desk[getFromXY(i, 7)] == 1)
				return(i + 1);
			if(this.desk[getFromXY(i, 0)] == -1)
				return -(i + 1);
		}
		return 0;
	}
}
