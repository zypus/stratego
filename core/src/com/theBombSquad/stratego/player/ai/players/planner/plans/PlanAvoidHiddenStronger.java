package com.theBombSquad.stratego.player.ai.players.planner.plans;

import java.util.ArrayList;
import java.util.List;

import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Encounter;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.players.planner.Plan;
import com.theBombSquad.stratego.player.ai.players.planner.TheQueen;

public class PlanAvoidHiddenStronger implements Plan{
	
	private UnitType unit;
	private float enemyUnitFactor;
	private float[] enemyWeakerProbs;
	private float enemyWeakerFactor;
	
	public PlanAvoidHiddenStronger(UnitType unit){
		this.unit = unit;
		this.enemyWeakerProbs = new float[12];
	}
	
	@Override
	public float evaluateMove(GameView view, Move move) {
		float value = 0;
		GameBoard board = view.getCurrentState();
		Unit self = board.getUnit(move.getFromX(), move.getFromY());
		Unit target = board.getUnit(move.getToX(), move.getToY());
		if(self.getType().equals(unit)){
			if(target.isUnknown()){
				if(!target.wasMoved(view.getCurrentTurn())){
					value = enemyWeakerFactor - TheQueen.getUnitValue(self.getType())*enemyUnitFactor;
				}
			}
		}
		if(value>0){
			System.out.println(value);
		}
		return value;
	}
	
	@Override
	public void postMoveUpdate(GameView view) {
		GameBoard board = view.getCurrentState();
		
		int unmoved = 0;
		for(int cy=0; cy<board.getHeight(); cy++){
			for(int cx=0; cx<board.getWidth(); cx++){
				Unit unit = board.getUnit(cx, cy);
				if(unit.getOwner().equals(view.getOpponentID())){
					if(unit.isUnknown()){
						if(!unit.wasMoved(view.getCurrentTurn())){
							unmoved++;
						}
					}
				}
			}
		}
		
		float factor = 0;
		
		for(int c=0; c<12; c++){
			UnitType type = Unit.getUnitTypeOfRank(c);
			int totalType = type.getQuantity();
			int deadType = view.getNumberOfOpponentDefeatedUnits(type);
			int revealedType = 0;
			for(int cy=0; cy<board.getHeight(); cy++){
				for(int cx=0; cx<board.getWidth(); cx++){
					Unit unit = board.getUnit(cx, cy);
					if(unit.getOwner().equals(view.getOpponentID())){
						if(unit.wasRevealed(view.getCurrentTurn())){
							if(unit.getType().equals(type)){
								revealedType++;
							}
						}
					}
				}
			}
			float midVal = ((float)(totalType-deadType-revealedType))/((float)unmoved);
			//Unit Will Lose
			if(Encounter.resolveFight(unit, type)!=Encounter.CombatResult.VICTORIOUS_ATTACK){
				factor += midVal;
				enemyWeakerProbs[unit.getRank()] = 0;
			}
			//Unit Will Win
			else{
				enemyWeakerProbs[unit.getRank()] = midVal;
			}
		}
		this.enemyUnitFactor = factor;
		float weaker = 0;
		for(int c=0; c<enemyWeakerProbs.length; c++){
			weaker += TheQueen.getUnitValue(Unit.getUnitTypeOfRank(c))*enemyWeakerProbs[c];
		}
		System.out.println("w "+weaker);
		this.enemyWeakerFactor = weaker;
	}
	
}
