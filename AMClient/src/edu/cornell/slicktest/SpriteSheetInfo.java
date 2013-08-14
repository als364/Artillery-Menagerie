package edu.cornell.slicktest;

import java.awt.Point;

import edu.cornell.slicktest.Enums.Units;

public class SpriteSheetInfo {
	public static Point getUnitSpriteSheetFrameSize(Units unit) {
		switch(unit) {
		case DEFAULT:
			return new Point(36, 65);
		case SPACEMARINE:
			return new Point(100, 150);
		case CENTAUR:
			return  new Point(175, 150);
		case UNICORN:
			return  new Point(175, 150);
		case FAIRY:
			return new Point(100, 150);
		}
		return null;
	}
}
