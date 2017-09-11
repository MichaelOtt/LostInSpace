import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;


public class Hud 
{
	//ArrayList<GravityObject> gravObjects;
	ArrayList<CircleObject> circObjects;
	ArrayList<Region> regions;
	Player player;
	final int GAME_WIDTH;
	final int GAME_HEIGHT;
	final int SCREEN_WIDTH;
	final int SCREEN_HEIGHT;
	final int PLAYER_RADIUS = 5;
	final int ITEM_RADIUS = 5;
	final int BLINK_PERIOD = 50;
	final int METER_WIDTH;
	final int METER_HEIGHT = 20;
	final int COOLDOWN_HEIGHT = 5;
	final int MESSAGE_HEIGHT = 110;
	final int MESSAGE_WIDTH = 250;
	final int MESSAGE_X = 35;
	double messageY;
	int playerBlink;
	double scale;
	int deaths;
	int WEAPON_DISPLAY_TIME = 100;
	int weaponDisplayTicks;
	public Hud(ArrayList<Region> regions, Player player, int GAME_WIDTH, int GAME_HEIGHT, int SCREEN_WIDTH, int SCREEN_HEIGHT)
	{
		circObjects = new ArrayList<CircleObject>();
		this.regions = regions;
		this.player = player;
		this.GAME_WIDTH = GAME_WIDTH;
		this.GAME_HEIGHT = GAME_HEIGHT;
		this.SCREEN_WIDTH = SCREEN_WIDTH;
		this.SCREEN_HEIGHT = SCREEN_HEIGHT;
		this.METER_WIDTH = SCREEN_WIDTH-100;
		playerBlink = 0;
		messageY = -MESSAGE_HEIGHT;
		deaths = 0;
		weaponDisplayTicks = 0;
		setScale();
	}
	public void resetHud(Player player)
	{
		this.player = player;
		playerBlink = 0;
		messageY = -MESSAGE_HEIGHT;
		deaths++;
	}
	public void updateHud()
	{
		if (weaponDisplayTicks > 0)
		{
			weaponDisplayTicks--;
		}
	}
	public void updateMap(ArrayList<? extends CircleObject> cos)
	{
		for (int i = 0; i < cos.size(); i++)
		{
			CircleObject o = cos.get(i);
			if (o.found && o.notOnMap)
			{
				if (!alreadyMapped(o))
				{
					circObjects.add(o);
				}
				o.notOnMap = false;
			}
		}
	}
	private boolean alreadyMapped(CircleObject o)
	{
		for (int i = 0; i < circObjects.size(); i++)
		{
			if (sameCirc(o,circObjects.get(i)))
			{
				return true;
			}
		}
		return false;
	}
	private boolean sameCirc(CircleObject o1, CircleObject o2)
	{
		return o1.x == o2.x && o1.y == o2.y && o1.radius == o2.radius;
	}
	public void setScale()
	{
		double xScale = (double)SCREEN_WIDTH/(double)GAME_WIDTH;
		double yScale = (double)SCREEN_HEIGHT/(double)GAME_HEIGHT;
		if (xScale < yScale)
		{
			scale = xScale;
		}
		else
		{
			scale = yScale;
		}
	}
	public void displayHud(Graphics g)
	{
		playerBlink++;
		displayLaunchMeter(g);
		displayMessageBox(g);
	}
	public void startWeaponDisplayTimer()
	{
		weaponDisplayTicks = WEAPON_DISPLAY_TIME;
	}
	public boolean displayWeapons()
	{
		return weaponDisplayTicks > 0;
	}
	private void displayItemInfo(double x, double y, Item item, Graphics g)
	{
		ArrayList<String> itemInfo;
		if (item != null)
		{
			itemInfo = item.getInfo();
		}
		else
		{
			itemInfo = new ArrayList<String>();
			itemInfo.add("Weapon: none");
		}
		Color tempColor = new Color(127,127,127,100);
		g.setColor(tempColor);
		g.fillRect((int)x, (int)y, MESSAGE_WIDTH, (itemInfo.size()+1)*20);
		
		tempColor = new Color(0,0,0);
		g.setColor(tempColor);
		for (int j = 0; j < itemInfo.size(); j++)
		{
			g.drawString(itemInfo.get(j), (int)(x+MESSAGE_X), (int)(y+20*(j+1)));
		}
		if (item instanceof Weapon && item != null)
		{
			drawAmmoBar(g,x+MESSAGE_WIDTH-20,y+10,15,(itemInfo.size())*20,((Weapon)item).ammo,((Weapon)item).getStartAmmo());
		}
	}
	private void drawAmmoBar(Graphics g, double x, double y, double w, double h, int ammo, int startAmmo)
	{
		Color tempColor = new Color(0,0,0,100);
		g.setColor(tempColor);
		g.drawRect((int)x, (int)y, (int)w, (int)h);
		
		double percent = (double)ammo/(double)startAmmo;
		tempColor = new Color((int)(255*(1-percent)),(int)(255*(percent)),0,100);
		g.setColor(tempColor);
		g.fillRect((int)x, (int)(y+h*(1-percent)),(int)w,(int)(h*percent));
	}
	public void displayItemDropInfo(Graphics g, ItemDrop itemdrop, ViewInfo view)
	{
		double x = view.shiftedX(itemdrop.x);
		double y = view.shiftedY(itemdrop.y);
		displayItemInfo(x,y,itemdrop.item,g);
	}
	public void displayItems(Graphics g)
	{
		
		double passiveY = 0;
		displayDeaths(g);
		for (int i = 0; i < player.passiveItems.size(); i++)
		{
			Item tempItem = player.passiveItems.get(i);
			ArrayList<String> itemInfo = tempItem.getInfo();
			double boxY = passiveY;
			double boxX = SCREEN_WIDTH-MESSAGE_WIDTH-15;
			passiveY += itemInfo.size()*20+35;
			displayItemInfo(boxX,boxY,tempItem,g);
		}
		double activeY = 65;
		for (int i = 0; i < player.weapons.length; i++)
		{
			Weapon tempWeapon = player.weapons[i];
			ArrayList<String> itemInfo;
			if (tempWeapon != null)
			{
				itemInfo = tempWeapon.getInfo();
			}
			else
			{
				itemInfo = new ArrayList<String>();
				itemInfo.add("Weapon: none");
			}
			double boxY = activeY;
			double boxX = 15;
			activeY += itemInfo.size()*20+35;
			if (player.activeWeapon == i)
			{
				Color tempColor = new Color(0,255,0,200);
				g.setColor(tempColor);
				g.drawRect((int)boxX, (int)boxY, MESSAGE_WIDTH, (itemInfo.size()+1)*20);
			}
			displayItemInfo(boxX,boxY,tempWeapon,g);
			
		}
	}
	private void displayDeaths(Graphics g)
	{
		double x = 15;
		double y = 0;
		Color tempColor = new Color(127,127,127,100);
		g.setColor(tempColor);
		g.fillRect((int)x, (int)y, MESSAGE_WIDTH, 2*20);
		
		tempColor = new Color(0,0,0);
		g.setColor(tempColor);
		g.drawString("Deaths: " + deaths, (int)(x+MESSAGE_X), (int)(y+20));
	}
	private void displayMessageBox(Graphics g)
	{
		int goalY = 0;
		if (!player.onPlanet)
		{
			goalY = -MESSAGE_HEIGHT;
		}
		double dx = goalY-messageY;
		messageY += dx/10;
		if (Math.abs(messageY-goalY) < 0.1)
		{
			messageY = goalY;
		}
		
		Color tempColor = new Color(127,127,127,100);
		g.setColor(tempColor);
		
		double boxX = (SCREEN_WIDTH/2-MESSAGE_WIDTH/2);
		g.fillRect((int)boxX, (int)messageY, MESSAGE_WIDTH, MESSAGE_HEIGHT);
		
		tempColor = new Color(0,0,0);
		g.setColor(tempColor);
		
		String message1 = "Planet Name: " + player.currentPlanet.planetName;
		String message2 = "Planet Radius: " + String.format("%.4g%n", player.currentPlanet.radius);
		String message3 = "Planet Density: " + String.format("%.3g%n", player.currentPlanet.density);
		g.drawString(message1, (int)(boxX+MESSAGE_X), (int)(messageY+30));
		g.drawString(message2, (int)(boxX+MESSAGE_X), (int)(messageY+60));
		g.drawString(message3, (int)(boxX+MESSAGE_X), (int)(messageY+90));
	}
	private void displayLaunchMeter(Graphics g)
	{
		
		double topLeftX = (SCREEN_WIDTH/2-METER_WIDTH/2);
		double topLeftY = (SCREEN_HEIGHT-METER_HEIGHT-40);
		
		Color tempColor = new Color(255,0,0);
		g.setColor(tempColor);
		g.fillRect((int)topLeftX,(int)topLeftY,(int)(METER_WIDTH*player.launchMeter/100),METER_HEIGHT);
		
		tempColor = new Color(0,255,0);
		g.setColor(tempColor);
		g.drawRect((int)topLeftX,(int)topLeftY,METER_WIDTH,METER_HEIGHT);
		
		tempColor = new Color(0,0,255);
		g.setColor(tempColor);
		g.fillRect((int)topLeftX,(int)topLeftY-COOLDOWN_HEIGHT,(int)(METER_WIDTH*player.launchCooldown/100),COOLDOWN_HEIGHT);
	}
	public double mapWidth()
	{
		return GAME_WIDTH*scale;
	}
	public double mapHeight()
	{
		return GAME_HEIGHT*scale;
	}
	public int mapX()
	{
		return (int)(SCREEN_WIDTH/2-mapWidth()/2);
	}
	public int mapY()
	{
		return (int)(SCREEN_HEIGHT/2-mapHeight()/2);
	}
	public void displayMap(Graphics g)
	{
		double width = mapWidth();
		double height = mapHeight();
		double topLeftX = (SCREEN_WIDTH/2-width/2);
		double topLeftY = (SCREEN_HEIGHT/2-height/2);
		
		Color tempColor = new Color(0,255,0);
		g.setColor(tempColor);
		g.drawRect((int)topLeftX, (int)topLeftY, (int)width, (int)height);
		
		tempColor = new Color(100,200,100,150);
		g.setColor(tempColor);
		g.fillRect((int)topLeftX, (int)topLeftY, (int)width, (int)height);
		
		
		for (int i = 0 ; i < circObjects.size(); i++)
		{
			CircleObject cObj = circObjects.get(i);
			double radius = cObj.radius;
			
			if (cObj instanceof Planet)
			{
				if (((Planet)cObj).homePlanet)
				{
					tempColor = new Color(0,0,255,200);
				}
				else
				{
					tempColor = new Color(127,127,127,200);
				}
			}
			if (cObj instanceof ItemDrop)
			{
				radius = ITEM_RADIUS/scale;
				if (((ItemDrop)cObj).type == ItemDrop.itemType.ExtraLife)
				{
					tempColor = Color.pink;
				}
				if (((ItemDrop)cObj).type == ItemDrop.itemType.Weapon)
				{
					tempColor = Color.cyan;
				}
			}
			
			g.setColor(tempColor);
			g.fillOval((int)(cObj.x*scale-radius*scale+topLeftX),(int)(cObj.y*scale-radius*scale+topLeftY),
					   (int)(radius*2*scale),(int)(radius*2*scale));
			/*
			if (resetColor)
			{
				resetColor = false;
				tempColor = new Color(127,127,127,200);
				g.setColor(tempColor);
			}*/
		}
		
		for (int i = 0 ; i < regions.size(); i++)
		{
			Region r = regions.get(i);
			double diff_scale = 1-(Region.maxDiff-r.difficulty)/Region.maxDiff;
			tempColor = new Color((int)(255*diff_scale),(int)(255*(1-diff_scale)),0,50);
			g.setColor(tempColor);
			
			g.fillOval((int)(r.x*scale-r.radius*scale+topLeftX),(int)(r.y*scale-r.radius*scale+topLeftY),
					   (int)(r.radius*2*scale),(int)(r.radius*2*scale));
			
		}
		if (playerBlink%BLINK_PERIOD < BLINK_PERIOD/2)
		{
			tempColor = Color.red;
			g.setColor(tempColor);
			g.fillOval((int)(player.x*scale-PLAYER_RADIUS+topLeftX),(int)(player.y*scale-PLAYER_RADIUS+topLeftY),
					   (int)(PLAYER_RADIUS*2),(int)(PLAYER_RADIUS*2));
		}
	}
}
