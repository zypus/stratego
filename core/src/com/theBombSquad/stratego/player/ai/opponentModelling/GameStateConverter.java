package com.theBombSquad.stratego.player.ai.opponentModelling;

import com.theBombSquad.stratego.StrategoConstants;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.ai.AIGameState;
import com.theBombSquad.stratego.player.ai.AIUnit;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created 17/01/15
 */
public class GameStateConverter {

	public static AIGameState convertToAIGameState(ProbabilityBoard pb, AIGameState gameState) {
		AIGameState convertedState = new AIGameState(gameState);
		for (int cx = 0; cx < convertedState.getWidth(); cx++) {
			for (int cy = 0; cy < convertedState.getHeight(); cy++) {
				AIUnit unit = convertedState.getAIUnit(cx,cy);
				for (int i = 3; i < Unit.UnitType.values().length; i++) {
					Unit.UnitType unitType = Unit.UnitType.values()[i];
					float prob = (float)pb.board[cy][cx].getProbAtRank(unitType.getRank());
					unit.setProbabilityFor(unitType, prob);
				}
			}
		}
		return convertedState;
	}

	public static ProbabilityBoard convertToProbabilityBoard(AIGameState gameState) {
		ProbabilityBoard pb = new ProbabilityBoard(gameState.getCurrentPlayer(), gameState.getCurrentPlayer().getOpponent());
		int[] revealed = new int[12];
		for (int i = 3; i < Unit.UnitType.values().length; i++) {
			Unit.UnitType unitType = Unit.UnitType.values()[i];
			AIGameState.PlayerInformation information = gameState.getPlayerInformation(gameState.getCurrentPlayer());
			revealed[unitType.getRank()] = (int)information.getDefeatedFor(unitType) + (int)information.getRevealedFor(unitType);
		}
		pb.amountRevealed = revealed;
		for (int cx = 0; cx < gameState.getWidth(); cx++) {
			for (int cy = 0; cy < gameState.getHeight(); cy++) {
				AIUnit unit = gameState.getAIUnit(cx, cy);
				ProbabilityTile tile = pb.board[cy][cx];
				if (unit.getOwner() != null && unit.getOwner() != StrategoConstants.PlayerID.NEMO) {
					if (unit.getConfirmedUnitType() == null) {
						for (int i = 3; i < Unit.UnitType.values().length; i++) {
							Unit.UnitType unitType = Unit.UnitType.values()[i];
							float prob = unit.getProbabilityFor(unitType);
							tile.setProbAtRank(unitType.getRank(), prob);
						}
					} else {
						tile.setRevealed(unit.getConfirmedUnitType().getRank());
					}
					if (unit.isMoved()) {
						tile.hasMoved();
					}
					tile.setPlayerID(unit.getOwner());
				} else {
					tile.setAllProbsToZero();
					tile.setPlayerID(null);
				}
			}
		}
		return pb;
	}

}
