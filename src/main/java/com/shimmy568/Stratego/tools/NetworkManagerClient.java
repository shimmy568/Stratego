package com.shimmy568.Stratego.tools;

import java.net.Socket;

/**
 * the class that the client uses to communicate with the host
 * @author Owen Anderson aka. Shimmy568
 *
 */
public class NetworkManagerClient extends NetworkManager{

	Socket s;
	
	/**
	 * the constructer used to setup the connection
	 * @param adress the ip that the client is attempting to connect to
	 * @throws Exception
	 */
	public NetworkManagerClient(String adress) throws Exception{
		super();
		host = false;
		
		s = new Socket(adress, PORT);
		setup(s);
		UpdateThread th = new UpdateThread(s);
		th.start();
	}
	
}
