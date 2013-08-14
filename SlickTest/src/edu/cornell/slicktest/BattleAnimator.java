package edu.cornell.slicktest;

import java.awt.Point;
import java.util.ArrayList;

import org.jbox2d.common.Vec2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class BattleAnimator {
	private JSONArray animatingFrameList;
	private int animatingFrameCounter;
	private Point animatedProjectilePoint;
	private Animation animatedProjectileAnimation;
	
	public BattleAnimator() {
		animatingFrameCounter = 0;
	}
	
	public void setAnimatingFrameList(JSONArray frameList) {
		animatingFrameList = frameList;
	}
	
	public Point getAnimatedProjectilePoint() {
		return animatedProjectilePoint;
	}
	
	public Animation getAnimatedProjectileAnimation() {
		return animatedProjectileAnimation;
	}
	
	public boolean animateNextFrame(ArrayList<Army> armies, 
			ArrayList<ArrayList<UnitAnimation>> unitAnimations, Physics physics, MSTerrain _terrain) throws JSONException, SlickException {
		
		if (animatingFrameCounter >= animatingFrameList.length()) {
			animatingFrameCounter = 0;
			return false;
		}
		JSONObject frame = animatingFrameList.getJSONObject(animatingFrameCounter);
		
		// Animating units
		
		JSONArray unitFrame = frame.getJSONArray("units");
		for (int i = 0; i < armies.size(); i++) {
			Army army = armies.get(i);
			for (int j = 0; j < army.units.size(); j++) {
				Unit unit = army.units.get(j);
				boolean stillExist = false;
				for (int k = 0; k < unitFrame.length(); k++) {
					JSONObject unitObject = unitFrame.getJSONObject(k);
					String unitID = unitObject.getString("unitID");
					if (unitID.equals(unit.displayName)) {
						unit.loadStateFromJSON(unitObject);
						stillExist = true;
						break;
					}
				}
				if (!stillExist) {
					army.units.remove(j);
					army.done.remove(j);
					unitAnimations.get(i).remove(j);
					j--;
				}
			}
		}
		
		// Animating shot
		
		if (frame.has("shot")) {
			JSONObject shotFrame = frame.getJSONObject("shot");
			String unitID = shotFrame.getString("unitID");
			int skillNumber = shotFrame.getInt("skillNumber");
			
			searchUnit: {
				for (Army army : armies) {
					for (Unit unit : army.units) {
						if (unitID.equals(unit.displayName)) {
							Ability ability = unit.abilities.get(skillNumber);
							animatedProjectileAnimation = new Animation(ability.getSpriteSheet(), 150);
							break searchUnit;
						}
					}
				}
			}
		}
		
		// Animating projectile
		
		if (frame.has("projectile")) {
			JSONObject projectileFrame = frame.getJSONObject("projectile");
			int x = projectileFrame.getInt("xpos");
			int y = projectileFrame.getInt("ypos");
			animatedProjectilePoint = new Point(x, y);
		}
		else {
			animatedProjectilePoint = null;
		}
		
		// Animating explosion
		
		if (frame.has("explosion")) {
			JSONObject explosionFrame = frame.getJSONObject("explosion");
			int x = explosionFrame.getInt("xpos");
			int y = explosionFrame.getInt("ypos");
			int radius = explosionFrame.getInt("radius");
			physics.updateCollisionBoard(x, y, radius);
			physics.updateCollisionTerrain(new Vec2(x, y), _terrain);
		}
		
		if (animatingFrameCounter == animatingFrameList.length()-1) {
			for (Army army : armies) {
				for (Unit unit : army.units) {
					unit.resetUnitState();
				}
			}
		}
		
		//System.out.println("Animating frames");
		
		animatingFrameCounter++;
		
		return true;
	}
}
