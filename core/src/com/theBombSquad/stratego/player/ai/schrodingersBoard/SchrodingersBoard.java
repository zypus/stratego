package com.theBombSquad.stratego.player.ai.schrodingersBoard;

import java.util.ArrayList;

import lombok.Data;
import lombok.Getter;

import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;

@Data
/** This Class is supposed to simplify and abstract board states and board state manipulation for unknown units */
public class SchrodingersBoard {

	private SchrodingersUnit[][] board;
	private int opponentArmySize;
	private int ownArmySize;
	private GameView view;
	
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
	
	/** Creates a new Board in which unit from origin is now at destination */
	public SchrodingersBoard moveUnit(int originX, int originY, int destX, int destY){
		SchrodingersBoard newBoard = clone();
		//Moves onto empty square
		if(newBoard.getBoard()[destY][destX].isAir()){
			newBoard.getBoard()[destY][destX] = newBoard.getBoard()[originY][originX];
			newBoard.getBoard()[originY][originX] = new SchrodingersUnit(true);
		}
		//Destination is not empty - i.e. Encounter!
		else{
			SchrodingersUnit dest = newBoard.getBoard()[destY][destX];
			SchrodingersUnit orig = newBoard.getBoard()[originY][originX];
			if(dest.unitIsKnown() && orig.unitIsKnown()){
				//Both Units are known
			}
			else{
				//Only Player's Unit is known
				ArrayList<UnitType> weakerUnits = new ArrayList<UnitType>();
				ArrayList<UnitType> strongerUnits = new ArrayList<UnitType>();
				ArrayList<UnitType> draws = new ArrayList<UnitType>();
				if(orig.getOwner()==view.getPlayerID()){
					//Origin Unit (Controlled By Self) Known
					UnitType ut = orig.getKnownUnit();
					for(UnitType unitType : new UnitType[]{Unit.UnitType.FLAG, Unit.UnitType.BOMB, Unit.UnitType.SPY, Unit.UnitType.SCOUT, Unit.UnitType.SAPPER, Unit.UnitType.SERGEANT, Unit.UnitType.LIEUTENANT, Unit.UnitType.CAPTAIN, Unit.UnitType.MAJOR, Unit.UnitType.COLONEL, Unit.UnitType.GENERAL, Unit.UnitType.MARSHAL}){
						if(ut.getRank()>unitType.getRank()){
							weakerUnits.add(unitType);
						}
						else if(ut.getRank()==Unit.UnitType.SAPPER.getRank() && unitType.getRank()==Unit.UnitType.BOMB.getRank()){
							weakerUnits.add(unitType);
						}
						else if(ut.getRank()==Unit.UnitType.SPY.getRank() && unitType.getRank()==Unit.UnitType.MARSHAL.getRank()){
							weakerUnits.add(unitType);
						}
						else if(ut.getRank()==unitType.getRank()){
							draws.add(unitType);
						}
						else{
							strongerUnits.add(unitType);
						}
					}
				}
				else{
					//Destination Unit Known (Controlled By Self)
					UnitType ut = dest.getKnownUnit();
					for(UnitType unitType : new UnitType[]{Unit.UnitType.FLAG, Unit.UnitType.BOMB, Unit.UnitType.SPY, Unit.UnitType.SCOUT, Unit.UnitType.SAPPER, Unit.UnitType.SERGEANT, Unit.UnitType.LIEUTENANT, Unit.UnitType.CAPTAIN, Unit.UnitType.MAJOR, Unit.UnitType.COLONEL, Unit.UnitType.GENERAL, Unit.UnitType.MARSHAL}){
						if(ut.getRank()>unitType.getRank()){
							weakerUnits.add(unitType);
						}
						else if(ut.getRank()==unitType.getRank()){
							draws.add(unitType);
						}
						else if(ut.getRank()==Unit.UnitType.BOMB.getRank() && unitType.getRank()==Unit.UnitType.SAPPER.getRank()){
							strongerUnits.add(unitType);
						}
						else{
							strongerUnits.add(unitType);
						}
					}
				}
			}
		}
		return newBoard;
	}
	
	
}
