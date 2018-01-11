package com.shimmy568.Stratego.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.shimmy568.Stratego.Game;
import com.shimmy568.Stratego.tools.Button;

/**
 * the menu that displays at the start of the game
 * @author Owen Anderson aka. Shimmy568
 *
 */
public class Menu extends BasicGameState{

	//makes the buttons and images
	Image head, redFlag, blueFlag;
	Button play, exit;
	boolean down;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		//loads the images
		head = new Image("img/menu/Header.png");
		redFlag = new Image("img/menu/RedFlagMenu.png");
		blueFlag = new Image("img/menu/BlueFlagMenu.png");
		
		//loads the buttons
		play = new Button("img/menu/PlayGame");
		exit = new Button("img/menu/Exit");
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		//draws the images
		head.drawCentered(Game.SCREENX / 2, 60);
		g.drawImage(redFlag, 10, 10);
		g.drawImage(blueFlag, Game.SCREENX - 85, 10);
		
		//draws the buttons
		play.draw(g, (Game.SCREENX - play.getWidth()) / 2, 150);
		exit.draw(g, (Game.SCREENX - play.getWidth()) / 2, 350);
		
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		//this makes sure that it only enters the connect screen if the mouse button is up as to not mistrigger one of the buttons in that state
		if(play.update(container.getInput()) || down){
			down = true;
			if(!container.getInput().isMouseButtonDown(0)){
				down = false;
				game.enterState(1);
			}
		}
		//checks for the exit button
		if(exit.update(container.getInput())){
			System.exit(0);
		}
		
	}
	

	@Override
	public int getID() {
		return 0;
	}

}
