package ServerCode;

import java.io.*;
import java.net.*;
import java.util.*;

public class OthelloClientHandler implements Runnable{
//	If you want to add in viewers
	public static ArrayList<OthelloClientHandler> clientHandlers = new ArrayList<>();
	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String clientUserName;
	
	public OthelloClientHandler(Socket socket) {
		try {
			this.socket = socket;
			this.bufferedWriter = new BufferedWriter( new OutputStreamWriter(socket.getOutputStream()));
			this.bufferedReader = new BufferedReader( new InputStreamReader(socket.getInputStream()));
			if (clientHandlers.size() == 0) {
				this.clientUserName = "Player 1";
			}
			else if(clientHandlers.size() == 1){
				this.clientUserName = "Player 2";
			}
//			Start the game when the number of clients reaches 2
			clientHandlers.add(this);
		}
		
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
