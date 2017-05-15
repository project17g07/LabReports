import java.io.*;
import java.net.*;

//Source https://systembash.com/a-simple-java-tcp-server-and-tcp-client/
class TCPServer {
 public static void main(String argv[]) throws Exception {
  String clientSentence;
  ServerSocket welcomeSocket = new ServerSocket(1337);

  while (true) {
   Socket connectionSocket = welcomeSocket.accept();
   BufferedReader inFromClient =
    new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
   DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
   clientSentence = inFromClient.readLine();
   System.out.println("Received: " + clientSentence);
   outToClient.writeBytes("SERIAL_VALID=1");
  }
 }
}