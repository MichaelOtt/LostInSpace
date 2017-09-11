import java.awt.Color;


public class ItemDrop extends PlanetLander
{
	Item item;
	itemType type;
	enum itemType
	{
		Weapon,
		ExtraLife
	}
	public ItemDrop(double angle, Planet currentPlanet, Item item, itemType type)
	{	
		super(angle,7, Color.blue, currentPlanet, 0);
		this.item = item;
		this.type = type;
		if (type == itemType.Weapon)
		{
			color = Color.cyan;
		}
		if (type == itemType.ExtraLife)
		{
			color = Color.pink;
		}
	}
	
}
