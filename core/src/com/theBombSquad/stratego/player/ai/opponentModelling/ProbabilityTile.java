package com.theBombSquad.stratego.player.ai.opponentModelling;

import com.theBombSquad.stratego.StrategoConstants.PlayerID;

public class ProbabilityTile {

	private PlayerID playerID;
	private double[] probs = new double[12];
	private boolean isRevealed = false;
	private boolean hasMoved = false;
	private boolean isEmpty = true;

	public ProbabilityTile(PlayerID playerid, double[] probabilities) {

		this.playerID = playerid;
		this.probs = probabilities;

	}

	public PlayerID getPlayerID() {
		return playerID;
	}

	public void setPlayerID(PlayerID player) {
		this.playerID = player;
	}

	public double[] getAllProbs() {
		return probs;
	}

	public double getProbAtRank(int rank) {
		return probs[rank];
	}

	public void setProbAtRank(int rank, double newProb) {
		probs[rank] = newProb;
		isEmpty = false;
	}

	public void setAllProbsToZero() {
		for (int i = 0; i < probs.length; i++) {
			setProbAtRank(i, 0);
		}
		this.playerID = null;
		isRevealed = false;
		hasMoved = false;
		isEmpty = true;
	}

	public void setAllProbs(double[] newProbs) {
		for (int i = 0; i < newProbs.length; i++) {
			this.probs[i] = newProbs[i];
		}
		isEmpty = false;
	}

	public void setRevealed(int rank) {
		setAllProbsToZero();
		setProbAtRank(rank, 100);
		isRevealed = true;
	}

	public boolean getRevealed() {
		return isRevealed;
	}
	
	public void hasMoved(){
		hasMoved= true;
		probs[0] = 0;
		probs[11]= 0;
	}
	
	public boolean getMoved(){
		return hasMoved;
	}
	
	public boolean getEmpty(){
		return isEmpty;
	}

	public double getMaxProb(int rank) {
		if (rank == 0) {
			return 100;
		}
		if (rank == 1) {
			return 1;
		}
		if (rank == 2) {
			return 8;
		}
		if (rank == 3) {
			return 5;
		}
		if (rank == 4) {
			return 4;
		}
		if (rank == 5) {
			return 4;
		}
		if (rank == 6) {
			return 4;
		}
		if (rank == 7) {
			return 3;
		}
		if (rank == 8) {
			return 2;
		}
		if (rank == 9) {
			return 1;
		}
		if (rank == 10) {
			return 1;
		}
		if (rank == 11) {
			return 6;
		}
		return 0;

	}

}
