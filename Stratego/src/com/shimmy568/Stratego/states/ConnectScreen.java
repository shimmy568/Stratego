package com.shimmy568.Stratego.states;

import java.net.BindException;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.shimmy568.Stratego.Game;
import com.shimmy568.Stratego.tools.Button;
import com.shimmy568.Stratego.tools.NetworkManagerClient;
import com.shimmy568.Stratego.tools.NetworkManagerHost;

/**
 * the state to connect to the other player 
 * @author Owen Anderson aka. Shimmy568
 *
 */
public class ConnectScreen extends BasicGameState{

	//declare the textfeilds, buttons, loading animation and the connecter thread
	TextField tfIP, tfUsername;
	Button client, host, back;
	Animation pacmanLoading;
	Thread connecter;
	
	//other varibles used in multiple methods
	int state = 0, timeToStart;
	boolean connecting, startGame, enterPressed, clicked, twoRunning;
	String username;
	
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		//inits the text feilds used to get the IP and the usernames
		tfIP = new TextField(container, container.getDefaultFont(), 100, 100, 175, 20);
		tfIP.setLocation((Game.SCREENX - tfIP.getWidth()) / 2, 200); 
		tfIP.setText("IP"); 
		tfUsername = new TextField(container, container.getDefaultFont(), 100, 100, 175, 20);
		tfUsername.setLocation((Game.SCREENX - tfUsername.getWidth()) / 2, (Game.SCREENY - tfUsername.getHeight()) / 2);
		
		//initing the buttons 
		client = new Button("img/connectScreen/ClientConnect");
		host = new Button("img/connectScreen/HostConnect");
		back = new Button("img/menu/Back");
		
		//loading the Animation useing the method created in the Game class
		pacmanLoading = Game.loadAnimation("img/connectScreen/LoadingSheet.png", 24, 9, 50);
		pacmanLoading.stop();
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)//the method where things are drawn to the screen
			throws SlickException {
		//i break down the stages of connect screen into states that it enters depending on the value in the state int
		if(state == 1){ //the state that gets weather the user wants to be host or client
			client.draw(g, (Game.SCREENX - client.getWidth()) / 2, 150);
			host.draw(g, (Game.SCREENX - host.getWidth()) / 2, 250);
		}else if(state == 2){ //the state that is for the client 
			g.drawString("Enter Host IP",(Game.SCREENX - 118) / 2 , 150);
			tfIP.render(container, g);
		}else if(state == 3){ // the state for the host
			if(twoRunning)
					g.drawString("Port in use", (Game.SCREENX - g.getFont().getWidth("Port in use")) / 2, (Game.SCREENY - g.getFont().getHeight("aA")) / 2);
		}else{//this is a sort of state 0 for the username but this was more efficent
			tfUsername.render(container, g);
			g.drawString("Enter Username", (Game.SCREENX - 128) / 2, tfUsername.getY() - 30);
		}
		
		//if the player in connecting show the loading animation
		if(connecting){
			pacmanLoading.draw((Game.SCREENX - 24) / 2, 250);
			pacmanLoading.start();
		}else{//if not connection stop it
			pacmanLoading.stop();
		}
		
		//startGame is true when the both players have been connected and the game is to start
		if(startGame){
			g.drawString("Connected to " + Game.network.getUsername(), (Game.SCREENX - g.getFont().getWidth("Connected to " + Game.network.getUsername())) / 2, 50);
		}
		
		back.draw(g, (Game.SCREENX - back.getWidth()) / 2, Game.SCREENY - 70);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		if(clicked){ //this is to ensure that the back button when clicked doesn't also click the next one after it's gone back
			if(!container.getInput().isMouseButtonDown(0)){
				clicked = false;
			}else{
				return;
			}
		}
		
		
		if(state == 1){ //the state that gets weather the user wants to be host or client
			if(client.update(container.getInput())){ //updating and checking the button for the client
				state = 2;
			}
			
			if(host.update(container.getInput())){ //the host button
				state = 3;
			}
			
			if(back.update(container.getInput())){ //the back button
				clicked = true;
				state = 0;
			}
		}else if(state == 2){ //the state that is for the client 
			//when enter is pressed get the info from the textfeild and attempt to connect to the IP
			if(container.getInput().isKeyDown(Input.KEY_ENTER) && !connecting && !enterPressed){
				connecter = new Thread(new Connecter(tfIP.getText(), "Player 2"));
				connecter.start();
				connecting = true;
				enterPressed = true;
			}
			
			if(back.update(container.getInput())){ //button to go back a step and stop the connecter Thread
				state = 1;
				clicked = true;
				connecter.interrupt();
				connecting = false;
			}
		}else if(state == 3){ // the state for the host 
			if(!connecting && !twoRunning){ //checks if the connecting process has started and if not starts it
				connecter = new Thread(new Connecter("Player 1"));
				connecter.start();
				connecting = true;
			}
			
			//the back button also needs to stop the connecter Thread
			if(back.update(container.getInput())){
				connecter.interrupt();
				state = 1;
				clicked = true;
				connecting = false;
				twoRunning = false;
			}

		}else{ //this agians acts as the 0 state the same as the render method
			if(container.getInput().isKeyDown(Input.KEY_ENTER) && tfUsername.getText() != null){
				state = 1;
				username = tfUsername.getText();
				tfUsername.setText("");
			}
			
			if(back.update(container.getInput()) && state == 0){
				tfUsername.setText("");
				game.enterState(0);
			}
		}
		
		if(enterPressed && !container.getInput().isKeyDown(Input.KEY_ENTER)){
			enterPressed = false;
		}
		
		//waits 2 seconds after establising the connection to go to the PlayGame state
		if(startGame){
			timeToStart += delta;
			if(timeToStart > 2000){
				game.enterState(2);
			}
		}
	}
	
	//if the client couldn't connect the the server we put a message in the textfeild and stop the loading icon
	public void timedOut(){
		connecting = false;
		if(state == 2){
			tfIP.setText("Couldn't Connect");
		}
		pacmanLoading.stop();
	}
	
	//once connected to the other player we send the username using this method
	public void connected() {
		Game.network.sendMsg("USERNAME " + username);
		startGame = true;
		connecting = false;
	}

	
	@Override
	public int getID() {
		return 1; //the id of the state is 1 (duh)
	}

	/**
	 * a class to handle the connect to the client / host as i want to display a loading wheel during this process 
	 * running it on the main thread would cause this to be impossible
	 * @author owen
	 *
	 */
	class Connecter implements Runnable{
		
		String ip, username;
		
		/**
		 * the constructer for the client's version of the thread
		 * @param ip the ip that we will attempt to connect to
		 * @param username the username of the client
		 */
		public Connecter(String ip, String username){
			this.ip = ip;
			this.username = username;
		}
		
		/**
		 * the constructer for the host's Thead
		 * @param username the username for the host player
		 */
		public Connecter(String username){
			this.username = username;
		}
		
		/**
		 * the run method of this thread where the NetworkManager is made
		 */
		public void run(){
			//if an ip was entered (only for the client) than a new NetworkManagerClient is made if it is null a new NetworkManagerHost instead
			if(ip == null){				
				try {
					Game.network = new NetworkManagerHost();
					connected();
				} catch(BindException e){
					twoRunning = true;
					timedOut();
				}catch (Exception e) { //if an error occurs we time out and exit
					e.printStackTrace();
					timedOut();
				} 
			}else{
				try {
					Game.network = new NetworkManagerClient(ip);
					connected();
				} catch (Exception e) { //if an error occurs we time out and exit
					e.printStackTrace();
					timedOut();
				}
			}
		}
	}
}
