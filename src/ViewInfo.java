
public class ViewInfo 
{
	final int SCREEN_WIDTH, SCREEN_HEIGHT;
	double viewX, viewY, scale;
	public ViewInfo(double viewX, double viewY, double scale, int SCREEN_WIDTH, int SCREEN_HEIGHT)
	{
		this.viewX = viewX;
		this.viewY = viewY;
		this.SCREEN_WIDTH = SCREEN_WIDTH;
		this.SCREEN_HEIGHT = SCREEN_HEIGHT;
		this.scale = scale;
	}
	public ViewInfo(double scale, int SCREEN_WIDTH, int SCREEN_HEIGHT)
	{
		this(SCREEN_WIDTH*scale/2, SCREEN_HEIGHT*scale/2, scale, SCREEN_WIDTH, SCREEN_HEIGHT);
	}
	public int reverseX(double x)
	{
		return (int)(((x-SCREEN_WIDTH/2)/scale)+viewX);
	}
	public int reverseY(double y)
	{
		return (int)(((y-SCREEN_HEIGHT/2)/scale)+viewY);
	}
	public int shiftedX(double x)
	{
		//return (int)(x-viewX+viewWidth/2);
		return (int)(((x-viewX)*scale)+SCREEN_WIDTH/2);
	}
	public int shiftedY(double y)
	{
		//return (int)(y-viewY+viewHeight/2);
		return (int)(((y-viewY)*scale)+SCREEN_HEIGHT/2);
	}
	public void fixView(int GAME_WIDTH, int GAME_HEIGHT)
	{
		if (viewX < SCREEN_WIDTH/scale/2)
		{
			viewX = SCREEN_WIDTH/scale/2;
		}
		else if (viewX > GAME_WIDTH-SCREEN_WIDTH/scale/2)
		{
			viewX = GAME_WIDTH-SCREEN_WIDTH/scale/2;
		}
		if (viewY < SCREEN_HEIGHT/scale/2)
		{
			viewY = SCREEN_HEIGHT/scale/2;
		}
		else if (viewY > GAME_HEIGHT-SCREEN_HEIGHT/scale/2)
		{
			viewY = GAME_HEIGHT-SCREEN_HEIGHT/scale/2;
		}
	}
}
