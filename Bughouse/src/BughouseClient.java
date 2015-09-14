import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ListIterator;

import javax.swing.JFrame;
import javax.swing.Timer;


public class BughouseClient extends JFrame implements WindowListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1429217534551098073L;

	Game g;
	ClientPanel panel;
	public BughouseClient(Game g) throws IOException{
		this.setSize(19*40+10,760);
		
		this.g=g;
		panel=new ClientPanel(this);
		panel.setPreferredSize(new Dimension(19*40+10,760));
		this.add(panel);
		
		Timer t=new Timer(50, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				BughouseClient.this.repaint();
			}
		});
		
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		this.addWindowListener(this);
		this.setVisible(true);
		t.start();
	}
	

	
	public void change(int cPlayer,int[] source,int[] sink,Piece captured){
		if(g.associatedPlayer==cPlayer&&captured!=null){
			ListIterator<GPiece> li=this.panel.onBoard.listIterator();
			while(li.hasNext()){
				
				GPiece gp=li.next();
				if(gp.boardX==sink[0]&&gp.boardY==sink[1]){
					li.remove();
					break;
				}
			}
		}else if(g.associatedPlayer==(cPlayer*3+3)%4&&captured!=null)
			this.panel.toDrop.add(new GPiece(this.panel.pmap.get(captured.pieceID), captured, this.panel));

		panel.repaint();
	}



	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void windowClosing(WindowEvent e) {
		try {
			g.out.writeObject(new PlayerTerminatedPacket(g.associatedPlayer));
			g.out.flush();
		} catch (IOException e4) {}
		try {
			g.in.close();
		} catch (IOException e3) {}
		try {
			g.out.close();
		} catch (IOException e2) {}
		try {
			g.client.close();
		} catch (IOException e1) {}
	}



	@Override
	public void windowClosed(WindowEvent e) {
		
	}



	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}





}
