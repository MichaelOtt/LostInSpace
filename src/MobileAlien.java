import java.util.ArrayList;

public class MobileAlien extends Alien
{
	final static int ALIEN_R = 5;
	public MobileAlien(double angle, Planet currentPlanet) 
	{
		super(angle, currentPlanet, ALIEN_R);
	}
	public void update(Player player)
	{
		if (currentPlanet == player.currentPlanet)
		{
			double angle = angleTo(player);
			if (angle < 0)
			{
				rotate(-1);
			}
			else
			{
				rotate(1);
			}
		}
	}
}
