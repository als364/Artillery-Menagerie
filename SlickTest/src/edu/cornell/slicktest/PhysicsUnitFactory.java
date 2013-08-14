package edu.cornell.slicktest;

import java.util.ArrayList;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SpriteSheet;

public class PhysicsUnitFactory {
	PolygonShape DEFAULT_POLYGON;
	int DEFAULT_HEIGHT;
	
	PolygonShape SPACEMARINE_POLYGON;
	int SM_HEIGHT;
	

	public static final int TWO_LEG_HW = 50;
	public static final int TWO_LEG_HH = 75;
	public static final int FOUR_LEG_HW = 88;
	public static final int FOUR_LEG_HH = 75;
	
	// Physics factory inits
	ArrayList<PolygonShape> shapes;
	ArrayList<Boolean> four_legs;
	
	
	public PhysicsUnitFactory(){	
		
		PolygonShape def = new PolygonShape();
		def.setAsBox(TWO_LEG_HW, TWO_LEG_HH);
		PolygonShape spacemarine = new PolygonShape();
		spacemarine.setAsBox(TWO_LEG_HW, TWO_LEG_HH);
		PolygonShape fairy = new PolygonShape();
		fairy.setAsBox(TWO_LEG_HW, TWO_LEG_HH);
		PolygonShape centaur = new PolygonShape();
		centaur.setAsBox(FOUR_LEG_HW,  FOUR_LEG_HH);
		PolygonShape unicorn = new PolygonShape();
		unicorn.setAsBox(FOUR_LEG_HW, FOUR_LEG_HH);
		shapes = new ArrayList<PolygonShape>();
		four_legs = new ArrayList<Boolean>();
		shapes.add(def);
		four_legs.add(false);
		shapes.add(spacemarine);
		four_legs.add(false);
		shapes.add(fairy);
		four_legs.add(false);
		shapes.add(centaur);
		four_legs.add(true);
		shapes.add(unicorn);
		four_legs.add(true);
	}
	
	public Body makeUnit(World world,  Enums.Units unit, GameContainer container, Physics physics, int scale){
		BodyDef def = new BodyDef();
		def.type = BodyType.DYNAMIC;

		float initY = 0;
		Vec2 position = new Vec2();
		float initX = (float)(((float)container.getWidth()-100)*Math.random())+50;
		while (initX +200 >= container.getWidth()){
			initX--;
		}
		float density = 0;
		PolygonShape sh = null;
		Vec2 offset = new Vec2();
		switch (unit){
			case DEFAULT:
				density = 1;
				sh = shapes.get(0);
				initY = physics.getInitY(initX, 2*(four_legs.get(0) ? FOUR_LEG_HH : TWO_LEG_HH));
				offset = new Vec2((four_legs.get(0) ? FOUR_LEG_HW : TWO_LEG_HW), (four_legs.get(0) ? FOUR_LEG_HH : TWO_LEG_HH));
				break;
			case SPACEMARINE:
				density = 1;
				sh = new PolygonShape();
				sh.setAsBox(TWO_LEG_HW * scale, TWO_LEG_HH * scale);
				initY = physics.getInitY(initX, 2*(four_legs.get(1) ? FOUR_LEG_HH : TWO_LEG_HH));	
				offset = new Vec2((four_legs.get(0) ? FOUR_LEG_HW : TWO_LEG_HW), (four_legs.get(0) ? FOUR_LEG_HH : TWO_LEG_HH));
				break;
			case FAIRY:
				density = 1;
				sh = new PolygonShape();
				sh.setAsBox(TWO_LEG_HW * scale, TWO_LEG_HH * scale);
				initY = physics.getInitY(initX, 2*(four_legs.get(2) ? FOUR_LEG_HH : TWO_LEG_HH));	
				offset = new Vec2((four_legs.get(0) ? FOUR_LEG_HW : TWO_LEG_HW), (four_legs.get(0) ? FOUR_LEG_HH : TWO_LEG_HH));
				break;
			case CENTAUR:
				density = 1;
				sh = new PolygonShape();
				sh.setAsBox(FOUR_LEG_HW * scale, FOUR_LEG_HH * scale);
				initY = physics.getInitY(initX, 2*(four_legs.get(3) ? FOUR_LEG_HH : TWO_LEG_HH));	
				offset = new Vec2((four_legs.get(0) ? FOUR_LEG_HW : TWO_LEG_HW), (four_legs.get(0) ? FOUR_LEG_HH : TWO_LEG_HH));
				break;
			case UNICORN:
				density = 1;
				sh = new PolygonShape();
				sh.setAsBox(FOUR_LEG_HW * scale, FOUR_LEG_HH * scale);	
				initY = physics.getInitY(initX, 2*(four_legs.get(4) ? FOUR_LEG_HH : TWO_LEG_HH));
				offset = new Vec2((four_legs.get(0) ? FOUR_LEG_HW : TWO_LEG_HW), (four_legs.get(0) ? FOUR_LEG_HH : TWO_LEG_HH));
				break;
		}
		
		Filter unitFilter = new Filter();
		unitFilter.categoryBits = 0x0004;
		unitFilter.maskBits = 0x0002;
		
		def.position = new Vec2(initX, initY);
		Body toReturn = world.createBody(def);
		toReturn.createFixture(sh, density);
		toReturn.setActive(true);
		toReturn.setAwake(true);
		toReturn.setType(BodyType.DYNAMIC);
		toReturn.setBullet(true);
		toReturn.m_fixtureList.setFilterData(unitFilter);
		//toReturn.m_fixtureList.m_friction = .2f;
		//toReturn.m_angularDamping = .05f;
		//toReturn.m_linearDamping = 2f;
		//toReturn.m_fixtureList.setSensor(true);
		
		return toReturn;
	}
}
