import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;


public class Server {
	volatile Integer player=0;
	ArrayList<ObjectOutputStream> outs=new ArrayList<ObjectOutputStream>();
	ArrayList<String> names=new ArrayList<String>();
	ArrayList<Integer> serverIDToGameID=new ArrayList<Integer>();
	int[] gameIDToServerID=new int[]{-1,-1,-1,-1};
	long[] timeStamp=new long[2];
	long[] time=new long[4];
	int[] cPlayer=new int[]{0,3};
	volatile boolean gameOver=false;
	volatile Integer readyToRoll=0;
	volatile Integer inCurrentGame=0;
	public static void main(String[] args) throws IOException {
		new Server();
	}
	public Server() throws IOException{
		ServerSocket server=new ServerSocket(60010, 0, InetAddress.getByName("localhost"));
		new TimeKeeper(this).start();


		while(true){
			final Socket socket=server.accept();
			new Thread(){

				public void run() {

					//Initialization
					ObjectInputStream in=null;
					ObjectOutputStream out=null;
					String[] n=null;
					int thisplayer=-1;
					GameInitializationPacket gip=null;
					try{
						in=new ObjectInputStream(socket.getInputStream());
						out=new ObjectOutputStream(socket.getOutputStream());
						gip=(GameInitializationPacket)in.readObject();
						thisplayer=-1;
						synchronized(player){
							thisplayer=player++;
							synchronized(outs){
								outs.add(out);
								names.add(gip.playerName);
								serverIDToGameID.add(-1);
							}
						}
						System.out.println("wrote "+thisplayer+" in game: "+inCurrentGame);
						boolean inGame=false;
						while(!inGame){
							if(inCurrentGame<4)
								synchronized(inCurrentGame){
									if(inCurrentGame<4){
										int i;
										for(i=0;i<4;i++)
											if(gameIDToServerID[i]==-1)
												break;
										gameIDToServerID[i]=thisplayer;
										serverIDToGameID.set(thisplayer, i);
										out.writeObject((Object)(i));
										out.flush();
										inCurrentGame++;
										inGame=true;
									}
								}
						}

						System.out.println("waiting for more players...");
						while(inCurrentGame<4);
						System.out.println("all players here");
						n=new String[]{names.get(gameIDToServerID[0]),names.get(gameIDToServerID[1]),names.get(gameIDToServerID[2]),names.get(gameIDToServerID[3])};
						out.writeObject(new GameInformationPacket(n));
						out.flush();
					}catch(IOException | ClassNotFoundException e){
						e.printStackTrace();
					}
					//Game loop
					boolean doubleCheck=false;
					Object read=null;

					do{
						try{
							do{
								if(doubleCheck)
									doubleCheck=false;
								else
									read=in.readObject();
							}while(!(read instanceof Boolean));
							synchronized(readyToRoll){
								System.out.println("checking in");
								gameOver=false;
								readyToRoll++;
								if(readyToRoll==4){
									synchronized(time){
										timeStamp[0]=System.currentTimeMillis();
										timeStamp[1]=timeStamp[0];
										for(int i=0;i<4;i++)
											time[i]=5*60*1000;
									}
									System.out.println("all checked in!");
								}
							}
							while(readyToRoll<4);
							n=new String[]{names.get(gameIDToServerID[0]),names.get(gameIDToServerID[1]),names.get(gameIDToServerID[2]),names.get(gameIDToServerID[3])};
							out.writeObject(new GameInformationPacket(n));
							out.flush();

							do{
								read=in.readObject();
								if(readyToRoll<4){
									doubleCheck=true;	//potentially captured check-in 'True' packet.
									break;
								}
								System.out.println("Object received from "+thisplayer+" playing as "+serverIDToGameID.get(thisplayer));
								if(!gameOver)
									synchronized(outs){

										time[serverIDToGameID.get(thisplayer)]-=System.currentTimeMillis()-timeStamp[serverIDToGameID.get(thisplayer)%2];
										timeStamp[serverIDToGameID.get(thisplayer)%2]=System.currentTimeMillis();
										cPlayer[serverIDToGameID.get(thisplayer)%2]=(serverIDToGameID.get(thisplayer)+2)%4;
										for(int i=0;i<4;i++)
											if(!gameOver)
												if(i!=serverIDToGameID.get(thisplayer)){
													outs.get(gameIDToServerID[i]).writeObject(read);
													outs.get(gameIDToServerID[i]).flush();
												}
										if(read instanceof KingTakenPacket){
											readyToRoll=0;
											gameOver=true;
											outs.get(thisplayer).writeObject(read);
											outs.get(thisplayer).flush();
										}else if(read instanceof PlayerTerminatedPacket){
											readyToRoll=0;
											gameOver=true;
										}
									}

							}while(!gameOver);
						}catch(IOException | ClassNotFoundException e){
							synchronized(outs){
								synchronized(inCurrentGame){
									System.out.println("Player "+thisplayer+"/"+inCurrentGame+" disconnected.");
									inCurrentGame--;
									outs.set(thisplayer, null);
									gameIDToServerID[serverIDToGameID.get(thisplayer)]=-1;
									serverIDToGameID.set(thisplayer, -1);
									break;
								}
							}
						}
					}while(!socket.isClosed());
					try {
						in.close();
						out.close();
						if(!socket.isClosed())
							socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


				}
			}.start();
		}
	}

}
class TimeKeeper extends Thread{
	Server s;
	public TimeKeeper(Server s){
		this.s=s;
	}
	public void run() {
		long counter=1;
		while(true){
			while(s.readyToRoll<4);
			while(!s.gameOver){
				try {
					Thread.sleep(50);
					synchronized(s.time){
						long[] passback=new long[4];
						for(int i=0;i<4;i++)
							if(i==s.cPlayer[i%2]){
								passback[i]=s.time[i]-System.currentTimeMillis()+s.timeStamp[i%2];
								if(passback[i]<0){
									synchronized(s.outs){
										TimeExpiredPacket tep=new TimeExpiredPacket(i);
										for(int j=0;j<4;j++){
											s.outs.get(s.gameIDToServerID[j]).writeObject(tep);
											s.outs.get(s.gameIDToServerID[j]).flush();
										}
									}
									s.readyToRoll=0;
									s.gameOver=true;
									break;
								}
							}else
								passback[i]=s.time[i];
						if(!s.gameOver&&counter++%20==0)
							synchronized(s.outs){
								if(!s.gameOver){
									TimeUpdatePacket tup=new TimeUpdatePacket(passback);
									for(int i=0;i<4;i++){
										s.outs.get(s.gameIDToServerID[i]).writeObject(tup);
										s.outs.get(s.gameIDToServerID[i]).flush();
									}
								}
							}
					}
				} catch (InterruptedException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}