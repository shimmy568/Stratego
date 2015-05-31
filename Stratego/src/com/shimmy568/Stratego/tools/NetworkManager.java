package com.shimmy568.Stratego.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * the class that is the base for the host and the client network managers
 * @author Owen Anderson aka. Shimmy568
 *
 */
public class NetworkManager {

	public boolean setupComp, moveMade, challMade, DX, responce;
	final int PORT = 23432; //the port that the program uses to communicate
	
	//the objects used to communicate
	private PrintWriter out; 
	private BufferedReader input;
	
	//other varibles
	String msgRec;
	private String oppUsername;
	public boolean host;
	int reqType = -10;
	int[][] reqMove;
	
	/**
	 * just a blank constructer
	 */
	public NetworkManager() {
		reqMove = new int[2][2];
	}
	
	/**
	 * this method takes input and finds out what it's trying to say 
	 * @param status that message that was sent from the other player
	 */
	private void statusUpdate(String status){
		//this is ment for safty just incase the message is blank for some reason it doesn't cause problems
		if(status == null){
			return;
		}
		//the message the is for once the setup is complete
		if(status.startsWith("SETUPCOMPLETE")){
			setupComp = true;
			return;
		}
		//getting the username from the other player
		if(status.startsWith("USERNAME")){
			oppUsername = status.substring(9, status.length());
			return;
		}
		//when a challange has been made a request is made through this message
		if(status.startsWith("REQUEST")){
			
			reqMove[0][0] = Integer.parseInt(status.charAt(8) + "");
			reqMove[0][1] = Integer.parseInt(status.charAt(9) + "");
			reqMove[1][0] = Integer.parseInt(status.charAt(10) + "");
			reqMove[1][1] = Integer.parseInt(status.charAt(11) + "");
			reqType = Integer.parseInt(status.substring(12));
			challMade = true;
			return;
		}
		//the challange is responded to with this
		if(status.startsWith("RESPOND")){
			reqType = Integer.parseInt(status.substring(8));
			return;
		}
		//getting the move from the other player
		if(status.startsWith("MOVE")){
			reqMove[0][0] = Integer.parseInt(status.charAt(5) + "");
			reqMove[0][1] = Integer.parseInt(status.charAt(6) + "");
			reqMove[1][0] = Integer.parseInt(status.charAt(7) + "");
			reqMove[1][1] = Integer.parseInt(status.charAt(8) + "");
			moveMade = true;
			return;
		}
		//when someone wins this is called
		if(status.startsWith("HAHA I WIN")){
			DX = true;
			return;
		}
		
		System.out.println("Error invalid status");
	}

	/**
	 * send a message to the other player
	 * @param msg the message to be sent
	 */
	public void sendMsg(String msg){
		out.println(msg);
	}

	/**
	 * gets the username from this class once it's been gotten
	 * @return the username
	 */
	public String getUsername(){
		return oppUsername;
	}
	
	/**
	 * does the setup for the connection
	 * @param s the socket used for setup
	 */
	public void setup(Socket s){
		try {
			input = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new PrintWriter(s.getOutputStream(), true);
		} catch (IOException e) {
			System.out.println("Connection died: " + e);
		}
	}
	
	/**
	 * the method called by the UpdateThread used to get the messages from the other player
	 */
	public void getMsgrec(){
		try {
			msgRec = input.readLine();
			statusUpdate(msgRec);
		} catch (IOException e) {
			System.out.println("Connection died: " + e);
		}
	}
	
	/**
	 * the class that gets the messages from the other player
	 * @author Owen Anderson aka. Shimmy568
	 *
	 */
	class UpdateThread extends Thread {

		Socket socket;

		/**
		 * the constructer used to setup the connection
		 * @param s the socket used to setup the connection
		 */
		public UpdateThread(Socket s) {
			socket = s;
			setup(s);
			System.out.println("Waiting for connection...");
			
		}

		/**
		 * the run method thats used to update the input
		 */
		public void run() {
			// loops untill the game is over this processes the commands from
			// the opp and send our commands
			try {
				while (true) {
					getMsgrec();					
					
				}
			} finally {
				//once the loop has finished/crashed close the socket
				try {socket.close();} catch (IOException e) {}
			}
		}
	}

	/**
	 * the method that send the challange request to the other player
	 * @param pos the move that it made for the challange
	 * @param type the type of the peice that it used to challange
	 */
	public void sendReq(int[][] pos, int type) {
		sendMsg("REQUEST " + pos[0][0] + pos[0][1] + pos[1][0] + pos[1][1] + type);
	}
	
	/**
	 * gets the type of the peice that the attacking player challanged
	 * @return
	 */
	public int getType(){
		int t = reqType;
		reqType = -10;
		return t;
		
	}

	/**
	 * the method that sends a move to the other player
	 * @param movePos the move
	 */
	public void sendMove(int[][] movePos) {
		sendMsg("MOVE " + movePos[0][0] + movePos[0][1] + movePos[1][0] + movePos[1][1]);
	}
	
	/**
	 * a method for the player waiting for a move to be sent to check if a move has been made
	 * @return if a move has been made this = true
	 */
	public boolean isMoveMade(){
		moveMade = false;
		return moveMade;
	}
	
	/**
	 * gets the move that the other player sent
	 * @return the move that was made by the other player
	 */
	public int[][] getMove(){
		int[][] t = reqMove.clone();
		reqMove = new int[2][2];
		moveMade = false;
		return t;
	}
	
	/**
	 * gets that challange that the other player made
	 * @return
	 */
	public int[] getChall(){
		challMade = false;
		return new int[]{9 - reqMove[0][0], 9 - reqMove[0][1], 9 - reqMove[1][0], 9 - reqMove[1][1], reqType};
	}
}
