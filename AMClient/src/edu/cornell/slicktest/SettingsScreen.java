package edu.cornell.slicktest;

import java.awt.Point;
import java.io.IOException;

import org.json.JSONException;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.MouseOverArea;

public class SettingsScreen implements Screen
{
	Image background;
	MouseOverArea backButton;
	
	public SettingsScreen(GameContainer container) throws SlickException
	{
		background = new Image("gui/background.png");
		background.setColor(Image.BOTTOM_RIGHT, 1f, 0.6f, 0.6f, 1f);
		int offsetX = 20;
		backButton = new MouseOverArea(container, new Image("gui/back_button.png"), container.getWidth()/2-50, 
				container.getHeight()-70,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						ClientEngine.getInstance().openMainScreen();
					}
				});
		backButton.setNormalColor(new Color(1,0,1,0.5f));
		backButton.setMouseOverColor(new Color(1,1,1,1.0f));
	}

	@Override
	public void render(GameContainer container, Graphics graphics)
			throws SlickException {
		// TODO Auto-generated method stub
		background.draw();
		backButton.render(container, graphics);
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException, IOException, JSONException,
			InterruptedException {
		// TODO Auto-generated method stub
		
	}

}
