package edu.cornell.slicktest;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.MouseOverArea;

public class AMPopUp implements ComponentListener {
	private MouseOverArea okButton;
	private String message;
	private int xpos;
	private int ypos;
	private int width;
	private int height;
	private Image background;
	
	private boolean okButtonClicked;
	
	public AMPopUp(GameContainer container, String message) throws SlickException {
		background = new Image("gui/popup.png");
		this.message = message;
		
		this.width = background.getWidth();
		this.height = background.getHeight();
		this.xpos = (container.getWidth()-width)/2;
		this.ypos = (container.getHeight()-height)/2;
		
		okButton = new MouseOverArea(container, new Image("gui/accept_button.png"), 
				xpos+width/2-50, ypos+height-70, this);
		okButton.setNormalColor(new Color(1,1,1,1.0f));
		okButton.setMouseOverColor(new Color(0.7f,0.7f,1,1.0f));
		okButtonClicked = false;
	}
	
	public void draw(GameContainer container, Graphics graphics) throws SlickException {
		background.draw(xpos, ypos);
		
		//AMFonts.getArialBold16().drawString(xpos +5, ypos + +5, message, Color.white);
		graphics.setColor(Color.white);
		graphics.drawString(message, xpos+20, ypos+20);
		
		okButton.render(container, graphics);
	}
	
	public void componentActivated(AbstractComponent component) {
		okButtonClicked = true;
	}
	
	public boolean needToClose() {
		return okButtonClicked;
	}
}
