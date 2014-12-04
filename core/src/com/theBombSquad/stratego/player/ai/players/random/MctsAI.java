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
import com.theBombSquad.stratego.player.ai.schrodingersBoard.SchrodingersBoard;

public class MctsAI extends AI{
	
	float[] evals;

	public MctsAI(GameView gameView) {
		super(gameView);
	}

	@Override
	protected Move move() {
		Move move = mcts();
		gameView.performMove(move);
		return move;
	}
	
	/* Performs actual MCTS 
	private Move mcts(){
		Random rand = new Random();
		SchrodingersBoard b = new SchrodingersBoard(this.gameView);
		List<Move> moves = b.generateAllMoves(gameView.getPlayerID());
		//While Check
		Move m = moves.get(rand.nextInt(moves.size()));
		List<SchrodingersBoard> board = new ArrayList<SchrodingersBoard>();
		List<Move> hypMoves = new ArrayList<Move>();
		boolean self = false;
		for(int c=0; c<5; c++){
			board = b.generateFromMove(m);
			hypMoves = board.get(rand.nextInt(board.size())).generateAllMoves((PlayerID)(self==true?gameView.getPlayerID():gameView.getOpponentID()));
			m = hypMoves.get(rand.nextInt(hypMoves.size()));
			self = !self;
		}
		//Return Random Move
		return moves.get(rand.nextInt(moves.size()));
	}
	*/
	
	private Move mcts(){
		//first generate your best 5 moves
		//simpleEvaluationFunction.evaluate(GameBoard, PlayerID);
		SchrodingersBoard b = new SchrodingersBoard(this.gameView);
		Move[] bestMoves = generateBestMoves(b, gameView.getPlayerID());
		
		//then check what move would give opponent least chance to decrease your evaluation
		//	so take move that decreases evaluation least after opponent move.
		//	so for every move take five best opponent moves.
		PlayerID opponent = StrategoConstants.PlayerID.PLAYER_1;
		if(opponent==gameView.getPlayerID()){
			opponent = StrategoConstants.PlayerID.PLAYER_2;
		}
		float[] evalPerMove = new float[bestMoves.length];
		//per move
		for(int i = 0; i < bestMoves.length ; i++){
			List<SchrodingersBoard> board = new ArrayList<SchrodingersBoard>();
			ArrayList<Float> evalOpp = new ArrayList<Float>();
			board = b.generateFromMove(bestMoves[i]);
			//per possible board (max = 3)
			for(int j = 0; j < board.size(); j++){
				generateBestMoves(b, opponent);
				for(int k = 0; k < evals.length; k++){
					evalOpp.add(evals[k]);
				}
			}
			//calculate average
			float average = 0;
			for(int l = 0; l < evalOpp.size(); l++){
				average = average + evalOpp.get(l);
			}
			average =  average/(evalOpp.size());
			evalPerMove[i] = average;
		}
		int worst = 0;
		for(int m = 0; m<evalPerMove.length; m++){
			if(evalPerMove[m] < evalPerMove[worst]){
				worst = m;
			}
		}
		return bestMoves[worst];
	}
	
	//Now shut up, Flo. I like it this way. Don't ruin my mood, please :)
	private Move[] generateBestMoves(SchrodingersBoard b, PlayerID player){
		List<Move> moves = b.generateAllMoves(player);
		Move[] bestMoves = new Move[5];
		evals = new float[bestMoves.length];
		int iteration = 0;
		for(int i = 0; i < evals.length; i++){
			evals[i] = 0;
		}
		//Go through all moves
		for( int i = 0; i < moves.size(); i++){
			List<SchrodingersBoard> board = new ArrayList<SchrodingersBoard>();
			board = b.generateFromMove(moves.get(i));
			//Evaluate all possible boards for move:
			for(int j = 0; j < board.size(); j++){
				float eval = (simpleEvaluationFunction.evaluate(board.get(j), player))*(board.get(j).getProbability());
				boolean changed = false;
				//Check if new evaluation is higher than any we already had
				for( int k = 0; k < evals.length; k++){
					//theoretically it is possible to fill ony one of evals, so first fill
					if(iteration < 5){
						evals[iteration] = eval;
						bestMoves[iteration] = moves.get(i); 
					}
					if(!changed){
						if(evals[j] < eval){
							evals[j] = eval;
							bestMoves[k] = moves.get(i);
							changed = true;
						}
					}
				}
			}
		}
		return bestMoves;
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
