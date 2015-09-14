import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class ClientPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2615170480139424970L;
	private boolean white;
	private Game g;
	private BufferedImage[] board=new BufferedImage[2];
	public HashMap<Integer,BufferedImage> pmap=new HashMap<Integer, BufferedImage>();

	public LinkedList<GPiece> onBoard=new LinkedList<GPiece>();
	public LinkedList<GPiece> toDrop =new LinkedList<GPiece>();

	int squareSize;

	public void initVars(){
		onBoard.clear();
		toDrop.clear();
		int playerID=g.associatedPlayer;
		this.white=g.associatedPlayer%3==0;
		for(Piece p:g.getBoard(playerID).onBoard)
			if(p.white==this.white)
				onBoard.add(new GPiece(pmap.get(p.pieceID), p, this));
	}
	public ClientPanel(BughouseClient bug) throws IOException{

		this.g=bug.g;

		this.squareSize=40;

		board[0]=ImageIO.read(new File("Blackboard.gif"));
		board[1]=ImageIO.read(new File("WhiteBoard.gif"));

		pmap.put(1,ImageIO.read(new File("White King.png")));
		pmap.put(2,ImageIO.read(new File("White Queen.png")));
		pmap.put(3,ImageIO.read(new File("White Rook.png")));
		pmap.put(4,ImageIO.read(new File("White Knight.png")));
		pmap.put(5,ImageIO.read(new File("White Bishop.png")));
		pmap.put(6,ImageIO.read(new File("White Pawn.png")));
		pmap.put(7,ImageIO.read(new File("Black King.png")));
		pmap.put(8,ImageIO.read(new File("Black Queen.png")));
		pmap.put(9,ImageIO.read(new File("Black Rook.png")));
		pmap.put(10,ImageIO.read(new File("Black Knight.png")));
		pmap.put(11,ImageIO.read(new File("Black Bishop.png")));
		pmap.put(12,ImageIO.read(new File("Black Pawn.png")));

		setBackground(Color.LIGHT_GRAY);

		addMouseMotionListener(new DragAdapter());
		addMouseListener(new DragAdapter());
	}

	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		int playerID=this.g.associatedPlayer;
		Graphics2D g2d=(Graphics2D) g;
		if(white){
			for(int x=0;x<8;x++)
				for(int y=0;y<8;y++)
					g2d.drawImage(board[(x+y)%2], squareSize*(x+1),squareSize*(11-y),squareSize,squareSize,null);
			for(int x=0;x<8;x++)
				for(int y=0;y<8;y++)
					g2d.drawImage(board[(x+y)%2], squareSize*(x+10),squareSize*(4+y),squareSize,squareSize,null);
			for(Piece p:this.g.getOtherBoard(playerID).onBoard)
				g2d.drawImage(pmap.get(p.pieceID),squareSize*(p.location[0]+10),squareSize*(p.location[1]+4),squareSize,squareSize,null);

		}else{
			for(int x=0;x<8;x++)
				for(int y=0;y<8;y++)
					g2d.drawImage(board[(x+y)%2],squareSize*(x+1),squareSize*(y+4),squareSize,squareSize,null);
			for(int x=0;x<8;x++)
				for(int y=0;y<8;y++)
					g2d.drawImage(board[(x+y)%2],squareSize*(x+10),squareSize*(11-y),squareSize,squareSize,null);
			for(Piece p:this.g.getOtherBoard(playerID).onBoard)
				g2d.drawImage(pmap.get(p.pieceID),squareSize*(p.location[0]+10),squareSize*(11-p.location[1]),squareSize,squareSize,null);
		}

		g2d.drawString(this.g.getTimeString(playerID)+"\n"+this.g.names[playerID], 10, squareSize*11);
		g2d.drawString(this.g.getTimeString((playerID+2)%4)+"\n"+this.g.names[(playerID+2)%4], 10, squareSize*4);
		g2d.drawString(this.g.getTimeString((3*playerID+3)%4)+"\n"+this.g.names[(3*playerID+3)%4], squareSize*18+5, squareSize*4);
		g2d.drawString(this.g.getTimeString((3*playerID+1)%4)+"\n"+this.g.names[(3*playerID+1)%4], squareSize*18+5,squareSize*11);

		ListIterator<GPiece> li=onBoard.listIterator(onBoard.size());
		while(li.hasPrevious()){
			GPiece rect=li.previous();
			g2d.drawImage(rect.img, rect.drawX, rect.drawY,squareSize,squareSize, null);
		}
		if(white){
			for(Piece p:this.g.getBoard(playerID).onBoard)
				if(!p.white)
					if(p instanceof Pawn&&((Pawn)p).actAs!=null){
						g2d.drawImage(pmap.get(((Pawn)p).actAs.pieceID),squareSize*(p.location[0]+1),squareSize*(11-p.location[1]),squareSize,squareSize,null);
						g2d.setColor(p.white?Color.BLACK:Color.WHITE);
						g2d.drawOval(squareSize*(p.location[0]+1),squareSize*(11-p.location[1]), squareSize,squareSize);
					}else
						g2d.drawImage(pmap.get(p.pieceID),squareSize*(p.location[0]+1),squareSize*(11-p.location[1]),squareSize,squareSize,null);
		}else
			for(Piece p:this.g.getBoard(playerID).onBoard)
				if(p.white)
					if(p instanceof Pawn&&((Pawn)p).actAs!=null){
						g2d.drawImage(pmap.get(((Pawn)p).actAs.pieceID),squareSize*(p.location[0]+1),squareSize*(p.location[1]+4),squareSize,squareSize,null);
						g2d.setColor(p.white?Color.BLACK:Color.WHITE);
						g2d.drawOval(squareSize*(p.location[0]+1),squareSize*(p.location[1]+4), squareSize,squareSize);
					}else
					g2d.drawImage(pmap.get(p.pieceID),squareSize*(p.location[0]+1),squareSize*(p.location[1]+4),squareSize,squareSize,null);
		int x=1;
		int y=12;
		for(GPiece p:toDrop){
			p.drawX=squareSize*x;
			p.drawY=squareSize*y;
			g2d.drawImage(p.img,squareSize*x,squareSize*y,squareSize,squareSize,null);
			x++;
			if(x==9)
				x=1;
		}
		if(whichPiece!=null)
			g2d.drawImage(whichPiece.img, whichPiece.drawX,whichPiece.drawY,squareSize,squareSize, null);
		if(!this.g.gameInProgress){
			if(this.g.endCondition!=null){
				Object o=this.g.endCondition;
				this.g.endCondition=null;
				if(o instanceof TimeExpiredPacket){
					int option=JOptionPane.showConfirmDialog(this, "Player "+((TimeExpiredPacket)o).playerID+" ran out of time.  Select YES to play again.");
					//reset EVERYTHING
					this.g.reset();
					this.g.checkIn();
					
					/*try {
						this.g.client.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.exit(0);*/
				}else if(o instanceof KingTakenPacket){
					int option=JOptionPane.showConfirmDialog(this, "Player "+((KingTakenPacket)o).playerID+" lost his king.  Select YES to play again.");
					this.g.reset();
					this.g.checkIn();
					/*try {
						this.g.client.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//System.exit(0);*/
				}else if(o instanceof PlayerTerminatedPacket){
					JOptionPane.showMessageDialog(this, "Player "+((PlayerTerminatedPacket)o).playerID+" terminated his connection.");
					this.g.reset();
					this.g.checkIn();
				}
			}
		}

	}
	public int[] pointToLocation(int x,int y){
		if(white)
			return new int[]{x/squareSize-1,11-y/squareSize};
		else
			return new int[]{x/squareSize-1,y/squareSize-4};
	}
	public int[] getLocationCorner(int[] location) {
		if(white)
			return new int[]{squareSize*(location[0]+1),squareSize*(11-location[1])};
		else
			return new int[]{squareSize*(location[0]+1),squareSize*(4+location[1])};
	}

	int pX,pY;
	int[] from;
	int fromDrawX=-1,fromDrawY=-1;
	GPiece whichPiece;
	boolean pressz;
	private class DragAdapter extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent e) {
			boolean bad=true;
			ListIterator<GPiece> li=onBoard.listIterator();
			while(li.hasNext()){
				GPiece rect=li.next();
				pX = rect.drawX - e.getX();
				pY = rect.drawY - e.getY();
				if (rect.contains(e.getX(), e.getY())) {
					from=new int[]{rect.boardX,rect.boardY};
					fromDrawX=rect.drawX;
					fromDrawY=rect.drawY;
					whichPiece=rect;
					li.remove();
					updateLocation(e);
					bad=false;
					break;
				}
			}
			if(bad){
				li=toDrop.listIterator();
				while(li.hasNext()){
					GPiece rect=li.next();
					pX = rect.drawX - e.getX();
					pY = rect.drawY - e.getY();
					if (rect.contains(e.getX(), e.getY())) {
						from=new int[]{rect.boardX,rect.boardY};
						fromDrawX=rect.drawX;
						fromDrawY=rect.drawY;
						whichPiece=rect;
						li.remove();
						updateLocation(e);
						bad=false;
						break;
					}
				}
			}
			if(bad)
				pressz = true;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (!pressz) {
				updateLocation(e);
			} else {
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			int playerID=g.associatedPlayer;
			if (g.gameInProgress&&whichPiece!=null&&whichPiece.contains(e.getX(), e.getY())) {
				Piece pat=null;
				if(whichPiece.boardX<0){
					ListIterator<Piece> find=ClientPanel.this.g.getBoard(playerID).toDrop.listIterator();
					while(find.hasNext()){
						pat=find.next();
						System.out.println(pat.pieceID);
						if(pat.pieceID==whichPiece.id)
							break;
					}
					int[] to=pointToLocation(e.getX(),e.getY());
					try {
						if(ClientPanel.this.g.makeMove(pat, to,playerID)){
							whichPiece.setDrawLocation(getLocationCorner(to)[0], getLocationCorner(to)[1]);
							whichPiece.setBoardLocation(to[0], to[1]);
						}else
							whichPiece.setDrawLocation(fromDrawX, fromDrawY);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}else{
					pat=ClientPanel.this.g.getBoard(playerID).getAt(new int[]{whichPiece.boardX,whichPiece.boardY});
					for(int[] move:pat.getMoves(g.getBoard(playerID)))
						System.out.println(pat.getClass().getName()+" to "+Arrays.toString(move)+" from "+Arrays.toString(pat.location));
					updateLocation(e);
					//fit to square closest to cursor
					int[] to=pointToLocation(e.getX(),e.getY());
					
					if(whichPiece.white==g.currentPlayer(playerID)){
						try {
							if(ClientPanel.this.g.makeMove(from,to,playerID)){
								whichPiece.setDrawLocation(getLocationCorner(to)[0], getLocationCorner(to)[1]);
								whichPiece.setBoardLocation(to[0], to[1]);
								if((pat instanceof King)&&(to[0]-from[0]==2)){
									ListIterator<GPiece> lig=onBoard.listIterator();
									while(lig.hasNext()){
										GPiece gp=lig.next();
										if(gp.boardX==7&&gp.boardY==to[1]){
											gp.setDrawLocation(getLocationCorner(new int[]{5,to[1]})[0], getLocationCorner(new int[]{5,to[1]})[1]);
											gp.setBoardLocation(5, to[1]);
											break;
										}
									}
								}
								else if((pat instanceof King)&&(to[0]-from[0]==-2)){
									ListIterator<GPiece> lig=onBoard.listIterator();
									while(lig.hasNext()){
										GPiece gp=lig.next();
										if(gp.boardX==0&&gp.boardY==to[1]){
											gp.setDrawLocation(getLocationCorner(new int[]{3,to[1]})[0], getLocationCorner(new int[]{3,to[1]})[1]);
											gp.setBoardLocation(3, to[1]);
											break;
										}
									}
								}else if((pat instanceof Pawn)&&(to[1]==0||to[1]==7)){
									BufferedImage buf=new BufferedImage(squareSize,squareSize,BufferedImage.TYPE_INT_ARGB);
									Graphics2D bufg2d=buf.createGraphics();
									Pawn pp=(Pawn)pat;
									bufg2d.drawImage(pmap.get(pp.actAs.pieceID), 0, 0, squareSize,squareSize, null);
									bufg2d.setColor(pat.white?Color.BLACK:Color.WHITE);
									bufg2d.drawOval(0, 0, squareSize,squareSize);
									bufg2d.dispose();
									whichPiece.img=buf;
								}
							}else
								whichPiece.setDrawLocation(fromDrawX, fromDrawY);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}else
						whichPiece.setDrawLocation(fromDrawX, fromDrawY);
				}
			} else{
				if(whichPiece!=null&&fromDrawX>-1)
					whichPiece.setDrawLocation(fromDrawX, fromDrawY);
				pressz = false;
			}
			if(whichPiece!=null)
				onBoard.add(whichPiece);
			whichPiece=null;
			repaint();
		}

		public void updateLocation(MouseEvent e) {
			whichPiece.setDrawLocation(pX + e.getX(), pY + e.getY());

			repaint();
		}
	}
}
