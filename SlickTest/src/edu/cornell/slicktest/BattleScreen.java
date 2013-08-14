package edu.cornell.slicktest;

// import MSTerrain;
// import PhysicsUnitFactory;
// import Unit;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import net.phys2d.math.Vector2f;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

public class BattleScreen {
	
	// Using server or not
	boolean singlePlayer = true;
	
	// Some positions to draw for debugging terrain
	private ArrayList<Vec2> draw_this = new ArrayList<Vec2>();
	private ArrayList<Vec2> draw_units = new ArrayList<Vec2>();
	
	private static final int VELOCITY_ITERATIONS = 8;

	private static final int POSITION_ITERATIONS = 3;
	
	public static ArrayList<Vec2[]> terrain_vecs;
	public static ArrayList<Vec2> terrain_pos;

	ArrayList<ArrayList<UnitAnimation>> unitAnimations = new ArrayList<ArrayList<UnitAnimation>>();
	
	
	
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
	
	final int healthBarOffsetY = 10;
	final int healthBarHeight = 5;
	final int healthBarWidth = 30;
	
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
	
	Image background;
	ImageBuffer foregroundbuffer;
	Image greenCursor;
	Image redCursor;
	int worldWidth;
	int worldHeight;
	
	int overLapCount = 0;
	
	boolean battleEnded = false;
	
	String battleKey;
	int playerNum;
	String playerName;
	BattleServerCommunication battleServer;
	
	int frameInterval = 0;
	
	boolean animatingFrames = false;
	boolean needToBeAnimatted = false;
	
	BattleAnimator battleAnimator;
	
	public BattleScreen() {
	}

	public void init(GameContainer container) throws SlickException, IOException, JSONException, InterruptedException {
		world = new World(new Vec2(0, 70), true);
		
		//ContactListener listener = null;
		//world.setContactListener(listener);
		
		background = new Image("images/mushroomsbackground.png");
		Image foreground = new Image("images/mushroomsforeground.png");
		//Image foreground = new Image("images/test.png");

		worldWidth = foreground.getWidth();
		worldHeight = foreground.getHeight();
		
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
		
		Unit unit1 = Factory.getUnit(Units.SPACEMARINE, "Marine 1");
		Body physicsUnit1 = PhysicsFactory.makeUnit(world, Units.SPACEMARINE, container, physics, 1);
		
		Unit unit2 = Factory.getUnit(Units.SPACEMARINE, "Marine 2");
		Body physicsUnit2 = PhysicsFactory.makeUnit(world, Units.SPACEMARINE, container, physics, 1);
		
		Unit unit3 = Factory.getUnit(Units.SPACEMARINE, "Marine 3");
		Body physicsUnit3 = PhysicsFactory.makeUnit(world, Units.SPACEMARINE, container, physics, 1);
		
		Unit unit4 = Factory.getUnit(Units.SPACEMARINE, "Marine 4");
		Body physicsUnit4 = PhysicsFactory.makeUnit(world, Units.SPACEMARINE, container, physics, 1);
		
		unit1.initBattleStates(UnitState.STAND, Direction.LEFT, new Rectangle(300, 275, 75, 100));
		unit2.initBattleStates(UnitState.STAND, Direction.LEFT, new Rectangle(300, 275, 75, 100));
		unit3.initBattleStates(UnitState.STAND, Direction.LEFT, new Rectangle(300, 275, 75, 100));
		unit4.initBattleStates(UnitState.STAND, Direction.LEFT, new Rectangle(300, 275, 75, 100));
		
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
		
		Army army1 = new Army(units1, 20);
		army1.side = 0;
		army1.refreshEnergy();
		armies.add(army1);

		Unit unit11 = Factory.getUnit(Units.FAIRY, "Fairy 1");
		physicsUnit1 = PhysicsFactory.makeUnit(world, Units.SPACEMARINE, container, physics, 1);
		Unit unit12 = Factory.getUnit(Units.FAIRY, "Fairy 2");
		physicsUnit2 = PhysicsFactory.makeUnit(world, Units.SPACEMARINE, container, physics, 1);
		Unit unit13 = Factory.getUnit(Units.FAIRY, "Fairy 3");
		physicsUnit3 = PhysicsFactory.makeUnit(world, Units.SPACEMARINE, container, physics, 1);
		Unit unit14 = Factory.getUnit(Units.FAIRY, "Fairy 4");
		physicsUnit4 = PhysicsFactory.makeUnit(world, Units.SPACEMARINE, container, physics, 1);
		
		unit11.initBattleStates(UnitState.STAND, Direction.RIGHT, new Rectangle(300, 275, 75, 100));
		unit12.initBattleStates(UnitState.STAND, Direction.RIGHT, new Rectangle(300, 275, 75, 100));
		unit13.initBattleStates(UnitState.STAND, Direction.RIGHT, new Rectangle(300, 275, 75, 100));
		unit14.initBattleStates(UnitState.STAND, Direction.RIGHT, new Rectangle(300, 275, 75, 100));

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
		
		Army army2 = new Army(units2, 20);
		army2.side = 1;
		army2.refreshEnergy();
		armies.add(army2);
		
		numberOfPlayers = armies.size();
		
		// Create UnitAnimation object for each unit
		battleInput = new BattleInputHandler();
		moveStack = new Stack<Memento>();
		
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
		moveStack.push(new Memento(armies));
		System.out.println("Initial memento: " + moveStack.peek());
		
		int randomInt = (int) (Math.random()*1000000);
		playerName = "Player " + randomInt;
		
		
		// Multiplayer
		if (!singlePlayer) {
			battleServer = new BattleServerCommunication();
			battleAnimator = new BattleAnimator();
			BattleJoinResult join = battleServer.startBattle(playerName);
			battleKey = join.battleKey;
			playerNum = join.playerNumber;
			turnCount = -1;
			if (playerNum == 0) {
				turnCount = 0;
				battleServer.storeStateToServer(battleKey, battleEnded, turnCount, playerNum, armies);
			}
		}
		else {
			turnCount = 0;
		}
	}
	
	public void render(GameContainer container, Graphics graphics) throws SlickException {
		
		if (turnCount < 0) {
			return;
		}
				
		graphics.drawImage(background, 0, 0);
		graphics.drawImage(physics.getUpdatedForeground(), 0, 0);
		
		int playerNumber = turnCount%numberOfPlayers;
		Army currentArmy= armies.get(playerNumber);
		
		graphics.setColor(Color.white);
		graphics.drawString("Turn: " + turnCount, 350, 20);
		graphics.drawString("Unit: " + currentUnitNumber, 350, 40);		
		
		graphics.drawString("Press Tab Key - Swap Unit", 450, 40);
		graphics.drawString("Press Enter Key - Commit Turn", 450, 60);
		graphics.drawString("Press Arrow Keys - Movement and Jump", 450, 80);
		graphics.drawString("Press Backspace - Undo Single Move", 450, 100);
		graphics.drawString("Press Shift + Backspace - Undo All Moves", 450, 120);
		graphics.drawString("Mouse Left Click - Shoot", 450, 140);
			
		
		// Energy bar
		graphics.setColor(Color.blue);
		graphics.drawRect((float)energyBarOffset.getX(), (float)energyBarOffset.getY(), (float)energyBarWidth, (float)energyBarHeight);
		float energyBar = (float)energyBarHeight*(float)currentArmy.currentEnergy/(float)currentArmy.maxEnergy;
		graphics.fillRect((float)energyBarOffset.getX(), (float)(energyBarOffset.getY()+energyBarHeight-energyBar), (float)energyBarWidth, energyBar);
		
		//Check if energy empty
		if(armies.get(playerNumber).currentEnergy <= 0)
		{
			graphics.setColor(Color.red);
			graphics.drawString("Out of energy! Press Enter to submit turn.", 300, 150);
		}
		
		// Draw cursor
		UnitAnimation currentUnitAnimation = unitAnimations.get(playerNumber).get(currentUnitNumber);
		Rectangle currentUnitRectangle = currentUnitAnimation.unit.getUnitRectangle();
		int cursorX = (int)currentUnitRectangle.getX() - 20;
		int cursorY = (int)currentUnitRectangle.getY() - 20;
		//if(currentUnitAnimation.unit.)
		graphics.drawImage(greenCursor, cursorX, cursorY);
		
		//Projected Path
		boolean multiplayerAllow = singlePlayer || (playerNum == playerNumber && !needToBeAnimatted && ! animatingFrames);
		if (multiplayerAllow && currentUnitAnimation.unit.getUnitState() == UnitState.STAND && !currentArmy.done.get(currentUnitNumber)) {
			graphics.setColor(Color.white);
			double mouseX = container.getInput().getMouseX();
			double mouseY = container.getInput().getMouseY();
			ProjectedPath projectedPath = physics.projectedPath(mouseX, mouseY, currentUnitAnimation.unit.getUnitRectangle());
			if (projectedPath.collidedWithUnit) {
				graphics.setColor(Color.red);
			}
			for (int i = 0; i < projectedPath.matrix.length; i++) {
				graphics.drawOval((int)projectedPath.matrix[i][0], (int)projectedPath.matrix[i][1], 7, 7);
			}
		}
		
		// Draw all units
		for (int i  = 0; i < numberOfPlayers; i++) {
			int side = armies.get(i).side;
			ArrayList<UnitAnimation> armyAnimation = unitAnimations.get(i);
			for (UnitAnimation unitAnimation : armyAnimation) {
				//if (unitAnimation.unit.currentAttributes.isAlife) {
					unitAnimation.render(container, graphics);
					if (unitAnimation.unit.getUnitState() == UnitState.SHOOT) {
						Point projectilePoint;
						if (animatingFrames) {
							projectilePoint = battleAnimator.getAnimatedProjectilePoint();
							projectileAnimation = battleAnimator.getAnimatedProjectileAnimation();
						}
						else {
							projectilePoint = physics.getProjectilePositionPoint();
						}
						if (projectileAnimation != null && projectilePoint != null) {
							projectileAnimation.draw(projectilePoint.x, projectilePoint.y);
						}
					}
					
					// Draw Army Symbol
					Rectangle unitRect = unitAnimation.unit.getUnitRectangle();
					int pointX = (int)unitRect.getX();
					int pointY = 0;
					pointY = (int)unitRect.getY() + 60;
					graphics.drawString("Army " + side, pointX, pointY);
				//}
			}
		}
		
		// Debugging terrain drawing
		/*
		for(Vec2 pos : draw_this){
			graphics.setColor(Color.red);
			Polygon poly = new Polygon();
			graphics.drawRect(pos.x, pos.y, 25, 25);
		}
		*/
		graphics.setColor(Color.red);
		for(int i = 0; i < terrain_pos.size(); i++){
			Polygon poly = new Polygon();
			for(int j = 0; j < terrain_vecs.get(i).length; j++){
				float x = terrain_vecs.get(i)[j].x;
				float y = terrain_vecs.get(i)[j].y;
				poly.addPoint(x, y);
			}
			poly.setCenterX(terrain_pos.get(i).x + 5f);
			poly.setCenterY(terrain_pos.get(i).y + 5f);
			graphics.draw(poly);
		}
		
		for(Vec2 pos : draw_units){
			graphics.setColor(Color.green);
			graphics.drawRect(pos.x - 37.5f, pos.y - 75, 100, 150);
		}
		
		
		//Multiplayer
		if (!singlePlayer) {
			graphics.setColor(Color.green);
			graphics.drawString("Player " + playerNum, 30, 100);
			if (playerNum != playerNumber) {
				graphics.drawString("Please wait for the other player.", 30, 200);
			}
			else if (needToBeAnimatted) {
				graphics.drawString("Press Enter to animate opponent's moves.", 30, 200);
			}
			else if (animatingFrames) {
				graphics.drawString("Animating opponent's moves.", 30, 200);
			}
		}
	}
	
	
	public void update(GameContainer container, int delta) throws SlickException, IOException, JSONException, InterruptedException {
		
		//Multiplayer
		if (!singlePlayer) {
			
			frameInterval += delta;
			if (animatingFrames && frameInterval > 30) {
				//if () {
					animatingFrames = battleAnimator.animateNextFrame(armies, unitAnimations, physics, _terrain);
					frameInterval = 0;
				//}
				return;
			}
			else if (frameInterval > 30) {
				battleServer.storeFrame(armies, physics);
				frameInterval = 0;
			}
		}
		
		if (turnCount < 0) {
			//Multiplayer
			if (!singlePlayer) {
				loadFromServer(delta);
			}
			return;
		}
		
		int playerNumber = turnCount%numberOfPlayers;
		
		//Multiplayer
		if (!singlePlayer) {
			if (playerNumber != playerNum) {
				loadFromServer(delta);
				return;
			}
		}
		
		physics.updateCollisionUnits(armies);
		
		if (container.getInput().isKeyPressed(Input.KEY_ENTER)) {
			
			if (needToBeAnimatted) {
				needToBeAnimatted = false;
				animatingFrames = true;
			}
			else {
				armies.get(turnCount%numberOfPlayers).refreshEnergy();
				turnCount++;
				currentUnitNumber = 0;
				moveStack.empty();
				moveStack.push(new Memento(armies));
			}
			
			//Multiplayer
			if (!singlePlayer) {
				battleServer.storeStateToServer(battleKey, battleEnded, turnCount, playerNumber, armies);
			}
			battleServer.readyForNewTurn();
		}

		if (container.getInput().isKeyPressed(Input.KEY_TAB)) {
			armies.get(playerNumber).units.get(currentUnitNumber).setUnitState(UnitState.STAND);
			currentUnitNumber++;
			currentUnitNumber = currentUnitNumber % armies.get(playerNumber).units.size();
			moveStack.push(new Memento(armies));
			System.out.println("Memoizing: " + moveStack.peek());
			
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
			if(moveStack.size() > 1)
			{
				unapply();
			}
		}
		
		// physics.updateGravity(armies);
		
		UnitAnimation currentUnitAnimation = unitAnimations.get(playerNumber).get(currentUnitNumber);
		Rectangle currentUnitRectangle = currentUnitAnimation.unit.getUnitRectangle();
		
		
		BattleMouseClickResult mouseResult = battleInput.handleMouseClick(container, armies.get(turnCount%numberOfPlayers));
		
		if (mouseResult.unitIsSelected) {
			currentUnitNumber = mouseResult.unitSelected;
		}
		
		if (armies.get(playerNumber).currentEnergy > 0 && !armies.get(playerNumber).done.get(currentUnitNumber)) 
		{ 
			float prevMovement = currentUnitAnimation.unit.getUnitRectangle().getX();
			battleInput.handleUnitControl(container, delta, physics.getGround(), currentUnitAnimation.unit);
			float afterMovement = currentUnitAnimation.unit.getUnitRectangle().getX();
			if (prevMovement != afterMovement) {
				physics.updateCollisionUnits(armies);
			}
			
			if (mouseResult.mouseClicked && !mouseResult.unitIsSelected) 
			{
				
				if (currentUnitAnimation.unit.getUnitState() == UnitState.STAND /*|| currentUnitAnimation.unit.state == UnitState.WALK*/) 
				{
					currentUnitAnimation.unit.setUnitState(UnitState.SHOOT);
					armies.get(playerNumber).done.set(currentUnitNumber, true);
					Ability baseAbility = currentUnitAnimation.unit.getBaseAbility();
					projectileAnimation = new Animation(baseAbility.getSpriteSheet(), 150);
					ProjectedPath projPath = physics.projectedPath(mouseResult.mouseX, mouseResult.mouseY, currentUnitRectangle);
					physics.createProjectile((int)currentUnitRectangle.getX(), (int)currentUnitRectangle.getY(), mouseResult.mouseX, mouseResult.mouseY, false, 
							currentUnitAnimation.unit.getUnitDirection(), 
							currentUnitAnimation.unit, projPath.collision_x, projPath.collision_y);
					double diffX = currentUnitRectangle.getX() - mouseResult.mouseX;
					double diffY = currentUnitRectangle.getY() - mouseResult.mouseY;
					double distance = Math.sqrt(diffX*diffX + diffY*diffY);
					armies.get(playerNumber).currentEnergy -= baseAbility.getEnergyCost() + (int)(distance/100.0);
					
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
				unitAnimation.updateAnimation(container, delta, physics.getGround());
			}
		}
		
		if (battleInput.getDeltaMovement() >= 50.0) {
			armies.get(playerNumber).currentEnergy--;
			battleInput.resetDeltaMovement();
			if (armies.get(playerNumber).currentEnergy == 0) {
				currentUnitAnimation.unit.setUnitState(UnitState.STAND);
			}
		}
		
		UnitState state = currentUnitAnimation.unit.getUnitState();
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
				armies.get(playerNumber).currentEnergy -= 2;
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
				physics.updateCollisionBoard(collisionPoint.x, collisionPoint.y, physics.getProjectileSplashRadius());
				physics.updateCollisionTerrain(new Vec2(collisionPoint.x, collisionPoint.y), _terrain);
				//physics.terrainExplosion(40, projectilePoint.x, projectilePoint.y);
				physics.unitExplosion(physicsUnits, impact, 15000*physicsUnits.get(0).m_mass, 25);
				damageUnits(collisionPoint, currentUnitAnimation.unit.getBaseAbility().getPower());
				
				//Multiplayer
				if (!singlePlayer) {
					battleServer.addExplosion(40, projectilePoint.x, projectilePoint.y);
				}
			}
			if (projectileState != ProjectileState.INAIR) {
				currentUnitAnimation.unit.setUnitState(UnitState.STAND);
			}
			//physics.updateProjectile(delta);
		}
		
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
				non_zero_mass.add(lst);
				nzm_pos.add(lst.getPosition());
			}else{
				zm_pos.add(lst.getPosition());
			}
			lst = lst.getNext();
		}
		draw_this = zm_pos;
		draw_units = nzm_pos;
		world.step(1f/60f, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
		
		// world.step(delta, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
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
					if (!unit.getCurrentAttributes().isAlive) {
						army.units.remove(j);
						army.done.remove(j);
						unitAnimations.get(i).remove(j);
						j--;
					}
				}
			}
		}
		
		for (Army army : armies) {
			if (army.isLost()) {
				battleEnded = true;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void unapply()
	{
		System.out.println("Stack size: " + moveStack.size());
		Memento currentState = moveStack.pop();
		System.out.println("Current state: " + currentState);
		System.out.println("Stack size: " + moveStack.size());
		Memento lastState = moveStack.peek();
		System.out.println("Last state: " + lastState);
		if(lastState != null)
		{
			System.out.println(lastState.toString());
			for(int i = 0; i < armies.size(); i++)
			{
				Army thisArmy = armies.get(i);
				Army mementoArmy = lastState.armies.get(i);
				//worldstate.armies.set(i, lastState.armies.get(i));
				//Fix units and their states
				thisArmy.units = (ArrayList<Unit>) mementoArmy.units.clone();
				//Fix done flags
				thisArmy.done = (ArrayList<Boolean>) mementoArmy.done.clone();
				//Fix maxhealth
				thisArmy.maxHealth = mementoArmy.maxHealth;
				//Fix currenthealth
				thisArmy.currentHealth = mementoArmy.currentHealth;
				//Fix maxenergy
				thisArmy.maxEnergy = mementoArmy.maxEnergy;
				//Fix currentenergy
				System.out.println(thisArmy.currentEnergy);
				System.out.println(mementoArmy.currentEnergy);
				thisArmy.currentEnergy = mementoArmy.currentEnergy;
				//Fix side
				thisArmy.side = mementoArmy.side;
				armies.set(i, thisArmy);
			}
			physics = lastState.worldState;
			lastState = null;
			currentState = null;
		}
		//moveStack.push(new Memento(armies, physics));
		
	}
	
	public void loadFromServer(int delta) throws IOException, JSONException, InterruptedException {
		if (!battleServer.waitOnLoad(delta)) {
			BattleStateUpdate update = battleServer.loadStateFromServer(battleKey, turnCount, armies, unitAnimations, physics, _terrain);
			if (update.needUpdate) {
				battleEnded = update.battleEnded;
				turnCount = update.turnCount;
				battleAnimator.setAnimatingFrameList(update.framesToBeAnimated);
				needToBeAnimatted = true;
			}
		}
	}
}
