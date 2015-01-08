package com.theBombSquad.stratego.player.ai.opponentModelling;

import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;

public class ProbabilityBoard {

	ProbabilityTile[][] board = new ProbabilityTile[10][10];
	PlayerID playerID1;
	PlayerID playerID2;
	
	public ProbabilityBoard(PlayerID playerID1, PlayerID playerID2){
		this.playerID1 = playerID1;
		this.playerID2 = playerID2;
		this.board = basicBoard;
	}

	public void moveMade(Move move){
		int fromX = move.getFromX();
		int toX = move.getToX();
		int fromY = move.getFromY();
		int toY = move.getToY();
		
		double[] probsMovedUnit = board[fromY][fromX].getAllProbs();
		PlayerID playerMove = board[fromY][fromX].getPlayerID();
		
		board[fromY][fromX].setAllProbsToZero();
		
		if(move.hasEncounter()== true){
			Unit victor = move.getEncounter().getVictoriousUnit();
			PlayerID player = victor.getOwner();
			if(player == null){
				//both 0
				board[toY][toX].setAllProbsToZero();
			}
			else if(player == playerMove){
				//attacker wins
				board[toY][toX].setAllProbs(probsMovedUnit);
				board[toY][toX].setPlayerID(playerMove);
			}
		}
		
		updateBoard();
		
	}
	
	public void updateBoard(){
		
	}
	
	ProbabilityTile[][] basicBoard = {};
	
	double[] tile1 = {	};
	
	
}
