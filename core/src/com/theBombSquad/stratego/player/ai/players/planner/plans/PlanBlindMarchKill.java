package com.theBombSquad.stratego.player.ai.players.planner.plans;

import java.util.List;

import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Encounter;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.gameMechanics.board.Encounter.CombatResult;
import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.AI;
import com.theBombSquad.stratego.player.ai.AIGameState;
import com.theBombSquad.stratego.player.ai.AIUnit;
import com.theBombSquad.stratego.player.ai.players.planner.Plan;
import com.theBombSquad.stratego.player.ai.players.planner.TheQueen;
import com.theBombSquad.stratego.player.ai.players.planner.aStar.GameSpecificAStar;

public class PlanBlindMarchKill implements Plan{
	
	private static final float NEGATIVITY_BIAS = 1.4f;
	
	private AIGameState state;
	
	private Unit target;
	private boolean currActive = false;
	private GameSpecificAStar star = null;
	private int x;
	private int y;

	
	public PlanBlindMarchKill(Unit target){
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
		
		if(!target.wasRevealed(view.getCurrentTurn())){
			List<GameSpecificAStar.Node> nodes = star.findPath(move.getFromX(), move.getFromY());
			for(int c=0; c<nodes.size(); c++){
				GameSpecificAStar.Node tNode = nodes.get(c);
				if(tNode.getX()==move.getToX() && tNode.getY()==move.getToY()){
					//System.out.println(self.getType()+" "+target.getType()+" "+(nodes.size()-c));
					value = calcFightPoints(view, self) / (nodes.size()-c);
				}
			}
		}
		return value;
	}

	@Override
	public void postMoveUpdate(GameView view) {
		this.state = AI.createAIGameState(view);
		GameBoard board = view.getCurrentState();
		int destX = -1;
		int destY = -1;
		for(int cy=0; cy<board.getHeight(); cy++){
			for(int cx=0; cx<board.getWidth(); cx++){
				if(board.getUnit(cx, cy).getId()==target.getId()){
					this.target = board.getUnit(cx, cy);
					destX = cx;
					destY = cy;
					this.x = destX;
					this.y = destY;
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
	
	private float calcFightPoints(GameView view, Unit self){
		float value = 0;
		GameBoard board = view.getCurrentState();
		if(target.isUnknown()){
			AIUnit opp = state.getAIUnit(x, y);
			float winPoints = 0;
			float loseProb = 0;
			for(int c=0; c<12; c++){
				UnitType type = Unit.getUnitTypeOfRank(c);
				if(Encounter.resolveFight(self.getType(), target.getType())==CombatResult.VICTORIOUS_ATTACK){
					winPoints += opp.getProbabilityFor(type) * TheQueen.getUnitValue(type);
				}
				else{
					loseProb += opp.getProbabilityFor(type);
				}
			}
			value = winPoints - ((TheQueen.getUnitValue(self.getType())*loseProb)*NEGATIVITY_BIAS);
		}
		return value;
	}
	
}
