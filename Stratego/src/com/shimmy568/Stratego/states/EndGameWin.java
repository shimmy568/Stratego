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
 * the that displays to the winning player
 * @author Owen Anderson aka. Shimmy568
 *
 */
public class EndGameWin extends BasicGameState{

	//banner for the winner
	Image victory;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		//loads the banner
		victory = new Image("img/misc/victory.png");
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		//draws the banner and the text telling the user to press enter
		g.drawImage(victory, (Game.SCREENX - victory.getWidth()) / 2, (Game.SCREENY - victory.getHeight()) / 2);
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
		return 3;
	}

}
