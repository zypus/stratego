package com.theBombSquad.stratego.player.ai.evaluationFunctions;

import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;

public class RuleEvaluationFunction implements EvaluationFunctionX{
	
	public float evaluate(GameView gameView, Move move){
		
		int fromX = move.getFromX();
		int toX = move.getToX();
		int fromY = move.getFromY();
		int toY = move.getToY();
		boolean ownIsRevealed = !gameView.getUnit(fromX, fromY).isUnknown();
		//is air known/unknown or doesn't it have that?
		
		
		float eval = 0;
		
		/**
		 * v+figuring out identity
		 * v+winning by attacking known lower piece
		 * v+getting rid of opponent bombs
		 * v-attacking known higher ranked piece 
		 * -killing known lower ranked unit with unknown or known higher ranked unit adjacent
		 * v+killing marshal by spy
		 * v+- attacking unknown piece (depending on own rank etc, except for scout)
		 * -leaving flag unattended with opponent near
		 * v-revealing high ranked pieces (small)
		 * 
		 */
		
		Unit fromUnit = gameView.getUnit(fromX, fromY);
		Unit toUnit = gameView.getUnit(toX, toY);
		
		//Since it'll get only possible moves, only option is to air or oppUnit
		//Air
		if(toUnit.isAir()){
			//going up slightly better than going down in beginning of game
			if(fromY>toY&&gameView.getCurrentTurn()<200){
				eval = eval + 15;
			}
			if(fromY==toY&&gameView.getCurrentTurn()<200){
				eval = eval + 10;
			}
		}
		
		//is Unit of opponent
		else{
			boolean oppIsRevealed = !gameView.getUnit(toX, toY).isUnknown();
			int ownRank = fromUnit.getType().getRank();			
			//if known
			if(oppIsRevealed){
				int oppRank =toUnit.getType().getRank(); 
				//known and opp is lower, bomb is rank 11, so cannot attack when known
				if(ownRank>oppRank){
					if(ownIsRevealed){
						eval = eval + 150;
					}
					else{
						eval = (float) eval + 25 + (100/(5*ownRank));
					}
				}
				//known but opp is higher
				if(ownRank<=oppRank){
					//known, opp is bomb, own is miner
					if(ownRank == 3 && oppRank == 11){
						eval = eval + 80;
					}
					if(ownRank == 1 && oppRank == 10){
						eval = eval+10000;
					}
					else eval = eval - 10000;
				}
			}
			//if unknown
			else{
				if(ownRank ==2){
					eval = eval + 40;
				}
				else if(ownRank ==3){
					eval = eval + 10;
				}
				else if(ownRank ==4){
					eval = eval + 30;
				}
				else if(ownRank ==5){
					eval = eval + 30;
				}
				else if(ownRank ==6){
					eval = eval + 25;
				}
				else if(ownRank ==7){
					eval = eval + 22;
				}
				else if(ownRank ==8){
					eval = eval + 20;
				}
				else if(ownRank ==9){
					eval = eval + 15;
				}
				else if(ownRank ==10){
					eval = eval + 15;
				}
			}
		}
		
		//check always
		//leaving flag when oppUnit is close
		///How to call flag?
		//if(gameView.)
		
		return eval;
	}

	@Override
	public float evaluate(GameBoard state, PlayerID player) {
		// TODO Auto-generated method stub
		return 0;
	}

}
