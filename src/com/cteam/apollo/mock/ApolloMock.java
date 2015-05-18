package com.cteam.apollo.mock;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class ApolloMock {

	private static final int PORT = 9001;

	private ServerSocket socket;
	private byte[] buffer = new byte[10];
	
	public void setUp() {
		while (true) {
			Socket conn = bind();
			if (conn == null) {
				System.err.println("Failed to bind socket!");
				return;
			}
			System.out.println("Established connection!");
			while(isConnected(conn)) {
				try {
					InputStream stream = conn.getInputStream();
					int length = stream.read(buffer);

					if (length % 3 == 0) {
						// Assume commands and try to parse
						for (int i = 0; i < length; i += 3) {
							Command com = Command.values()[buffer[i]];
							byte[] data = new byte[] {buffer[i + 1], buffer[i + 2]};
							System.out.println("Received Command: " + com.name() + " " + Arrays.toString(data));
						}
					} else if (length == -1) {
						// Connection closed
						System.out.println("Client closed connection");
						return;
					} else {
						// Other data, just log it
						byte[] data = Arrays.copyOfRange(buffer, 0, length);
						System.out.println("Received data: " + Arrays.toString(data));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void tearDown() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Socket bind() {
		try {
			if (socket == null) {
				socket = new ServerSocket(PORT);
				System.out.println("Successfully bound socket");
			}
			return socket.accept();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private boolean isConnected(Socket conn) {
		try {
			conn.getOutputStream().write(0);
			return true;
		} catch (IOException e) {
			System.out.println("Connection closed");
			return false;
		}
	}


	public static void main(String[] args) {
		ApolloMock mock = new ApolloMock();
		mock.setUp();
	}

}
