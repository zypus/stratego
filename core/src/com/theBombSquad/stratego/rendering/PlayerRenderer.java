package com.theBombSquad.stratego.rendering;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.theBombSquad.stratego.StrategoConstants;
import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.gameMechanics.board.Encounter;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.humanoid.HumanPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.theBombSquad.stratego.StrategoConstants.*;
import static com.theBombSquad.stratego.gameMechanics.Game.*;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created $(DATE)
 */
public class PlayerRenderer
		extends Renderer {

	public static final float MOVE_TIME = 0.4f;

	private Game game;
	private GameView gameView;

	/**
	 * Textures of Units
	 */
	private TextureRegion[][] rUnits;
	/**
	 * Textures of Units's backs that have been defeated, in the order they will be drawn from top to bottom
	 */
	private TextureRegion[] unitBacks;
	private BitmapFont font;
	private StrategoConstants.PlayerID playerID;
	private TextureAtlas.AtlasRegion eye;

	private TweenManager manager;
	private Map<Integer, UnitTile> unitTileMap;
	private Map<Integer, UnitTile> movingUnits = new HashMap<Integer, UnitTile>();

	private int lastPly = 0;

	private boolean isSetup = false;

	public PlayerRenderer(Game game, GameView gameView) {
		this.game = game;
		this.gameView = gameView;
	}

	@Override public void init() {
		manager = new TweenManager();
		font = super.renderData.getFont();
		font.setColor(Color.WHITE);
		initUnitImages();
		Tween.registerAccessor(UnitTile.class, new UnitTile.UnitTileAccessor());
	}

	/**
	 * Initializes the Texture Regions that represent the Units
	 */
	private void initUnitImages() {
		eye = super.renderData.getAtlas().findRegion("eye");
		rUnits = new TextureRegion[2][12];
		Array<TextureAtlas.AtlasRegion> units = super.renderData.getAtlas().findRegions("unit");
		Array<TextureAtlas.AtlasRegion> units2 = super.renderData.getAtlas().findRegions("unit2");
		for (int c = 0; c < rUnits[0].length; c++) {
			rUnits[0][c] = units.get(c);
			rUnits[1][c] = units2.get(c);
		}
		unitTileMap = new HashMap<Integer, UnitTile>();
		setUnitTiles(game.getPlayer1Units(), 0);
		setUnitTiles(game.getPlayer2Units(), 1);
		unitBacks = new TextureRegion[2];
		Array<TextureAtlas.AtlasRegion> backs = super.renderData.getAtlas().findRegions("back");
		for (int c = 0; c < unitBacks.length; c++) {
			unitBacks[c] = backs.get(c);
		}
	}

	private void setUnitTiles(List<Unit> unitList, int player) {
		for (Unit unit : unitList) {
			int unitRank = unit.getType().getRank();
			UnitTile unitTile = new UnitTile()
					.setUnit(unit)
					.setTile(rUnits[player][unitRank]);
			unitTileMap.put(unit.getId(), unitTile);
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		if (!game.isReseted()) {
			if (game.getActiveGameView() == gameView || (gameView.getPlayer() instanceof HumanPlayer && ((HumanPlayer) gameView.getPlayer()).getSetUpPhase())) {
				float gridX = GRID_POSITION_X * getScale();
				float gridY = GRID_POSITION_Y * getScale();
				float size = POINT_TILE_SIZE * getScale();
				font.setScale(1);
				font.setColor(new Color(1, 1, 1, 0.8f));
				drawXAxis(batch);
				drawYAxis(batch);
				//Draw Units
				if (!game.isFinishedSetup() && gameView.getPlayer() instanceof HumanPlayer && ((HumanPlayer) gameView.getPlayer()).getSetUpPhase()) {
					HumanPlayer player = (HumanPlayer) gameView.getPlayer();
					int playerOrdinal = gameView.getPlayerID().ordinal();
					Setup unitPallet = player.getUnitPallet();
					Setup setup = player.getCurrentSetup();
					for (int cy = 0; cy < unitPallet.getHeight(); cy++) {
						for (int cx = 0; cx < unitPallet.getWidth(); cx++) {
							Unit unit = unitPallet.getUnit(cx, cy);
							int unitRank = unit.getType().getRank();
							if (unitRank != -1) {
								drawTile(rUnits[playerOrdinal][unitRank], batch, cx, cy, size, gridX, gridY);
							}
						}
					}
					for (int cy = 0; cy < setup.getHeight(); cy++) {
						for (int cx = 0; cx < setup.getWidth(); cx++) {
							Unit unit = setup.getUnit(cx, cy);
							int unitRank = unit.getType().getRank();
							if (unitRank != -1) {
								drawTile(rUnits[playerOrdinal][unitRank], batch, cx, cy + 6, size, gridX, gridY);
							}
						}
					}
				} else if (!game.isFinishedSetup()) {
					// do nothing
				} else if (game.isBlind()) {
					GameBoard board = gameView.getState((gameView.getCurrentTurn() > 1) ? gameView.getCurrentTurn()-1 : gameView.getCurrentTurn());
					for (int cy = 0; cy < board.getHeight(); cy++) {
						for (int cx = 0; cx < board.getWidth(); cx++) {
							Unit unit = board.getUnit(cx, cy);
							int unitRank = unit.getType().getRank();
							if (unitRank != -1) {
								int player = (board.getUnit(cx, cy).getOwner().equals(PlayerID.PLAYER_1) ? 0 : 1);
								if (!unit.isUnknown() && !game.isBlind()) {
									PlayerID activePlayer = game.getActiveGameView().getPlayerID();
									if (!game.isGameOver() && game.isFinishedSetup()
										&& activePlayer == unit.getOwner()
										&& activePlayer != game.getCurrentPlayer()) {
										batch.setColor(new Color(0.8f, 0.8f, 0.8f, 1));
									}
									drawTile(rUnits[player][unitRank], batch, cx, cy, size, gridX, gridY);
									if (!game.isGameOver() && unit.getOwner() == activePlayer
										&& unit.getRevealedInTurn() != UNREVEALED
										&& gameView.getCurrentTurn() >= unit.getRevealedInTurn()) {
										batch.setColor(new Color(1, 1, 1, 0.75f));
										drawTile(eye, batch, cx, cy, size, gridX, gridY);
									}
									batch.setColor(Color.WHITE);
								} else {
									drawTile(unitBacks[player], batch, cx, cy, size, gridX, gridY);
								}
							}
						}
					}
				} else {
					if (!isSetup) {
						GameBoard initialState = gameView.getState(1);
						for (int cy = 0; cy < initialState.getHeight(); cy++) {
							for (int cx = 0; cx < initialState.getWidth(); cx++) {
								Unit unit = initialState.getUnit(cx, cy);
								if (!unit.isAir() && !unit.isLake()) {
									float x = gridX + cx * POINT_TILE_SIZE * getScale();
									float y = gridY + (GRID_HEIGHT - cy - 1) * POINT_TILE_SIZE * getScale();
									Vector2 position = new Vector2(x, y);
									unitTileMap.get(unit.getId()).setPosition(position);
								}
							}
						}
						isSetup = true;
					}
					while (lastPly < gameView.getMoves().size()) {
						final Move move = gameView.getMove(lastPly);
						lastPly++;
						final Unit movedUnit = move.getMovedUnit();
						UnitTile tile = unitTileMap.get(movedUnit.getId());
						movingUnits.put(movedUnit.getId(), tile);
						float xf = gridX + move.getFromX() * POINT_TILE_SIZE * getScale();
						float yf = gridY + (GRID_HEIGHT - move.getFromY() - 1) * POINT_TILE_SIZE * getScale();
						float x = gridX + move.getToX() * POINT_TILE_SIZE * getScale();
						float y = gridY + (GRID_HEIGHT - move.getToY() - 1) * POINT_TILE_SIZE * getScale();
						Timeline timeline = Timeline.createSequence();
						if (move.hasEncounter()) {
							Timeline tl = Timeline.createSequence();
							if (move.getDistance() > 1) {
								float xa = x + (xf - x) / (move.getDistance());
								float ya = y + (yf - y) / (move.getDistance());
								tl.push(Tween.to(tile, UnitTile.UnitTileAccessor.POSITION, MOVE_TIME * 0.8f * (move.getDistance() - 1))
											 .target(xa, ya));
							}
							float first = 1.5f;
							float max = 2f;
							float second = max - first;
							tl.beginParallel()
							  .beginSequence()
							  .pushPause(MOVE_TIME * first)
							  .push(Tween.to(tile, UnitTile.UnitTileAccessor.POSITION, MOVE_TIME * second)
										 .target(x, y))
							  .end()
							  .beginSequence()
							  .push(Tween.to(tile, UnitTile.UnitTileAccessor.SCALE, MOVE_TIME * first)
										 .target(1.3f))
							  .push(Tween.to(tile, UnitTile.UnitTileAccessor.SCALE, MOVE_TIME * second)
										 .target(1f))
							  .end()
							  .end();
							timeline.push(tl);
						} else {
							Tween tw = Tween.to(tile, UnitTile.UnitTileAccessor.POSITION, MOVE_TIME * move.getDistance())
											.target(x, y);
							timeline.push(tw);
						}

						timeline.push(Tween.call(new TweenCallback() {
							@Override public void onEvent(int i, BaseTween<?> baseTween) {
								if (move.hasEncounter()) {
									Encounter encounter = move.getEncounter();
									Timeline killedTimeline = Timeline.createParallel();
									for (final Unit killedUnit : encounter.getDefeatedUnits()) {
										final UnitTile unitTile = unitTileMap.get(killedUnit.getId());
										movingUnits.put(killedUnit.getId(), unitTile);
										if (!killedUnit.getType().equals(Unit.UnitType.FLAG)) {
											Vector2 position = DefeatedUnitRenderer.getGridPositionForUnit(killedUnit);
											Timeline t = Timeline.createSequence()
																 .push(Tween.to(unitTile,
																				UnitTile.UnitTileAccessor.POSITION,
																				0.75f).target(position.x, position.y).ease(
																		 TweenEquations.easeOutQuad))
																 .push(Tween.call(new TweenCallback() {
																	 @Override public void onEvent(int i, BaseTween<?> baseTween) {
																		 movingUnits.remove(killedUnit.getId());
																		 unitTile.setKilled(true);
																	 }
																 }));
											killedTimeline.push(t);
										} else {
											unitTile.setKilled(true);
											movingUnits.remove(killedUnit.getId());
										}
									}
									manager.add(killedTimeline);
								} else {
									movingUnits.remove(movedUnit.getId());
								}
							}
						}));
						manager.add(timeline);
					}
					manager.update(Gdx.graphics.getDeltaTime());
					for (Integer unitKey : unitTileMap.keySet()) {
						if (!movingUnits.containsKey(unitKey)) {
							UnitTile tile = unitTileMap.get(unitKey);
							if (!tile.isKilled()) {
								drawUnit(tile, batch, size);
							}
						}
					}
					for (UnitTile unitTile : movingUnits.values()) {
						drawUnit(unitTile, batch, size);
					}
				}
			}
		} else {
			if (isSetup) {
				for (UnitTile tile : unitTileMap.values()) {
					tile.setKilled(false);
				}
				isSetup = false;
			}
			lastPly = 0;
		}

	}

	private void drawUnit(UnitTile unitTile, SpriteBatch batch, float size) {
		float scaledSize = size*unitTile.getScale();
		float x = unitTile.getPosition().x;
		float y = unitTile.getPosition().y;
		Unit unit = unitTile.getUnit();
		int player = (unit.getOwner().equals(PlayerID.PLAYER_1) ? 0 : 1);
		boolean unknown = !gameView.getPlayerID().equals(PlayerID.NEMO) && !unit.getOwner().equals(gameView.getPlayerID()) &&  (unit.getRevealedInTurn() == UNREVEALED || unit.getRevealedInTurn() > ((game.isWaitingForEndTurn()) ? gameView.getCurrentTurn()+1 : gameView.getCurrentTurn()));
		if (!unknown) {
			if (!game.isGameOver() && game.isFinishedSetup()
				&& game.getActiveGameView().getPlayerID() == unit.getOwner()
				&& game.getActiveGameView().getPlayerID() != game.getCurrentPlayer()) {
				batch.setColor(new Color(0.8f, 0.8f, 0.8f, 1));
			}
			batch.draw(unitTile.getTile(),
					   x,
					   y,
					   scaledSize,
					   scaledSize);
			if (!game.isGameOver() && unit.getOwner() == gameView.getPlayerID()
				&& unit.getRevealedInTurn() != UNREVEALED
				&& gameView.getCurrentTurn() >= unit.getRevealedInTurn()) {
				batch.setColor(new Color(1, 1, 1, 0.75f));
				batch.draw(eye, x, y, scaledSize, scaledSize);
			}
			batch.setColor(Color.WHITE);
		} else {
			batch.draw(unitBacks[player], x, y, scaledSize, scaledSize);
		}

	}

	/**
	 * Draws specified tile onto grid, starting in upper left corner
	 */
	private void drawTile(TextureRegion tile, SpriteBatch batch, float x, float y, float size, float relGridX, float relGridY) {
		batch.draw(tile,
				   relGridX + x * POINT_TILE_SIZE * getScale(),
				   relGridY + (GRID_HEIGHT - y - 1) * POINT_TILE_SIZE * getScale(),
				   size,
				   size);
	}

	private void drawXAxis(SpriteBatch batch) {
		float gridX = GRID_POSITION_X * getScale();
		float gridY = GRID_POSITION_Y * getScale();
		float size = POINT_TILE_SIZE * getScale();
		PlayerID player;
		if (game.isFinishedSetup()) {
			player = game.getActiveGameView().getPlayerID();
		} else {
			if (!game.isPlayer1FinishedSetup()) {
				player = PlayerID.PLAYER_1;
			} else {
				player = PlayerID.PLAYER_2;
			}
		}

		if (player == PlayerID.PLAYER_1 || player == PlayerID.NEMO) {
			for (int i = 0; i < Move.xRep.length; i++) {
				String text = Move.xRep[i];
				BitmapFont.TextBounds bounds = font.getBounds(text);
				font.draw(batch, text,
						  gridX + i * size + size / 2 - bounds.width / 2,
						  gridY - bounds.height / 2);
			}
		} else {
			for (int i = Move.xRep.length - 1; i >= 0; i--) {
				String text = Move.xRep[i];
				BitmapFont.TextBounds bounds = font.getBounds(text);
				font.draw(batch, text,
						  gridX + (Move.xRep.length - 1 - i) * size + size / 2 - bounds.width / 2,
						  gridY - bounds.height / 2);
			}
		}
	}

	private void drawYAxis(SpriteBatch batch) {
		float gridX = GRID_POSITION_X * getScale();
		float gridY = GRID_POSITION_Y * getScale();
		float size = POINT_TILE_SIZE * getScale();
		PlayerID player;
		if (game.isFinishedSetup()) {
			player = game.getActiveGameView().getPlayerID();
		} else {
			if (!game.isPlayer1FinishedSetup()) {
				player = PlayerID.PLAYER_1;
			} else {
				player = PlayerID.PLAYER_2;
			}
		}

		if (player == PlayerID.PLAYER_2) {
			for (int i = 0; i < GRID_HEIGHT; i++) {
				String text = "" + i;
				BitmapFont.TextBounds bounds = font.getBounds(text);
				font.draw(batch, text,
						  gridX + GRID_WIDTH * size + bounds.height / 2,
						  gridY + i * size + size / 2 + bounds.height / 2);
			}
		} else {
			for (int i = GRID_HEIGHT - 1; i >= 0; i--) {
				String text = "" + i;
				BitmapFont.TextBounds bounds = font.getBounds(text);
				font.draw(batch, text,
						  gridX - bounds.height,
						  gridY + (GRID_HEIGHT - 1 - i) * size + size / 2 + bounds.height / 2);
			}
		}
	}
}
