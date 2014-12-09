package com.theBombSquad.stratego.player.ai.players.random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.theBombSquad.stratego.StrategoConstants;
import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.AI;
import com.theBombSquad.stratego.player.ai.AIGameState;
import com.theBombSquad.stratego.player.ai.AIUnit;

public class MctsAI extends AI{

	public MctsAI(GameView gameView) {
		super(gameView);
	}

	@Override
	protected Move move() {
		Move move = mcts();
		gameView.performMove(move);
		return move;
	}
	
	/** Performs actual MCTS */
	private Move mcts(){
		int generateProbableBoards = 1;
		AIGameState state = super.createAIGameState(super.gameView);
		List<Move> possibleMoves = super.createAllLegalMoves(gameView, gameView.getCurrentState());
		//Create Game Boards based upon all the possible moves
		GameBoard[] probableBoards = new GameBoard[generateProbableBoards];
		for(int c=0; c<probableBoards.length; c++){
			probableBoards[c] = setProbableOpponent();
		}
		for(int cy=0; cy<probableBoards[0].getHeight(); cy++){
			for(int cx=0; cx<probableBoards[0].getWidth(); cx++){
				System.out.print(probableBoards[0].getUnit(cx, cy).getType()+" ");
			}
			System.out.println();
		}
		return null;
	}
	
	/** 'Reveals' the actual board state based on known information and probabilistically based on AIGameState's information */
	private GameBoard setProbableOpponent(){
		PlayerID opponent = StrategoConstants.PlayerID.PLAYER_1;
		if(opponent==gameView.getPlayerID()){
			opponent = StrategoConstants.PlayerID.PLAYER_2;
		}
		Random rand = new Random();
		GameBoard probableBoard = this.gameView.getCurrentState().duplicate();
		AIGameState state = super.createAIGameState(super.gameView);
		int[] numberOfStillPlaceable = getNumberOfRevealedOpponentUnits(probableBoard);
		//Converts all unknown units into 'likely' units for the spot
		for(int cy=0; cy<probableBoard.getHeight(); cy++){
			for(int cx=0; cx<probableBoard.getWidth(); cx++){
				AIUnit aiUnit = state.getAIUnit(cx, cy);
				//Check IF unknown
				if(probableBoard.getUnit(cx, cy).isUnknown()){
					boolean done = false;
					float randomRoll = rand.nextFloat();
					float probsSum = aiUnit.getProbabilitySum();
					while(!done){
						float base = 0;
						float nextBase = base;
						for(int c=0; c<numberOfStillPlaceable.length; c++){
							nextBase = (aiUnit.getProbabilityFor(Unit.getUnitTypeOfRank(c))/probsSum)+base;
							if(numberOfStillPlaceable[c]>0){
								if(randomRoll>=base && randomRoll<=nextBase){
									numberOfStillPlaceable[c]--;
									probableBoard.setUnit(cx, cy, new Unit(Unit.getUnitTypeOfRank(c), opponent));
									done = true;
									break;
								}
							}
							base = nextBase;
						}
					}
				}
			}
		}
		return probableBoard;
	}
	
	/** Returns an integer array with each index representing the yet unplaced and not dead number of units for a Unit type of the opponent, sorted according to rank */
	private int[] getNumberOfRevealedOpponentUnits(GameBoard board){
		int[] numberOfStillPlacable = new int[12];
		for(int c=0; c<numberOfStillPlacable.length; c++){
			numberOfStillPlacable[c] = 0;
			numberOfStillPlacable[c] = Unit.getUnitTypeOfRank(c).getQuantity();
			numberOfStillPlacable[c] -= this.gameView.getNumberOfOpponentDefeatedUnits(Unit.getUnitTypeOfRank(c));
		}
		for(int cy=0; cy<board.getHeight(); cy++){
			for(int cx=0; cx<board.getWidth(); cx++){
				Unit unit = board.getUnit(cx, cy);
				//Make sure that unit is not air, lake or unknown
				if(!unit.isAir() && !unit.isLake() && !unit.isUnknown()){
					//Is Opponent's Unit
					if(!unit.getOwner().equals(this.gameView.getPlayerID())){
						numberOfStillPlacable[unit.getType().getRank()]--;
					}
				}
				
			}
		}
		return numberOfStillPlacable;
	}

	@Override
	protected Setup setup() {
		//TODO: Remove Random Setup here!!!
		Setup setup = new Setup(10,4);
		List<Unit> availableUnits = new ArrayList<Unit>(gameView.getAvailableUnits());
		// shuffle the list containing all available units
		Collections.shuffle(availableUnits);
		//go through the list and place them on the board as the units appear in the randomly shuffled list
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 10; x++) {
				setup.setUnit(x, y, availableUnits.get(y * 10 + x));
			}
		}
		// no need to check if the setup is valid because it cannot be invalid by the way it is created
		// so simply sending the setup over to the game
		gameView.setSetup(setup);
		return setup;
	}

}
