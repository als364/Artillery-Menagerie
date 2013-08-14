package edu.cornell.slicktest;

import java.awt.Font;
import java.awt.Point;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
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
	final float nameOffsetY = 30;
	
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
	protected Vec2 offset;
	
	protected boolean mid_jump = false;
	
	public UnitAnimation(Unit unit, GameContainer container)  throws SlickException {
		this.unit = unit;
		float initX;
		switch(unit.unitType)
		{
		case DEFAULT:
			sheet = new SpriteSheet(unit.getCurrentAttributes().spritesheetPath, 36, 65);
			walkLeftAnimation = new Animation(sheet, 150);
			walkRightAnimation = new Animation();
			standLeftAnimation = new Animation();
			standLeftAnimation.addFrame(sheet.getSprite(7,0), 150);
			jumpLeftAnimation = new Animation();
			jumpLeftAnimation.addFrame(sheet.getSprite(7,0), 150);
			shootLeftAnimation = new Animation();
			shootLeftAnimation.addFrame(sheet.getSprite(0,0),150);
		case SPACEMARINE:
			sheet = new SpriteSheet(unit.getCurrentAttributes().spritesheetPath, 100, 150);
			walkLeftAnimation = new Animation();
			walkRightAnimation = new Animation();
			standLeftAnimation = new Animation();
			standRightAnimation = new Animation();
			for(int i = 0; i < 8; i++)
			{
				walkLeftAnimation.addFrame(sheet.getSprite(i, 0), 50);
				walkRightAnimation.addFrame(sheet.getSprite(i, 1), 50);
				standLeftAnimation.addFrame(sheet.getSprite(i, 2), 50);
				standRightAnimation.addFrame(sheet.getSprite(i, 3), 50);
			}
			jumpLeftAnimation = new Animation();
			jumpLeftAnimation.addFrame(sheet.getSprite(0, 2), 50);
			jumpRightAnimation = new Animation();
			jumpRightAnimation.addFrame(sheet.getSprite(0, 2), 50);
			shootLeftAnimation = new Animation();
			shootLeftAnimation.addFrame(sheet.getSprite(0, 2), 50);
			shootRightAnimation = new Animation();
			shootRightAnimation.addFrame(sheet.getSprite(0, 2), 50);
		case CENTAUR:
			sheet = new SpriteSheet(unit.getCurrentAttributes().spritesheetPath, 175, 150);
			walkLeftAnimation = new Animation();
			walkRightAnimation = new Animation();
			standLeftAnimation = new Animation();
			standRightAnimation = new Animation();
			for(int i = 0; i < 8; i++)
			{
				walkLeftAnimation.addFrame(sheet.getSprite(i, 0), 50);
				walkRightAnimation.addFrame(sheet.getSprite(i, 1), 50);
				standLeftAnimation.addFrame(sheet.getSprite(i, 2), 50);
				standRightAnimation.addFrame(sheet.getSprite(i, 3), 50);
			}
			jumpLeftAnimation = new Animation();
			jumpLeftAnimation.addFrame(sheet.getSprite(0, 2), 50);
			jumpRightAnimation = new Animation();
			jumpRightAnimation.addFrame(sheet.getSprite(0, 2), 50);
			shootLeftAnimation = new Animation();
			shootLeftAnimation.addFrame(sheet.getSprite(0, 2), 50);
			shootRightAnimation = new Animation();
			shootRightAnimation.addFrame(sheet.getSprite(0, 2), 50);
		case UNICORN:
			sheet = new SpriteSheet(unit.getCurrentAttributes().spritesheetPath, 175, 150);
			walkLeftAnimation = new Animation();
			walkRightAnimation = new Animation();
			standLeftAnimation = new Animation();
			standRightAnimation = new Animation();
			for(int i = 0; i < 8; i++)
			{
				walkLeftAnimation.addFrame(sheet.getSprite(i, 0), 50);
				walkRightAnimation.addFrame(sheet.getSprite(i, 1), 50);
				standLeftAnimation.addFrame(sheet.getSprite(i, 2), 50);
				standRightAnimation.addFrame(sheet.getSprite(i, 3), 50);
			}
			jumpLeftAnimation = new Animation();
			jumpLeftAnimation.addFrame(sheet.getSprite(0, 2), 50);
			jumpRightAnimation = new Animation();
			jumpRightAnimation.addFrame(sheet.getSprite(0, 2), 50);
			shootLeftAnimation = new Animation();
			shootLeftAnimation.addFrame(sheet.getSprite(0, 2), 50);
			shootRightAnimation = new Animation();
			shootRightAnimation.addFrame(sheet.getSprite(0, 2), 50);
		case FAIRY:
			sheet = new SpriteSheet(unit.getCurrentAttributes().spritesheetPath, 100, 150);
			walkLeftAnimation = new Animation();
			walkRightAnimation = new Animation();
			standLeftAnimation = new Animation();
			standRightAnimation = new Animation();
			for(int i = 0; i < 8; i++)
			{
				walkLeftAnimation.addFrame(sheet.getSprite(i, 0), 50);
				walkRightAnimation.addFrame(sheet.getSprite(i, 1), 50);
				standLeftAnimation.addFrame(sheet.getSprite(i, 2), 50);
				standRightAnimation.addFrame(sheet.getSprite(i, 3), 50);
			}
			jumpLeftAnimation = new Animation();
			jumpLeftAnimation.addFrame(sheet.getSprite(0, 2), 50);
			jumpRightAnimation = new Animation();
			jumpRightAnimation.addFrame(sheet.getSprite(0, 2), 50);
			shootLeftAnimation = new Animation();
			shootLeftAnimation.addFrame(sheet.getSprite(0, 2), 50);
			shootRightAnimation = new Animation();
			shootRightAnimation.addFrame(sheet.getSprite(0, 2), 50);
		}
		offset = new Vec2(sheet.getSprite(0, 0).getWidth(), sheet.getSprite(0, 0).getHeight()).mul(.5f);
	}
	
	public void render(GameContainer container, Graphics graphics) throws SlickException {
		
		// Health bar
		graphics.setColor(Color.red);
		float healthBar = healthBarWidth*(float)unit.getCurrentHealth()/(float)unit.getMaxHealth();
		
		Rectangle unitRectangle = unit.getUnitRectangle();
		
		graphics.drawRect(unitRectangle.getX(), unitRectangle.getY()-healthBarOffsetY, healthBarWidth, healthBarHeight);
		graphics.fillRect(unitRectangle.getX(), unitRectangle.getY()-healthBarOffsetY, healthBar, healthBarHeight);
		graphics.setColor(Color.white);
		graphics.drawString(unit.displayName, unitRectangle.getX(), unitRectangle.getY()-nameOffsetY);
		
		UnitState state = unit.getUnitState();
		Direction direction = unit.getUnitDirection();
		if ((state == UnitState.JUMP || state == UnitState.FALL) && direction == Direction.LEFT) 
		{
			jumpLeftAnimation.draw(unitRectangle.getX(), unitRectangle.getY());
		}
		else if((state == UnitState.JUMP || state == UnitState.FALL) && direction == Direction.RIGHT)
		{
			jumpRightAnimation.draw(unitRectangle.getX(), unitRectangle.getY());
		}
		else if (state == UnitState.WALK && direction == Direction.LEFT)
		{
			walkLeftAnimation.draw(unitRectangle.getX(), unitRectangle.getY());
		}
		else if (state == UnitState.WALK && direction == Direction.RIGHT)
		{
			walkRightAnimation.draw(unitRectangle.getX(), unitRectangle.getY());
		}
		else if (state == UnitState.STAND && direction == Direction.LEFT) 
		{
			standLeftAnimation.draw(unitRectangle.getX(), unitRectangle.getY());
		}
		else if (state == UnitState.STAND && direction == Direction.RIGHT) 
		{
			standRightAnimation.draw(unitRectangle.getX(), unitRectangle.getY());
		}
		else if (state == UnitState.SHOOT && direction == Direction.LEFT) 
		{
			shootLeftAnimation.draw(unitRectangle.getX(), unitRectangle.getY());
		}
		else if (state == UnitState.SHOOT && direction == Direction.RIGHT) 
		{
			shootRightAnimation.draw(unitRectangle.getX(), unitRectangle.getY());
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
	
	public void updateAnimation(GameContainer container, int delta, Ground ground) throws SlickException {
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
		unit.setUnitPosition(pos.x, pos.y);
	}
	
	public void setBody(Body b){
		body = b;
	}
	
	public Body body(){
		return body;
	}
}
