import java.io.*;
import java.net.*;

class UDPRecieve {
	public static void main(String[] args) throws IOException {

		DatagramSocket datagramSocket = new DatagramSocket(9999);
		byte[] b = new byte[1024];
		DatagramPacket datagramPacket = new DatagramPacket(b, 1024);

		while (true) {
			datagramSocket.receive(datagramPacket);

			byte[] data = datagramPacket.getData();
			InetAddress address = datagramPacket.getAddress();
			String ip = address.getHostAddress();
			String string = new String(data, 0, datagramPacket.getLength());
			System.out.println("IP is " + ip + " " + string);
		}
	}
}