import java.util.ArrayList;


public class Item 
{
	ArrayList<String> info;
	boolean active;
	public Item(boolean active)
	{
		this.active = active;
		info = new ArrayList<String>();
	}
	public ArrayList<String> getInfo()
	{
		return info;
	}
}
