package com.theBombSquad.stratego.gameMechanics;

import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.player.ai.schrodingersBoard.SchrodingersBoard;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created 10/12/14
 */
@Data
@AllArgsConstructor
public class GameState {
	GameBoard board;
	List<Move> moves;
	int[] ownDefeated;
	int[] opponentDefeated;
	SchrodingersBoard schrodingersBoard;
}
