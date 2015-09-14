import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;

import javax.imageio.ImageIO;


public abstract class Piece implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6213196416257515968L;
	int[] location;
	boolean white;
	int pieceID;
	boolean moved=false;
	public Piece(int x,int y,boolean white){
		location=new int[]{x,y};
		this.white=white;
	}
	public int[] getLocation(){
		return location;
	}
	public abstract LinkedList<int[]> getMoves(Board b);
	public abstract Piece clone();
	protected final boolean okay(int x,int y){
		return location[0]+x>=0&&location[1]+y>=0&&location[0]+x<8&&location[1]+y<8;
	}
	protected final boolean free(Board b,int x,int y){
		Piece p=b.getAt(x+location[0],y+location[1]);
		return p==null||p.white!=this.white;
	}
	protected final boolean empty(Board b,int x,int y){
		return b.getAt(x+location[0],y+location[1])==null;
	}
	protected final int[] coord(int x,int y){
		return new int[]{location[0]+x,location[1]+y};
	}
}

class King extends Piece {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6042166671392695791L;

	public King(int x, int y, boolean white) {
		super(x, y, white);
		if(white)
			this.pieceID=1;
		else
			this.pieceID=7;
	}

	@Override
	public LinkedList<int[]> getMoves(Board b) {
		LinkedList<int[]> m=new LinkedList<int[]>();
		for(int i=-1;i<2;i++)
			for(int j=-1;j<2;j++){
				if(i==0&&j==0)
					continue;
				if(okay(i,j)&&free(b,i,j))	//needs to check for Check
					m.add(new int[]{i+location[0],j+location[1]});
			}
		if(!moved&&empty(b,1,0)&&empty(b,2,0)&&!b.b[location[0]+3][location[1]].moved)
			m.add(coord(2,0));
		if(!moved&&empty(b,-1,0)&&empty(b,-2,0)&&empty(b,-3,0)&&!b.b[location[0]-4][location[1]].moved)
			m.add(coord(-2,0));
		return m;
	}

	@Override
	public Piece clone() {
		return new King(this.location[0],this.location[1],this.white);
	}

}
class Rook extends Piece {
	/**
	 * 
	 */
	private static final long serialVersionUID = 53111695987528600L;
	public Rook(int x,int y,boolean white){
		super(x,y,white);
		if(white)
			this.pieceID=3;
		else
			this.pieceID=9;
	}
	public LinkedList<int[]> getMoves(Board b) {
		LinkedList<int[]> m=new LinkedList<int[]>();
		for(int x=1;x<8;x++)
			if(okay(x,0)&&empty(b,x,0))
				m.add(coord(x,0));
			else if(okay(x,0)&&free(b,x,0)){
				m.add(coord(x,0));
				break;
			}else
				break;
		for(int x=-1;x>-8;x--)
			if(okay(x,0)&&empty(b,x,0))
				m.add(coord(x,0));
			else if(okay(x,0)&&free(b,x,0)){
				m.add(coord(x,0));
				break;
			}else
				break;

		for(int y=1;y<8;y++)
			if(okay(0,y)&&empty(b,0,y))
				m.add(coord(0,y));
			else if(okay(0,y)&&free(b,0,y)){
				m.add(coord(0,y));
				break;
			}else
				break;
		for(int y=-1;y>-8;y--)
			if(okay(0,y)&&empty(b,0,y))
				m.add(coord(0,y));
			else if(okay(0,y)&&free(b,0,y)){
				m.add(coord(0,y));
				break;
			}else
				break;

		return m;
	}
	@Override
	public Piece clone() {
		return new Rook(this.location[0],this.location[1],this.white);
	}
}
class Bishop extends Piece{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4461979451945643200L;

	public Bishop(int x, int y, boolean white) {
		super(x, y, white);
		if(white)
			this.pieceID=5;
		else
			this.pieceID=11;
	}

	@Override
	public LinkedList<int[]> getMoves(Board b) {
		LinkedList<int[]> m=new LinkedList<int[]>();
		for(int j=-1;j<=2;j+=2)
			for(int k=-1;k<=2;k+=2)
				for(int i=1;i<8;i++)
					if(okay(i*j,i*k)&&empty(b,i*j,i*k))
						m.add(coord(i*j,i*k));
					else if(okay(i*j,i*k)&&free(b,i*j,i*k)){
						m.add(coord(i*j,i*k));
						break;
					}else
						break;
		return  m;
	}

	@Override
	public Piece clone() {
		return new Bishop(this.location[0],this.location[1],white);
	}

}
class Pawn extends Piece{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4825730112627517716L;
	Piece actAs=null;
	public Pawn(int x, int y, boolean white) {
		super(x, y, white);
		if(white)
			this.pieceID=6;
		else
			this.pieceID=12;
	}

	@Override
	public LinkedList<int[]> getMoves(Board b) {
		if(actAs!=null){
			actAs.location=this.location;
			return actAs.getMoves(b);
		}
		LinkedList<int[]> m=new LinkedList<int[]>();
		if(white){
			if(okay(0,1)&&empty(b,0,1)){
				m.add(coord(0,1));
				if(!moved&&okay(0,2)&&empty(b,0,1))
					m.add(coord(0,2));
			}
			if(okay(1,1)&&free(b,1,1)&&!empty(b,1,1))
				m.add(coord(1,1));
			if(okay(-1,1)&&free(b,-1,1)&&!empty(b,-1,1))
				m.add(coord(-1,1));
			return m;
		}
		if(okay(0,-1)&&empty(b,0,-1)){
			m.add(coord(0,-1));
			if(!moved&&okay(0,-2)&&empty(b,0,-1))
				m.add(coord(0,-2));
		}
		if(okay(1,-1)&&free(b,1,-1)&&!empty(b,1,-1))
			m.add(coord(1,-1));
		if(okay(-1,-1)&&free(b,-1,-1)&&!empty(b,-1,-1))
			m.add(coord(-1,-1));
		return m;
	}

	@Override
	public Piece clone() {
		return new Pawn(this.location[0],this.location[1],white);
	}

}
class Knight extends Piece{
	/**
	 * 
	 */
	private static final long serialVersionUID = -31324425649581999L;
	static int[][] jumps=new int[][]{
		{1,2},{1,-2},{-1,2},{-1,-2},
		{2,1},{2,-1},{-2,1},{-2,-1}
	};
	public Knight(int x, int y, boolean white) {
		super(x, y, white);
		if(white)
			this.pieceID=4;
		else
			this.pieceID=10;
	}

	@Override
	public LinkedList<int[]> getMoves(Board b) {
		LinkedList<int[]> m=new LinkedList<int[]>();
		for(int[] j:jumps)
			if(okay(j[0],j[1])&&free(b,j[0],j[1]))
				m.add(coord(j[0],j[1]));
		return m;
	}

	@Override
	public Piece clone() {
		return new Knight(this.location[0],this.location[1],white);
	}

}
class Queen extends Piece{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2867721583611756445L;

	public Queen(int x, int y, boolean white) {
		super(x, y, white);
		if(white)
			this.pieceID=2;
		else
			this.pieceID=8;
	}

	@Override
	public LinkedList<int[]> getMoves(Board b) {
		LinkedList<int[]> m=new LinkedList<int[]>();
		for(int j=-1;j<=2;j+=2)
			for(int k=-1;k<=2;k+=2)
				for(int i=1;i<8;i++)
					if(okay(i*j,i*k)&&empty(b,i*j,i*k))
						m.add(coord(i*j,i*k));
					else if(okay(i*j,i*k)&&free(b,i*j,i*k)){
						m.add(coord(i*j,i*k));
						break;
					}else
						break;
		for(int x=1;x<8;x++)
			if(okay(x,0)&&empty(b,x,0))
				m.add(coord(x,0));
			else if(okay(x,0)&&free(b,x,0)){
				m.add(coord(x,0));
				break;
			}else
				break;
		for(int x=-1;x>-8;x--)
			if(okay(x,0)&&empty(b,x,0))
				m.add(coord(x,0));
			else if(okay(x,0)&&free(b,x,0)){
				m.add(coord(x,0));
				break;
			}else
				break;

		for(int y=1;y<8;y++)
			if(okay(0,y)&&empty(b,0,y))
				m.add(coord(0,y));
			else if(okay(0,y)&&free(b,0,y)){
				m.add(coord(0,y));
				break;
			}else
				break;
		for(int y=-1;y>-8;y--)
			if(okay(0,y)&&empty(b,0,y))
				m.add(coord(0,y));
			else if(okay(0,y)&&free(b,0,y)){
				m.add(coord(0,y));
				break;
			}else
				break;
		return m;
	}

	@Override
	public Piece clone() {
		return new Queen(this.location[0],this.location[1],white);
	}

}