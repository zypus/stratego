package com.theBombSquad.stratego.player.ai.setup;

import lombok.Getter;
import lombok.Setter;

import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
@Getter
@Setter
public class UnitPlacement {
	public UnitPlacement(UnitType unitType, int i, int j, int weight) {
		this.unitType=unitType;
		this.x=i;
		this.y=j;
		this.weight=weight;
	}
	private UnitType unitType;
	private int x;
	private	int y;
	private int weight;
	
}
