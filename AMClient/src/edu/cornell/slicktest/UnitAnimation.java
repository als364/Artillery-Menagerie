package edu.cornell.slicktest;

import java.awt.Font;
import java.awt.Point;
import java.util.ArrayList;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Rectangle;

import edu.cornell.slicktest.Enums.*;

public class UnitAnimation {
	
	final float healthBarOffsetY = 10;
	final float healthBarHeight = 5;
	final float healthBarWidth = 30;
	final float nameOffsetY = 25;
	//final float armySymbolOffsetX = 10;
	float scale;
	
	Unit unit;
	SpriteSheet sheet;
	protected Animation walkLeftAnimation;
	protected Animation walkRightAnimation;
	protected Animation standLeftAnimation;
	protected Animation standRightAnimation;
	protected Animation shootLeftAnimation;
	protected Animation shootRightAnimation;
	protected Animation jumpLeftAnimation;
	protected Animation jumpRightAnimation;
	protected ArrayList<Animation> equipmentAnimationsLeft;
	protected ArrayList<Animation> equipmentAnimationsRight;
	protected float initX;//initial x-position
	protected double deltaMovement;
	protected float jumpMovement;
	protected double projectileX;
	protected double projectileY;
	protected int projectileStartX;
	protected int projectileStartY;
	protected int projectileDestX;
	protected int projectileDestY;
	protected Body body;
	Vec2 offset;
	
	protected boolean mid_jump = false;
	
	public UnitAnimation(Unit unit, GameContainer container)  throws SlickException {
		scale = BattleScreen.scale;
		this.unit = unit;
		switch(unit.factoryKey)
		{
		case DEFAULT:
			sheet = new SpriteSheet(unit.getCurrentAttributes().spritesheetPath, 36, 65);
			walkLeftAnimation = new Animation(sheet, 150);
			walkRightAnimation = new Animation();
			standLeftAnimation = new Animation();
			standLeftAnimation.addFrame(sheet.getSprite(7,0), 150);
			jumpLeftAnimation = new Animation();
			jumpLeftAnimation.addFrame(sheet.getSprite(7,0), 150);
			jumpRightAnimation = new Animation();
			jumpRightAnimation.addFrame(sheet.getSprite(0, 2).getFlippedCopy(true, false), 50);
			shootLeftAnimation = new Animation();
			shootLeftAnimation.addFrame(sheet.getSprite(0,0),150);
			shootRightAnimation = new Animation();
			shootRightAnimation.addFrame(sheet.getSprite(0, 2).getFlippedCopy(true, false), 50);
			break;
		case SPACEMARINE:
			sheet = new SpriteSheet(unit.getCurrentAttributes().spritesheetPath, 100, 150);
			walkLeftAnimation = new Animation();
			walkRightAnimation = new Animation();
			standLeftAnimation = new Animation();
			standRightAnimation = new Animation();
			for(int i = 0; i < 16; i++)
			{
				walkLeftAnimation.addFrame(sheet.getSprite(i, 0), 50);
				walkRightAnimation.addFrame(sheet.getSprite(i, 1), 50);
				standLeftAnimation.addFrame(sheet.getSprite(i, 2), 50);
				standRightAnimation.addFrame(sheet.getSprite(i, 3), 50);
			}
			jumpLeftAnimation = new Animation();
			jumpLeftAnimation.addFrame(sheet.getSprite(0, 2), 50);
			jumpRightAnimation = new Animation();
			jumpRightAnimation.addFrame(sheet.getSprite(0, 2).getFlippedCopy(true, false), 50);
			shootLeftAnimation = new Animation();
			shootLeftAnimation.addFrame(sheet.getSprite(0, 2), 50);
			shootRightAnimation = new Animation();
			shootRightAnimation.addFrame(sheet.getSprite(0, 2).getFlippedCopy(true, false), 50);
			break;
		case CENTAUR:
			sheet = new SpriteSheet(unit.getCurrentAttributes().spritesheetPath, 175, 150);
			walkLeftAnimation = new Animation();
			walkRightAnimation = new Animation();
			standLeftAnimation = new Animation();
			standRightAnimation = new Animation();
			for(int i = 0; i < 16; i++)
			{
				walkLeftAnimation.addFrame(sheet.getSprite(i, 0), 50);
				walkRightAnimation.addFrame(sheet.getSprite(i, 1), 50);
				standLeftAnimation.addFrame(sheet.getSprite(i, 2), 50);
				standRightAnimation.addFrame(sheet.getSprite(i, 3), 50);
			}
			jumpLeftAnimation = new Animation();
			jumpLeftAnimation.addFrame(sheet.getSprite(0, 2), 50);
			jumpRightAnimation = new Animation();
			jumpRightAnimation.addFrame(sheet.getSprite(0, 2).getFlippedCopy(true, false), 50);
			shootLeftAnimation = new Animation();
			shootLeftAnimation.addFrame(sheet.getSprite(0, 2), 50);
			shootRightAnimation = new Animation();
			shootRightAnimation.addFrame(sheet.getSprite(0, 2).getFlippedCopy(true, false), 50);
			break;
		case UNICORN:
			sheet = new SpriteSheet(unit.getCurrentAttributes().spritesheetPath, 175, 150);
			walkLeftAnimation = new Animation();
			walkRightAnimation = new Animation();
			standLeftAnimation = new Animation();
			standRightAnimation = new Animation();
			for(int i = 0; i < 16; i++)
			{
				walkLeftAnimation.addFrame(sheet.getSprite(i, 0), 50);
				walkRightAnimation.addFrame(sheet.getSprite(i, 1), 50);
				standLeftAnimation.addFrame(sheet.getSprite(i, 2), 50);
				standRightAnimation.addFrame(sheet.getSprite(i, 3), 50);
			}
			jumpLeftAnimation = new Animation();
			jumpLeftAnimation.addFrame(sheet.getSprite(0, 2), 50);
			jumpRightAnimation = new Animation();
			jumpRightAnimation.addFrame(sheet.getSprite(0, 2).getFlippedCopy(true, false), 50);
			shootLeftAnimation = new Animation();
			shootLeftAnimation.addFrame(sheet.getSprite(0, 2), 50);
			shootRightAnimation = new Animation();
			shootRightAnimation.addFrame(sheet.getSprite(0, 2).getFlippedCopy(true, false), 50);
			break;
		case DRAGON:
			sheet = new SpriteSheet(unit.getCurrentAttributes().spritesheetPath, 175, 150);
			walkLeftAnimation = new Animation();
			walkRightAnimation = new Animation();
			standLeftAnimation = new Animation();
			standRightAnimation = new Animation();
			for(int i = 0; i < 16; i++)
			{
				walkLeftAnimation.addFrame(sheet.getSprite(i, 0), 50);
				walkRightAnimation.addFrame(sheet.getSprite(i, 1), 50);
				standLeftAnimation.addFrame(sheet.getSprite(i, 2), 50);
				standRightAnimation.addFrame(sheet.getSprite(i, 3), 50);
			}
			jumpLeftAnimation = new Animation();
			jumpLeftAnimation.addFrame(sheet.getSprite(0, 2), 50);
			jumpRightAnimation = new Animation();
			jumpRightAnimation.addFrame(sheet.getSprite(0, 2).getFlippedCopy(true, false), 50);
			shootLeftAnimation = new Animation();
			shootLeftAnimation.addFrame(sheet.getSprite(0, 2), 50);
			shootRightAnimation = new Animation();
			shootRightAnimation.addFrame(sheet.getSprite(0, 2).getFlippedCopy(true, false), 50);
			break;
		case FAIRY:
			sheet = new SpriteSheet(unit.getCurrentAttributes().spritesheetPath, 100, 150);
			walkLeftAnimation = new Animation();
			walkRightAnimation = new Animation();
			standLeftAnimation = new Animation();
			standRightAnimation = new Animation();
			for(int i = 0; i < 16; i++)
			{
				walkLeftAnimation.addFrame(sheet.getSprite(i, 0), 50);
				walkRightAnimation.addFrame(sheet.getSprite(i, 1), 50);
				standLeftAnimation.addFrame(sheet.getSprite(i, 2), 50);
				standRightAnimation.addFrame(sheet.getSprite(i, 3), 50);
			}
			jumpLeftAnimation = new Animation();
			jumpLeftAnimation.addFrame(sheet.getSprite(0, 2), 50);
			jumpRightAnimation = new Animation();
			jumpRightAnimation.addFrame(sheet.getSprite(0, 2).getFlippedCopy(true, false), 50);
			shootLeftAnimation = new Animation();
			shootLeftAnimation.addFrame(sheet.getSprite(0, 2), 50);
			shootRightAnimation = new Animation();
			shootRightAnimation.addFrame(sheet.getSprite(0, 2).getFlippedCopy(true, false), 50);
			break;
		case ROBOT:
			sheet = new SpriteSheet(unit.getCurrentAttributes().spritesheetPath, 100, 150);
			walkLeftAnimation = new Animation();
			walkRightAnimation = new Animation();
			standLeftAnimation = new Animation();
			standRightAnimation = new Animation();
			for(int i = 0; i < 16; i++)
			{
				walkLeftAnimation.addFrame(sheet.getSprite(i, 0), 50);
				walkRightAnimation.addFrame(sheet.getSprite(i, 1), 50);
				standLeftAnimation.addFrame(sheet.getSprite(i, 2), 50);
				standRightAnimation.addFrame(sheet.getSprite(i, 3), 50);
			}
			jumpLeftAnimation = new Animation();
			jumpLeftAnimation.addFrame(sheet.getSprite(0, 2), 50);
			jumpRightAnimation = new Animation();
			jumpRightAnimation.addFrame(sheet.getSprite(0, 2).getFlippedCopy(true, false), 50);
			shootLeftAnimation = new Animation();
			shootLeftAnimation.addFrame(sheet.getSprite(0, 2), 50);
			shootRightAnimation = new Animation();
			shootRightAnimation.addFrame(sheet.getSprite(0, 2).getFlippedCopy(true, false), 50);
			break;
		case AI:
			sheet = new SpriteSheet(unit.getCurrentAttributes().spritesheetPath, 100, 150);
			walkLeftAnimation = new Animation();
			walkRightAnimation = new Animation();
			standLeftAnimation = new Animation();
			standRightAnimation = new Animation();
			for(int i = 0; i < 16; i++)
			{
				walkLeftAnimation.addFrame(sheet.getSprite(i, 0), 50);
				walkRightAnimation.addFrame(sheet.getSprite(i, 1), 50);
				standLeftAnimation.addFrame(sheet.getSprite(i, 2), 50);
				standRightAnimation.addFrame(sheet.getSprite(i, 3), 50);
			}
			jumpLeftAnimation = new Animation();
			jumpLeftAnimation.addFrame(sheet.getSprite(0, 2), 50);
			jumpRightAnimation = new Animation();
			jumpRightAnimation.addFrame(sheet.getSprite(0, 2).getFlippedCopy(true, false), 50);
			shootLeftAnimation = new Animation();
			shootLeftAnimation.addFrame(sheet.getSprite(0, 2), 50);
			shootRightAnimation = new Animation();
			shootRightAnimation.addFrame(sheet.getSprite(0, 2).getFlippedCopy(true, false), 50);
			break;
		case ALIEN:
			sheet = new SpriteSheet(unit.getCurrentAttributes().spritesheetPath, 100, 150);
			walkLeftAnimation = new Animation();
			walkRightAnimation = new Animation();
			standLeftAnimation = new Animation();
			standRightAnimation = new Animation();
			for(int i = 0; i < 16; i++)
			{
				walkLeftAnimation.addFrame(sheet.getSprite(i, 0), 50);
				walkRightAnimation.addFrame(sheet.getSprite(i, 1), 50);
				standLeftAnimation.addFrame(sheet.getSprite(i, 2), 50);
				standRightAnimation.addFrame(sheet.getSprite(i, 3), 50);
			}
			jumpLeftAnimation = new Animation();
			jumpLeftAnimation.addFrame(sheet.getSprite(0, 2), 50);
			jumpRightAnimation = new Animation();
			jumpRightAnimation.addFrame(sheet.getSprite(0, 2).getFlippedCopy(true, false), 50);
			shootLeftAnimation = new Animation();
			shootLeftAnimation.addFrame(sheet.getSprite(0, 2), 50);
			shootRightAnimation = new Animation();
			shootRightAnimation.addFrame(sheet.getSprite(0, 2).getFlippedCopy(true, false), 50);
			break;
		}

		offset = new Vec2(sheet.getSprite(0, 0).getWidth(), sheet.getSprite(0, 0).getHeight()).mul(.5f*scale);
		// Creating equipment animations/images
		
		equipmentAnimationsLeft = new ArrayList<Animation>();
		equipmentAnimationsRight = new ArrayList<Animation>();
		for (Item equipment : unit.equipments) {
			SpriteSheet sheet = equipment.spriteSheet;
			Animation equipmentAnimationLeft = new Animation();
			equipmentAnimationLeft.addFrame(sheet.getSprite(0, 0), 50);
			equipmentAnimationsLeft.add(equipmentAnimationLeft);
			Animation equipmentAnimationRight = new Animation();
			equipmentAnimationRight.addFrame(sheet.getSprite(0, 0).getFlippedCopy(true, false), 50);
			equipmentAnimationsRight.add(equipmentAnimationRight);
		}
		
	}
	
	public void render(GameContainer container, Graphics graphics, Color armyColor, int screenOffsetX, int screenOffsetY) throws SlickException {
		
		// Health bar
		
		float healthBar = healthBarWidth*(float)unit.getCurrentHealth()/(float)unit.getMaxHealth();
		
		Rectangle unitRectangle = unit.getUnitRectangle();
		
		graphics.setColor(Color.black);
		graphics.fillRect(unitRectangle.getCenterX()-healthBarWidth/2-screenOffsetX, unitRectangle.getY()-healthBarOffsetY-screenOffsetY, healthBarWidth, healthBarHeight);
		graphics.setColor(armyColor);
		graphics.drawRect(unitRectangle.getCenterX()-healthBarWidth/2-screenOffsetX, unitRectangle.getY()-healthBarOffsetY-screenOffsetY, healthBarWidth, healthBarHeight);
		//graphics.setColor(armyColor);
		graphics.fillRect(unitRectangle.getCenterX()-healthBarWidth/2-screenOffsetX, unitRectangle.getY()-healthBarOffsetY-screenOffsetY, healthBar, healthBarHeight);
		//graphics.setColor(armyColor);
		//graphics.drawString(unit.displayName, unitRectangle.getX()-screenOffsetX, unitRectangle.getY()-nameOffsetY-screenOffsetY);
		int nameWidth = AMFonts.getArialBold16().getWidth(unit.displayName);
		AMFonts.getArialBold16().drawString(unitRectangle.getCenterX()-nameWidth/2-screenOffsetX, unitRectangle.getY()+nameOffsetY*3.5f-screenOffsetY, unit.displayName, armyColor);
		
		//Draw army symbol
		//armySymbol.draw(unitRectangle.getCenterX()+nameWidth/2+armySymbolOffsetX-screenOffsetX, unitRectangle.getY()-nameOffsetY-screenOffsetY);
		
		UnitState state = unit.getUnitState();
		Direction direction = unit.getUnitDirection();
		
		// Draw equipments
		
		if (direction == Direction.LEFT) {
			
			for (int i = 0; i < unit.equipments.size(); i++) {
				Item equipment = unit.equipments.get(i);
				Animation equipmentAnimation = equipmentAnimationsLeft.get(i);
				//equipmentAnimation.draw(unitRectangle.getX()-scale*offset.x+equipment.equipPoint.x-screenOffsetX, unitRectangle.getY()+equipment.equipPoint.y-screenOffsetY, equipmentAnimation.getWidth(), equipmentAnimation.getHeight());
				equipmentAnimation.draw(unitRectangle.getX()-screenOffsetX+equipment.equipPoint.x*scale, unitRectangle.getY()-screenOffsetY+equipment.equipPoint.y*scale, equipmentAnimation.getWidth()*scale, equipmentAnimation.getHeight()*scale);
			}
			
		}
		else {
			for (int i = 0; i < unit.equipments.size(); i++) {
				Item equipment = unit.equipments.get(i);
				Animation equipmentAnimation = equipmentAnimationsRight.get(i);
				equipmentAnimation.draw(unitRectangle.getX()+(unitRectangle.getWidth()-equipmentAnimation.getWidth()-equipment.equipPoint.x*scale)-screenOffsetX +equipmentAnimation.getWidth()*(1-scale), unitRectangle.getY()+equipment.equipPoint.y*scale-screenOffsetY, equipmentAnimation.getWidth()*scale, equipmentAnimation.getHeight()*scale);
			}
		}
		
		if ((state == UnitState.JUMP || state == UnitState.FALL) && direction == Direction.LEFT) 
		{
			jumpLeftAnimation.draw(unitRectangle.getX()-screenOffsetX, unitRectangle.getY()-screenOffsetY, unitRectangle.getWidth(), unitRectangle.getHeight());
		}
		else if((state == UnitState.JUMP || state == UnitState.FALL) && direction == Direction.RIGHT)
		{
			jumpRightAnimation.draw(unitRectangle.getX()-screenOffsetX, unitRectangle.getY()-screenOffsetY, unitRectangle.getWidth(), unitRectangle.getHeight());
		}
		else if (state == UnitState.WALK && direction == Direction.LEFT)
		{
			walkLeftAnimation.draw(unitRectangle.getX()-screenOffsetX, unitRectangle.getY()-screenOffsetY, unitRectangle.getWidth(), unitRectangle.getHeight());
		}
		else if (state == UnitState.WALK && direction == Direction.RIGHT)
		{
			walkRightAnimation.draw(unitRectangle.getX()-screenOffsetX, unitRectangle.getY()-screenOffsetY, unitRectangle.getWidth(), unitRectangle.getHeight());
		}
		else if (state == UnitState.STAND && direction == Direction.LEFT) 
		{
			standLeftAnimation.draw(unitRectangle.getX()-screenOffsetX, unitRectangle.getY()-screenOffsetY, unitRectangle.getWidth(), unitRectangle.getHeight());
		}
		else if (state == UnitState.STAND && direction == Direction.RIGHT) 
		{
			standRightAnimation.draw(unitRectangle.getX()-screenOffsetX, unitRectangle.getY()-screenOffsetY, unitRectangle.getWidth(), unitRectangle.getHeight());
		}
		else if (state == UnitState.SHOOT && direction == Direction.LEFT) 
		{
			shootLeftAnimation.draw(unitRectangle.getX()-screenOffsetX, unitRectangle.getY()-screenOffsetY, unitRectangle.getWidth(), unitRectangle.getHeight());
		}
		else if (state == UnitState.SHOOT && direction == Direction.RIGHT) 
		{
			shootRightAnimation.draw(unitRectangle.getX()-screenOffsetX, unitRectangle.getY()-screenOffsetY, unitRectangle.getWidth(), unitRectangle.getHeight());
		}
		
	}
	
	/*
	
	public int updateControl(GameContainer container, int delta, Ground ground) throws SlickException {
		int energyUsage = 0;
		
		UnitState state = unit.getUnitState();
		Rectangle unitRectangle = unit.getUnitRectangle();
		float x = unitRectangle.getX();
		float y = unitRectangle.getY();
		
		if (container.getInput().isKeyDown(Input.KEY_UP)) {
			if (state != UnitState.JUMP && state != UnitState.FALL) {
				unit.setUnitState(UnitState.JUMP);
				unit.startJump();
			}
		}
		if (container.getInput().isKeyDown(Input.KEY_LEFT)) {
			x -= delta*0.1;
			deltaMovement += delta*0.1;
			if (x < 0) {
				x = 0;
			}
			if (state == UnitState.STAND) {
				unit.setUnitState(UnitState.WALK);
			}
			unit.setUnitDirection(Direction.LEFT);
		}
		
		if (container.getInput().isKeyDown(Input.KEY_RIGHT)) {
			x += delta*0.1;
			deltaMovement += delta*0.1;
			if (x > container.getWidth() - unit.getUnitRectangle().getWidth()) {
				x = container.getWidth() - unit.getUnitRectangle().getWidth();
			}
			if (state == UnitState.STAND) {
				unit.setUnitState(UnitState.WALK);
			}
			unit.setUnitDirection(Direction.RIGHT);
		}
		if (!(container.getInput().isKeyDown(Input.KEY_UP) || container.getInput().isKeyDown(Input.KEY_LEFT) || container.getInput().isKeyDown(Input.KEY_RIGHT))) {
			if (state != UnitState.JUMP && state != UnitState.FALL && state != UnitState.SHOOT ) {
				unit.setUnitState(UnitState.STAND);
			}
		}
		x = body.getPosition().x;
		y = body.getPosition().y;
		Vec2 temp = new Vec2(x, y);
		temp = temp.sub(offset);
		unit.setUnitPosition(temp.x, temp.y);
		
		return energyUsage;
	}
	*/
	
	public void updateAnimation(GameContainer container, int delta, int width) throws SlickException {
		UnitState state = unit.getUnitState();
		Rectangle unitRectangle = unit.getUnitRectangle();
		float x = unitRectangle.getX();
		float y = unitRectangle.getY();
		
		if (state == UnitState.JUMP) {
			if(!mid_jump){
				mid_jump = true;
				//body.applyForce(new Vec2(0, body.m_mass*-4000), body.getPosition());
				//body.applyLinearImpulse(new Vec2(0, body.m_mass*-60), body.getPosition());
				body.setLinearVelocity(new Vec2 (body.getLinearVelocity().x, -100));
			}
			
			// y-=delta*0.3;
			if (body.getLinearVelocity().y > 0) {
				unit.setUnitState(UnitState.FALL);
			}
		}
		if (state == UnitState.FALL) {
			if (body.getLinearVelocity().y > -1 && body.getLinearVelocity().y < 1) {
				unit.setUnitState(UnitState.STAND);
				mid_jump = false;
			}	
		}
		Vec2 pos = body.getPosition();
		pos = pos.sub(offset);
		
		if((pos.x + unit.width*scale) >= width){
			pos.x = width - unit.width*scale - 1;
			if(body.m_linearVelocity.x > 0){
				body.setLinearVelocity(new Vec2(0, body.m_linearVelocity.y));
			}
		}else if(pos.x < 0){
			pos.x = 0;
			if(body.m_linearVelocity.x < 0){
				body.setLinearVelocity(new Vec2(0, body.m_linearVelocity.y));
			}
		}
		if((pos.y + unit.height*scale/2f) < 0){
			pos.y = 1 - unit.height*scale/2f;
			if(body.m_linearVelocity.y <= 0){
				body.setLinearVelocity(new Vec2(body.m_linearVelocity.x, 0));
			}
		}
		
		unit.setUnitPosition(pos.x, pos.y);
	}
	
	public void setBody(Body b){
		body = b;
	}
	
	public Body body(){
		return body;
	}
	
	public Vec2 getOffset(){
		return offset;
	}
}
