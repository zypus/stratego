package com.theBombSquad.stratego.player.ai.opponentModelling;

import com.theBombSquad.stratego.StrategoConstants.PlayerID;

public class ProbabilityTile {

	private PlayerID playerID;
	private double[] probs = new double[12];
	private boolean isKnown = false;
	
	
	public ProbabilityTile(PlayerID playerid, double[] probabilities){
		
		this.playerID = playerid;
		this.probs = probabilities;
		
	}
	
	public PlayerID getPlayerID(){
		return playerID;
	}
	
	public void setPlayerID(PlayerID player){
		this.playerID = player;
	}
	
	public double[] getAllProbs(){
		return probs;
	}
	
	public double getProbAtRank(int rank){
		return probs[rank];
	}
	
	public void setProbAtRank(int rank, double newProb){
		probs[rank] = newProb;
	}
	
	public void setAllProbsToZero(){
		for(int i = 0; i < probs.length; i++){
			setProbAtRank(i, 0);
		}
		this.playerID = null;
	}
	
	public void setAllProbs(double[] newProbs){
		for(int i = 0; i < newProbs.length; i++){
			this.probs[i] = newProbs[i];
		}
	}
	
	private void setRevealed(){
		this.isKnown = true;
	}
	
	public void checkRevealed(){
		for(int i = 0; i < probs.length; i++){
			if(probs[i] == 1){
				setRevealed();
			}
		}
	}
	
	public boolean isVisible(){
		return isKnown;
	}
}
