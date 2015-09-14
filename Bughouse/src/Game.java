import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.LinkedBlockingDeque;

import javax.swing.JFrame;


public class Game {
	int associatedPlayer;
	int[] cPlayer=new int[2];
	long[] time=new long[]{5*60*1000,5*60*1000,5*60*1000,5*60*1000};
	long[] timeStamp=new long[2];
	private Board[] boards=new Board[2];
	BughouseClient bclient;
	Socket client;
	String ip="localhost";//"98.231.209.210"
	ObjectInputStream in;
	ObjectOutputStream out;
	volatile boolean gameInProgress=false;
	Object endCondition=null;

	String playerName=null;
	String partnerName=null;
	
	String[] names=new String[]{"","","",""};
	
	public static void main(String[] args) throws IOException, ClassNotFoundException{
		//new Game("frank","henry");
		//new Game("george","isaac");
		//new Game("henry","frank");
		new Game("isaac","george");
	}
	public Game(String playerName,String partnerName) throws IOException, ClassNotFoundException{
		this.partnerName=partnerName;
		this.playerName=playerName;
		client=new Socket(ip,60010);

		boards[0]=new Board();
		boards[1]=new Board();

		cPlayer[0]=0;
		cPlayer[1]=3;
		bclient=new BughouseClient(this);
		new Thread(){
			public void run() {
				try {
					out=new ObjectOutputStream(client.getOutputStream());
					in=new ObjectInputStream(client.getInputStream());
					out.writeObject(new GameInitializationPacket(playerName,partnerName));
					associatedPlayer=(Integer)in.readObject();
					System.out.println("We are player: "+associatedPlayer);
					Game.this.bclient.panel.initVars();
					System.out.println("inited");
					GameInformationPacket gip=(GameInformationPacket)in.readObject();
					names=gip.names;
					Game.this.checkIn();
				} catch (IOException | ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					while(!client.isClosed()){
						while(gameInProgress){
							System.out.println("waiting for packets...");
							Object o=in.readObject();
							if(o instanceof PlayerMovePacket){
								PlayerMovePacket pmp=(PlayerMovePacket)o;
								Game.this.makeMove(pmp.from, pmp.to, pmp.id);
							}else if(o instanceof PlayerDropPacket){
								PlayerDropPacket pdp=(PlayerDropPacket)o;
								Game.this.makeMove(pdp.p, pdp.to, pdp.id);
							}else if(o instanceof TimeUpdatePacket){
								TimeUpdatePacket tup=(TimeUpdatePacket)o;
								Game.this.updateTime(tup.time);
							}else if(o instanceof TimeExpiredPacket){
								endCondition=o;
								gameInProgress=false;
							}else if(o instanceof KingTakenPacket){
								endCondition=o;
								gameInProgress=false;
							}else if(o instanceof PlayerTerminatedPacket){
								endCondition=o;
								gameInProgress=false;
							}else
								System.out.println(o.toString());

						}
					}
				} catch (ClassNotFoundException | IOException e) {
					gameInProgress=false;
					System.err.println("The player: "+associatedPlayer);
					e.printStackTrace();
				}
			}
		}.start();

	}
	public Game(String playerName) throws IOException, ClassNotFoundException{
		this(playerName,null);
	}
	public Game() throws IOException, ClassNotFoundException{
		this(null,null);
	}
	public void close() throws IOException{
		client.close();
	}
	public Board getBoard(int id){
		return boards[id%2];
	}
	public Board getOtherBoard(int id){
		return boards[(id+1)%2];
	}
	public boolean currentPlayer(int id){
		return cPlayer[id%2]%3==0;
	}

	public String getTimeString(int player){
		if(!gameInProgress)
			return "5:00";
		long clock;
		if(cPlayer[player%2]==player)
			clock=time[player]-System.currentTimeMillis()+timeStamp[player%2];
		else
			clock=time[player];
		if(clock>=60000)
			return ((clock/60000)+":"+((clock/1000)%60));
		return (clock/1000)+"_"+(clock/100)%10;
	}

	public synchronized void updateTime(long[] t){
		synchronized(time){
			synchronized(timeStamp){
				long[] passback=new long[4];
				for(int i=0;i<4;i++)
					if(i==cPlayer[i%2])
						passback[i]=time[i]-System.currentTimeMillis()+timeStamp[i%2];
					else
						passback[i]=time[i];
				//	System.out.println("Already had: "+Arrays.toString(passback));
				//	System.out.println("Updated to: "+Arrays.toString(t));
				timeStamp[0]=System.currentTimeMillis();
				timeStamp[1]=System.currentTimeMillis();
				time=t;
			}
		}
	}

	public boolean makeMove(Piece p,int[] to,int id) throws IOException{
		System.out.println("On board "+associatedPlayer+" received drop by "+id);
		if(cPlayer[id%2]!=id)
			return false;
		if(to[0]<0||to[1]<0||to[0]>7||to[1]>7)
			return false;
		if((p instanceof Pawn)&&(to[1]==0||to[1]==7))
			return false;
		Board b=boards[id%2];
		if(b.getAt(to)!=null)
			return false;
		ListIterator<Piece> td=b.toDrop.listIterator();
		boolean flag=true;
		while(td.hasNext()){
			Piece t=td.next();
			if(t.pieceID==p.pieceID){
				flag=false;
				td.remove();
				break;
			}
		}
		if(flag)
			return false;
		p.moved=true;
		p.location=new int[]{to[0],to[1]};
		b.onBoard.push(p.clone());

		b.b[to[0]][to[1]]=p.clone();

		cPlayer[id%2]=(id+2)%4;

		synchronized(time){
			synchronized(timeStamp){
				time[id]-=System.currentTimeMillis()-timeStamp[id%2];
				timeStamp[id%2]=System.currentTimeMillis();
			}
		}



		//tell server what we did...
		if(id==this.associatedPlayer){
			out.writeObject(new PlayerDropPacket(p,to,id));
			out.flush();

		}
		//update this client side
		bclient.change((id+2)%4,null,to,null);
		System.out.println("On board "+associatedPlayer+" Made drop by "+id);
		return true;
	}
	public boolean makeMove(int[] from,int[] to,int id) throws IOException{
		System.out.println("On board "+associatedPlayer+" received move by "+id);
		if(cPlayer[id%2]!=id)
			return false;
		if(from[0]<0||from[1]<0||to[0]<0||to[1]<0)
			return false;
		if(from[0]>7||from[1]>7||to[1]>7||to[0]>7)
			return false;
		Board b=boards[id%2];
		Piece p=b.getAt(from);
		if(p==null)
			return false;
		for(int[] m:p.getMoves(b))
			if(m[0]==to[0]&&m[1]==to[1]){
				Piece taken=b.setAt(from[0], from[1], to[0], to[1]);
				if(taken!=null)
					taken.location=null;
				if((p instanceof King)&&(to[0]-from[0]==2))
					b.setAt(7,from[1],5,from[1]);
				else if((p instanceof King)&&(to[0]-from[0]==-2))
					b.setAt(0,from[1],3,from[1]);
				if((p instanceof Pawn)&&(to[1]==0||to[1]==7)){
					((Pawn)b.b[to[0]][to[1]]).actAs=new Queen(to[0],to[1],p.white);
					ListIterator<Piece> li=b.onBoard.listIterator();
					while(li.hasNext()){
						Piece cc=li.next();
						if(cc.location[0]==to[0]&&cc.location[1]==to[1]){
							((Pawn) cc).actAs=new Queen(cc.location[0],cc.location[1],cc.white);
							break;
						}
					}
				}
				if(taken!=null)
					this.getOtherBoard(id).toDrop.add(taken);

				cPlayer[id%2]=(id+2)%4;
				p.moved=true;

				synchronized(time){
					synchronized(timeStamp){
						time[id]-=System.currentTimeMillis()-timeStamp[id%2];
						timeStamp[id%2]=System.currentTimeMillis();
					}
				}


				//tell server what we did...
				if(id==this.associatedPlayer){
					System.out.println("wrote Object");
					out.writeObject(new PlayerMovePacket(from,to,id));
					if(taken!=null&&(taken instanceof King))
						out.writeObject(new KingTakenPacket((id+2)%4));
					out.flush();
				}
				//update this client side
				System.out.println("On board "+associatedPlayer+" Made move by "+id);
				bclient.change((id+2)%4,from,to,taken);
				return true;
			}
		return false;
	}

	public void reset() {
		this.gameInProgress=false;
		this.boards=new Board[]{new Board(),new Board()};
		this.cPlayer=new int[]{0,3};
		this.endCondition=null;
		this.time=new long[]{5*60*1000,5*60*1000,5*60*1000,5*60*1000};
		Game.this.bclient.panel.initVars();
	}

	public void checkIn(){
		try {
			out.writeObject((Boolean)true);
			out.flush();
			GameInformationPacket gip=(GameInformationPacket)in.readObject();
			names=gip.names;
			System.out.println("Game started with "+Arrays.toString(names));
			gameInProgress=true;
			timeStamp[0]=System.currentTimeMillis();
			timeStamp[1]=System.currentTimeMillis();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
