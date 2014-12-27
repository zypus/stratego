package com.theBombSquad.stratego.player.ai.players.planner;

import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Encounter;
import com.theBombSquad.stratego.gameMechanics.board.Encounter.CombatResult;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;

/** Plan Concerned With Attacking Enemy Unit */
public class PlanAttackAdjacent implements Plan{

	@Override
	public float evaluateMove(GameView view, Move move) {
		GameBoard board = view.getCurrentState();
		Unit self = board.getUnit(move.getFromX(), move.getFromY());
		Unit target = board.getUnit(move.getToX(), move.getToY());
		//If Not Enemy Unit, Plan Won't Judge
		if(target.isAir()){
			return 0;
		}
		//Enemy Is Unit
		else{
			//Enemy Is Revealed!
			if(target.wasRevealed(view.getCurrentTurn())){
				CombatResult result = Encounter.resolveFight(self.getType(), target.getType());
				//Attack Successful
				if(result.equals(CombatResult.VICTORIOUS_ATTACK)){
					//Value Of Opponent
					float foeValue = TheQueen.getUnitValue(target.getType())*2;
					return foeValue;
				}
				//Attack Not Successful (Attacking A Known Unit And Failing Is NEVER A Good Idea!)
				else if(result.equals(CombatResult.VICTORIOUS_DEFENSE)){
					float selfValue = TheQueen.getUnitValue(self.getType());
					return (selfValue * -10);
				}
			}
			//Enemy Is Not Revealed
			else{
				float revelationBonus = calcRevBonus(view);
				//Target Has Not Been Moved Yet (May Be A Bomb)
				if(!target.wasMoved(view.getCurrentTurn())){
					//Calculate Expected Profit (Reduce As We Should Be Somewhat Pessimistic When Dealing With Unknowns)
					float expectedUnmovedValue = calculateExpectedUnmovedValue(view, self);
					expectedUnmovedValue = expectedUnmovedValue*0.95f;
					return expectedUnmovedValue + revelationBonus;
				}
				//Target Has Already Moved (Can't Be A Bomb)
				else{
					//Calculate Expected Profit (Reduce As We Should Be Somewhat Pessimistic When Dealing With Unknowns)
					float expectedMovedValue = calculateExpectedMovedValue(view, self);
					expectedMovedValue = expectedMovedValue*0.8f;
					return expectedMovedValue + revelationBonus;
				}
			}
		}
		return 0;
	}
	
	/** Calc Bonus That Should Be Handed Out For Uncovering Opponent Piece */
	private float calcRevBonus(GameView view){
		//See How Many Units Of Opponent Of Certain Type Have Been Revealed
		int opponentHiddenArmySize = 0;
		int[] revealedOfType = new int[12];
		GameBoard board = view.getCurrentState();
		for(int cy=0; cy<board.getHeight(); cy++){
			for(int cx=0; cx<board.getWidth(); cx++){
				Unit unit = board.getUnit(cx, cy);
				if(unit.getOwner().equals(view.getOpponentID())){
					if(unit.wasRevealed(view.getCurrentTurn())){
						revealedOfType[unit.getType().getRank()]++;
					}
					else{
						opponentHiddenArmySize++;
					}
				}
			}
		}
		//Default To Scout, Weakest UnitType As Strongest Hidden Alive Opponent Unit
		UnitType strongestAliveHiddenUnit = Unit.UnitType.SCOUT;
		for(int c=10; c>=0; c--){
			if(board.getAliveUnits(Unit.getUnitTypeOfRank(c), view.getOpponentID())>0){
				if(revealedOfType[c]<Unit.getUnitTypeOfRank(c).getQuantity()){
					strongestAliveHiddenUnit = Unit.getUnitTypeOfRank(c);
				}
			}
		}
		//Calculate Value Of Maybe Revealing This Unit
		return TheQueen.getUnitValue(strongestAliveHiddenUnit)/opponentHiddenArmySize;
	}
	
	/** Calculates The Expected Profit For Attacking An Unmoved Unit With This Unit */
	private float calculateExpectedUnmovedValue(GameView view, Unit self){
		//See How Many Units Of Opponent Of Certain Type Have Been Revealed
		int opponentHiddenArmySize = 0;
		int[] revealedOfType = new int[12];
		GameBoard board = view.getCurrentState();
		for(int cy=0; cy<board.getHeight(); cy++){
			for(int cx=0; cx<board.getWidth(); cx++){
				Unit unit = board.getUnit(cx, cy);
				if(unit.getOwner().equals(view.getOpponentID())){
					if(unit.wasRevealed(view.getCurrentTurn())){
						revealedOfType[unit.getType().getRank()]++;
					}
				}
			}
		}
		//How Many Of Units Of Certain UnitType Of opponent Remain Still On The Board, Hidden
		int[] remainingHidden = new int[12];
		for(int c=0; c<12; c++){
			remainingHidden[c] = view.getCurrentState().getAliveUnits(Unit.getUnitTypeOfRank(c), view.getOpponentID())-revealedOfType[c];
			opponentHiddenArmySize += remainingHidden[c];
		}
		float winPoints = 0;
		float losePoints = 0;
		for(int c=0; c<12; c++){
			CombatResult result = Encounter.resolveFight(self.getType(), Unit.getUnitTypeOfRank(c));
			//Win Possibs
			if(result.equals(CombatResult.VICTORIOUS_ATTACK)){
				float value = TheQueen.getUnitValue(Unit.getUnitTypeOfRank(c)) * remainingHidden[c];
				//Weighing Possibility Of Finding A Flag Lower
				if(Unit.getUnitTypeOfRank(c).equals(Unit.UnitType.FLAG)){
					value = value/22;
				}
				winPoints += value;
			}
			//Lose Possibs
			else if(result.equals(CombatResult.VICTORIOUS_DEFENSE)){
				float value = TheQueen.getUnitValue(self.getType()) * remainingHidden[c];
				//Weighing Possibility Of Finding A Bomb Higher
				if(Unit.getUnitTypeOfRank(c).equals(Unit.UnitType.BOMB)){
					value = value*2;
				}
				losePoints += value;
			}
			//Mutual Defeat
			else{
				//losePoints += TheQueen.getUnitValue(Unit.getUnitTypeOfRank(c)) * remainingHidden[c] * 0.8;
			}
		}
		return (winPoints-losePoints);
	}
	
	/** Calculates The Expected Profit For Attacking A Moved Unit With This Unit */
	private float calculateExpectedMovedValue(GameView view, Unit self){
		//See How Many Units Of Opponent Of Certain Type Have Been Revealed
		int opponentHiddenArmySize = 0;
		int[] revealedOfType = new int[12];
		GameBoard board = view.getCurrentState();
		for(int cy=0; cy<board.getHeight(); cy++){
			for(int cx=0; cx<board.getWidth(); cx++){
				Unit unit = board.getUnit(cx, cy);
				if(unit.getOwner().equals(view.getOpponentID())){
					if(unit.wasRevealed(view.getCurrentTurn())){
						revealedOfType[unit.getType().getRank()]++;
					}
				}
			}
		}
		//How Many Of Units Of Certain UnitType Of opponent Remain Still On The Board, Hidden
		int[] remainingHidden = new int[12];
		for(int c=0; c<12; c++){
			if(Unit.getUnitTypeOfRank(c).equals(Unit.UnitType.BOMB) || Unit.getUnitTypeOfRank(c).equals(Unit.UnitType.FLAG)){
				remainingHidden[c] = 0;
			}
			else{
				remainingHidden[c] = view.getCurrentState().getAliveUnits(Unit.getUnitTypeOfRank(c), view.getOpponentID())-revealedOfType[c];
			}
			opponentHiddenArmySize += remainingHidden[c];
		}
		float winPoints = 0;
		float losePoints = 0;
		for(int c=0; c<12; c++){
			CombatResult result = Encounter.resolveFight(self.getType(), Unit.getUnitTypeOfRank(c));
			//Win Possibs
			if(result.equals(CombatResult.VICTORIOUS_ATTACK)){
				float value = TheQueen.getUnitValue(Unit.getUnitTypeOfRank(c)) * remainingHidden[c];
				winPoints += value;
			}
			//Lose Possibs
			else if(result.equals(CombatResult.VICTORIOUS_DEFENSE)){
				float value = TheQueen.getUnitValue(self.getType()) * remainingHidden[c];
				losePoints += value;
			}
			//Mutual Defeat
			else{
				//losePoints += TheQueen.getUnitValue(Unit.getUnitTypeOfRank(c)) * remainingHidden[c] * 0.8;
			}
		}
		return (winPoints-losePoints);
	}

	@Override
	public void postMoveUpdate(GameView view) {
		//Nothing To Do here, Just Chill :)
	}

}
