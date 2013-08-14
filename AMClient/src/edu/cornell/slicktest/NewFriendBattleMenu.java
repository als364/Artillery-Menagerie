package edu.cornell.slicktest;

import java.io.IOException;
import java.util.Map.Entry;

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

public class NewFriendBattleMenu implements Screen {
	
	private Image background;
	private GameContainer container;
	private ClientEngine main;
	private AMSelector battleSelector;
	private MouseOverArea requestButton;
	private MouseOverArea backButton;
	private AMPopUp popUp;
	
	public NewFriendBattleMenu(GameContainer container, ClientEngine main) throws SlickException, IOException, JSONException {
		this.main = main;
		this.container = container;
		background = new Image("gui/background.png");
	
		popUp = null;
		
		battleSelector = new AMSelector(container, AMFonts.getArialRegular16(), (container.getWidth() - AMSelector.getWidth())/2, 50);
		JSONArray friendList = ServerConnection.friendList(Player.getInstance().userID);
		/*
		JSONArray friendList = new JSONArray();
		friendList.put("a000");
		friendList.put("a111");
		friendList.put("a222");
		friendList.put("a333");
		friendList.put("a444");
		friendList.put("a555");
		friendList.put("a666");
		friendList.put("a777");
		friendList.put("a888");
		friendList.put("a999");
		*/
		
		for (int i = 0; i < friendList.length(); i++) {
			JSONObject friend = friendList.getJSONObject(i);
			battleSelector.addSelection(new StringPair(friend.getString("name"), friend.getString("userID")));
		}
		
		requestButton = new MouseOverArea(container, new Image("gui/create_battle_button.png"), 
				container.getWidth()/2-200, container.getHeight()-70,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						try {
							clickRequestButton();
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
		requestButton.setNormalColor(new Color(1,1,1,1.0f));
		requestButton.setMouseOverColor(new Color(0.7f,0.7f,1,1.0f));
		
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
		requestButton.render(container, graphics);
		backButton.render(container, graphics);
		if (popUp != null) {
			popUp.draw(container, graphics);
		}
	}

	public void update(GameContainer container, int delta) {
		if (popUp != null && popUp.needToClose()) {
			popUp = null;
		}
	}
	
	private void clickRequestButton() throws IOException, JSONException, SlickException {
		StringPair content = battleSelector.getSelectedContent();
		if (content != null) {
			System.out.println(content.getValue());
			String battleKey = ServerConnection.newBattle(Player.getInstance().userID, content.getValue());
			ServerConnection.joinBattle(battleKey, Player.getInstance().userID, Player.getInstance().army.createArmyJSON().toString());
			popUp = new AMPopUp(container, "Battle request is sent");
		}
	}
	
	private void clickBackButton() throws SlickException {
		main.openBattleMenu();
	}
}
