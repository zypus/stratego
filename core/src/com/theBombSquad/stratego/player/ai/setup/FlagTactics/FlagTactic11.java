package com.theBombSquad.stratego.player.ai.setup.FlagTactics;

import java.util.ArrayList;

import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.setup.AISetup;
import com.theBombSquad.stratego.player.ai.setup.FlagTactic;
import com.theBombSquad.stratego.player.ai.setup.UnitPlacement;

public class FlagTactic11 extends FlagTactic{
	
	public FlagTactic11(AISetup setup) {
		super(setup);
		proceed();
	}

	public FlagTactic11() {
		super();

	}
	public void addSetup(AISetup setup){
		super.setup=setup;
		proceed();
	}

	private void proceed() {
		
		ArrayList<UnitType> units = new ArrayList<UnitType>();
		units.add(UnitType.BOMB);
		units.add(UnitType.BOMB);
		units.add(UnitType.MARSHAL);
		ArrayList<UnitType> order = super.giveOrder(units);
		int y = 0;
		if( order.get(0) == UnitType.MARSHAL){
			blockMarshal(y);
		}
		else{ 
			blockBomb(y);
		}
		y = 4;
		if(order.get(1) == UnitType.MARSHAL){
			blockMarshal(y);
		}
		else{ 
			blockBomb(y);
		}
		y = 8;
		if(order.get(2) == UnitType.MARSHAL){
			blockMarshal(y);
		}
		else{ 
			blockBomb(y);
		}
		blockFlag();
		
	}
	
	private void blockMarshal(int y){
		ArrayList<UnitType> units = new ArrayList<UnitType>();
		units.add(UnitType.MARSHAL);
		units.add(UnitType.GENERAL);
		units.add(UnitType.COLONEL);
		units.add(UnitType.MAJOR);
		ArrayList<UnitType> order = super.giveOrder(units);
		
		UnitPlacement toPut = new UnitPlacement(order.get(0), y,0, 1);
		super.placeUnit(toPut);
		toPut = new UnitPlacement(order.get(1),  y+1,0, 1);
		super.placeUnit(toPut);
		toPut = new UnitPlacement(order.get(2),  y+1,1, 1);
		super.placeUnit(toPut);
		toPut = new UnitPlacement(order.get(3), y, 1, 1);
		super.placeUnit(toPut);
		
		//SPY
		int [][] spots = {
				{y-1 , 0 , 1},
				{y-1, 1	,  2},
				{y-1,2	,  1},
				{y,2	  , 2},
				{y+1,2	,  2},
				{y+2,2	 , 1},
				{y+2,1	,  2},
				{y+2,0  ,  1}
		};
		super.empty();
		//generate alle possible spyplacements
		for(int i = 0; i< spots.length ; i++){
			if(spots[i][0]>=0&&spots[i][0]<setup.getWidth()){
			if(super.isFree(spots[i][0], spots[i][1])){
				possiblePlacements.add(new UnitPlacement(UnitType.SPY, spots[i][0]  , spots[i][1], spots[i][2]));
			}
		}}
		toPut = super.randomizeUnitPlacement();
		super.placeUnit(toPut);
	}
	
	private void blockBomb(int y){
		UnitPlacement toPut = new UnitPlacement(UnitType.BOMB, y, 0, 1);
		super.placeUnit(toPut);
		toPut.setX(y+1);
		super.placeUnit(toPut);
	}
	
	private void blockFlag(){
		super.empty();
		UnitPlacement toPut = new UnitPlacement(UnitType.FLAG, 0, 3, 1);
		for(int i = 0 ; i < setup.getWidth() ; i++ ){
			toPut.setX(i);
			if(i <= 3 || i >= 6){
				toPut.setWeight(3);
				possiblePlacements.add(toPut);
			}
			else{
				toPut.setWeight(1);
				possiblePlacements.add(toPut);
			}
		}
		toPut = super.randomizeUnitPlacement();
		super.placeUnit(toPut);
		int x = toPut.getX();
		toPut.setUnitType(UnitType.BOMB);
		if(super.isFree(x+1, 3)){
			toPut.setX(x+1);
			super.placeUnit(toPut);
		}
		if(super.isFree( x,2)){
			toPut.setX(x);
			toPut.setY(2);
			super.placeUnit(toPut);
		}
		if(super.isFree(x-1,3)&& super.hasAvailable(UnitType.BOMB)){
			toPut.setX(x-1);
			toPut.setX(3);
			super.placeUnit(toPut);
		}
		if(super.hasAvailable(UnitType.BOMB)){
			if(super.isFree(x+1,2)){
				toPut.setX(x+1);
				toPut.setY(2);
				super.placeUnit(toPut);
			}
			else if(super.isFree(x-1,2 )){
				toPut.setX(x-1);
				toPut.setY(2);
				super.placeUnit(toPut);
			}
		}
	}
}
