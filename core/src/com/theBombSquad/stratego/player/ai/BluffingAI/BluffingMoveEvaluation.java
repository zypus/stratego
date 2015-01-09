package com.theBombSquad.stratego.player.ai.BluffingAI;

import java.util.ArrayList;

import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.ai.AIGameState;
import com.theBombSquad.stratego.player.ai.AIUnit;

public class BluffingMoveEvaluation {
	private double spyPenalty = 100;
	private double minerPenalty = 50;
	private double evaluationConstant = 150;
	private double encounterPenalty = 150;
	private double scoutJumpPenalty = 100;
	private ArrayList<AIUnit> toUnitsRevealed;
	private ArrayList<AIUnit> toUnitsUnrevealed;
	private ArrayList<AIUnit> fromUnitsRevealed;
	private ArrayList<AIUnit> fromUnitsUnrevealed;
	private Move move;
	private AIGameState state;
	private int fromX;
	private int fromY;
	private int toX;
	private int toY;
	private int rank;
	private Unit unit;
	private double evaluation;

	public double evaluateBluff(Move move, AIGameState state) {
		evaluation = 0;
		fromX = move.getFromX();
		fromY = move.getFromY();
		unit = state.getAIUnit(fromX, fromY).getUnitReference();
		rank = state.getAIUnit(fromX, fromY).getUnitReference().getType()
				.getRank();
		// System.out.println(rank);
		toX = move.getToX();
		toY = move.getToY();
		toUnitsRevealed = new ArrayList<AIUnit>();
		toUnitsUnrevealed = new ArrayList<AIUnit>();
		fromUnitsRevealed = new ArrayList<AIUnit>();
		fromUnitsUnrevealed = new ArrayList<AIUnit>();
		this.move = move;
		this.state = state;

		findUnitsAround();
		addPenalties();
		evaluateForUnitsRevealed();
		evaluateForUnitsUnrevealed();

		return evaluation;
	}

	private void evaluateForUnitsUnrevealed() {
		for (int i = 0; i < fromUnitsUnrevealed.size(); i++) {
			float[] prob = fromUnitsUnrevealed.get(i).getProbabilities();
			ArrayList<Integer> threeMostProbableRanks = new ArrayList<Integer>();
			ArrayList<Float> threeHighestProbabilities = new ArrayList<Float>();
			for (int j = 0; j < prob.length; j++) {
				if (threeMostProbableRanks.size() <= 2) {
					threeMostProbableRanks.add(j);
					threeHighestProbabilities.add(prob[j]);
				} else {
					threeMostProbableRanks.add(j);
					threeHighestProbabilities.add(prob[j]);
					int minRank = findMinRank(threeHighestProbabilities);
					threeMostProbableRanks.remove(minRank);
					threeHighestProbabilities.remove(minRank);
				}

			}
			//filling arraylist to have the size of 3
			if(threeMostProbableRanks.size()<2){
				threeHighestProbabilities.add(threeHighestProbabilities.get(0));
				threeMostProbableRanks.add(threeMostProbableRanks.get(0));
			}
			
			if(threeMostProbableRanks.size()<3){
				if(Math.max(threeHighestProbabilities.get(0),threeHighestProbabilities.get(1))==threeHighestProbabilities.get(0)){
				threeHighestProbabilities.add(threeHighestProbabilities.get(0));
				threeMostProbableRanks.add(threeMostProbableRanks.get(0));}
				else{
					threeHighestProbabilities.add(threeHighestProbabilities.get(1));
					threeMostProbableRanks.add(threeMostProbableRanks.get(1));}
				}
			// evaluating
			for (int j = 0; j < threeMostProbableRanks.size(); j++) {
				if (threeMostProbableRanks.get(j) - rank != 0) {
					evaluation = evaluation + 1
							/ (rank - threeMostProbableRanks.get(j))
							* evaluationConstant;
				}
			}

		}

		for (int i = 0; i < toUnitsUnrevealed.size(); i++) {
			float[] prob = toUnitsUnrevealed.get(i).getProbabilities();
			ArrayList<Integer> threeMostProbableRanks = new ArrayList<Integer>();
			ArrayList<Float> threeHighestProbabilities = new ArrayList<Float>();
			for (int j = 0; j < prob.length; j++) {
				if (threeMostProbableRanks.size() <= 2) {
					threeMostProbableRanks.add(j);
					threeHighestProbabilities.add(prob[j]);
				} else {
					threeMostProbableRanks.add(j);
					threeHighestProbabilities.add(prob[j]);
					int minRank = findMinRank(threeHighestProbabilities);
					threeMostProbableRanks.remove(minRank);
					threeHighestProbabilities.remove(minRank);
				}

			}
			
			//filling arraylist to have the size of 3
			if(threeMostProbableRanks.size()<2){
				threeHighestProbabilities.add(threeHighestProbabilities.get(0));
				threeMostProbableRanks.add(threeMostProbableRanks.get(0));
			}
			
			if(threeMostProbableRanks.size()<3){
				if(Math.max(threeHighestProbabilities.get(0),threeHighestProbabilities.get(1))==threeHighestProbabilities.get(0)){
				threeHighestProbabilities.add(threeHighestProbabilities.get(0));
				threeMostProbableRanks.add(threeMostProbableRanks.get(0));}
				else{
					threeHighestProbabilities.add(threeHighestProbabilities.get(1));
					threeMostProbableRanks.add(threeMostProbableRanks.get(1));}
				}
			//evaluating
			for (int j = 0; j < threeMostProbableRanks.size(); j++) {
				if (threeMostProbableRanks.get(j) - rank != 0) {
					evaluation = evaluation + 1
							* (threeMostProbableRanks.get(j) - rank);
				}
			}

		}

	}

	private void evaluateForUnitsRevealed() {
		// calculating evaluation for toUnitsRevealed
		for (int i = 0; i < toUnitsRevealed.size(); i++) {
			if (toUnitsRevealed.get(i).getUnitReference().getType().getRank()
					- rank != 0) {
				evaluation = evaluation
						+ 3
						* (toUnitsRevealed.get(i).getUnitReference().getType()
								.getRank() - rank);
			}
		}

		// calculating evaluation for fromUnitsRevealed
		for (int i = 0; i < fromUnitsRevealed.size(); i++) {
			if (fromUnitsRevealed.get(i).getUnitReference().getType().getRank()
					- rank != 0) {
				evaluation = evaluation
						+ 3
						/ (rank - fromUnitsRevealed.get(i).getUnitReference()
								.getType().getRank()) * evaluationConstant;
			}
		}

	}

	private int findMinRank(ArrayList<Float> list) {
		int minRank = 0;
		for (int i = 1; i < list.size(); i++) {
			if (list.get(i) < list.get(minRank)) {
				minRank = i;
			}
		}
		return minRank;
	}

	private void findUnitsAround() {
		// adding to units
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				if ((j <= 2 - i && j <= 2 + i && j >= -2 + i && j >= -2 - i)
						&& ((toY > fromY && j >= toY)
								|| (toY < fromY && j <= toY)
								|| (toX > fromX && i >= toX) || (toX < fromX && i <= toX))) {
					if (toX + i >= 0 && toX + i <= 9 && toY + j >= 0
							&& toY + j <= 9) {
						if (!state.getAIUnit(toX + i, toY + j)
								.getUnitReference().isAir()
								&& !state.getAIUnit(toX + i, toY + j)
										.getUnitReference().isLake()) {
							if (unit.getOwner().getOpponent() == state
									.getAIUnit(toX + i, toY + j)
									.getUnitReference().getOwner()) {
								if (state.getAIUnit(toX + i, toY + j)
										.getUnitReference().getRevealedInTurn() == -42) {
									toUnitsUnrevealed.add(state.getAIUnit(toX
											+ i, toY + j));
									// System.out.println("FRONT unrevealed " +
									// i + " " + j+" "+ state
									// .getAIUnit(toX + i, toY + j)
									// .getUnitReference().getType().getRank());

								} else {
									toUnitsRevealed.add(state.getAIUnit(
											toX + i, toY + j));
									// System.out.println("Front revealed " + i
									// + " " + j+" "+ state
									// .getAIUnit(toX + i, toY + j)
									// .getUnitReference().getType().getRank());
								}
							}
						}
					}
				}
			}
		}

		// adding from units
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				if ((j <= 2 - i && j <= 2 + i && j >= -2 + i && j >= -2 - i)
						&& ((toY > fromY && j <= toY)
								|| (toY < fromY && j >= toY)
								|| (toX > fromX && i <= toX) || (toX < fromX && i >= toX))) {
					if (fromX + i >= 0 && fromX + i <= 9 && fromY + j >= 0
							&& fromY + j <= 9) {
						if (!state.getAIUnit(fromX + i, fromY + j)
								.getUnitReference().isAir()
								&& !state.getAIUnit(fromX + i, fromY + j)
										.getUnitReference().isLake()) {
							if (unit.getOwner().getOpponent() == state
									.getAIUnit(fromX + i, fromY + j)
									.getUnitReference().getOwner()) {
								if (state.getAIUnit(fromX + i, fromY + j)
										.getUnitReference().getRevealedInTurn() == -42) {
									fromUnitsUnrevealed.add(state.getAIUnit(
											fromX + i, fromY + j));
									// System.out.println("back unrevealed " + i
									// + " " + j+" "+ state
									// .getAIUnit(fromX + i, fromY + j)
									// .getUnitReference().getType().getRank());
								} else {
									fromUnitsRevealed.add(state.getAIUnit(fromX
											+ i, fromY + j));
									// System.out.println("back revealed " + i +
									// " " + j+" "+ state
									// .getAIUnit(fromX + i, fromY + j)
									// .getUnitReference().getType().getRank());
								}
							}
						}
					}
				}
			}
		}
	}

	private void addPenalties() {
		// penalty if bluffing with a spy
		boolean penalty = true;
		if (unit.getType().getRank() == 1) {
			for (int i = 0; i < fromUnitsRevealed.size(); i++) {
				if (fromUnitsRevealed.get(i).getUnitReference().getType()
						.getRank() == 10) {
					evaluation = evaluation + spyPenalty;
					fromUnitsRevealed.remove(i);
					penalty = false;
					break;
				}
			}
			if (penalty) {
				evaluation = evaluation - spyPenalty;
			}
		}
		// penalty if bluffing with a miner
		if (unit.getType().getRank() == 3) {
			evaluation = evaluation - minerPenalty;
		}
		// penalty if there is an encounter
		if (state.getAIUnit(toX, toY).getUnitReference().getType().getRank() != -1) {
			{
				evaluation = evaluation - encounterPenalty;
			}

		}
		// spyJumpPenalty
		if (unit.getType().getRank() == 2) {
			for (int i = 0; i < toUnitsRevealed.size(); i++) {
				if (Math.abs(Math.abs(toX - fromX) + Math.abs(toY - fromY)) > 1) {
					evaluation = evaluation - scoutJumpPenalty;
				}
			}

		}
	}
}
