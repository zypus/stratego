package com.theBombSquad.stratego.player.ai.setup.FlagTactics;

import java.util.ArrayList;

import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.setup.AISetup;
import com.theBombSquad.stratego.player.ai.setup.FlagTactic;
import com.theBombSquad.stratego.player.ai.setup.UnitPlacement;

public class FlagTactic4 extends FlagTactic{
	
	public FlagTactic4(AISetup setup) {
		super(setup);
		proceed();
	}

	public FlagTactic4() {
		super();

	}
	public void addSetup(AISetup setup){
		super.setup=setup;
		proceed();
	}

	private void proceed() {
		possiblePlacements = new ArrayList<UnitPlacement>();
		for (int j = 2; j < setup.getWidth() - 2; j++) {
			possiblePlacements.add(new UnitPlacement(UnitType.FLAG, j, 3, 1));
		}
		UnitPlacement toPut = super.randomizeUnitPlacement();
		int x = toPut.getX();
		int y = toPut.getY();
		// Flag/Scout
		super.placeUnit(toPut);
		// left bomb
		toPut.setX(toPut.getX() - 1);
		toPut.setUnitType(UnitType.BOMB);
		super.placeUnit(toPut);
		// Up bomb
		toPut.setX(toPut.getX() + 1);
		toPut.setY(toPut.getY() - 1);
		super.placeUnit(toPut);
		// right bomb
		toPut.setY(toPut.getY() + 1);
		toPut.setX(toPut.getX() + 1);
		super.placeUnit(toPut);
		// 4/5
		toPut.setX(toPut.getY() - 1);
		toPut.setUnitType(super.randomizeSL());
		super.placeUnit(toPut);
		toPut.setY(toPut.getX() - 2);
		toPut.setUnitType(super.randomizeSL());
		super.placeUnit(toPut);
		
		//all possible bombplacements
		
		int [][] spots = {
				{y  , x-2, 1},
				{y-1, x-2, 2},
				{y-2, x-2, 1},
				{y-2, x-1, 1},
				{y-2, x  , 2},
				{y-2, x+1, 1},
				{y-2, x+2, 1},
				{y-1, x+2, 2},
				{y  , x+2, 1}
		};
		
		super.empty();
		//generate alle possible bombplacements
		for(int i = 0; i< spots.length ; i++){
			possiblePlacements.add(new UnitPlacement(UnitType.BOMB  , spots[i][1], spots[i][0], spots[i][2]));
		}
		/* Save for if array doesn't work
		possiblePlacements.add(new UnitPlacement(UnitType.BOMB, x  , y-2, 1));
		possiblePlacements.add(new UnitPlacement(UnitType.BOMB, x-1, y-2, 2));
		possiblePlacements.add(new UnitPlacement(UnitType.BOMB, x-2, y-2, 1));
		possiblePlacements.add(new UnitPlacement(UnitType.BOMB, x-2, y-1, 1));
		possiblePlacements.add(new UnitPlacement(UnitType.BOMB, x-2, y  , 2));
		possiblePlacements.add(new UnitPlacement(UnitType.BOMB, x-2, y+1, 1));
		possiblePlacements.add(new UnitPlacement(UnitType.BOMB, x-2, y+2, 1));
		possiblePlacements.add(new UnitPlacement(UnitType.BOMB, x-1, y+2, 2));
		possiblePlacements.add(new UnitPlacement(UnitType.BOMB, x  , y+2, 1));
		*/
		
		//place 1/2/3 bombs
		int pick = (int)( Math.random()*9);
		toPut = super.randomizeUnitPlacement();
		super.placeUnit(toPut);
		if(pick > 1){
			System.out.println("here1");
			toPut = super.randomizeUnitPlacement();
			super.placeUnit(toPut);
		}
		if(pick >4){
			System.out.println("here2");
			toPut = super.randomizeUnitPlacement();
			super.placeUnit(toPut);
		}
		
		super.empty();
		
		//place 1/2/3 times 4/5
		for(int i = 0; i < spots.length ; i++){
			if(super.isFree( spots[i][1],spots[i][0])){
				possiblePlacements.add(new UnitPlacement(super.randomizeSL()  , spots[i][1], spots[i][0], spots[i][2]));
			}
		}
		int pick2 = (int) (Math.random()*9);
		toPut = super.randomizeUnitPlacement();
		super.placeUnit(toPut);
		if(pick2 > 1){
			toPut = super.randomizeUnitPlacement();
			super.placeUnit(toPut);
		}
		if(pick2 >4){
			toPut = super.randomizeUnitPlacement();
			super.placeUnit(toPut);
		}
		/*
		if(super.isFree(x, y-2)){
			possiblePlacements.add(new UnitPlacement(super.randomizeSL(), x  , y-2, 1));
		}
		if(super.isFree(x-1, y-2)){ 
			possiblePlacements.add(new UnitPlacement(super.randomizeSL(), x-1, y-2, 2));
		}
		if(super.isFree(x-2, y-2)){
			possiblePlacements.add(new UnitPlacement(super.randomizeSL(), x-2, y-2, 1));
		}
		if(super.isFree(x-2, y-1)){
			possiblePlacements.add(new UnitPlacement(super.randomizeSL(), x-2, y-1, 1));
		}
		if(super.isFree(x-2, y)){
			possiblePlacements.add(new UnitPlacement(super.randomizeSL(), x-2, y  , 2));
		}
		if(super.isFree(x-2, y+1))
		possiblePlacements.add(new UnitPlacement(super.randomizeSL(), x-2, y+1, 1));
		possiblePlacements.add(new UnitPlacement(super.randomizeSL(), x-2, y+2, 1));
		possiblePlacements.add(new UnitPlacement(super.randomizeSL(), x-1, y+2, 2));
		possiblePlacements.add(new UnitPlacement(super.randomizeSL(), x  , y+2, 2));
		*/
		
	}
}
