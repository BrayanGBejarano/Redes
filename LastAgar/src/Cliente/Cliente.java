package Cliente;

import java.io.FileInputStream;
import java.net.Socket;
import java.security.KeyStore;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import ClienteStrea.ClienteStrea;

/**
 *
 */
public class Cliente implements Runnable {
	// Declaramos las variables necesarias para la conexion y comunicacion
	private static Socket cliente;

	// El puerto debe ser el mismo en el que escucha el servidor
	private int puerto = 2027;

	// Si estamos en nuestra misma maquina usamos localhost si no la ip de la
	// maquina servidor
	private String host = "localhost";

	/**
	 * Constructor de la clase cliente
	 * 
	 * @param soc socket de cada cliente
	 */
	public Cliente(Socket soc) {
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Metodo run del cliente que se asegura de crear la conexion entre este y ewl
	 * servidor por medio de SSL
	 */
	@Override
	public void run() {
		try {

			// todo lo relacionado al SSL en la parte del cliente
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(new FileInputStream("src/main/certs/client/clientKey.jks"), "clientpass".toCharArray());

			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(keyStore, "clientpass".toCharArray());

			KeyManager[] keyManagers = kmf.getKeyManagers();

			KeyStore trustedStore = KeyStore.getInstance("JKS");
			trustedStore.load(new FileInputStream("src/main/certs/client/clientTrustedCerts.jks"),
					"clientpass".toCharArray());

			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(trustedStore);

			TrustManager[] trustManagers = tmf.getTrustManagers();

			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(keyManagers, trustManagers, null);

			SSLSocketFactory ssf = sc.getSocketFactory();
			@SuppressWarnings("unused")
			SSLSocketFactory clientFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket client = (SSLSocket) ssf.createSocket(host, puerto);
			client.startHandshake();

			// Cuando conectamos con el servidor
			System.out.println("Cliente Conectado");

			while (true) {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * main de la clase cliente
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Cliente cli = new Cliente(cliente);
		cli.run();

	}

}