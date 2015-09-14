import java.awt.Image;


public class GPiece {
	public Image img;
	public int drawX,drawY;
	public int boardX,boardY;
	boolean white;
	int dim;
	int id;

	public GPiece(Image img,Piece p,final ClientPanel b){
		this.img=img;
		if(p.location!=null){
			this.boardX=p.location[0];
			this.boardY=p.location[1];
			this.drawX=b.getLocationCorner(p.location)[0];
			this.drawY=b.getLocationCorner(p.location)[1];
		}else{
			this.boardX=-1;
			this.boardY=-1;
			this.drawX=-1000;
			this.drawY=-1000;
		}
		this.white=p.white;
		this.dim=b.squareSize;
		this.id=p.pieceID;
	}
	public boolean contains(int X, int Y) {
		int w = this.getWidth();
		int h = this.getHeight();
		if ((w | h) < 0) {	//either less than zero
			return false;
		}
		int x = this.drawX;
		int y = this.drawY;
		if (X < x || Y < y) {
			return false;
		}
		w += x;
		h += y;
		return ((w < x || w > X) &&
				(h < y || h > Y));
	}
	public int getWidth() {
		return dim;
	}
	public int getHeight() {
		return dim;
	}
	public void setDrawLocation(int new_x, int new_y) {
		this.drawX=new_x;
		this.drawY=new_y;
	}
	public void setBoardLocation(int x,int y){
		this.boardX=x;
		this.boardY=y;
	}
}
