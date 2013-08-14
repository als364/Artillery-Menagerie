package edu.cornell.slicktest;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import edu.cornell.slicktest.Ability;
import edu.cornell.slicktest.Enums.*;

public class Factory {
	public static Unit getUnit(Units unit, String displayName) throws SlickException {
		Unit output = null;
		AttributeSet attribute;
		switch (unit) {
		case DEFAULT:
			attribute = new AttributeSet(10, 10, 1, 10, 10, "images/homeranim.png", false, false);
			output = new Unit("Homer", displayName, Units.DEFAULT, attribute, 1);
			output.abilities.add(getAbility(Abilities.DEFAULT));
			break;
		case SPACEMARINE:
			attribute = new AttributeSet(10, 10, 1, 10, 10, "images/spaceMarineSheet.png", false, false);
			output = new Unit("SpaceMarine", displayName, Units.SPACEMARINE, attribute, 1);
			output.abilities.add(getAbility(Abilities.DEFAULT));
			break;
		case CENTAUR:
			attribute = new AttributeSet(10, 10, 1, 10, 10, "images/centaurSheet.png", true, false);
			output = new Unit("Centaur", displayName, Units.CENTAUR, attribute, 1);
			output.abilities.add(getAbility(Abilities.DEFAULT));
		case UNICORN:
			attribute = new AttributeSet(10, 10, 1, 10, 10, "images/unicornSheet.png", true, false);
			output = new Unit("Unicorn", displayName, Units.UNICORN, attribute, 1);
			output.abilities.add(getAbility(Abilities.DEFAULT));
		case FAIRY:
			attribute = new AttributeSet(10, 10, 1, 10, 10, "images/fairySheet.png", false, true);
			output = new Unit("Fairy", displayName, Units.FAIRY, attribute, 1);
			output.abilities.add(getAbility(Abilities.DEFAULT));
		}
		return output;
	}
	
	public static Item getItem(Items item) throws SlickException {
		Item output = null;
		switch (item) {
		case DEFAULT:
			output = null;
			break;
			
		}
		return output;
	}
	
	public static Ability getAbility(Abilities ability) throws SlickException {
		Ability output = null;
		switch (ability) {
		case DEFAULT:
			SpriteSheet sheet = new SpriteSheet("images/rocket.png", 28, 48);
			output = new Ability("Shoot Rocket", AbilityType.PROJECTILE_DAMAGE, sheet, 2, 1000, 5);
			break;
			
		}
		return output;
	}
}
