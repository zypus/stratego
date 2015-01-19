package com.theBombSquad.stratego.player.ai.players.random;

import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.player.ai.AI;
import com.theBombSquad.stratego.player.ai.evaluationFunctions.EvaluationFunctionX;
import com.theBombSquad.stratego.player.ai.evaluationFunctions.SimpleBloodthirstyEvaluationFunction;
import com.theBombSquad.stratego.player.ai.evaluationFunctions.SimpleFunctionOfEvaluation;
import com.theBombSquad.stratego.player.ai.schrodingersBoard.SchrodingersBoard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OnePlyDeepAI extends AI{

	public OnePlyDeepAI(GameView gameView) {
		super(gameView);
	}

	@Override protected Move move() {
		EvaluationFunctionX eval = new SimpleFunctionOfEvaluation();
		SchrodingersBoard board = new SchrodingersBoard(super.gameView);
		List<Move> moves = board.generateAllMoves(super.gameView.getPlayerID());
		ArrayList<Move> bestMoves = new ArrayList<Move>();
		float bestScore = Float.NEGATIVE_INFINITY;
		for(Move move : moves){
			if(super.gameView.validateMove(move)){
				float currentScore = 0;
				List<SchrodingersBoard> schrodingersList = board.generateFromMove(move);
				if(schrodingersList.size()>0){
					float relProb = 0;
					for(SchrodingersBoard possibleResult : schrodingersList){
						relProb = possibleResult.getRelativeProbability();
						currentScore += possibleResult.evaluate(eval, super.gameView.getPlayerID())*possibleResult.getRelativeProbability();
					}
					currentScore = currentScore / relProb;
					if(currentScore==bestScore){
						bestMoves.add(move);
					}
					else if(currentScore>bestScore){
						bestMoves = new ArrayList<Move>();
						bestMoves.add(move);
						bestScore = currentScore;
					}
				}
			}
		}
		Collections.shuffle(bestMoves);
		Move bestMove = bestMoves.get(0);
		gameView.performMove(bestMove);
		return bestMove;
	}

	@Override protected Setup setup() {
		return new SetupPlayerAI(gameView).setup_directAccessOverwrite();
//		Setup setup = new Setup(10,4);
//		List<Unit> availableUnits = new ArrayList<Unit>(gameView.getAvailableUnits());
//		// shuffle the list containing all available units
//		Collections.shuffle(availableUnits);
//		//go through the list and place them on the board as the units appear in the randomly shuffled list
//		for (int y = 0; y < 4; y++) {
//			for (int x = 0; x < 10; x++) {
//				setup.setUnit(x, y, availableUnits.get(y * 10 + x));
//			}
//		}
//		// no need to check if the setup is valid because it cannot be invalid by the way it is created
//		// so simply sending the setup over to the game
//		gameView.setSetup(setup);
//		return setup;
	}

}
