import java.io.Serializable;


public class GameInitializationPacket implements Serializable{
	String playerName;
	String partnerName;
	public GameInitializationPacket(String playerName, String partnerName) {
		this.playerName=playerName;
		this.partnerName=partnerName;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8659089905256479252L;

}
class GameInformationPacket implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3739100911914543519L;
	String[] names;
	public GameInformationPacket(String[] names) {
		this.names=names;
	}



}