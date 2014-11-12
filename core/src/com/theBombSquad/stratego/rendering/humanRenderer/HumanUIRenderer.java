package com.theBombSquad.stratego.rendering.humanRenderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.Player;
import com.theBombSquad.stratego.player.ai.AI;
import com.theBombSquad.stratego.player.humanoid.HumanPlayer;
import com.theBombSquad.stratego.player.remote.RemoteServingPlayer;
import com.theBombSquad.stratego.rendering.Renderer;

import java.util.ArrayList;
import java.util.List;

import static com.theBombSquad.stratego.StrategoConstants.*;
import static com.theBombSquad.stratego.gameMechanics.Game.*;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created $(DATE)
 */
public class HumanUIRenderer extends Renderer {

	private final Game game;
	private TextureRegion selection;
	private TextureRegion attack;
	private TextureRegion[][] rUnits;

	public HumanUIRenderer(Game game) {
		this.game = game;
	}

	@Override public void init() {
		selection = renderData.getAtlas().findRegion("selection");
		attack = renderData.getAtlas().findRegion("attack");
		rUnits = new TextureRegion[2][12];
		Array<TextureAtlas.AtlasRegion> units = super.renderData.getAtlas().findRegions("unit");
		Array<TextureAtlas.AtlasRegion> units2 = super.renderData.getAtlas().findRegions("unit2");
		for (int c = 0; c < rUnits[0].length; c++) {
			rUnits[0][c] = units.get(c);
			rUnits[1][c] = units2.get(c);
		}
	}

	@Override public void render(SpriteBatch batch) {
		List<Player> relevantPlayers = new ArrayList<Player>();
		if (!game.isFinishedSetup()) {
			relevantPlayers.add(game.getPlayer1());
			relevantPlayers.add(game.getPlayer2());
		} else {
			switch (game.getCurrentPlayer()) {
				case PLAYER_1:
					relevantPlayers.add(game.getPlayer1());
					break;
				case PLAYER_2:
					relevantPlayers.add(game.getPlayer2());
			}
		}
		for (Player player : relevantPlayers) {
			HumanPlayer humanPlayer = null;
			if (player instanceof HumanPlayer) {
				humanPlayer = (HumanPlayer) player;
			} else if (player instanceof RemoteServingPlayer && ((RemoteServingPlayer) player).getLocalPlayer() instanceof HumanPlayer) {
				humanPlayer = (HumanPlayer) ((RemoteServingPlayer) player).getLocalPlayer();
			}
			if (humanPlayer != null) {
				if (humanPlayer.getSetUpPhase()) {
					renderSetupSelectionHover(batch, humanPlayer);
				} else {
					renderGameSelectionHover(batch, humanPlayer);
				}
				renderSelection(batch, humanPlayer);
			}
		}
	}

	private void renderSelection(SpriteBatch batch, HumanPlayer player) {
		float gridX = GRID_POSITION_X * getScale();
		float gridY = GRID_POSITION_Y * getScale();
		float size = POINT_TILE_SIZE * getScale();
		int x = player.getXSelected();
		int y = player.getYSelected();
		if (x != -1 && y != -1) {
			batch.setColor(new Color(0.8f, 0.8f, 0, 0.8f));
			batch.draw(selection,
					   gridX + x * POINT_TILE_SIZE * getScale(),
					   gridY + (GRID_HEIGHT - y - 1) * POINT_TILE_SIZE * getScale(),
					   size,
					   size);
			batch.setColor(Color.WHITE);
		}
	}

	private void renderSetupSelectionHover(SpriteBatch batch, HumanPlayer player) {
		float gridX = GRID_POSITION_X * getScale();
		float gridY = GRID_POSITION_Y * getScale();
		float size = POINT_TILE_SIZE * getScale();
		int xo = player.getXMouseOver();
		int yo = player.getYMouseOver();
		int x = player.getXSelected();
		int y = player.getYSelected();
		if (x != -1 && y != -1 && xo != -1 && yo != -1) {
			Unit unit = player.getGameView().getUnit(x, y);
			int unitRank = unit.getType().getRank();
			if (unitRank != -1) {
				int playerID = (player.getGameView().getUnit(x, y).getOwner().equals(PlayerID.PLAYER_1) ? 0 : 1);
				if (validSetupPosition(xo, yo)) {
					batch.setColor(new Color(0.5f, 1f, 0.5f, 0.5f));
				} else {
					batch.setColor(new Color(1.0f, 0, 0, 0.5f));
				}

				batch.draw(rUnits[playerID][unitRank],
						   gridX + xo * POINT_TILE_SIZE * getScale(),
						   gridY + (GRID_HEIGHT - yo - 1) * POINT_TILE_SIZE * getScale(),
						   size,
						   size);
				batch.setColor(Color.WHITE);
			}

		}
	}

	private void renderGameSelectionHover(SpriteBatch batch, HumanPlayer player) {
		float gridX = GRID_POSITION_X * getScale();
		float gridY = GRID_POSITION_Y * getScale();
		float size = POINT_TILE_SIZE * getScale();
		int xo = player.getXMouseOver();
		int yo = player.getYMouseOver();
		int x = player.getXSelected();
		int y = player.getYSelected();
		if (x != -1 && y != -1 && xo != -1 && yo != -1) {
			if (x == xo && y == yo) {
				Unit unit = player.getGameView().getUnit(x, y);
				int unitRank = unit.getType().getRank();
				if (unitRank != -1) {
					int playerID = (player.getGameView().getUnit(x, y).getOwner().equals(PlayerID.PLAYER_1) ? 0 : 1);
					List<Move> allLegalMovesForUnit = AI.createAllLegalMovesForUnit(player.getGameView(), player.getGameView().getCurrentState(), x, y);
					for (Move move : allLegalMovesForUnit) {
						Unit toUnit = player.getGameView().getUnit(move.getToX(), move.getToY());
						if (toUnit.getOwner() != PlayerID.NEMO && toUnit.getOwner() != player.getGameView().getPlayerID()) {
							batch.setColor(new Color(1, 1, 1, 0.3f));
							batch.draw(attack,
									   gridX + move.getToX() * POINT_TILE_SIZE * getScale(),
									   gridY + (GRID_HEIGHT - move.getToY() - 1) * POINT_TILE_SIZE * getScale(),
									   size,
									   size);
							batch.setColor(Color.WHITE);
						} else {
							batch.setColor(new Color(1, 1, 1, 0.3f));
							batch.draw(rUnits[playerID][unitRank],
									   gridX + move.getToX() * POINT_TILE_SIZE * getScale(),
									   gridY + (GRID_HEIGHT - move.getToY() - 1) * POINT_TILE_SIZE * getScale(),
									   size,
									   size);
							batch.setColor(Color.WHITE);
						}
					}
				}
			}
			else if (validMovePosition(player.getGameView(), x, y, xo, yo)) {
				Unit unit = player.getGameView().getUnit(x, y);
				int unitRank = unit.getType().getRank();
				if (unitRank != -1) {
					int playerID = (player.getGameView().getUnit(x, y).getOwner().equals(PlayerID.PLAYER_1) ? 0 : 1);

					List<Move> allLegalMovesForUnit =
							AI.createAllLegalMovesForUnit(player.getGameView(), player.getGameView().getCurrentState(), x, y);
					Move attemptedMove = new Move(x, y, xo, yo);
					attemptedMove.setPlayerID(player.getGameView().getPlayerID());
					for (Move m : allLegalMovesForUnit) {
						if (m.isMovementInBetween(attemptedMove) && !m.isSameMovementAs(attemptedMove)) {
							batch.setColor(new Color(1, 1, 1, 0.3f));
							batch.draw(rUnits[playerID][unitRank],
									   gridX + m.getToX() * POINT_TILE_SIZE * getScale(),
									   gridY + (GRID_HEIGHT - m.getToY() - 1) * POINT_TILE_SIZE * getScale(),
									   size,
									   size);
							batch.setColor(Color.WHITE);
						}
					}
					batch.setColor(new Color(1, 1, 1, 0.7f));
					Unit toUnit = player.getGameView().getUnit(xo, yo);
					TextureRegion icon = rUnits[playerID][unitRank];
					if (toUnit.getOwner() != PlayerID.NEMO && toUnit.getOwner() != player.getGameView().getPlayerID()) {
						icon = attack;
					}
					batch.draw(icon,
							   gridX + xo * POINT_TILE_SIZE * getScale(),
							   gridY + (GRID_HEIGHT - yo - 1) * POINT_TILE_SIZE * getScale(),
							   size,
							   size);
					batch.setColor(Color.WHITE);
				}
			}
		}
	}

	private boolean validMovePosition(GameView gameView, int x,int y, int tx, int ty) {
		List<Move> allLegalMovesForUnit = AI.createAllLegalMovesForUnit(gameView, gameView.getCurrentState(), x, y);
		boolean valid = false;
		for (Move move : allLegalMovesForUnit) {
			if (move.getToX() == tx && move.getToY() == ty) {
				valid = true;
				break;
			}
		}
		return valid;
	}

	private boolean validSetupPosition(int x, int y) {
		return y != 4 && y != 5;
	}
}
