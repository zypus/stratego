package com.theBombSquad.stratego.player.ai.BluffingAI;

import java.util.ArrayList;

import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.AIGameState;
import com.theBombSquad.stratego.player.ai.AIUnit;

public class BluffingProbability {
	private double Beta;
	private ArrayList<Double> BluffingProbabilities= new ArrayList<Double>();
	private int n=0;
	private double probMargin=0.1;

	
	public void encounter(Move move, AIGameState state){
		if(checkIfJustDiscovered(move)){
			if(checkIfBluffed(move, state)){
				updateBluffProbability();
			}
		}
	}
	
	public boolean checkIfJustDiscovered(Move move){
		int turnRevealed=move.getEncounter().getVictoriousUnit().getRevealedInTurn();
		int turn=move.getTurn();
		if(turn==turnRevealed){
			return true;
		}
		return false;
	}
	
	public boolean checkIfBluffed(Move move, AIGameState state){
		UnitType type= move.getMovedUnit().getType();
		AIUnit actual=state.getAIUnit(move.getFromX(), move.getFromY());
		double prob=actual.getProbabilityFor(type);
		if(prob<probMargin){
			return true;
		}
		else
			return false;
	}
	
	public void updateBluffProbability(){
		n++;
		double last= getCurrentProb();
		int i=BluffingProbabilities.size()+1;
		double actual= 1/(1+Beta)*(last+n/i*Beta);
		BluffingProbabilities.add(actual);
	}
	
	public double getCurrentProb(){
		return BluffingProbabilities.get(BluffingProbabilities.size()-1);
	}
}
