package edu.cornell.slicktest;

import java.applet.Applet;
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
import org.newdawn.slick.AppletGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import edu.cornell.slicktest.Enums.Items;
import edu.cornell.slicktest.Enums.Screens;
import edu.cornell.slicktest.Enums.Units;

public class ClientEngine extends BasicGame{
	
	boolean loadFromServer = true;
	
	private static ClientEngine instance = null;
	private GameContainer container;
	private BattleMenu battleMenu;
	private MainMenu mainMenu;
	private ArmyMenu armyMenu;
	private TutorialStart tutorial;
	private InventoryMenu inventoryMenu;
	private Screen currentScreen;
	// Screens current;
	
	public ClientEngine() {
		super("Artillery Menagerie");
	}
	
	public static ClientEngine getInstance() {
		if (instance == null) {
			instance = new ClientEngine();
		}
		return instance;
	}
	
	public void openBattleMenu() throws SlickException {
		currentScreen = battleMenu;
	}
	
	public void openMainScreen() {
		currentScreen = mainMenu;
	}
	
	public void openInventoryScreen() {
		currentScreen = inventoryMenu;
	}
	
	public void openArmyScreen() {
		currentScreen = armyMenu;
	}
	
	public void openShopScreen() {
	}
	
	public void openTutorialScreen() {
		currentScreen = tutorial;
	}
	
	public void openSettingScreen() {
	}
	
	public void setCurrentScreen(Screen screen) {
		currentScreen = screen;
	}
	
	public void updatePlayerToServer() throws IOException, JSONException {
		ServerConnection.updatePlayer(Player.getInstance().createJSONObject());
	}
	
	public void endBattle() {
		currentScreen = mainMenu;
	}
	
	@Override
	public void init(GameContainer container) throws SlickException {
		
		this.container = container;
		
		container.setTargetFrameRate(60);
		
		// Create the player
		Player player = Player.getInstance();
		player.userID = "player" + (int)(Math.random()*10000000);
		if (container instanceof AppletGameContainer.Container) {
			Applet applet = ((AppletGameContainer.Container) container).getApplet();
			player.userID = applet.getParameter("userID");
		}
		
		if (!loadFromServer) {
			Unit unit1 = Factory.getUnit(Units.SPACEMARINE, "Marine 1", Math.random()+"");
			Unit unit2 = Factory.getUnit(Units.SPACEMARINE, "Marine 2", Math.random()+"");
			Unit unit3 = Factory.getUnit(Units.SPACEMARINE, "Marine 3", Math.random()+"");
			Unit unit4 = Factory.getUnit(Units.SPACEMARINE, "Marine 4", Math.random()+"");
			player.units.add(unit1);
			player.units.add(unit2);
			player.units.add(unit3);
			player.units.add(unit4);
			player.army.units.add(unit1);
			player.army.units.add(unit2);
			player.army.units.add(unit3);
			player.army.units.add(unit4);
			for (int i = 0 ; i < 50; i++) {
				player.items.add(Factory.getItem(Items.ROCKET_LAUNCHER, Math.random() + ""));
			}
			unit1.equipItem(player.items.get(0));
			player.money = 100;
			player.rank = 1000;
		}
		else {
			try {
				//player.userID = "635257593";
				//player.userID = "100003142498214";
				player.name = player.userID;
				
				JSONObject playerData = ServerConnection.logIn(player.userID, player.name);
				if (player.loadJSONObject(playerData)) {
					player.newAccountResouces();
					ServerConnection.updatePlayer(player.createJSONObject());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		mainMenu = new MainMenu(container, this);
		inventoryMenu = new InventoryMenu(container, this);
		armyMenu = new ArmyMenu(container);
		tutorial = new TutorialStart(container);
		battleMenu = new BattleMenu(container, this);
		currentScreen = mainMenu;
	}
	
	@Override
	public void render(GameContainer container, Graphics graphics) throws SlickException {
		currentScreen.render(container, graphics);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		try {
			currentScreen.update(container, delta);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public static void main(String[] argv) {
		try {
			
			AppGameContainer container = new AppGameContainer(ClientEngine.getInstance());
			//container.setDisplayMode(1100,600,false);
			container.setDisplayMode(680,510,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

}
