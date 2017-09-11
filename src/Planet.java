import java.awt.Color;
import java.awt.Graphics;


public class Planet extends GravityObject
{
	boolean homePlanet;
	String planetName;
	int alienNum;
	public Planet(double x, double y, double radius, Color color, double density) 
	{
		this(x,y,radius,color,density,"unnamed");
	}
	public Planet(double x, double y, double radius, Color color, double density, String name) 
	{
		super(x, y, radius, color, density);
		homePlanet = false;
		this.planetName = name;
	}
}
