import java.awt.Color;
import java.awt.Graphics;


public class GravityObject extends CircleObject
{
	final static double G = 0.2;
	double mass;
	double density;
	double effectiveR;
	public GravityObject(double x, double y) 
	{
		this(x,y,5);
	}
	public GravityObject(double x, double y, double radius)
	{
		this(x,y,radius,Color.red);
	}
	public GravityObject(double x, double y, double radius, Color color)
	{
		this(x,y,radius,color,1);
	}
	public GravityObject(double x, double y, double radius, Color color, double density)
	{
		super(x,y,radius,color);
		this.density = density;
		mass = density*Math.PI*radius*radius;
		//effectiveR = Math.sqrt(mass*G)*3;
		effectiveR = radius + 100;
		found = false;
	}
	public void pullPhysObject(PhysicsObject p)
	{
		double dx = x-p.getX();
		double dy = y-p.getY();
		double r = Math.sqrt(dx*dx+dy*dy);
		if (r < effectiveR)
		{
			double tempMass = mass;
			if (r < radius)
			{
				tempMass = mass*r*r/(radius*radius);
			}
			double a = tempMass*G/(r*r);
			p.accel(dx/r*a, dy/r*a);
		}
	}
	public void draw(Graphics g, ViewInfo view)
	{
		//g.setColor(Color.white);
		super.draw(g,view);
		//g.drawOval((int)(drawX-effectiveR*view.scale),(int)(drawY-effectiveR*view.scale),(int)(effectiveR*2*view.scale),(int)(effectiveR*2*view.scale));
	}
}
