package edu.cornell.slicktest;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.MouseOverArea;

public class BattleHelpScreen implements ComponentListener {
	private Image background;
	private int xpos;
	private int ypos;
	private MouseOverArea backButton;
	private boolean needToClose;
	
	public BattleHelpScreen(GameContainer container) throws SlickException {
		background = new Image("gui/help_screen.png");
		xpos = (container.getWidth()-background.getWidth())/2;
		ypos = (container.getHeight()-background.getHeight())/2;
		backButton = new MouseOverArea(container, new Image("gui/back_button_blue.png"), 
				xpos+background.getWidth()/2-50, ypos+background.getHeight()-70, this);
		backButton.setNormalColor(new Color(1,1,1,1.0f));
		backButton.setMouseOverColor(new Color(0.7f,0.7f,1,1.0f));
		needToClose = false;
	}
	
	public void draw(GameContainer container, Graphics graphics) {
		background.draw(xpos, ypos);
		backButton.render(container,  graphics);
		
		// Controls
		int x = xpos + 20;
		int y = ypos + 100;
		int offset = 30;
		graphics.setColor(Color.white);
		graphics.drawString("keys left, right, up or A, D, W: movement", x, y);
		
		y += offset;
		graphics.drawString("mouse click: select unit or shoot", x, y);
		
		y += offset;
		graphics.drawString("keys 1, 2, 3, 4: adjust camera", x, y);
		
		y += offset;
		graphics.drawString("key tab: change unit", x, y);
		
		y += offset;
		graphics.drawString("key enter: commit turn or animate opponent's moves", x, y);
		
		y += offset;
		graphics.drawString("key esc: exit to main menu", x, y);
		
	}

	@Override
	public void componentActivated(AbstractComponent arg0) {
		// TODO Auto-generated method stub
		needToClose = true;
	}
	
	public boolean needToClose() {
		return needToClose;
	}
}
