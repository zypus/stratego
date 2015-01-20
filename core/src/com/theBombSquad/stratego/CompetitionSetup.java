package com.theBombSquad.stratego;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.player.Player;
import com.theBombSquad.stratego.player.ai.BluffingAI.MoveEvalAI;
import com.theBombSquad.stratego.player.ai.BluffingAI.StateMoveEvalAI;
import com.theBombSquad.stratego.player.ai.players.HybridAI;
import com.theBombSquad.stratego.player.ai.players.RandomAI;
import com.theBombSquad.stratego.player.ai.players.planner.TheQueen;
import com.theBombSquad.stratego.player.ai.players.random.OnePlyDeepAI;
import com.theBombSquad.stratego.player.ai.players.random.SetupPlayerAI;
import com.theBombSquad.stratego.player.ai.setup.FlagTactic;
import com.theBombSquad.stratego.player.ai.setup.FlagTactics.FlagTactic1;
import com.theBombSquad.stratego.player.ai.setup.FlagTactics.FlagTactic10;
import com.theBombSquad.stratego.player.ai.setup.FlagTactics.FlagTactic11;
import com.theBombSquad.stratego.player.ai.setup.FlagTactics.FlagTactic2;
import com.theBombSquad.stratego.player.ai.setup.FlagTactics.FlagTactic3;
import com.theBombSquad.stratego.player.ai.setup.FlagTactics.FlagTactic4;
import com.theBombSquad.stratego.player.ai.setup.FlagTactics.FlagTactic5;
import com.theBombSquad.stratego.player.ai.setup.FlagTactics.FlagTactic6;
import com.theBombSquad.stratego.player.ai.setup.FlagTactics.FlagTactic7;
import com.theBombSquad.stratego.player.ai.setup.FlagTactics.FlagTactic8;
import com.theBombSquad.stratego.player.ai.setup.FlagTactics.FlagTactic9;

public class CompetitionSetup implements Game.GameListener {
	private static final int MAX_ROUNDS = 100;
	private static final int NUM_OF_GAMES = 10;

	private int round = 0;
	int player1Wins = 0;
	int player2Wins =00;
	int draws = 0;
	int totalPlys = 0;
	int i = 7;
	int j = 2;

	private Game game;
	private Player player1;
	private Player player2;
	private final Player mover1;
	private final Player mover2;
	ArrayList<FlagTactic> flagTactics;
	Game.GameView playerOneView ;
	Game.GameView playerTwoView;

	private List<Integer> rounds = new ArrayList<Integer>(MAX_ROUNDS);

	public static void main(String[] args) {
		new CompetitionSetup();
	}

	public CompetitionSetup() {
		game = new Game();
		game.setGameListener(this);
		// creates the two game views, one for each player perspective
	 playerOneView = new Game.GameView(game,
				StrategoConstants.PlayerID.PLAYER_1);
		 playerTwoView = new Game.GameView(game,
				StrategoConstants.PlayerID.PLAYER_2);
		// create some observer view

		flagTactics = new ArrayList<FlagTactic>();
		flagTactics.add(new FlagTactic1());
		flagTactics.add(new FlagTactic2());
		flagTactics.add(new FlagTactic3());
		flagTactics.add(new FlagTactic4());
		flagTactics.add(new FlagTactic5());
		flagTactics.add(new FlagTactic6());
		flagTactics.add(new FlagTactic7());
		flagTactics.add(new FlagTactic8());
		flagTactics.add(new FlagTactic9());
		flagTactics.add(new FlagTactic10());
		flagTactics.add(new FlagTactic11());

		mover1 = new TheQueen(playerOneView);
		mover2 = new StateMoveEvalAI(playerTwoView);

		player1 = new HybridAI(playerOneView).setMover(mover1).setSetuper(
				new SetupPlayerAI(playerOneView, i));
		player2 = new HybridAI(playerTwoView).setMover(mover2).setSetuper(
				new SetupPlayerAI(playerTwoView,j));
		// player1.setLearning(true);
		// player2.setLearning(true);

		game.reset();
		gameFinished(-1, null);

	}

	@Override
	public void gameFinished(int ply, StrategoConstants.PlayerID winner) {
		if (ply >= 0) {
			if (winner != null) {
				if (winner == StrategoConstants.PlayerID.PLAYER_1) {
					player1Wins++;
				} else if (winner == StrategoConstants.PlayerID.PLAYER_2) {
					player2Wins++;
				} else {
					draws++;
				}
			}
			totalPlys += ply;
			rounds.add(ply);
			
		}
		if (round < NUM_OF_GAMES) {
			round++;
			// mover1.reset();
			game.reset();
			game.setPlayer1(player1);
			game.setPlayer2(player2);
			game.startSetupPhase();
		
		} else {
			round=1;

			System.out.println("FlagTact: " + (i + 1) + " vs FlagTact: "
					+ (j + 1) + " Result: " + player1Wins + "/" + player2Wins
					+ "/" + draws + " - average game length: " + totalPlys
					/ NUM_OF_GAMES);
			player1Wins = 0;
			player2Wins = 0;
			draws = 0;
			totalPlys = 0;

			j++;
			if (j >= 11) {
				i++;
				j = 0;
			}
			if(i!=11){
			game.reset();
			player1 = new HybridAI(playerOneView).setMover(mover1).setSetuper(
					new SetupPlayerAI(playerOneView, i));
			player2 = new HybridAI(playerTwoView).setMover(mover2).setSetuper(
					new SetupPlayerAI(playerTwoView,j));
			game.setPlayer1(player1);
			game.setPlayer2(player2);
			game.startSetupPhase();
			}
			
			

		}
	}

	@Override
	public boolean performPly(final int ply) {
		
		if (ply % 1000 == 0) {
		}
		if (ply > 3000) {
			round--;
			// mover1.reset();
			// player2.reset();
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
