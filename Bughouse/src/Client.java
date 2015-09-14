import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;


public class Client {

	public static void main(String[] args) throws UnknownHostException, IOException {
		String ip=JOptionPane.showInputDialog("What is the server IP address?");
		Socket client=new Socket(ip,60010);
		BufferedReader input=new BufferedReader(new InputStreamReader(client.getInputStream()));
		DataOutputStream output=new DataOutputStream(client.getOutputStream());
		output.writeBytes("Hello, wazzap\n");
		output.flush();
		String data;
		while ((data = input.readLine()) != null) {
            System.out.println("Server: " + data);
            if (data.indexOf("Ok") != -1) {
              break;
            }
        }
	}

}
