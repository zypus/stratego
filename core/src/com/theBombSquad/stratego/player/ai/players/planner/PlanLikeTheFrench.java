package com.theBombSquad.stratego.player.ai.players.planner;

import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Encounter;
import com.theBombSquad.stratego.gameMechanics.board.Encounter.CombatResult;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;

/** Moves Unit Away From Adjacent Unit That Is Likely To Be A Threat 
 * Name By James R. */
public class PlanLikeTheFrench implements Plan{

	@Override
	public float evaluateMove(GameView view, Move move) {
		GameBoard board = view.getCurrentState();
		Unit self = board.getUnit(move.getFromX(), move.getFromY());
		
		//Check Whether Any Of The Units Neighbouring Units Are Known Threats
		boolean needsToFlee = false;
		int x = move.getFromX();
		int y = move.getFromY()-1;
		if(needsToFlee(x, y, view, self)){
			needsToFlee = true;
		}
		x = move.getFromX()+1;
		y = move.getFromY();
		if(needsToFlee(x, y, view, self)){
			needsToFlee = true;
		}
		x = move.getFromX();
		y = move.getFromY()+1;
		if(needsToFlee(x, y, view, self)){
			needsToFlee = true;
		}
		x = move.getFromX()-1;
		y = move.getFromY();
		if(needsToFlee(x, y, view, self)){
			needsToFlee = true;
		}
		//If Neighbours Are Threats, Check Whether Destination Is Save To Walk Onto, If So Return Positive Inforcement
		if(needsToFlee){
			float profit =TheQueen.getUnitValue(self.getType())*2;
			//If Destination Completely Save Return Full Profit
			if(board.getUnit(move.getToX(), move.getToY()).isAir()){
				return profit;
			}
			//Return Partial Profit As Destination May Not Be Safe At All
			else if(!needsToFlee(move.getToX(), move.getFromY(), view, self)){
				return profit*0.6f;
			}
		}
		//Neutral If No KNOWN Threat has been found or move doesn't lead to escape of Known threat
		return 0;
	}
	
	private boolean needsToFlee(int x, int y, GameView view, Unit self){
		GameBoard board = view.getCurrentState();
		if(board.isInBounds(x, y)){
			if(!board.getUnit(x, y).isAir()){
				if(board.getUnit(x, y).getOwner().equals(view.getOpponentID())){
					CombatResult result = Encounter.resolveFight(board.getUnit(x, y).getType(), self.getType());
					if(result.equals(CombatResult.VICTORIOUS_ATTACK)){
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void postMoveUpdate(GameView view) {
		//Nothing to do here yolo
	}

}
