import java.util.LinkedList;
import java.util.ListIterator;


public class Board {
	Piece[][] b=new Piece[8][8];
	LinkedList<Piece> onBoard=new LinkedList<Piece>();
	LinkedList<Piece> toDrop=new LinkedList<Piece>();
	public Board(){
		for(int i=0;i<8;i++){
			b[i][1]=new Pawn(i,1,true);
			b[i][6]=new Pawn(i,6,false);
		}
		b[0][0]=new Rook(0,0,true);
		b[1][0]=new Knight(1,0,true);
		b[2][0]=new Bishop(2,0,true);
		b[3][0]=new Queen(3,0,true);
		b[4][0]=new King(4,0,true);
		b[5][0]=new Bishop(5,0,true);
		b[6][0]=new Knight(6,0,true);
		b[7][0]=new Rook(7,0,true);

		b[0][7]=new Rook(0,7,false);
		b[1][7]=new Knight(1,7,false);
		b[2][7]=new Bishop(2,7,false);
		b[3][7]=new Queen(3,7,false);
		b[4][7]=new King(4,7,false);
		b[5][7]=new Bishop(5,7,false);
		b[6][7]=new Knight(6,7,false);
		b[7][7]=new Rook(7,7,false);

		for(int x=0;x<8;x++)
			for(int y=0;y<8;y++)
				if(b[x][y]!=null)
					onBoard.add(b[x][y].clone());
	}
	public Piece setAt(int fX,int fY,int tX,int tY){
		Piece p=b[tX][tY];

		ListIterator<Piece> li=onBoard.listIterator();
		while(li.hasNext()){
			Piece a=li.next();
			if(a.location[0]==tX&&a.location[1]==tY)
				li.remove();
			else if(a.location[0]==fX&&a.location[1]==fY)
				a.location=new int[]{tX,tY};
		}
		b[tX][tY]=b[fX][fY];
		b[fX][fY]=null;
		b[tX][tY].location=new int[]{tX,tY};
		return p;
	}
	public Piece getAt(int x,int y){
		return b[x][y];
	}
	public Piece getAt(int[] loc){
		return b[loc[0]][loc[1]];
	}

}
