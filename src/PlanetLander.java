import java.awt.Color;


public class PlanetLander extends CircleObject
{
	
	Planet currentPlanet;
	double rotSpeed;
	boolean onPlanet;
	double angle;
	public PlanetLander(double x, double y, double radius, Color color)
	{
		super(x,y,radius,color);
		currentPlanet = null;
		onPlanet = false;
		rotSpeed = 5;
	}
	public PlanetLander(double x, double y, double radius, Color color, double rotSpeed)
	{
		super(x,y,radius,color);
		currentPlanet = null;
		onPlanet = false;
		this.rotSpeed = rotSpeed;
	}
	public PlanetLander(double x, double y, double radius, Color color, Planet currentPlanet, double rotSpeed) 
	{
		super(x, y, radius, color);
		this.currentPlanet = currentPlanet;
		onPlanet = true;
		this.rotSpeed = rotSpeed;
		angleFromXY();
	}
	public PlanetLander(double angle, double radius, Color color, Planet currentPlanet, double rotSpeed)
	{
		super(0,0,radius,color);
		this.currentPlanet = currentPlanet;
		this.angle = angle;
		onPlanet = true;
		this.rotSpeed = rotSpeed;
		xyFromAngle();
	}
	public void angleFromXY()
	{
		double dx = x-currentPlanet.x;
		double dy = y-currentPlanet.y;
		angle = Math.atan2(dy,dx);
	}
	public void xyFromAngle()
	{
		setX((radius+currentPlanet.radius)*Math.cos(angle)+currentPlanet.x);
		setY((radius+currentPlanet.radius)*Math.sin(angle)+currentPlanet.y);
	}
	public void rotate(double direction)//1 = counterclockwise rotation, -1 = clockwise rotation, between is intermediate speed
	{
		if (onPlanet)
		{
			angle += direction*rotSpeed/currentPlanet.radius;
			xyFromAngle();
		}
	}
	public double angleTo(PlanetLander p)
	{
		double tempA = actualAngle();
		double pTempA = p.actualAngle();
		double dA1 = pTempA-tempA;
		double dA2 = Math.PI*2-Math.abs(dA1);
		if (dA1 > 0)
		{
			dA2 *= -1;
		}
		if (Math.abs(dA1) < Math.abs(dA2))
		{
			return dA1;
		}
		else
		{
			return dA2;
		}
	}
	public double actualAngle()
	{
		if (angle > Math.PI*2)
		{
			return angle%(Math.PI*2);
		}
		else if (angle < -Math.PI*2)
		{
			return angle%(Math.PI*2)+Math.PI*2;
		}
		else
		{
			if (angle < 0)
			{
				return 2*Math.PI + angle;
			}
			else
			{
				return angle;
			}
		}
	}
}
