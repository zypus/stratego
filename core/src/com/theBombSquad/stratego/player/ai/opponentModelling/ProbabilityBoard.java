package com.theBombSquad.stratego.player.ai.opponentModelling;

import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;

public class ProbabilityBoard {

	ProbabilityTile[][] board;
	PlayerID playerID1;
	PlayerID playerID2;
	PlayerID noPlayer = null;
	
	/**
	 * create probabilityboard
	 * first give player on downside of board
	 */
	public ProbabilityBoard(PlayerID playerID1, PlayerID playerID2){
		this.playerID1 = playerID1;
		this.playerID2 = playerID2;
		this.board = makeFirstBoard();
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

	public ProbabilityTile[][] makeFirstBoard(){
		
		double[] a11 = {8,1,2,6.25,10,10,3.5,5.25,1.5,0.5,0.5,24};
		double[] a12 = {4,1,2,6.25,10,10,3.5,5.25,1.5,0.5,0.5,24};
		double[] a13 = {7,1,2,6.25,10,10,3.5,5.25,1.5,0.5,0.5,24};
		double[] a14 = {7,1,2,6.25,10,10,3.5,5.25,1.5,0.5,0.5,24};
		double[] a15 = {5,1.25,2,6.25,10,10,3.5,5.25,1.5,0.5,0.5,24};
		
		double[] a21 = {3,2.75,28,20,10,10,8,11.25,4,1,1,16.5};
		double[] a22 = {1.5,3.25,28,20,10,10,8,11.25,6,1.75,1.75,16.5};
		double[] a23 = {2,5.5,28,20,10,10,8,11.25,9,1.75,1.75,16.5};
		double[] a24 = {2,6,28,20,10,10,8,11.25,8,1.75,1.75,16.5};
		double[] a25 = {1.5,6,28,20,10,10,8,11.25,5,1.75,1.75,16.5};
		
		double[] a31 = {1,1.75,18,15,10,10,16,11.5,5,2,2,15};
		double[] a32 = {0.5,2.25,18,15,10,10,16,11.5,7,3,3,12};
		double[] a33 = {1,4.5,18,15,10,10,16,11.25,11.5,6,6,9};
		double[] a34 = {1,5,18,15,10,10,16,11.25,9,6,6,9};
		double[] a35 = {0.75,5,18,15,10,10,16,11.5,6.5,5.75,5.75,12};
		
		double[] a41 = {0.25,0.25,40,2.5,10,10,12.5,1.5,2,1,1,10.5};
		double[] a42 = {0.25,0.25,40,2.5,10,10,12.5,1.5,2,1,1,10.5};
		double[] a43 = {2,1,20,15,10,10,12.5,3,9,5.75,5.75,4.5};
		double[] a44 = {2,1,20,15,10,10,12.5,3,7,8,8,4.5};
		double[] a45 = {0.25,0.25,40,2.5,10,10,12.5,1.5,1.5,1,1,10.5};
		
		ProbabilityTile t111 = new ProbabilityTile(playerID1, a11);
		ProbabilityTile t112 = new ProbabilityTile(playerID1, a12);
		ProbabilityTile t113 = new ProbabilityTile(playerID1, a13);
		ProbabilityTile t114 = new ProbabilityTile(playerID1, a14);
		ProbabilityTile t115 = new ProbabilityTile(playerID1, a15);
		ProbabilityTile t121 = new ProbabilityTile(playerID1, a21);
		ProbabilityTile t122 = new ProbabilityTile(playerID1, a22);
		ProbabilityTile t123 = new ProbabilityTile(playerID1, a23);
		ProbabilityTile t124 = new ProbabilityTile(playerID1, a24);
		ProbabilityTile t125 = new ProbabilityTile(playerID1, a25);
		ProbabilityTile t131 = new ProbabilityTile(playerID1, a31);
		ProbabilityTile t132 = new ProbabilityTile(playerID1, a32);
		ProbabilityTile t133 = new ProbabilityTile(playerID1, a33);
		ProbabilityTile t134 = new ProbabilityTile(playerID1, a34);
		ProbabilityTile t135 = new ProbabilityTile(playerID1, a35);
		ProbabilityTile t141 = new ProbabilityTile(playerID1, a41);
		ProbabilityTile t142 = new ProbabilityTile(playerID1, a42);
		ProbabilityTile t143 = new ProbabilityTile(playerID1, a43);
		ProbabilityTile t144 = new ProbabilityTile(playerID1, a44);
		ProbabilityTile t145 = new ProbabilityTile(playerID1, a45);
	
		ProbabilityTile t211 = new ProbabilityTile(playerID2, a11);
		ProbabilityTile t212 = new ProbabilityTile(playerID2, a12);
		ProbabilityTile t213 = new ProbabilityTile(playerID2, a13);
		ProbabilityTile t214 = new ProbabilityTile(playerID2, a14);
		ProbabilityTile t215 = new ProbabilityTile(playerID2, a15);
		ProbabilityTile t221 = new ProbabilityTile(playerID2, a21);
		ProbabilityTile t222 = new ProbabilityTile(playerID2, a22);
		ProbabilityTile t223 = new ProbabilityTile(playerID2, a23);
		ProbabilityTile t224 = new ProbabilityTile(playerID2, a24);
		ProbabilityTile t225 = new ProbabilityTile(playerID2, a25);
		ProbabilityTile t231 = new ProbabilityTile(playerID2, a31);
		ProbabilityTile t232 = new ProbabilityTile(playerID2, a32);
		ProbabilityTile t233 = new ProbabilityTile(playerID2, a33);
		ProbabilityTile t234 = new ProbabilityTile(playerID2, a34);
		ProbabilityTile t235 = new ProbabilityTile(playerID2, a35);
		ProbabilityTile t241 = new ProbabilityTile(playerID2, a41);
		ProbabilityTile t242 = new ProbabilityTile(playerID2, a42);
		ProbabilityTile t243 = new ProbabilityTile(playerID2, a43);
		ProbabilityTile t244 = new ProbabilityTile(playerID2, a44);
		ProbabilityTile t245 = new ProbabilityTile(playerID2, a45);
		
		double[] a00 = {0,0,0,0,0,0,0,0,0,0,0,0};
		ProbabilityTile t000 = new ProbabilityTile(noPlayer, a00);
		
		ProbabilityTile[][] basicBoard = {
				{t211,t212,t213,t214,t215,t215,t214,t213,t212,t211},
				{t221,t222,t223,t224,t225,t225,t224,t223,t222,t221},
				{t231,t232,t233,t234,t235,t235,t234,t233,t232,t231},
				{t241,t242,t243,t244,t245,t245,t244,t243,t242,t241},
				{t000,t000,t000,t000,t000,t000,t000,t000,t000,t000},
				{t000,t000,t000,t000,t000,t000,t000,t000,t000,t000},
				{t141,t142,t143,t144,t145,t145,t144,t143,t142,t141},
				{t131,t132,t133,t134,t135,t135,t134,t133,t132,t131},
				{t121,t122,t123,t124,t125,t125,t124,t123,t122,t121},
				{t111,t112,t113,t114,t115,t115,t114,t113,t112,t111}
		};
		return basicBoard;
	}
	
	
	
}
