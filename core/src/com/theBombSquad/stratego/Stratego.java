package com.theBombSquad.stratego;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.gameMechanics.GameView;
import com.theBombSquad.stratego.player.Player;
import com.theBombSquad.stratego.player.ai.players.random.RandomAI;
import com.theBombSquad.stratego.player.remote.RemoteListeningPlayer;
import com.theBombSquad.stratego.player.remote.RemoteServingPlayer;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.swing.*;
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

import static com.theBombSquad.stratego.StrategoConstants.ASSUMED_WINDOW_WIDTH;

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

	@Override
	public void create () {
		// TODO setup everything
		windowScale = (float)Gdx.graphics.getWidth() / (float)ASSUMED_WINDOW_WIDTH;
		setupGame();
		// TODO start the setup phase of the game
		startGame();
	}

	@Override
	public void render () {

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// TODO call the appropriate renderer
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
		List<Object> messageList = new ArrayList<Object>();

		final JButton startButton = new JButton("Start");
		startButton.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				JOptionPane pane = getOptionPane((JComponent) e.getSource());
				pane.setValue(startButton);
			}
		});
		JTextField ipAdress = new JTextField("127.0.0.1");
		ipAdress.getDocument().addDocumentListener(new DocumentListener() {

			private Pattern
					pattern =
					Pattern.compile(
							"\\b([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\b\\.\\b([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\b\\.\\b([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\b\\.\\b([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\b");

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
					messageList.add(i.getHostAddress());
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

		return new GameSetting(host.isSelected(), ipAdress.getText());
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
		// for now instantiate two random players
		Player player1;
		Player player2;
		String[] selectionValues = { "Host", "Join" };
		GameSetting gameSetting = showMainMenu();
		if (gameSetting.isServing()) {
			player1 = new RemoteServingPlayer(new RandomAI(playerOneView), playerOneView, gameSetting.getIp());
			player2 = new RemoteListeningPlayer(playerTwoView);
		} else {
			player1 = new RemoteListeningPlayer(playerOneView);
			player2 = new RemoteServingPlayer(new RandomAI(playerTwoView), playerTwoView, gameSetting.getIp());
		}
//		Player player1 = new RandomAI(playerOneView);
//		Player player2 = new RandomAI(playerTwoView);

		// tell the game about the players
		game.setPlayer1(player1);
		game.setPlayer2(player2);

		// TODO setup renderers
		setupRenderer(playerOneView, playerTwoView, observerView);
	}

	private void setupRenderer(GameView gameView1, GameView gameView, GameView observerView) {
		// TODO setup the render system
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

		private boolean serving;
		private String ip;
	}
}
