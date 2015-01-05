package com.theBombSquad.stratego.player.ai.players.TDStratego;

import com.theBombSquad.stratego.StrategoConstants;
import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.player.Player;
import com.theBombSquad.stratego.player.ai.players.HybridAI;
import com.theBombSquad.stratego.player.ai.players.random.SetupPlayerAI;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created 09/12/14
 */
public class TDStrategoLearner implements Game.GameListener {

	private static final int MAX_ROUNDS = 100;

	private int round = 0;

	private Game game;
	private Player player1;
	private Player player2;
	private final TDStratego stratego1;
	private final TDStratego stratego2;

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

		stratego1 = new TDStratego(playerOneView);
		player1 = new HybridAI(playerOneView).setMover(stratego1)
											 .setSetuper(new SetupPlayerAI(playerOneView));
		stratego2 = new TDStratego(playerTwoView);
		player2 = new HybridAI(playerOneView).setMover(stratego2)
													   .setSetuper(new SetupPlayerAI(playerOneView));

		stratego1.setLearning(true);
		stratego2.setLearning(true);

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
			stratego1.save("test/TDStratego/player1.net");
			stratego2.save("test/TDStratego/player2.net");
		}
	}

	@Override
	public boolean performPly(final int ply) {
		if (ply % 1000 == 0) {
			System.out.println("Ply "+ply);
		}
		if (ply < 0) {
			System.out.println("Round interrupted!");
			stratego1.reset();
			stratego2.reset();
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
