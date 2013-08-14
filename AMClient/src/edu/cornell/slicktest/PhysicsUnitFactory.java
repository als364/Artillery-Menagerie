package edu.cornell.slicktest;

import java.util.ArrayList;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Transform;
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
		PolygonShape robot = new PolygonShape();
		robot.setAsBox(TWO_LEG_HW, TWO_LEG_HH);
		PolygonShape ai = new PolygonShape();
		ai.setAsBox(TWO_LEG_HW, TWO_LEG_HH);
		PolygonShape fairy = new PolygonShape();
		fairy.setAsBox(TWO_LEG_HW, TWO_LEG_HH);
		PolygonShape alien = new PolygonShape();
		alien.setAsBox(TWO_LEG_HW, TWO_LEG_HH);
		PolygonShape centaur = new PolygonShape();
		centaur.setAsBox(FOUR_LEG_HW,  FOUR_LEG_HH);
		PolygonShape unicorn = new PolygonShape();
		unicorn.setAsBox(FOUR_LEG_HW, FOUR_LEG_HH);
		PolygonShape dragon = new PolygonShape();
		//dragon.setAsBox(FOUR_LEG_HW, FOUR_LEG_HH);
		dragon.set(new Vec2[] {new Vec2(25, 45), new Vec2(59, 47), new Vec2(121, 71), new Vec2(117, 134), new Vec2(30, 130)}, 5);
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
	
	public Body makeUnit(World world,  Enums.Units unit, GameContainer container, Physics physics, int worldWidth, float scale){
		BodyDef def = new BodyDef();
		def.type = BodyType.DYNAMIC;

		float initY = 0;
		Vec2 position = new Vec2();
		float initX = (float)((worldWidth-100)*Math.random())+50;
		while (initX +200 >= worldWidth){
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
			case ROBOT:
				density = 1;
				sh = new PolygonShape();
				sh.setAsBox(TWO_LEG_HW * scale, TWO_LEG_HH * scale);
				initY = physics.getInitY(initX, 2*(four_legs.get(1) ? FOUR_LEG_HH : TWO_LEG_HH));	
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
			case DRAGON:
				density = 1;
				sh = new PolygonShape();
				sh.set(new Vec2[] {new Vec2(25, 45).mul(scale), new Vec2(59, 47).mul(scale), new Vec2(121, 71).mul(scale), new Vec2(117, 134).mul(scale), new Vec2(30, 130).mul(scale)}, 5);
				initY = physics.getInitY(initX, 2*(four_legs.get(4) ? FOUR_LEG_HH : TWO_LEG_HH));
				offset = new Vec2((four_legs.get(0) ? FOUR_LEG_HW : TWO_LEG_HW), (four_legs.get(0) ? FOUR_LEG_HH : TWO_LEG_HH));
				break;
			case AI:
				density = 1;
				sh = new PolygonShape();
				sh.setAsBox(TWO_LEG_HW * scale, TWO_LEG_HH * scale);
				initY = physics.getInitY(initX, 2*(four_legs.get(1) ? FOUR_LEG_HH : TWO_LEG_HH));	
				offset = new Vec2((four_legs.get(0) ? FOUR_LEG_HW : TWO_LEG_HW), (four_legs.get(0) ? FOUR_LEG_HH : TWO_LEG_HH));
				break;
			case ALIEN:
				density = 1;
				sh = new PolygonShape();
				sh.setAsBox(TWO_LEG_HW * scale, TWO_LEG_HH * scale);
				initY = physics.getInitY(initX, 2*(four_legs.get(1) ? FOUR_LEG_HH : TWO_LEG_HH));	
				offset = new Vec2((four_legs.get(0) ? FOUR_LEG_HW : TWO_LEG_HW), (four_legs.get(0) ? FOUR_LEG_HH : TWO_LEG_HH));
				break;
		}
		offset = offset.mul(scale);
		Filter unitFilter = new Filter();
		unitFilter.categoryBits = 0x0004;
		unitFilter.maskBits = 0x0002;
		
		def.position = new Vec2(initX, initY);
		def.fixedRotation = true;
		Body toReturn = world.createBody(def);
		toReturn.setFixedRotation(true);
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
	
	// TODO: start using this constructor instead when levels are implemented
	public Body makeUnit(World world,  Enums.Units unit, GameContainer container, Physics physics, int worldWidth, float scale, Vec2 position){
		BodyDef def = new BodyDef();
		def.type = BodyType.DYNAMIC;

		float density = 0;
		PolygonShape sh = null;
		Vec2 offset = new Vec2();
		switch (unit){
			case DEFAULT:
				density = 1;
				sh = shapes.get(0);
				offset = new Vec2((four_legs.get(0) ? FOUR_LEG_HW : TWO_LEG_HW), (four_legs.get(0) ? FOUR_LEG_HH : TWO_LEG_HH));
				break;
			case SPACEMARINE:
				density = 1;
				sh = new PolygonShape();
				sh.setAsBox(TWO_LEG_HW * scale, TWO_LEG_HH * scale);
				offset = new Vec2((four_legs.get(0) ? FOUR_LEG_HW : TWO_LEG_HW), (four_legs.get(0) ? FOUR_LEG_HH : TWO_LEG_HH));
				break;
			case FAIRY:
				density = 1;
				sh = new PolygonShape();
				sh.setAsBox(TWO_LEG_HW * scale, TWO_LEG_HH * scale);
				offset = new Vec2((four_legs.get(0) ? FOUR_LEG_HW : TWO_LEG_HW), (four_legs.get(0) ? FOUR_LEG_HH : TWO_LEG_HH));
				break;
			case CENTAUR:
				density = 1;
				sh = new PolygonShape();
				sh.setAsBox(FOUR_LEG_HW * scale, FOUR_LEG_HH * scale);
				offset = new Vec2((four_legs.get(0) ? FOUR_LEG_HW : TWO_LEG_HW), (four_legs.get(0) ? FOUR_LEG_HH : TWO_LEG_HH));
				break;
			case UNICORN:
				density = 1;
				sh = new PolygonShape();
				sh.setAsBox(FOUR_LEG_HW * scale, FOUR_LEG_HH * scale);
				offset = new Vec2((four_legs.get(0) ? FOUR_LEG_HW : TWO_LEG_HW), (four_legs.get(0) ? FOUR_LEG_HH : TWO_LEG_HH));
				break;
			case DRAGON:
				density = 1;
				sh = new PolygonShape();
				sh.setAsBox(FOUR_LEG_HW * scale, FOUR_LEG_HH * scale);
				//sh.set(new Vec2[] {new Vec2(25, 45).mul(scale), new Vec2(59, 47).mul(scale), new Vec2(121, 71).mul(scale), new Vec2(117, 134).mul(scale), new Vec2(30, 130).mul(scale)}, 5);
				/*
				float min_x = 200 , min_y = 200;
				float max_x = -200, max_y = -200;
				for(int i = 0; i < sh.m_vertexCount; i++){
					if(sh.m_vertices[i].x > max_x){
						max_x = sh.m_vertices[i].x;
					}
					if(sh.m_vertices[i].x < min_x){
						min_x = sh.m_vertices[i].x;
					}
					if(sh.m_vertices[i].y > max_y){
						max_y = sh.m_vertices[i].y;
					}
					if(sh.m_vertices[i].y < min_y){
						min_y = sh.m_vertices[i].y;
					}
				}				
				offset = new Vec2((max_x - min_x)/2/scale, (max_y - min_y)/2/scale);
				*/
				offset = new Vec2((four_legs.get(0) ? FOUR_LEG_HW : TWO_LEG_HW), (four_legs.get(0) ? FOUR_LEG_HH : TWO_LEG_HH));
				break;
			case ROBOT:
				density = 1;
				sh = new PolygonShape();
				sh.setAsBox(TWO_LEG_HW * scale, TWO_LEG_HH * scale);
				offset = new Vec2((four_legs.get(0) ? FOUR_LEG_HW : TWO_LEG_HW), (four_legs.get(0) ? FOUR_LEG_HH : TWO_LEG_HH));
				break;
			case AI:
				density = 1;
				sh = new PolygonShape();
				sh.setAsBox(TWO_LEG_HW * scale, TWO_LEG_HH * scale);
				offset = new Vec2((four_legs.get(0) ? FOUR_LEG_HW : TWO_LEG_HW), (four_legs.get(0) ? FOUR_LEG_HH : TWO_LEG_HH));
				break;
			case ALIEN:
				density = 1;
				sh = new PolygonShape();
				sh.setAsBox(TWO_LEG_HW * scale, TWO_LEG_HH * scale);
				offset = new Vec2((four_legs.get(0) ? FOUR_LEG_HW : TWO_LEG_HW), (four_legs.get(0) ? FOUR_LEG_HH : TWO_LEG_HH));
				break;
		}
		offset = offset.mul(scale);
		Filter unitFilter = new Filter();
		unitFilter.categoryBits = 0x0004;
		unitFilter.maskBits = 0x0002;
		
		def.position = position;
		def.fixedRotation = true;
		Body toReturn = world.createBody(def);
		toReturn.setFixedRotation(true);
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
