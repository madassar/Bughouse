import java.io.Serializable;


public class PlayerMovePacket implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5912045134677297328L;
	int[] from,to;
	int id;
	public PlayerMovePacket(int[] from, int[] to, int id) {
		this.from=from;
		this.to=to;
		this.id=id;
	}

}
