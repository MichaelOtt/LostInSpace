import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;


public class MapGenerator 
{
	final int GAME_WIDTH;
	final int GAME_HEIGHT;
	final int PLANET_NUM = 5500;
	final int REGION_NUM = 4;
	final double PLANET_MIN_R = 100;
	final double PLANET_MAX_R = 500;
	final double PLANET_MIN_DENSITY = 0.7;
	final double PLANET_MAX_DENSITY = 0.85;
	final double REGION_MIN_R = 5000;
	final double REGION_MAX_R = 10000;
	final double REGION_MIN_DIFF = 20;
	final double REGION_MAX_DIFF = 70;
	final double REGION_BUFFER = -500;
	final double BUFFER = 50;
	
	Planet homeP = null;
	final double HOME_DIFF = REGION_MAX_DIFF;
	final double HOME_REGION_R = 20000;
	Random randGen;
	long rngseed;
	ItemGenerator itemGen;
	public MapGenerator(int GAME_WIDTH, int GAME_HEIGHT)
	{
		this.GAME_WIDTH = GAME_WIDTH;
		this.GAME_HEIGHT = GAME_HEIGHT;
		rngseed = System.currentTimeMillis();
		itemGen = new ItemGenerator(REGION_MAX_DIFF);
		resetGenerator();	
	}
	public void newSeed()
	{
		rngseed = System.currentTimeMillis();
		itemGen.newSeed();
	}
	public void resetGenerator()
	{
		randGen = new Random(rngseed);
		itemGen.resetGenerator();
	}
	public ArrayList<CircleObject> createStars()
	{
		ArrayList<CircleObject> stars = new ArrayList<CircleObject>();
		for (int i = 0; i < 300; i++)
		{
			double randX = randGen.nextInt(GAME_WIDTH);
			double randY = randGen.nextInt(GAME_HEIGHT);
			CircleObject star = new CircleObject(randX,randY,3,Color.white);
			stars.add(star);
		}
		return stars;
	}
	public ArrayList<GravityObject> createPlanets()
	{
		ArrayList<GravityObject> gravObjects = new ArrayList<GravityObject>();
		int notPlaced = PLANET_NUM;
		while(notPlaced > 0)
		{
			double r = genMinMax(PLANET_MIN_R,PLANET_MAX_R);
			double x = genMinMax(r+BUFFER,GAME_WIDTH-r-BUFFER);
			double y = genMinMax(r+BUFFER,GAME_HEIGHT-r-BUFFER);
			double density = genMinMax(PLANET_MIN_DENSITY,PLANET_MAX_DENSITY);
			int cVal = 100+(int)(155*density);
			Planet tempPlanet = new Planet(x,y,r,new Color(cVal,cVal,cVal),density);
			if (checkValid(tempPlanet, gravObjects))
			{
				notPlaced--;
				gravObjects.add(tempPlanet);
			}
			else
			{
				
			}
		}
		Planet home = (Planet)gravObjects.get(randGen.nextInt(PLANET_NUM));
		home.homePlanet = true;
		home.color = Color.green;
		home.found = true;
		home.notOnMap = true;
		homeP = home;
		return gravObjects;
	}
	public ArrayList<Region> createRegions()
	{
		Region.maxDiff = REGION_MAX_DIFF;
		
		ArrayList<Region> regions = new ArrayList<Region>();
		int notPlaced = REGION_NUM;
		
		double r = HOME_REGION_R;
		double x = homeP.x;
		double y = homeP.y;
		double difficulty = HOME_DIFF;
		regions.add(new Region(x,y,r,difficulty));
		
		while(notPlaced > 0)
		{
			r = genMinMax(REGION_MIN_R,REGION_MAX_R);
			x = genMinMax(r+REGION_BUFFER,GAME_WIDTH-r-REGION_BUFFER);
			y = genMinMax(r+REGION_BUFFER,GAME_HEIGHT-r-REGION_BUFFER);
			difficulty = genMinMax(REGION_MIN_DIFF,REGION_MAX_DIFF);
			Region tempRegion = new Region(x,y,r,difficulty);
			if (checkValidRegion(tempRegion, regions))
			{
				notPlaced--;
				regions.add(tempRegion);
			}
			else
			{
				
			}
		}
		return regions;
	}
	public ArrayList<Alien> createAliens(ArrayList<GravityObject> planets, ArrayList<Region> regions, ArrayList<ItemDrop> drops)
	{
		ArrayList<Alien> aliens = new ArrayList<Alien>();
		for (int i = 0; i < planets.size(); i++)
		{
			Alien tempA;
			Planet p = (Planet)planets.get(i);
			double diff = planetDiff(p,regions);
			int notPlaced = (int)(diff*p.radius/PLANET_MAX_R)+randGen.nextInt(5)-2;
			if (p.homePlanet) notPlaced = 0;
			p.alienNum = 0;
			while(notPlaced > 0)
			{
				if (randGen.nextInt(4) == 0)
				{
					tempA = new MobileAlien(randGen.nextDouble()*2.0*Math.PI,p);
				}
				else
				{
					tempA = new ImmobileAlien(randGen.nextDouble()*2.0*Math.PI,p);
				}
				if (checkValid(tempA, drops))
				{
					notPlaced--;
					p.alienNum++;
					aliens.add(tempA);
				}
			}
		}
		return aliens;
	}
	public ArrayList<ItemDrop> createItemDrops(ArrayList<GravityObject> planets, ArrayList<Region> regions)
	{
		ArrayList<ItemDrop> itemDrops = new ArrayList<ItemDrop>();
		for (int i = 0; i < planets.size(); i++)
		{
			Planet p = (Planet)planets.get(i);
			double diff = planetDiff(p,regions);
			double percent = diff/REGION_MAX_DIFF;
			if (randGen.nextDouble() < Math.sqrt(percent))
			{
				boolean placed = false;
				ItemDrop tempI;
				while (!placed)
				{
					if (randGen.nextInt(4)==0)
					{
						tempI = new ItemDrop(randGen.nextDouble()*2.0*Math.PI,p,new ExtraLife(),ItemDrop.itemType.ExtraLife);
					}
					else
					{
						tempI = new ItemDrop(randGen.nextDouble()*2.0*Math.PI,p,itemGen.createWeapon(diff),ItemDrop.itemType.Weapon);
					}
					//if (checkValid(tempI, aliens))
					{
						placed = true;
						itemDrops.add(tempI);
					}
				}
			}
		}
		return itemDrops;
	}
	private double genMinMax(double min, double max)
	{
		return randGen.nextDouble()*(max-min)+min;
	}
	private boolean checkValidRegion(Region r, ArrayList<Region> regions)
	{
		for (int i = 0; i < regions.size(); i++)
		{
			Region tempr = regions.get(i);
			double dx = r.x-tempr.x;
			double dy = r.y-tempr.y;
			if (Math.sqrt(dx*dx+dy*dy) <= (r.radius+tempr.radius)+REGION_BUFFER)
			{
				return false;
			}
		}
		return true;
	}
	private boolean checkValid(CircleObject c, ArrayList<? extends CircleObject> circObjects)
	{
		for (int i = 0; i < circObjects.size(); i++)
		{
			CircleObject tempc = circObjects.get(i);
			if (c.collision(tempc,BUFFER))
			{
				return false;
			}
		}
		return true;
	}
	private double planetDiff(Planet p, ArrayList<Region> regions)
	{
		double total = 0;
		for (int i = 0; i < regions.size(); i++)
		{
			total += regions.get(i).diff(p);
		}
		return total;
	}
	public Player placePlayer(ArrayList<GravityObject> planets)
	{
		Player player;
		do
		{
			player = new Player(randGen.nextDouble()*2.0*Math.PI,(Planet)planets.get(randGen.nextInt(PLANET_NUM)));
		}while(player.currentPlanet.alienNum != 0 || player.currentPlanet.homePlanet);
		return player;
	}
}
