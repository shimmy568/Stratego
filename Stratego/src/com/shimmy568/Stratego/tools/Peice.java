package com.shimmy568.Stratego.tools;

/**
 * the class that all the peices for the board are based on
 * @author Owen Anderson aka. Shimmy568
 *
 */
public class Peice {
	
	private int color, type;
	public boolean filled;
	
	/**
	 * 0 = flag
	 * 1 = spy
	 * 2 = scout (can move any number of spaced)
	 * 3 = miner (can kill bombs)
	 * 4 = sergeants
	 * 5 = lieutenants
	 * 6 = captains
	 * 7 = majors
	 * 8 = colonels
	 * 9 = general
	 * 10 = marshal
	 * 11 = bomb
	 * 12 = unknown
	 */
	
	public Peice(int x, int y){
		color = -1;
		filled = false;
	}
	
	/**
	 * Places the peice at setup and checks if it's a valid placement
	 * @param c the color of the peice
	 * @param t the type of peice that is being placed
	 * @param x the x cord of the peice being placed
	 * @param y the y cord of the peice being placed
	 * @return if the placement is valid it returns true
	 */
	public boolean place(int c, int t){
		//if c is 0 the peice is blue if it's 1 it's red
		if(!isFilled()){
			color = c;
			type = t;
			filled = true;
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * gets if the spot is filled with a peice
	 * @return
	 */
	public boolean isFilled(){
		return filled;
	}
	
	/**
	 * clears all of the data in the
	 * peice like if it was moved off the board
	 */
	public void clearSpot(){
		filled = false;
		color = -1;
	}
	
	/**
	 * sets the type of the peice in this spot
	 * @param t the type that it's setting to
	 */
	public void setType(int t){
		type = t;
	}
	
	/**
	 * a method to set the peice to another used for when a peice is moving to an empty spot
	 * @param p The peice that is moving to this spot
	 */
	public void setTo(Peice p){
		if(!p.filled){
			return;
		}
		filled = true;
		color = p.color;
		type = p.type;
	}
	
	
	/**
	 * checks if the move is valid and if so returns true
	 * @param board
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 * @return
	 */
	public static boolean isValidMove(Peice[][] board, int startX, int startY, int endX, int endY){
		//the moves that are invalid for any peice
		if(endX == -2){
			return false;
		}
		if(startX != endX && startY != endY){
			return false;
		}
		if(board[endX][endY].getType() == 15){
			return false;
		}
		if(board[startX][startY].getColor() == board[endX][endY].getColor()){
			return false;
		}
		
		switch(board[startX][startY].getType()){
		case 0:
			return false;
		case 11:
			return false;
		default:
			if(!(Math.abs(startX - endX) <= 1 && Math.abs(startY - endY) <= 1) && board[startX][startY].getType() != 2){
				return false;
			}else if(board[startX][startY].getType() == 2){
				boolean f = false;
				if(startX != endX){
					for(int i = startX; i != endX;){
						if(board[i][endY].filled && f){
							return false;
						}
						
						if(i > endX){
							i--;
						}else if(i < endX){
							i++;
						}
						f = true;
					}
					return true;
				}else if(startY != endY){
					for(int i = startY; i != endY;){
						if(board[endX][i].filled && f){
							return false;
						}
						
						if(i > endY){
							i--;
						}else if(i < endY){
							i++;
						}
						f = true;
					}
					return true;
				}
			}
		}
		return true;
	}
	/**
	 * a method that challanges the peice that it moves to
	 * return 1 = victory remove peice on that space
	 * return 2 = lost remove own peice
	 * return 3 = tie remove both peices
	 * @param board the board
	 * @param startX the starting peice's x cord
	 * @param startY the starting peice's y cord
	 * @param endX the end spot's x cord
	 * @param endY the end spot's y cord
	 * @return
	 */
	public static int challange(Peice[][] board, int startX, int startY, int type){
		if(type == 11 && board[startX][startY].type == 3){
			return 1;
		}else if(type == 10 && board[startX][startY].type == 1){
			return 1;
		}		
		if(board[startX][startY].type > type){
			return 1;
		}else if(board[startX][startY].type == type){
			return 2;
		}else{
			return 3;
		}
	}
	
	/**
	 * gets the type of the peice
	 * @return the type
	 */
	public int getType(){
		return type;
	}
	
	/**
	 * get the Color of the peice thats in this spot
	 * @return the color 
	 */
	public int getColor(){
		return color;
	}
}
