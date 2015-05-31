package com.shimmy568.Stratego.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.shimmy568.Stratego.Game;

/**
 * the that displays to the losing player
 * @author Owen Anderson aka. Shimmy568
 *
 */
public class EndGameLose extends BasicGameState{

	//the banner that shows to the losing player more effient to make a new image instead of the font route
	Image defeat; 
	
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		//loads the banner
		defeat = new Image("img/misc/defeat.png");
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		//draws the banner and the text telling the user to press enter
		g.drawImage(defeat, (Game.SCREENX - defeat.getWidth()) / 2, (Game.SCREENY - defeat.getHeight()) / 2);
		g.drawString("Press Enter to Exit...", 170 , 330);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		//checks if the user has pressed enter and if so exits the game
		if(container.getInput().isKeyDown(Input.KEY_ENTER)){
			container.exit();
		}
	}

	@Override
	public int getID() {
		return 4;
	}

}
