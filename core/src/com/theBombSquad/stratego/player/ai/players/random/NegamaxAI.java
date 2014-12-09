package com.theBombSquad.stratego.player.ai.players.random;

import java.util.List;

import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.player.ai.AI;
import com.theBombSquad.stratego.player.ai.evaluationFunction.simpleEvaluationFunction;
import com.theBombSquad.stratego.player.ai.schrodingersBoard.SchrodingersBoard;

public class NegamaxAI extends AI
{
	private final int maxDepth = 3;
	public NegamaxAI(GameView gameView) 
	{
		super(gameView);
	}

	private Float negamax(int depth, SchrodingersBoard board, float alpha, float beta)
	{	simpleEvaluationFunction simpleEvaluationFunction = new simpleEvaluationFunction();
		if (depth==0)
		{
			return simpleEvaluationFunction.evaluate(null, null);
		}
		List<Move> moves = board.generateAllMoves(player);
		for (Move move:moves)
		{
			List<SchrodingersBoard> boards = board.doesntyethaveaname(move); //returns a list of schrodinger boards
			if (boards.size()>1)
			{
				alpha = Math.max(alpha, -expectimax(depth-1, boards, -beta, -alpha));
			}
			else
			{
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
			return simpleEvaluationFunction.evaluate(null, null);
		}
		float sum = 0;
		for (SchrodingersBoard board:boards)
		{
			sum+=board.getProbability()*-negamax(depth, board, alpha, beta); 
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
		Move bestMove;
		SchrodingersBoard board = new SchrodingersBoard(gameView);
		List<Move> moves = board.generateAllMoves(player);
		for (Move move:moves)
		{
			List<SchrodingersBoard> boards = board.doesntYetHaveAName(move); //returns a list of schrodinger boards
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
