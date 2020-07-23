import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

public class MainWindow extends JPanel implements MouseListener, KeyListener {

	/**
	 * This is a default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	public static int WIDTH = 400, HEIGHT = 711;
	private JFrame win;
	private Font font, font1;
	private Bird b;
	Image background, textImage, play, base1, base2, message, gameover, black, scoreBoard, gold, silver, bronze,
			diamond, newBest;
	AudioInputStream audio;
	Clip clip;
	private int score = 0, bestScore = 0, xBase1, xBase2, xPlay, yPlay, wait = 0;
	private Pipe[] pipes = new Pipe[3];
	public static boolean started = false, flied = false, blacked = false, gameOver = false, newBestScore = false,
			playdie = false;
	private Timer timer;

	public static void main(String args[]) {
		MainWindow win = new MainWindow();
		win.timer.setRepeats(true);
		win.timer.start();
	}

	public MainWindow() {
		try {
			// create the font to use. Specify the size!
			font = Font.createFont(Font.TRUETYPE_FONT, new File("font//flappy-bird-font.ttf")).deriveFont(45f);
			font1 = Font.createFont(Font.TRUETYPE_FONT, new File("font//flappy-bird-font1.ttf")).deriveFont(45f);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			// register the font
			ge.registerFont(font);
		} catch (IOException e) {
		} catch (FontFormatException e) {
		}

		try {
			Scanner input = new Scanner(new File("score.txt"));
			if (input.hasNext())
				bestScore = input.nextInt();
			input.close();
		} catch (IOException ex) {
		}

		win = new JFrame();
		try {
			win.setIconImage(ImageIO.read(new File("images//game-icon.jpg")));
			background = ImageIO.read(new File("images//background.png"));
			textImage = ImageIO.read(new File("images//flappy-bird.png"));
			play = ImageIO.read(new File("images//play1.png"));
			base1 = ImageIO.read(new File("images//base1.png"));
			base2 = ImageIO.read(new File("images//base1.png"));
			message = ImageIO.read(new File("images//message.png"));
			gameover = ImageIO.read(new File("images//gameover.png"));
			scoreBoard = ImageIO.read(new File("images//score.png"));
			bronze = ImageIO.read(new File("images//bronze.png"));
			silver = ImageIO.read(new File("images//silver.png"));
			gold = ImageIO.read(new File("images//gold.png"));
			diamond = ImageIO.read(new File("images//diamond.png"));
			black = ImageIO.read(new File("images//black.png"));
			newBest = ImageIO.read(new File("images//new.png"));
		} catch (IOException e) {
			System.out.println("No images file found");
		}
		timer = new Timer(1000 / 30, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				repaint();
			}
		});

		win.add(this);
		win.setTitle("Flappy Bird");
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		win.setSize(WIDTH, HEIGHT);
		win.setResizable(false);
		win.setLocation(dim.width / 2 - win.getSize().width / 2, dim.height / 2 - win.getSize().height / 2);
		win.setVisible(true);
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addMouseListener(this);
		win.addKeyListener(this);
		b = new Bird();
		xBase1 = 0;
		xBase2 = xBase1 + WIDTH;
		xPlay = WIDTH / 2 - play.getWidth(this) / 2 - 5;
		yPlay = HEIGHT - base1.getHeight(this) - play.getHeight(this) - 10;
		for (int i = 0; i < pipes.length; i++)
			pipes[i] = new Pipe((i + 1) * 200);
	}

	public void paintComponent(Graphics g) throws NullPointerException {
		g.setFont(font);
		g.drawImage(background, 0, 0, this);

		// Introduction of Game
		if (!started) {
			g.drawImage(textImage, WIDTH / 2 - textImage.getWidth(this) / 2, HEIGHT / 4 - textImage.getHeight(this) / 2,
					this);
			g.drawImage(play, xPlay, yPlay, this);
		}

		// Instruction for playing game after hitting play button
		if (started && !flied && !gameOver)
			g.drawImage(message, (WIDTH - message.getWidth(this)) / 2, 190, this);

		// Game started
		if (flied) {
			b.move();
			for (int i = 0; i < pipes.length; i++) {
				if (!Pipe.collided && b.updateScore(pipes[i])) {
					play("sounds//score.wav");
					score += 1;
				}
			}
			for (int i = pipes.length; i < pipes.length * 2; i++)
				pipes[i % pipes.length].draw(g, this, pipes[(i - 1) % pipes.length]);
			if (pipes[0].collided(b) || pipes[1].collided(b) || pipes[2].collided(b)) {
				if (!Pipe.collided)
					play("sounds//hit.wav");
				Pipe.collided = true;
			}
			String sc = Integer.toString(score);
			int charwidth = g.getFontMetrics().stringWidth(sc);
			g.setColor(Color.BLACK);
			g.drawString(sc, (WIDTH - charwidth) / 2, 120);
			g.setFont(font1);
			g.setColor(Color.WHITE);
			g.drawString(sc, (WIDTH - charwidth) / 2, 120);
		}

		// GameOver
		if (gameOver) {
			if (!playdie) {
				play("sounds//die.wav");
				playdie = true;
			}
			b.move();
			for (int i = pipes.length; i < pipes.length * 2; i++) {
				pipes[i % pipes.length].draw(g, this, pipes[(i - 1) % pipes.length]);
			}
			int yplay = HEIGHT - base1.getHeight(this) - play.getHeight(this) - 10;
			g.drawImage(gameover, (WIDTH - gameover.getWidth(this)) / 2, 150, this);
			g.drawImage(scoreBoard, (WIDTH - scoreBoard.getWidth(this)) / 2, yplay - 10 - scoreBoard.getHeight(this),
					this);
			if (score >= 40)
				g.drawImage(diamond, (WIDTH - scoreBoard.getWidth(this)) / 2 + 37,
						yplay - 10 - scoreBoard.getHeight(this) + 64, this);
			else if (score >= 30)
				g.drawImage(gold, (WIDTH - scoreBoard.getWidth(this)) / 2 + 34,
						yplay - 10 - scoreBoard.getHeight(this) + 65, this);
			else if (score >= 20)
				g.drawImage(silver, (WIDTH - scoreBoard.getWidth(this)) / 2 + 41,
						yplay - 10 - scoreBoard.getHeight(this) + 62, this);
			else if (score >= 10)
				g.drawImage(bronze, (WIDTH - scoreBoard.getWidth(this)) / 2 + 38,
						yplay - 10 - scoreBoard.getHeight(this) + 54, this);
			String sc = Integer.toString(score);
			if (score > bestScore) {
				bestScore = score;
				try {
					PrintWriter writer = new PrintWriter("score.txt", "UTF-8");
					writer.print(Integer.toString(bestScore));
					writer.close();
					System.out.println("Successfully wrote to the file.");
				} catch (IOException e) {
					System.out.println("An error occurred.");
					e.printStackTrace();
				}
				newBestScore = true;
			}
			if (newBestScore) {
				g.drawImage(newBest, WIDTH - (WIDTH - scoreBoard.getWidth(this)) / 2 - 92 - newBest.getWidth(this),
						yplay - 10 - scoreBoard.getHeight(this) + 92, this);
			}
			g.setFont(font.deriveFont(30f));
			g.setColor(Color.BLACK);
			String bs = Integer.toString(bestScore);
			int scorewidth = g.getFontMetrics().stringWidth(sc);
			int bestscorewidth = g.getFontMetrics().stringWidth(bs);
			g.drawString(sc, WIDTH - (WIDTH - scoreBoard.getWidth(this)) / 2 - 35 - scorewidth,
					yplay - 10 - scoreBoard.getHeight(this) + 85);
			g.drawString(bs, WIDTH - (WIDTH - scoreBoard.getWidth(this)) / 2 - 35 - bestscorewidth,
					yplay - 10 - scoreBoard.getHeight(this) + 150);
			g.setFont(font1.deriveFont(30f));
			g.setColor(Color.WHITE);
			g.drawString(sc, WIDTH - (WIDTH - scoreBoard.getWidth(this)) / 2 - 35 - scorewidth,
					yplay - 10 - scoreBoard.getHeight(this) + 85);
			g.drawString(bs, WIDTH - (WIDTH - scoreBoard.getWidth(this)) / 2 - 35 - bestscorewidth,
					yplay - 10 - scoreBoard.getHeight(this) + 150);
			g.drawImage(play, xPlay, yPlay, this);
			flied = false;
		}
		b.draw(g, this);

		// Base moving
		if (!Pipe.collided && !gameOver) {
			xBase1 -= 5;
			xBase2 -= 5;
			if (xBase1 <= -base1.getWidth(this))
				xBase1 = xBase2 + base2.getWidth(this);
			if (xBase2 <= -base2.getWidth(this))
				xBase2 = xBase1 + base1.getWidth(this);
		}
		g.drawImage(base1, xBase1, HEIGHT - base1.getHeight(this), this);
		g.drawImage(base2, xBase2, HEIGHT - base2.getHeight(this), this);

		if (blacked) {
			wait++;
			g.drawImage(black, 0, 0, this);
			if (wait == 20) {
				blacked = !blacked;
				wait = 0;
			}
			newBestScore = false;
		}
	}

	public void mouseClicked(MouseEvent e) {
		if (started && !blacked && !gameOver) {
			flied = true;
		}
		if (flied && !Pipe.collided) {
			if (b.y > b.width) {
				play("sounds//wing.wav");
				b.jump();
			}
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if (!started || gameOver) {
			int x = e.getX();
			int y = e.getY();
			if (x > xPlay + 5 && x < xPlay + play.getWidth(this) - 5 && y > yPlay + 3
					&& y < yPlay + play.getWidth(this) - 3) {
				yPlay = HEIGHT - base1.getHeight(this) - play.getHeight(this) - 10 + 5;
			} else
				yPlay = HEIGHT - base1.getHeight(this) - play.getHeight(this) - 10;
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (!started || gameOver) {
			int x = e.getX();
			int y = e.getY();
			yPlay = HEIGHT - base1.getHeight(this) - play.getHeight(this) - 10;
			if (x > xPlay + 5 && x < xPlay + play.getWidth(null) - 5 && y > yPlay + 3
					&& y < yPlay + play.getHeight(null) - 3) {
				play("sounds//button.wav");
				started = true;
				blacked = true;
				b = new Bird();
				b.y = HEIGHT / 2 - b.height / 2 - 50;
				b.x = WIDTH / 4;
				for (int i = 0; i < pipes.length; i++)
					pipes[i].reset();
				gameOver = false;
				playdie = false;
				Pipe.collided = false;
			}
		}
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			if (!started || gameOver) {
				yPlay = HEIGHT - base1.getHeight(this) - play.getHeight(this) - 10 + 5;
			}
			e.setKeyCode(KeyEvent.VK_UP);
		}
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			if (started && !blacked && !gameOver) {
				flied = true;
			}
			if (flied && !Pipe.collided) {
				if (b.y > b.width) {
					play("sounds//wing.wav");
					b.jump();
				}
			}
		}
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
			if (!started || gameOver) {
				play("sounds//button.wav");
				yPlay = HEIGHT - base1.getHeight(this) - play.getHeight(this) - 10;
				started = true;
				blacked = true;
				b = new Bird();
				b.y = HEIGHT / 2 - b.height / 2 - 50;
				b.x = WIDTH / 4;
				for (int i = 0; i < pipes.length; i++)
					pipes[i].reset();
				gameOver = false;
				playdie = false;
				Pipe.collided = false;
				score = 0;
			}
	}

	public void keyTyped(KeyEvent arg0) {
	}

	private void play(String path) {
		try {
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(new File(path)));
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
