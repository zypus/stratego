package com.theBombSquad.stratego.player.ai.players.planner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.AI;
import com.theBombSquad.stratego.player.ai.setup.AISetup;

/** The Queen, AI Based On Several Predefined Plans That Evaluate Each Possible Move According To Unit Values And A Little Bit Of 'Research' Based Magic */
public class TheQueen extends AI{
	
	private static final float[] unitValues = new float[]{1000f, 200f, 30f, 25f, 15f, 25f, 50f, 75f, 100f, 200f, 400f, 20f};
	
	public static float getUnitValue(UnitType unit){
		return unitValues[unit.getRank()];
	}
	
	private List<Plan> plans;
	private boolean planSetupFinished = false;
	
	public TheQueen(GameView gameView) {
		super(gameView);
	}
	
	private void planSetup(){
		plans = new ArrayList<Plan>();
		plans.add(new PlanRandom());
		plans.add(new PlanAttackAdjacent());
		plans.add(new PlanPunishUnitReveals());
		plans.add(new PlanFleeDefeatableUnitFromKnownStrongerThreat());
		plans.add(new PlanDefendFlag());
		plans.add(new PlanReveal());
		for(int cy=0; cy<10; cy++){
			for(int cx=0; cx<10; cx++){
				if(gameView.getUnit(cx, cy).getOwner().equals(gameView.getOpponentID())){
					plans.add(new PlanMarchKill(gameView.getUnit(cx, cy)));
				}
			}
		}
		this.planSetupFinished = true;
	}
	
	
	@Override
	protected Move move() {
		if(!planSetupFinished){
			planSetup();
		}
		for(Plan plan : plans){
			plan.postMoveUpdate(gameView);
		}
		List<Move> moves = super.createAllLegalMoves(gameView, gameView.getCurrentState());
		Move bestMove = null;
		float bestProfit = Float.NEGATIVE_INFINITY;
		for(Move move : moves){
			float currentProfit = 0;
			for(Plan plan : plans){
				currentProfit += plan.evaluateMove(gameView, move);
			}
			if(currentProfit>bestProfit){
				bestProfit = currentProfit;
				bestMove = move;
			}
		}
		gameView.performMove(bestMove);
		return bestMove;
	}
	
	
	@Override
	protected Setup setup() {
		AISetup setup = new AISetup(gameView);
		gameView.setSetup(setup);
		//System.out.println(gameView.getPlayerID());
		Setup setup2 = new Setup(10,4);
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 10; x++) {
				setup2.setUnit(x, y, setup.getUnit(x, y));
			}
		}
		System.out.println(gameView.validateSetup(setup));
		return setup2;
		
//		Setup setup = new Setup(10,4);
//		List<Unit> availableUnits = new ArrayList<Unit>(gameView.getAvailableUnits());
//		// shuffle the list containing all available units
//		Collections.shuffle(availableUnits);
//		//go through the list and place them on the board as the units appear in the randomly shuffled list
//		for (int y = 0; y < 4; y++) {
//			for (int x = 0; x < 10; x++) {
//				setup.setUnit(x, y, availableUnits.get(y * 10 + x));
//			}
//		}
//		// no need to check if the setup is valid because it cannot be invalid by the way it is created
//		// so simply sending the setup over to the game
//		gameView.setSetup(setup);
//		return setup;
	}

}
