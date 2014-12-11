package com.theBombSquad.stratego.player.ai.players.random;

import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.player.ai.AI;
import com.theBombSquad.stratego.player.ai.evaluationFunction.SimpleEvaluationFunction;
import com.theBombSquad.stratego.player.ai.schrodingersBoard.SchrodingersBoard;

import java.util.List;
import java.util.Random;

public class NegamaxAI extends AI
{
	private final int MAX_DEPTH = 4;
	private Random                   random             = new Random();
	private SimpleEvaluationFunction evaluationFunction = new SimpleEvaluationFunction();

	public NegamaxAI(GameView gameView) {
		super(gameView);
	}

	private Float negamax(int depth, SchrodingersBoard board, float alpha, float beta, PlayerID playerID) {
		if (depth == 0) {
			return board.evaluate(evaluationFunction, playerID);
		}
		List<Move> moves = board.generateAllMoves(playerID);
		for (Move move : moves) {
			List<SchrodingersBoard> boards = board.generateFromMove(move);//returns a list of schrodinger boards
			if (boards.size() > 1) {
				alpha = Math.max(alpha, -expectimax(depth - 1, boards, -beta, -alpha, playerID.getOpponent()));
			} else {
				alpha = Math.max(alpha, -negamax(depth - 1, boards.get(0), -beta, -alpha, playerID.getOpponent()));
			}
			//beta cutoff
			if (alpha >= beta)
			{
				return alpha;
			}
		}

		return alpha;
	}

	private Float expectimax(int depth, List<SchrodingersBoard> boards, float alpha, float beta, PlayerID playerID)
	{
		if (depth==0)
		{
			return boards.get(0).evaluate(evaluationFunction, playerID);
		}
		float sum = 0;
		for (SchrodingersBoard board:boards)
		{
			sum+=board.getRelativeProbability()*-negamax(depth, board, alpha, beta, playerID);
		}
		return sum;
	}

	private SchrodingersBoard generateAllMoves(PlayerID player)
	{
		return null;
	}

	protected Move move()
	{
		float alpha = -Float.MAX_VALUE;
		float beta = Float.MAX_VALUE;
		SchrodingersBoard board = new SchrodingersBoard(gameView);
		List<Move> moves = board.generateAllMoves(this.gameView.getPlayerID());
//		Collections.shuffle(moves);
		Move bestMove = moves.get(random.nextInt(moves.size()));
		for (Move move:moves)
		{
			List<SchrodingersBoard> boards = board.generateFromMove(move); //returns a list of schrodinger boards
			Float value;
			if (boards.size()>1)
			{
				value = -expectimax(MAX_DEPTH-1, boards, -beta, -alpha, gameView.getPlayerID());
			}
			else
			{
				value = -negamax(MAX_DEPTH-1, boards.get(0), -beta, -alpha, gameView.getPlayerID());
			}
			if(value>alpha)
			{
				alpha = value;
				bestMove = move;
			}
			//beta cutoff
			if (alpha >= beta)
			{
				bestMove = move;
				break;
			}
		}

		gameView.performMove(bestMove);
		return bestMove;

	}

	@Override
	protected Setup setup()
	{
		System.out.println("NEGAMAX");
		return new SetupPlayerAI(gameView).setup_directAccessOverwrite();
	}

}
