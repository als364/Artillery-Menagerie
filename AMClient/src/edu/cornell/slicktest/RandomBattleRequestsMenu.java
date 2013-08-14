package edu.cornell.slicktest;

import java.io.IOException;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.MouseOverArea;

public class RandomBattleRequestsMenu implements Screen {
	private Image background;
	private GameContainer container;
	private ClientEngine main;
	private AMSelector battleSelector;
	private MouseOverArea acceptButton;
	private MouseOverArea backButton;
	
	public RandomBattleRequestsMenu(GameContainer container, ClientEngine main) throws SlickException, IOException, JSONException {
		this.main = main;
		this.container = container;
		background = new Image("gui/background.png");
		
		battleSelector = new AMSelector(container, AMFonts.getArialRegular16(), (container.getWidth() - AMSelector.getWidth())/2, 50);
		
		JSONArray requestList = ServerConnection.getBattleList("random", Player.getInstance().userID);
		
		String temp;
		JSONObject currentObject;
		for (int i = 0; i < requestList.length(); i++) {
			currentObject = requestList.getJSONObject(i);
			temp = currentObject.getString("player1Name") + " vs ???\n";
			temp += (String) currentObject.get("startDate");
			battleSelector.addSelection(new StringPair(temp, currentObject.getString("battleKey")));
		}
		
		acceptButton = new MouseOverArea(container, new Image("gui/accept_button.png"), 
				container.getWidth()/2-200, container.getHeight()-70,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						try {
							clickAcceptButton();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SlickException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		acceptButton.setNormalColor(new Color(1,1,1,1.0f));
		acceptButton.setMouseOverColor(new Color(0.7f,0.7f,1,1.0f));
		
		backButton = new MouseOverArea(container, new Image("gui/back_button_blue.png"), 
				container.getWidth()/2+100, container.getHeight()-70,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						try {
							clickBackButton();
						} catch (SlickException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		backButton.setNormalColor(new Color(1,1,1,1.0f));
		backButton.setMouseOverColor(new Color(0.7f,0.7f,1,1.0f));
	}
	
	public void render(GameContainer container, Graphics graphics)
			throws SlickException {
		background.draw();
		battleSelector.render(container, graphics);
		acceptButton.render(container, graphics);
		backButton.render(container, graphics);
	}

	public void update(GameContainer container, int delta) {
		
	}
	
	private void clickAcceptButton() throws IOException, JSONException, SlickException {
		StringPair content = battleSelector.getSelectedContent();
		if (content != null) {
			ServerConnection.joinBattle(content.getValue(), Player.getInstance().userID, Player.getInstance().army.createArmyJSON().toString());
			RandomBattleRequestsMenu battle = new RandomBattleRequestsMenu(container, main);
			main.setCurrentScreen(battle);
		}
	}
	private void clickBackButton() throws SlickException {
		main.openBattleMenu();
	}
}
