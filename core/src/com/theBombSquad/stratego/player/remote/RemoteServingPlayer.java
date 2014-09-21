package com.theBombSquad.stratego.player.remote;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.player.Player;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static com.theBombSquad.stratego.StrategoConstants.PORT;
import static com.theBombSquad.stratego.StrategoConstants.SERVE_TIMEOUT;

/**
 * // TODO description
 *
 * @author Fabian Fränz <f.fraenz@t-online.de>
 * @author Flo
 */
@RequiredArgsConstructor
public class RemoteServingPlayer
		extends Player {

	private final Player localPlayer;
	private final String IPAddress;

	private void sendObject(Serializable object) {
		SocketHints socketHints = new SocketHints();
		// Socket will time our in 4 seconds
		socketHints.connectTimeout = SERVE_TIMEOUT;
		//create the socket and connect to the server entered in the text box ( x.x.x.x format ) on port PORT
		Socket socket = Gdx.net.newClientSocket(Net.Protocol.TCP, IPAddress, PORT, socketHints);
		try {
			ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
			output.writeObject(object);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override protected Move move() {

		Move move = localPlayer.move_directAccessOverwrite();

		sendObject(move);

		return move;
	}

	@Override protected GameBoard setup() {

		GameBoard setup = localPlayer.setup_directAccessOverwrite();

		sendObject(setup);

		return setup;

	}

	@Override protected void idle() {

		localPlayer.idle_directAccessOverwrite();
	}
}
