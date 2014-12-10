package com.theBombSquad.stratego.player.ai.players.random;

import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.player.ai.AI;
import com.theBombSquad.stratego.player.ai.evaluationFunctions.SimpleEvaluationFunction;
import com.theBombSquad.stratego.player.ai.schrodingersBoard.SchrodingersBoard;

import java.util.List;

public class NegamaxAI extends AI
{
	private final int maxDepth = 3;
	private SimpleEvaluationFunction evaluationFunction = new SimpleEvaluationFunction();

	public NegamaxAI(GameView gameView) {
		super(gameView);
	}

	private Float negamax(int depth, SchrodingersBoard board, float alpha, float beta) {
		SimpleEvaluationFunction simpleEvaluationFunction = new SimpleEvaluationFunction();
		if (depth == 0) {
			return simpleEvaluationFunction.evaluate(null, null);
		}
		List<Move> moves = board.generateAllMoves(this.gameView.getPlayerID());
		for (Move move : moves) {
			List<SchrodingersBoard> boards = board.generateFromMove(move);//returns a list of schrodinger boards
			if (boards.size() > 1) {
				alpha = Math.max(alpha, -expectimax(depth - 1, boards, -beta, -alpha));
			} else {
				alpha = Math.max(alpha, -negamax(depth-1, boards.get(0), -beta, -alpha));
			}
			//beta cutoff
			if (alpha >= beta)
			{
				return alpha;
			}
		}

		return alpha;
	}

	private Float expectimax(int depth, List<SchrodingersBoard> boards, float alpha, float beta)
	{
		if (depth==0)
		{
			return evaluationFunction.evaluate(null, null);
		}
		float sum = 0;
		for (SchrodingersBoard board:boards)
		{
			sum+=board.getRelativeProbability()*-negamax(depth, board, alpha, beta);
		}
		return sum;
	}

	private SchrodingersBoard generateAllMoves(PlayerID player)
	{
		return null;
	}

	protected Move move()
	{
		float max = Float.MIN_VALUE;
		float alpha = Float.MIN_VALUE;
		float beta = Float.MAX_VALUE;
		Move bestMove = null;
		SchrodingersBoard board = new SchrodingersBoard(gameView);
		List<Move> moves = board.generateAllMoves(this.gameView.getPlayerID());
		for (Move move:moves)
		{
			List<SchrodingersBoard> boards = board.generateFromMove(move); //returns a list of schrodinger boards
			Float value;
			if (boards.size()>1)
			{
				value = -expectimax(maxDepth-1, boards, -beta, -alpha);
			}
			else
			{
				value = -negamax(maxDepth-1, boards.get(0), -beta, -alpha);
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
		// TODO Auto-generated method stub
		return null;
	}

}
