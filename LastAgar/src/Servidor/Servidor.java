package Servidor;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.awt.*;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Chat.ClienteChat;
import Chat.ServerChat;
import Cliente.Camera;
import Cliente.Cell;
import Persistencia.Leaderboard;
import Cliente.Particle;
import ClienteStrea.ClienteStrea;
import Musica.Server;
import ServidorStrea.*;
import web.WebServer;

import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@SuppressWarnings("serial")
public class Servidor extends JFrame implements MouseMotionListener {

	// Inicializamos el puerto
	private final int puerto = 2027;

	// Numero maximo de conexiones
	private final int noConexiones = 5;

	// Creamos una lista de sockets para guardar el socket de cada jugador
	private LinkedList<Socket> usuarios = new LinkedList<Socket>();

	// Boolean que se asegura si el jugador ya fue creado
	public static boolean playerCreated = false;

	// Boolean que pregunta si el servidor esta actualmente corriendo
	public boolean isRunning = true;

	// Ancho de la ventana
	public static int width = 800;

	// Altura de la ventana
	public static int height = 600;

	// BackBuffer de la ventana
	public static BufferedImage backBuffer;

	// Determina los bordes de la ventana
	public static Insets insets;

	// Relacion con la clase que maneja la lista de puntajes
	public static Leaderboard lb = new Leaderboard();

	// Relacion con la clase camara para asegurarse que el servidor refresque lo que
	// cada cliente ve
	public static Camera cam = new Camera(0, 0, 1, 1);

	// Nombre predefinido de cada cliente
	public static String name = "Kuky";

	// El tiempo maximo en milisegundos que el juego puede correr
	private static int TMAXIMO = 120000;

	// El tiempo inicial en el que comienza a correr el juego, en milisegundos
	private long startTime = System.currentTimeMillis();

	// Boolean que se encarga de esperar que los 2 jugadores iniciales se conecten
	// al servidor
	private boolean esperando;

	// Boolean que se encarga de marcar el final del juego
	public boolean finJuego;

	// Funcion para que el servidor empieze a recibir conexiones de clientes
	@SuppressWarnings("unused")
	public void escuchar() {
		esperando = true;
		finJuego = false;
		try {
			// Creamos la variable para esperar los 2 min maximos a que se conecten
			// jugadores
			long endTime = System.currentTimeMillis() - startTime;

			// Creamos el socket servidor con SSL
			System.setProperty("javax.net.ssl.keyStore", "src/main/certs/server/serverKey.jks");
			System.setProperty("javax.net.ssl.keyStorePassword", "servpass");
			System.setProperty("javax.net.ssl.trustStore", "src/main/certs/server/serverTrustedCerts.jks");
			System.setProperty("javax.net.ssl.trustStorePassword", "servpass");

			SSLServerSocketFactory serverFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(new FileInputStream("src/main/certs/server/serverKey.jks"), "servpass".toCharArray());

			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(keyStore, "servpass".toCharArray());

			KeyManager[] keyManagers = kmf.getKeyManagers();

			KeyStore trustedStore = KeyStore.getInstance("JKS");
			trustedStore.load(new FileInputStream("src/main/certs/server/serverTrustedCerts.jks"),
					"servpass".toCharArray());

			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(trustedStore);

			TrustManager[] trustManagers = tmf.getTrustManagers();

			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(keyManagers, trustManagers, null);

			SSLServerSocketFactory ssf = sc.getServerSocketFactory();
			ServerSocket serverSocket = ssf.createServerSocket(puerto, noConexiones);

			// Ciclo para estar escuchando por nuevos jugadores
			System.out.println("El tiempo maximo de espera de jugadores es de 2 minutos");
			System.out.println("Esperando jugadores....");
			while (esperando) {

				// Cuando el tiempo llege al maximo el servidor dejara de esperar por conexiones
				if (endTime >= TMAXIMO) {
					esperando = false;
					System.out.println("El tiempo maximo de espera de jugadores (2 minutos) ha terminado");
					this.setVisible(false);
				}

				// Cuando un jugador se conecte guardamos el socket en nuestra lista

				Socket cliente = serverSocket.accept();

				// Se agrega el socket a la lista
				usuarios.add(cliente);

				System.out.println("Cliente #: " + usuarios.size() + " conectado satisfactoriamente");
				// Instanciamos un hilo que estara atendiendo al cliente y lo ponemos a escuchar
				Runnable run = new HiloServidor(cliente, usuarios, this);

				if (usuarios.size() <= 1) {
//					ClienteStrea c = new ClienteStrea();
//					ServerChat chat = new ServerChat();
//					chat.go();
//					ClienteChat cliChat = new ClienteChat();
//					cliChat.go();
//					Server musica = new Server();
//					musica.main(null);
				}

				// Espera que la cantidad de ususarios sea mayor a 2
				if (usuarios.size() >= 2) {
					Thread hilo = new Thread(run);
					hilo.start();
					WebServer ser = new WebServer();
				}

				// Se encarga de terminar el juego cuando han pasado mas de 5 minutos
				if (finJuego == true) {
					JOptionPane.showMessageDialog(this, "El juego ha terminado");
				}

			}
			serverSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Metodo que se encarga de inicializar el servidor
	 */
	public void initialize() {
		requestFocus();
		setTitle("'Agario'");
		setSize(width, height);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				try {
					guardar();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		addMouseMotionListener(this);

		insets = getInsets();
		setSize(insets.left + width + insets.right, insets.top + height + insets.bottom);

		backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}

	/**
	 * Metodo que se encarga de actualizar los elementos (celulas enemigas, celula
	 * jugador y comidas) en la pantalla del servidor
	 */
	public void update() {
		lb.Update();

		for (int i = 0; i < Cell.cells.size(); i++) {
			if (Cell.cells.get(i).name.equals(name)) {
				cam.Update(Cell.cells.get(i));
			}
		}

		if (Cell.cellCount < 150) {
			Cell.cells.add(new Cell(("Cell " + Cell.cellCount), (int) Math.floor(Math.random() * 10001),
					(int) Math.floor(Math.random() * 2801), false));
		}

		if (Particle.particleCount < 5000) {
			Particle.particles.add(new Particle((int) Math.floor(Math.random() * 10001),
					(int) Math.floor(Math.random() * 10001), 1, false));
		}

		if (!playerCreated) {
			playerCreated = true;
			Cell.cells.add(new Cell(name, (int) Math.floor(Math.random() * 10001),
					(int) Math.floor(Math.random() * 10001), true));
		}

		for (Iterator<Particle> it = Particle.particles.iterator(); it.hasNext();) {
			Particle p = it.next();
			if (!p.getHealth()) {
				p.Update();
			} else {
				it.remove();
			}
		}

		for (Cell cell : Cell.cells) {
			cell.Update();
		}
	}

	/**
	 * Metodo que se encarga de dibujar los objetos en la ventana del servidor
	 */
	public void draw() {
		Graphics g = getGraphics();

		Graphics bbg = backBuffer.getGraphics();
		Graphics bbg2 = backBuffer.getGraphics();

		bbg.setColor(Color.WHITE);
		bbg.fillRect(0, 0, width, height);

		cam.set(bbg);

		ArrayList<Particle> pCopy = new ArrayList<Particle>(Particle.particles);
		for (Particle p : pCopy) {
			p.Draw(bbg);
		}

		for (Cell cell : Cell.cells) {
			cell.Draw(bbg);
		}

		cam.unset(bbg);

		for (Cell cell : Cell.cells) {
			if (cell.name.equals(name)) {
				String pos = ("X: " + (int) cell.x + " Y: " + (int) cell.y);
				bbg2.drawString(pos, (Servidor.width - pos.length() * pos.length()), 10);
			}
		}

		lb.Draw(bbg2);

		g.drawImage(backBuffer, insets.left, insets.top, this);
	}

	/**
	 * Metodo que se encarga de escuchar los movimientos del raton
	 */
	public void mouseMoved(MouseEvent e) {
		try {
			for (Cell cell : Cell.cells) {
				if (cell.name.equals(name)) {
					cell.getMouseX((int) (e.getX() / cam.sX + cam.x));
					cell.getMouseY((int) (e.getY() / cam.sY + cam.y));
				}
			}
		} catch (Exception e2) {
		}

	}

	/**
	 * 
	 */
	public void mouseDragged(MouseEvent e) {
	}

	/**
	 * ToString de la clase servidor
	 * 
	 * @param x
	 * @return
	 */
	public static String print(String x) {
		System.out.println(x);
		return "";
	}

	/**
	 * get del boolean finJuego
	 * 
	 * @return boolean
	 */
	public boolean isFinJuego() {
		return finJuego;
	}

	/**
	 * set del boolean finJuego
	 * 
	 * @param finJuego
	 */
	public void setFinJuego(boolean finJuego) {
		this.finJuego = finJuego;
	}

	// Funcion main para correr el servidor
	public static void main(String[] args) {
		Servidor servidor = new Servidor();
		servidor.escuchar();

	}

	private void guardar() throws IOException {

		long endTime = System.currentTimeMillis() - startTime;
		endTime = TimeUnit.MILLISECONDS.toMinutes(endTime);

		Calendar c = Calendar.getInstance();

		BufferedWriter bw = new BufferedWriter(new FileWriter("./data/" + name + ".txt", true));
		ArrayList<String[]> use = lb.retornarJugadoresReales();
		for (int i = 0; i < use.size(); i++) {
			double comida = Double.parseDouble(use.get(i)[1]) - 30;
			bw.write(c.get(Calendar.DATE) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.YEAR) + " " + comida
					+ " " + use.get(i)[1] + " " + "No" + " " + endTime);
			bw.newLine();
		}
		bw.close();

		boolean existe = false;
		BufferedWriter bw2 = new BufferedWriter(new FileWriter("./data/Base.txt", true));
		for (int i = 0; i < use.size() && !existe; i++) {
			bw2.write(use.get(i)[0]);
			bw2.newLine();
		}
		bw2.close();

		dispose();
	}
}