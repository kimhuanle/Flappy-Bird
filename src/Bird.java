import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Bird {

	double x, y, start, v, gravity;
	int width;
	int height;
	Image image;
	private int angle, imgCount;
	private Image[] img;
	private static int MAX_ROTATION = -20;
	private static int ANGLE_VELOCITY = 15;
	private static int ANIMATION = 2;

	public Bird() {
		try {
			img = new Image[] { ImageIO.read(new File("images//bird-downflap.png")),
					ImageIO.read(new File("images//bird.png")), ImageIO.read(new File("images//bird-upflap.png")) };
		} catch (IOException e) {
			System.out.println("No bird images found!");
		}
		width = img[0].getWidth(null);
		height = img[0].getHeight(null);
		x = MainWindow.WIDTH / 2 - width / 2;
		y = MainWindow.HEIGHT / 2 - height / 2 - 50;
		start = y;
		v = 1;
		gravity = 2.3;
	}

	public void move() {
		y += v;
		v += gravity;
		if (y < start) {
			angle -= ANGLE_VELOCITY;
			if (angle < MAX_ROTATION)
				angle = MAX_ROTATION;
		} else {
			if (angle < 90)
				angle += ANGLE_VELOCITY;
			else
				angle = 90;
		}
		if (y >= MainWindow.HEIGHT - 170 - image.getHeight(null)) {
			y = MainWindow.HEIGHT - 170 - image.getHeight(null);
			MainWindow.gameOver = true;
		}
	}

	public void jump() {
		v = -17;
		start = y;
	}

	public void start() {
		y += v;
		if ((y - start) >= 6 || (y - start) <= -6)
			v *= -1;
	}

	public void draw(Graphics g, JPanel panel) throws NullPointerException {
		imgCount += 1;
		image = img[1];
		if (!MainWindow.gameOver) {
			if (imgCount < ANIMATION)
				image = img[0];
			else if (imgCount < ANIMATION * 2)
				image = img[1];
			else if (imgCount < ANIMATION * 3)
				image = img[2];
			else if (imgCount < ANIMATION * 4)
				image = img[1];
			else if (imgCount < ANIMATION * 5)
				image = img[0];
			if (imgCount == ANIMATION * 5)
				imgCount = 0;
			if (angle <= -80) {
				image = img[1];
				imgCount = ANIMATION * 2;
			}
		}
		width = image.getWidth(null);
		height = image.getHeight(null);
		if (!MainWindow.flied && !MainWindow.gameOver)
			start();
		Graphics2D g2 = (Graphics2D) g;
		AffineTransform oldTransform = g2.getTransform();
		AffineTransform newTransform = (AffineTransform) oldTransform.clone();
		newTransform.rotate(Math.toRadians(angle), x + width / 2, y + height / 2);
		g2.setTransform(newTransform);
		g2.drawImage(image, (int) x, (int) y, width, height, panel);
		g2.setTransform(oldTransform);
	}
	
	public boolean updateScore(Pipe p) {
		return x > p.x && x < p.x + 6;
	}

}
