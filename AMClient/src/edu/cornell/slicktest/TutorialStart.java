package edu.cornell.slicktest;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.json.JSONException;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import edu.cornell.slicktest.Physics;
import edu.cornell.slicktest.Screen;
import edu.cornell.slicktest.Enums.Direction;
import edu.cornell.slicktest.Enums.UnitState;
import edu.cornell.slicktest.Enums.Units;


public class TutorialStart implements Screen
{
	public static float scale = .5f;
	public static int maxEnergy = 100;
	final Point energyBarOffset = new Point(50, 50);
	final int energyBarHeight = 50;
	final int energyBarWidth = 20;
	
	ArrayList<Army> armies;
	Army army;
	ArrayList<Unit> units;
	Physics physics;
	PhysicsUnitFactory physicsFactory;
	World world;
	Image background;
	Image foreground;
	int worldWidth;
	int worldHeight;
	int screenOffsetX;
	int screenOffsetY;
	int energy;
	int currentStep;
	Unit fairy;
	Unit dragon;
	Body physicsFairy;
	UnitAnimation fairyAnim;
	BattleInputHandler input;
	MementoHandler moveStack;
	
	public TutorialStart(GameContainer container) throws SlickException
	{
		world = new World(new Vec2(0, 70), true);
		background = new Image("images/mushroomsbackground.png");
		foreground = new Image("images/mushroomsforeground.png");
		worldWidth = foreground.getWidth();
		worldHeight = foreground.getHeight();
		screenOffsetX = (worldWidth-container.getWidth())/2;
		screenOffsetY = (worldHeight-container.getHeight())/2;
		physics = new Physics(worldWidth, worldHeight, foreground);	
		physicsFactory = new PhysicsUnitFactory();
		input = new BattleInputHandler();
		moveStack = new MementoHandler();
		energy = maxEnergy;
		currentStep = 0;
		
		fairy = Factory.getUnit(Units.FAIRY, "Fairy" , "");
		physicsFairy = physicsFactory.makeUnit(world, Units.FAIRY, container, physics, worldWidth, scale, new Vec2(600,350));
		fairy.initBattleStates(UnitState.STAND, Direction.RIGHT, new Rectangle(300, 275, fairy.width*scale, fairy.height*scale));
		fairyAnim = new UnitAnimation(fairy, container);
		
		units = new ArrayList<Unit>();
		units.add(fairy);
		army = new Army(units, maxEnergy);
		armies = new ArrayList<Army>();
		armies.add(army);
	}
	
	@Override
	public void render(GameContainer container, Graphics graphics) throws SlickException 
	{
		graphics.drawImage(background, -1*screenOffsetX, -1*screenOffsetY);
		graphics.drawImage(physics.getUpdatedForeground(), -1*screenOffsetX, -1*screenOffsetY);
		
		graphics.setColor(Color.magenta);
		graphics.drawRect((float)energyBarOffset.getX(), (float)energyBarOffset.getY(), (float)energyBarWidth, (float)energyBarHeight);
		float energyBar = (float)energyBarHeight*(float)energy/(float)maxEnergy;
		graphics.fillRect((float)energyBarOffset.getX(), (float)(energyBarOffset.getY()+energyBarHeight-energyBar), (float)energyBarWidth, energyBar);
		fairyAnim.render(container, graphics, Color.green, screenOffsetX, screenOffsetY);
		switch(currentStep)
		{
			case 0:
				graphics.setColor(Color.white);
				graphics.drawString("Welcome to the Artillery Menagerie tutorial!", 100, 50);
				graphics.drawString("You are in control of the fairy marked with the green arrow.", 100, 75);
				graphics.drawString("Use the WASD keys or the arrow keys to move the fairy around.", 100, 100);
				break;
			case 1:
				graphics.setColor(Color.white);
				graphics.drawString("You may have noticed the purple meter", 100, 50);
				graphics.drawString("at the top left emptying. That is your Energy", 100, 75);
				graphics.drawString("Meter. Once it runs out, you cannot move anymore", 100, 100);
				graphics.drawString("that turn. Go ahead and run around until your energy", 100, 125);
				graphics.drawString("runs out, then press ENTER to end your turn.", 100, 150);
				break;
			case 2:
				graphics.setColor(Color.white);
				graphics.drawString("Now you can shoot. A line of circles will display", 100, 50);
				graphics.drawString("your trajectory as you move the mouse. Once you click,", 100, 75);
				graphics.drawString("the shot will fire. Shooting uses energy and after a unit", 100, 100);
				graphics.drawString("shoots it cannot move again that turn.", 100, 125);
				graphics.drawString("Try shooting now.", 100, 150);
				break;
			case 3:
				graphics.setColor(Color.white);
				graphics.drawString("Now you have an additional unit. Use TAB to switch", 100, 50);
				graphics.drawString("units. Use both units to destroy the targets.", 100, 75);
				break;
			case 4:
				graphics.setColor(Color.white);
				graphics.drawString("Great! Time to venture out into the world and blow up your friends.", 100, 50);
		}
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException, IOException, JSONException, InterruptedException 
	{
		// TODO Auto-generated method stub
		physics.updateCollisionUnits(armies);
	}

}
