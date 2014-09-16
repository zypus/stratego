package com.theBombSquad.stratego.player;

import com.theBombSquad.stratego.gameMechanics.GameView;

/**
 * Abstract player class which performs each player action (setup,move,idle).
 * Each action is run on a separate thread and subsequent action interrupt the previous action.
 *
 * @author Fabian Fränz <f.fraenz@t-online.de>
 * @author Flo
 */
public abstract class Player {

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
	protected abstract void move();
	protected abstract void setup();
	protected abstract void idle();

}
