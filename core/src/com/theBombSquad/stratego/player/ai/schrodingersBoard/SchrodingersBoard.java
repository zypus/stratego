package com.theBombSquad.stratego.player.ai.schrodingersBoard;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.Getter;

import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.evaluationFunction.EvaluationFunction;

@Data
/** This Class is supposed to simplify and abstract board states and board state manipulation for unknown units */
public class SchrodingersBoard {

	private SchrodingersUnit[][] board;
	private int opponentArmySize;
	private int ownArmySize;
	private GameView view;
	
	public int getHeight(){
		return board.length;
	}
	
	public int getWidth(){
		return board[0].length;
	}
	
	/** Constructs New Schrodingers Box Which Is Dependent On A Single (The Current) State */
	public SchrodingersBoard(GameView view){
		this.view = view;
		GameBoard gameBoard = view.getCurrentState();
		int[] stillUnknownUnits = new int[12];
		int opponentArmySize = 0;
		int ownArmySize = 0;
		for(int c=0; c<12; c++){
			stillUnknownUnits[c] = Unit.getUnitTypeOfRank(c).getQuantity()-view.getNumberOfOpponentDefeatedUnits(Unit.getUnitTypeOfRank(c));
		}
		for(int cy=0; cy<gameBoard.getHeight(); cy++){
			for(int cx=0; cx<gameBoard.getWidth(); cx++){
				Unit u = gameBoard.getUnit(cx, cy);
				if(!u.isLake() && !u.isAir()){
					if(u.getOwner()==view.getOpponentID()){
						opponentArmySize++;
						if(!u.isUnknown()){
							stillUnknownUnits[u.getType().getRank()]--;
						}
					}
					else if(u.getOwner()==view.getPlayerID()){
						ownArmySize++;
					}
				}
			}
		}
		this.ownArmySize = ownArmySize;
		this.opponentArmySize = opponentArmySize;
		this.board = new SchrodingersUnit[gameBoard.getHeight()][gameBoard.getWidth()];
		for(int cy=0; cy<board.length; cy++){
			for(int cx=0; cx<board[cy].length; cx++){
				Unit u = gameBoard.getUnit(cx, cy);
				if(!u.isAir() && !u.isLake()){
					if(u.isUnknown()){
						float[] probs = new float[12];
						for(int c=0; c<probs.length; c++){
							probs[c] = stillUnknownUnits[c]/((float)opponentArmySize);
						}
						board[cy][cx] = new SchrodingersUnit(view.getOpponentID(), probs);
					}
					else{
						board[cy][cx] = new SchrodingersUnit(u.getOwner(), u.getType());
					}
				}
				else{
					//Create Empty
					if(u.isAir()){
						board[cy][cx] = new SchrodingersUnit(true);
					}
					else{
						board[cy][cx] = new SchrodingersUnit(false);
					}
				}
			}
		}
	}
	
	
	/** Constructor Used for cloning */
	public SchrodingersBoard(SchrodingersUnit[][] board, int ownArmySize, int opponentArmySize, GameView view){
		this.board = board;
		this.ownArmySize = ownArmySize;
		this.opponentArmySize = opponentArmySize;
		this.view = view;
	}
	
	public SchrodingersBoard clone(){
		SchrodingersUnit[][] cBoard = new SchrodingersUnit[board.length][board[0].length];
		for(int cy=0; cy<cBoard.length; cy++){
			for(int cx=0; cx<cBoard[cy].length; cx++){
				cBoard[cy][cx] = board[cy][cx].clone();
			}
		}
		return new SchrodingersBoard(cBoard, ownArmySize, opponentArmySize, view);
	}
	
	/** Creates a new Board in which unit from origin is now at destination - Also executes Encounters! */
	public ArrayList<SchrodingersBoard> moveUnit(int originX, int originY, int destX, int destY){
		ArrayList<SchrodingersBoard> list = new ArrayList<SchrodingersBoard>();
		SchrodingersBoard placeHolder = clone();
		placeHolder.board[originY][originX].removePossibilityFor(Unit.UnitType.BOMB);
		placeHolder.board[originY][originX].removePossibilityFor(Unit.UnitType.FLAG);
		//Moves onto empty square
		if(getBoard()[destY][destX].isAir()){
			SchrodingersBoard newBoard = placeHolder.clone();
			newBoard.getBoard()[destY][destX] = newBoard.getBoard()[originY][originX];
			newBoard.getBoard()[originY][originX] = new SchrodingersUnit(true);
			list.add(newBoard);
		}
		//Destination is not empty - i.e. Encounter!
		else{
			SchrodingersUnit dest = getBoard()[destY][destX];
			SchrodingersUnit orig = getBoard()[originY][originX];
			SchrodingersUnit analysed = null;
			SchrodingersUnit helper = null;
			boolean offensiveNotDefensive = orig.getOwner()==view.getOpponentID();
			if(orig.getOwner()==view.getOpponentID()){
				analysed = orig;
				helper = dest;
			}
			else{
				analysed = dest;
				helper = orig;
			}
			//Win With Analyzed
			SchrodingersBoard win = placeHolder.clone();
			if(analysed.combatUpdateWin(helper, win, offensiveNotDefensive)){
				SchrodingersBoard newBoard = win;
				newBoard.getBoard()[destY][destX] = newBoard.getBoard()[originY][originX];
				newBoard.getBoard()[originY][originX] = new SchrodingersUnit(true);
				newBoard.ownArmySize = newBoard.ownArmySize-1;
				list.add(newBoard);
			}
			//Draw With Analyzed
			SchrodingersBoard draw = placeHolder.clone();
			if(analysed.combatUpdateDraw(helper, draw, offensiveNotDefensive)){
				SchrodingersBoard newBoard = draw;
				newBoard.getBoard()[destY][destX] = new SchrodingersUnit(true);
				newBoard.getBoard()[originY][originX] = new SchrodingersUnit(true);
				newBoard.ownArmySize = newBoard.ownArmySize-1;
				newBoard.opponentArmySize = newBoard.opponentArmySize-1;
				list.add(newBoard);
			}
			//Lose With Analyzed
			SchrodingersBoard lose = placeHolder.clone();
			if(analysed.combatUpdateDraw(helper, lose, offensiveNotDefensive)){
				SchrodingersBoard newBoard = lose;
				newBoard.getBoard()[originY][originX] = new SchrodingersUnit(true);
				newBoard.opponentArmySize = newBoard.opponentArmySize-1;
				list.add(newBoard);
			}
		}
		return list;
	}
	
	/** Generates All Possible Moves That Can Follow This Board Given Player */
	public List<Move> generateAllMoves(PlayerID player){
		ArrayList<Move> list = new ArrayList<Move>();
		for(int cy=0; cy<board.length; cy++){
			for(int cx=0; cx<board[cy].length; cx++){
				SchrodingersUnit u = board[cy][cx];
				if(!u.isAir() && !u.isLake()){
					if(u.getOwner()==player){
						boolean canMove = false;
						for(int c=1; c<11; c++){
							//Goes Through All Units Not Flag (0) And Not Bomb (11)
							if(u.getProbabilityFor(Unit.getUnitTypeOfRank(c))>0){
								canMove = true;
							}
						}
						if(canMove){
							int posX = -1;
							int posY = -1;
							if(!(board[cy][cx].getProbabilityFor(Unit.UnitType.SCOUT)>0)){
								posX = cx;
								posY = cy-1;
								if(posX>=0 && posY>=0 && posX<board[0].length && posY<board.length){
									if(board[posY][posX].isAir() || (board[posY][posX].isActualUnit() && board[posY][posX].getOwner()!=player)){
										list.add(new Move(cx, cy, posX, posY));
									}
								}
								posX = cx;
								posY = cy+1;
								if(posX>=0 && posY>=0 && posX<board[0].length && posY<board.length){
									if(board[posY][posX].isAir() || (board[posY][posX].isActualUnit() && board[posY][posX].getOwner()!=player)){
										list.add(new Move(cx, cy, posX, posY));
									}
								}
								posX = cx-1;
								posY = cy;
								if(posX>=0 && posY>=0 && posX<board[0].length && posY<board.length){
									if(board[posY][posX].isAir() || (board[posY][posX].isActualUnit() && board[posY][posX].getOwner()!=player)){
										list.add(new Move(cx, cy, posX, posY));
									}
								}
								posX = cx+1;
								posY = cy;
								if(posX>=0 && posY>=0 && posX<board[0].length && posY<board.length){
									if(board[posY][posX].isAir() || (board[posY][posX].isActualUnit() && board[posY][posX].getOwner()!=player)){
										list.add(new Move(cx, cy, posX, posY));
									}
								}
							}
							else{
								//Up
								posX = cx;
								posY = cy;
								while(posY>=0+1){
									posY--;
									if(board[posY][posX].isAir()){
										list.add(new Move(cx, cy, posX, posY));
									}
									else if(board[posY][posX].isActualUnit() && board[posY][posX].getOwner()!=player){
										list.add(new Move(cx, cy, posX, posY));
										break;
									}
									else{
										break;
									}
								}
								//Right
								posX = cx;
								posY = cy;
								while(posX<board[cy].length-1){
									posX++;
									if(board[posY][posX].isAir()){
										list.add(new Move(cx, cy, posX, posY));
									}
									else if(board[posY][posX].isActualUnit() && board[posY][posX].getOwner()!=player){
										list.add(new Move(cx, cy, posX, posY));
										break;
									}
									else{
										break;
									}
								}
								//Down
								posX = cx;
								posY = cy;
								while(posY<board.length-1){
									posY++;
									if(board[posY][posX].isAir()){
										list.add(new Move(cx, cy, posX, posY));
									}
									else if(board[posY][posX].isActualUnit() && board[posY][posX].getOwner()!=player){
										list.add(new Move(cx, cy, posX, posY));
										break;
									}
									else{
										break;
									}
								}
								//Left
								posX = cx;
								posY = cy;
								while(posX>=0+1){
									posX--;
									if(board[posY][posX].isAir()){
										list.add(new Move(cx, cy, posX, posY));
									}
									else if(board[posY][posX].isActualUnit() && board[posY][posX].getOwner()!=player){
										list.add(new Move(cx, cy, posX, posY));
										break;
									}
									else{
										break;
									}
								}
							}
						}
					}
				}
			}
		}
		return list;
	}
	
	/** Generates List Of Schrodingers Boards based upon move (Move is assumed to be legal) */
	public List<SchrodingersBoard> generateFromMove(Move move){
		return moveUnit(move.getFromX(), move.getFromY(), move.getToX(), move.getToY());
	}
	
	/** Evaluates This Board */
	public float evaluate(EvaluationFunction eval){
		//TODO: Make
		return 42f;
	}
	
	/** Generates A Random Move Based Upon The Given Player */
	public SchrodingersBoard generateRandomMove(PlayerID player){
		//TODO: Make
		return this;
	}

	public int getProbability() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}
