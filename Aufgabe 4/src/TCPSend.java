import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

class TCPSend {

	BufferedReader inFromUser;
	BufferedReader inFromServer;
	DataOutputStream outToServer;
	Socket clientSocket;
	
	public TCPSend() {
		try {
			clientSocket = new Socket("license-server.svslab", 1337);
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} 
		catch (Exception e) {
		}
	}
	
	public void sendLine(String text)
	{
		try {
			String send = text +"\n";
			System.out.print(send); 
			outToServer.writeBytes(send);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String readLine()
	{
		String s = "";
		try {
			s =  inFromServer.readLine();
			while (s.length() < 1) {
				s += inFromServer.readLine();
			}
		} catch (Exception e) {
		}
		return s;
	}
}