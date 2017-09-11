import java.awt.*;
import java.awt.event.*;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JPanel;

public class SpaceGame extends JPanel implements MouseListener, MouseMotionListener, KeyListener, MouseWheelListener
{
	HashMap<Integer,Boolean> keyMap;
	final int SCREEN_WIDTH, SCREEN_HEIGHT;
	final int GAME_WIDTH, GAME_HEIGHT;
	final double VIEW_SCALE;
	ViewInfo view;
	Graphics2D offg;
	Image offscreen;
	boolean ready;
	ArrayList<CircleObject> stars;
	ArrayList<GravityObject> gravObjects;
	ArrayList<Alien> aliens;
	ArrayList<Region> regions;
	ArrayList<Projectile> projectiles;
	ArrayList<ItemDrop> itemDrops;
	Player player;
	Hud playerHud;
	MapGenerator mGen;
	ProjectileIndicator pIndicator;
	boolean indicator_toggle;
	int mousex, mousey;
	int scroll;
	public SpaceGame(int width, int height)
	{
		SCREEN_WIDTH = width;
		SCREEN_HEIGHT = height;
		VIEW_SCALE = 2;
		GAME_WIDTH = 50000;
		GAME_HEIGHT = 50000;
		view = new ViewInfo(VIEW_SCALE,SCREEN_WIDTH,SCREEN_HEIGHT);
		ready = false;
		
		this.setFocusable(true);
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		keyMapInitialization();
		
		pIndicator = new ProjectileIndicator(3,50,2,5,Color.blue);
		pIndicator.initializeDots();
		indicator_toggle = false;
		
		mGen = new MapGenerator(GAME_WIDTH,GAME_HEIGHT);
		createGame();
		
	}
	public void createGame()
	{
		//mGen.newSeed(); //used in stress testing map generation
		mGen.resetGenerator();
		//stars = mGen.createStars();
		gravObjects = mGen.createPlanets();
		regions = mGen.createRegions();
		itemDrops = mGen.createItemDrops(gravObjects, regions);
		aliens = mGen.createAliens(gravObjects, regions,itemDrops);
		
		player = mGen.placePlayer(gravObjects);
		projectiles = new ArrayList<Projectile>();
		if (playerHud == null)
		{
			playerHud = new Hud(regions, player, GAME_WIDTH, GAME_HEIGHT, SCREEN_WIDTH, SCREEN_HEIGHT);
		}
		else
		{
			playerHud.resetHud(player);
		}
		changeView(player.x,player.y,VIEW_SCALE);
	}
	public void createOffscreenImage()
	{
		offscreen = this.createImage((int)SCREEN_WIDTH,(int)SCREEN_HEIGHT);
		offg = (Graphics2D) offscreen.getGraphics();
		offg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		ready = true;
	}
	public void checkLose()
	{
		if (player.x < -player.radius || player.x > GAME_WIDTH+player.radius
		 || player.y < -player.radius || player.y > GAME_HEIGHT+player.radius)
		{
			createGame();
		}
		else
		{
			boolean touchingAlien = false;
			for (int i = 0; i < aliens.size(); i++)
			{
				if (aliens.get(i).collision(player,0))
				{
					if (player.attemptKill())
					{
						touchingAlien = true;
					}
				}
			}
			if (touchingAlien)
			{
				createGame();
			}
		}
	}
	public void update()
	{
		if(!player.onPlanet)
		{
			for(int i = 0; i < gravObjects.size(); i++)
			{
				if (!player.launched || gravObjects.get(i) != player.currentPlanet)
				{
					gravObjects.get(i).pullPhysObject(player);
				}
			}
		}
		if (keyMap.get(KeyEvent.VK_E))
		{
			for (int i = 0; i < itemDrops.size(); i++)
			{
				ItemDrop id = itemDrops.get(i);
				if (id.collision(player, 0))
				{
					keyMap.put(KeyEvent.VK_E, false);
					player.pickUpItem(id);
					if (id.item == null)
					{
						itemDrops.remove(id);
					}
					break;
				}
			}
		}
		player.keyInput(keyMap,scroll);
		player.update();
		if (keyMap.get(KeyEvent.VK_1) || keyMap.get(KeyEvent.VK_2) || keyMap.get(KeyEvent.VK_3) || scroll != 0)
		{
			playerHud.startWeaponDisplayTimer();
		}
		scroll = 0;
		for (int i = 0; i < aliens.size(); i++)
		{
			if (aliens.get(i) instanceof MobileAlien)
			{
				MobileAlien tempAlien = (MobileAlien)aliens.get(i);
				tempAlien.update(player);
			}
		}
		if (player.firing)
		{
			player.shoot(view.reverseX(mousex), view.reverseY(mousey), projectiles);
		}
		
		checkLose();
		
		for (int i = 0; i < projectiles.size(); i++)
		{
			Projectile p = projectiles.get(i);
			if (p.physical)
			{
				for (int j = 0; j < gravObjects.size(); j++)
				{
					gravObjects.get(j).pullPhysObject(p);
				}
			}
			p.update();
			if (p.ticksLeft <= 0)
			{
				projectiles.remove(p);
				i--;
			}
			for (int j = 0; j < aliens.size(); j++)
			{
				if(p.collision(aliens.get(j), 0))
				{
					aliens.remove(j);
					projectiles.remove(p);
					break;
				}
			}
			for (int j = 0; j < gravObjects.size(); j++)
			{
				if(p.collision(gravObjects.get(j), 0))
				{
					projectiles.remove(p);
					break;
				}
			}
		}
		if(!player.onPlanet)
		{
			for(int i = 0; i < gravObjects.size(); i++)
			{
				GravityObject g = gravObjects.get(i);
				if (g instanceof Planet)
				{
					player.checkLand((Planet)g);
				}
			}
		}
		
		//pIndicator.updateInfo(player, view.reverseX(mousex), view.reverseY(mousey));
		//pIndicator.updateDots(gravObjects);
		
		double approachScale = VIEW_SCALE-Math.sqrt(player.getVR())*VIEW_SCALE/5;
		double dScale = approachScale - view.scale;
		double newScale = view.scale+dScale/20;
		if (Math.abs(dScale) < 0.01)newScale = approachScale;
		changeView(player.x,player.y,newScale);
	}
	public void paint(Graphics g1)
	{
		Graphics2D g = (Graphics2D) g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//offscreen graphics must exist first
		if (!ready)return;
		
		//background
		offg.setColor(Color.BLACK);
		offg.fillRect(0, 0, (int)SCREEN_WIDTH, (int)SCREEN_HEIGHT);
		
		//stars
		/*
		for (int i = 0; i < stars.size(); i++)
		{
			stars.get(i).draw(offg,view);
		}
		*/
		//gravity objects
		for (int i = 0; i < gravObjects.size(); i++)
		{
			gravObjects.get(i).draw(offg,view);
		}
		//aliens
		for (int i = 0; i < aliens.size(); i++)
		{
			aliens.get(i).draw(offg,view);
		}
		for (int i = 0; i < projectiles.size(); i++)
		{
			projectiles.get(i).draw(offg,view);
		}
		for (int i = 0; i < itemDrops.size(); i++)
		{
			itemDrops.get(i).draw(offg,view);
		}
		//projectile indicator
		if (indicator_toggle)
		{
			pIndicator.drawDots(offg, view);
		}
		//player
		player.draw(offg,view);
		paintView(g,offscreen,0,0);
		
		playerHud.updateHud();
		playerHud.updateMap(gravObjects);
		playerHud.updateMap(itemDrops);
		
		playerHud.displayHud(g);
		if (keyMap.get(KeyEvent.VK_SHIFT))
		{
			//Image miniMap = this.createImage((int)playerHud.mapWidth(),(int)playerHud.mapHeight());
			//miniMap = makeColorTransparent(miniMap,Color.white);
			//Graphics mapG = miniMap.getGraphics();
			playerHud.displayMap(g);
			//paintView(g,miniMap,playerHud.mapX(),playerHud.mapY());
		}
		if (keyMap.get(KeyEvent.VK_Q) || playerHud.displayWeapons())
		{
			playerHud.displayItems(g);
		}
		for (int i = 0; i < itemDrops.size(); i++)
		{
			if (itemDrops.get(i).collision(player, 0))
			{
				playerHud.displayItemDropInfo(g, itemDrops.get(i), view);
			}
		}
	}
	public void changeView(double newX, double newY, double scale)
	{
		view.scale = scale;
		view.viewX = newX;
		view.viewY = newY;
		view.fixView(GAME_WIDTH, GAME_HEIGHT);
	}
	public void paintView(Graphics g, Image i, int x, int y)
	{
		g.drawImage(i, x, y, this);
	}
	//pulled from internets;
	public static Image makeColorTransparent(Image im, final Color color) {
	    ImageFilter filter = new RGBImageFilter() {

	        // the color we are looking for... Alpha bits are set to opaque
	        public int markerRGB = color.getRGB() | 0xFF000000;

	        public final int filterRGB(int x, int y, int rgb) {
	            if ((rgb | 0xFF000000) == markerRGB) {
	                // Mark the alpha bits as zero - transparent
	                return 0x00FFFFFF & rgb;
	            } else {
	                // nothing to do
	                return rgb;
	            }
	        }
	    };

	    ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
	    return Toolkit.getDefaultToolkit().createImage(ip);
	}
	//Mouse Listener Functions
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) 
	{
		if (keyMap.containsKey(e.getButton()))
		{
			keyMap.put(e.getButton(), true);
		}
	}
	public void mouseReleased(MouseEvent e) 
	{
		if (keyMap.containsKey(e.getButton()))
		{
			keyMap.put(e.getButton(), false);
		}
	}
	public void mouseDragged(MouseEvent e) 
	{
		mousex = e.getX();
		mousey = e.getY();
	}
	public void mouseMoved(MouseEvent e) 
	{
		mousex = e.getX();
		mousey = e.getY();
	}
	public void keyMapInitialization()
	{
		keyMap = new HashMap<Integer,Boolean>();
		keyMap.put(KeyEvent.VK_W, false);
		keyMap.put(KeyEvent.VK_A, false);
		keyMap.put(KeyEvent.VK_S, false);
		keyMap.put(KeyEvent.VK_D, false);
		keyMap.put(KeyEvent.VK_E, false);
		keyMap.put(KeyEvent.VK_SPACE, false);
		keyMap.put(KeyEvent.VK_SHIFT, false);
		keyMap.put(KeyEvent.VK_Q, false);
		keyMap.put(KeyEvent.VK_1, false);
		keyMap.put(KeyEvent.VK_2, false);
		keyMap.put(KeyEvent.VK_3, false);
		keyMap.put(MouseEvent.BUTTON1, false);
	}
	//Key Listener Functions
	public void keyPressed(KeyEvent e) 
	{
		if (keyMap.containsKey(e.getKeyCode()))
		{
			keyMap.put(e.getKeyCode(), true);
		}
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			createGame();
		}
	}
	public void keyReleased(KeyEvent e) 
	{
		if (keyMap.containsKey(e.getKeyCode()))
		{
			keyMap.put(e.getKeyCode(), false);
		}
	}
	public void keyTyped(KeyEvent e) 
	{
		
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) 
	{
		scroll = e.getWheelRotation();
	}

}
