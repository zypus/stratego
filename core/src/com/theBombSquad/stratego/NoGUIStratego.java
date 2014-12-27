package com.theBombSquad.stratego;

import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.player.Player;
import com.theBombSquad.stratego.player.ai.players.HybridAI;
import com.theBombSquad.stratego.player.ai.players.RandomAI;
import com.theBombSquad.stratego.player.ai.players.random.MctsAI;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created 10/12/14
 */
public class NoGUIStratego implements Game.GameListener {
	private static final int MAX_ROUNDS = 1000;

	private int round = 0;
	int player1Wins = 0;
	int player2Wins = 0;
	int draws = 0;

	private Game       game;
	private Player player1;
	private Player     player2;

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

		player1 = new HybridAI(playerOneView).setMover(new MctsAI(playerOneView))
											 .setSetuper(new RandomAI(playerOneView));
		player2 = new HybridAI(playerTwoView).setMover(new RandomAI(playerTwoView))
											 .setSetuper(new RandomAI(playerTwoView));

//		player1.setLearning(true);
		//		player2.setLearning(true);

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
		}
		if (round < MAX_ROUNDS) {
			round++;
			System.out.println("Starting round "+round);
			game.reset();
			game.setPlayer1(player1);
			game.setPlayer2(player2);

			game.startSetupPhase();
		} else {
			System.out.println("Result: "+player1Wins+"/"+player2Wins+"/"+draws);
//			player1.save("test/TDStratego/player1.net");
			//			player2.save("test/TDStratego/player2.net");
		}
	}

	@Override
	public boolean performPly(final int ply) {
		if (ply % 1000 == 0) {
			System.out.println("Ply "+ply);
		}
		if (ply < 0) {
			System.out.println("Round interrupted!");
//			player1.reset();
			//			player2.reset();
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
