package com.theBombSquad.stratego.player.remote;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.theBombSquad.stratego.gameMechanics.GameView;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.player.Player;
import lombok.extern.java.Log;

import java.io.IOException;
import java.io.ObjectInputStream;

import static com.theBombSquad.stratego.StrategoConstants.LISTEN_TIMEOUT;
import static com.theBombSquad.stratego.StrategoConstants.PORT;

/**
 * TODO Add description
 *
 * @author Fabian Fränz <f.fraenz@t-online.de>
 * @author Flo
 */
@Log
public class RemoteListeningPlayer
		extends Player {

	private final ServerSocket serverSocket;

	public RemoteListeningPlayer(GameView gameView) {
		super(gameView);

		serverSocket = setupServerSocket();
	}

	private ServerSocket setupServerSocket() {
		ServerSocketHints serverSocketHint = new ServerSocketHints();
		// 0 means no timeout.  Probably not the greatest idea in production!
		serverSocketHint.acceptTimeout = LISTEN_TIMEOUT;

		// Create the socket server using TCP protocol and listening on 9021
		// Only one app can listen to a port at a time, keep in mind many ports are reserved
		// especially in the lower numbers ( like 21, 80, etc )
		return Gdx.net.newServerSocket(Net.Protocol.TCP, PORT, serverSocketHint);
	}

	private Object receiveObject() {
		// Create a socket
		Socket socket = serverSocket.accept(null);

		Object receivedObject = null;

		// Read data from the socket into an ObjectInputStream
		try {
			ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
			receivedObject = input.readObject();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return receivedObject;
	}

	@Override protected Move move() {

		Object receivedObject = receiveObject();

		if (receivedObject instanceof Move) {
			Move move = (Move) receivedObject;
			gameView.performMove(move);
			return move;
		} else {
			log.severe("Unrecognized object received. Objects class is: "+receivedObject.getClass());
			return null;
		}
	}

	@Override protected GameBoard setup() {

		Object receivedObject = receiveObject();

		if (receivedObject instanceof GameBoard) {
			GameBoard setup = (GameBoard) receivedObject;
			gameView.setSetup(setup);
			return setup;
		} else {
			log.severe("Unrecognized object received. Objects class is: " + receivedObject.getClass());
			return null;
		}
	}

	@Override protected void idle() {

	}
}
