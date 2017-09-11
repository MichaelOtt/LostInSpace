import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.HashMap;


public class Projectile extends CircleObject implements PhysicsObject
{
	double vx, vy;
	boolean physical;
	int ticksLeft;
	public Projectile(double x, double y, double v, double angle, double radius, Color color, boolean physical, int ticks) 
	{
		super(x, y, radius, color);
		vx = v*Math.cos(angle);
		vy = v*Math.sin(angle);
		this.physical = physical;
		this.ticksLeft = ticks;
	}
	public void update() 
	{
		setX(x+vx);
		setY(y+vy);
		ticksLeft--;
	}
	public void accel(double ax, double ay) 
	{
		if (physical)
		{
			vx += ax;
			vy += ay;
		}
	}
	public double getX() 
	{
		return x;
	}

	public double getY() 
	{
		return y;
	}
}
