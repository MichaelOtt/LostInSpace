import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;


public class ProjectileIndicator 
{
	double speed;
	double angle;
	int stepsPerDot;
	int dotNum;
	int dotRadius;
	int x;
	int y;
	int displayThrough;
	Color color;
	CircleObject[] dots;
	public ProjectileIndicator(int x, int y, int stepsPerDot, int dotNum, int dotRadius, double angle, double speed, Color color)
	{
		this(stepsPerDot, dotNum, dotRadius, speed, color);
		this.x = x;
		this.y = y;
		this.angle = angle;
	}
	public ProjectileIndicator(int stepsPerDot, int dotNum, int dotRadius, double speed, Color color)
	{
		this.dotNum = dotNum;
		this.stepsPerDot = stepsPerDot;
		this.dotRadius = dotRadius;
		this.color = color;
		this.speed = speed;
		dots = new CircleObject[dotNum];
	}
	public void drawDots(Graphics g, ViewInfo view)
	{
		for (int i = 0; i < displayThrough; i++)
		{
			dots[i].draw(g,view);
		}
	}
	public void updateInfo(Player player, int mousex, int mousey)
	{
		double dx = mousex-player.x;
		double dy = mousey-player.y;
		angle = Math.atan2(dy,dx);
		this.x = (int)player.x;
		this.y = (int)player.y;
	}
	public void initializeDots()
	{
		for (int i = 0; i < dotNum; i++)
		{
			dots[i] = new CircleObject(0,0,dotRadius,color);
		}
	}
	public void updateDots(ArrayList<GravityObject> gravObjects)
	{
		Projectile p = new Projectile(x,y,speed,angle,dotRadius,color, true, 10);
		for (int i = 0; i < dotNum*stepsPerDot; i++)
		{
			for (int j = 0; j < gravObjects.size(); j++)
			{
				gravObjects.get(j).pullPhysObject(p);
			}
			p.update();
			for (int j = 0; j < gravObjects.size(); j++)
			{
				if (p.collision(gravObjects.get(j), 0))
				{
					displayThrough = i/stepsPerDot;
					return;
				}
			}
			if (i%stepsPerDot == stepsPerDot-1)
			{
				dots[i/stepsPerDot].x = p.x;
				dots[i/stepsPerDot].y = p.y;
			}
		}
	}
}
