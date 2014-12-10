package com.theBombSquad.stratego.player.ai.players.TDStratego;

import com.theBombSquad.stratego.StrategoConstants;
import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.player.Player;
import com.theBombSquad.stratego.player.ai.players.RandomAI;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created 09/12/14
 */
public class TDStrategoLearner implements Game.GameListener {

	private static final int MAX_ROUNDS = 1000;

	private int round = 0;

	private Game game;
	private TDStratego player1;
	private Player player2;

	public static void main(String[] args) {
		new TDStrategoLearner();
	}

	public TDStrategoLearner() {
		game = new Game();
		game.setGameListener(this);
		// creates the two game views, one for each player perspective
		Game.GameView playerOneView = new Game.GameView(game, StrategoConstants.PlayerID.PLAYER_1);
		Game.GameView playerTwoView = new Game.GameView(game, StrategoConstants.PlayerID.PLAYER_2);
		// create some observer view

		player1 = new TDStratego(playerOneView);
		player2 = new RandomAI(playerTwoView);

		player1.setLearning(true);
//		player2.setLearning(true);

		// tell the game about the players
		gameFinished(-1, null);
	}

	@Override
	public void gameFinished(int ply, StrategoConstants.PlayerID winner) {
		if (ply >= 0) {
			System.out.println("Round ended at ply "+ply);
		}
		if (round < MAX_ROUNDS) {
			round++;
			System.out.println("Starting round "+round);
			game.reset();
			game.setPlayer1(player1);
			game.setPlayer2(player2);

			game.startSetupPhase();
		} else {
			player1.save("test/TDStratego/player1.net");
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
			player1.reset();
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
