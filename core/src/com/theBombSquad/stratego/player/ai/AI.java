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

	public static List<Move> createAllLegalMoves(GameView gameView, GameBoard board){
		List<Move> list = new ArrayList<Move>();
		for(int cy=0; cy<board.getHeight(); cy++){
			for(int cx=0; cx<board.getWidth(); cx++){
				list.addAll(createAllLegalMovesForUnit(gameView, board, cx, cy));
			}
		}
		return list;
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
		AIGameState.PlayerInformation own = gameState.getOwn();
		int ownRevealedUnitCount = 0;
		int ownUnrevealedAndUnmovedUnitCount = 0;
		int[] ownRevealedCountByUnit = new int[12];
		AIGameState.PlayerInformation opponent = gameState.getOpponent();
		int opponentRevealedUnitCount = 0;
		int opponentUnrevealedAndUnmovedUnitCount = 0;
		int[] opponentRevealedCountByUnit = new int[12];
		for (int cx = 0; cx < board.getWidth(); cx++) {
			for (int cy = 0; cy < board.getHeight(); cy++) {
				Unit unit = board.getUnit(cx, cy);
				if (unit.getOwner() != NEMO && unit.getOwner() != me) {
					opponent.unitCount++;
					if (unit.wasMoved(currentTurn) && unit.wasRevealed(currentTurn)) {
						opponentRevealedCountByUnit[unit.getType().ordinal() - 3]++;
						opponentRevealedUnitCount++;
					} else if (unit.wasMoved(currentTurn)) {
					} else if (unit.wasRevealed(currentTurn)) {
						opponentRevealedCountByUnit[unit.getType().ordinal() - 3]++;
						opponentRevealedUnitCount++;
					} else {
						opponentUnrevealedAndUnmovedUnitCount++;
					}
				} else if (unit.getOwner() != NEMO && unit.getOwner() == me) {
					gameState.getOwn().unitCount++;
					if (unit.wasMoved(currentTurn) && unit.wasRevealed(currentTurn)) {
						ownRevealedCountByUnit[unit.getType().ordinal() - 3]++;
						ownRevealedUnitCount++;
					} else if (unit.wasMoved(currentTurn)) {
					} else if (unit.wasRevealed(currentTurn)) {
						ownRevealedCountByUnit[unit.getType().ordinal() - 3]++;
						ownRevealedUnitCount++;
					} else {
						ownUnrevealedAndUnmovedUnitCount++;
					}
				}
			}
		}
		own
				.setRevealed(ownRevealedCountByUnit)
				.setUnrevealedUnitCount(own.getUnitCount() - ownRevealedUnitCount)
				.setUnrevealedAndUnmovedUnitCount(ownUnrevealedAndUnmovedUnitCount);
		opponent
				.setRevealed(opponentRevealedCountByUnit)
				.setUnrevealedUnitCount(opponent.getUnitCount() - opponentRevealedUnitCount)
				.setUnrevealedAndUnmovedUnitCount(opponentUnrevealedAndUnmovedUnitCount);
		for (int i = 3; i < UnitType.values().length; i++) {
			UnitType unitType = UnitType.values()[i];
			own.setDefeatedFor(unitType, gameView.getNumberOfOwnDefeatedUnits(unitType));
			opponent.setDefeatedFor(unitType, gameView.getNumberOfOpponentDefeatedUnits(unitType));
		}
		for (int cx = 0; cx < board.getWidth(); cx++) {
			for (int cy = 0; cy < board.getHeight(); cy++) {
				Unit unit = board.getUnit(cx, cy);
				AIUnit aiUnit = null;
				if (unit.isAir() || unit.isLake()) {
					aiUnit = new AIUnit()
							.setOwner(NEMO)
							.setUnitReference(unit)
							.setRevealed(true);
				} else if (unit.getOwner() != NEMO) {
					aiUnit = new AIUnit()
							.setOwner(unit.getOwner())
							.setUnitReference(unit);
					if (unit.wasRevealed(currentTurn)) {
						aiUnit.setProbabilityFor(unit.getType(), 1f);
					} else {
						AIGameState.PlayerInformation player = gameState.getPlayerInformation(unit.getOwner());
						for (int i = 3; i < UnitType.values().length; i++) {
							UnitType unitType = UnitType.values()[i];
							int onBoard = unitType.getQuantity() - player.getDefeatedFor(unitType);
							int unknown = onBoard - player.getRevealedFor(unitType);
							if (unitType == FLAG || unitType == BOMB) {
								if (!unit.wasMoved(currentTurn)) {
									aiUnit.setProbabilityFor(unitType, (float)unknown / (float)player.getUnrevealedAndUnmovedUnitCount());
								}
							} else {
								aiUnit.setProbabilityFor(unitType, (float)unknown / (float)player.getUnrevealedUnitCount());
							}
						}
						// normalize
						float sum = aiUnit.getProbabilitySum();
						for (int i = 3; i < UnitType.values().length; i++) {
							UnitType unitType = UnitType.values()[i];
							aiUnit.setProbabilityFor(unitType, aiUnit.getProbabilityFor(unitType)/sum);
						}
						assert abs(aiUnit.getProbabilitySum()-1) < 0.01 : "Probabilities do not sum up to 1.0, but to " + aiUnit.getProbabilitySum();
					}
					aiUnit.setMoved(unit.wasMoved(currentTurn))
						  .setRevealed(unit.wasRevealed(currentTurn));
				}
				gameState.setAIUnit(cx, cy, aiUnit);
			}
		}
		return gameState;
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
				AIGameState.PlayerInformation attackingPlayer = outcome.getPlayerInformation(movingAIUnit.getOwner());
				AIGameState.PlayerInformation defendingPlayer = outcome.getPlayerInformation(destination.getOwner());
				if (encounter.getResult() == VICTORIOUS_ATTACK) {
					//					    ^              _____
					//					    |             /     \
					//					    |       \/   | () () |
					//					    |       /\    \  ^  /
					//					   o+o             |||||
					//					    0              |||||
					//
					saveKill(destination, encounter.defender, defendingPlayer);
					correctRevealStatus(movingAIUnit, attackingPlayer);
					movingAIUnit.clearProbabilities();
					movingAIUnit.setProbabilityFor(encounter.attacker, 1f);
					if (!movingAIUnit.isRevealed()) {
						attackingPlayer.addToRevealedFor(encounter.attacker, +1);
					}
					movingAIUnit.setRevealed(true);
					movingAIUnit.setMoved(true);
					outcome.setAIUnit(toX, toY, movingAIUnit);
					outcome.setAIUnit(fromX, fromY, air);
				} else if (encounter.getResult() == VICTORIOUS_DEFENSE) {
					//					  _____            _____
					//					 /     \          /  |  \ <-- shield, not coffin
					//					| () () |	\/   / --|-- \
					//					 \  ^  /	/\   \   |   /
					//					  |||||           \  |  /
					//					  |||||            \___/
					//
					saveKill(movingAIUnit, encounter.attacker, attackingPlayer);
					correctRevealStatus(destination, defendingPlayer);
					destination.clearProbabilities();
					destination.setProbabilityFor(encounter.defender, 1f);
					if (!destination.isRevealed()) {
						defendingPlayer.addToRevealedFor(encounter.defender, +1);
					}
					destination.setRevealed(true);
					outcome.setAIUnit(fromX, fromY, air);
				} else if (encounter.getResult() == MUTUAL_DEFEAT) {
					//					  _____            _____
					//					 /     \          /     \
					//					| () () |	\/   | () () |
					//					 \  ^  /	/\	  \  ^  /
					//					  |||||            |||||
					//					  |||||            |||||
					//
					saveKill(movingAIUnit, encounter.attacker, attackingPlayer);
					saveKill(destination, encounter.defender, defendingPlayer);
					outcome.setAIUnit(fromX, fromY, air);
					outcome.setAIUnit(toX, toY, air);
				}
				outcome.setProbability(outcome.getProbability() * encounter.probability);
				reevaluateGameState(outcome);
				outcomes.add(outcome);
			}
		}
		return outcomes;
	}

    public static AIGameState compressedState(List<AIGameState> states) {
        AIGameState compressedState = new AIGameState(states.get(0));

        for (int cx = 0; cx < compressedState.getWidth(); cx++) {
            for (int cy = 0; cy < compressedState.getHeight(); cy++) {
                for (int i = 3; i < UnitType.values().length; i++) {
                    UnitType unitType = UnitType.values()[i];
                    float prob = 0;
                    boolean moved = false;
                    boolean revealed = false;
                    for (int s = 0; s < states.size(); s++) {
                        AIGameState state = states.get(s);
                        AIUnit aiUnit = state.getAIUnit(cx, cy);
                        prob += state.getProbability() * aiUnit.getProbabilityFor(unitType);
                        if (aiUnit.isMoved()) {
                            moved = true;
                        }
                        if (aiUnit.isRevealed()) {
                            revealed = true;
                        }
                    }
                    compressedState.getAIUnit(cx, cy).setProbabilityFor(unitType, prob)
                    .setMoved(moved)
                    .setRevealed(revealed);
                }
            }
        }
        compressedState.setCompressed(true);

        return compressedState;
    }

	public static void reevaluateGameState(AIGameState gameState) {
		for (int cx = 0; cx < gameState.getWidth(); cx++) {
			for (int cy = 0; cy < gameState.getHeight(); cy++) {
				AIUnit aiUnit = gameState.getAIUnit(cx, cy);
				if (aiUnit.getOwner() != NEMO) {
					AIGameState.PlayerInformation player = gameState.getPlayerInformation(aiUnit.getOwner());
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
							int onBoard = unitType.getQuantity() - player.getDefeatedFor(unitType);
							int unknown = onBoard - player.getRevealedFor(unitType);
							if (unitType == FLAG || unitType == BOMB) {
								aiUnit.setProbabilityFor(unitType, (float) unknown / (float) player.getUnrevealedAndUnmovedUnitCount());
							} else {
								aiUnit.setProbabilityFor(unitType, (float) unknown / (float) player.getUnrevealedUnitCount());
							}
						}
					}
				}
			}
		}
	}

	private static void correctRevealStatus(AIUnit unit, AIGameState.PlayerInformation playerInfo) {
		if (unit.isUntouched()) {
			playerInfo.unrevealedAndUnmovedUnitCount--;
		}
		if (!unit.isRevealed()) {
			playerInfo.unrevealedUnitCount--;
		}
	}

	private static void saveKill(AIUnit unit, UnitType type, AIGameState.PlayerInformation playerInfo) {
		playerInfo.addToDefeatedFor(type, +1);
		if (unit.isUntouched()) {
			playerInfo.unrevealedAndUnmovedUnitCount--;
		}
		if (!unit.isRevealed()) {
			playerInfo.unrevealedUnitCount--;
		} else {
			playerInfo.addToRevealedFor(type, -1);
		}
		playerInfo.unitCount--;
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

	public static AIGameState createOwnView(AIGameState gameState) {
		AIGameState ownState = new AIGameState(gameState);
		for (int cx = 0; cx < ownState.getWidth(); cx++) {
			for (int cy = 0; cy < ownState.getHeight(); cy++) {
				AIUnit aiUnit = ownState.getAIUnit(cx, cy);
				if (aiUnit.getOwner() == ownState.getCurrentPlayer()) {
					aiUnit.clearProbabilities();
					aiUnit.setProbabilityFor(aiUnit.getUnitReference().getType(), 1f);
				}
			}
		}
		return ownState;
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
