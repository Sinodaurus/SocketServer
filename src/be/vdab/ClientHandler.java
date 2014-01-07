package be.vdab;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler extends Thread {
	protected Socket socket;
	protected DataInputStream input;
	protected DataOutputStream output;

	public ClientHandler(Socket socket) throws IOException {
		this.socket = socket;
		input = new DataInputStream(new BufferedInputStream(
				socket.getInputStream()));
		output = new DataOutputStream(new BufferedOutputStream(
				socket.getOutputStream()));
	}

	protected static List<ClientHandler> handlers = new ArrayList<>();

	public void run() {
		try {
			handlers.add(this);
			while (true) {
				String msg = input.readUTF();
				System.out.println("incoming message" + msg);
				broadcast(msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			handlers.remove(this);
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected static void broadcast(String message) {
		synchronized (handlers) {
			for (ClientHandler clientHandler : handlers) {
				try {
					synchronized (clientHandler.output) {
						System.out.println("broadcasting " + message);
						clientHandler.output.writeUTF(message);
					}
					clientHandler.output.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}
}
