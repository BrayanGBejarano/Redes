package Cliente;

import java.awt.*;

import Servidor.Servidor;

public class Camera {

	// Doubles de posicion x y y de la camara
	public double x, y;

	// Double de la scale de x
	public double sX;

	// Double de la scale de y
	public double sY;

	/**
	 * Constructor de la camara
	 * 
	 * @param x
	 * @param y
	 * @param sX
	 * @param sY
	 */
	public Camera(double x, double y, double sX, double sY) {
		this.x = x;
		this.y = y;
		this.sX = sX;
		this.sY = sY;
	}

	/**
	 * Metodo que mapea la camara
	 * 
	 * @param x
	 * @param min1
	 * @param max1
	 * @param min2
	 * @param max2
	 * @return
	 */
	public double map(double x, double min1, double max1, double min2, double max2) {
		return (x - min1) * (max2 - min2) / (max1 - min1) + min2;
	}

	/**
	 * Metodo set de la camara que dibuja los graphics
	 * 
	 * @param bbg
	 */
	public void set(Graphics bbg) {
		Graphics2D g2 = (Graphics2D) bbg;
		g2.scale(sX, sY);
		g2.translate(-x, -y);
	}

	/**
	 * Metodo que se encarga de acomodar las coordenadas de x y y
	 * 
	 * @param bbg
	 */
	public void unset(Graphics bbg) {
		Graphics2D g2 = (Graphics2D) bbg;
		g2.translate(x, y);
	}

	/**
	 * set de las scale
	 * 
	 * @param sx
	 * @param sy
	 */
	public void scale(double sx, double sy) {
		sX = sx;
		sY = sy;
	}

	/**
	 * Metodo que actualiza la comida en la camara
	 * 
	 * @param cell
	 */
	public void Update(Cell cell) {
		double scaleFactor;

		if (cell.mass < 1000) {
			scaleFactor = map(cell.mass, 10, 1000, 1.2, 0.1);
		} else {
			scaleFactor = 0.1;
		}

		scale(scaleFactor, scaleFactor);
		x = ((cell.x + cell.mass * 0.5) - Servidor.width / sX * 0.5);
		y = ((cell.y + cell.mass * 0.5) - Servidor.height / sY * 0.5);
	}
}