package com.theBombSquad.stratego.player.ai.schrodingersBoard;

import java.util.ArrayList;

import lombok.Getter;

import com.theBombSquad.stratego.StrategoConstants;
import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;

public class SchrodingersUnit {
	
	//Direct Reference to the Unit Type of this Object IF the Unit Type is actually known and set
	private UnitType knownUnit = null;
	
	//Index equal to Unit Rank (probabilities are not stored normalized)
	private float[] probabilities;
	private float probSum;
	@Getter
	private PlayerID owner;
	
	//Placeholder variables for when the unit is used as air or lake
	private boolean placeholder = false;
	private boolean airNotLake = false;
	
	/** Returns whether this Unit actually represents any Unit or just Air/Lake */
	public boolean isActualUnit(){
		return !placeholder;
	}
	
	public boolean isAir(){
		return placeholder && airNotLake;
	}
	
	public boolean isLake(){
		return placeholder && !airNotLake;
	}
	
	/** Clones this Unit */
	public SchrodingersUnit clone(){
		float[] probs = new float[probabilities.length];
		for(int c=0; c<probs.length; c++){
			probs[c] = probabilities[c];
		}
		return new SchrodingersUnit(knownUnit, probs, probSum, owner, placeholder, airNotLake);
	}
	
	/** Constructor used for cloning */
	private SchrodingersUnit(UnitType knownUnit, float[] probabilities, float probSum, PlayerID owner, boolean placeholder, boolean airNotLake){
		this.knownUnit = knownUnit;
		this.probabilities = probabilities;
		this.probSum = probSum;
		this.owner = owner;
		this.placeholder = placeholder;
		this.airNotLake = airNotLake;
	}
	
	/** Constructs All Empty Unit, No Chance of being anything (Air) */
	public SchrodingersUnit(boolean airNotLake){
		this.placeholder = true;
		this.airNotLake = airNotLake;
		this.owner = StrategoConstants.PlayerID.NEMO;
		this.probabilities = new float[12];
		for(int c=0; c<this.probabilities.length; c++){
			this.probabilities[c] = 0;
		}
	}
	
	/** Constructs new Schrodinger Unit, probabilities' indexes = Units' ranks, array has to be 12 long */
	public SchrodingersUnit(PlayerID player, float[] probabilities){
		this.owner = player;
		this.probabilities = probabilities;
		calcProbSum();
	}
	
	/** Constructs new Schrodingers Unit with known UnitType */
	public SchrodingersUnit(PlayerID player, UnitType unit){
		this.owner = player;
		this.knownUnit = unit;
		this.probabilities = new float[12];
		for(int c=0; c<this.probabilities.length; c++){
			if(c!=unit.getRank()){
				this.probabilities[c] = 0;
			}
			else{
				this.probabilities[c] = 1f;
			}
		}
		calcProbSum();
	}
	
	
	/** Sets this Schrodinger's Unit to one Certain Unit */
	public void setKnown(UnitType unit){
		this.knownUnit = unit;
		for(int c=0; c<probabilities.length; c++){
			probabilities[c] = 0;
		}
		probabilities[unit.getRank()] = 1f;
		probSum = 1f;
	}
	
	public boolean unitIsKnown(){
		return knownUnit!=null;
	}
	
	public UnitType getKnownUnit(){
		return knownUnit;
	}
	
	/** Returns Normalized Probability for this Unit being the given Unit Type */
	public float getProbabilityFor(UnitType unit){
		return probabilities[unit.getRank()]/probSum;
	}
	
	//Recalcs Prob Sum (should be called after each manipulation of probabilities)
	private void calcProbSum(){
		float help = 0;
		for(int c=0; c<probabilities.length; c++){
			help += probabilities[c];
		}
		this.probSum = help;
	}
	
	
	/** Removes the possibility for the Unit to be of given unit type */
	public void removePossibilityFor(UnitType unit){
		if(!unitIsKnown()){
			this.probabilities[unit.getRank()] = 0;
			//Check if there is only one type of Unit this Unit can still be
			int possibilities = 0;
			int onlyType = -1;
			for(int c=0; c<probabilities.length; c++){
				if(probabilities[c]>0){
					possibilities++;
					onlyType = c;
				}
			}
			if(possibilities==1){
				setKnown(Unit.getUnitTypeOfRank(onlyType));
			}
			else{
				calcProbSum();
			}
		}
		else{
			//TODO: Throw something
			System.out.println("Cannot Remove The Possibility For Unit To Be Of Type "+unit+", Only Possibility Left.");
		}
	}
	
	public void setProbabilityForUnitType(UnitType unit, float probability){
		probabilities[unit.getRank()] = probability;
		calcProbSum();
	}
	
	/** Returns the normalized probability for either of the given Unit Types to be the Type of this Unit */
	public float getProbabilityFor(UnitType ... units){
		float sum = 0;
		for(UnitType unit : units){
			sum += probabilities[unit.getRank()];
		}
		return sum/probSum;
	}
	
	/** Updates the probability of the unit type according to the death of another unit that may have been this type and the size of all units with this units colour (before the death of that unit) */
	public void deathUpdate(UnitType unit, float probOfDeath, float armySize){
		probabilities[unit.getRank()] = ((probabilities[unit.getRank()]*armySize)-probSum*probOfDeath)/(armySize-1);
		calcProbSum();
	}
	
	/** Updates Probability of given Unit Type in case a unit should just have been revealed to definitely be this Unit Type */
	public void revealedUpdate(UnitType unit, float armySize){
		probabilities[unit.getRank()] = probabilities[unit.getRank()]-(probSum/armySize);
		calcProbSum();
	}
	
	/** Updates itself and the entire board accordingly, should only be performed on opponents units if this Unit is supposed to lose, returns false if it is impossible for it to lose */
	public boolean combatUpdateLose(SchrodingersUnit known, SchrodingersBoard board, boolean offensiveNotDefensive){
		boolean unitWasKnown = this.unitIsKnown();
		ArrayList<UnitType> weakerThanKnown = new ArrayList<UnitType>();
		ArrayList<UnitType> not = new ArrayList<UnitType>();
		boolean possible = false;
		for(UnitType unitType : new UnitType[]{Unit.UnitType.FLAG, Unit.UnitType.BOMB, Unit.UnitType.SPY, Unit.UnitType.SCOUT, Unit.UnitType.SAPPER, Unit.UnitType.SERGEANT, Unit.UnitType.LIEUTENANT, Unit.UnitType.CAPTAIN, Unit.UnitType.MAJOR, Unit.UnitType.COLONEL, Unit.UnitType.GENERAL, Unit.UnitType.MARSHAL}){
			if(known.getKnownUnit().getRank()>unitType.getRank() && !(!offensiveNotDefensive && known.getKnownUnit().getRank()==Unit.UnitType.SPY.getRank() && unitType.getRank()==Unit.UnitType.MARSHAL.getRank())){
				weakerThanKnown.add(unitType);
				if(this.probabilities[unitType.getRank()]>0){
					possible = true;
				}
			}
			else{
				not.add(unitType);
			}
		}
		if(!possible){
			return false;
		}
		else{
			for(int c=0; c<not.size(); c++){
				this.probabilities[not.get(c).getRank()] = 0;
			}
			this.calcProbSum();
			if(this.unitIsKnown() && !unitWasKnown){
				//Revealed Update
				for(int cy=0; cy<board.getHeight(); cy++){
					for(int cx=0; cx<board.getWidth(); cx++){
						SchrodingersUnit u = board.getBoard()[cy][cx];
						if(u.isActualUnit() && u.owner==this.owner){
							u.revealedUpdate(this.knownUnit, board.getOpponentArmySize());
						}
					}
				}
			}
			//Update Death
			for(int cy=0; cy<board.getHeight(); cy++){
				for(int cx=0; cx<board.getWidth(); cx++){
					SchrodingersUnit u = board.getBoard()[cy][cx];
					if(u.isActualUnit() && u.owner==this.owner){
						for(int c=0; c<this.probabilities.length; c++){
							u.deathUpdate(Unit.getUnitTypeOfRank(c), this.probabilities[c], board.getOpponentArmySize());
						}
					}
				}
			}
			return true;
		}
	}
	
	
	/** Updates itself and the entire board accordingly, should only be performed on opponents units if this Unit is supposed to draw, returns false if it is impossible for it to do so */
	public boolean combatUpdateDraw(SchrodingersUnit known, SchrodingersBoard board, boolean offensiveNotDefensive){
		boolean unitWasKnown = this.unitIsKnown();
		boolean possible = this.probabilities[known.getKnownUnit().getRank()]>0;
		if(!possible){
			return false;
		}
		else{
			for(int c=0; c<this.probabilities.length; c++){
				if(c!=known.getKnownUnit().getRank()){
					this.probabilities[c] = 0;
				}
			}
			this.calcProbSum();
			if(this.unitIsKnown() && !unitWasKnown){
				//Revealed Update
				for(int cy=0; cy<board.getHeight(); cy++){
					for(int cx=0; cx<board.getWidth(); cx++){
						SchrodingersUnit u = board.getBoard()[cy][cx];
						if(u.isActualUnit() && u.owner==this.owner){
							u.revealedUpdate(this.knownUnit, board.getOpponentArmySize());
						}
					}
				}
			}
			//Update Death
			for(int cy=0; cy<board.getHeight(); cy++){
				for(int cx=0; cx<board.getWidth(); cx++){
					SchrodingersUnit u = board.getBoard()[cy][cx];
					if(u.isActualUnit() && u.owner==this.owner){
						for(int c=0; c<this.probabilities.length; c++){
							u.deathUpdate(Unit.getUnitTypeOfRank(c), this.probabilities[c], board.getOpponentArmySize());
						}
					}
				}
			}
			return true;
		}
	}
	
	
	/** Updates itself and the entire board accordingly, should only be performed on opponents units if this Unit is supposed to draw, returns false if it is impossible for it to do so */
	public boolean combatUpdateWin(SchrodingersUnit known, SchrodingersBoard board, boolean offensiveNotDefensive){
		boolean unitWasKnown = this.unitIsKnown();
		ArrayList<UnitType> strongerThanKnown = new ArrayList<UnitType>();
		ArrayList<UnitType> not = new ArrayList<UnitType>();
		boolean possible = false;
		for(UnitType unitType : new UnitType[]{Unit.UnitType.FLAG, Unit.UnitType.BOMB, Unit.UnitType.SPY, Unit.UnitType.SCOUT, Unit.UnitType.SAPPER, Unit.UnitType.SERGEANT, Unit.UnitType.LIEUTENANT, Unit.UnitType.CAPTAIN, Unit.UnitType.MAJOR, Unit.UnitType.COLONEL, Unit.UnitType.GENERAL, Unit.UnitType.MARSHAL}){
			known.getKnownUnit().getRank();
			if((known.getKnownUnit().getRank()<unitType.getRank() || (offensiveNotDefensive && known.getKnownUnit().getRank()==Unit.UnitType.MARSHAL.getRank() && unitType.getRank()==Unit.UnitType.SPY.getRank()) || (known.getKnownUnit().getRank()==Unit.UnitType.BOMB.getRank() && unitType.getRank()==Unit.UnitType.SAPPER.getRank())) && !(unitType.getRank()==Unit.UnitType.BOMB.getRank() && known.getKnownUnit().getRank()==Unit.UnitType.SAPPER.getRank())){
				strongerThanKnown.add(unitType);
				if(this.probabilities[unitType.getRank()]>0){
					possible = true;
				}
			}
			else{
				not.add(unitType);
			}
		}
		if(!possible){
			return false;
		}
		else{
			for(int c=0; c<not.size(); c++){
				this.probabilities[not.get(c).getRank()] = 0;
			}
			this.calcProbSum();
			if(this.unitIsKnown() && !unitWasKnown){
				//Revealed Update
				for(int cy=0; cy<board.getHeight(); cy++){
					for(int cx=0; cx<board.getWidth(); cx++){
						SchrodingersUnit u = board.getBoard()[cy][cx];
						if(u.isActualUnit() && u.owner==this.owner){
							u.revealedUpdate(this.knownUnit, board.getOpponentArmySize());
						}
					}
				}
			}
			return true;
		}
	}
	

}
