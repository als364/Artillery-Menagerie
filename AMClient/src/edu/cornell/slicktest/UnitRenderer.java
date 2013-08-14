package edu.cornell.slicktest;

import java.awt.Point;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class UnitRenderer {
	
	public static void renderUnit(GameContainer container, Graphics graphics,
			Unit unit, Point centerPoint) throws SlickException {
		Point frameSize = SpriteSheetInfo.getUnitSpriteSheetFrameSize(unit.factoryKey);
		SpriteSheet unitSheet = new SpriteSheet(unit.equippedAttributes.spritesheetPath, frameSize.x, frameSize.y);
		Image unitImage = unitSheet.getSprite(0, 0);
		
		int nameWidth = AMFonts.getArialBold16().getWidth(unit.displayName);
		int nameHeight = AMFonts.getArialBold16().getHeight(unit.displayName);
		int space = 0;
		int totalHeight = nameHeight + space + frameSize.y;
		AMFonts.getArialBold16().drawString(centerPoint.x-nameWidth/2, centerPoint.y-totalHeight/2, unit.displayName, Color.black);
		
		int unitFrameX = centerPoint.x-frameSize.x/2;
		int unitFrameY = centerPoint.y-totalHeight/2+nameHeight+space;
		for (Item item : unit.equipments) {
			Image itemImage = item.spriteSheet.getSprite(0, 0);
			itemImage.draw(unitFrameX+item.equipPoint.x, unitFrameY+item.equipPoint.y);
		}
		unitImage.draw(unitFrameX, unitFrameY);
	}
}
