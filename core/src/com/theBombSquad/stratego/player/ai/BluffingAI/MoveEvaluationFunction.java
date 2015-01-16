package com.theBombSquad.stratego.player.ai.BluffingAI;

import java.util.ArrayList;

import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.AIGameState;
import com.theBombSquad.stratego.player.ai.AIUnit;

public class MoveEvaluationFunction {

	private double flagReward = 200;
	private double bombReward = 100;
	private double spyReward = 100;
	private double marshalReward = 150;

	private ArrayList<AIUnit> toUnitsRevealed;
	private ArrayList<AIUnit> toUnitsUnrevealed;
	private ArrayList<AIUnit> fromUnitsRevealed;
	private ArrayList<AIUnit> fromUnitsUnrevealed;
	private AIUnit encounterUnit;
	private Move move;
	private AIGameState state;
	private int fromX;
	private int fromY;
	private int toX;
	private int toY;
	private int rank;
	private Unit unit;
	private double evaluation;
	private double marshalRevealReward;

	public double evaluateMove(Move move, AIGameState state) {
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
		encounterUnit = null;
		this.move = move;
		this.state = state;

		findUnitsAround();

		evaluateForEncounter();
		evaluateForUnitsRevealed();
		evaluateForUnitsUnrevealed();

		return evaluation;
	}

	private void evaluateForEncounter() {
		if (encounterUnit != null) {
			if(rank==2&&encounterUnit.getUnitReference().getType().getRank()==-2){
				evaluation=evaluation+30*(encounterUnit.getHighestProbabilityRank()-rank);
			}
			// if it is probable to attack the flag
			if (encounterUnit.getProbabilityFor(UnitType.FLAG) > 0.4) {
				evaluation = evaluation + flagReward;
			}
			//if a spy attacks and uncover 
			
			// else if it is bomb
			else if (encounterUnit.getProbabilityFor(UnitType.BOMB) > 0.4
					|| encounterUnit.getUnitReference().getType().getRank() == 11) {
				if (rank == 3) {
					evaluation = evaluation + bombReward;
				} else if (rank == 1) {
					evaluation = evaluation - bombReward;
				} else {
					evaluation = evaluation - rank / 5 * bombReward;
				}
			}
			// else if it is a spy
			else if (encounterUnit.getProbabilityFor(UnitType.SPY) > 0.4
					|| encounterUnit.getUnitReference().getType().getRank() == 1) {
				if (rank == 1) {
					evaluation = evaluation - spyReward;
				} else {
					evaluation = evaluation + spyReward - (rank - 2) * 10;
				}
			} else if (encounterUnit.getProbabilityFor(UnitType.MARSHAL) > 0.4
					|| encounterUnit.getUnitReference().getType().getRank() == 10) {
				if (rank == 1) {
					evaluation = evaluation +2*marshalReward;
				} else {
					if (encounterUnit.getUnitReference().getType().getRank() == -2) {
						evaluation = evaluation + marshalRevealReward
								- (rank - 2) * 10;
						System.out.println("Encounter marshal hidden");
					} else {
						evaluation = evaluation - 2*marshalReward + (10 - rank)
								* 10;
						System.out.println("encounter marshal revealed");
					}
				}
			} else {
				if (encounterUnit.getUnitReference().getType().getRank() == -2) {
					toUnitsUnrevealed.add(encounterUnit);
				} else {
					toUnitsRevealed.add(encounterUnit);
				}
			}

		}
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
				evaluation = evaluation + 10
						* (threeMostProbableRanks.get(j) - rank) / 2;
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

	}

	private void evaluateForUnitsRevealed() {
		// calculating evaluation for toUnitsRevealed
		for (int i = 0; i < toUnitsRevealed.size(); i++) {

			if (rank
					- toUnitsRevealed.get(i).getUnitReference().getType()
							.getRank() > 0) {
				evaluation = evaluation
						+ 3
						* (100 - (rank - toUnitsRevealed.get(i)
								.getUnitReference().getType().getRank()) * 10);

			} else if (rank
					- toUnitsRevealed.get(i).getUnitReference().getType()
							.getRank() < 0) {
				evaluation = evaluation
						+ 3
						* (-100 - (rank - toUnitsRevealed.get(i)
								.getUnitReference().getType().getRank()) * 10);

			}

		}

		// calculating evaluation for fromUnitsRevealed
		for (int i = 0; i < fromUnitsRevealed.size(); i++) {

			if (fromUnitsRevealed.get(i).getUnitReference().getType().getRank()
					- rank != 0) {

				if (fromUnitsRevealed.get(i).getUnitReference().getType()
						.getRank()
						- rank != 0) {
					evaluation = evaluation
							+ 3
							* (fromUnitsRevealed.get(i).getUnitReference()
									.getType().getRank() - rank) * 10;

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
						&& ((toY > fromY && j >= 0)
								|| (toY < fromY && j <= 0)
								|| (toX > fromX && i >=0) || (toX < fromX &&i <= 0))) {
					if (toX + i >= 0 && toX + i <= 9 && toY + j >= 0
							&& toY + j <= 9) {
						if (!state.getAIUnit(toX + i, toY + j)
								.getUnitReference().isAir()
								&& !state.getAIUnit(toX + i, toY + j)
										.getUnitReference().isLake()) {
							if (unit.getOwner().getOpponent() == state
									.getAIUnit(toX + i, toY + j)
									.getUnitReference().getOwner()) {
								
								if (i==0 && j==0) {
									encounterUnit = state.getAIUnit(toX + i,
											toY + j);
									System.out.println("Encounter");
								}

								else if (state.getAIUnit(toX + i, toY + j)
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
						&& ((toY > fromY && j <= 0)
								|| (toY < fromY && j >= 0)
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
}
