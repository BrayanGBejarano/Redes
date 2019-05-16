package Servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.LinkedList;

/**
 *
 */
public class HiloServidor implements Runnable {

	// Declaramos las variables que utiliza el hilo para estar recibiendo y mandando
	// mensajes
	private static int TMAXIMO = 300000;
	
	//
	private Socket socket;
	
	//
	@SuppressWarnings("unused")
	private DataOutputStream out;
	
	//
	@SuppressWarnings("unused")
	private DataInputStream in;
	
	//
	private LinkedList<Socket> usuarios = new LinkedList<Socket>();
	
	//
	public Servidor server;
	
	//
	public int fps = 60;
	
	//
	public long startTime = System.currentTimeMillis();

	//
	public boolean enJuego;

	// Constructor que recibe el socket que atendera el hilo y la lista de los
	// jugadores el turno y la matriz del juego
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HiloServidor(Socket soc, LinkedList users, Servidor ser) {
		socket = soc;
		usuarios = users;
		server = ser;
		enJuego = true;
	}
	
	/**
	 * run del hilo servidor
	 */
	@Override
	public void run() {
		try {
			// Inicializamos los canales de comunicacion y mandamos el turno a cada jugador
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());

			// Ciclo infinito que estara escuchando por los movimientos de cada jugador
			server.initialize();
			while (enJuego) {
				long time = System.currentTimeMillis();
				long endTime = System.currentTimeMillis() - startTime;

				server.update();
				server.draw();
				time = (1000 / fps) - (System.currentTimeMillis() - time);

				if (time > 0) {
					try {
						Thread.sleep(time);
					} catch (Exception e) {
					}
				}

				if (endTime >= TMAXIMO) {
					enJuego = false;
					server.setFinJuego(true);
				}

			}

		} catch (Exception e) {

			// Si ocurre un excepcion lo mas seguro es que sea por que algun jugador se
			// desconecto asi que lo quitamos de la lista de conectados
			for (int i = 0; i < usuarios.size(); i++) {
				if (usuarios.get(i) == socket) {
					usuarios.remove(i);
					break;
				}
			}
		}
	}

	/**
	 * get de enJuego
	 * 
	 * @return
	 */
	public boolean isEnJuego() {
		return enJuego;
	}

	/**
	 * set de enJuego
	 * 
	 * @param enJuego
	 */
	public void setEnJuego(boolean enJuego) {
		this.enJuego = enJuego;
	}

}