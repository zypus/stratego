package com.theBombSquad.stratego.player.ai;

import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.gameMechanics.board.Encounter;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.Player;
import com.theBombSquad.stratego.player.ai.opponentModelling.GameStateConverter;
import com.theBombSquad.stratego.player.ai.opponentModelling.ProbabilityBoard;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static com.theBombSquad.stratego.StrategoConstants.*;
import static com.theBombSquad.stratego.StrategoConstants.PlayerID.*;
import static com.theBombSquad.stratego.gameMechanics.Game.*;
import static com.theBombSquad.stratego.gameMechanics.board.Unit.*;
import static com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType.*;

/**
 * Abstract AI class which gives access to several utility stuff.
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @author Flo
 */
public abstract class AI extends Player {

	private static final boolean MERGE = false;
	public static final int POSITIVE_MARSHAL_FLAG_MODIFIER = 2;
	public static final float NEGATIVE_MARSHAL_FLAG_MODIFIER = 0.5f;
	private static AIUnit AIR_AI_UNIT = new AIUnit()
			.setOwner(NEMO)
			.setUnitReference(Unit.AIR)
			.setRevealed(true);

	private static AIGameState[] setupReferences = new AIGameState[] { null, null };
	private static AIGameState[] currentState = new AIGameState[]{null, null};

	public static Game game;
	private static boolean DEBUG_STATES = false;

	public AI(GameView gameView) {
		super(gameView);
	}

	public static List<Move> createAllLegalMoves(GameView gameView, GameBoard board) {
		List<Move> list = new ArrayList<Move>();
		for (int cy = 0; cy < board.getHeight(); cy++) {
			for (int cx = 0; cx < board.getWidth(); cx++) {
				list.addAll(createAllLegalMovesForUnit(gameView, board, cx, cy));
			}
		}
		return list;
	}

	public static List<Move> createAllLegalMovesForUnit(GameView gameView, GameBoard board, int cx, int cy) {
		List<Move> list = new ArrayList<Move>();
		Unit unit = board.getUnit(cx, cy);
		if (unit.getOwner() == gameView.getPlayerID()) {
			if (!unit.getType()
					 .equals(BOMB) && !unit.getType()
										   .equals(FLAG)) {
				if (unit.getType()
						.equals(SCOUT)) {
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
		boolean up = true;
		boolean left = true;
		boolean right = true;
		boolean down = true;
		int counter = 1;
		while (up) {
			Move move = new Move(cx, cy, cx + counter, cy);
			if (gameView.validateMove(move)) {
				list.add(new Move(cx, cy, cx + counter, cy));
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


	/** ------------------------------------------------------------------------------------------------------------------------------
	 * AIGameState usage example:
	 *
	 * // Creating a new AIGameState:
	 * Gameview gameview;
	 * AIGameState state = AI.createAIGameState(gameview);
	 *
	 * // Copying a state is done using the constructor
	 * AIGameState copy = new AIGameState(state);
	 *
	 * // To get all possible move use the state AI method, it is not possible to get possible moves from the state itself
	 * List<Move> moves = AI.createAllLegalMoves(gameview);
	 *
	 * // Getting the outcome of a move
	 * Move move;
	 * AI.createOutcomeOfMove(state, move)
	 *
	 * // Advance the gameState by an actual taken move.
	 * Move performedMove;
	 * AIGameState advancedState = AI.advanceGameState(state, performedMove);
	 *
	 *
	 * IMPORTANT
	 *
	 * // Get the current AIGameState for the specified player									IMPORTANT
	 * PlayerID playerID;																		<-------- Use this instead of gameview.getCurrentState()
	 * AIGameState currentState = AI.getCurrentAIGameStateFor(playerID);							<-------- to work with the AIGameState instead
	 *
	 *
	 *
	 *
	 * // The AIGameState is composed out of AIUnits
	 * int x, y;
	 * AIUnit aiUnit = state.getAIUnit(x, y);			(After an encounter the state might contain a position (x,y) where one AIUnit of each player is present, in order to get them separately call state.getAIUnitFor(x,y, PlayerID))
	 *
	 * // Each AIUnit contains the probabilities for a certain unitType of the owner of that AIUnit
	 * float probability = aiUnit.getProbabilityFor( SCOUT );
	 *
	 ---------------------------------------------------------------------------------------------------------------------------------*/


	/**
	 * Creates a fresh AIGameState from the given gameView, assumes an equally probability distribution for each unit type. IMPORTANT: Does also represent the units of the gameview owner probability wise, so in other words the units are represented in the way the opponent would think about the units.
	 * @param gameView The gameview from which the current game state is taken.
	 * @return The new AIGameState which describes the game state solely with probabilities.
	 */
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
		float[] ownRevealedCountByUnit = new float[12];
		AIGameState.PlayerInformation opponent = gameState.getOpponent();
		int opponentRevealedUnitCount = 0;
		int opponentUnrevealedAndUnmovedUnitCount = 0;
		float[] opponentRevealedCountByUnit = new float[12];
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
							float onBoard = unitType.getQuantity() - player.getDefeatedFor(unitType);
							float unknown = onBoard - player.getRevealedFor(unitType);
							if (unitType == FLAG || unitType == BOMB) {
								if (!unit.wasMoved(currentTurn)) {
									aiUnit.setProbabilityFor(unitType, unknown / player.getUnrevealedAndUnmovedUnitCount());
								}
							} else if (unit.wasMoved(currentTurn) || unit.wasRevealed(currentTurn) ||  player.getUnrevealedAndUnmovedUnitCount() > (FLAG.getQuantity() - player.getRevealedFor(FLAG) - player.getDefeatedFor(FLAG) + BOMB.getQuantity() - player.getRevealedFor(BOMB) - player.getDefeatedFor(BOMB)) ) {
								aiUnit.setProbabilityFor(unitType, unknown / player.getUnrevealedUnitCount());
							}
						}
						// normalize
//						float sum = aiUnit.getProbabilitySum();
//						for (int i = 3; i < UnitType.values().length; i++) {
//							UnitType unitType = UnitType.values()[i];
//							aiUnit.setProbabilityFor(unitType, aiUnit.getProbabilityFor(unitType)/sum);
//						}
//						assert abs(aiUnit.getProbabilitySum()-1) < 0.01 : "Probabilities do not sum up to 1.0, but to " + aiUnit.getProbabilitySum();
					}
					aiUnit.setMoved(unit.wasMoved(currentTurn))
						  .setRevealed(unit.wasRevealed(currentTurn));
				}
				gameState.setAIUnit(cx, cy, aiUnit);
			}
		}
		normalize(gameState);
//		AIGameStateDebugger.debug(gameState);
		return gameState;
	}

	static final float THRESHOLD = 0.1f;

	public static void normalize(AIGameState gameState) {
		float error = 1;
		int counter = 0;
		boolean failed = false;
		while (error > THRESHOLD) {
			normalizeUnits(gameState);
			if (failed) {
				AIGameStateDebugger.debug(new AIGameState(gameState));
			}
			error = 0;
			for (int i = 3; i < UnitType.values().length; i++) {
				UnitType unitType = UnitType.values()[i];
				float[] sum = gameState.getTotalProbabilityFor(unitType);
				for (int cx = 0; cx < gameState.getWidth(); cx++) {
					for (int cy = 0; cy < gameState.getHeight(); cy++) {
						for (PlayerID playerID : new PlayerID[] { PLAYER_1, PLAYER_2 }) {
							AIUnit aiUnit = gameState.getAIUnitFor(cx, cy, playerID);
							if (aiUnit.getOwner() != null && aiUnit.getOwner() != NEMO && !aiUnit.isRevealed()) {
								AIGameState.PlayerInformation information = gameState.getPlayerInformation(playerID);
								float revealedFor = information.getRevealedFor(unitType);
								float stillOnBoard = (unitType.getQuantity() - information.getDefeatedFor(unitType) - revealedFor);
								float s = sum[(playerID == PLAYER_1)
												? 0
												: 1] - revealedFor;
								float currentProb = aiUnit.getProbabilityFor(unitType);
								float prob = 0;
								if ( s != 0 ) {
									prob = currentProb / s * stillOnBoard;
								}
								error += Math.abs(prob - currentProb);
								aiUnit.setProbabilityFor(unitType, prob);
							}
						}
					}
				}
			}
			counter++;
			if (counter > 1000) {
				System.out.println("Stuck in normalization, "+error+" "+gameState);
				AIGameStateDebugger.debug(new AIGameState(gameState));
//				break;
				if (failed) {
					while (true) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} else {
					failed = true;
				}
			}
		}
		normalizeUnits(gameState);
	}

	private static void normalizeUnits(AIGameState gameState) {
		int[] unmovedAndUnrevealed = new int[2];
		for (int cx = 0; cx < gameState.getWidth(); cx++) {
			for (int cy = 0; cy < gameState.getHeight(); cy++) {
				for (PlayerID playerID : new PlayerID[] { PLAYER_1, PLAYER_2 }) {
					AIUnit aiUnit = gameState.getAIUnitFor(cx, cy, playerID);
					if (aiUnit.getOwner() != null && aiUnit.getOwner() != NEMO) {
						float sum = aiUnit.getProbabilitySum();
						for (int i = 3; i < UnitType.values().length; i++) {
							UnitType unitType = UnitType.values()[i];
							if (aiUnit.getProbabilityFor(unitType) < 0.0001f || aiUnit.getProbabilityFor(unitType) == Float.NaN) {
								aiUnit.setProbabilityFor(unitType, 0);
							} else {
								aiUnit.setProbabilityFor(unitType, aiUnit.getProbabilityFor(unitType) / sum);
							}
						}
						if (aiUnit.isUntouched()) {
							unmovedAndUnrevealed[playerID.ordinal()]++;
						}
					}
				}
			}
		}
		gameState.getPlayerInformation(PLAYER_1).setUnrevealedAndUnmovedUnitCount(unmovedAndUnrevealed[0]);
		gameState.getPlayerInformation(PLAYER_2).setUnrevealedAndUnmovedUnitCount(unmovedAndUnrevealed[1]);
	}

	/**
	 * Given an AIGameState and a move, a new AIGameState will be created which represents the outcome of the given move. If the move results in an encounter the probability distribution will not be normalized, but will still represent the likelihood that a given unit type of a certain player will occupy a certain game field after performing the move.
	 * @param gameState The AIGameState.
	 * @param move The Move.
	 * @return Probability based game state after the move.
	 */
	public static AIGameState createOutcomeOfMove(AIGameState gameState, Move move) {
		// Sharon update
		ProbabilityBoard pb;
		if (MERGE) {
			pb = GameStateConverter.convertToProbabilityBoard(gameState);
			pb.moveMade(move, (gameState.getCurrentPlayer() == PLAYER_1)
							  ? game.getPlayer1()
									.getGameView()
							  : game.getPlayer2()
									.getGameView());
		}
		// Fabian update
		AIGameState outcome;
		AIGameState movement = new AIGameState(gameState);
		int fromX = move.getFromX();
		int fromY = move.getFromY();
		int toX = move.getToX();
		int toY = move.getToY();
		AIUnit movingAIUnit = new AIUnit(movement.getAIUnit(fromX, fromY));
		AIUnit destination = new AIUnit(movement.getAIUnit(toX, toY));

		// merge
		if (MERGE) {
			movement = GameStateConverter.convertToAIGameState(pb, movement);
			movement.setAIUnit(fromX, fromY, movingAIUnit);
			movement.setAIUnit(toX, toY, destination);
		}

		movingAIUnit.setProbabilityFor(FLAG, 0);
		movingAIUnit.setProbabilityFor(BOMB, 0);
		AIGameState.PlayerInformation movementPlayerInformation = movement.getPlayerInformation(movingAIUnit.getOwner());
		movingAIUnit.normalize();
		boolean movingRevealed = movingAIUnit.isRevealed();
		boolean scout = false;
		if (move.getDistance() > 1) {
			scout = true;
			movingAIUnit.clearProbabilities();
			movingAIUnit.setProbabilityFor(SCOUT, 1f);
		}

		boolean opponentsMove = movingAIUnit.getOwner() != movement.getCurrentPlayer();
		if (move.hasEncounter()) {
			Encounter encounter = move.getEncounter();
			movingAIUnit.clearProbabilities();
			movingAIUnit.setProbabilityFor(encounter.getAttackingUnit().getType(), 1f);
			destination.clearProbabilities();
			destination.setProbabilityFor(encounter.getDefendingUnit()
													.getType(), 1f);
		}
		List<ProbabilityEncounter> encounters = computePossibleEncounters(movingAIUnit, destination);
		// moving to air
		if (encounters.isEmpty()) {
			movement.setAIUnit(fromX, fromY, destination);
			movement.setAIUnit(toX, toY, movingAIUnit);
			if (!movingAIUnit.isMoved()) {
				movementPlayerInformation
					   .setUnrevealedAndUnmovedUnitCount(movementPlayerInformation.getUnrevealedAndUnmovedUnitCount() - 1);
			}
			if (scout && !movingRevealed) {
				movementPlayerInformation.addToRevealedFor(SCOUT, 1);
				movementPlayerInformation.setUnrevealedUnitCount(movementPlayerInformation.getUnrevealedUnitCount() - 1);
			}
			movingAIUnit.setMoved(true);
			if (scout && !movingRevealed) {
				movingAIUnit.setRevealed(true);
				updateReveal(movement, movingAIUnit);
			}
			movement.setContext(move);
//			reevaluateGameState(movement);
			normalize(movement);
			outcome = movement;
		} else {
			List<AIGameState> encounterOutcomes = new ArrayList<AIGameState>();
			for (ProbabilityEncounter encounter : encounters) {
				AIGameState encounterOutcome = new AIGameState(movement);
				AIUnit attackingAIUnit = encounterOutcome.getAIUnit(fromX, fromY);
				AIUnit defendingAIUnit = encounterOutcome.getAIUnit(toX, toY);
				AIGameState.PlayerInformation attackingPlayer = encounterOutcome.getPlayerInformation(movingAIUnit.getOwner());
				AIGameState.PlayerInformation defendingPlayer = encounterOutcome.getPlayerInformation(destination.getOwner());
				Encounter.CombatResult result = encounter.getResult();
				UnitType attacker = encounter.attacker;
				UnitType defender = encounter.defender;
				resolveEncounter(fromX, fromY, toX, toY, encounterOutcome, attackingAIUnit, defendingAIUnit, attackingPlayer, defendingPlayer, result, attacker, defender);
				encounterOutcome.setProbability(encounterOutcome.getProbability() * encounter.probability);
				encounterOutcome.setContext(encounter);
				normalize(encounterOutcome);
//				reevaluateGameState(encounterOutcome);
//				AIGameStateDebugger.debug(encounterOutcome);
				encounterOutcomes.add(encounterOutcome);
			}
			outcome = compressedState(encounterOutcomes);
		}
		flagBombLocalisation(outcome, movingAIUnit);
//		AIGameStateDebugger.debug(outcome);
		return outcome;
	}

	private static void flagBombLocalisation(AIGameState outcome, AIUnit movingAIUnit) {
		AIGameState.PlayerInformation outcomeInformation = outcome.getPlayerInformation(movingAIUnit.getOwner());
		checkAndUpdateBombsAndFlagsFor(outcome, movingAIUnit.getOwner(), outcomeInformation);
		AIGameState.PlayerInformation outcomeOpponentInformation = outcome.getPlayerInformation(movingAIUnit.getOwner().getOpponent());
		checkAndUpdateBombsAndFlagsFor(outcome, movingAIUnit.getOwner().getOpponent(), outcomeOpponentInformation);
	}

	private static void checkAndUpdateBombsAndFlagsFor(AIGameState outcome, PlayerID owner, AIGameState.PlayerInformation outcomeInformation) {
		if (!outcomeInformation.isBombsAndFlagsPositionsKnown() &&outcomeInformation.getUnrevealedAndUnmovedUnitCount() == FLAG.getQuantity() - outcomeInformation.getRevealedFor(FLAG) - outcomeInformation.getDefeatedFor(FLAG) + BOMB.getQuantity() - outcomeInformation.getRevealedFor(BOMB) - outcomeInformation.getDefeatedFor(BOMB)) {
			for (int cx = 0; cx < outcome.getWidth(); cx++) {
				for (int cy = 0; cy < outcome.getHeight(); cy++) {
					AIUnit aiUnit = outcome.getAIUnit(cx, cy);
					if (aiUnit.getOwner() == owner) {
						if (!aiUnit.isMoved()) {
							for (int i = 5; i < UnitType.values().length; i++) {
								UnitType unitType = UnitType.values()[i];
								aiUnit.setProbabilityFor(unitType, 0);
							}
						}
					}
				}
			}
			outcomeInformation.setBombsAndFlagsPositionsKnown(true);
		}
	}

	private static void resolveEncounter(int fromX, int fromY, int toX, int toY, AIGameState encounterOutcome, AIUnit attackingAIUnit, AIUnit defendingAIUnit, AIGameState.PlayerInformation attackingPlayer, AIGameState.PlayerInformation defendingPlayer, Encounter.CombatResult result, UnitType attacker, UnitType defender) {
		boolean attackingRevealed = attackingAIUnit.isRevealed();
		boolean defendingRevealed = defendingAIUnit.isRevealed();
		switch (result) {
			case VICTORIOUS_ATTACK:
				//					    ^              _____
				//					    |             /     \
				//					    |       \/   | () () |
				//					    |       /\    \  ^  /
				//					   o+o             |||||
				//					    0              |||||
				//
				saveKill(defendingAIUnit, defender, defendingPlayer);
				correctRevealStatus(attackingAIUnit, attackingPlayer);
				attackingAIUnit.clearProbabilities();
				attackingAIUnit.setProbabilityFor(attacker, 1f);
				if (!attackingAIUnit.isRevealed()) {
					attackingPlayer.addToRevealedFor(attacker, +1);
				}
				attackingAIUnit.setRevealed(true);
				attackingAIUnit.setMoved(true);
				defendingAIUnit.clearProbabilities();
				defendingAIUnit.setProbabilityFor(defender, 1f);
				encounterOutcome.replaceAIUnit(toX, toY, attackingAIUnit);
				encounterOutcome.setAIUnit(fromX, fromY, AIR_AI_UNIT);
				break;
			case VICTORIOUS_DEFENSE:
				//					  _____            _____
				//					 /     \          /  |  \ <-- shield, not coffin
				//					| () () |	\/   / --|-- \
				//					 \  ^  /	/\   \   |   /
				//					  |||||           \  |  /
				//					  |||||            \___/
				//
				saveKill(attackingAIUnit, attacker, attackingPlayer);
				correctRevealStatus(defendingAIUnit, defendingPlayer);
				defendingAIUnit.clearProbabilities();
				defendingAIUnit.setProbabilityFor(defender, 1f);
				if (!defendingAIUnit.isRevealed()) {
					defendingPlayer.addToRevealedFor(defender, +1);
				}
				defendingAIUnit.setRevealed(true);
				attackingAIUnit.clearProbabilities();
				attackingAIUnit.setProbabilityFor(attacker, 1f);
				encounterOutcome.setAIUnit(fromX, fromY, AIR_AI_UNIT);
				break;
			case MUTUAL_DEFEAT:
				//					  _____            _____
				//					 /     \          /     \
				//					| () () |	\/   | () () |
				//					 \  ^  /	/\	  \  ^  /
				//					  |||||            |||||
				//					  |||||            |||||
				//
				saveKill(attackingAIUnit, attacker, attackingPlayer);
				saveKill(defendingAIUnit, defender, defendingPlayer);
				attackingAIUnit.clearProbabilities();
				attackingAIUnit.setProbabilityFor(attacker, 1f);
				defendingAIUnit.clearProbabilities();
				defendingAIUnit.setProbabilityFor(defender, 1f);
				encounterOutcome.setAIUnit(fromX, fromY, AIR_AI_UNIT);
				encounterOutcome.setAIUnit(toX, toY, AIR_AI_UNIT);
				break;
		}
		if (!attackingRevealed) {
			updateReveal(encounterOutcome, attackingAIUnit);
		}
		if (!defendingRevealed) {
			updateReveal(encounterOutcome, defendingAIUnit);
		}
	}

	private static void updateReveal(AIGameState updatedState, AIUnit unit) {
		PlayerID owner = unit.getOwner();
		AIGameState setupReference = setupReferences[owner.ordinal()];
		// find the unit in the setup
//		System.out.println(unit);
//		AIGameStateDebugger.debug(setupReference);
		int[] xy = setupReference.getCorrespondingCoordinates(unit.getUnitReference());
		int sx = xy[0];
		int sy = xy[1];
		// update front
		adjacencyUpdate(unit, updatedState, owner, setupReference, sx, sy+((owner == updatedState.getCurrentPlayer())
																					 ? 1
																					 : -1), AdjacencyTables.Direction.UP);
		// update behind
		adjacencyUpdate(unit, updatedState, owner, setupReference, sx, sy + ((owner == updatedState.getCurrentPlayer())
																					? -1
																					: 1), AdjacencyTables.Direction.DOWN);
		// update left
		adjacencyUpdate(unit, updatedState, owner, setupReference, sx-1, sy, AdjacencyTables.Direction.SIDE);
		// update right
		adjacencyUpdate(unit, updatedState, owner, setupReference, sx+1, sy, AdjacencyTables.Direction.SIDE);

		if (unit.getConfirmedUnitType() == MARSHAL) {
			marshalUpdate(updatedState, unit, setupReference, sx);
		}
	}

	private static void marshalUpdate(AIGameState updatedState, AIUnit unit, AIGameState setupReference, int sx) {
		int h1 = (updatedState.getCurrentPlayer() == unit.getOwner()) ? 6 : 0;
		int h2 = (updatedState.getCurrentPlayer() == unit.getOwner()) ? 10 : 5;
		for (int cx = 0; cx < setupReference.getWidth(); cx++) {
			for (int cy = h1; cy < h2; cy++) {
				AIUnit refUnit = setupReference.getAIUnit(cx, cy);
				AIUnit currentUnit = updatedState.getCorresponding(refUnit.getUnitReference());
				if (currentUnit != null) {
					if (sx >= 5) {
						if (cx >= 5) {
							currentUnit.setProbabilityFor(FLAG, currentUnit.getProbabilityFor(FLAG) * POSITIVE_MARSHAL_FLAG_MODIFIER);
						} else {
							currentUnit.setProbabilityFor(FLAG, currentUnit.getProbabilityFor(FLAG) * NEGATIVE_MARSHAL_FLAG_MODIFIER);
						}
					} else {
						if (cx >= 5) {
							currentUnit.setProbabilityFor(FLAG, currentUnit.getProbabilityFor(FLAG) * NEGATIVE_MARSHAL_FLAG_MODIFIER);
						} else {
							currentUnit.setProbabilityFor(FLAG, currentUnit.getProbabilityFor(FLAG) * POSITIVE_MARSHAL_FLAG_MODIFIER);
						}
					}
				}
			}
		}
	}

	private static void adjacencyUpdate(AIUnit unit, AIGameState updatedState, PlayerID owner, AIGameState setupReference, int sx, int sy, AdjacencyTables.Direction direction) {
		AIUnit adjacentUnit = setupReference.getAIUnit(sx, sy);
		if (adjacentUnit != null) {
			AIUnit currentUnit = updatedState.getCorresponding(adjacentUnit.getUnitReference());
			if (currentUnit != null) {
				if (adjacentUnit.getOwner() != null && adjacentUnit.getOwner() != NEMO) {
					for (int i = 3; i < UnitType.values().length; i++) {
						UnitType unitType = UnitType.values()[i];
						if (unit.getConfirmedUnitType() == null) {
							System.out.println(unit);
						}
						currentUnit.setProbabilityFor(unitType, currentUnit.getProbabilityFor(unitType) * AdjacencyTables.factorFor(unit.getConfirmedUnitType(), unitType, direction));
					}
				}
			}
		}
	}

	public static void setSetupReferences(AIGameState reference, PlayerID playerID) {
		if (playerID != null && playerID != NEMO) {
			setupReferences[playerID.ordinal()] = reference;
			currentState[playerID.ordinal()] = reference;
		}
	}

	public static AIGameState getSetupReferences(PlayerID playerID) {
		if (playerID != null && playerID != NEMO) {
			return setupReferences[playerID.ordinal()];
		}
		return null;
	}

	public static AIGameState getCurrentAIGameStateFor(PlayerID playerID) {
		if (playerID != null && playerID != NEMO) {
			return currentState[playerID.ordinal()];
		}
		return null;
	}

	public static void updateCurrentAIGameStateWith(PlayerID playerID, Move move) {
		if (playerID != null && playerID != NEMO) {
			currentState[playerID.ordinal()] = advanceGameState(currentState[playerID.ordinal()], move);
		}
	}

	public static AIGameState compressedState(List<AIGameState> states) {
        AIGameState compressedState = new AIGameState(states.get(0));

        for (int cx = 0; cx < compressedState.getWidth(); cx++) {
            for (int cy = 0; cy < compressedState.getHeight(); cy++) {
				Unit originalRef = compressedState.getAIUnit(cx, cy)
												  .getUnitReference();
				for (PlayerID playerID : new PlayerID[]{PLAYER_1, PLAYER_2}) {
					AIUnit compressibleUnit = new AIUnit(compressedState.getAIUnitFor(cx, cy, playerID));
					boolean playerLoaded = false;
					Unit reference = null;
					for (int i = 3; i < UnitType.values().length; i++) {
						UnitType unitType = UnitType.values()[i];
						float prob = 0;
						boolean moved = false;
						boolean revealed = false;
						for (int s = 0; s < states.size(); s++) {
							AIGameState state = states.get(s);
							AIUnit aiUnit = state.getAIUnitFor(cx, cy, playerID);
							if (aiUnit.getOwner() == playerID) {
								playerLoaded = true;
								reference = aiUnit.getUnitReference();
								prob += state.getProbability() * aiUnit.getProbabilityFor(unitType);
								if (aiUnit.isMoved()) {
									moved = true;
								}
								if (aiUnit.isRevealed()) {
									revealed = true;
								}

							}
						}
						compressibleUnit.setOwner((playerLoaded)
												  ? playerID
												  : NEMO)
									   .setUnitReference((reference != null)
														 ? reference
														 : originalRef)
									   .setProbabilityFor(unitType, prob)
									   .setMoved(moved)
									   .setRevealed(revealed);
					}
					compressedState.setAIUnitFor(cx, cy, compressibleUnit, playerID);
				}
            }
        }
		for (PlayerID playerID : new PlayerID[] { PLAYER_1, PLAYER_2 }) {
			float[] defeated = new float[12];
			float[] revealed = new float[12];
			float unitCount = 0;
			float unrevealedAndUnmovedUnitCount = 0;
			float unrevealedUnitCount = 0;
			for (int s = 0; s < states.size(); s++) {
				AIGameState state = states.get(s);
				AIGameState.PlayerInformation information = state.getPlayerInformation(playerID);
				for (int i = 0; i < 12; i++) {
					defeated[i] = state.getProbability() * information.getDefeated()[i];
					revealed[i] = state.getProbability() * information.getRevealed()[i];
				}
				unitCount = state.getProbability() * information.getUnitCount();
				unrevealedAndUnmovedUnitCount = state.getProbability() * information.getUnrevealedAndUnmovedUnitCount();
				unrevealedUnitCount = state.getProbability() * information.getUnrevealedUnitCount();
			}
			compressedState.getPlayerInformation(playerID)
						   .setDefeated(defeated)
						   .setRevealed(revealed)
						   .setUnitCount(unitCount)
						   .setUnrevealedAndUnmovedUnitCount(unrevealedAndUnmovedUnitCount)
						   .setUnrevealedUnitCount(unrevealedUnitCount);
		}
		compressedState.setProbability(1f);
        compressedState.setCompressed(true);

        return compressedState;
    }

	public static AIGameState advanceGameState(AIGameState gameState, Move move) {
		AIGameState state = createOutcomeOfMove(gameState, move);
		if (gameState.getCurrentPlayer() == PLAYER_1 && DEBUG_STATES) {
			AIGameStateDebugger.debug(state);
		}
		return state;
	}

	public static AIGameState advanceGameState2(AIGameState gameState, Move move) {
		AIGameState advancedState =  new AIGameState(gameState);
		advancedState.setContext(move);
		int fromX = move.getFromX();
		int fromY = move.getFromY();
		int toX = move.getToX();
		int toY = move.getToY();
		AIUnit air = new AIUnit()
				.setOwner(NEMO)
				.setUnitReference(Unit.AIR)
				.setRevealed(true);
		AIUnit movingAIUnit = advancedState.getAIUnit(fromX, fromY);
		movingAIUnit.setProbabilityFor(FLAG, 0f);
		movingAIUnit.setProbabilityFor(BOMB, 0f);
		boolean movingRevealed = movingAIUnit.isRevealed();
		AIGameState.PlayerInformation movementPlayerInformation = advancedState.getPlayerInformation(movingAIUnit.getOwner());

//		System.out.println(movementPlayerInformation.getUnrevealedAndUnmovedUnitCount());

		if (move.hasEncounter()) {
			Encounter encounter = move.getEncounter();
			AIUnit attackingAIUnit = advancedState.getAIUnit(fromX, fromY);
			AIUnit defendingAIUnit = advancedState.getAIUnit(toX, toY);
			AIGameState.PlayerInformation attackingPlayer = advancedState.getPlayerInformation(attackingAIUnit.getOwner());
			AIGameState.PlayerInformation defendingPlayer = advancedState.getPlayerInformation(defendingAIUnit.getOwner());
			Encounter.CombatResult result = encounter.getResult();
			UnitType attacker = encounter.getAttackingUnit().getType();
			UnitType defender = encounter.getDefendingUnit().getType();
			resolveEncounter(fromX, fromY, toX, toY, advancedState, attackingAIUnit, defendingAIUnit, attackingPlayer, defendingPlayer, result, attacker, defender);
//			System.out.println("encounter");
		} else {
			boolean scout = false;
			if (move.getDistance() > 1) {
				scout = true;
				movingAIUnit.clearProbabilities();
				movingAIUnit.setProbabilityFor(SCOUT, 1f);
				movingAIUnit.setRevealed(true);
				if (!movingRevealed) {
					movementPlayerInformation.addToRevealedFor(SCOUT, 1);
					movementPlayerInformation.setUnrevealedUnitCount(movementPlayerInformation.getUnrevealedUnitCount() - 1);
				}
				if (!movingAIUnit.isMoved()) {
					movementPlayerInformation.setUnrevealedAndUnmovedUnitCount(movementPlayerInformation.getUnrevealedAndUnmovedUnitCount() - 1);
				}
			}
			if (!scout && !movingAIUnit.isMoved()) {
				movementPlayerInformation.setUnrevealedAndUnmovedUnitCount(movementPlayerInformation.getUnrevealedAndUnmovedUnitCount() - 1);
			}
			movingAIUnit.setMoved(true);
			if (scout && !movingRevealed) {
				updateReveal(advancedState, movingAIUnit);
			}
			advancedState.setAIUnit(toX, toY, movingAIUnit);
			advancedState.setAIUnit(fromX, fromY, air);
//			System.out.println("move");
		}
//		System.out.println(movementPlayerInformation.getUnrevealedAndUnmovedUnitCount());
		// check if all unmoved units needs to be BOMBs or the FLAG
		if (movementPlayerInformation.getUnrevealedAndUnmovedUnitCount() == FLAG.getQuantity() - movementPlayerInformation.getRevealedFor(FLAG) - movementPlayerInformation.getDefeatedFor(FLAG) + BOMB.getQuantity() - movementPlayerInformation.getRevealedFor(BOMB) - movementPlayerInformation.getDefeatedFor(BOMB) ) {
			System.out.println("Its happening for "+movingAIUnit.getOwner());
			for (int cx = 0; cx < advancedState.getWidth(); cx++) {
				for (int cy = 0; cy < advancedState.getHeight(); cy++) {
					AIUnit aiUnit = advancedState.getAIUnit(cx, cy);
					if (aiUnit.getOwner() == movingAIUnit.getOwner()) {
						if (!aiUnit.isMoved()) {
							for (int i = 5; i < UnitType.values().length; i++) {
								UnitType unitType = UnitType.values()[i];
								aiUnit.setProbabilityFor(unitType, 0);
							}
						}
					}
				}
			}
		}
		normalize(advancedState);
//		AIGameStateDebugger.debug(advancedState);
		return advancedState;
	}

	private static void reevaluateGameState(AIGameState gameState) {
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
							float onBoard = unitType.getQuantity() - player.getDefeatedFor(unitType);
							float unknown = onBoard - player.getRevealedFor(unitType);
							if (unitType == FLAG || unitType == BOMB) {
								aiUnit.setProbabilityFor(unitType, unknown / player.getUnrevealedAndUnmovedUnitCount());
							} else {
								aiUnit.setProbabilityFor(unitType, unknown / player.getUnrevealedUnitCount());
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
				continue;
			}
			for (int d = 3; d < UnitType.values().length; d++) {
				UnitType defenderType = UnitType.values()[d];
				float defenderProbability = defender.getProbabilityFor(defenderType);
				if (defenderProbability == 0) {
					continue;
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
	@ToString
	private static class ProbabilityEncounter {
		private UnitType attacker;
		private UnitType defender;
		private Encounter.CombatResult result;
		private float probability;
	}

}
