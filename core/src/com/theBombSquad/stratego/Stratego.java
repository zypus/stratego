package com.theBombSquad.stratego;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.gameMechanics.GameView;
import com.theBombSquad.stratego.player.Player;
import com.theBombSquad.stratego.player.remote.RemoteListeningPlayer;
import com.theBombSquad.stratego.player.remote.RemoteServingPlayer;
import com.theBombSquad.stratego.rendering.AtlasPacker;
import com.theBombSquad.stratego.rendering.BoardRenderer;
import com.theBombSquad.stratego.rendering.DefeatedUnitRenderer;
import com.theBombSquad.stratego.rendering.LayerRenderer;
import com.theBombSquad.stratego.rendering.RenderData;
import com.theBombSquad.stratego.rendering.Renderer;
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
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.theBombSquad.stratego.StrategoConstants.*;

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
		// TODO setup everything
		AtlasPacker.pack();
		windowScale = (float)Gdx.graphics.getWidth() / (float)ASSUMED_WINDOW_WIDTH;
		setupGame();
		this.batch = new SpriteBatch();
		// TODO start the setup phase of the game
		startGame();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		layerRenderer.render(batch);
		batch.end();
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
			JOptionPane pane = new JOptionPane(message,
											   JOptionPane.QUESTION_MESSAGE,
											   JOptionPane.YES_NO_OPTION, null, new Object[]{"Start"});
			JDialog dialog = pane.createDialog(null, "Game Setup");
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
											   JOptionPane.YES_NO_OPTION, null, new Object[] { startButton });
			final JDialog dialog = pane.createDialog(null, "Game Setup");
			dialog.setVisible(true);

			return new GameSetting(false, (StrategoConstants.PlayerType)comboBox1.getSelectedItem(), null, host.isSelected(), ipAdress.getText());
		}
	}

	private boolean isLocal() {

		String op1 = "Local";
		String op2 = "Remote";
		Object[] options = { op1, op2 };
		return JOptionPane.showOptionDialog(null,
											"Do you want a local or remote session?",
											"Local or Remote",
											JOptionPane.YES_NO_OPTION,
											JOptionPane.QUESTION_MESSAGE,
											null,
											options, op1) == 0;
	}

	private void setupGame() {
		// TODO create the game instance
		game = new Game();
		// creates the two game views, one for each player perspective
		GameView playerOneView = new GameView(game, StrategoConstants.PlayerID.PLAYER_1);
		GameView playerTwoView = new GameView(game, StrategoConstants.PlayerID.PLAYER_2);
		// create some observer view
		GameView observerView = new GameView(game, StrategoConstants.PlayerID.NEMO);

		// TODO create the players or get the players?
		Player player1;
		Player player2;
		GameSetting gameSetting = showMainMenu();
		if (gameSetting.isLocal()) {
			player1 = gameSetting.getPlayer1().createPlayer(playerOneView);
			player2 = gameSetting.getPlayer2().createPlayer(playerTwoView);
			if (gameSetting.getPlayer1() == PlayerType.HUMAN) {
				observerView = playerOneView;
			} else if (gameSetting.getPlayer2() == PlayerType.HUMAN) {
				observerView = playerTwoView;
			}
		} else {
			if (gameSetting.isServing()) {
				player1 = new RemoteServingPlayer(gameSetting.getPlayer1().createPlayer(playerOneView), playerOneView, gameSetting.getIp());
				player2 = new RemoteListeningPlayer(playerTwoView);
				observerView = playerOneView;
			} else {
				player1 = new RemoteListeningPlayer(playerOneView);
				player2 = new RemoteServingPlayer(gameSetting.getPlayer1().createPlayer(playerTwoView), playerTwoView, gameSetting.getIp());
				observerView = playerTwoView;
			}
		}

		// tell the game about the players
		game.setPlayer1(player1);
		game.setPlayer2(player2);
		//TODO: this is only supposed to happen if the view is of Player 2, who is a human player:
		//((HumanPlayer)player2).setFlippedBoard(true);

		// TODO setup renderers
		setupRenderer(playerOneView, playerTwoView, observerView);
	}

	private void setupRenderer(GameView gameView1, GameView gameView2, GameView observerView) {
		Renderer board = new BoardRenderer(observerView);
		Renderer death = new DefeatedUnitRenderer();
		ArrayList<Renderer> rendererList = new ArrayList<Renderer>();
		rendererList.add(board);
		rendererList.add(death);
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
}
