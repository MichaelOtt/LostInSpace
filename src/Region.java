
public class Region 
{
	double x, y, radius;
	double difficulty;
	static double maxDiff;
	public Region(double x, double y, double radius, double difficulty)
	{
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.difficulty = difficulty;
	}
	public double diff(Planet p)
	{
		double dx = p.x-this.x;
		double dy = p.y-this.y;
		double d = Math.sqrt(dx*dx+dy*dy);
		if (radius > d)
		{
			return difficulty*(radius-d)/radius;
		}
		else
		{
			return 0;
		}
	}
}
