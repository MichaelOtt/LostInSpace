import java.awt.Graphics;


public class Drawable 
{
	double x, y;
	int drawX, drawY;
	double drawRadius;
	public Drawable(double x, double y, double drawRadius)
	{
		setX(x);
		setY(y);
		this.drawRadius = drawRadius;
	}
	public void setX(double x)
	{
		this.x = x;
	}
	public void setY(double y)
	{
		this.y = y;
	}
	protected void viewShift(ViewInfo view)
	{
		drawX = view.shiftedX(x);
		drawY = view.shiftedY(y);
	}
	protected boolean shouldDraw(ViewInfo view)
	{
		if (drawX < -drawRadius*view.scale || drawX > view.SCREEN_WIDTH + drawRadius*view.scale ||
			drawY < -drawRadius*view.scale || drawY > view.SCREEN_HEIGHT + drawRadius*view.scale)
		{
			return false;
		}
		return true;
	}
	public void draw(Graphics g, ViewInfo view)
	{

	}
}
