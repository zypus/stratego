package com.theBombSquad.stratego.gameMechanics;

import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.Player;

import lombok.RequiredArgsConstructor;

import java.lang.Character.UnicodeScript;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.theBombSquad.stratego.StrategoConstants.PlayerID;

/**
 * TODO Add description
 *
 * @author Fabian Fränz <f.fraenz@t-online.de>
 * @author Flo
 */
@RequiredArgsConstructor
public class GameView {

	
	private final Game game;
	private final PlayerID playerID;

	public boolean validateMove(Move move) {
		return false;

	}

	public void performMove(Move move) {

	}

	public boolean validateSetup(Unit[][] setup) {
		return false;

	}

	public void setSetup(Unit[][] setup) {

	}

	public int getCurrentTurn() {

		return 0;
	}

	public GameBoard getCurrentState() {
		// TODO make sure you first duplicate() the gameBoard and replace non revealed opponent units by unknown
		return null;
	}

	public GameBoard getState(int turn) {
		// TODO make sure you first duplicate() the gameBoard and replace non revealed opponent units by unknown
		return null;
	}

	public boolean isAir(int x, int y) {

		return false;
	}

	public boolean isLake(int x, int y) {

		return false;
	}

	public boolean isUnknown(int x, int y) {

		return false;
	}

	public List<Move> getMoves() {

		return null;
	}

	public Move getMove(int turn) {

		return null;
	}

	public Move getLastMove() {

		return null;
	}

	public List<Unit> getAllDefeatedUnits() {

		List<Unit> allList = new ArrayList<Unit>();
		allList.addAll(game.getDefeatedUnitsPlayer1());
		allList.addAll(game.getDefeatedUnitsPlayer2());

		return Collections.unmodifiableList(allList);
	}

	public List<Unit> getOwnDefeatedUnits() {

		return null;
	}

	public List<Unit> getOpponentsDefeatedUnits() {

		return null;
	}

	public void startSetup() {
		GameBoard board=game.getCurrentState();
		ArrayList<Unit> units= new ArrayList<Unit>();
		units.add(new Unit(UnitType.FLAG,playerID));
		units.add(new Unit(UnitType.BOMB,playerID));
		units.add(new Unit(UnitType.SPY,playerID));
		units.add(new Unit(UnitType.SCOUT,playerID));
		units.add(new Unit(UnitType.SAPPER,playerID));
		units.add(new Unit(UnitType.SERGEANT,playerID));
		units.add(new Unit(UnitType.LIEUTENANT,playerID));
		units.add(new Unit(UnitType.CAPTAIN,playerID));
		units.add(new Unit(UnitType.MAJOR,playerID));
		units.add(new Unit(UnitType.COLONEL,playerID));
		units.add(new Unit(UnitType.GENERAL,playerID));
		units.add(new Unit(UnitType.MARSHAL,playerID));
		int counter=0;
		int marker=0;
		for(int i = 0; i<board.getWidth();i++){
			for(int j = 0; j<4;j++){
				if(units.get(marker).getType().getQuantity()>counter){
					counter++;
					board.setUnit(i,j,new Unit(units.get(marker).getType(),playerID));
				}
				else{
					marker++;
					counter=0;
					j--;
				}
			}
		}

		
	}

	public PlayerID getPlayerID() {
		return playerID;
	}

}
