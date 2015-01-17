package com.theBombSquad.stratego.player.ai;

import com.theBombSquad.stratego.gameMechanics.board.Unit;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created 13/01/15
 */
public class AdjacencyTables {

	public static enum Direction {
		UP,
		SIDE,
		DOWN
	}

	public static final float[][] behindTable = new float[][]{
		//				   F	 B	   Sp    Sc	   Sa	 Sg	   L	 Ca	  Maj    Co	   G	Mar
		/* Flag 	  */{ 0.0f, 5.6f, 0.5f, 0.9f, 0.8f, 1.1f, 0.6f, 0.6f, 0.6f, 0.5f, 0.2f, 0.4f },
		/* Bomb 	  */{ 3.4f, 0.6f, 1.3f, 1.0f, 0.9f, 1.3f, 1.1f, 1.1f, 1.1f, 0.8f, 0.5f, 0.4f },
		/* Spy		  */{ 0.3f, 0.9f, 0.0f, 0.9f, 0.9f, 0.4f, 0.7f, 0.7f, 0.7f, 2.7f, 2.3f, 1.1f },
		/* Scout 	  */{ 0.1f, 0.5f, 1.0f, 1.0f, 0.8f, 0.7f, 0.8f, 0.8f, 0.8f, 1.2f, 1.1f, 1.1f },
		/* Sapper 	  */{ 0.4f, 0.6f, 1.0f, 1.5f, 1.0f, 0.8f, 1.0f, 1.0f, 1.0f, 1.1f, 1.1f, 1.1f },
		/* Sergeant   */{ 0.9f, 1.8f, 0.6f, 0.9f, 0.8f, 0.6f, 1.0f, 1.0f, 1.0f, 0.8f, 0.9f, 0.8f },
		/* Lieutenant */{ 0.4f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.9f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f },
		/* Captain 	  */{ 0.5f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f },
		/* Major 	  */{ 0.6f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f },
		/* Colonel 	  */{ 0.3f, 0.8f, 2.8f, 1.5f, 1.1f, 0.5f, 0.6f, 0.6f, 0.7f, 0.2f, 1.0f, 1.6f },
		/* General 	  */{ 0.1f, 0.5f, 2.5f, 1.4f, 1.0f, 0.7f, 0.5f, 0.5f, 0.5f, 1.2f, 0.0f, 1.7f },
		/* Marshal 	  */{ 0.2f, 0.4f, 1.3f, 1.3f, 0.9f, 0.6f, 0.5f, 0.5f, 0.5f, 1.8f, 1.8f, 0.0f }
	};

//	public static float[][] infrontTable = new float[][]{
//		// 				   F	 B	   Sp    Sc	   Sa	 Sg	   L	 Ca	  Maj    Co	   G	Mar
//		/* Flag 	  */{ 0.0f, 3.4f, 0.3f, 0.1f, 0.4f, 0.9f, 0.4f, 0.5f, 0.6f, 0.3f, 0.1f, 0.2f },
//		/* Bomb 	  */{ 5.6f, 0.6f, 0.9f, 0.5f,     , 1.8f,     ,     ,     , 0.8f, 0.5f, 0.4f },
//		/* Spy		  */{ 0.5f, 1.3f, 0.0f,     ,     , 0.6f,     ,     ,     , 2.8f, 2.5f, 1.3f },
//		/* Scout 	  */{     ,     ,     ,     ,     ,     ,     ,     ,     ,     ,     ,      },
//		/* Sapper 	  */{     ,     ,     ,     ,     ,     ,     ,     ,     ,     ,     ,      },
//		/* Sergeant   */{ 1.1f, 1.3f, 0.4f,     ,     , 0.6f,     ,     ,     , 0.5f, 0.7f, 0.6f },
//		/* Lieutenant */{     ,     ,     ,     ,     ,     ,     ,     ,     ,     ,     ,      },
//		/* Captain 	  */{     ,     ,     ,     ,     ,     ,     ,     ,     ,     ,     ,      },
//		/* Major 	  */{     ,     ,     ,     ,     ,     ,     ,     ,     ,     ,     ,      },
//		/* Colonel 	  */{ 0.5f, 0.8f, 2.7f,     ,     , 0.8f,     ,     ,     , 0.2f, 1.2f, 1.8f },
//		/* General 	  */{ 0.2f, 0.5f, 2.3f,     ,     , 0.9f,     ,     ,     , 1.0f, 0.0f, 1.8f },
//		/* Marshal 	  */{ 0.4f, 0.4f, 1.1f,     ,     , 0.8f,     ,     ,     , 1.6f, 1.7f, 0.0f }
//	};

	public static final float[][] sideTable = new float[][]{
		//				   F	 B	   Sp    Sc	   Sa	 Sg	   L	 Ca	  Maj    Co	   G	Mar
		/* Flag 	  */{ 0.0f, 4.2f, 0.5f, 0.3f, 0.5f, 2.0f, 0.8f, 0.8f, 0.8f, 0.7f, 0.4f, 0.5f },
		/* Bomb 	  */{ 2.1f, 2.0f, 1.3f, 0.8f, 0.8f, 1.8f, 1.2f, 1.2f, 1.1f, 0.9f, 0.6f, 0.5f },
		/* Spy		  */{ 0.5f, 1.1f, 0.0f, 1.3f, 1.0f, 0.7f, 1.0f, 1.0f, 1.0f, 2.7f, 2.3f, 1.1f },
		/* Scout 	  */{ 0.4f, 1.1f, 1.3f, 2.0f, 1.2f, 0.6f, 0.8f, 0.8f, 0.8f, 0.7f, 0.7f, 0.7f },
		/* Sapper 	  */{ 0.7f, 0.8f, 0.8f, 1.2f, 0.8f, 0.6f, 1.1f, 1.1f, 1.1f, 0.8f, 0.8f, 0.8f },
		/* Sergeant   */{ 1.8f, 2.3f, 0.6f, 0.7f, 0.7f, 0.6f, 1.2f, 1.2f, 1.2f, 0.8f, 0.8f, 0.8f },
		/* Lieutenant */{ 1.1f, 1.3f, 1.0f, 1.0f, 1.0f, 0.8f, 1.2f, 1.2f, 1.2f, 0.9f, 0.9f, 0.9f },
		/* Captain 	  */{ 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.8f, 1.2f, 1.2f, 1.2f, 0.9f, 0.9f, 0.9f },
		/* Major 	  */{ 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.8f, 1.2f, 1.2f, 1.2f, 0.9f, 0.9f, 0.9f },
		/* Colonel 	  */{ 0.3f, 0.8f, 2.8f, 1.0f, 1.0f, 0.5f, 1.1f, 1.1f, 1.1f, 0.2f, 1.0f, 1.6f },
		/* General 	  */{ 0.1f, 0.5f, 2.5f, 1.0f, 1.0f, 0.7f, 1.1f, 1.1f, 1.1f, 1.2f, 0.0f, 1.7f },
		/* Marshal 	  */{ 0.2f, 0.4f, 1.3f, 1.0f, 1.0f, 0.6f, 1.1f, 1.1f, 1.1f, 1.8f, 1.8f, 0.0f }
	};

	public static float factorFor(Unit.UnitType from, Unit.UnitType to, Direction direction) {
		if (direction == Direction.SIDE) {
			return sideTable[from.ordinal()-3][to.ordinal()-3];
		} else if (direction == Direction.DOWN) {
			return behindTable[from.ordinal() - 3][to.ordinal() - 3];
		} else {
			return behindTable[to.ordinal() - 3][from.ordinal() - 3];
		}
	}

}
