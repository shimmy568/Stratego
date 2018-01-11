package com.shimmy568.Stratego;

import org.newdawn.slick.Animation;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.shimmy568.Stratego.states.ConnectScreen;
import com.shimmy568.Stratego.states.EndGameLose;
import com.shimmy568.Stratego.states.EndGameWin;
import com.shimmy568.Stratego.states.Menu;
import com.shimmy568.Stratego.states.PlayGame;
import com.shimmy568.Stratego.tools.NetworkManager;

public class Game extends StateBasedGame{
	public static final int SCREENX = 532; //the varible used to hold the width of the window
	public static final int SCREENY = 572; //the varible used to hold the height of the window
	
	public static NetworkManager network; //the network manager that does both the host and the client connections

	/**
	 * the default constructer for StateBasedGame instences
	 * @param name the name of the window that will be created
	 */
	public Game(String name) { 
		super(name);
	}
	
	/**
	 * the main method that starts the game and sets things like width, height and FPS
	 * @param args
	 */
	public static void main(String[] args){
		try { //beucase making a new AppGameContainer throws SlickException we need a try catch statement to run the code
			AppGameContainer appgc = new AppGameContainer(new Game("Stratego")); //creates a new intences of AppGameContainer that holds the window and is used to start the game and set the settings for the window
			appgc.setTargetFrameRate(100); //set the max frame rate for the game 
			appgc.setShowFPS(false); //stops the deafault fps counter from showing in the top left corner
			appgc.setDisplayMode(SCREENX, SCREENY, false); //sets the screens width and height
			appgc.setIcons(new String[]{"img/misc/32.tga", "img/misc/24.tga", "img/misc/16.tga"}); //sets the icon that will appear for the window
			appgc.start(); //starts the game
		} catch (SlickException e) {
			e.printStackTrace(); 
		}
	}

	//adds the states that will be involved in the game so we can switch to them when needed
	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		addState(new Menu());
		addState(new ConnectScreen());
		addState(new PlayGame());
		addState(new EndGameWin());
		addState(new EndGameLose());
		
	}
	
	/**
	 * a method to create a animation object from a sprite sheet 
	 * @param spriteSheetPath the path to the sprite sheet so the Image object can be loaded
	 * @param width the width of each frame
	 * @param frames the number of frames in the animation
	 * @param delay the delay between frames
	 * @return the animation object
	 * @throws SlickException loading an Image throws SlickException
	 */
	public static Animation loadAnimation(String spriteSheetPath, int width, int frames, int delay) throws SlickException{
		//loads the sprite sheet and creates the animation object for the frames to be loaded to
		Animation a = new Animation(); 
		Image img = new Image(spriteSheetPath);
		
		//using the width to get the frames and load them to the animation
		for(int i = 0; i < frames; i++){
			a.addFrame(img.getSubImage(i * width, 0, width, img.getHeight()), delay);
		}
		
		return a;
	}

}
