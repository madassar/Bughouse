import java.io.Serializable;


public class PlayerDropPacket implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 772320123251741519L;
	Piece p;
	int[] to;
	int id;
	public PlayerDropPacket(Piece p, int[] to, int id) {
		this.p=p;
		this.to=to;
		this.id=id;
	}

}
