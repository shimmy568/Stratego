package com.shimmy568.Stratego.states;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.shimmy568.Stratego.Game;
import com.shimmy568.Stratego.tools.Peice;

public class PlayGame extends BasicGameState{

	Peice[][] board; //the board that hold the peices
	Peice[][] boardTemp; //a temp board used for showing the other player's peice during a challange
	Image[][] peices; //the images for all the peices red and blue
	Color highlightColor; //the color that the square that the mouse is in will be colored
	int state; //keeps track of what stage the game is in
	int[] mouseCord = new int[2]; //keeps track of the mouse's x,y pos
	int[] sideBarCounter = new int[12]; //in the placing stage this keeps track of how many peices still need to be placed and in the main game part it keeps track of how many peices you have captured
	int[][] movePos = new int[2][2]; //keeps track of the move that the player made
	
	//other ints and booleans used throughout the code
	int sideClicked = -1, playerColor, oppColor, time;
	boolean clicked, clickedR, makingMove, waitingForResponce, challR;
	
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		//inits the board and loads the peices images
		initBoard();
		loadPeicesImg();
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		//if a peice is being revealed during a challange than instead of drawing the normal board draw 
		//the temp one
		if(challR){
			drawBoard(g, boardTemp);
			drawSideBar(g, oppColor);
			return;
		}
		//draws the board and the highlight
		drawBoard(g, board);
		drawHighlight(g, mouseToBoardCord(mouseCord), highlightColor);
		
		//draws diffrent thing depending on what stage the game is in 
		switch(state){
		case 0:
			placingStateRender(g);
			break;
		case 2:
		case 3:
			playGameRender(g); //the same thing draws for both stage 2 and 3
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		//reveals the peice for 1 seconds before hiding it agian
		if(challR){
			time += delta;
			if(time > 1000){
				challR = false;
				time = 0;
			}
			return;
		}
		
		//if the game has been lost go to the EndGameLose state
		if(Game.network.DX){
			game.enterState(4);
		}
		
		//if you capture the flag you tell the other player you win and enter the winning state
		if(sideBarCounter[0] == 1 && state != 0){
			Game.network.sendMsg("HAHA I WIN");
			game.enterState(3);
		}
		
		setMouse(container.getInput()); //getting mouse input
		
		//calls the diffrent update methods for each state
		switch(state){
		case 0: //placing the peices
			placingStateUpdate(container);
			break;
		case 1: //waiting for other player to place
			waitingForPlacement();
			break;
		case 2: //player's turn
			makeTurn(container);
			break;
		case 3:
			waitForTurn();
			break;
		}
		
		
	}


	@Override
	public int getID() {
		return 2;
	}
	
	/**
	 * draws the elements on the board that will remain static throughout the game
	 * @param g Graphics for drawing the things
	 */
	public void drawBoard(Graphics g, Peice[][] b){
		//draws the lines on the b
		for(int i = 0; i < 11; i++){
			g.drawLine(48 * i, 0, 48 * i, 480);
			g.drawLine(0, 48 * i, Game.SCREENX, 48 * i);
		}
		//the water (changed values so water rect would fit within the b lines)
		g.setColor(Color.blue);
		g.fillRect(97, 193, 94, 94);
		g.fillRect(289, 193, 94, 94);
		g.setColor(Color.white);
		
		//draws the thicker line on the right side of the b
		g.fillRect(480, 0, 3, Game.SCREENY);
		g.drawLine(480, 528, Game.SCREENX, 528);
		g.drawLine(480, 572, Game.SCREENX, 572);
		
		//draws the peices on the b
		for(int x = 0; x < 10; x++){
			for(int y = 0; y < 10; y++){
				if(b[x][y].isFilled() && b[x][y].getType() != 15){
					if(b[x][y].getColor() == -1){
						continue;
					}
					g.drawImage(peices[b[x][y].getColor()][b[x][y].getType()], x * 48 + 6, y * 48 + 6);
				}
			}
		}
		//if the state isn't the placing state make it draw the opponent's color instead of your own
		if(state != 1){
			for(int i = 0; i < 12; i++){
				g.drawString("" + sideBarCounter[i], 483, i * 48);
			}
		}
	}
	
	/**
	 * by this time we know the move is valid so we only need to move the peice from point a to b
	 * @param startX the peice that we are moving's x cord
	 * @param startY the peice that we are moving's y cord
	 * @param endX the ending x cord
	 * @param endY the ending y cord
	 */
	private boolean movePeice(int startX, int startY, int endX, int endY){
		//if the spot is filled send a challange if not just more to it
		if(!board[endX][endY].filled){
			System.out.println("sexy");
			board[endX][endY].setTo(board[startX][startY]);
			board[startX][startY].clearSpot();
			return true;
		}else{
			waitingForResponce = true; 
			Game.network.sendReq(movePos, board[movePos[0][0]][movePos[0][1]].getType());
			return false;
		}
	}
	
	/**
	 * a method to take in the mouse position and find what square on the board to mouse is over
	 * @param cord a int list with 2 values the first being the x of the mouse and the second being the y
	 * @return the square that the mouse is over will be between 0 and 9 inclusivly
	 */
	private int[] mouseToBoardCord(int[] cord){
		if(cord[0] >= 480 || cord[1] >= 480){
			return new int[]{-2, -2};
		}else{
			return new int[]{cord[0] / 48, cord[1] / 48};
		}
	}
	
	//just a vareation of the above method for the sidebar of the game
	private int mouseToSideCord(int[] cord){
		if(cord[0] > 483){
			return (cord[1] - 3) / 48;
		}
		return -2;
	}
	
	/**
	 * a method to draw a highlight around a box when the mouse is over it
	 * @param g the graphics object from render method
	 * @param cord the cord of the box gotten from mouseToBoardCord
	 * @param c the color of the highlight
	 */
	private void drawHighlight(Graphics g, int[] cord, Color c){
		Color tc = g.getColor();
		g.setColor(c);
		g.drawRect(cord[0] * 48, cord[1] * 48, 48, 48);
		g.setColor(tc);
	}
	
	//just a vareation of the above method for the sidebar of the game
	private void drawHighlight(Graphics g, int cord, Color c){
		Color tc = g.getColor();
		g.setColor(c);
		g.drawRect(483, cord * 48, 48, 48);
		g.setColor(tc);
	}
	
	/**
	 * a method to get and store the mouses cord
	 * @param i the input object
	 */
	private void setMouse(Input i){
		mouseCord[0] = i.getMouseX();
		mouseCord[1] = i.getMouseY();
	}
	
	/**
	 * a method to init the board used to store peices
	 */
	private void initBoard(){
		//inits the boards and fills it along with the temp one
		board = new Peice[10][10];
		boardTemp = new Peice[10][10];
		for(int x = 0; x < 10; x++){
			for(int y = 0; y < 10; y++){
				board[x][y] = new Peice(x, y);
				boardTemp[x][y] = new Peice(x, y);
			}
		}
		//set the water for the board
		board[2][4].filled = true;
		board[3][4].filled = true;
		board[2][5].filled = true;
		board[3][5].filled = true;
		
		board[6][4].filled = true;
		board[7][4].filled = true;
		board[6][5].filled = true;
		board[7][5].filled = true;
		
		board[2][4].setType(15);
		board[3][4].setType(15);
		board[2][5].setType(15);
		board[3][5].setType(15);
		
		board[6][4].setType(15);
		board[7][4].setType(15);
		board[6][5].setType(15);
		board[7][5].setType(15);
		
		
		//sets the sideBarCounter array for the setup phase
		sideBarCounter[0] = 1;
		sideBarCounter[1] = 1;
		sideBarCounter[2] = 8;
		sideBarCounter[3] = 5;
		sideBarCounter[4] = 4;
		sideBarCounter[5] = 4;
		sideBarCounter[6] = 4;
		sideBarCounter[7] = 3;
		sideBarCounter[8] = 2;
		sideBarCounter[9] = 1;
		sideBarCounter[10] = 1;
		sideBarCounter[11] = 6;
	}
	
	/**
	 * the methods for rendering and update the stage in the game where each player places his/her peices
	 * @param c the game container from update
	 */
	private void placingStateUpdate(GameContainer c){
		int[] boardCord = mouseToBoardCord(mouseCord);
		getPlayerColor();
		
		//when the left mouse button is down place the peice and lower the respective counter by one
		//also i put in a thing which makes you need to click once per square instead of being able
		//to "paint" the peices along the board
		if(c.getInput().isMouseButtonDown(0) && !clicked){
			clicked = true;
			//if the mouse is on the side bar it selects the peice
			if(mouseCord[0] > 483 && sideBarCounter[mouseToSideCord(mouseCord)] != 0){
				sideClicked = mouseToSideCord(mouseCord);
			}else if(boardCord[1] > 5 && sideClicked != -1 && !board[boardCord[0]][boardCord[1]].isFilled()){ //makes sure that you have a peice selected, the space is not allready filled and that you are on your side
				board[boardCord[0]][boardCord[1]].place(playerColor, sideClicked);
				//if all the peices ran out deselect the peice from the sidebar
				if(--sideBarCounter[sideClicked] == 0){
					sideClicked = -1;
				}
			}
		}else if(c.getInput().isMouseButtonDown(1) && !clickedR){ //if rightclick remove the piece and add back to the counter
			clickedR = true;
			if(boardCord[1] > 5 && board[boardCord[0]][boardCord[1]].isFilled()){
				sideBarCounter[board[boardCord[0]][boardCord[1]].getType()]++;
				board[boardCord[0]][boardCord[1]].clearSpot();
			}
		}else {
			//waits till you lift the button to let you place/remove another peice
			if(clicked && !c.getInput().isMouseButtonDown(0)){
				clicked = false;
			}
			if(clickedR && !c.getInput().isMouseButtonDown(1)){
				clickedR = false;
			}
		}
		//if you haven't selected a peice or the spot is invalid set the highlight to red
		if(sideClicked != -1 && boardCord[1] > 5 && !board[boardCord[0]][boardCord[1]].isFilled()){
			highlightColor = Color.green;
		}else{
			highlightColor = Color.red;
		}
		
		//if any of the peices still need to be placed continue if not exit this state
		for(int i = 0; i < 12; i++){//this must be at the bottom of this method
			if(sideBarCounter[i] != 0){
				return;
			}
		}
		donePlacing();
	}
	

	/**
	 * rendering for the placing stage of the game
	 * @param g the graphics object from the render method
	 */
	private void placingStateRender(Graphics g){
		//highlight the peice on the sidebar that you have selected blue
		if(sideClicked != -1){
			drawHighlight(g, sideClicked, Color.blue);
		}
		
		drawSideBar(g, playerColor);
		
		drawHighlight(g, mouseToSideCord(mouseCord), Color.green);
	}
	
	/**
	 * loads the images for the peices
	 */
	private void loadPeicesImg(){
		//inits the list and creates the image that will hold the sprite sheet
		peices = new Image[2][13];
		Image sheet;
		
		//loads the sprite sheet and splits it into the list
		try {
			sheet = new Image("img/playGame/sheet.png");
			for(int i = 0; i < 13; i++){
				peices[0][i] = sheet.getSubImage(36 * i, 0, 36, 36);
				peices[1][i] = sheet.getSubImage(36 * i + 468, 0, 36, 36);
			}
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * gets what color the player is and puts it in the playerColor and oppColor varibles
	 */
	private void getPlayerColor(){
		if(Game.network.host){
			playerColor = 0;
			oppColor = 1;
		}else{
			playerColor = 1;
			oppColor = 0;
		}
	}
	
	/**
	 * this runs when the player is done placing all their peices in the first stage
	 * and lets the other player know that they have done so
	 */
	private void donePlacing() {
		Game.network.sendMsg("SETUPCOMPLETE");
		state = 1;
	}
	
	/**
	 * this is one of the updates for states and checks if the other player has finished placing
	 * and if they have depending on weather they are blue or red it sets to move state or
	 * waiting for turn state
	 */
	private void waitingForPlacement() {
		//setupComp is set to true when the other player has placed their peices
		if(Game.network.setupComp){
			//places all the blank peices on the other side of the board
			for(int x = 0; x < 10; x++){
				for(int y = 0; y < 4; y++){
					board[x][y].place(oppColor, 12);
				}
			}
			//sets the state for making a move
			if(playerColor == 0){
				state = 2;
			}else{
				state = 3;
			}
		}
	}

	/**
	 * this waits for the other player to make a turn and updates any moves/challenges that
	 * the other player made
	 */
	private void waitForTurn() {
		
		highlightColor = Color.red; //highlight is allways set to red in this stage
		
		//this is true if the player has made a move
		if(Game.network.moveMade){
			int[][] move = Game.network.getMove(); //gets the move from the network manager
			movePeice(9 - move[0][0], 9 - move[0][1], 9 - move[1][0], 9 - move[1][1]); //move the peice inverted as the board cords are flipped for each player
			state = 2; //sets the stage to moveing
		}else if(Game.network.challMade){ //this is true if they have made a challange
			int[] chall = Game.network.getChall(); //gets the challange that the other player made
			int[][] chall2D = new int[][]{{chall[0], chall[1]}, {chall[2], chall[3]}};
			Game.network.sendMsg("RESPOND " + board[chall[2]][chall[3]].getType()); //sends the type of the peice that they challanged
			int result = Peice.challange(board, chall[2], chall[3], chall[4]); //finds the result of the challange the the player made
			
			//this sets the sideBarCounter up one if the captured a peice
			if(result == 1 || result == 2){
				sideBarCounter[chall[4]] += 1;
			}
			//reveals the peice and updates the board
			reveal(chall[4], chall[0], chall[1]);
			challHandle(chall2D, result, false);
			state = 2;
		}
	}

	/**
	 * this runs when it's the player's turn to make a move
	 * @param c the game container from the update method used to get input
	 */
	private void makeTurn(GameContainer c) {
		//if the player has made a challange and is waiting on the other player to send the type back this runs
		if(waitingForResponce){
			//gets the type from the network manager (if nothing was sent it is -10)
			int oppStr = Game.network.getType();
			if(oppStr != -10){
				//if something was sent this runs to make update the board and reveal
				int result = Peice.challange(board, movePos[0][0], movePos[0][1], oppStr);
				if(result == 1 || result == 2){
					sideBarCounter[oppStr] += 1;
				}
				reveal(oppStr, movePos[1][0], movePos[1][1]);
				challHandle(movePos, result, true);
				waitingForResponce = false;
				state = 3;
			}
			return;
		}
		//if a move isn't possible to make on the current move location it turns red
		if(!movePossible() && !makingMove){
			highlightColor = Color.red;
		}else if(!Peice.isValidMove(board, movePos[0][0], movePos[0][1], mouseToBoardCord(mouseCord)[0], mouseToBoardCord(mouseCord)[1]) && makingMove){ 
			//if the first peice has been selected a diffrent method must be employed as the first method
			//only accounts for the first peice and not the end location at all
			highlightColor = Color.red;
		}else{
			//if the move is valid this runs
			highlightColor = Color.green;
			//the same clicked method makes sure that they dont move wrong
			if(c.getInput().isMouseButtonDown(0) && !clicked){
				clicked = true;
				//this is for selecting the peice to move
				if(!makingMove){
					makingMove = true;
					movePos[0] = mouseToBoardCord(mouseCord);
				}else if(mouseToBoardCord(mouseCord)[0] != -2){ 
					//this runs if the player has selected the first peice and the mouse is on the board
					movePos[1] = mouseToBoardCord(mouseCord);
					//this runs if the move was a move and not a challange but if it was then it doesn't send the move
					if(movePeice(movePos[0][0], movePos[0][1], movePos[1][0], movePos[1][1])){
						Game.network.sendMove(movePos);
						state = 3;
					}
					makingMove = false;
				}
			}else if(!c.getInput().isMouseButtonDown(0) && clicked){
				clicked = false;
			}
		}
		
		//if you right click the move cansels
		if(c.getInput().isMouseButtonDown(1) && !c.getInput().isMouseButtonDown(0)){
			makingMove = false;
		}
	}
	
	/**
	 * draws anything that is only for the playing stage and not the placing stage
	 * @param g the graphics object used for drawing things
	 */
	private void playGameRender(Graphics g) {
		drawSideBar(g, oppColor); //draws the side bar
		
		//draws the blue highlight that goes around the first selected peice
		if(makingMove){
			drawHighlight(g, movePos[0], Color.blue);
		}
	}

	/**
	 * draws the sidebar with the peices
	 * @param g the grapihcs object
	 * @param color the color of the peices it's to draw
	 */
	private void drawSideBar(Graphics g, int color){
		for(int i = 0; i < 12; i++){
			g.drawImage(peices[color][i], 491, i * 48 + 6);
		}
	}
	
	/**
	 * checks if a move can be made from a spot
	 * @return if the move is possible and if it is returns true
	 */
	private boolean movePossible(){
		int[] cord = mouseToBoardCord(mouseCord);
		
		//if the mouse is off the screen reutrns false
		if(cord[0] == -2){
			return false;
		}
		
		//if the piece isn't your color or it has no piece on it returns false
		if(board[cord[0]][cord[1]].getColor() != playerColor || !board[cord[0]][cord[1]].filled){
			return false;
		}
		
		switch(board[cord[0]][cord[1]].getType()){
		case 15:
			return false;
		case 11:
			return false;
		case 0:
			return false;
		}
		
		//you have to do each of these in try catch statements becuase if the peice is on the edge of
		//the board it will crash because it trys to get a cord that isn't in the array
		try{
			if(!board[cord[0]][cord[1] + 1].filled || board[cord[0]][cord[1] + 1].getColor() == oppColor){
				return true;
			}
		}catch(ArrayIndexOutOfBoundsException e){}
		
		try{
			if(!board[cord[0]][cord[1] - 1].filled || board[cord[0]][cord[1] - 1].getColor() == oppColor){
				return true;
			}
		}catch(ArrayIndexOutOfBoundsException e){}
		
		try{
			if(!board[cord[0] + 1][cord[1]].filled || board[cord[0] + 1][cord[1]].getColor() == oppColor){
				return true;
			}
		}catch(ArrayIndexOutOfBoundsException e){}
		
		try{
			if(!board[cord[0] - 1][cord[1]].filled || board[cord[0] - 1][cord[1]].getColor() == oppColor){
				return true;
			}
		}catch(ArrayIndexOutOfBoundsException e){}
		
		return false;
	}

	/**
	 * the method that updates the board after a challange
	 * @param move the move that was made
	 * @param result if you won or lost or tied
	 * @param mover if you are the one moving the peice or defending
	 */
	private void challHandle(int[][] move, int result, boolean mover){
		
		// this sets what cord in move is your peice and which is theirs
		int oppPeice = -1;
		int myPeice = -1;
		if(mover){
			oppPeice = 1;
			myPeice = 0;
		}else{
			oppPeice = 0;
			myPeice = 1;
		}
		
		//depending on the outcome it updates the board
		switch(result){
		case 1:
			board[move[oppPeice][0]][move[oppPeice][1]].clearSpot();
			if(mover){
				movePeice(move[0][0], move[0][1], move[1][0], move[1][1]);
			}
			break;
		case 2:
			board[move[1][0]][move[1][1]].clearSpot();
			board[move[0][0]][move[0][1]].clearSpot();
			break;
		case 3:
			board[move[myPeice][0]][move[myPeice][1]].clearSpot();
			if(!mover){
				movePeice(move[0][0], move[0][1], move[1][0], move[1][1]);
			}
			break;
		}
	}
	
	/**
	 * this reveals the peice and draws it instead of the normal board for the next seconds
	 * @param type what type of peice it is
	 * @param x the x cord for the peice
	 * @param y the y cord for the peice
	 */
	private void reveal(int type, int x, int y){
		challR = true;
		
		//this copys the boards over
		for(int i = 0; i < 10; i++){
			for(int o = 0; o < 10; o++){
				boardTemp[i][o].clearSpot();
				boardTemp[i][o].setTo(board[i][o]);
			}
		}
		//this sets the peice to be revealed
		boardTemp[x][y].clearSpot();
		boardTemp[x][y].place(oppColor, type);
	}
}
