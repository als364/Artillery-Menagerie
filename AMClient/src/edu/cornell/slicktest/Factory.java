package edu.cornell.slicktest;

import java.awt.Point;
import java.util.ArrayList;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import edu.cornell.slicktest.Ability;
import edu.cornell.slicktest.Enums.*;

public class Factory {
	static float scale = BattleScreen.scale;
	
	public static Unit getUnit(Units unit, String displayName, String unitID) throws SlickException {
		Unit output = null;
		AttributeSet attribute;
		Point launchPoint;
		switch (unit) {
		case DEFAULT:
			attribute = new AttributeSet(20, 10, 1, 10, 2, "images/spaceMarineSheet.png", "images/enemySpaceMarineSheet.png", false, false);
			launchPoint = new Point(0, 50);
			output = new Unit("SpaceMarine", displayName, unitID, Units.SPACEMARINE, attribute, launchPoint, 1, 100, 150);
			output.abilities.add(getAbility(Abilities.BOMB));
			break;
		case SPACEMARINE:
			attribute = new AttributeSet(20, 10, 1, 10, 2, "images/spaceMarineSheet.png", "images/enemySpaceMarineSheet.png", false, false);
			launchPoint = new Point(0, 50);
			output = new Unit("SpaceMarine", displayName, unitID, Units.SPACEMARINE, attribute, launchPoint, 1, 100, 150);
			output.abilities.add(getAbility(Abilities.BOMB));
			break;
		case CENTAUR:
			attribute = new AttributeSet(20, 10, 1, 10, 1, "images/centaurSheet.png", "images/enemyCentaurSheet.png", true, false);
			launchPoint = new Point(0, 50);
			output = new Unit("Centaur", displayName, unitID, Units.CENTAUR, attribute, launchPoint, 1, 175, 150);
			output.abilities.add(getAbility(Abilities.BOMB));
			break;
		case UNICORN:
			attribute = new AttributeSet(15, 10, 1, 10, 1, "images/unicornSheet.png", "images/enemyUnicornSheet.png", true, false);
			launchPoint = new Point(0, 50);
			output = new Unit("Unicorn", displayName, unitID, Units.UNICORN, attribute, launchPoint, 1, 175, 150);
			output.abilities.add(getAbility(Abilities.BOMB));
			break;
		case DRAGON:
			attribute = new AttributeSet(15, 10, 1, 10, 1, "images/dragonSheet.png", "images/enemyDragonSheet.png", true, false);
			launchPoint = new Point(0, 50);
			output = new Unit("Dragon", displayName, unitID, Units.DRAGON, attribute, launchPoint, 1, 175, 150);
			output.abilities.add(getAbility(Abilities.BOMB));
			break;
		case FAIRY:
			attribute = new AttributeSet(15, 10, 1, 10, 1, "images/fairySheet.png", "images/enemyFairySheet.png", false, true);
			launchPoint = new Point(0, 50);
			output = new Unit("Fairy", displayName, unitID, Units.FAIRY, attribute, launchPoint, 1, 100, 150);
			output.abilities.add(getAbility(Abilities.BOMB));
			break;
		case ROBOT:
			attribute = new AttributeSet(15, 10, 1, 10, 1, "images/robotSheet.png", "images/enemyRobotSheet.png", false, true);
			launchPoint = new Point(0, 50);
			output = new Unit("Robot", displayName, unitID, Units.ROBOT, attribute, launchPoint, 1, 100, 150);
			output.abilities.add(getAbility(Abilities.BOMB));
			break;
		case AI:
			attribute = new AttributeSet(15, 10, 1, 10, 1, "images/AISheet.png", "images/enemyAISheet.png", false, true);
			launchPoint = new Point(0, 50);
			output = new Unit("AI", displayName, unitID, Units.AI, attribute, launchPoint, 1, 100, 150);
			output.abilities.add(getAbility(Abilities.BOMB));
			break;
		case ALIEN:
			attribute = new AttributeSet(15, 10, 1, 10, 1, "images/alienSheet.png", "images/enemyAlienSheet.png", false, true);
			launchPoint = new Point(0, 50);
			output = new Unit("Alien", displayName, unitID, Units.ALIEN, attribute, launchPoint, 1, 100, 150);
			output.abilities.add(getAbility(Abilities.BOMB));
			break;
		}
		return output;
	}
	
	public static Item getItem(Items item, String itemID) throws SlickException {
		Item output = null;
		Image image;
		SpriteSheet sheet;
		AttributeSet boosts;
		Point equipPoint;
		switch (item) {
		case DEFAULT:
			output = null;
			break;
		case ROCKET_LAUNCHER:
			image = new Image("images/testRocket.png");
			image = image.getScaledCopy(100, 100);
			sheet = new SpriteSheet(image, image.getWidth(), image.getHeight());
			boosts = new AttributeSet(0, 0, 5, 0, 5, null, null, false, false);
			equipPoint = new Point(0, (int)(50*scale));
			output = new Item("Rocket Launcher", item, itemID, 100, boosts, null, 1, sheet, equipPoint);
			break;
		}
		return output;
	}
	
	public static Ability getAbility(Abilities ability) throws SlickException {
		Ability output = null;
		Image image;
		SpriteSheet sheet;
		switch (ability) {
		case DEFAULT:
			sheet = new SpriteSheet("images/rocket.png", 50, 50);
			output = new Ability("Shoot Rocket", ability, AbilityType.PROJECTILE_DAMAGE, sheet, 25, 1000, 5);
			break;
		case BOMB:
			image = new Image("images/bomb.png").getScaledCopy(0.5f);
			sheet = new SpriteSheet(image, image.getWidth(), image.getHeight());
			output = new Ability("Throw Bomb", ability, AbilityType.PROJECTILE_DAMAGE, sheet, 25, 1000, 5);
			break;
		}
		return output;
	}
}
