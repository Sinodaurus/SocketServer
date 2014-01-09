package be.vdab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
	public static void main(String[] args) throws IOException{
		int portNumber = Integer.parseInt(args[0]);

		try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
			while (true) {
				Socket client = serverSocket.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				String userName = in.readLine();
				ClientHandler c = new ClientHandler(client, userName);
				System.out.println(userName + " has connected...");
				c.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}