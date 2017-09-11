import javax.swing.JFrame;
import java.util.Timer;
import java.util.TimerTask;
public class GameRunner 
{
	public static void main(String[] args)
	{
		final int WIDTH = 1024;
		final int HEIGHT = 768;
		
		SpaceGame game = new SpaceGame(WIDTH,HEIGHT);
		Timer timer = new Timer();
		JFrame frame = new JFrame("Lost in Space");
		
		frame.add(game);
		frame.setSize(WIDTH,HEIGHT);
		frame.setResizable(false);
		frame.setVisible(true);
		game.createOffscreenImage();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		timer.schedule(new Loop(game), 0, 1000/60);
	}
}

class Loop extends TimerTask
{
	SpaceGame game;
	public Loop(SpaceGame game)
	{
		this.game = game;
	}
	public void run()
	{
		game.update();
		game.repaint();
	}
	
}
