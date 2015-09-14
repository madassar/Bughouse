import java.io.Serializable;


public class TimeUpdatePacket implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2144503665771055758L;
	long[] time;
	public TimeUpdatePacket(long[] time){
		this.time=time;
	}
}
