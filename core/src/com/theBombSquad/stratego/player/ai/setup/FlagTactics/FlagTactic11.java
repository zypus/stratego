package com.theBombSquad.stratego.player.ai.setup.FlagTactics;

import java.util.ArrayList;

import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.setup.AISetup;
import com.theBombSquad.stratego.player.ai.setup.FlagTactic;
import com.theBombSquad.stratego.player.ai.setup.Strategy;
import com.theBombSquad.stratego.player.ai.setup.UnitPlacement;
import com.theBombSquad.stratego.player.ai.setup.Strategies.Defensive;
import com.theBombSquad.stratego.player.ai.setup.Strategies.Middle;
import com.theBombSquad.stratego.player.ai.setup.Strategies.Ofensive;

public class FlagTactic11 extends FlagTactic{
	
	public FlagTactic11(AISetup setup) {
		super(setup);
		proceed();
		addStrategies();
	}

	public FlagTactic11() {
		super();
		addStrategies();
	}
	public void addSetup(AISetup setup){
		super.setup=setup;
		proceed();
	}

	protected void addStrategies() {
		strategies = new ArrayList<Strategy>();
		strategies.add(new Defensive());
		strategies.add(new Ofensive());
		strategies.add(new Middle());
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
				{y-1, 0 , 1},
				{y-1, 1	, 2},
				{y-1, 2	, 1},
				{y	, 2	, 2},
				{y+1, 2	, 2},
				{y+2, 2	, 1},
				{y+2, 1	, 2},
				{y+2, 0 , 1}
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
		UnitPlacement toPut;
		for(int i = 0 ; i < setup.getWidth() ; i++ ){
			if(i <= 3 || i >= 6){
				System.out.println("IF!");
				possiblePlacements.add(new UnitPlacement(UnitType.FLAG, i, 3, 2));
			}
			else{
				System.out.println("IF@");
				possiblePlacements.add(new UnitPlacement(UnitType.FLAG, i, 3, 1));
			}
		}
		toPut = super.randomizeUnitPlacement();
		super.placeUnit(toPut);
		int x = toPut.getX();
		System.out.println("x= " + x);
		toPut.setUnitType(UnitType.BOMB);
		if(super.isFree(x, 2)){
			System.out.println("CHECK1");
			toPut.setX(x);
			toPut.setY(2);
			super.placeUnit(toPut);
		}
		if(super.isFree(x+1,3)){
			System.out.println("CHECK2");
			toPut.setX(x+1);
			toPut.setY(3);
			super.placeUnit(toPut);
		}
		if(super.isFree(x-1,3)&& super.hasAvailable(UnitType.BOMB)){
			System.out.println("CHECK3");
			toPut.setX(x-1);
			toPut.setY(3);
			super.placeUnit(toPut);
		}
		if(super.hasAvailable(UnitType.BOMB)){
			System.out.println("CHECK4");
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
