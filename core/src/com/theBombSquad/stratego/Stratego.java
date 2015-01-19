package com.theBombSquad.stratego;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.player.Player;
import com.theBombSquad.stratego.player.ai.players.planner.TheQueen;
import com.theBombSquad.stratego.player.humanoid.HumanPlayer;
import com.theBombSquad.stratego.player.remote.RemoteListeningPlayer;
import com.theBombSquad.stratego.player.remote.RemoteServingPlayer;
import com.theBombSquad.stratego.rendering.AtlasPacker;
import com.theBombSquad.stratego.rendering.BoardRenderer;
import com.theBombSquad.stratego.rendering.DefeatedUnitRenderer;
import com.theBombSquad.stratego.rendering.InformationRenderer;
import com.theBombSquad.stratego.rendering.LayerRenderer;
import com.theBombSquad.stratego.rendering.PlayerRenderer;
import com.theBombSquad.stratego.rendering.RenderData;
import com.theBombSquad.stratego.rendering.Renderer;
import com.theBombSquad.stratego.rendering.humanRenderer.HumanUIRenderer;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.theBombSquad.stratego.StrategoConstants.*;
import static com.theBombSquad.stratego.gameMechanics.Game.*;

/**
 * Entry point for the stratego game. Setups everything and establishes remote connections if necessary.
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @author Flo
 *
 * @version 1.0
 * @created 10.09.14
 *
 * @date 16.09.14
 *
 * @log
 * - Skeleton	10.09.14
 * - TODOs 16.09.14
 */

public class Stratego extends ApplicationAdapter {

	private float windowScale;
	private Game game;

	private SpriteBatch batch;

	private Renderer layerRenderer;

	@Override
	public void create () {

		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(inputMultiplexer);

		// TODO setup everything
		AtlasPacker.pack();
		windowScale = (float)Gdx.graphics.getWidth() / (float)ASSUMED_WINDOW_WIDTH;
		
		experimentalGame();
		
	}
	
	
	private void experimentalGame(){
		
		
		this.game = new Game();
		GameView playerOneView = new GameView(game, StrategoConstants.PlayerID.PLAYER_1);
		GameView playerTwoView = new GameView(game, StrategoConstants.PlayerID.PLAYER_2);
		Player p1 = new TheQueen(playerOneView, false);
		Player p2 = new TheQueen(playerTwoView, true);
		setupGame(p1, p2, game);
		
		this.currentExperiment = new Experiment(p1, p2);
		
		
		this.batch = new SpriteBatch();
		// TODO start the setup phase of the game

		//inputMultiplexer.addProcessor(new GameRestarter(game, this));

		// delayed method execution
		new Thread(new Runnable() {
			@Override public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				startGame();
			}
		}).start();
	}
	

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		layerRenderer.render(batch);
		batch.end();
		if(this.game.isGameOver() || this.game.getPlayer1().getGameView().getCurrentTurn()>3000){
			game.reset();
			experimentalGame();
		}
	}
	
	@Data
	public class Experiment{
		private Player player1;
		private Player player2;
		
		public Experiment(Player player1, Player player2){
			this.player1 = player1;
			this.player2 = player2;
		}
		
		private boolean victory1 = false;
		private boolean victory2 = false;
		private boolean draw = false;
		
		private int turns;
		
	}
	
	private Experiment currentExperiment;
	private ArrayList<Experiment> games = new ArrayList<Experiment>();
	
	private int runs = 50;
	
	private void experimantalGameSetter(){
		
		if(game.getWinner().equals(currentExperiment.player1)){
			currentExperiment.setVictory1(true);
		}
		else if(game.getWinner().equals(currentExperiment.player2)){
			currentExperiment.setVictory2(true);
		}
		else{
			currentExperiment.setDraw(true);
		}
		currentExperiment.setTurns(currentExperiment.getPlayer1().getGameView().getCurrentTurn());
		
		games.add(currentExperiment);
		
		if(games.size()>=runs){
			eval();
		}
		else{
			experimentalGame();
		}
		
	}
	
	private void eval(){
		ArrayList<Integer> turns = new ArrayList<Integer>();
		int avgTurns = 0;
		int p1 = 0;
		int p2 = 0;
		int draw = 0;
		for(int c=0; c<games.size(); c++){
			Experiment e = games.get(c);
			turns.add(e.getTurns());
			avgTurns += e.getTurns();
			if(e.isVictory1()){
				p1++;
			}
			else if(e.isVictory2()){
				p2++;
			}
			else{
				draw++;
			}
		}
		Experiment e = games.get(0);
		avgTurns = avgTurns/games.size();
		Collections.sort(turns);
		float median = turns.get(turns.size()/2);
		System.out.println("P1: "+p1+" | P2: "+p2+" | Draw: "+draw+" | AvgTurns: "+avgTurns+" | Median: "+median);
	}

	protected JOptionPane getOptionPane(JComponent parent) {
		JOptionPane pane = null;
		if (!(parent instanceof JOptionPane)) {
			pane = getOptionPane((JComponent) parent.getParent());
		} else {
			pane = (JOptionPane) parent;
		}
		return pane;
	}

	private GameSetting showMainMenu() {
		// TODO create and show the main menu
		// TODO listen for main menu completion
		if (isLocal()) {
			JComboBox comboBox1 = new JComboBox(StrategoConstants.PlayerType.values());
			JComboBox comboBox2 = new JComboBox(StrategoConstants.PlayerType.values());
			Object[] message = new Object[] { "Player 1", comboBox1, "Player 2", comboBox2 };
			final JButton startButton = new JButton("Start");
			startButton.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) {
					JOptionPane pane = getOptionPane((JComponent) e.getSource());
					pane.setValue(startButton);
				}
			});
			final JOptionPane pane = new JOptionPane(message,
											   JOptionPane.QUESTION_MESSAGE,
											   JOptionPane.YES_NO_OPTION, null, new Object[]{startButton}, startButton);
			JDialog dialog = pane.createDialog(null, "Game Setup");
//			dialog.setAlwaysOnTop(true);
//			dialog.setAutoRequestFocus(true);
			dialog.setVisible(true);

			return new GameSetting(true, (StrategoConstants.PlayerType)comboBox1.getSelectedItem(),
								   (StrategoConstants.PlayerType)comboBox2.getSelectedItem(), false, "");
		} else {
			List<Object> messageList = new ArrayList<Object>();

			JComboBox comboBox1 = new JComboBox(StrategoConstants.PlayerType.values());

			final JButton startButton = new JButton("Start");
			startButton.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) {
					JOptionPane pane = getOptionPane((JComponent) e.getSource());
					pane.setValue(startButton);
				}
			});
			JTextField ipAdress = new JTextField(LOCAL_HOST);
				final Pattern pattern =
					Pattern.compile(
							"\\b([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\b\\.\\b([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\b\\.\\b([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\b\\.\\b([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\b");
			ipAdress.getDocument().addDocumentListener(new DocumentListener() {

				private void update(DocumentEvent e) {
					try {
						String text = (e.getDocument().getLength() > 0) ? e.getDocument().getText(0, e.getDocument().getLength()) : "";
						Matcher matcher = pattern.matcher(text);
						startButton.setEnabled(matcher.matches());
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
				}

				@Override public void insertUpdate(DocumentEvent e) {
					update(e);
				}

				@Override public void removeUpdate(DocumentEvent e) {
					update(e);
				}

				@Override public void changedUpdate(DocumentEvent e) {
					update(e);
				}
			});
			JRadioButton host = new JRadioButton("Host");
			JRadioButton client = new JRadioButton("Join");
			ButtonGroup buttonGroup = new ButtonGroup();
			buttonGroup.add(host);
			buttonGroup.add(client);
			host.setSelected(true);
			messageList.add("Player");
			messageList.add(comboBox1);
			messageList.add(host);
			messageList.add(client);
			messageList.add("");
			messageList.add("Enter the IP Address you want to connect to:");
			messageList.add(ipAdress);
			messageList.add("");
			messageList.add("Found following IP addresses:");
			Enumeration e = null;
			try {
				e = NetworkInterface.getNetworkInterfaces();
				while (e.hasMoreElements()) {
					NetworkInterface n = (NetworkInterface) e.nextElement();
					Enumeration ee = n.getInetAddresses();
					while (ee.hasMoreElements()) {
						InetAddress i = (InetAddress) ee.nextElement();
						String hostAddress = i.getHostAddress();
						Matcher matcher = pattern.matcher(hostAddress);
						if (matcher.matches() && !hostAddress.equals(LOCAL_HOST)) {
							messageList.add(hostAddress);
						}
					}
				}
			} catch (SocketException e1) {
				e1.printStackTrace();
			}

			Object[] message = messageList.toArray();

			JOptionPane pane = new JOptionPane(message,
											   JOptionPane.QUESTION_MESSAGE,
											   JOptionPane.YES_NO_OPTION, null, new Object[] { startButton }, startButton);
			final JDialog dialog = pane.createDialog(null, "Game Setup");
//			dialog.setAlwaysOnTop(true);
//			dialog.setAutoRequestFocus(true);
			dialog.setVisible(true);

			return new GameSetting(false, (StrategoConstants.PlayerType)comboBox1.getSelectedItem(), null, host.isSelected(), ipAdress.getText());
		}
	}

	private boolean isLocal() {

		String op1 = "Local";
		String op2 = "Remote";
		Object[] options = { op1, op2 };
		JOptionPane pane = new JOptionPane("Do you want a local or remote session?", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION, null, options, op1);
		JDialog dialog = pane.createDialog(null, "Local or Remote");
		dialog.setAlwaysOnTop(true);
		dialog.setAutoRequestFocus(true);
		dialog.setVisible(true);
		if (pane.getValue() == null) {
			return true;
		} else {
			return pane.getValue() == op1;
		}
	}

	private void setupGame() {
		// TODO create the game instance
		game = new Game();
		// creates the two game views, one for each player perspective
		GameView playerOneView = new GameView(game, StrategoConstants.PlayerID.PLAYER_1);
		GameView playerTwoView = new GameView(game, StrategoConstants.PlayerID.PLAYER_2);
		// create some observer view
		GameView renderView = new GameView(game, StrategoConstants.PlayerID.NEMO);

		// TODO create the players or get the players?
		Player[] player = determinePlayers(playerOneView, playerTwoView);

		// tell the game about the players
		game.setPlayer1(player[0]);
		game.setPlayer2(player[1]);
		//TODO: this is only supposed to happen if the view is of Player 2, who is a human player:
		//((HumanPlayer)player2).setFlippedBoard(true);

		game.setAI_delay(AI_DELAY);

		// TODO setup renderers
		setupRenderer(game);
	}
	
	private void setupGame(Player player1, Player player2, Game game) {
		// TODO create the game instance
		// creates the two game views, one for each player perspective
		GameView playerOneView = new GameView(game, StrategoConstants.PlayerID.PLAYER_1);
		GameView playerTwoView = new GameView(game, StrategoConstants.PlayerID.PLAYER_2);
		// create some observer view
		GameView renderView = new GameView(game, StrategoConstants.PlayerID.NEMO);

		// TODO create the players or get the players?
		Player[] player = new Player[]{player1, player2};//determinePlayers(playerOneView, playerTwoView);

		// tell the game about the players
		game.setPlayer1(player[0]);
		game.setPlayer2(player[1]);
		//TODO: this is only supposed to happen if the view is of Player 2, who is a human player:
		//((HumanPlayer)player2).setFlippedBoard(true);
		// TODO setup renderers
		
		game.setAI_delay(0);
		
		setupRenderer(game);
	}

	private Player[] determinePlayers(GameView playerOneView, GameView playerTwoView) {
		Player[] player = new Player[2];
		GameSetting gameSetting = showMainMenu();
		if (gameSetting.isLocal()) {
			player[0] = gameSetting.getPlayer1().createPlayer(playerOneView);
			player[1] = gameSetting.getPlayer2().createPlayer(playerTwoView);
		} else {
			if (gameSetting.isServing()) {
				player[0] = new RemoteServingPlayer(gameSetting.getPlayer1().createPlayer(playerOneView), playerOneView, gameSetting.getIp());
				player[1] = new RemoteListeningPlayer(playerTwoView);
			} else {
				player[0] = new RemoteListeningPlayer(playerOneView);
				player[1] = new RemoteServingPlayer(gameSetting.getPlayer1().createPlayer(playerTwoView), playerTwoView, gameSetting.getIp());
			}
		}
		return player;
	}

	private void setupRenderer(Game game) {
		Renderer board = new BoardRenderer(game);
		Renderer death = new DefeatedUnitRenderer(game);
		Renderer info = new InformationRenderer(game);
		Renderer ui = new HumanUIRenderer(game);
		ArrayList<Renderer> rendererList = new ArrayList<Renderer>();
		rendererList.add(death);
		rendererList.add(board);
		boolean noHumanPlayer = true;
		Player player1 = game.getPlayer1();
		Player player2 = game.getPlayer2();
		if (player1 instanceof HumanPlayer || (player1 instanceof RemoteServingPlayer
											   && ((RemoteServingPlayer) player1).getLocalPlayer() instanceof HumanPlayer)) {
			rendererList.add(new PlayerRenderer(game, player1.getGameView()));
			noHumanPlayer = false;
		}
		if (player2 instanceof HumanPlayer || (player2 instanceof RemoteServingPlayer
											   && ((RemoteServingPlayer) player2).getLocalPlayer() instanceof HumanPlayer)) {
			rendererList.add(new PlayerRenderer(game, player2.getGameView()));
			noHumanPlayer = false;
		}
		if (noHumanPlayer) {
			rendererList.add(new PlayerRenderer(game, game.getActiveGameView()));
		}
		rendererList.add(info);
		rendererList.add(ui);
		this.layerRenderer = new LayerRenderer(rendererList, new RenderData(windowScale, new TextureAtlas(Gdx.files.internal("atlas/atlas.atlas"))));
	}

	private void startGame() {
		game.startSetupPhase();
	}

	private void listenForRemoteGameCreation() {
		// TODO wait for an remote game creation on a yet to be defined socket
		// TODO perform the necessary steps to connect and keep the game in sync
	}

	@AllArgsConstructor
	@Data
	public class GameSetting {

		private boolean local;
		private StrategoConstants.PlayerType player1;
		private StrategoConstants.PlayerType player2;
		private boolean serving;
		private String ip;
	}

	@AllArgsConstructor
	private class GameRestarter
			extends InputAdapter {

		Game game;
		Stratego strategoInstance;

		@Override public boolean keyDown(int keycode) {
			if (keycode == Input.Keys.SPACE) {
				if (this.game.isGameOver() && game.isPlayer1FinishedCleanup() && game.isPlayer2FinishedCleanup()) {
					InputMultiplexer inputMultiplexer = (InputMultiplexer) Gdx.input.getInputProcessor();
					inputMultiplexer.clear();
					inputMultiplexer.addProcessor(this);
					this.game.reset();
					GameView playerOneView = new GameView(this.game, StrategoConstants.PlayerID.PLAYER_1);
					GameView playerTwoView = new GameView(this.game, StrategoConstants.PlayerID.PLAYER_2);
					Player[] player = strategoInstance.determinePlayers(playerOneView, playerTwoView);
					// tell the game about the players
					this.game.setPlayer1(player[0]);
					this.game.setPlayer2(player[1]);
					strategoInstance.setupRenderer(game);
					new Thread(new Runnable() {
						@Override
						public void run() {
							strategoInstance.startGame();
						}
					}).start();
				} else
					if (game.isWaitingForEndTurn()) {
						game.setWaitingForEndTurn(false);
					} else
						if (game.isBlind()) {
							game.setBlind(false);
						}
			} else if (keycode == Input.Keys.F2) {
				if (!this.game.isGameOver()) {
					InputMultiplexer inputMultiplexer = (InputMultiplexer) Gdx.input.getInputProcessor();
					inputMultiplexer.clear();
					inputMultiplexer.addProcessor(this);
					this.game.reset();
					GameView playerOneView = new GameView(this.game, StrategoConstants.PlayerID.PLAYER_1);
					GameView playerTwoView = new GameView(this.game, StrategoConstants.PlayerID.PLAYER_2);
					Player[] player = strategoInstance.determinePlayers(playerOneView, playerTwoView);
					// tell the game about the players
					this.game.setPlayer1(player[0]);
					this.game.setPlayer2(player[1]);
					strategoInstance.setupRenderer(game);
					new Thread(new Runnable() {
						@Override
						public void run() {
							strategoInstance.startGame();
						}
					}).start();
				}
			}
			return super.keyDown(keycode);
		}

	}
	
	
	
}
