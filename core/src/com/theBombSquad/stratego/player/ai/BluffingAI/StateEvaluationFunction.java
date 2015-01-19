package com.theBombSquad.stratego.player.ai.BluffingAI;

import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.AIGameState;
import com.theBombSquad.stratego.player.ai.AIGameState.PlayerInformation;

public class StateEvaluationFunction {

	private int valueOfMarshal = 400;
	private int valueOfGeneral = 200;
	private int valueOfColonel = 100;
	private int valueOfMajor = 75;
	private int valueOfCaptain = 50;
	private int valueOfLieutenant = 25;
	private int valueOfSergeant = 15;
	private int valueOfSapper = 30;
	private int valueOfScout = 30;
	private int valueOfSpy = 200;
	private int valueOfBomb = 30;
	private int valueOfFlag = 10000;
	private int[] unitValues = new int[] { 1000, 200, 30, 30, 15, 25, 50, 75,
			100, 200, 400, 30 };
	private int[] unitRevealedPenalties = new int[] { 0, 100, 10, 15, 10, 10,
			15, 20, 25, 30, 50, 20 };

	private int marshalBonusNoSpy = 150;
	private int marshalBonusNoMarshal = 150;
	private int bombBonusNoSapper = 20;
	private int spyPenaltyNoMarshal = 150;
	private int marshalRevealedPenalty = 20;

	private PlayerInformation own;
	private PlayerInformation oponent;

	private int evaluation;
	private float[] defeatedOponent;
	private float[] defeatedOwn;
	private float[] revealedOwn;
	private float[] revealedOponent;
	private AIGameState state;

	public double evaluateState(AIGameState state) {
		evaluation = 0;
		this.state = state;
		own = state.getOwn();
		oponent = state.getOpponent();

		defeatedOwn = own.getDefeated();
		defeatedOponent = oponent.getDefeated();
		revealedOwn = own.getRevealed();
		revealedOponent = oponent.getRevealed();

		evaluatePlayerUnits();
		addPlayerBonuses();
		evaluateOponentUnits();
		addOponentBonuses();
		return evaluation;
	}

	private void addOponentBonuses() {
		// TODO Auto-generated method stub
		// penalties and bonuses for the marshal
		if (defeatedOponent[10] == 0) {
			if (defeatedOwn[1] == 1) {
				evaluation = evaluation - marshalBonusNoSpy;
			} else {
				if (revealedOponent[10] == 1)
					evaluation = evaluation + marshalRevealedPenalty;
			}
			if (defeatedOwn[10] == 1) {
				evaluation = evaluation - marshalBonusNoMarshal;
			} else {
				if (revealedOponent[10] == 1)
					evaluation = evaluation + marshalRevealedPenalty;
			}
		}
		// if no sappers and some bombs
		if (defeatedOponent[3] == 5) {
			for (int i = 0; i < 6 - defeatedOwn[11]; i++) {
				evaluation = evaluation + bombBonusNoSapper;
			}
		}

		int flagX = 0;
		int flagY = 0;
		double maxP=0;
		for (int i = 9; i >= 0; i--) {
			for (int j = 9; j >= 0; j--) {
				if (state.getAIUnit(i, j).getUnitReference().getOwner()
						.getOpponent() == state.getCurrentPlayer()) {
					if (maxP<state.getAIUnit(i, j).getProbabilityFor(UnitType.FLAG)) {
						maxP=state.getAIUnit(i, j).getProbabilityFor(UnitType.FLAG);
						flagX = i;
						flagY = j;
					}
				}
			}
		}
		int min = 100;
		int dist = 0;
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (state.getAIUnit(i, j).getUnitReference().getOwner()
						 == state.getCurrentPlayer()) {
					dist = Math.abs(flagX - i) + Math.abs(flagY - j);
					if (dist < min) {
						min = dist;
					}
				}
			}
		}
		evaluation = evaluation + (20 - dist) * 10;


	}

	private void addPlayerBonuses() {
		// TODO Auto-generated method stub
		// penalties and bonuses for the marshal
		if (defeatedOwn[10] == 0) {
			if (defeatedOponent[1] == 1) {
				evaluation = evaluation + marshalBonusNoSpy;
			} else {
				if (revealedOwn[10] == 1)
					evaluation = evaluation - marshalRevealedPenalty;
			}
			if (defeatedOponent[10] == 1) {
				evaluation = evaluation + marshalBonusNoMarshal;
			} else {
				if (revealedOwn[10] == 1)
					evaluation = evaluation - marshalRevealedPenalty;
			}
		}
		// if no sappers and some bombs
		if (defeatedOwn[3] == 5) {
			for (int i = 0; i < 6 - defeatedOponent[11]; i++) {
				evaluation = evaluation - bombBonusNoSapper;
			}
		}

		int flagX = 0;
		int flagY = 0;
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (state.getAIUnit(i, j).getUnitReference().getType()
						.getRank() == 0) {
					flagX = i;
					flagY = j;
					break;
				}
			}
		}



		int min = 100;
		int dist = 0;
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (state.getAIUnit(i, j).getUnitReference().getOwner()
						.getOpponent() == state.getCurrentPlayer()) {
					dist = Math.abs(flagX - i) + Math.abs(flagY - j);
					if (dist < min) {
						min = dist;
//						System.out.println(dist);
					}
				}
			}
		}
		evaluation = evaluation - (20 - dist) * 10;
	}

	private void evaluatePlayerUnits() {
		// substracting values of defeated units
		for (int i = 0; i < defeatedOwn.length; i++) {
			for (int j = 0; j < defeatedOwn[i]; j++) {
				evaluation = evaluation - unitValues[i];
			}
		}
		// substracting revealed penalties
		for (int i = 0; i < revealedOwn.length; i++) {
			for (int j = 0; j < revealedOwn[i]; j++) {
				evaluation = evaluation - unitRevealedPenalties[i];
			}
		}
	}

	private void evaluateOponentUnits() {
		// adding values of defeated units
		for (int i = 0; i < defeatedOponent.length; i++) {
			for (int j = 0; j < defeatedOponent[i]; j++) {
				evaluation = evaluation + unitValues[i];
			}
		}
		// adding revealed penalties
		for (int i = 0; i < revealedOponent.length; i++) {
			for (int j = 0; j < revealedOponent[i]; j++) {
				evaluation = evaluation + unitRevealedPenalties[i];
			}
		}
	}
}
