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

		ProbabilityTile attacker = new ProbabilityTile(board[fromY][fromX]);
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
		if (!gameView.isUnknown(toX, toY) && !board[toY][toX].getRevealed() && gameView.getUnit(toX, toY)
																					   .getType()
																					   .getRank() != -1) {
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
						if (toX+j < 10 && toY+i < 10) {
							if (!board[toY + i][toX + j].getRevealed() && !board[toY + i][toX + j].getEmpty() && gameView.getUnit(toX, toY)
																														 .getId() == gameView.getUnit(toX + j, toY + i)
																																			 .getId()) {
								double newProb = board[toY + i][toX + j].getProbAtRank(1) + board[toY + i][toX + j].getProbAtRank(1) * 0.25;
								board[toY + i][toX + j].setProbAtRank(1, newProb);
							}
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

		ProbabilityTile t111_01 = new ProbabilityTile(playerID1, a11);
		ProbabilityTile t112_01 = new ProbabilityTile(playerID1, a12);
		ProbabilityTile t113_01 = new ProbabilityTile(playerID1, a13);
		ProbabilityTile t114_01 = new ProbabilityTile(playerID1, a14);
		ProbabilityTile t115_01 = new ProbabilityTile(playerID1, a15);
		ProbabilityTile t121_01 = new ProbabilityTile(playerID1, a21);
		ProbabilityTile t122_01 = new ProbabilityTile(playerID1, a22);
		ProbabilityTile t123_01 = new ProbabilityTile(playerID1, a23);
		ProbabilityTile t124_01 = new ProbabilityTile(playerID1, a24);
		ProbabilityTile t125_01 = new ProbabilityTile(playerID1, a25);
		ProbabilityTile t131_01 = new ProbabilityTile(playerID1, a31);
		ProbabilityTile t132_01 = new ProbabilityTile(playerID1, a32);
		ProbabilityTile t133_01 = new ProbabilityTile(playerID1, a33);
		ProbabilityTile t134_01 = new ProbabilityTile(playerID1, a34);
		ProbabilityTile t135_01 = new ProbabilityTile(playerID1, a35);
		ProbabilityTile t141_01 = new ProbabilityTile(playerID1, a41);
		ProbabilityTile t142_01 = new ProbabilityTile(playerID1, a42);
		ProbabilityTile t143_01 = new ProbabilityTile(playerID1, a43);
		ProbabilityTile t144_01 = new ProbabilityTile(playerID1, a44);
		ProbabilityTile t145_01 = new ProbabilityTile(playerID1, a45);

		ProbabilityTile t111_02 = new ProbabilityTile(t111_01);
		ProbabilityTile t112_02 = new ProbabilityTile(t112_01);
		ProbabilityTile t113_02 = new ProbabilityTile(t113_01);
		ProbabilityTile t114_02 = new ProbabilityTile(t114_01);
		ProbabilityTile t115_02 = new ProbabilityTile(t115_01);
		ProbabilityTile t121_02 = new ProbabilityTile(t121_01);
		ProbabilityTile t122_02 = new ProbabilityTile(t122_01);
		ProbabilityTile t123_02 = new ProbabilityTile(t123_01);
		ProbabilityTile t124_02 = new ProbabilityTile(t124_01);
		ProbabilityTile t125_02 = new ProbabilityTile(t125_01);
		ProbabilityTile t131_02 = new ProbabilityTile(t131_01);
		ProbabilityTile t132_02 = new ProbabilityTile(t132_01);
		ProbabilityTile t133_02 = new ProbabilityTile(t133_01);
		ProbabilityTile t134_02 = new ProbabilityTile(t134_01);
		ProbabilityTile t135_02 = new ProbabilityTile(t135_01);
		ProbabilityTile t141_02 = new ProbabilityTile(t141_01);
		ProbabilityTile t142_02 = new ProbabilityTile(t142_01);
		ProbabilityTile t143_02 = new ProbabilityTile(t143_01);
		ProbabilityTile t144_02 = new ProbabilityTile(t144_01);
		ProbabilityTile t145_02 = new ProbabilityTile(t145_01);

		ProbabilityTile t211_01 = new ProbabilityTile(playerID2, a11);
		ProbabilityTile t212_01 = new ProbabilityTile(playerID2, a12);
		ProbabilityTile t213_01 = new ProbabilityTile(playerID2, a13);
		ProbabilityTile t214_01 = new ProbabilityTile(playerID2, a14);
		ProbabilityTile t215_01 = new ProbabilityTile(playerID2, a15);
		ProbabilityTile t221_01 = new ProbabilityTile(playerID2, a21);
		ProbabilityTile t222_01 = new ProbabilityTile(playerID2, a22);
		ProbabilityTile t223_01 = new ProbabilityTile(playerID2, a23);
		ProbabilityTile t224_01 = new ProbabilityTile(playerID2, a24);
		ProbabilityTile t225_01 = new ProbabilityTile(playerID2, a25);
		ProbabilityTile t231_01 = new ProbabilityTile(playerID2, a31);
		ProbabilityTile t232_01 = new ProbabilityTile(playerID2, a32);
		ProbabilityTile t233_01 = new ProbabilityTile(playerID2, a33);
		ProbabilityTile t234_01 = new ProbabilityTile(playerID2, a34);
		ProbabilityTile t235_01 = new ProbabilityTile(playerID2, a35);
		ProbabilityTile t241_01 = new ProbabilityTile(playerID2, a41);
		ProbabilityTile t242_01 = new ProbabilityTile(playerID2, a42);
		ProbabilityTile t243_01 = new ProbabilityTile(playerID2, a43);
		ProbabilityTile t244_01 = new ProbabilityTile(playerID2, a44);
		ProbabilityTile t245_01 = new ProbabilityTile(playerID2, a45);

		ProbabilityTile t211_02 = new ProbabilityTile(t211_01);
		ProbabilityTile t212_02 = new ProbabilityTile(t212_01);
		ProbabilityTile t213_02 = new ProbabilityTile(t213_01);
		ProbabilityTile t214_02 = new ProbabilityTile(t214_01);
		ProbabilityTile t215_02 = new ProbabilityTile(t215_01);
		ProbabilityTile t221_02 = new ProbabilityTile(t221_01);
		ProbabilityTile t222_02 = new ProbabilityTile(t222_01);
		ProbabilityTile t223_02 = new ProbabilityTile(t223_01);
		ProbabilityTile t224_02 = new ProbabilityTile(t224_01);
		ProbabilityTile t225_02 = new ProbabilityTile(t225_01);
		ProbabilityTile t231_02 = new ProbabilityTile(t231_01);
		ProbabilityTile t232_02 = new ProbabilityTile(t232_01);
		ProbabilityTile t233_02 = new ProbabilityTile(t233_01);
		ProbabilityTile t234_02 = new ProbabilityTile(t234_01);
		ProbabilityTile t235_02 = new ProbabilityTile(t235_01);
		ProbabilityTile t241_02 = new ProbabilityTile(t241_01);
		ProbabilityTile t242_02 = new ProbabilityTile(t242_01);
		ProbabilityTile t243_02 = new ProbabilityTile(t243_01);
		ProbabilityTile t244_02 = new ProbabilityTile(t244_01);
		ProbabilityTile t245_02 = new ProbabilityTile(t245_01);

		double[] a00 = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		ProbabilityTile t000_01 = new ProbabilityTile(noPlayer, a00);
		ProbabilityTile t000_02 = new ProbabilityTile(t000_01);
		ProbabilityTile t000_03 = new ProbabilityTile(t000_01);
		ProbabilityTile t000_04 = new ProbabilityTile(t000_01);
		ProbabilityTile t000_05 = new ProbabilityTile(t000_01);
		ProbabilityTile t000_06 = new ProbabilityTile(t000_01);
		ProbabilityTile t000_07 = new ProbabilityTile(t000_01);
		ProbabilityTile t000_08 = new ProbabilityTile(t000_01);
		ProbabilityTile t000_09 = new ProbabilityTile(t000_01);
		ProbabilityTile t000_10 = new ProbabilityTile(t000_01);
		ProbabilityTile t000_11 = new ProbabilityTile(t000_01);
		ProbabilityTile t000_12 = new ProbabilityTile(t000_01);
		ProbabilityTile t000_13 = new ProbabilityTile(t000_01);
		ProbabilityTile t000_14 = new ProbabilityTile(t000_01);
		ProbabilityTile t000_15 = new ProbabilityTile(t000_01);
		ProbabilityTile t000_16 = new ProbabilityTile(t000_01);
		ProbabilityTile t000_17 = new ProbabilityTile(t000_01);
		ProbabilityTile t000_18 = new ProbabilityTile(t000_01);
		ProbabilityTile t000_19 = new ProbabilityTile(t000_01);
		ProbabilityTile t000_20 = new ProbabilityTile(t000_01);

		ProbabilityTile[][] basicBoard = {
				{ t211_01, t212_01, t213_01, t214_01, t215_01, t215_02, t214_02, t213_02, t212_02, t211_02 },
				{ t221_01, t222_01, t223_01, t224_01, t225_01, t225_02, t224_02, t223_02, t222_02, t221_02 },
				{ t231_01, t232_01, t233_01, t234_01, t235_01, t235_02, t234_02, t233_02, t232_02, t231_02 },
				{ t241_01, t242_01, t243_01, t244_01, t245_01, t245_02, t244_02, t243_02, t242_02, t241_02 },
				{ t000_01, t000_02, t000_03, t000_04, t000_05, t000_06, t000_07, t000_08, t000_09, t000_10 },
				{ t000_11, t000_12, t000_13, t000_14, t000_15, t000_16, t000_17, t000_18, t000_19, t000_20 },
				{ t141_01, t142_01, t143_01, t144_01, t145_01, t145_02, t144_02, t143_02, t142_02, t141_02 },
				{ t131_01, t132_01, t133_01, t134_01, t135_01, t135_02, t134_02, t133_02, t132_02, t131_02 },
				{ t121_01, t122_01, t123_01, t124_01, t125_01, t125_02, t124_02, t123_02, t122_02, t121_02 },
				{ t111_01, t112_01, t113_01, t114_01, t115_01, t115_02, t114_02, t113_02, t112_02, t111_02 } };
		return basicBoard;
	}

}
