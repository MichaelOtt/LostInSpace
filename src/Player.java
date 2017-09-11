import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class Player extends PlanetLander implements PhysicsObject
{
	double vx, vy;
	double prevX, prevY;
	double rotationMovement;
	boolean didJump;
	boolean launched;
	boolean launching;
	boolean firing;
	boolean immune;
	int immuneTicks;
	final double JUMP_SPEED = 5;
	final double LAUNCH_MAX = 20;
	final int IMMUNE_TIME = 100;
	double launchCooldown;
	double launchMeter;
	ArrayList<Item> passiveItems;
	Weapon[] weapons;
	//Weapon weapon;
	int activeWeapon;
	public Player(double angle, Planet currentPlanet)
	{
		super(angle, 5, Color.green, currentPlanet, 3);
		launchMeter = 0;
		launchCooldown = 0;
		prevX = x;
		prevY = y;
		passiveItems = new ArrayList<Item>();
		weapons = new Weapon[3];
		activeWeapon = 0;
		firing = false;
		immune = false;
		initializeActiveItems();
	}
	private void initializeActiveItems()
	{
		for (int i = 0; i < weapons.length; i++)
		{
			weapons[i] = null;
		}
	}
	public boolean attemptKill()
	{
		if (immune) return false;
		for (int i = 0; i < passiveItems.size(); i++)
		{
			if (passiveItems.get(i) instanceof ExtraLife)
			{
				immune = true;
				immuneTicks = IMMUNE_TIME;
				passiveItems.remove(i);
				return false;
			}
		}
		return true;
	}
	public void checkLand(Planet p)
	{
		if (!onPlanet)
		{
			if (collision(p,0))
			{
				currentPlanet = p;
				onPlanet = true;
				launched = false;
				angleFromXY();
				xyFromAngle();
			}
		}
	}
	public void keyInput(HashMap<Integer,Boolean> keyMap, int scroll)
	{
		rotationMovement = 0;
		if (!launching)
		{
			if (keyMap.get(KeyEvent.VK_A))
			{
				rotationMovement -= 1;
			}
			if (keyMap.get(KeyEvent.VK_D))
			{
				rotationMovement += 1;
			}
			if (keyMap.get(KeyEvent.VK_W) && onPlanet)
			{
				didJump = true;
			}
			if (keyMap.get(KeyEvent.VK_SPACE) && onPlanet && launchCooldown == 0)
			{
				launching = true;
				//didJump = true;
				//launched = true;
			}
		}
		else
		{
			if(!keyMap.get(KeyEvent.VK_SPACE) && onPlanet)
			{
				didJump = true;
				launched = true;
				launching = false;
			}
		}
		if (keyMap.get(MouseEvent.BUTTON1))
		{
			firing = true;
		}
		else
		{
			firing = false;
		}
		if (scroll < 0)
		{
			activeWeapon = (activeWeapon+scroll)%weapons.length+weapons.length;
		}
		else
		{
			activeWeapon = (activeWeapon+scroll)%weapons.length;
		}
		if (keyMap.get(KeyEvent.VK_1))
		{
			activeWeapon = 0;
		}
		if (keyMap.get(KeyEvent.VK_2))
		{
			activeWeapon = 1;
		}
		if (keyMap.get(KeyEvent.VK_3))
		{
			activeWeapon = 2;
		}
		
	}
	private Weapon getWeapon()
	{
		return weapons[activeWeapon];
	}
	public void pickUpItem(ItemDrop itemDrop)
	{
		Item item = itemDrop.item;
		if (item instanceof Weapon)
		{
			itemDrop.item = getWeapon();
			weapons[activeWeapon] = (Weapon)item;
		}
		else
		{
			itemDrop.item = null;
			passiveItems.add(item);
		}
		
	}
	public void shoot(double mouseX, double mouseY, ArrayList<Projectile> projectiles)
	{
		if (getWeapon() != null && getWeapon().canFire())
		{
			getWeapon().shoot();
			double dx = mouseX-x;
			double dy = mouseY-y;
			double tempAngle = Math.atan2(dy,dx);
			for (int i = 0; i < getWeapon().shots; i++)
			{
				Projectile p = new Projectile(x,y,getWeapon().speed,tempAngle+getWeapon().angleShift(),2,(getWeapon().physical ? new Color(127,127,127):new Color(255,0,0)),getWeapon().physical,getWeapon().range);
				projectiles.add(p);
			}
			if (getWeapon().outOfAmmo())
			{
				weapons[activeWeapon] = null;
			}
		}
	}
	public void update()
	{
		for (int i = 0; i < weapons.length; i++)
		{
			if (weapons[i] != null)
			{
				weapons[i].update();
			}
		}
		launchCooldown-=0.2;
		if (immune)immuneTicks--;
		if (immuneTicks == 0)immune = false;
		if (launchCooldown < 0)launchCooldown = 0;
		if (launching)
		{
			launchMeter++;
			if (launchMeter > 100)launchMeter = 100;
		}
		if (!onPlanet)
		{
			setX(x+vx);
			setY(y+vy);
		}
		else
		{
			rotate(rotationMovement);
			if(didJump)
			{
				didJump = false;
				onPlanet = false;
				vx = x-prevX;
				vy = y-prevY;
				if (!launched)
				{
					vx += JUMP_SPEED*Math.cos(angle);
					vy += JUMP_SPEED*Math.sin(angle);
				}
				if (launched)
				{
					vx += LAUNCH_MAX*launchMeter/100*Math.cos(angle);
					vy += LAUNCH_MAX*launchMeter/100*Math.sin(angle);
					launchCooldown = launchMeter;
					launchMeter = 0;
				}
				setX(x+vx);
				setY(y+vy);
			}
		}
	}
	public void draw(Graphics g, ViewInfo view)
	{
		if (!immune || immuneTicks%10<5)
		{
			super.draw(g,view);
		}
	}
	public double getVR()
	{
		double dx = x-prevX;
		double dy = y-prevY;
		return Math.sqrt(dx*dx+dy*dy);
	}
	public void accel(double ax, double ay) 
	{
		if (!onPlanet)
		{
			vx += ax;
			vy += ay;
		}
	}
	public void setX(double x)
	{
		prevX = this.x;
		super.setX(x);
	}
	public void setY(double y)
	{
		prevY = this.y;
		super.setY(y);
	}
	public double getX() 
	{
		return x;
	}

	public double getY() 
	{
		return y;
	}

}
