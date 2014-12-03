package com.theBombSquad.stratego.player.ai.setup;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.ai.setup.FlagTactics.FlagTactic1;
import com.theBombSquad.stratego.player.ai.setup.FlagTactics.*;

public class AISetup extends Setup{
	FlagTactic tactic;
	Strategy strategy;
	@Getter
	private  ArrayList<Unit> availableUnits;
	@Getter
	private GameView view;
	
	public AISetup(GameView view){
		super(10,4);
		this.view=view;
		initAvailableUnits();
		pickFlagTactic();
		executeTactics();
	}
	private void initAvailableUnits() {
//		availableUnits= new ArrayList<Unit>();
//		Unit.UnitType[] unitTypeEnum = {Unit.UnitType.FLAG, Unit.UnitType.BOMB, Unit.UnitType.SPY, Unit.UnitType.SCOUT, Unit.UnitType.SAPPER, Unit.UnitType.SERGEANT, Unit.UnitType.LIEUTENANT, Unit.UnitType.CAPTAIN, Unit.UnitType.MAJOR, Unit.UnitType.COLONEL, Unit.UnitType.GENERAL, Unit.UnitType.MARSHAL};//Unit.UnitType.values();
//		// create a list containing all units that needs to be placed on the board
//		for (Unit.UnitType type : unitTypeEnum) {
//			if(type!=Unit.UnitType.AIR && type!=Unit.UnitType.LAKE && type!=Unit.UnitType.UNKNOWN){
//				for (int i = 0; i < type.getQuantity(); i++) {
//					availableUnits.add(new Unit(type, view.getPlayerID()));
//				}
//			}
//		}
		this.availableUnits = new ArrayList<Unit>(view.getAvailableUnits());
	}
	
	//executing the strategy step by step
	private void executeTactics() {
		tactic.addSetup(this);
		for(int i = 0; i < strategy.getTactics().size();i++){
			strategy.getTactics().get(i).addSetup(this);
			this.board=strategy.getTactics().get(i).getSetup().getBoard();
			strategy.getTactics().remove(i);
			i--;
			/*if(view.getPlayerID()==PlayerID.PLAYER_1){
			for(int h =0; h<10;h++){
				System.out.println();
				for(int j=0;j<4;j++){
					System.out.print(board[j][h].getType().getRank());
				}
			}
			System.out.println();
			System.out.println();

		}*/
		}
//		for(int cy=0; cy<this.getHeight(); cy++){
//			for(int cx=0; cx<this.getWidth(); cx++){
//				System.out.print(this.getBoard()[cy][cx].getType()+" ");
//			}
//			System.out.println();
//		}
	}
	//picking random tactic and strategy possible for this tactic
	private void pickFlagTactic() {
		ArrayList<FlagTactic> tactics= new ArrayList<FlagTactic>();
		//FLAGTACTIC7 HAS BUGS
		tactics.add(new FlagTactic7());
		// add them all one by one
		int random= (int)(Math.random()*tactics.size());
		tactic=tactics.get(random);
		random= (int)(Math.random()*tactic.getStrategies().size());
		strategy= tactic.getStrategies().get(random);
	}
	
	
	
}
