package com.theBombSquad.stratego.player;

import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import lombok.Getter;

import static com.theBombSquad.stratego.gameMechanics.Game.*;

/**
 * Abstract player class which performs each player action (setup,move,idle).
 * Each action is run on a separate thread and subsequent action interrupt the previous action.
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @author Flo
 */
public abstract class Player {

	@Getter
	protected final GameView gameView;

	private Thread currentAction = null;

	public Player(GameView gameView) {
		this.gameView = gameView;
	}

	public void startSetup() {
		stopAction();
		currentAction = new Thread(new Runnable() {
			@Override public void run() {
				setup();
			}
		});
		currentAction.start();
	}

	public void startMove() {
		stopAction();
		currentAction = new Thread(new Runnable() {
			@Override public void run() {
				move();
			}
		});
		currentAction.start();
	}

	public void startIdle() {
		stopAction();
		currentAction = new Thread(new Runnable() {
			@Override public void run() {
				idle();
			}
		});
		currentAction.start();
	}

	public void startCleanup() {
		stopAction();
		currentAction = new Thread(new Runnable() {
			@Override public void run() {
				cleanup();
			}
		});
		currentAction.start();
	}

	private void stopAction() {
		if (currentAction != null && currentAction.isAlive()) {
			currentAction.interrupt();
			currentAction = null;
		}
	}

	/**
	 * Implementation is responsible to finish their work once the thread gets interrupted.
	 * -- if ( Thread.currentThread().isInterrupted() ) --
	 */
	protected abstract Move move();
	protected abstract Setup setup();
	protected void idle() {

	}

	protected void cleanup() {
		gameView.finishedCleanup();
	}

	/**
	 * Method to call the move() method directly. Primarily used by the remote players to access there underlying local player instance
	 * method, without starting another thread. Moreover otherwise the remote player cannot access the move of the local player.
	 * @return The performed move.
	 */
	public Move move_directAccessOverwrite() {
		return move();
	}

	/**
	 * Method to call the setup() method directly. Primarily used by the remote players to access there underlying local player instance
	 * method, without starting another thread. Moreover otherwise the remote player cannot access the setup of the local player.
	 *
	 * @return The requested setup.
	 */
	public Setup setup_directAccessOverwrite() {
		return setup();
	}

	/**
	 * Method to call the idle() method directly. Primarily used by the remote players to access there underlying local player instance
	 * method, without starting another thread.
	 */
	public void idle_directAccessOverwrite() {
		idle();
	}

}
