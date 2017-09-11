import java.awt.Color;
import java.awt.Graphics;

public class CircleObject extends Drawable
{
	double radius;
	Color color;
	boolean found;
	boolean notOnMap;
	public CircleObject(double x, double y) 
	{
		this(x,y,5);
	}
	public CircleObject(double x, double y, double radius)
	{
		this(x,y,radius,Color.red);
	}
	public CircleObject(double x, double y, double radius, Color color)
	{
		super(x,y,radius);
		this.radius = radius;
		this.color = color;
		found = false;
		notOnMap = false;
	}
	public boolean collision(CircleObject o, double buffer)
	{
		double dx = x-o.x;
		double dy = y-o.y;
		if (Math.sqrt(dx*dx+dy*dy) <= (o.radius+radius)+buffer)
		{
			return true;
		}
		return false;
	}
	public void draw(Graphics g, ViewInfo view)
	{
		viewShift(view);
		if (shouldDraw(view))
		{
			g.setColor(color);
			g.fillOval((int)(drawX-radius*view.scale),(int)(drawY-radius*view.scale),(int)(radius*2*view.scale),(int)(radius*2*view.scale));
			if (!found)
			{
				found = true;
				notOnMap = true;
			}
		}
	}

}
