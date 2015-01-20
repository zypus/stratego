package com.theBombSquad.stratego.player.ai.players.planner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.theBombSquad.stratego.StrategoConstants;
import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.AI;
import com.theBombSquad.stratego.player.ai.AIGameState;
import com.theBombSquad.stratego.player.ai.players.planner.plans.PlanAttackWeakerRevealedAdjacent;
import com.theBombSquad.stratego.player.ai.players.planner.plans.PlanAvoidHiddenStronger;
import com.theBombSquad.stratego.player.ai.players.planner.plans.PlanBlindMarchKill;
import com.theBombSquad.stratego.player.ai.players.planner.plans.PlanDiscourageLoops;
import com.theBombSquad.stratego.player.ai.players.planner.plans.PlanBluffing;
import com.theBombSquad.stratego.player.ai.players.planner.plans.PlanDoNOTAttackStrongerPiece;
import com.theBombSquad.stratego.player.ai.players.planner.plans.PlanFleeStrongerRevealedAdjacent;
import com.theBombSquad.stratego.player.ai.players.planner.plans.PlanKillWeakerHidden;
import com.theBombSquad.stratego.player.ai.players.planner.plans.PlanStrongestPieceAttackPlan;
import com.theBombSquad.stratego.player.ai.players.random.SetupPlayerAI;
import com.theBombSquad.stratego.player.ai.setup.AISetup;

/** The Queen, AI Based On Several Predefined Plans That Evaluate Each Possible Move According To Unit Values And A Little Bit Of 'Research' Based Magic */
public class TheQueen extends AI{
	
	private static final float[] unitValues = new float[]{600f, 200f, 30f, 25f, 15f, 25f, 50f, 75f, 100f, 200f, 400f, 20f};
	private int[] weights={254,243,237,245,236,247,255,230,260,240,275};
	
	public static float getUnitValue(UnitType unit){
		return unitValues[unit.getRank()];
	}
	
	private List<Plan> plans;
	private boolean planSetupFinished = false;
	
	public TheQueen(GameView gameView) {
		super(gameView);
	}
	
	private PlanBluffing bluffing;
	
	private void planSetup(){
		bluffing = new PlanBluffing();
		plans = new ArrayList<Plan>();
		plans.add(new PlanAttackWeakerRevealedAdjacent());
		plans.add(new PlanFleeStrongerRevealedAdjacent());
		plans.add(new PlanDefendFlag());
		plans.add(new PlanReveal());
		plans.add(new PlanKillWeakerHidden());
		plans.add(new PlanStrongestPieceAttackPlan());
		plans.add(new PlanDiscourageLoops());
		plans.add(new PlanDoNOTAttackStrongerPiece());
		plans.add(bluffing);
		for(int cy=0; cy<10; cy++){
			for(int cx=0; cx<10; cx++){
				if(gameView.getUnit(cx, cy).getOwner().equals(gameView.getOpponentID())){
					plans.add(new PlanMarchKill(gameView.getUnit(cx, cy)));
					plans.add(new PlanBlindMarchKill(gameView.getUnit(cx, cy)));
				}
			}
		}
		for(int c=0; c<12; c++){
			UnitType type = Unit.getUnitTypeOfRank(c);
			if(!type.equals(Unit.UnitType.BOMB) && !type.equals(Unit.UnitType.FLAG)){
				plans.add(new PlanAvoidHiddenStronger(type));
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
		for(Move move : moves){
			bluffing.normalize(move);
		}
		float bestProfit = Float.NEGATIVE_INFINITY;
		ArrayList<Move> bestMoves = new ArrayList<Move>();
		for(Move move : moves){
			float currentProfit = 0;
			for(Plan plan : plans){
				currentProfit += plan.evaluateMove(gameView, move);
			}
			if(currentProfit>bestProfit){
				bestProfit = currentProfit;
				bestMoves = new ArrayList<Move>();
				bestMoves.add(move);
			}
			else if(currentProfit==bestProfit){
				bestMoves.add(move);
			}
		}
		Collections.shuffle(bestMoves);
		Move bestMove = bestMoves.get(0);
		gameView.performMove(bestMove);
		return bestMove;
	}
	
	
	@Override
	protected Setup setup() {
		boolean f = true;
		
		if(f){
			return new SetupPlayerAI(gameView).setup_directAccessOverwrite();
		}
		else{
			Setup setup = new Setup(10,4);
			List<Unit> availableUnits = new ArrayList<Unit>(gameView.getAvailableUnits());
			// shuffle the list containing all available units
			Collections.shuffle(availableUnits);
			//go through the list and place them on the board as the units appear in the randomly shuffled list
			for (int y = 0; y < 4; y++) {
				for (int x = 0; x < 10; x++) {
					setup.setUnit(x, y, availableUnits.get(y * 10 + x));
				}
			}
			// no need to check if the setup is valid because it cannot be invalid by the way it is created
			// so simply sending the setup over to the game
			gameView.setSetup(setup);
			return setup;
		}
	}
	public int[] getWeights(){
		return weights;
	}

}
