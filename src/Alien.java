import java.awt.Color;


public abstract class Alien extends PlanetLander
{
	final static int ALIEN_R = 5;
	public Alien(double angle, Planet currentPlanet, double radius)
	{
		super(angle, radius, Color.red, currentPlanet, 3.1);
	}
	public Alien(double angle, Planet currentPlanet)
	{
		super(angle, ALIEN_R, Color.red, currentPlanet, 3.1);
	}
	public void update()
	{
		
	}
}
