package edu.cornell.slicktest;

import java.io.IOException;

import org.json.JSONException;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public interface Screen {
	public void render(GameContainer container, Graphics graphics)  throws SlickException;
	public void update(GameContainer container, int delta) throws SlickException, IOException, JSONException, InterruptedException, Exception ;
}
