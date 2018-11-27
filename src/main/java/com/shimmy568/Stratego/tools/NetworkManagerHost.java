package com.shimmy568.Stratego.tools;

import java.net.ServerSocket;


/**
 * the class that the host uses to wait for a connection 
 * @author Owen Anderson aka. Shimmy568
 *
 */
public class NetworkManagerHost extends NetworkManager {
	

	// This class is to be run on the hosting player's computer

	// this gets the connection setup and ready to go
	public NetworkManagerHost() throws Exception {
		super();
		host = true;

		ServerSocket list = new ServerSocket(PORT);

		try {
			// gets the connection from the other player and starts the
			// HostThread
			UpdateThread th = new UpdateThread(list.accept());
			th.start();
		} finally {
			list.close();
		}

	}
}
