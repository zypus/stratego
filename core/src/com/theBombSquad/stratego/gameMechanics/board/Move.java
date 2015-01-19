package com.theBombSquad.stratego.gameMechanics.board;

import com.theBombSquad.stratego.StrategoConstants;
import com.theBombSquad.stratego.StrategoConstants.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

import static com.theBombSquad.stratego.StrategoConstants.*;
import static java.lang.Math.*;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @author Flo
 */
@Getter
@ToString
@RequiredArgsConstructor
public class Move implements Serializable {

	public static String[] xRep = new String[]{"A","B","C","D","E","F","G","H","J","K"};

	// set by player
	private final int fromX;
	private final int fromY;
	private final int toX;
	private final int toY;

	// set by game view
	private StrategoConstants.PlayerID playerID = null;
	// set by game
	private int turn = UNREVEALED;
	private Unit movedUnit = null;
	transient private Encounter encounter = null;

	public boolean hasEncounter() {
		return encounter != null;
	}

	public void setPlayerID(StrategoConstants.PlayerID playerID) {
		assert this.playerID == null : "Trying to set playerID more than once";
		this.playerID = playerID;
	}

	public void setTurn(int turn) {
//		assert this.turn == -1 : "Trying to set turn more than once";
		this.turn = turn;
	}

	public void setMovedUnit(Unit movedUnit) {
//		assert this.movedUnit == null : "Trying to set movedUnit more than once";
		this.movedUnit = movedUnit;
	}

	public void setEncounter(Encounter encounter) {
		//		assert this.encounter.getResult() == null : "Trying to set encounter more than once";
		this.encounter = encounter;
	}

	/**
	 * Utility methods
	 */

	public int getDistance() {
		if (fromX == toX) {
			return abs(fromY - toY);
		} else if (fromY == toY) {
			return abs(fromX - toX);
		}
		return 0;
	}

	public boolean isXMovement() {
		return this.fromY == this.toY;
	}

	public boolean isYMovement() {
		return this.fromX == this.toX;
	}

	public int xMovementDirection() {
		if (this.fromX < this.toX) {
			return 1;
		} else if (this.fromX > this.toX) {
			return -1;
		} else {
			return 0;
		}
	}

	public int yMovementDirection() {
		if (this.fromY < this.toY) {
			return 1;
		} else if (this.fromY > this.toY) {
			return -1;
		} else {
			return 0;
		}
	}

	public boolean isSameMovementAs(Move move) {
		return (this.fromX == move.getFromX() 
				&& this.fromY == move.getFromY() 
				&& this.toX == move.getToX()
				&& this.toY == move.getToY());
	}

	public boolean isMovementInBetween(Move move) {
		if (isXMovement() && move.isXMovement()) {
			if (xMovementDirection() == move.xMovementDirection()) {
				if (xMovementDirection() == 1) {
					return this.fromX >= move.getFromX() && this.toX <= move.getToX();
				} else {
					return this.fromX <= move.getFromX() && this.toX >= move.getToX();
				}
			} else {
				if (xMovementDirection() == 1) {
					return this.fromX >= move.getToX() && this.toX <= move.getFromX();
				} else {
					return this.fromX <= move.getToX() && this.toX >= move.getFromX();
				}
			}
		} else if (isYMovement() && move.isYMovement()) {
			if (yMovementDirection() == move.yMovementDirection()) {
				if (yMovementDirection() == 1) {
					return this.fromY >= move.getFromY() && this.toY <= move.getToY();
				} else {
					return this.fromY <= move.getFromY() && this.toY >= move.getToY();
				}
			} else {
				if (yMovementDirection() == 1) {
					return this.fromY >= move.getToY() && this.toY <= move.getFromY();
				} else {
					return this.fromY <= move.getToY() && this.toY >= move.getFromY();
				}
			}
		} else {
			return false;
		}
	}

	/** Returns Move As Text */
	public String toString(){
		String text = "";
		String nameOfMovedUnit = "Unit";
		if(movedUnit != null && !(movedUnit.getRevealedInTurn()== UNREVEALED)){
			nameOfMovedUnit = ""+movedUnit.getType();
		}
		text += ""+playerName(playerID)+"'s "+nameOfMovedUnit+" from "+(fromX+1)+"|"+(fromY+1)+" to "+(toX+1)+"|"+(toY+1);
		if(encounter != null){
			text += ", ";
			if(encounter.mutualDefeat()){
				text += "both it and "+playerName(encounter.getDefendingUnit().getOwner())+"'s "+encounter.getDefendingUnit().getType()+" died.";
			}
			else{
				text += playerName(encounter.getVictoriousUnit().getOwner())+"'s "+encounter.getVictoriousUnit().getType()+" killed "+playerName(encounter.getDefeatedUnits()[0].getOwner())+"'s "+encounter.getDefeatedUnits()[0].getType();
			}
		}
		return text;
	}

	private String playerName(PlayerID id){
		return (id==StrategoConstants.PlayerID.PLAYER_1)?"Pl 1":"Pl 2";
	}

	public String moveFromRepresentaion() {
		return locationRepresentaion(fromX, fromY);
	}

	public String moveToRepresentation() {
		return locationRepresentaion(toX, toY);
	}

	public String locationRepresentaion(int x, int y) {
		return xRep[x]+y;
	}
}
