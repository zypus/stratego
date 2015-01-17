package com.theBombSquad.stratego.player.ai.opponentModelling;

import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;

public class ProbabilityBoard {

	ProbabilityTile[][] board;
	int[]  amountRevealed = new int[12];
	PlayerID playerID1;
	PlayerID playerID2;
	PlayerID noPlayer = null;

	/**
	 * create probabilityboard first give player on downside of board
	 */
	public ProbabilityBoard(PlayerID playerID1, PlayerID playerID2) {
		this.playerID1 = playerID1;
		this.playerID2 = playerID2;
		this.board = makeFirstBoard();
	}

	public void moveMade(Move move, GameView gameView) {
		int fromX = move.getFromX();
		int toX = move.getToX();
		int fromY = move.getFromY();
		int toY = move.getToY();
		
		board[fromY][fromX].hasMoved();
		
		ProbabilityTile attacker = board[fromY][fromX];
		PlayerID defender = board[toY][toX].getPlayerID();

		board[fromY][fromX].setAllProbsToZero();

		if (move.hasEncounter() == true) {
			Unit victor = move.getEncounter().getVictoriousUnit();
			PlayerID player = victor.getOwner();
			if (player == null) {
				// both 0
				board[toY][toX].setAllProbsToZero();
			} else if (player == attacker.getPlayerID()) {
				// attacker wins
				board[toY][toX] = attacker;
				
				
				//
			}
		}		
		
		// if known through last move
		if (!gameView.isUnknown(toX, toY) && !board[toY][toX].getRevealed()) {
			int rank = gameView.getUnit(toX, toY).getType().getRank();
			board[toY][toX].setRevealed(rank);
			amountRevealed[rank]++;
			//if max amount of pieces is found
			if(amountRevealed[rank] == (board[toY][toX].getMaxProb(rank))){
				for(int y = 0; y < board.length; y++){
					for( int x = 0; x < board[y].length; x++){
						board[toY][toX].setProbAtRank(rank, 0);
					}
				}
			}
			//else, decrease
			else{
				for(int y = 0; y < board.length; y++){
					for( int x = 0; x < board[y].length; x++){
						board[toY][toX].setProbAtRank(rank, (board[toY][toX].getProbAtRank(rank) / (amountRevealed[rank]+1) * (amountRevealed[rank])));
					}
				}
			}
			
			//if 8/9, increase for spy
			if(rank == 9 || rank == 8){
				for(int i = 0; i < 3; i++){
					for(int j = 0; j < 3; j++){
						if(!board[toY+i][toX+j].getRevealed()&&!board[toY+i][toX+j].getEmpty()&&gameView.getUnit(toX, toY).getId()==gameView.getUnit(toX+j, toY+i).getId()){
							double newProb = board[toY+i][toX+j].getProbAtRank(1)+ board[toY+i][toX+j].getProbAtRank(1)*0.25;
							board[toY+i][toX+j].setProbAtRank(1, newProb);
						}
					}
				}
			}
			
			//if marshal, increase flag on that side
			//if marshal, 9 on other side
			
			//if bomb
			if(rank == 11){
				if(board[toY][toX].getPlayerID() == playerID1){
					if(!board[toY+1][toX].getRevealed()&&!board[toY+1][toX].getEmpty()&&defender == board[toY+1][toX].getPlayerID()){
						board[toY+1][toX].setProbAtRank(0, (board[toY+1][toX].getProbAtRank(0)+(board[toY+1][toX].getProbAtRank(0)*0.2)));
					}
					if(!board[toY-1][toX].getRevealed()&&!board[toY-1][toX].getEmpty()&&defender == board[toY-1][toX].getPlayerID()){
						board[toY-1][toX].setProbAtRank(0, (board[toY-1][toX].getProbAtRank(0)+(board[toY-1][toX].getProbAtRank(0)*0.08)));
					}
				}else{
					if(!board[toY-1][toX].getRevealed()&&!board[toY-1][toX].getEmpty()&&defender == board[toY-1][toX].getPlayerID()){
						board[toY-1][toX].setProbAtRank(0, (board[toY-1][toX].getProbAtRank(0)+(board[toY-1][toX].getProbAtRank(0)*0.2)));
					}
					if(!board[toY+1][toX].getRevealed()&&!board[toY+1][toX].getEmpty()&&defender == board[toY+1][toX].getPlayerID()){
						board[toY+1][toX].setProbAtRank(0, (board[toY+1][toX].getProbAtRank(0)+(board[toY+1][toX].getProbAtRank(0)*0.08)));
					}
				}
				if(fromX > toX||fromX == toX){
					if(!board[toY][toX-1].getRevealed()&&!board[toY][toX-1].getEmpty()&&defender == board[toY][toX-1].getPlayerID()){
						board[toY][toX-1].setProbAtRank(0, (board[toY][toX-1].getProbAtRank(0)+(board[toY][toX-1].getProbAtRank(0)*0.13)));
					}
				}	
				if(fromX < toX||fromX == toX){
					if(!board[toY][toX+1].getRevealed()&&!board[toY][toX+1].getEmpty()&&defender == board[toY][toX+1].getPlayerID()){
						board[toY][toX+1].setProbAtRank(0, (board[toY][toX+1].getProbAtRank(0)+(board[toY][toX+1].getProbAtRank(0)*0.13)));
					}
				}
			}
			
		}
		
		
		//if moved piece is unrevealed (so, no encounter!)
		if(!board[toY][toX].getRevealed()){
			
			//Moved, so not flag or bomb
			
		// 		going towards opponent units
			for (int i = -2; i <= 2; i++) {
				for (int j = -2; j <= 2; j++) {
					if ((j <= 2 - i && j <= 2 + i && j >= -2 + i && j >= -2 - i)
								&& ((toY > fromY && j >= 0) || (toY < fromY && j <= 0)
								|| (toX > fromX && i >= 0) || (toX < fromX && i <= 0))) {
						if (toX + i >= 0 && toX + i <= 9 && toY + j >= 0 && toY + j <= 9) {
							if (!(gameView.getUnit(toX + i, toY + j).getType().getRank() == -1)) {
								if (gameView.getUnit(toX, toY).getOwner().getOpponent() == gameView.getUnit(toX + i, toY + j).getOwner()) {
									if (!gameView.getUnit(toX + i, toY + j).isUnknown()) {
										int rankOpp = gameView.getUnit(toX + i, toY + j).getType().getRank();
										for (int r = rankOpp+1; r < board[toX][toY].getAllProbs().length - 1; r++) {
											double oldProb = board[toX][toY].getProbAtRank(r);
											double newProb = oldProb + ((0.8 / r)* board[toX][toY].getMaxProb(r) * 
													(0.5 / (Math.abs(i) * Math.abs(j))));
											board[toX][toY].setProbAtRank(r,newProb);
										}
										//coming towards bomb = sapper
										if (rankOpp==11) {
											double oldProb = board[toX][toY].getProbAtRank(3);
											double newProb = oldProb + ((0.8 / 3)* board[toX][toY].getMaxProb(3) * 
													(0.5 / (Math.abs(i) * Math.abs(j))));
											board[toX][toY].setProbAtRank(3,newProb);
										}
										//coming towards marshal = spy
										if (rankOpp==10) {
											double oldProb = board[toX][toY].getProbAtRank(1);
											double newProb = oldProb + ((0.8 / 1)* board[toX][toY].getMaxProb(1) * 
													(0.5 / (Math.abs(i) * Math.abs(j))));
											board[toX][toY].setProbAtRank(1,newProb);
										}
									}
								}
							}
						}
					}
				}
			}

			//running from opponent units
			for (int i = -2; i <= 2; i++) {
				for (int j = -2; j <= 2; j++) {
					if ((j <= 2 - i && j <= 2 + i && j >= -2 + i && j >= -2 - i)
							&& ((toY > fromY && j <= 0) || (toY < fromY && j >= 0)
									|| (toX > fromX && i <= 0) || (toX < fromX && i >= 0))) {
						if (toX + i >= 0 && toX + i <= 9 && toY + j >= 0 && toY + j <= 9) {
							if (!(gameView.getUnit(toX + i, toY + j).getType().getRank() == -1)) {
								if (gameView.getUnit(toX, toY).getOwner().getOpponent() == gameView.getUnit(toX + i,toY + j).getOwner()) {
									if (!gameView.getUnit(toX + i, toY + j).isUnknown()) {
										int rankOpp = gameView.getUnit(toX + i, toY + j).getType().getRank();
										for (int r = 1; r <= rankOpp; r++) {
											double oldProb = board[toX][toY].getProbAtRank(r);
											double newProb = oldProb- ((1 / r)* board[toX][toY].getMaxProb(r) 
													* (0.5 / (Math.abs(i) * Math.abs(j))));
											board[toX][toY].setProbAtRank(r,newProb);
										}
									}
								}
							}
						}
					}
				}
			}
		}	
		//go through all units and make them add up to max prob, then divide through max prob, then * 100 
		for(int u = 0; u < 12;u++){
			double totalProb = 0;
			for(int cy = 0; cy < board.length; cy++){
				for(int cx = 0; cx < board[0].length; cx++){
					totalProb = totalProb + board[cy][cx].getProbAtRank(u);
				}
			}
			double division = (totalProb/100);
			for(int cy = 0; cy < board.length; cy++){
				for(int cx = 0; cx < board[0].length; cx++){
					double newProb = board[cy][cx].getProbAtRank(u)/division;
					board[cy][cx].setProbAtRank(u, newProb);
				}
			}
		}
	}
	
	public double[] ruleProbabilityTile(int x, int y){
		double totalProb = 0;
		double[] tempProb = new double[12];
		for(int u = 0; u < board.length; u++){
			totalProb = totalProb + board[y][x].getProbAtRank(u);
			tempProb[u] = board[y][x].getProbAtRank(u);
		}
		for(int u = 0; u < board.length; u++){
			tempProb[u] = tempProb[u]/totalProb*100;
		}
		return tempProb;
	}
	
	public ProbabilityTile[][] ruleProbabilityBoard(){
		return board;
	}
	
	public double[][] ruleProbabilityRank(int rank){
		double[][] newBoard = new double[10][10];
		for(int cy = 0; cy < board.length; cy++){
			for(int cx = 0; cx < board[0].length; cx++){
				newBoard[cy][cx] = board[cy][cx].getProbAtRank(rank);
			}
		}
		return newBoard;
	}

	public ProbabilityTile[][] makeFirstBoard() {

		double[] a11 = { 8, 1, 2, 6.25, 10, 10, 3.5, 5.25, 1.5, 0.5, 0.5, 24 };
		double[] a12 = { 4, 1, 2, 6.25, 10, 10, 3.5, 5.25, 1.5, 0.5, 0.5, 24 };
		double[] a13 = { 7, 1, 2, 6.25, 10, 10, 3.5, 5.25, 1.5, 0.5, 0.5, 24 };
		double[] a14 = { 7, 1, 2, 6.25, 10, 10, 3.5, 5.25, 1.5, 0.5, 0.5, 24 };
		double[] a15 = { 5, 1.25, 2, 6.25, 10, 10, 3.5, 5.25, 1.5, 0.5, 0.5, 24 };

		double[] a21 = { 3, 2.75, 28, 20, 10, 10, 8, 11.25, 4, 1, 1, 16.5 };
		double[] a22 = { 1.5, 3.25, 28, 20, 10, 10, 8, 11.25, 6, 1.75, 1.75,
				16.5 };
		double[] a23 = { 2, 5.5, 28, 20, 10, 10, 8, 11.25, 9, 1.75, 1.75, 16.5 };
		double[] a24 = { 2, 6, 28, 20, 10, 10, 8, 11.25, 8, 1.75, 1.75, 16.5 };
		double[] a25 = { 1.5, 6, 28, 20, 10, 10, 8, 11.25, 5, 1.75, 1.75, 16.5 };

		double[] a31 = { 1, 1.75, 18, 15, 10, 10, 16, 11.5, 5, 2, 2, 15 };
		double[] a32 = { 0.5, 2.25, 18, 15, 10, 10, 16, 11.5, 7, 3, 3, 12 };
		double[] a33 = { 1, 4.5, 18, 15, 10, 10, 16, 11.25, 11.5, 6, 6, 9 };
		double[] a34 = { 1, 5, 18, 15, 10, 10, 16, 11.25, 9, 6, 6, 9 };
		double[] a35 = { 0.75, 5, 18, 15, 10, 10, 16, 11.5, 6.5, 5.75, 5.75, 12 };

		double[] a41 = { 0.25, 0.25, 40, 2.5, 10, 10, 12.5, 1.5, 2, 1, 1, 10.5 };
		double[] a42 = { 0.25, 0.25, 40, 2.5, 10, 10, 12.5, 1.5, 2, 1, 1, 10.5 };
		double[] a43 = { 2, 1, 20, 15, 10, 10, 12.5, 3, 9, 5.75, 5.75, 4.5 };
		double[] a44 = { 2, 1, 20, 15, 10, 10, 12.5, 3, 7, 8, 8, 4.5 };
		double[] a45 = { 0.25, 0.25, 40, 2.5, 10, 10, 12.5, 1.5, 1.5, 1, 1,
				10.5 };

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

		double[] a00 = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		ProbabilityTile t000 = new ProbabilityTile(noPlayer, a00);

		ProbabilityTile[][] basicBoard = {
				{ t211, t212, t213, t214, t215, t215, t214, t213, t212, t211 },
				{ t221, t222, t223, t224, t225, t225, t224, t223, t222, t221 },
				{ t231, t232, t233, t234, t235, t235, t234, t233, t232, t231 },
				{ t241, t242, t243, t244, t245, t245, t244, t243, t242, t241 },
				{ t000, t000, t000, t000, t000, t000, t000, t000, t000, t000 },
				{ t000, t000, t000, t000, t000, t000, t000, t000, t000, t000 },
				{ t141, t142, t143, t144, t145, t145, t144, t143, t142, t141 },
				{ t131, t132, t133, t134, t135, t135, t134, t133, t132, t131 },
				{ t121, t122, t123, t124, t125, t125, t124, t123, t122, t121 },
				{ t111, t112, t113, t114, t115, t115, t114, t113, t112, t111 } };
		return basicBoard;
	}

}
