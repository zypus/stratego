package com.theBombSquad.stratego.player.ai.schrodingersBoard;

import com.theBombSquad.stratego.StrategoConstants;
import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.ai.evaluationFunctions.EvaluationFunctionX;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
/** This Class is supposed to simplify and abstract board states and board state manipulation for unknown units */
public class SchrodingersBoard {

	private SchrodingersUnit[][] board;
	private int opponentArmySize;
	private int ownArmySize;
	private GameView view;

	/** Probability Of This Board Occurring Given Previous Board And Move */
	@Getter
	@Setter
	private float relativeProbability;

	public int getHeight(){
		return board.length;
	}

	public int getWidth(){
		return board[0].length;
	}

	/** Constructs New Schrodingers Box Which Is Dependent On A Single (The Current) State */
	public SchrodingersBoard(GameView view){
		this.view = view;
		GameBoard gameBoard = view.getCurrentState();
		int[] stillUnknownUnits = new int[12];
		int opponentArmySize = 0;
		int ownArmySize = 0;
		this.relativeProbability = 1f;
		for(int c=0; c<12; c++){
			stillUnknownUnits[c] = Unit.getUnitTypeOfRank(c).getQuantity()-view.getNumberOfOpponentDefeatedUnits(Unit.getUnitTypeOfRank(c));
		}
		for(int cy=0; cy<gameBoard.getHeight(); cy++){
			for(int cx=0; cx<gameBoard.getWidth(); cx++){
				Unit u = gameBoard.getUnit(cx, cy);
				if(!u.isLake() && !u.isAir()){
					if(u.getOwner()==view.getOpponentID()){
						opponentArmySize++;
						if(!u.isUnknown()){
							stillUnknownUnits[u.getType().getRank()]--;
						}
					}
					else if(u.getOwner()==view.getPlayerID()){
						ownArmySize++;
					}
				}
			}
		}
		this.ownArmySize = ownArmySize;
		this.opponentArmySize = opponentArmySize;
		this.board = new SchrodingersUnit[gameBoard.getHeight()][gameBoard.getWidth()];
		for(int cy=0; cy<board.length; cy++){
			for(int cx=0; cx<board[cy].length; cx++){
				Unit u = gameBoard.getUnit(cx, cy);
				if(!u.isAir() && !u.isLake()){
					if(u.isUnknown()){
						float[] probs = new float[12];
						for(int c=0; c<probs.length; c++){
							probs[c] = stillUnknownUnits[c]/((float)opponentArmySize);
						}
						board[cy][cx] = new SchrodingersUnit(view.getOpponentID(), probs);
						for(int c=0; c<12; c++){
							board[cy][cx].getUnitsStillPossibleToBe()[c] = Unit.getUnitTypeOfRank(c).getQuantity()-view.getNumberOfOpponentDefeatedUnits(Unit.getUnitTypeOfRank(c));
						}
						for(Move move : view.getMoves()){
							if(move.hasEncounter()){
								if(move.getEncounter().getAttackingUnit().getId()==u.getId()){
									board[cy][cx].setKnown(move.getEncounter().getAttackingUnit().getType());
									board[cy][cx].setRevealedToOpponent(true);
								}
								else if(move.getEncounter().getDefendingUnit().getId()==u.getId()){
									board[cy][cx].setKnown(move.getEncounter().getDefendingUnit().getType());
									board[cy][cx].setRevealedToOpponent(true);
								}
							}
							if(move.getMovedUnit().getId()==u.getId()){
								board[cy][cx].removePossibilityFor(Unit.UnitType.BOMB);
								board[cy][cx].removePossibilityFor(Unit.UnitType.FLAG);
							}
						}
					}
					else{
						board[cy][cx] = new SchrodingersUnit(u.getOwner(), u.getType());
					}
					if (u.wasRevealed(view.getCurrentTurn())) {
						board[cy][cx].setRevealed(true);
					}
				}
				else{
					//Create Empty
					if(u.isAir()){
						board[cy][cx] = new SchrodingersUnit(true);
					}
					else{
						board[cy][cx] = new SchrodingersUnit(false);
					}
				}
			}
		}
	}


	/** Constructor Used for cloning */
	public SchrodingersBoard(SchrodingersUnit[][] board, int ownArmySize, int opponentArmySize, GameView view, float relativeProbability){
		this.board = board;
		this.ownArmySize = ownArmySize;
		this.opponentArmySize = opponentArmySize;
		this.view = view;
		this.relativeProbability = relativeProbability;
	}

	public SchrodingersBoard clone(){
		SchrodingersUnit[][] cBoard = new SchrodingersUnit[board.length][board[0].length];
		for(int cy=0; cy<cBoard.length; cy++){
			for(int cx=0; cx<cBoard[cy].length; cx++){
				cBoard[cy][cx] = board[cy][cx].clone();
			}
		}
		return new SchrodingersBoard(cBoard, ownArmySize, opponentArmySize, view, relativeProbability);
	}
	/** Creates a new Board in which unit from origin is now at destination - Also executes Encounters! */
	/** Creates a new Board in which unit from origin is now at destination - Also executes Encounters! */
	public ArrayList<SchrodingersBoard> moveUnit(int originX, int originY, int destX, int destY){
		ArrayList<SchrodingersBoard> list = new ArrayList<SchrodingersBoard>();
		SchrodingersBoard placeHolder = clone();
		placeHolder.board[originY][originX].removePossibilityFor(Unit.UnitType.BOMB);
		placeHolder.board[originY][originX].removePossibilityFor(Unit.UnitType.FLAG);
		//Check For whether Unit HAS TO BE Scout
		if(Math.abs(originX-destX)>=2 || Math.abs(originY-destY)>=2){
			float probs = placeHolder.board[originY][originX].getProbabilityFor(Unit.UnitType.SCOUT);
			placeHolder.board[originY][originX].setKnown(Unit.UnitType.SCOUT);
			placeHolder.board[originY][originX].setRevealed(true);
//			placeHolder.setRelativeProbability(probs);
			placeHolder.setRelativeProbability(1);
		}
		//Moves onto empty square
		if(getBoard()[destY][destX].isAir()){
			SchrodingersBoard newBoard = placeHolder.clone();
			newBoard.getBoard()[destY][destX] = newBoard.getBoard()[originY][originX];
			newBoard.getBoard()[originY][originX] = new SchrodingersUnit(true);
			newBoard.setRelativeProbability(1);
			list.add(newBoard);
		}
		//Destination is not empty - i.e. Encounter!
		else{
			SchrodingersUnit dest = getBoard()[destY][destX];
			SchrodingersUnit orig = getBoard()[originY][originX];
			SchrodingersUnit analyzed = null;
			SchrodingersUnit helper = null;
			boolean offensiveNotDefensive = orig.getOwner()==view.getOpponentID();
			if(orig.getOwner()==view.getOpponentID()){
				analyzed = orig;
				helper = dest;
				offensiveNotDefensive = true;
			}
			else{
				analyzed = dest;
				helper = orig;
				offensiveNotDefensive = true;
			}
			//Win With Analyzed
			SchrodingersBoard win = placeHolder.clone();
			SchrodingersUnit an = analyzed.clone();
			if(an.combatUpdateWin(helper.clone(), win, offensiveNotDefensive)){
				SchrodingersBoard newBoard = win;
				newBoard.getBoard()[destY][destX] = an;
				newBoard.board[destY][destX].setRevealed(true);
				newBoard.getBoard()[originY][originX] = new SchrodingersUnit(true);
				newBoard.ownArmySize = newBoard.ownArmySize-1;
				list.add(newBoard);
			}
			//Draw With Analyzed
			SchrodingersBoard draw = placeHolder.clone();
			an = analyzed.clone();
			if(an.combatUpdateDraw(helper.clone(), draw, offensiveNotDefensive)){
				SchrodingersBoard newBoard = draw;
				newBoard.getBoard()[destY][destX] = new SchrodingersUnit(true);
				newBoard.getBoard()[originY][originX] = new SchrodingersUnit(true);
				newBoard.ownArmySize = newBoard.ownArmySize-1;
				newBoard.opponentArmySize = newBoard.opponentArmySize-1;
				list.add(newBoard);
			}
			//Lose With Analyzed
			SchrodingersBoard lose = placeHolder.clone();
			SchrodingersUnit u = helper.clone();
			an = analyzed.clone();
			if(an.combatUpdateLose(u, lose, offensiveNotDefensive)){
				SchrodingersBoard newBoard = lose;
				if(offensiveNotDefensive){
					newBoard.getBoard()[originY][originX] = new SchrodingersUnit(true);
				}
				else{
					newBoard.getBoard()[originY][originX] = new SchrodingersUnit(true);
					newBoard.getBoard()[destY][destX] = u;
				}
				newBoard.opponentArmySize = newBoard.opponentArmySize-1;
				list.add(newBoard);
			}
		}
		if(list.size()==0){
			SchrodingersBoard newBoard = placeHolder.clone();
			list.add(newBoard);
			System.out.println("Void Move ... Returned Same Board Instead");
		}
		return list;
	}

	/** Generates All Possible Moves That Can Follow This Board Given Player */
	public List<Move> generateAllMoves(PlayerID player){
		PlayerID opp = player.getOpponent();
		ArrayList<Move> list = new ArrayList<Move>();
		for(int cy=0; cy<board.length; cy++){
			for(int cx=0; cx<board[cy].length; cx++){
				SchrodingersUnit u = board[cy][cx];
				if(u.isActualUnit()){
					if(u.getOwner()==player && !u.getOwner().equals(opp) && !u.getOwner().equals(StrategoConstants.PlayerID.NEMO)){
						boolean canMove = false;
						if(u.getKnownUnit()!=null && (u.getKnownUnit().getRank()==Unit.UnitType.BOMB.getRank() || u.getKnownUnit().getRank()==Unit.UnitType.FLAG.getRank())){
							canMove = false;
						}
						else{
							for(int c=0; c<12; c++){
								//Goes Through All Units Not Flag (0) And Not Bomb (11)
								if(u.getProbabilityFor(Unit.getUnitTypeOfRank(c))>0){
									if(!(Unit.getUnitTypeOfRank(c)==Unit.UnitType.BOMB || Unit.getUnitTypeOfRank(c)==Unit.UnitType.FLAG)){
										canMove = true;
									}
								}
							}
						}
						if(canMove){
							int posX = -1;
							int posY = -1;
							//Up
							posX = cx;
							posY = cy;
							while(posY>=0+1){
								posY--;
								if(board[posY][posX].isAir()){
									Move move = new Move(cx, cy, posX, posY);
									if (view.validateMove(move)) {
										list.add(move);
									}
								}
								else if(board[posY][posX].isActualUnit() && board[posY][posX].getOwner().equals(opp)){
									Move move = new Move(cx, cy, posX, posY);
									if (view.validateMove(move)) {
										list.add(move);
									}
									break;
								}
								else{
									break;
								}
								if(!(board[cy][cx].getProbabilityFor(Unit.UnitType.SCOUT)>0)){
									break;
								}
							}
							//Right
							posX = cx;
							posY = cy;
							while(posX<board[cy].length-1){
								posX++;
								if(board[posY][posX].isAir()){
									Move move = new Move(cx, cy, posX, posY);
									if (view.validateMove(move)) {
										list.add(move);
									}
								}
								else if(board[posY][posX].isActualUnit() && board[posY][posX].getOwner().equals(opp)){
									Move move = new Move(cx, cy, posX, posY);
									if (view.validateMove(move)) {
										list.add(move);
									}
									break;
								}
								else{
									break;
								}
								if(!(board[cy][cx].getProbabilityFor(Unit.UnitType.SCOUT)>0)){
									break;
								}
							}
							//Down
							posX = cx;
							posY = cy;
							while(posY<board.length-1){
								posY++;
								if(board[posY][posX].isAir()){
									Move move = new Move(cx, cy, posX, posY);
									if (view.validateMove(move)) {
										list.add(move);
									}
								}
								else if(board[posY][posX].isActualUnit() && board[posY][posX].getOwner().equals(opp)){
									Move move = new Move(cx, cy, posX, posY);
									if (view.validateMove(move)) {
										list.add(move);
									}
									break;
								}
								else{
									break;
								}
								if(!(board[cy][cx].getProbabilityFor(Unit.UnitType.SCOUT)>0)){
									break;
								}
							}
							//Left
							posX = cx;
							posY = cy;
							while(posX>=0+1){
								posX--;
								if(board[posY][posX].isAir()){
									Move move = new Move(cx, cy, posX, posY);
									if (view.validateMove(move)) {
										list.add(move);
									}
								}
								else if(board[posY][posX].isActualUnit() && board[posY][posX].getOwner().equals(opp)){
									Move move = new Move(cx, cy, posX, posY);
									if (view.validateMove(move)) {
										list.add(move);
									}
									break;
								}
								else{
									break;
								}
								if(!(board[cy][cx].getProbabilityFor(Unit.UnitType.SCOUT)>0)){
									break;
								}
							}
						}
					}
				}
			}
		}
		return list;
	}

	/** Generates List Of Schrodingers Boards based upon move (Move is assumed to be legal) */
	public List<SchrodingersBoard> generateFromMove(Move move){
		return moveUnit(move.getFromX(), move.getFromY(), move.getToX(), move.getToY());
	}

	/** Evaluates This Board */
	public float evaluate(EvaluationFunctionX eval, PlayerID player){
		return 0;//eval.evaluate(gameView, move)
		//return eval.evaluate(this, player);
	}

	public SchrodingersUnit getUnit(int x, int y) {
		return board[y][x];
	}

}
