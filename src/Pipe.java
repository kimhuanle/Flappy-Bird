import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Pipe {
	public static final int HOLE_WIDTH = 150;
	Image pipeup;
	Image pipedown;
	int x, yTop, yBot, origin;
	private int width, height, yHole, v;
	public static boolean collided;

	public Pipe(int x) {
		try {
			pipedown = ImageIO.read(new File("images//pipe-down.png"));
			pipeup = ImageIO.read(new File("images//pipe-up.png"));
		} catch (IOException e) {
			System.out.println("No pipe images found");
		}
		height = pipedown.getHeight(null);
		width = pipedown.getWidth(null);
		origin = x;
		reset();
	}

	public void reset() {
		yHole = (int) (Math.random() * 310 + 50);
		this.x = MainWindow.WIDTH + origin;
		yTop = yHole - height;
		yBot = yHole + HOLE_WIDTH;
		v = 5;
	}

	public void draw(Graphics g, JPanel panel, Pipe before) {
		if (!collided && !MainWindow.gameOver) {
			x -= v;
			if (x <= 0 - width) {
				x = before.x + 200;
				yHole = (int) (Math.random() * 300 + 50);
				yTop = yHole - height;
				yBot = yHole + HOLE_WIDTH;
			}
		}
		g.drawImage(pipedown, x, yTop, panel);
		g.drawImage(pipeup, x, yBot, panel);
	}

	public boolean collided(Bird b) {
		int xb = (int) b.x;
		int yb = (int) b.y;
		int wb = (int) b.image.getWidth(null);
		int hb = (int) b.image.getHeight(null);
		if ((xb + wb - 3 >= x && xb + 3 <= x + width) && (yb + 3 <= yHole || yb + hb - 3 >= yBot))
			return true;
		return false;
	}

	
}

