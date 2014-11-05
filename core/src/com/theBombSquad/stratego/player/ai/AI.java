package com.theBombSquad.stratego.player.ai;

import com.theBombSquad.stratego.gameMechanics.board.Encounter;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static com.theBombSquad.stratego.StrategoConstants.*;
import static com.theBombSquad.stratego.StrategoConstants.PlayerID.*;
import static com.theBombSquad.stratego.gameMechanics.Game.*;
import static com.theBombSquad.stratego.gameMechanics.board.Encounter.CombatResult.*;
import static com.theBombSquad.stratego.gameMechanics.board.Unit.*;
import static com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType.*;
import static java.lang.Math.*;

/**
 * Abstract AI class which gives access to several utility stuff.
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @author Flo
 */
public abstract class AI extends Player {

	public AI(GameView gameView) {
		super(gameView);
	}

	public static List<Move> createAllLegalMovesForUnit(GameView gameView, GameBoard board, int cx, int cy) {
		List<Move> list = new ArrayList<Move>();
		Unit unit = board.getUnit(cx, cy);
		if (unit.getOwner() == gameView.getPlayerID()) {
			if (!unit.getType().equals(BOMB) && !unit.getType().equals(FLAG)) {
				if (unit.getType().equals(SCOUT)) {
					addScoutMoves(gameView, list, cx, cy);
				} else {
					for (int xx = -1; xx <= 1; xx += 2) {
						if (walkable(gameView, board, cx + xx, cy)) {
							Move move = new Move(cx, cy, cx + xx, cy);
							if (gameView.validateMove(move)) {
								list.add(move);
							}
						}
					}
					for (int yy = -1; yy <= 1; yy += 2) {
						if (walkable(gameView, board, cx, cy + yy)) {
							Move move = new Move(cx, cy, cx, cy + yy);
							if (gameView.validateMove(move)) {
								list.add(move);
							}
						}
					}
				}
			}
		}
		return list;
	}

	public static List<Move> createAllLegalMoves(GameView gameView, GameBoard board){
		List<Move> list = new ArrayList<Move>();
		for(int cy=0; cy<board.getHeight(); cy++){
			for(int cx=0; cx<board.getWidth(); cx++){
				list.addAll(createAllLegalMovesForUnit(gameView, board, cx, cy));
			}
		}
		return list;
	}

	private static void addScoutMoves(GameView gameView, List<Move> list, int cx, int cy) {
		// TODO Auto-generated method stub
		boolean up=true;
		boolean left=true;
		boolean right=true;
		boolean down=true;
		int counter=1;
		while(up){
			Move move=new Move(cx,cy,cx+counter,cy);
			if(gameView.validateMove(move)){
				list.add(new Move(cx,cy,cx+counter,cy));
				counter++;
			}
			else{
				counter=1;
				up=false;
			}
		}
		while(down){
			Move move=new Move(cx,cy,cx-counter,cy);
			if(gameView.validateMove(move)){
				list.add(new Move(cx,cy,cx-counter,cy));
				counter++;
			}
			else{
				counter=1;
				down=false;
			}
		}
		while(left){
			Move move=new Move(cx,cy,cx,cy-counter);
			if(gameView.validateMove(move)){
				list.add(new Move(cx,cy,cx,cy-counter));
				counter++;
			}
			else{
				counter=1;
				left=false;
			}
		}
		while(right){
			Move move=new Move(cx,cy,cx,cy+counter);
			if(gameView.validateMove(move)){
				list.add(new Move(cx,cy,cx,cy+counter));
				counter++;
			}
			else{
				counter=1;
				right=false;
			}
		}
	}

	protected static boolean walkable(GameView gameView, GameBoard board, int x, int y){
		return board.isInBounds(x, y) && (gameView.isEnemy(x, y) || gameView.isAir(x, y));
	}

	public static AIGameState createAIGameState(GameView gameView) {
		GameBoard board = gameView.getCurrentState();
		PlayerID me = gameView.getPlayerID();
		AIGameState gameState = new AIGameState(board.getWidth(), board.getHeight());
		gameState.setCurrentPlayer(me);
		gameState.setProbability(1f);
		int currentTurn = gameView.getCurrentTurn();
		// analyse
		int opponentUnitCount = 0;
		int opponentMovedUnitCount = 0;
		int opponentRevealedUnitCount = 0;
		int opponentRevealedAndMovedUnitCount = 0;
		int opponentUnrevealedAndUnmovedUnitCount = 0;
		int[] revealedCountByUnit = new int[12];
		for (int cx = 0; cx < board.getWidth(); cx++) {
			for (int cy = 0; cy < board.getHeight(); cy++) {
				Unit unit = board.getUnit(cx, cy);
				if (unit.getOwner() != NEMO && unit.getOwner() != me) {
					opponentUnitCount++;
					if (unit.wasMoved(currentTurn) && unit.wasRevealed(currentTurn)) {
						revealedCountByUnit[unit.getType().ordinal() - 3]++;
						gameState.getOpponent().unitCount++;
						opponentMovedUnitCount++;
						opponentRevealedUnitCount++;
						opponentRevealedAndMovedUnitCount++;

					} else if (unit.wasMoved(currentTurn)) {
						opponentMovedUnitCount++;
					} else if (unit.wasRevealed(currentTurn)) {
						revealedCountByUnit[unit.getType().ordinal() - 3]++;
						opponentRevealedUnitCount++;
					} else {
						opponentUnrevealedAndUnmovedUnitCount++;
					}
				}
			}
		}
		int opponentUnmovedUnitCount = opponentUnitCount-opponentMovedUnitCount;
		int opponentUnrevealedUnitCount = opponentUnitCount-opponentRevealedUnitCount;
		gameState.getOpponent()
				 .setRevealed(revealedCountByUnit)
				 .setUnitCount(opponentUnitCount)
		.setUnrevealedUnitCount(opponentUnrevealedUnitCount)
		.setUnrevealedAndUnmovedUnitCount(opponentUnrevealedAndUnmovedUnitCount);
		for (int i = 3; i < UnitType.values().length; i++) {
			UnitType unitType = UnitType.values()[i];
			gameState.setOwnDefeated(unitType, gameView.getNumberOfOwnDefeatedUnits(unitType));
			gameState.setOpponentDefeated(unitType, gameView.getNumberOfOpponentDefeatedUnits(unitType));
		}
		for (int cx = 0; cx < board.getWidth(); cx++) {
			for (int cy = 0; cy < board.getHeight(); cy++) {
				Unit unit = board.getUnit(cx, cy);
				AIUnit aiUnit = null;
				if (unit.isAir()) {
					aiUnit = new AIUnit()
							.setOwner(NEMO)
							.setUnitReference(unit)
							.setRevealed(true);
				} else if (unit.getOwner() == me) {
					aiUnit = new AIUnit()
							.setOwner(me)
							.setUnitReference(unit)
							.setProbabilityFor(unit.getType(), 1f)
							.setMoved(unit.wasMoved(currentTurn))
							.setRevealed(unit.wasRevealed(currentTurn));
				} else if (unit.getOwner() != NEMO) {
					aiUnit = new AIUnit()
							.setOwner(me)
							.setUnitReference(unit);
					if (unit.wasRevealed(currentTurn)) {
						aiUnit.setProbabilityFor(unit.getType(), 1f);
					} else {
						for (int i = 3; i < UnitType.values().length; i++) {
							UnitType unitType = UnitType.values()[i];
							int onBoard = unitType.getQuantity() - gameState.getOpponentDefeated(unitType);
							int unknown = onBoard - revealedCountByUnit[i-3];
							if (unitType == FLAG || unitType == BOMB) {
								if (!unit.wasMoved(currentTurn)) {
									aiUnit.setProbabilityFor(unitType, (float)unknown / (float)opponentUnrevealedAndUnmovedUnitCount);
								}
							} else {
								aiUnit.setProbabilityFor(unitType, (float)unknown / (float)opponentUnrevealedUnitCount);
							}
						}
						assert abs(aiUnit.getProbabilitySum()-1) < 0.01 : "Probabilities do not sum up to 1.0";
					}
					aiUnit.setMoved(unit.wasMoved(currentTurn))
						  .setRevealed(unit.wasRevealed(currentTurn));
				}
				gameState.setAIUnit(cx, cy, aiUnit);
			}
		}
		return gameState;
	}

	public static void reevaluateGameState(AIGameState gameState) {
		for (int cx = 0; cx < gameState.getWidth(); cx++) {
			for (int cy = 0; cy < gameState.getHeight(); cy++) {
				AIUnit aiUnit = gameState.getAIUnit(cx, cy);
				if (aiUnit.getOwner().getOpponent() != null && aiUnit.getOwner().getOpponent() == gameState.getCurrentPlayer()) {
					if (aiUnit.isMoved()) {
						float changedProb = aiUnit.getProbabilityFor(BOMB) + aiUnit.getProbabilityFor(FLAG);
						if (changedProb > 0) {
							float deltaProb = changedProb / aiUnit.getPossibilities();
							for (int i = 3; i < UnitType.values().length; i++) {
								UnitType unitType = UnitType.values()[i];
								if (unitType == FLAG || unitType == BOMB) {
									aiUnit.setProbabilityFor(unitType, 0);
								} else {
									aiUnit.setProbabilityFor(unitType, aiUnit.getProbabilityFor(unitType) + deltaProb);
								}
							}
						}
					}
					for (int i = 3; i < UnitType.values().length; i++) {
						UnitType unitType = UnitType.values()[i];
						if (aiUnit.getProbabilityFor(unitType) > 0) {
							int onBoard = unitType.getQuantity() - gameState.getOpponentDefeated(unitType);
							int unknown = onBoard - gameState.getOpponentRevealed(unitType);
							if (unitType == FLAG || unitType == BOMB) {
								aiUnit.setProbabilityFor(unitType, (float)unknown / (float)gameState.getOpponentUnrevealedAndUnmovedUnitCount());
							} else {
								aiUnit.setProbabilityFor(unitType, (float)unknown / (float)gameState.getOpponentUnrevealedUnitCount());
							}
						}
					}
				}
			}
		}
	}

	public static List<AIGameState> createOutcomesOfMove(AIGameState gameState, Move move) {
		List<AIGameState> outcomes = new ArrayList<AIGameState>();
		int fromX = move.getFromX();
		int fromY = move.getFromY();
		int toX = move.getToX();
		int toY = move.getToY();
		AIUnit movingAIUnit = gameState.getAIUnit(fromX, fromY);
		AIUnit destination = gameState.getAIUnit(toX, toY);
		boolean opponentsMove = movingAIUnit.getOwner() != gameState.getCurrentPlayer();
		List<ProbabilityEncounter> encounters = computePossibleEncounters(movingAIUnit, destination);
		// moving to air
		if (encounters.isEmpty()) {
			AIGameState outcome = new AIGameState(gameState);
			outcome.setAIUnit(fromX, fromY, destination);
			outcome.setAIUnit(toX, toY, movingAIUnit);
			movingAIUnit.setMoved(true);
			if (opponentsMove) {
				reevaluateGameState(outcome);
			}
			outcomes.add(outcome);
		} else {
			AIUnit air = new AIUnit()
					.setOwner(NEMO)
					.setUnitReference(Unit.AIR)
					.setRevealed(true);
			for (ProbabilityEncounter encounter : encounters) {
				AIGameState outcome = new AIGameState(gameState);
				if (encounter.getResult() == VICTORIOUS_ATTACK) {
					if (!opponentsMove) { // MY TURN
						outcome.setOpponentUnitCount(outcome.getOpponentUnitCount()-1);
						if (!destination.isRevealed() && !destination.isMoved()) {
							outcome.setOpponentUnrevealedAndUnmovedUnitCount(outcome.getOpponentUnrevealedAndUnmovedUnitCount()-1);
						}
						if (!destination.isRevealed()) {
							outcome.setOpponentUnrevealedUnitCount(outcome.getOpponentUnrevealedUnitCount() - 1);
						} else {
							outcome.setOpponentRevealed(encounter.defender, outcome.getOpponentRevealed(encounter.defender) - 1);
						}
						outcome.setOpponentDefeated(encounter.defender, outcome.getOpponentDefeated(encounter.defender) - 1);
					} else { // OPPONENT TURN
						outcome.setOwnDefeated(encounter.defender, outcome.getOwnDefeated(encounter.defender) - 1);
						movingAIUnit.clearProbabilities();
						movingAIUnit.setProbabilityFor(encounter.attacker, 1f);
						if (!movingAIUnit.isRevealed()) {
							outcome.setOpponentRevealed(encounter.attacker, outcome.getOpponentRevealed(encounter.attacker)+1);
						}
					}
					movingAIUnit.setRevealed(true);
					movingAIUnit.setMoved(true);
					outcome.setAIUnit(toX, toY, movingAIUnit);
					outcome.setAIUnit(fromX, fromY, air);
				} else if (encounter.getResult() == VICTORIOUS_DEFENSE) {
					if (!opponentsMove) { // MY TURN
						outcome.setOwnDefeated(encounter.attacker, outcome.getOwnDefeated(encounter.attacker) - 1);
						if (destination.isMoved() && !destination.isRevealed()) {
							outcome.setOpponentUnrevealedAndUnmovedUnitCount(outcome.getOpponentUnrevealedAndUnmovedUnitCount() + 1);
						}
						if (!destination.isRevealed()) {
							outcome.setOpponentUnrevealedUnitCount(outcome.getOpponentUnrevealedUnitCount() + 1);
						}
						destination.clearProbabilities();
						destination.setProbabilityFor(encounter.defender, 1f);
					} else { // OPPONENT TURN
						outcome.setOpponentUnitCount(outcome.getOpponentUnitCount() - 1);
						if (!movingAIUnit.isRevealed() && !movingAIUnit.isMoved()) {
							outcome.setOpponentUnrevealedAndUnmovedUnitCount(outcome.getOpponentUnrevealedAndUnmovedUnitCount() - 1);
						}
						if (!movingAIUnit.isRevealed()) {
							outcome.setOpponentUnrevealedUnitCount(outcome.getOpponentUnrevealedUnitCount() - 1);
						} else {
							outcome.setOpponentRevealed(encounter.attacker, outcome.getOpponentRevealed(encounter.attacker) + 1);
						}
						outcome.setOpponentDefeated(encounter.attacker, outcome.getOpponentDefeated(encounter.attacker) - 1);
					}
					destination.setRevealed(true);
					outcome.setAIUnit(fromX, fromY, air);
				} else if (encounter.getResult() == MUTUAL_DEFEAT) {
					if (!opponentsMove) { // MY TURN
						outcome.setOwnDefeated(encounter.attacker, outcome.getOwnDefeated(encounter.attacker) - 1);
						outcome.setOpponentDefeated(encounter.defender, outcome.getOpponentDefeated(encounter.defender) - 1);
						if (!destination.isMoved() && !destination.isRevealed()) {
							outcome.setOpponentUnrevealedAndUnmovedUnitCount(outcome.getOpponentUnrevealedAndUnmovedUnitCount() + 1);
						}
						if (!destination.isRevealed()) {
							outcome.setOpponentUnrevealedUnitCount(outcome.getOpponentUnrevealedUnitCount() - 1);
						} else {
							outcome.setOpponentRevealed(encounter.defender, outcome.getOpponentRevealed(encounter.defender) - 1);
						}
						outcome.setOpponentUnitCount(outcome.getOpponentUnitCount() - 1);
					} else { // OPPONENT TURN
						outcome.setOwnDefeated(encounter.defender, outcome.getOwnDefeated(encounter.defender) - 1);
						outcome.setOpponentDefeated(encounter.attacker, outcome.getOpponentDefeated(encounter.attacker) - 1);
						if (!movingAIUnit.isMoved() && !movingAIUnit.isRevealed()) {
							outcome.setOpponentUnrevealedAndUnmovedUnitCount(outcome.getOpponentUnrevealedAndUnmovedUnitCount() + 1);
						}
						if (!movingAIUnit.isRevealed()) {
							outcome.setOpponentUnrevealedUnitCount(outcome.getOpponentUnrevealedUnitCount() - 1);
						} else {
							outcome.setOpponentRevealed(encounter.attacker, outcome.getOpponentRevealed(encounter.attacker) - 1);
						}
						outcome.setOpponentUnitCount(outcome.getOpponentUnitCount() - 1);
					}
					outcome.setAIUnit(fromX, fromY, air);
					outcome.setAIUnit(toX, toY, air);
				}
				outcome.setProbability(outcome.getProbability()*encounter.probability);
				reevaluateGameState(outcome);
				outcomes.add(outcome);
			}
		}
		return outcomes;
	}

	public static AIGameState createOpponentView(AIGameState gameState) {
		AIGameState opponentState = new AIGameState(gameState);

		return opponentState;
	}

	/**
	 * Computes all possible encounters between two units based on probabilities.
	 * @param attacker The attacking unit.
	 * @param defender The defending unit.
	 * @return All possible encounters. Return an empty list if the defender is air.
	 */
	public static List<ProbabilityEncounter> computePossibleEncounters(AIUnit attacker, AIUnit defender) {
		List<ProbabilityEncounter> encounters = new ArrayList<ProbabilityEncounter>();
		for (int a = 3; a < UnitType.values().length; a++) {
			UnitType attackerType = UnitType.values()[a];
			float attackerProbability = attacker.getProbabilityFor(attackerType);
			if (attackerProbability == 0) {
				break;
			}
			for (int d = 3; d < UnitType.values().length; d++) {
				UnitType defenderType = UnitType.values()[a];
				float defenderProbability = defender.getProbabilityFor(defenderType);
				if (defenderProbability == 0) {
					break;
				}
				Encounter.CombatResult result = Encounter.resolveFight(attackerType, defenderType);
				ProbabilityEncounter encounter = new ProbabilityEncounter(attackerType, defenderType, result, attackerProbability * defenderProbability);
				encounters.add(encounter);
			}
		}
		return encounters;
	}

	@Getter
	@AllArgsConstructor
	private static class ProbabilityEncounter {
		private UnitType attacker;
		private UnitType defender;
		private Encounter.CombatResult result;
		private float probability;
	}

}
