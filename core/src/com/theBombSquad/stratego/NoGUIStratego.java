package com.theBombSquad.stratego;

import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.player.Player;
import com.theBombSquad.stratego.player.ai.BluffingAI.MoveEvalAI;
import com.theBombSquad.stratego.player.ai.BluffingAI.StateMoveEvalAI;
import com.theBombSquad.stratego.player.ai.players.HybridAI;
import com.theBombSquad.stratego.player.ai.players.RandomAI;
import com.theBombSquad.stratego.player.ai.players.planner.TheQueen;
import com.theBombSquad.stratego.player.ai.players.random.SetupPlayerAI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created 10/12/14
 */
public class NoGUIStratego implements Game.GameListener {
	private static final int MAX_ROUNDS = 100;

	private int round = 0;
	int player1Wins = 0;
	int player2Wins = 0;
	int draws = 0;
	int totalPlys = 0;

	private Game       game;
	private Player player1;
	private Player     player2;
	private final Player mover1;
	private final Player mover2;

	private List<Integer> rounds = new ArrayList<Integer>(MAX_ROUNDS);

	public static void main(String[] args) {
		new NoGUIStratego();
	}

	public NoGUIStratego() {
		game = new Game();
		game.setGameListener(this);
		// creates the two game views, one for each player perspective
		Game.GameView playerOneView = new Game.GameView(game, StrategoConstants.PlayerID.PLAYER_1);
		Game.GameView playerTwoView = new Game.GameView(game, StrategoConstants.PlayerID.PLAYER_2);
		// create some observer view

		mover1 = new TheQueen(playerOneView);
		player1 = new HybridAI(playerOneView).setMover(mover1)
											 .setSetuper(new SetupPlayerAI(playerOneView, mover1.getWeights()));
		mover2 = new StateMoveEvalAI(playerTwoView);
		player2 = new HybridAI(playerTwoView).setMover(mover2)
											 .setSetuper(new SetupPlayerAI(playerTwoView, mover2.getWeights()));
		//		player1.setLearning(true);
		//		player2.setLearning(true);

		game.reset();
		gameFinished(-1, null);
	}

	@Override
	public void gameFinished(int ply, StrategoConstants.PlayerID winner) {
		if (ply >= 0) {
			System.out.println("Round ended at ply "+ply+" with winner "+winner);
			if (winner != null) {
				if (winner == StrategoConstants.PlayerID.PLAYER_1) {
					player1Wins++;
				} else if (winner == StrategoConstants.PlayerID.PLAYER_2){
					player2Wins++;
				} else {
					draws++;
				}
			}
			totalPlys += ply;
			rounds.add(ply);
		}
		if (round < MAX_ROUNDS) {
			round++;
			System.out.println("Starting round "+round);
//			mover1.reset();
			game.reset();
			game.setPlayer1(player1);
			game.setPlayer2(player2);

			game.startSetupPhase();
		} else {
			int median;
			Collections.sort(rounds);
			if (MAX_ROUNDS % 2 == 0) {
				median = (rounds.get(MAX_ROUNDS / 2 - 1) + rounds.get(MAX_ROUNDS / 2)) / 2;
			} else {
				median = rounds.get(MAX_ROUNDS/2);
			}
			System.out.println("Result: "+player1Wins+"/"+player2Wins+"/"+draws+" - average game length: "+totalPlys/MAX_ROUNDS+" median: "+median);
		}
	}

	@Override
	public boolean performPly(final int ply) {
		if (ply % 1000 == 0) {
			System.out.println("Ply "+ply);
		}
		if (ply > 3000) {
			System.out.println("Round interrupted!");
//			mover1.reset();
			//			player2.reset();
			game.reset();
			new Thread(new Runnable() {
				@Override
				public void run() {
					gameFinished(ply - 1, null);
				}
			}).start();
			return false;
		} else {
			return true;
		}
	}
}
