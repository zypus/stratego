package com.theBombSquad.stratego.player.ai.BluffingAI;

import java.util.ArrayList;

import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.ai.AIGameState;
import com.theBombSquad.stratego.player.ai.AIUnit;

public class BluffingMoveEvaluation {
	private double spyPenalty = 100;
	private double minerPenalty = 50;
	private double encounterPenalty = 150;
	private double scoutJumpPenalty = 100;
	private ArrayList<AIUnit> toUnitsRevealed;
	private ArrayList<AIUnit> toUnitsUnrevealed;
	private ArrayList<AIUnit> fromUnitsRevealed;
	private ArrayList<AIUnit> fromUnitsUnrevealed;
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
		toX = move.getToX();
		toY = move.getToY();
		toUnitsRevealed = new ArrayList<AIUnit>();
		toUnitsUnrevealed = new ArrayList<AIUnit>();
		fromUnitsRevealed = new ArrayList<AIUnit>();
		fromUnitsUnrevealed = new ArrayList<AIUnit>();
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
			// filling arraylist to have the size of 3
			if (threeMostProbableRanks.size() < 2) {
				threeHighestProbabilities.add(threeHighestProbabilities.get(0));
				threeMostProbableRanks.add(threeMostProbableRanks.get(0));
			}

			if (threeMostProbableRanks.size() < 3) {
				if (Math.max(threeHighestProbabilities.get(0),
						threeHighestProbabilities.get(1)) == threeHighestProbabilities
						.get(0)) {
					threeHighestProbabilities.add(threeHighestProbabilities
							.get(0));
					threeMostProbableRanks.add(threeMostProbableRanks.get(0));
				} else {
					threeHighestProbabilities.add(threeHighestProbabilities
							.get(1));
					threeMostProbableRanks.add(threeMostProbableRanks.get(1));
				}
			}
			// evaluating
			for (int j = 0; j < threeMostProbableRanks.size(); j++) {
				if (rank - threeMostProbableRanks.get(j) > 0) {

					evaluation = evaluation
							+ (100 - (rank - threeMostProbableRanks.get(j)) * 10)
							/ 2;
				} else if (rank - threeMostProbableRanks.get(j) < 0) {

					evaluation = evaluation
							+ (-100 - (rank - threeMostProbableRanks.get(j)) * 10)
							/ 2;
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

			// filling arraylist to have the size of 3
			if (threeMostProbableRanks.size() < 2) {
				threeHighestProbabilities.add(threeHighestProbabilities.get(0));
				threeMostProbableRanks.add(threeMostProbableRanks.get(0));
			}

			if (threeMostProbableRanks.size() < 3) {
				if (Math.max(threeHighestProbabilities.get(0),
						threeHighestProbabilities.get(1)) == threeHighestProbabilities
						.get(0)) {
					threeHighestProbabilities.add(threeHighestProbabilities
							.get(0));
					threeMostProbableRanks.add(threeMostProbableRanks.get(0));
				} else {
					threeHighestProbabilities.add(threeHighestProbabilities
							.get(1));
					threeMostProbableRanks.add(threeMostProbableRanks.get(1));
				}
			}
			// evaluating
			for (int j = 0; j < threeMostProbableRanks.size(); j++) {

				evaluation = evaluation + 10
						* (threeMostProbableRanks.get(j) - rank) / 2;
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
								.getRank() - rank) * 10;

			}
		}

		// calculating evaluation for fromUnitsRevealed
		for (int i = 0; i < fromUnitsRevealed.size(); i++) {

			if (fromUnitsRevealed.get(i).getUnitReference().getType().getRank()
					- rank != 0) {

				if (rank
						- fromUnitsRevealed.get(i).getUnitReference().getType()
								.getRank() > 0) {
					evaluation = evaluation
							+ 3
							* (100 - (rank - fromUnitsRevealed.get(i)
									.getUnitReference().getType().getRank()) * 10);

				} else if (rank
						- fromUnitsRevealed.get(i).getUnitReference().getType()
								.getRank() < 0) {
					evaluation = evaluation
							+ 3
							* (-100 - (rank - fromUnitsRevealed.get(i)
									.getUnitReference().getType().getRank()) * 10);

				}

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
						&& ((toY > fromY && j >= 0) || (toY < fromY && j <= 0)
								|| (toX > fromX && i >= 0) || (toX < fromX && i <= 0))) {
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
										.getUnitReference().getType().getRank() == -2) {
									toUnitsUnrevealed.add(state.getAIUnit(toX
											+ i, toY + j));

								} else {
									toUnitsRevealed.add(state.getAIUnit(
											toX + i, toY + j));

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
						&& ((toY > fromY && j <= 0) || (toY < fromY && j >= 0)
								|| (toX > fromX && i <= 0) || (toX < fromX && i >= 0))) {
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

								} else {
									fromUnitsRevealed.add(state.getAIUnit(fromX
											+ i, fromY + j));

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
		// scoutJumpPenalty
		if (unit.getType().getRank() == 2) {

			if (Math.abs(Math.abs(toX - fromX) + Math.abs(toY - fromY)) > 1) {

				evaluation = evaluation - scoutJumpPenalty;

			}

		}
	}
}
