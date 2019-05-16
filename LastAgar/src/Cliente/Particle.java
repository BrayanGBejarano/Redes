package Cliente;

import java.util.ArrayList;
import java.awt.*;

public class Particle {

	public static ArrayList<Particle> particles = new ArrayList<Particle>();

	public static int particleCount;

	public int x, y, r, g, b, mass;
	public double speed, angle, dx, dy;
	private double goalX, goalY;

	private boolean cellParticle = false;
	private boolean die = false;
	public boolean isShot;

	private Color color = new Color((int) Math.floor(Math.random() * 256), (int) Math.floor(Math.random() * 256),
			(int) Math.floor(Math.random() * 256));

	public Particle(int x, int y, int mass, boolean p) {
		particleCount++;
		this.x = x;
		this.y = y;
		this.mass = mass;
		cellParticle = p;
	}

	public void Update() {
		for (Cell cell : Cell.cells) {
			if (this.checkCollide(cell.x, cell.y, cell.mass) && !cellParticle) {
				if (cell.mass <= 200) {
					cell.addMass(this.mass);
				}
				if (cell.mass >= 200) {
					cell.isTarget = false;
					cell.goalReached = true;
					cell.targetType = "c";
				}
				if (cell.targetType.equals("p")) {
					cell.goalReached = true;
					cell.isTarget = false;
				}
				this.x = (int) Math.floor(Math.random() * 10001);
				this.y = (int) Math.floor(Math.random() * 10001);
			} else if (this.checkCollide(cell.x, cell.y, cell.mass) && cellParticle && !cell.isPlayer) {
				cell.addMass(this.mass);
				this.die = true;
			}
		}
		if (isShot) {
			dx = (speed) * Math.cos(angle);
			dy = (speed) * Math.sin(angle);
			x += dx;
			y += dy;
			speed -= 0.1;
			if (speed <= 0) {
				isShot = false;
				speed = 0;
			}
		}
	}

	private boolean checkCollide(double x, double y, double mass) {
		return x < this.x + 10 && x + mass > this.x && y < this.y + 10 && y + mass > this.y;
	}

	public void Draw(Graphics bbg) {
		bbg.setColor(color);
		bbg.fillRect(x, y, 10, 10);
		bbg.drawRect(x, y, 10, 10);
	}

	public boolean getHealth() {
		return die;
	}

}