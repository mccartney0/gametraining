package graficos;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable {

	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	private final int WIDTH = 240;
	private final int HEIGHT = 160;
	private final int SCALE = 3;

	private BufferedImage image;

	private SpriteSheet sheet;
	private BufferedImage[] player;
	private int frames = 0;
	private int maxFrames = 12;
	private int curAnimation = 0,maxAnimation =3;

	public Game() throws IOException {
		sheet = new SpriteSheet("/spritesheet.png");
		player = new BufferedImage[4];
		player[0] = sheet.getSprite(0, 0, 16, 16);
		player[1] = sheet.getSprite(16, 0, 16, 16);
		player[2] = sheet.getSprite(32, 0, 16, 16);
		player[3] = sheet.getSprite(48, 0, 16, 16);
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		initFrame();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_BGR);
	}

	public void initFrame() {
		frame = new JFrame("Firt Gamer");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}

	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		Game game = new Game();
		game.start();
	}

	public void update() {

		frames++;
		if (frames > maxFrames) {
			frames = 0;
			curAnimation++;
			if (curAnimation > maxAnimation) {
				curAnimation = 0;
			}
		}
	}

	public void render() { // Renderização funciona por ordem de código, primeira linhas, segunda, etc...

		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = image.getGraphics();
		g.setColor(new Color(45, 26, 42));
		g.fillRect(0, 0, WIDTH, HEIGHT);

//		Renderizar jogo

		Graphics2D g2 = (Graphics2D) g;
//		g2.rotate(Math.toRadians(90),90+8,90+8); ROTACIONAR

//		g2.setColor(new Color(0,0,0,120));  // Escurecendo RGBS
		g2.fillRect(0, 0, WIDTH, HEIGHT);
		g.drawImage(player[curAnimation], 20, 20, null);
//		
		g.dispose();
//		g.setColor(Color.CYAN);
//		g.fillRect(10, 0, 10, 10);
//		g.setFont(new Font("Arial",Font.BOLD,20));
//		g.setColor(Color.white);
//		g.drawString("Olá mundo", 20,20);

		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		bs.show();
	}

	@Override
	public void run() {

		long lastTime = System.nanoTime();
		double amountOfUpdates = 60.0;
		double ns = 1000000000 / amountOfUpdates;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();

		while (isRunning) {

			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;

			if (delta >= 1) {
				update();
				render();
				frames++;
				delta--;
			}

			if (System.currentTimeMillis() - timer >= 1000) {
				System.out.println("FPS: " + frames);
				frames = 0;
				timer += 1000;
			}

		}

		stop();
	}

}
