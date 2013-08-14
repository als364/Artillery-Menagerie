package edu.cornell.slicktest;

// import MSTerrain;
// import PhysicsUnitFactory;
// import Unit;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Animation;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.MouseOverArea;
import edu.cornell.slicktest.BattleStates;
import edu.cornell.slicktest.Enums.*;

import org.jbox2d.callbacks.ContactFilter;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

public class BattleScreen implements Screen {
	
	// scale
	public static float scale = .5f;
	
	// Using server or not
	boolean singlePlayer = false;
	
	// Some positions to draw for debugging terrain
	private ArrayList<Vec2> draw_this = new ArrayList<Vec2>();
	private ArrayList<Vec2> draw_units = new ArrayList<Vec2>();
	
	private static final int VELOCITY_ITERATIONS = 8;

	private static final int POSITION_ITERATIONS = 3;
	
	public static ArrayList<Vec2[]> terrain_vecs;
	public static ArrayList<Vec2> terrain_pos;

	ArrayList<ArrayList<UnitAnimation>> unitAnimations = new ArrayList<ArrayList<UnitAnimation>>();
	
	int numLevels = 3;
	Level[] levels = new Level[numLevels];
	int currentLevel = 2;
	
	ArrayList<Army> armies = new ArrayList<Army>();

	ArrayList<Unit> units1 = new ArrayList<Unit>();
	ArrayList<Unit> units2 = new ArrayList<Unit>();
	
	ArrayList<Body> physicsObjects = new ArrayList<Body>();
	ArrayList<Body> physicsUnits = new ArrayList<Body>();
	World world;
	
	Animation projectileAnimation;
	
	final Point energyBarOffset = new Point(50, 50);
	final int energyBarHeight = 50;
	final int energyBarWidth = 20;
	final double baseDistance = 100.0; //this is the distance which is equal to 1xbaseEnergyCost for ability
	
	final int cursorOffset = 30;
	
	// Keeping track of whose turn it is and what unit is moving
	int numberOfPlayers;
	int turnCount = 0;
	int currentUnitNumber = 0;
	boolean hasJumped = false;
	boolean hasFlown = false;
	
	private MSTerrain _terrain;
	PhysicsUnitFactory PhysicsFactory;
	
	Physics physics;
	BattleInputHandler battleInput;
	Stack<Memento> moveStack;
	boolean hasActed = false;
	
	Image background;
	ImageBuffer foregroundbuffer;
	Image greenCursor;
	Image redCursor;
	int worldWidth;
	int worldHeight;
	
	//Buttons
	BattleButtons battleButtons;
	
	BattleHelpScreen battleHelpScreen;
	BattlePopUp battlePopUp;
	
	boolean hasPopUp = false;
	boolean popUpCloseCountDownStart = false;
	int popUpCloseCountDown = 0;
	
	// Messages
	Image waitOpponent;
	Image needAnimation;
	Image animatingMoves;
	
	int overLapCount = 0;
	
	boolean battleEnded = false;
	boolean i_won = false;
	
	String battleKey;
	int playerNum;
	String playerName;
	BattleServerCommunication battleServer;
	
	int frameInterval = 0;
	
	boolean animatingFrames = false;
	boolean needToBeAnimatted = false;
	boolean overEnergyLimit = false;
	boolean needLongCount = false;
	long time = 0;
	final int frameTime = 30;
	
	BattleAnimator battleAnimator;
	
	int winner = -1;
	
	// Screen offset
	int screenOffsetX;
	int screenOffsetY;
	boolean selectAdjust = true;
	
	boolean outOfUnits = false;
	
	int battleEndTimeCount;
	final int battleEndTime = 3000;
	
	boolean forceLoad = false;
	boolean loadPastExplosions = false;
	
	ClientEngine main;
	
	public BattleScreen(ClientEngine main, boolean singlePlayer) {
		this.main = main;
		this.singlePlayer = singlePlayer;
	}

	public void init(GameContainer container) throws SlickException, IOException, JSONException, InterruptedException {
		initLevels();
		
		
		world = new World(new Vec2(0, 70), true);
		
		//ContactListener listener = null;
		//world.setContactListener(listener);
		
		
		//TODO: Uncomment this section and remove the statements below when levels are implemented
		initLevels();
		background = levels[currentLevel].getBackground();
		Image foreground = levels[currentLevel].getForeground();
		Vec2[] startingPositions = levels[currentLevel].getStarts();
		
		battleButtons = new BattleButtons();
		waitOpponent = new Image("gui/wait_opponent.png");
		needAnimation = new Image("gui/need_animation.png");
		animatingMoves = new Image("gui/animating.png");
		
		
		//background = new Image("images/mushroomsbackground.png");
		//Image foreground = new Image("images/mushroomsforeground.png");
		//Image foreground = new Image("images/test1.png");
		//Image foreground = new Image("images/test2.png");

		worldWidth = foreground.getWidth();
		worldHeight = foreground.getHeight();
		
		// Initial screen offsets
		screenOffsetX = (worldWidth-container.getWidth())/2;
		screenOffsetY = (worldHeight-container.getHeight())/2;
		
		terrain_pos = new ArrayList<Vec2>();
		terrain_vecs = new ArrayList<Vec2[]>();
		
		_terrain = new MSTerrain(world, new AABB(new Vec2(0, 0), new Vec2(worldWidth, worldHeight)));
		_terrain.PointsPerUnit = 10;
		_terrain.CellSize = 10;
        _terrain.SubCellSize = 2;
        _terrain.decomposer = Decomposer.Earclip;
        _terrain.Iterations = 2;
        
        _terrain.Initialize();
        
        _terrain.ApplyTexture(foreground, new Vec2(0, 0), 0);
		ArrayList<Body> world_bodies = new ArrayList<Body>();
		ArrayList<Body> non_zero_mass = new ArrayList<Body>();
		ArrayList<Vec2> nzm_pos = new ArrayList<Vec2>();
		ArrayList<Vec2> zm_pos = new ArrayList<Vec2>();
		Body lst = world.getBodyList();
		while(lst.getNext() != null){
			world_bodies.add(lst);
			if(lst.m_mass != 0 ){
				non_zero_mass.add(lst);
				nzm_pos.add(lst.getPosition());
			}else{
				zm_pos.add(lst.getPosition());
			}
			lst = lst.getNext();
		}
		
		physics = new Physics(worldWidth, worldHeight, foreground);		
		
		PhysicsFactory = new PhysicsUnitFactory(); 
		
		int randomInt = (int) (Math.random()*1000000);
		playerName = "Player" + randomInt;
		
		if (singlePlayer) {
			int k = 0;
			// TODO:  uncomment four declarations, remove the corresponding ones above when levels are implemented
			Unit unit1 = Factory.getUnit(Units.ROBOT, "Dalek-D2" , "");
			//Body physicsUnit1 = PhysicsFactory.makeUnit(world, Units.SPACEMARINE, container, physics, worldWidth, scale);
			Body physicsUnit1 = PhysicsFactory.makeUnit(world, Units.ROBOT, container, physics, worldWidth, scale, startingPositions[k++]);
			
			
			Unit unit2 = Factory.getUnit(Units.CENTAUR, "Centaur 2", "");
			//Body physicsUnit2 = PhysicsFactory.makeUnit(world, Units.SPACEMARINE, container, physics, worldWidth, scale);
			Body physicsUnit2 = PhysicsFactory.makeUnit(world, Units.CENTAUR, container, physics, worldWidth, scale, startingPositions[k++]);
			
			Unit unit3 = Factory.getUnit(Units.AI, "AI 3", "");
			//Body physicsUnit3 = PhysicsFactory.makeUnit(world, Units.SPACEMARINE, container, physics, worldWidth, scale);
			Body physicsUnit3 = PhysicsFactory.makeUnit(world, Units.AI, container, physics, worldWidth, scale, startingPositions[k++]);
			
			Unit unit4 = Factory.getUnit(Units.SPACEMARINE, "Marine 4", "");
			//Body physicsUnit4 = PhysicsFactory.makeUnit(world, Units.SPACEMARINE, container, physics, worldWidth, scale);
			Body physicsUnit4 = PhysicsFactory.makeUnit(world, Units.SPACEMARINE, container, physics, worldWidth, scale, startingPositions[k++]);
			unit4.equipItem(Factory.getItem(Items.ROCKET_LAUNCHER, Math.random() + ""));
			
			unit1.initBattleStates(UnitState.STAND, Direction.LEFT, new Rectangle(300, 275, unit1.width*scale, unit1.height*scale));
			unit2.initBattleStates(UnitState.STAND, Direction.LEFT, new Rectangle(300, 275, unit2.width*scale, unit2.height*scale));
			unit3.initBattleStates(UnitState.STAND, Direction.LEFT, new Rectangle(300, 275, unit3.width*scale, unit3.height*scale));
			unit4.initBattleStates(UnitState.STAND, Direction.LEFT, new Rectangle(300, 275, unit4.width*scale, unit4.height*scale));
			
			units1.add(unit1);
			units1.add(unit2);
			units1.add(unit3);
			units1.add(unit4);
			physicsObjects.add(physicsUnit1);
			physicsObjects.add(physicsUnit2);
			physicsObjects.add(physicsUnit3);
			physicsObjects.add(physicsUnit4);
			physicsUnits.add(physicsUnit1);
			physicsUnits.add(physicsUnit2);
			physicsUnits.add(physicsUnit3);
			physicsUnits.add(physicsUnit4);
			
			Army army1 = new Army(units1, Army.baseMaxEnergy);
			army1.setSide(0);
			army1.refreshEnergy();
			
			System.out.println("JSON object:");
			System.out.println(army1.createArmyJSON());
			System.out.println("JSON object End");
			
			armies.add(army1);

			// TODO:  uncomment four declarations, remove the corresponding ones above when levels are implemented
			Unit unit11 = Factory.getUnit(Units.UNICORN, "Unicorn 1", "");
			//physicsUnit1 = PhysicsFactory.makeUnit(world, Units.FAIRY, container, physics, worldWidth, scale);
			physicsUnit1 = PhysicsFactory.makeUnit(world, Units.UNICORN, container, physics, worldWidth, scale, startingPositions[k++]);
			Unit unit12 = Factory.getUnit(Units.FAIRY, "Fairy 2", "");
			//physicsUnit2 = PhysicsFactory.makeUnit(world, Units.FAIRY, container, physics, worldWidth, scale);
			physicsUnit2 = PhysicsFactory.makeUnit(world, Units.FAIRY, container, physics, worldWidth, scale, startingPositions[k++]);
			Unit unit13 = Factory.getUnit(Units.ALIEN, "Alien 3", "");
			//physicsUnit3 = PhysicsFactory.makeUnit(world, Units.FAIRY, container, physics, worldWidth, scale);
			physicsUnit3 = PhysicsFactory.makeUnit(world, Units.ALIEN, container, physics, worldWidth, scale, startingPositions[k++]);
			Unit unit14 = Factory.getUnit(Units.DRAGON, "Dragon 4", "");
			//physicsUnit4 = PhysicsFactory.makeUnit(world, Units.FAIRY, container, physics, worldWidth, scale);
			physicsUnit4 = PhysicsFactory.makeUnit(world, Units.DRAGON, container, physics, worldWidth, scale, startingPositions[k++]);
			
			unit11.initBattleStates(UnitState.STAND, Direction.RIGHT, new Rectangle(300, 275, unit11.width*scale, unit11.height*scale));
			unit12.initBattleStates(UnitState.STAND, Direction.RIGHT, new Rectangle(300, 275, unit12.width*scale, unit12.height*scale));
			unit13.initBattleStates(UnitState.STAND, Direction.RIGHT, new Rectangle(300, 275, unit13.width*scale, unit13.height*scale));
			unit14.initBattleStates(UnitState.STAND, Direction.RIGHT, new Rectangle(300, 275, unit14.width*scale, unit14.height*scale));
	
			units2.add(unit11);
			units2.add(unit12);
			units2.add(unit13);
			units2.add(unit14);
			physicsObjects.add(physicsUnit1);
			physicsObjects.add(physicsUnit2);
			physicsObjects.add(physicsUnit3);
			physicsObjects.add(physicsUnit4);
			physicsUnits.add(physicsUnit1);
			physicsUnits.add(physicsUnit2);
			physicsUnits.add(physicsUnit3);
			physicsUnits.add(physicsUnit4);
			
			Army army2 = new Army(units2, Army.baseMaxEnergy);
			army2.setSide(1);
			army2.refreshEnergy();
			armies.add(army2);
			
			//System.out.println(armies);
		}
		else 
		{
			// Multiplayer unit, army, battle creation
			ArrayList<Unit> myUnits = new ArrayList<Unit>();
			if (Math.random() > 0.5) {
				Unit unit1 = Factory.getUnit(Units.ROBOT, "Robot 1", Math.random() + "");
				Unit unit2 = Factory.getUnit(Units.CENTAUR, "Centaur 2", Math.random() + "");
				unit2.equipItem(Factory.getItem(Items.ROCKET_LAUNCHER, Math.random() + ""));
				Unit unit3 = Factory.getUnit(Units.AI, "Marine 3", Math.random() + "");
				Unit unit4 = Factory.getUnit(Units.SPACEMARINE, "Marine 4", Math.random() + "");
				myUnits.add(unit1);
				myUnits.add(unit2);
				myUnits.add(unit3);
				myUnits.add(unit4);
			}
			else {
				Unit unit1 = Factory.getUnit(Units.UNICORN, "Unicorn 1", Math.random() + "");
				Unit unit2 = Factory.getUnit(Units.FAIRY, "Fairy 2", Math.random() + "");
				Unit unit3 = Factory.getUnit(Units.ALIEN, "Alien 3", Math.random() + "");
				unit3.equipItem(Factory.getItem(Items.ROCKET_LAUNCHER, Math.random() + ""));
				Unit unit4 = Factory.getUnit(Units.DRAGON, "Dragon 4", Math.random() + "");
				myUnits.add(unit1);
				myUnits.add(unit2);
				myUnits.add(unit3);
				myUnits.add(unit4);
			}
			//Army myArmy = new Army (myUnits, Army.baseMaxEnergy);
			Army myArmy = Player.getInstance().army;
			
			battleServer = new BattleServerCommunication();
			battleAnimator = new BattleAnimator();
			BattleJoinResult join = battleServer.startBattle(playerName, myArmy.createArmyJSON());
			battleKey = join.battleKey;
			playerNum = join.playerNumber;
			armies = join.armies;
			//System.out.println(armies);
			
			int unitPosIndex = 0;
			for (int i = 0; i < armies.size(); i++) {
				Army army = armies.get(i);
				army.setSide(i);
				army.refreshEnergy();
				Direction dir;
				if (i%2 == 0) {
					dir = Direction.LEFT;
				}
				else {
					dir = Direction.RIGHT;
				}
				
				for (Unit unit : army.units) {
					unit.initBattleStates(UnitState.STAND, Direction.LEFT, new Rectangle(300, 275, unit.width*scale, unit.height*scale));
					//Body physicsUnit = PhysicsFactory.makeUnit(world, unit.factoryKey, container, physics, worldWidth, scale);
					// TODO: uncomment when levels are implemented, remove the above declaration
					Body physicsUnit = PhysicsFactory.makeUnit(world, unit.factoryKey, container, physics, worldWidth, scale, startingPositions[unitPosIndex++]);
					physicsObjects.add(physicsUnit);
					physicsUnits.add(physicsUnit);
				}
			}
			
			turnCount = -1;
			if (playerNum == 0) {
				turnCount = 0;
				battleServer.storeStateToServer(battleKey, battleEnded, winner, turnCount, playerNum, armies);
			}
		}
		
		if (singlePlayer) {
			turnCount = 0;
		}
		
		//System.out.println(armies.size());
		numberOfPlayers = armies.size();
		//System.out.println("FFFFFFFFFFFFFFFFFFF: " + armies.size());
		
		// Create UnitAnimation object for each unit
		battleInput = new BattleInputHandler(this);
		moveStack = new Stack<Memento>();
		//System.out.println(armies.size());
		
		for (int i = 0; i < numberOfPlayers; i++) 
		{
			unitAnimations.add(new ArrayList<UnitAnimation>());
			ArrayList<Unit> unitsOfArmy = armies.get(i).units;
			for (int j = 0; j < unitsOfArmy.size(); j++) 
			{
				int pos = j + unitsOfArmy.size()*i;
				Body b = physicsUnits.get(pos);
				UnitAnimation temp = new UnitAnimation(unitsOfArmy.get(j), container);
				temp.setBody(b);
				temp.unit.setBody(b);
				temp.unit.setUnitPosition(b.getPosition().x - temp.offset.x, b.getPosition().y - temp.offset.y);
				unitAnimations.get(i).add(temp);
			}
		}
		
		greenCursor = new Image("images/GreenCursor.png");
		redCursor = new Image("images/RedCursor.png");
		
		physics.updateCollisionUnits(armies);
		Memento newMemento = new Memento(armies, physics);
		if (!singlePlayer) {
			newMemento.mementoCount = battleServer.getMementoCount();
		}
		moveStack.push(newMemento);
		
		//System.out.println(armies);
		hasActed = false;
		System.out.println("Initial memento: " + moveStack.peek());
		//System.out.println(armies);
		
	}
	
	public void resumeBattle(GameContainer container, String battleKey) throws SlickException, IOException, JSONException, InterruptedException {
		initLevels();
		
		world = new World(new Vec2(0, 70), true);
		
		//TODO: Uncomment this section and remove the statements below when levels are implemented
		initLevels();
		background = levels[currentLevel].getBackground();
		Image foreground = levels[currentLevel].getForeground();
		Vec2[] startingPositions = levels[currentLevel].getStarts();

		battleButtons = new BattleButtons();
		waitOpponent = new Image("gui/wait_opponent.png");
		needAnimation = new Image("gui/need_animation.png");
		animatingMoves = new Image("gui/animating.png");
		
		worldWidth = foreground.getWidth();
		worldHeight = foreground.getHeight();
		
		// Initial screen offsets
		screenOffsetX = (worldWidth-container.getWidth())/2;
		screenOffsetY = (worldHeight-container.getHeight())/2;
		
		terrain_pos = new ArrayList<Vec2>();
		terrain_vecs = new ArrayList<Vec2[]>();
		
		_terrain = new MSTerrain(world, new AABB(new Vec2(0, 0), new Vec2(worldWidth, worldHeight)));
		_terrain.PointsPerUnit = 10;
		_terrain.CellSize = 10;
        _terrain.SubCellSize = 2;
        _terrain.decomposer = Decomposer.Earclip;
        _terrain.Iterations = 2;
        
        _terrain.Initialize();
        
        _terrain.ApplyTexture(foreground, new Vec2(0, 0), 0);
		ArrayList<Body> world_bodies = new ArrayList<Body>();
		ArrayList<Body> non_zero_mass = new ArrayList<Body>();
		ArrayList<Vec2> nzm_pos = new ArrayList<Vec2>();
		ArrayList<Vec2> zm_pos = new ArrayList<Vec2>();
		Body lst = world.getBodyList();
		while(lst.getNext() != null){
			world_bodies.add(lst);
			if(lst.m_mass != 0 ){
				non_zero_mass.add(lst);
				nzm_pos.add(lst.getPosition());
			}else{
				zm_pos.add(lst.getPosition());
			}
			lst = lst.getNext();
		}
		
		physics = new Physics(worldWidth, worldHeight, foreground);		
		
		PhysicsFactory = new PhysicsUnitFactory(); 
		
		int randomInt = (int) (Math.random()*1000000);
		playerName = "Player" + randomInt;
	
			// Multiplayer unit, army, battle creation
		
			Army myArmy = Player.getInstance().army;
			
			battleServer = new BattleServerCommunication();
			battleAnimator = new BattleAnimator();
			BattleJoinResult join = battleServer.resumeBattle(battleKey);
			this.battleKey = join.battleKey;
			this.playerNum = join.playerNumber;
			this.armies = join.armies;
			if (!join.isFirstTurn) {
				forceLoad = true;
				loadPastExplosions = true;
			}
			//System.out.println(armies);
			
			int unitPosIndex = 0;
			for (int i = 0; i < armies.size(); i++) {
				Army army = armies.get(i);
				army.setSide(i);
				army.refreshEnergy();
				Direction dir;
				if (i%2 == 0) {
					dir = Direction.LEFT;
				}
				else {
					dir = Direction.RIGHT;
				}
				
				for (Unit unit : army.units) {
					unit.initBattleStates(UnitState.STAND, Direction.LEFT, new Rectangle(300, 275, unit.width*scale, unit.height*scale));
					//Body physicsUnit = PhysicsFactory.makeUnit(world, unit.factoryKey, container, physics, worldWidth, scale);
					// TODO: uncomment when levels are implemented, remove the above declaration
					Body physicsUnit = PhysicsFactory.makeUnit(world, unit.factoryKey, container, physics, worldWidth, scale, startingPositions[unitPosIndex++]);
					physicsObjects.add(physicsUnit);
					physicsUnits.add(physicsUnit);
				}
			}
			
			turnCount = -1;
			if (playerNum == 0 && join.isFirstTurn) {
				turnCount = 0;
				battleServer.storeStateToServer(battleKey, battleEnded, winner, turnCount, playerNum, armies);
			}
		
		numberOfPlayers = armies.size();
		
		// Create UnitAnimation object for each unit
		battleInput = new BattleInputHandler(this);
		moveStack = new Stack<Memento>();
		//System.out.println(armies.size());
		
		for (int i = 0; i < numberOfPlayers; i++) 
		{
			unitAnimations.add(new ArrayList<UnitAnimation>());
			ArrayList<Unit> unitsOfArmy = armies.get(i).units;
			for (int j = 0; j < unitsOfArmy.size(); j++) 
			{
				int pos = j + unitsOfArmy.size()*i;
				Body b = physicsUnits.get(pos);
				UnitAnimation temp = new UnitAnimation(unitsOfArmy.get(j), container);
				temp.setBody(b);
				temp.unit.setBody(b);
				temp.unit.setUnitPosition(b.getPosition().x - temp.offset.x, b.getPosition().y - temp.offset.y);
				unitAnimations.get(i).add(temp);
			}
		}
		
		greenCursor = new Image("images/GreenCursor.png");
		redCursor = new Image("images/RedCursor.png");
		
		physics.updateCollisionUnits(armies);
		moveStack.push(new Memento(armies, physics));
		//System.out.println(armies);
		hasActed = false;
		System.out.println("Initial memento: " + moveStack.peek());
		//System.out.println(armies);
		
		
	}
	
	public void render(GameContainer container, Graphics graphics) throws SlickException {
		
	
		//Check to see if the battle ended
		if (battleEnded && !animatingFrames && !needToBeAnimatted) {
			graphics.setColor(Color.yellow);
			if (i_won) {
				graphics.drawString("You won.", 100, 100);
			}
			else {
				graphics.drawString("You lost.", 100, 100);
			}
			
			return;
		}
		
		if (turnCount < 0) {
			return;
		}
				
		graphics.drawImage(background, -1*screenOffsetX, -1*screenOffsetY);
		graphics.drawImage(physics.getUpdatedForeground(), -1*screenOffsetX, -1*screenOffsetY);
		
		int playerNumber = turnCount%numberOfPlayers;
		Army currentArmy= armies.get(playerNumber);
		
		graphics.setColor(Color.white);
		graphics.drawString("Turn: " + turnCount, 350, 20);
		graphics.drawString("Unit: " + currentUnitNumber, 350, 40);		
		
		//graphics.drawString("Press Tab Key - Swap Unit", 450, 40);
		//graphics.drawString("Press Enter Key - Commit Turn", 450, 60);
		//graphics.drawString("Press Arrow Keys - Movement and Jump", 450, 80);
		//graphics.drawString("Press Backspace - Undo Single Move", 450, 100);
		//graphics.drawString("Press Shift + Backspace - Undo All Moves", 450, 120);
		//graphics.drawString("Mouse Left Click - Shoot", 450, 140);
		
		if (overEnergyLimit==true){
			graphics.setColor(Color.red);
			graphics.drawString("Out of energy! Press Enter to submit turn.", 300, 150);
			//battlePopUp = new BattlePopUp(container, "gui/turn1.png", "gui/turnEnd.png", "gui/turnRestart.png");
			//hasPopUp = true;
			overEnergyLimit=false;
		}
		
		// Energy bar
		graphics.setColor(Color.magenta);
		graphics.drawRect((float)energyBarOffset.getX(), (float)energyBarOffset.getY(), (float)energyBarWidth, (float)energyBarHeight);
		float energyBar = (float)energyBarHeight*(float)currentArmy.currentEnergy/(float)currentArmy.maxEnergy;
		graphics.fillRect((float)energyBarOffset.getX(), (float)(energyBarOffset.getY()+energyBarHeight-energyBar), (float)energyBarWidth, energyBar);
		
		//Check if energy empty
		if(armies.get(playerNumber).currentEnergy <= 0)
		{
			//graphics.setColor(Color.red);
			//graphics.drawString("Out of energy! Press Enter to submit turn.", 300, 150);
			battlePopUp = new BattlePopUp(container, "gui/turn.png", "gui/turnEnd.png", "gui/turnRestart.png");
			hasPopUp = true;
		}
		
		if (unitAnimations.get(playerNumber).size() <= currentUnitNumber) {
			currentUnitNumber = 0;
		}
		
		if (unitAnimations.get(playerNumber).size() == 0) {
			return;
		}
		
		// Draw cursor
		UnitAnimation currentUnitAnimation = unitAnimations.get(playerNumber).get(currentUnitNumber);
		Rectangle currentUnitRectangle = currentUnitAnimation.unit.getUnitRectangle();
		int cursorWidth = redCursor.getWidth();
		int cursorHeight = redCursor.getHeight();
		int cursorX = (int)(currentUnitRectangle.getCenterX() - cursorWidth/2);
		int cursorY = (int) currentUnitRectangle.getY() - cursorHeight - cursorOffset;
		if(currentArmy.done.get(currentUnitNumber))
		{
			graphics.drawImage(redCursor, cursorX-screenOffsetX, cursorY-screenOffsetY);
		}
		else
		{
			graphics.drawImage(greenCursor, cursorX-screenOffsetX, cursorY-screenOffsetY);
		}
		
		if (!hasPopUp) {
		
		double mouseX = container.getInput().getMouseX();
		double mouseY = container.getInput().getMouseY();
		
		if (battleButtons.checkMouseIn((int) mouseX, (int)mouseY) == 0) {		
		//Projected Path
		boolean multiplayerAllow = singlePlayer || (playerNum == playerNumber && !needToBeAnimatted && ! animatingFrames);
		if (multiplayerAllow && currentUnitAnimation.unit.getUnitState() == UnitState.STAND && !currentArmy.done.get(currentUnitNumber)) {
			graphics.setColor(Color.white);
			ProjectedPath projectedPath = physics.projectedPath(mouseX+screenOffsetX, mouseY+screenOffsetY, currentUnitAnimation.unit.getLaunchPoint(), currentUnitAnimation.unit.getUnitRectangle(), armies, currentUnitAnimation.unit);
			if (projectedPath.collidedWithUnit) {
				graphics.setColor(Color.red);
			}
			for (int i = 0; i < projectedPath.matrix.length; i++) {
				graphics.drawOval((int)projectedPath.matrix[i][0]-screenOffsetX, (int)projectedPath.matrix[i][1]-screenOffsetY, 7, 7);
			}
			double diffX = currentUnitRectangle.getX() - (mouseX+screenOffsetX);
			double diffY = currentUnitRectangle.getY() - (mouseY+screenOffsetY);
			double distance = Math.sqrt(diffX*diffX + diffY*diffY);
			double currentCost = (float)currentUnitAnimation.unit.getBaseAbility().getEnergyCost()*(distance/baseDistance);
			graphics.setColor(Color.red);
			float costBar = (float)energyBarHeight*(float)currentCost/(float)currentArmy.maxEnergy;
			float costBarX = (float)energyBarOffset.getX()+1;
			float costBarY = (float)(energyBarOffset.getY()+energyBarHeight-energyBar);
			if (costBar > energyBar) {
				graphics.setColor(Color.red);
				costBar = energyBar;
				graphics.drawString("Not enough energy.", 300, 150);
			}else{
				graphics.setColor(Color.green);
			}
			graphics.fillRect(costBarX, costBarY, (float)energyBarWidth-1, costBar);
		}
		}
		
		}
		// Draw all units
		for (int i  = 0; i < numberOfPlayers; i++) {
			Color armyColor = armies.get(i).armyColor;
			ArrayList<UnitAnimation> armyAnimation = unitAnimations.get(i);
			for (UnitAnimation unitAnimation : armyAnimation) {
				if (!unitAnimation.unit.dirty) {
					unitAnimation.render(container, graphics, armyColor, screenOffsetX, screenOffsetY);
					if (unitAnimation.unit.getUnitState() == UnitState.SHOOT) {
						hasActed = true;
						Point projectilePoint;
						if (animatingFrames) {
							projectilePoint = battleAnimator.getAnimatedProjectilePoint();
							projectileAnimation = battleAnimator.getAnimatedProjectileAnimation();
						}
						else {
							projectilePoint = physics.getProjectilePositionPoint();
						}
						if (projectileAnimation != null && projectilePoint != null) {
							projectileAnimation.draw(projectilePoint.x-screenOffsetX, projectilePoint.y-screenOffsetY);
						}
					}
					
					// Draw Army Symbol
					/*
					Rectangle unitRect = unitAnimation.unit.getUnitRectangle();
					int pointX = (int)unitRect.getX();
					int pointY = 0;
					pointY = (int)unitRect.getY() + 60;
					graphics.drawString("Army " + side, pointX-screenOffsetX, pointY-screenOffsetY);
					*/
				}
			}
		}
		
		// Debugging terrain drawing
		/*for(Vec2 pos : draw_this){
			graphics.setColor(Color.red);
			Polygon poly = new Polygon();
			graphics.drawRect(pos.x, pos.y, 25, 25);
		}*/
		/*
		graphics.setColor(Color.red);
		for(int i = 0; i < terrain_pos.size(); i++){
			Polygon poly = new Polygon();
			for(int j = 0; j < terrain_vecs.get(i).length; j++){
				float x = terrain_vecs.get(i)[j].x;
				float y = terrain_vecs.get(i)[j].y;
				poly.addPoint(x, y);
			}
			poly.setCenterX(terrain_pos.get(i).x  - screenOffsetX);
			poly.setCenterY(terrain_pos.get(i).y  - screenOffsetY);
			graphics.draw(poly);
		}
		*/
		/*
		for(Vec2 pos : draw_units){
			graphics.setColor(Color.green);
			graphics.drawRect(pos.x - 100*scale/2f - screenOffsetX, pos.y - 150*scale/2f - screenOffsetY, 100*scale, 150*scale);
		}
		*/
		// draw the bounding boxes for units
		/*
		graphics.setColor(Color.green);
		for(Army army : armies){
			for(Unit unit : army.units){
				graphics.drawRect(unit.getUnitRectangle().getX()-screenOffsetX, unit.getUnitRectangle().getY()-screenOffsetY, unit.getUnitRectangle().getWidth(), unit.getUnitRectangle().getHeight());
			}
		}
		graphics.setColor(Color.orange);
		for(Army army : armies){
			for(Unit unit : army.units){
				graphics.drawRect(unit.body.m_xf.position.x-screenOffsetX-unit.getUnitRectangle().getWidth()/2, unit.body.m_xf.position.y-screenOffsetY-unit.getUnitRectangle().getHeight()/2, unit.getUnitRectangle().getWidth(), unit.getUnitRectangle().getHeight());
			}
		}
		*/
		
		// Buttons
		battleButtons.draw();
		
		if (hasPopUp) {
			if (battleHelpScreen != null) {
				battleHelpScreen.draw(container, graphics);
			}
			if (battlePopUp != null) {
				double mouseX = container.getInput().getMouseX();
				double mouseY = container.getInput().getMouseY();
				battlePopUp.draw((int)mouseX, (int)mouseY);
			}
		}
		
		//Multiplayer
		if (!singlePlayer) {
			graphics.setColor(Color.green);
			graphics.drawString("Player " + playerNum, 30, 100);
			graphics.setColor(Color.blue);
			if (playerNum != playerNumber) {
				//graphics.drawString("Please wait for the other player.", 30, 150);
				waitOpponent.draw();
			}
			else if (needToBeAnimatted) {
				//graphics.drawString("Press Enter to animate opponent's moves.", 30, 150);
				needAnimation.draw();
			}
			else if (animatingFrames) {
				//graphics.drawString("Animating opponent's moves.", 30, 150);
				animatingMoves.draw(30, 120);
			}
		}
		
		// TODO: display something here that is representational of you being out of units but it still
		// being your turn
		if(outOfUnits){
			
		}
	}
	
	
	public void update(GameContainer container, int delta) throws Exception {
		/*
		if(armies == null)
		{
			System.out.println("FUCK");
		}
		*/
		
		// Exit battle screen
		if (container.getInput().isKeyDown(Input.KEY_ESCAPE)) {
			main.openMainScreen();
		}
		
		// Popup
		
		if (hasPopUp) {
			if (battleHelpScreen != null) {
				if (battleHelpScreen.needToClose()) {
					battleHelpScreen = null;
					popUpCloseCountDownStart = true;
					popUpCloseCountDown = 500;
				}
			}
			if (battlePopUp != null) {
				if (container.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
					double mouseX = container.getInput().getMouseX();
					double mouseY = container.getInput().getMouseY();
					int num = battlePopUp.checkMouse((int)mouseX, (int)mouseY);
					if (num == 1) {
						endTurn();
					}
					else if (num == 2) {
						//moveStack.clear();
						while (moveStack.size() > 1) {
							unapply();
						}
						if (!singlePlayer) {
							battleServer.restartTurn(turnCount);
						}
					}
					
					if (num != 0) {
						battlePopUp = null;
						popUpCloseCountDownStart = true;
						popUpCloseCountDown = 500;
					}
				}
				
			}
			if (popUpCloseCountDownStart) {
				popUpCloseCountDown -= delta;
				if (popUpCloseCountDown < 0) {
					popUpCloseCountDownStart = false;
					hasPopUp = false;
				}
			}
			return;
		}
		
		//Check to see if the battle ended
		if (!animatingFrames && !needToBeAnimatted) {
			if (battleEnded) {
				battleEndTimeCount += delta;
				if (battleEndTimeCount > battleEndTime) {
					if(turnCount%numberOfPlayers == playerNum){
						endTurn();
					}
					main.endBattle();
				}
			return;
			}
			checkBattleCondition();
			if (battleEnded) {
				return;
			}
		}
		//System.out.println(armies);
		//Multiplayer
		if (!singlePlayer) 
		{
			
			frameInterval += delta;
			if (animatingFrames && frameInterval > frameTime) {
				
				BattleAnimationResult result = battleAnimator.animateNextFrame(armies, unitAnimations, physics, _terrain, screenOffsetX, screenOffsetY, world);
				animatingFrames = result.needFurthurAnimation;
				screenOffsetX = result.screenOffsetX;
				screenOffsetY = result.screenOffsetY;
				frameInterval = 0;
				if (!animatingFrames) {
					selectAdjust = true;
				}
				return;
			}
			else if (frameInterval > frameTime) {
				battleServer.storeFrame(armies, screenOffsetX, screenOffsetY, physics, moveStack.peek().mementoCount);
				frameInterval = 0;
			}
		}
		
		if (turnCount < 0) {
			//Multiplayer
			if (!singlePlayer) 
			{
				loadFromServer(delta);
			}
			return;
		}
		
		//numberOfPlayers = 2;
		int playerNumber = turnCount%numberOfPlayers;
		
		int temp = 0;
		for(Unit unit : armies.get(playerNumber).units){
			temp += unit.dirty ? 0 : 1;
		}
		if(temp == 0){
			outOfUnits = true;
		}
		
		//Multiplayer
		if (!singlePlayer) {
			if (playerNumber != playerNum) {
				loadFromServer(delta);
				return;
			}
		}
		
		physics.updateCollisionUnits(armies);
		
		if (container.getInput().isKeyPressed(Input.KEY_ENTER)) {
			
			endTurn();
		}
		
		
		if (container.getInput().isKeyDown(Input.KEY_1)) {
			if (screenOffsetX > 0) {
				screenOffsetX--;
			}
		}
		if (container.getInput().isKeyDown(Input.KEY_2)) {
			if (container.getWidth()+screenOffsetX < worldWidth) {
				screenOffsetX++;
			}
		}
		if (container.getInput().isKeyDown(Input.KEY_3)) {
			if (screenOffsetY > 0) {
				screenOffsetY--;
			}
		}
		if (container.getInput().isKeyDown(Input.KEY_4)) {
			int offsetY = 65;
			if (container.getHeight()+screenOffsetY < worldHeight + offsetY) {
				screenOffsetY++;
			}
		}
		
		if (container.getInput().isKeyPressed(Input.KEY_TAB) && ! outOfUnits) {
			armies.get(playerNumber).units.get(currentUnitNumber).setUnitState(UnitState.STAND);
			currentUnitNumber++;
			currentUnitNumber = currentUnitNumber % armies.get(playerNumber).units.size();
			while(armies.get(playerNumber).units.get(currentUnitNumber).dirty)
			{
				currentUnitNumber++;
				currentUnitNumber = currentUnitNumber % armies.get(playerNumber).units.size();				
			}
			System.out.println(armies);
			Memento newMemento = new Memento(armies, physics);
			if (!singlePlayer) {
				newMemento.mementoCount = battleServer.getMementoCount();
			}
			moveStack.push(newMemento);
			System.out.println(armies);
			hasActed = false;
			System.out.println("Memoizing: " + moveStack.peek());
			System.out.println("Armies: " + moveStack.peek().unit_positions);
			selectAdjust = true;
		}
		
		if (container.getInput().isKeyDown(Input.KEY_LSHIFT) || container.getInput().isKeyDown(Input.KEY_RSHIFT)) {
			//System.out.println("ping");
			if(container.getInput().isKeyPressed(Input.KEY_BACK))
			{
				while(moveStack.size() > 1)
				{
					unapply();
				}
			}
		}
		
		if (container.getInput().isKeyPressed(Input.KEY_BACK)) {
			//System.out.println("ping");
			if(moveStack.size() >= 1)
			{
				unapply();
			}
		}
		
		// physics.updateGravity(armies);
		if(outOfUnits){
			return;
		}
		
		UnitAnimation currentUnitAnimation = unitAnimations.get(playerNumber).get(currentUnitNumber);
		Rectangle currentUnitRectangle = currentUnitAnimation.unit.getUnitRectangle();
		
		while(armies.get(playerNumber).units.get(currentUnitNumber).dirty){
			currentUnitNumber++;
			currentUnitNumber = currentUnitNumber % armies.get(playerNumber).units.size();				
		}
		
		BattleMouseClickResult mouseResult = battleInput.handleMouseClick(container, armies.get(turnCount%numberOfPlayers), screenOffsetX, screenOffsetY, battleButtons);
		
		if (mouseResult.battleButtonClicked == 2) {
			main.openMainScreen();
		}
		else if (mouseResult.battleButtonClicked == 3) {
			
			endTurn();
		}
		else if (mouseResult.battleButtonClicked == 4) {
			if(moveStack.size() > 1)
			{
				unapply();
			}
		}
		else if (mouseResult.battleButtonClicked == 5) {
			// Help Screen
			battleHelpScreen = new BattleHelpScreen(container);
			hasPopUp = true;
		}
		
		if (mouseResult.unitIsSelected) {
			currentUnitNumber = mouseResult.unitSelected;
			armies.get(playerNumber).units.get(currentUnitNumber).setUnitState(UnitState.STAND);
			currentUnitNumber = mouseResult.unitSelected;
			System.out.println(armies);
			Memento newMemento = new Memento(armies, physics);
			if (!singlePlayer) {
				newMemento.mementoCount = battleServer.getMementoCount();
			}
			moveStack.push(newMemento);
			System.out.println(armies);
			hasActed = false;
			System.out.println("Memoizing: " + moveStack.peek());
			System.out.println("Armies: " + moveStack.peek().unit_positions);
			selectAdjust = true;
		}
		
		if (armies.get(playerNumber).currentEnergy > 0 && !armies.get(playerNumber).done.get(currentUnitNumber)) 
		{ 
			float prevMovement = currentUnitAnimation.unit.getUnitRectangle().getX();
			battleInput.handleUnitControl(container, delta, physics.getGround(), currentUnitAnimation.unit, worldWidth);
			float afterMovement = currentUnitAnimation.unit.getUnitRectangle().getX();
			if (prevMovement != afterMovement) {
				physics.updateCollisionUnits(armies);
			}
			
			if (mouseResult.mouseClicked && mouseResult.battleButtonClicked == 0 && !mouseResult.unitIsSelected) 
			{
				
				if (currentUnitAnimation.unit.getUnitState() == UnitState.STAND && !animatingFrames && !needToBeAnimatted) 
				{
					double diffX = currentUnitRectangle.getX() - (mouseResult.mouseX+screenOffsetX);
					double diffY = currentUnitRectangle.getY() - (mouseResult.mouseY+screenOffsetY);
					double distance = Math.sqrt(diffX*diffX + diffY*diffY);
					Ability baseAbility = currentUnitAnimation.unit.getBaseAbility();
					if(armies.get(playerNumber).currentEnergy >= (int)(baseAbility.getEnergyCost())*(distance/baseDistance)){
						armies.get(playerNumber).done.set(currentUnitNumber, true);
						currentUnitAnimation.unit.setUnitState(UnitState.SHOOT);
						projectileAnimation = new Animation(baseAbility.getSpriteSheet(), 150);
						double x = mouseResult.mouseX + (double)screenOffsetX;
						double y = mouseResult.mouseY + (double)screenOffsetY;
						ProjectedPath projPath = physics.projectedPath(x, y, currentUnitAnimation.unit.getLaunchPoint(), currentUnitRectangle, armies, currentUnitAnimation.unit);
						physics.createProjectile((int)currentUnitRectangle.getX(), (int)currentUnitRectangle.getY(), x, y, false, 
								currentUnitAnimation.unit.getUnitDirection(), 
								currentUnitAnimation.unit, projPath.collision_x, projPath.collision_y);
						armies.get(playerNumber).currentEnergy -= (int)((float)baseAbility.getEnergyCost())*(distance/baseDistance);
					}else{
						overEnergyLimit=true;
						needLongCount = true;
					}
					
					//Multiplayer
					if (!singlePlayer) {
						battleServer.addShot(currentUnitAnimation.unit, 0);
					}
					
				}
			}
		
		}
		
		for (ArrayList<UnitAnimation> armyAnimation : unitAnimations)
		{
			for (UnitAnimation unitAnimation : armyAnimation) {
				unitAnimation.updateAnimation(container, delta, worldWidth);
			}
		}
		
		if (battleInput.getDeltaMovement() >= 50.0) {
			armies.get(playerNumber).currentEnergy-=5;
			battleInput.resetDeltaMovement();
			if (armies.get(playerNumber).currentEnergy == 0) {
				currentUnitAnimation.unit.setUnitState(UnitState.STAND);
			}
		}
		
		UnitState state = currentUnitAnimation.unit.getUnitState();
		
		if (((state != UnitState.STAND && state != UnitState.SHOOT) || selectAdjust) && !animatingFrames) {
			adjustScreenOffsets(container, currentUnitRectangle, delta);
		}
		
		if(state == UnitState.STAND)
		{
			hasJumped = false;
			hasFlown = false;
		}
		else if(state == UnitState.JUMP)
		{
			hasFlown = false;
			if(!hasJumped)
			{
				hasJumped = true;
				armies.get(playerNumber).currentEnergy -= 7;
			}
		}
		else if(state == UnitState.FLY)
		{
			hasJumped = false;
			/*if(!hasFlown)
			{
				hasFlown = true;
				armies.get(playerNumber).currentEnergy -= 3;
			}*/			
		}
		else if(state == UnitState.WALK)
		{
			hasJumped = false;
			hasFlown = false;
		}
		else if(state == UnitState.FALL)
		{
			hasJumped = false;
			hasFlown = false;
		}
		else if(state == UnitState.SHOOT)
		{
			hasJumped = false;
			hasFlown = false;
			ProjectileState projectileState = physics.updateProjectile(delta);
			if (projectileState == ProjectileState.HITUNIT) {
				
				System.out.println("Hit!");
				
				Point projectilePoint = physics.getProjectilePositionPoint();
				Point collisionPoint = physics.getProjectileCollisionPoint();
				
				Vec2 impact = new Vec2(collisionPoint.x, collisionPoint.y);
				double radius = physics.getProjectileSplashRadius();
				physics.updateCollisionBoard(collisionPoint.x, collisionPoint.y, radius);
				physics.updateCollisionTerrain(new Vec2(collisionPoint.x, collisionPoint.y), _terrain);
				//physics.terrainExplosion(40, projectilePoint.x, projectilePoint.y);
				physics.unitExplosion(physicsUnits, impact, 10000*physicsUnits.get(0).m_mass, 25);
				Ability baseAbility = currentUnitAnimation.unit.getBaseAbility();
				System.out.println(currentUnitAnimation.unit.getAbilityDamage(baseAbility));
				damageUnits(collisionPoint, currentUnitAnimation.unit.getAbilityDamage(baseAbility));
				
				//Multiplayer
				if (!singlePlayer) {
					battleServer.addExplosion(radius, projectilePoint.x, projectilePoint.y, turnCount, moveStack.peek().mementoCount);
				}
			}
			if (projectileState != ProjectileState.INAIR) {
				currentUnitAnimation.unit.setUnitState(UnitState.STAND);
			}
			//physics.updateProjectile(delta);
		}
		
		// Die if walk or move off screen
		killUnitsOffScreen();
		
		checkBattleCondition();
		if (battleEnded) {
			return;
		}
		/*
		Body b = currentUnitAnimation.body;
		boolean[] facts = {b.isActive(), b.isAwake(), b.isBullet(), b.isFixedRotation(), b.isSleepingAllowed(), b.m_type == BodyType.DYNAMIC};
		ArrayList<Body> world_bodies = new ArrayList<Body>();
		ArrayList<Body> non_zero_mass = new ArrayList<Body>();
		ArrayList<Vec2> nzm_pos = new ArrayList<Vec2>();
		ArrayList<Vec2> zm_pos = new ArrayList<Vec2>();
		Body lst = world.getBodyList();
		while(lst.getNext() != null){
			world_bodies.add(lst);
			if(lst.m_mass != 0 ){
				//System.out.println(lst.getAngle());
				non_zero_mass.add(lst);
				nzm_pos.add(lst.getPosition());
			}else{
				zm_pos.add(lst.getPosition());
			}
			lst = lst.getNext();
		}
		draw_this = zm_pos;
		draw_units = nzm_pos;
		*/
		if(outOfUnits && playerNum == playerNumber && moveStack.size() == 0){
			System.out.println("I lost");
			battleEnded = true;
			i_won = false;
		}
		
		
		world.step(1f/60f, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
		
		
		//world.step(delta, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
	}
	
	private void checkBattleCondition() throws JSONException, IOException {
		// Check to see if all other players are lost assuming 2 players
		boolean won = false;
		for (Army army : armies) {
			if (army.isLost()) {
				won = true;
			}
		}
		if (won) {
			battleEnded = true;
			for (Army army : armies) {
				if (!army.isLost()) {
					winner = army.getSide();
					break;
				}
			}
			turnCount++;
			currentUnitNumber = 0;
			if (!singlePlayer) {
				battleServer.storeStateToServer(battleKey, battleEnded, winner, turnCount, playerNum, armies);
			}
			System.out.println("Battle Ended");
		}
	}
	
	// This to do to end the turn
	private void endTurn() throws JSONException, IOException {
		
		int playerNumber = turnCount%numberOfPlayers;
		
		if (needToBeAnimatted) {
			needToBeAnimatted = false;
			animatingFrames = true;
		}
		else {
			armies.get(turnCount%numberOfPlayers).refreshEnergy();
			armies.get(turnCount%numberOfPlayers).refreshState();
			turnCount++;
			currentUnitNumber = 0;
			moveStack.clear();
			killDirtyCritters();
			moveStack.push(new Memento(armies, physics));
			hasActed = false;
			selectAdjust = true;
		}
		
		//Multiplayer
		if (!singlePlayer) {
			battleServer.storeFrame(armies, screenOffsetX, screenOffsetY, physics, moveStack.peek().mementoCount);
			battleServer.storeStateToServer(battleKey, battleEnded, winner, turnCount, playerNumber, armies);
			battleServer.readyForNewTurn();
		}
		
		int temp = 0;
		ArrayList<Integer> indeces = new ArrayList<Integer>();
		for(int i = 1; i <= numberOfPlayers; i++){
			if(i != playerNum){
				indeces.add(i);
			}
		}
		for(int i : indeces){
			for(Unit unit : armies.get(i - 1).units){
				temp += unit.dirty ? 0 : 1;
			}
		}
		
		if(!outOfUnits && temp == 0){
			battleEnded = true;
			i_won = true;
		}
	}
	
	// Make sure the selected unit is in the screen
	private void adjustScreenOffsets(GameContainer container, Rectangle currentUnitRectangle, int delta) {
		
		int tempX = screenOffsetX;
		int tempY = screenOffsetY;
		
		int offsetY = 65;
		
		double multiplier = 0.5;
		
		if ((int)currentUnitRectangle.getCenterX() > container.getWidth()/2+screenOffsetX) {
			if (container.getWidth()+screenOffsetX < worldWidth) {
				screenOffsetX += 1;
			}
		}
		else if ((int)currentUnitRectangle.getCenterX() < container.getWidth()/2+screenOffsetX) {
			if (screenOffsetX > 0) {
				screenOffsetX -= 1;
			}
		}
				
		if ((int)currentUnitRectangle.getCenterY() > container.getHeight()/2+screenOffsetY) {
			if (container.getHeight()+screenOffsetY < worldHeight + offsetY) {
				screenOffsetY += 1;
			}
		}
		else if ((int)currentUnitRectangle.getCenterY() < container.getHeight()/2+screenOffsetY) {
			if (screenOffsetY > 0) {
				screenOffsetY -= 1;
			}
		}
		
		if (tempX == screenOffsetX && tempY == screenOffsetY) {
			selectAdjust = false;
		}
	}
	
	private void killUnitsOffScreen() {
		Rectangle worldRect = new Rectangle(0, 0, worldWidth, worldHeight);
		for (int i = 0; i < armies.size(); i++) {
			Army army = armies.get(i);
			for (int j = 0; j < army.units.size(); j++) {
				Rectangle unitRect = army.units.get(j).getUnitRectangle();	
				Unit unit = army.units.get(j);
				float x = unitRect.getCenterX(); float y = unitRect.getCenterY();
				if (!worldRect.contains(x, y) && y > 0) 
				{
					unit.dirty = true;
					//army.units.remove(j);
					//unitAnimations.get(i).remove(j);
					/*if (turnCount%2 == i && currentUnitNumber == j) {
						currentUnitNumber = 0;
					}
					j--;*/
				}
			}
		}
	}
	
	public ArrayList<Object[]> getUnitsHit(int x, int y, double r){
		ArrayList<Object[]> toReturn = new ArrayList<Object[]>();
		
		ArrayList<Unit> temp = new ArrayList<Unit>();
		for(Unit unit : units1){
			temp.add(unit);
		}
		for(Unit unit : units2){
			temp.add(unit);
		}
		
		for(Unit unit : temp){
			Point2D impact = new Point2D.Float(x, y);
			Rectangle unitRectangle = unit.getUnitRectangle();
			Point2D tL = new Point2D.Float(unitRectangle.getX(), unitRectangle.getY());
			Point2D tR = new Point2D.Float((float)unitRectangle.getMaxX(), unitRectangle.getY());
			Point2D bL = new Point2D.Float(unitRectangle.getX(), (float)unitRectangle.getMaxY());
			Point2D bR = new Point2D.Float((float)unitRectangle.getMaxX(), (float)unitRectangle.getMaxY());
			
			ArrayList<Double> dists = new ArrayList<Double>();
			dists.add(impact.distance(tL)); dists.add(impact.distance(tR)); 
			dists.add(impact.distance(bL)); dists.add(impact.distance(bR));
			if(impact.getY() > tL.getY() && impact.getY() < bL.getY()){
				dists.add(Math.abs(tL.getX() - (double)x));
				dists.add(Math.abs(tR.getX() - (double)x));
			}
			if(impact.getX() > tL.getX() && impact.getX() < tR.getX()){
				dists.add(Math.abs(tL.getY() - (double)y));
				dists.add(Math.abs(bR.getY() - (double)y));
			}
			double minDist = UsefulFunctions.doubleMin(dists);
			
			Rectangle rect1 = new Rectangle((int)tL.getX(), (int)tL.getY() - (int)r, (int)bR.getX(), (int)bR.getY() + (int)r);
			Rectangle rect2 = new Rectangle((int)tL.getX() - (int)r, (int)tL.getY(), (int)bR.getX() + (int)r, (int)bR.getY());
			if(rect1.contains((float)impact.getX(), (float)impact.getY()) || rect2.contains((float)impact.getX(), (float)impact.getY()) || minDist < r){				
				Object[] toAdd = {unit, minDist};
				toReturn.add(toAdd);
			}			
		}
		
		return toReturn;
	}
	
	public void damageUnits(Point point, int damage) {
		final int offSet = 5;
		for (int i = 0; i < numberOfPlayers; i++) {
			Army army = armies.get(i);
			for (int j = 0; j < army.units.size(); j++) {
				Unit unit = army.units.get(j);
				Rectangle unitRectangle = unit.getUnitRectangle();
				Rectangle rectangle = new Rectangle(unitRectangle.getX()-offSet, unitRectangle.getY()-offSet, unitRectangle.getWidth()+2*offSet, unitRectangle.getMaxX()+2*offSet);
				if (rectangle.contains((float)point.getX(), (float)point.getY())) {
					unit.decreaseHealth(damage);
					if (!unit.getCurrentAttributes().isAlive) 
					{
						unit.dirty = true;
						//army.units.remove(j);
						//army.done.remove(j);
						//unitAnimations.get(i).remove(j);
					}
				}
			}
		}
		/*
		for (Army army : armies) {
			if (army.isLost()) {
				battleEnded = true;
			}
		}
		*/
	}
	
	@SuppressWarnings("unchecked")
	public void unapply() throws JSONException
	{
		
		// Adjust variables in the battle server
		
		
		//System.out.println("armies == null");
		System.out.println("Stack size: " + moveStack.size());
		Memento currentState;
		boolean[][] dirty_terrain;
		boolean terrain_dirty;
		if(hasActed){
			currentState = moveStack.peek();
			if(physics.dirtyTerrain != null){
				dirty_terrain = physics.dirtyTerrain.clone();
			}else{
				dirty_terrain = null;
			}			
			terrain_dirty = physics.isDirty;
			physics.dirtyTerrain = null;
		}else if(moveStack.size() > 1){						
			currentState = moveStack.pop();
			dirty_terrain = currentState.worldState.dirtyTerrain;
			terrain_dirty = currentState.worldState.isDirty;
		}else{
			return;
		}
		if (!singlePlayer) {
			battleServer.unapplyMemento(moveStack.peek().mementoCount);
		}
		System.out.println("Current state: " + currentState);
		System.out.println("Stack size: " + moveStack.size());
		Memento lastState = moveStack.peek();
		System.out.println("Last state: " + lastState);
		if(lastState != null)
		{
			System.out.println(lastState.unit_positions == null);
			for(int i = 0; i < armies.size(); i++)
			{
				//System.out.println(currentState.army_energies[i]);
				armies.get(i).setEnergy(lastState.army_energies[i]);
				//System.out.println(armies.get(i).currentEnergy);
				for(int j = 0; j < armies.get(i).units.size(); j++)
				{
					armies.get(i).units.get(j).setHealth(lastState.unit_health.get(i)[j]);
					//armies.get(i).units.get(j).setUnitPosition(lastState.unit_positions.get(i)[j].x, 
					//								   lastState.unit_positions.get(i)[j].y);
					Vec2 pos = new Vec2(lastState.unit_positions.get(i)[j].x, lastState.unit_positions.get(i)[j].y);
					boolean b = unitAnimations.get(i).get(j).body.isAwake();
					unitAnimations.get(i).get(j).body.setAwake(true);
					armies.get(i).units.get(j).setUnitPosition(pos.x, pos.y);
					pos = pos.add(unitAnimations.get(i).get(j).offset);
					armies.get(i).units.get(j).body.setTransform(pos, 0);
					//armies.get(i).units.get(j).body.getPosition().set(pos.x, pos.y);
					armies.get(i).done.set(j, lastState.unit_done.get(i)[j]);
					armies.get(i).units.get(j).setDirt(lastState.unit_dirty.get(i)[j]);
					unitAnimations.get(i).get(j).body.setAwake(b);
				}
			}
			if(terrain_dirty){				
				ArrayList<Vec2> vecs = new ArrayList<Vec2>();
				if(dirty_terrain != null){
				for(int i = 0; i < dirty_terrain.length; i++){
					for(int j = 0; j < dirty_terrain[0].length; j++){
						if(dirty_terrain[i][j]){
							vecs.add(new Vec2(i, j));
							_terrain.ModifyTerrain(new Vec2(i, j), (byte)-1);
						}
					}
				}
			}
			_terrain.RegenerateTerrain();
			physics.revertCollisionBoard(vecs);
			lastState = null;
			currentState = null;
			}
		}
		hasActed = false;
		int temp = 0;
		for(Unit unit : armies.get(playerNum).units){
			temp += unit.dirty ? 0 : 1;
		}
		if(temp != 0){
			outOfUnits = false;
		}
		//moveStack.push(new Memento(armies, physics));
		
	}
	
	public void loadFromServer(int delta) throws Exception {
		if (!battleServer.waitOnLoad(delta)) {
			BattleStateUpdate update = battleServer.loadStateFromServer(battleKey, turnCount, armies, unitAnimations, physics, _terrain);
			if (update.needUpdate) {
				battleEnded = update.battleEnded;
				winner = update.winner;
				turnCount = update.turnCount;
				battleAnimator.setAnimatingFrameList(update.framesToBeAnimated);
				needToBeAnimatted = true;
				if (loadPastExplosions) {
					System.out.println("Draw past explosions");
					battleAnimator.loadExplosions(battleServer.getExplosionList(), turnCount, physics, _terrain);
					loadPastExplosions = false;
				}
			}
			
		}
	}
	
	public void killDirtyCritters()
	{
		for(int i = 0; i < armies.size(); i++)
		{
			Army army = armies.get(i);
			for(int j = 0; j < army.units.size(); j++)
			{
				if(army.units.get(j).dirty)
				{
					world.destroyBody(army.units.get(j).body);
					army.units.remove(j);
					army.done.remove(j);
					unitAnimations.get(i).remove(j);
					j--;
					//unit = null;
				}
			}
		}
	}
	
	private void initLevels() throws SlickException{
		// TODO: add starting positions to these levels and give them images
		Vec2[] starting0 = new Vec2[8];
		Vec2[] starting1 = new Vec2[8];
		Vec2[] starting2 = new Vec2[8];
		
		//for(int i = 0; i < 8; i++){
		//	starting0[i]=new Vec2(500,300);
		//}
		
		starting0[0]=new Vec2(600,350);
		starting0[1]=new Vec2(700,350);
		starting0[2]=new Vec2(750,300);
		starting0[3]=new Vec2(550,340);
		starting0[4]=new Vec2(400,300);
		starting0[5]=new Vec2(300,300);
		starting0[6]=new Vec2(500,300);
		starting0[7]=new Vec2(200,200);
		
		starting1[0]=new Vec2(950,350);
		starting1[1]=new Vec2(1000,350);
		starting1[2]=new Vec2(1020,250);
		starting1[3]=new Vec2(1000,100);
		starting1[4]=new Vec2(150,350);
		starting1[5]=new Vec2(100,350);
		starting1[6]=new Vec2(80,300);
		starting1[7]=new Vec2(100,100);
		
		starting2[0]=new Vec2(100,60);
		starting2[1]=new Vec2(100,200);
		starting2[2]=new Vec2(100,400);
		starting2[3]=new Vec2(450,300);
		starting2[4]=new Vec2(700,300);
		starting2[5]=new Vec2(1000,60);
		starting2[6]=new Vec2(1000,150);
		starting2[7]=new Vec2(1000,350);
		
		Image back0 = new Image("images/mushroomsbackground.png");
		Image fore0 = new Image("images/mushroomsforeground.png");
		Image fore1 = new Image("images/test1.png");
		Image fore2 = new Image("images/test2.png");
		
		
		levels[0] = new Level(starting0, fore0, back0);
		levels[1] = new Level(starting1, fore1, back0);
		levels[2] = new Level(starting2, fore2, back0);
		return;
	}
}
