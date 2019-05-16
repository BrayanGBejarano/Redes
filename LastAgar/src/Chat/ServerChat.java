package Chat;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerChat {
	@SuppressWarnings("rawtypes")
	public ArrayList clientOutputStreams;

	// Hilo que se encarga de recibir y mostrar los mensajes
	public class ClientHandler implements Runnable {
		public BufferedReader reader;
		public Socket sock;

		/**
		 * Constructor del hilo que maneja los mensajes
		 * 
		 * @param clientSocket
		 */
		public ClientHandler(Socket clientSocket) {
			try {
				sock = clientSocket;
				InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
				reader = new BufferedReader(isReader);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} // close constructor

		/**
		 * Metodo run del hilo
		 */
		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					System.out.println("read " + message);
					tellEveryone(message);
				} // close while
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} // close run
	} // close inner class

	public static void main(String[] args) {
		new ServerChat().go();
	}

	/**
	 * Metodo que se encarga de recibir la conexion de los clientes
	 */
	public void go() {
		clientOutputStreams = new ArrayList();
		try {
			ServerSocket serverSock = new ServerSocket(5001);
			while (true) {
				Socket clientSocket = serverSock.accept();
				PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
				clientOutputStreams.add(writer);
				Thread t = new Thread(new ClientHandler(clientSocket));
				t.start();
				System.out.println("got a connection");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	} // close go

	/**
	 * Metodo que se encarga de hacer el broadcasting de los mensajes
	 * 
	 * @param message el mensaje
	 */
	public void tellEveryone(String message) {
		Iterator it = clientOutputStreams.iterator();
		while (it.hasNext()) {
			try {
				PrintWriter writer = (PrintWriter) it.next();
				writer.println(message);
				writer.flush();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} // end while
	} // close tellEveryone
} // close class