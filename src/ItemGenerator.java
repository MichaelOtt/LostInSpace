import java.util.Random;


public class ItemGenerator 
{
	//Weapon Paramaters: int range, int shots, double spread, double speed, int fireRate, boolean physical
	Random randGen;
	long rngseed;
	double [][] weaponTypes;
	double maxDiff;
	String [] weaponNames;
	public ItemGenerator(double maxDiff)
	{
		this.maxDiff = maxDiff;
		rngseed = System.currentTimeMillis();
		resetGenerator();
		createWeaponArray();
	}
	public void newSeed()
	{
		rngseed = System.currentTimeMillis();
	}
	public void resetGenerator()
	{
		randGen = new Random(rngseed);
	}
	//Weapon Paramaters: int range, int shots, double spread, double speed, int fireRate, boolean physical
	private void createWeaponArray()
	{
		weaponNames = new String[]{
		"Sparkler",
		"Gun",
		"Trail",
		"Pulse"//,
		//"Thrower"
		};
		weaponTypes = new double[][]{
		{5,15,1,15,360,360,4,4,5,1,2,200},
		{20,200,1,1,0,0,5,15,20,5,2,50},
		{5,200,1,1,0,0,0,0,1,1,0,1000},
		{20,100,50,100,360,360,4,4,100,10,2,20},
		{5,50,1,15,50,30,4,8,1,1,2}
		};
	}
	private double genRange(double first, double second, double diff)
	{
		double percent = diff/maxDiff+randGen.nextDouble()/10-0.05;
		if (percent < 0) percent = 0;
		if (percent > 1) percent = 1;
		double d = second-first;
		return first+d*percent;
	}
	//Weapon Paramaters: int range, int shots, double spread, double speed, int fireRate, boolean physical
	private Weapon weaponFromType(int num, double diff)
	{
		String name = weaponNames[num];
		double[] par = weaponTypes[num];
		int range = (int)Math.round(genRange(par[0],par[1],diff));
		int shots = (int)Math.round(genRange(par[2],par[3],diff));
		double spread = Math.round(genRange(par[4],par[5],diff));
		double speed = Math.round(genRange(par[6],par[7],diff));
		int fireRate = (int)Math.round(genRange(par[8],par[9],diff));
		int ammo = (int)(par[11]*par[8]/fireRate);
		boolean physical = false;
		if (par[10] == 1)
		{
			physical = true;
		}
		if (par[10] == 2)
		{
			if (randGen.nextInt(2)==0)physical = true;
		}
		if (physical) name = "Bullet " + name;
		else name = "Laser " + name;
		return new Weapon(name,range,shots,spread,speed,fireRate,physical,ammo);
	}
	public Weapon createWeapon(double diff)//difficulty = alienNum/planetRadius*PLANET_MAX_RADIUS
	{
		Weapon tempW;
		tempW = weaponFromType(randGen.nextInt(weaponNames.length),diff);
		return tempW;
	}
}
