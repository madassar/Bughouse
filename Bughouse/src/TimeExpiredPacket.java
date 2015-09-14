import java.io.Serializable;


public class TimeExpiredPacket implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1573397832147798864L;
	int playerID;
	public TimeExpiredPacket(int playerID){
		this.playerID=playerID;
	}

}
class KingTakenPacket implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 961646129889848044L;
	int playerID;
	public KingTakenPacket(int takenID) {
		this.playerID=takenID;
	}

}
class PlayerTerminatedPacket implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 518958637905038076L;
	int playerID;
	public PlayerTerminatedPacket(int playerID) {
		this.playerID=playerID;
	}

}
