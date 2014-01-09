package be.vdab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler extends Thread {
	protected Socket socket;
	protected BufferedReader input;
	protected PrintWriter output;
	protected String userName;

	public ClientHandler(Socket socket, String userName) throws IOException {
		this.socket = socket;
		this.userName = userName;
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		output = new PrintWriter(socket.getOutputStream());
	}

	protected static List<ClientHandler> handlers = new ArrayList<>();

	public void run() {
		try {
			handlers.add(this);
			while (true) {
				String msg = input.readLine();
				System.out.println("incoming message: " + msg);
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
					synchronized (clientHandler.output) {
						System.out.println("broadcasting: " + message);
						clientHandler.output.println(message);
					}
					clientHandler.output.flush();
			}
		}
	}
}
