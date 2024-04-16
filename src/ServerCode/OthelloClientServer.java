package ServerCode;


import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.Thread;

public class OthelloClientServer{
private ServerSocket serverSocket;
public OthelloClientServer(ServerSocket serverSocket) {
	this.serverSocket = serverSocket;
}
public void startServer() {
	try {
		while (!serverSocket.isClosed()) {
			Socket socket = serverSocket.accept();
			System.out.println("A new client is approaching!");
			OthelloClientHandler clientHandler = new OthelloClientHandler(socket);
			Thread thread = Thread(clientHandler);
			thread.start();
		}
	}
	catch(IOException e) {
		e.printStackTrace();
	}
}
public void closeServerSocket() {
	try {
		if (serverSocket != null){
			serverSocket.close();
		}
	}catch(IOException e) {
		e.printStackTrace();
	}
}
public static void main(String[] args) throws IOException{
	ServerSocket serverSocket = new ServerSocket(8888);
	OthelloClientServer server = new OthelloClientServer(serverSocket);
	server.startServer();
}
}