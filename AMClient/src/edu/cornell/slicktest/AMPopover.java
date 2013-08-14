package edu.cornell.slicktest;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class AMPopover {
	private String desc;
	private int centerX;
	private int centerY;
	private int space;
	private int width;
	private int height;
	private int xpos;
	private int ypos;
	private Color color;
	
	public AMPopover(GameContainer container, String desc, int centerX, int centerY) throws SlickException {
		this.desc= desc;
		this.centerX = centerX;
		this.centerY = centerY;
		
		space = 10;
		width = AMFonts.getArialBold16().getWidth(desc) + space;
		height = AMFonts.getArialBold16().getHeight(desc) + space;
		if (centerY > container.getHeight()/2) {
			ypos = centerY - height;
		}
		else {
			ypos = centerY;
		}
		xpos = centerX - width/2;
		if (xpos < 0) {
			xpos = 0;
		}
		else  if (xpos + width > container.getWidth()) {
			xpos -= xpos + width - container.getWidth();
		}
		color = new Color(0.9f, 0.9f, 0, 0.7f);
	}
	
	public void draw(Graphics graphics) throws SlickException {

		graphics.setColor(Color.yellow);
		graphics.drawRect(xpos, ypos, width, height);
		graphics.setColor(color);
		graphics.fillRect(xpos, ypos, width, height);
		AMFonts.getArialBold16().drawString(xpos + space/2, ypos + space/2, desc, Color.black);
	}
}
