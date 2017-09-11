import java.util.ArrayList;


public class Weapon extends Item
{
	int range;//time steps projectiles last
	int shots;
	double spread;//spread angle
	double speed;
	int fireRate;//time steps between firing
	int ticks;
	int ammo;
	private int startAmmo;
	boolean physical;
	
	public Weapon(String name, int range, int shots, double spread, double speed, int fireRate, boolean physical, int ammo)
	{
		super(true);
		this.range = range;
		this.shots = shots;
		this.spread = spread;
		this.speed = speed;
		this.fireRate = fireRate;
		this.startAmmo = ammo;
		this.ammo = ammo;
		ticks = 0;
		this.physical = physical;
		info.add("Weapon: " + name);
		info.add("Range: " + range);
		info.add("Shots: " + shots);
		info.add("Spread: " + spread);
		info.add("Speed: " + speed);
		info.add("Fire Rate: " + fireRate);
		info.add("Type: " + (physical ? "Bullet" : "Laser"));
	}
	public void eraseInfo()
	{
		info = new ArrayList<String>();
	}
	public void update()
	{
		ticks--;
	}
	public boolean canFire()
	{
		return (ticks<=0);
	}
	public boolean outOfAmmo()
	{
		return ammo<=0;
	}
	public void shoot()
	{
		ammo--;
		resetTicks();
	}
	private void resetTicks()
	{
		ticks = fireRate;
	}
	public int getStartAmmo()
	{
		return startAmmo;
	}
	public double angleShift()
	{
		return Math.random()*(spread/180*Math.PI)-(spread/360*Math.PI);
	}
}
