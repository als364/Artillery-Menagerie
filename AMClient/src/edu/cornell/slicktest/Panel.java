package edu.cornell.slicktest;

import org.newdawn.slick.Image;

public class Panel {
	
	private Image image;
	private int xpos;
	private int ypos;
	
	public Panel(Image image, int xpos, int ypos) {
		this.image = image;
		this.image.setColor(Image.TOP_LEFT, 1f, 0.9f, 0.9f, 1f);
		this.xpos = xpos;
		this.ypos = ypos;
	}
	
	public void render() {
		image.draw(xpos, ypos);
	}
	
	public int getMinX() {
		return xpos;
	}
	
	public int getMinY() {
		return ypos;
	}
	
	public int getCenterX() {
		return xpos + image.getWidth()/2;
	}
	
	public int getCenterY() {
		return ypos + image.getHeight()/2;
	}
	
	public int getMaxX() {
		return xpos + image.getWidth();
	}
	
	public int getMaxY() {
		return ypos + image.getHeight();
	}
}
