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

public class FlagTactic7 extends FlagTactic{

	public FlagTactic7(AISetup setup) {
		super(setup);
		proceed();
		addStrategies();
	}

	public FlagTactic7() {
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
		//strategies.add(new Ofensive());
		strategies.add(new Middle());
	}

private void proceed() {
		
		blockFlag();
		blockBombs();
		blockMarshal();
		blockBombs();
		blockColonel();	
	}

	private void blockFlag(){
		
		super.empty();
		//generate flag
		for(int i = 0; i < setup.getWidth(); i=i+2){
			possiblePlacements.add(new UnitPlacement(UnitType.FLAG, i, 3, 1));
		}
		UnitPlacement toPut =  randomizeUnitPlacement();
		super.placeUnit(toPut);
		//nr 8
		toPut.setUnitType(UnitType.COLONEL);
		toPut.setX(toPut.getX()+1);
		super.placeUnit(toPut);
		//2 times 4/5
		toPut.setUnitType(super.randomizeSL());
		toPut.setY(2);
		super.placeUnit(toPut);
		toPut.setUnitType(super.randomizeSL());
		toPut.setX(toPut.getX()-1);
		super.placeUnit(toPut);
		//2 bombs
		toPut.setUnitType(UnitType.BOMB);
		toPut.setY(1);
		super.placeUnit(toPut);
		toPut.setX(toPut.getX()+1);
		super.placeUnit(toPut);
	}
	
	private void blockBombs(){
		
		super.empty();
		//generate leftupper bomb
		for(int i = 0; i < setup.getWidth(); i=i+2){
			if(super.isFree(i, 1)){ 
				possiblePlacements.add(new UnitPlacement(UnitType.BOMB, i, 1, 1));
			}
		}
		UnitPlacement toPut = randomizeUnitPlacement();
		super.placeUnit(toPut);
		//rightupper bomb
		toPut.setX(toPut.getX()+1);
		super.placeUnit(toPut);
		//4/5 under bombs (1 for sure, second by chance)
		toPut.setUnitType(super.randomizeSL());
		toPut.setY(2);
		double random = Math.random();
		if(Math.random() < 0.5){
			toPut.setX(toPut.getX()-1);
			super.placeUnit(toPut);
			//place second one?
			if(random < 0.7){
				toPut.setUnitType(super.randomizeSL());
				toPut.setX(toPut.getX()+1);
			}
		}
		else{
			super.placeUnit(toPut);
			if(random < 0.6){
				toPut.setUnitType(super.randomizeSL());
				toPut.setX(toPut.getX()-1);
			}
		}	
	}
	
	private void blockMarshal(){
		
		//place marshal
		super.empty();
		for(int i = 0; i < setup.getWidth(); i++){
			possiblePlacements.add(new UnitPlacement(UnitType.MARSHAL, i, 1, 1));
		}
		UnitPlacement toPut = randomizeUnitPlacement();
		super.placeUnit(toPut);
		//place 9
		//if x = even spot, then marshal was placed on left side of block
		if(toPut.getX()%2 == 0){
			toPut.setX(toPut.getX()+1);
		}
		else{
			toPut.setX(toPut.getX()-1);
		}
		toPut.setUnitType(UnitType.GENERAL);
		super.placeUnit(toPut);
		//place spy
		double random = Math.random();
		toPut.setY(2);
		toPut.setUnitType(UnitType.SPY);
		if(toPut.getX()%2 == 0){
			if(random < 0.5){
				toPut.setX(toPut.getX());
			}else{
				toPut.setX(toPut.getX()+1);
			}
		}else{
			if(random < 0.5){
				toPut.setX(toPut.getX());
			}else{
				toPut.setX(toPut.getX()-1);
			}
		}
		super.placeUnit(toPut);
	}
	
	private void blockColonel() {
		
		super.empty();
		//place 8
		for(int i = 0; i < setup.getWidth(); i++){
			if(super.isFree(i, 1)){ 
				possiblePlacements.add(new UnitPlacement(UnitType.COLONEL, i, 1, 1));
			}
		}
		UnitPlacement toPut = randomizeUnitPlacement();
		super.placeUnit(toPut);
		//place 7
		toPut.setUnitType(UnitType.MAJOR);
		if(toPut.getX()%2 == 0){
			toPut.setX(toPut.getX()+1);
		}
		else{
			toPut.setX(toPut.getX()-1);
		}
		super.placeUnit(toPut);
		
		int pick = (int) (Math.random()*9);
		if(pick > 1){
			toPut.setUnitType(super.randomizeSL());
			toPut.setY(toPut.getY()+1);
			if(super.isFree(toPut.getX(), toPut.getY())){
				super.placeUnit(toPut);
			}
			else if(super.isFree(toPut.getX(), toPut.getY()+1)){
					toPut.setY(toPut.getY()+1);
					super.placeUnit(toPut);
				}
			else if(super.isFree(toPut.getX(), toPut.getY()-1)){
					toPut.setY(toPut.getY()-1);
					super.placeUnit(toPut);
				}
			else return;
		}
		if(pick >4){
			if(super.isFree(toPut.getX(), toPut.getY()+1)){
					toPut.setY(toPut.getY()+1);
					super.placeUnit(toPut);
				}
			else if(super.isFree(toPut.getX(), toPut.getY()-1)){
					toPut.setY(toPut.getY()-1);
					super.placeUnit(toPut);
				}
			else return;
		}
	}
}

