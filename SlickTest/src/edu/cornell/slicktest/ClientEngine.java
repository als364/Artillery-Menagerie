package edu.cornell.slicktest;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class ClientEngine extends BasicGame{
	
	BattleScreen battleScreen;
	
	public ClientEngine() {
		super("Artillery Menagerie");
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		battleScreen = new BattleScreen();
		try {
			battleScreen.init(container);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void render(GameContainer container, Graphics graphics) throws SlickException {
		battleScreen.render(container, graphics);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		try {
			battleScreen.update(container, delta);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] argv) {
		try {
			
			AppGameContainer container = new AppGameContainer(new ClientEngine());
			container.setDisplayMode(1100,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

}
