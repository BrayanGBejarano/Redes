package Persistencia;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import Cliente.Cell;


public class Leaderboard {

	// Posicion en x de la tabla de puntuaciones
	public int x;

	// Posicion en y de la lista de puntuaciones
	public int y;

	// ArrayList de la lista de celulas en juego
	public static ArrayList<Cell> cellsCopy = Cell.cells;

	// Cantidad maxima de celulas en la tabla de puntuaciones
	public int z = 3;

	// Posicion en Y actual para generar las filas de la tabla de puntuaciones
	public int currentY = 0;

	// Color de la tabla de posiciones
	public Color color = new Color(50, 50, 50, 128);

	// Lugares en la tabla de posiciones
	public int spots[] = new int[z];

	/**
	 * Constructor de la clase puntuaciones
	 */
	public Leaderboard() {
		for (int i = 0; i < z; i++) {
			spots[i] = currentY;
			currentY += 30;
		}
	}

	/**
	 * Metodo que actualiza la tabla de puntuaciones
	 */
	public void Update() {
		cellsCopy = Cell.cells;
		Collections.sort(cellsCopy, new leaderComparator());
	}

	/**
	 * Metodo que dibuja la tabla de puntuaciones
	 * 
	 * @param bbg
	 */
	public void Draw(Graphics bbg) {
		for (int i = 0; i < z; i++) {
			bbg.setColor(color);
			bbg.drawRect(x, y + spots[i], 125, 30);
			bbg.fillRect(x, y + spots[i], 125, 30);
			bbg.setColor(Color.WHITE);
			if (Cell.cells.size() >= z) {
				bbg.drawString("#" + (i + 1) + ": " + cellsCopy.get(i).name + " : " + (int) cellsCopy.get(i).mass, x,
						y + spots[i] + 25);
			}
		}
	}

	/**
	 * 
	 * Metodo que se encarga de comparar que celula se encuentra en primer lugar
	 */
	private class leaderComparator implements Comparator<Cell> {
		@Override
		public int compare(Cell c1, Cell c2) {
			if (c1.mass == c2.mass) {
				return 0;
			} else if (c1.mass > c2.mass) {
				return -1;
			} else {
				return 1;
			}
		}
	}

	public int[] getSpots() {
		return spots;
	}

	public void setSpots(int[] spots) {
		this.spots = spots;
	}
	
	public ArrayList<String[]> retornarJugadoresReales() {
		ArrayList<String[]> retorno = new ArrayList<String[]>();
		for (int i = 0; i < cellsCopy.size(); i++) {
			if(cellsCopy.get(i).isPlayer) {
				retorno.add(new String[]{cellsCopy.get(i).getName(),cellsCopy.get(i).getPuntaje()});
			}
		}
//		for (int i = 0; i < spots.length; i++) {
//			if(spots[i] != 0) {
//				retorno.add(new String[]{Integer.toString(x),Integer.toString(y)});
//			}
//		}
		return retorno;
	}

}