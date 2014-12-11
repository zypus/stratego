package com.theBombSquad.stratego.player.ai.setup;

import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
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
import lombok.Getter;

import java.util.ArrayList;

public class AISetup extends Setup{
	FlagTactic flagTactic;
	Strategy strategy;
	@Getter
	private  ArrayList<Unit> availableUnits;
	@Getter
	private Game.GameView view;

	public AISetup(Game.GameView view) {
		super(10, 4);
		this.view = view;
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
		flagTactic.addSetup(this);
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
		ArrayList<FlagTactic> flagTactics= new ArrayList<FlagTactic>();
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

		// add them all one by one
		int random= (int)(Math.random()*flagTactics.size());
		flagTactic=flagTactics.get(random);
		random= (int)(Math.random()*flagTactic.getStrategies().size());
		strategy= flagTactic.getStrategies().get(random);
	}



}
