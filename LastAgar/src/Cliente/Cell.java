package Cliente;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import java.util.ArrayList;

public class Cell {

	public static ArrayList<Cell> cells = new ArrayList<Cell>();
	public static int cellCount;

	public String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double mass = 30;
	public int speed = 1;

	public boolean isPlayer = false;

	public Cell target;
	public Particle pTarget;

	public boolean isTarget = false;
	public String targetType = "p";

	public double x;
	public double y;

	public Color cellColor;

	double goalX, goalY;
	boolean goalReached = true;

	public Cell(String name, int x, int y, boolean isPlayer) {
		this.name = name;
		this.x = x;
		this.y = y;

		this.isPlayer = isPlayer;

		this.randomColor();

		cellCount++;
	}

	public void randomColor() {

		int r = (int) (Math.random() * 256);
		int g = (int) (Math.random() * 256);
		int b = (int) (Math.random() * 256);
		Color randomColor = new Color(r, g, b);

		cellColor = randomColor;
	}

	public void addMass(double mass) {
		this.mass += mass;
	}

	public int returnNearestCell() {

		int x = 0;
		int distance = 9999999;
		int min = distance;
		int max = 500;
		for (Cell cell : cells) {
			if (this != cell) {
				distance = (int) Math
						.sqrt((this.x - cell.x) * (this.x - cell.x) + (cell.y - this.y) * (cell.y - this.y));
				if (distance < min && this.mass > cell.mass + 10) {
					min = distance;
					x = cells.indexOf(cell);
				} else if (distance < min && this.mass < cell.mass + 10 && cell.cellCount == cells.size()) {
					x = -1;
				}
			}
		}
		return x;
	}

	public int returnNearestP() {

		int x = 0;
		int distance = 99999999;
		int min = distance;

		for (Particle p : Particle.particles) {
			distance = (int) Math.sqrt((this.x - p.x) * (this.x - p.x) + (this.y - p.y) * (this.y - p.y));
			if (distance < min && this.mass > p.mass) {
				min = distance;
				x = Particle.particles.indexOf(p);
			}
		}

		return x;
	}

	public void Update() {
		if (this.mass > 3500) {
			this.mass = 3500;
		}
		for (Cell cell : cells) {
			if (this.checkCollide(cell.x, cell.y, cell.mass) && this != cell && this.mass > cell.mass + 10) {
				if (1 / (this.mass / cell.mass) >= .4 && this.mass < 4000) {
					addMass(cell.mass);
				}
				respawn(cell);
			}
		}
		if (!isPlayer) {
			if (goalReached) {
				if (returnNearestCell() > -1) { // No Cell Found
					if (!isTarget) {
						target = cells.get(returnNearestCell());
						isTarget = true;
						targetType = "c";
					} else if (isTarget && targetType.equals("c")) {
						targetType = "n";
						isTarget = false;
					}
				} else if (returnNearestCell() == -1) { // Cell Found
					if (!isTarget) {
						pTarget = Particle.particles.get(returnNearestP());
						isTarget = true;
						targetType = "p";
					} else if (isTarget) {
						targetType = "n";
						isTarget = false;
					}
				}
				goalReached = false;
			} else {
				double dx = 0;

				double dy = 0;
				if (targetType.equals("c")) {
					if (returnNearestCell() > -1) {
						target = cells.get(returnNearestCell());
						dx = (target.x - this.x);
						dy = (target.y - this.y);
					} else {
						goalReached = true;
					}
				} else if (targetType.equals("p")) {
					pTarget = Particle.particles.get(returnNearestP());
					dx = (pTarget.x - this.x);
					dy = (pTarget.y - this.y);
				} else {
					goalReached = true;
				}
				double distance = Math.sqrt((dx) * (dx) + (dy) * (dy));
				if (distance > 1) {
					x += (dx) / distance * speed;
					y += (dy) / distance * speed;
				} else if (distance <= 1) {
					goalReached = true;
				}

			}
		} else {
			double dx = (goalX - this.x);
			double dy = (goalY - this.y);
			this.x += (dx) * 1 / 50;
			this.y += (dy) * 1 / 50;
			// addMass(10);
		}
	}

	public void getMouseX(int mx) {
		goalX = mx;
	}

	public void getMouseY(int my) {
		goalY = my;
	}

	public void respawn(Cell cell) {
		cell.x = (int) Math.floor(Math.random() * 10001);
		cell.y = (int) Math.floor(Math.random() * 10001);
		cell.mass = 20;
	}

	public boolean checkCollide(double x, double y, double mass) {
		return x < this.x + this.mass && x + mass > this.x && y < this.y + this.mass && y + mass > this.y;
	}

	public void Draw(Graphics bbg) {
		bbg.setColor(cellColor);
		bbg.drawOval((int) x, (int) y, (int) mass, (int) mass);
		bbg.fillOval((int) x, (int) y, (int) mass, (int) mass);
		bbg.setColor(Color.WHITE);
		bbg.drawString(name, ((int) x + (int) mass / 2 - name.length() * 3),
				((int) y + (int) mass / 2 + name.length()));
	}

	public String getPuntaje() {
		return mass+"";
	}
}