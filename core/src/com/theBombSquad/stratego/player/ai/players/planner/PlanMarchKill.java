package com.theBombSquad.stratego.player.ai.players.planner;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;

import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Encounter;
import com.theBombSquad.stratego.gameMechanics.board.Encounter.CombatResult;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.ai.players.planner.aStar.GameSpecificAStar;

/** Gives Points For Getting A Unit Able To Defeat A Specific Revealed Opponent Unit To A Space Next To It */
public class PlanMarchKill implements Plan{
	
	private Unit target;
	private boolean currActive = false;
	private GameSpecificAStar star = null;

	
	public PlanMarchKill(Unit target){
		this.target = target;
	}
	
	@Override
	public float evaluateMove(GameView view, Move move) {
		GameBoard board = view.getCurrentState();
		Unit self = board.getUnit(move.getFromX(), move.getFromY());
		
		if(!currActive){
			return 0;
		}
		
		float value = 0;
		
		if(target.wasRevealed(view.getCurrentTurn())){
			CombatResult result = Encounter.resolveFight(self.getType(), target.getType());
			if(result.equals(CombatResult.VICTORIOUS_ATTACK)){
				List<GameSpecificAStar.Node> nodes = star.findPath(move.getFromX(), move.getFromY());
				for(int c=0; c<nodes.size(); c++){
					GameSpecificAStar.Node tNode = nodes.get(c);
					if(tNode.getX()==move.getToX() && tNode.getY()==move.getToY()){
						System.out.println(self.getType()+" "+target.getType()+" "+(nodes.size()-c));
						value = TheQueen.getUnitValue(target.getType())/(nodes.size()-c);
					}
				}
			}
		}
//		if(value>0){
//			System.out.println(value);
//		}
		return value;
	}

	@Override
	public void postMoveUpdate(GameView view) {
		GameBoard board = view.getCurrentState();
		int destX = -1;
		int destY = -1;
		for(int cy=0; cy<board.getHeight(); cy++){
			for(int cx=0; cx<board.getWidth(); cx++){
				if(board.getUnit(cx, cy).getId()==target.getId()){
					this.target = board.getUnit(cx, cy);
					destX = cx;
					destY = cy;
				}
			}
		}
		if(destX==-1){
			currActive = false;
		}
		else{
			currActive = true;
			this.star = new GameSpecificAStar(board, destX, destY);
		}
	}

}
