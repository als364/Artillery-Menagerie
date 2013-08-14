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

public class RequestsSentMenu implements Screen {
	
	private Image background;
	private GameContainer container;
	private ClientEngine main;
	private AMSelector battleSelector;
	private MouseOverArea cancelButton;
	private MouseOverArea backButton;
	
	public RequestsSentMenu(GameContainer container, ClientEngine main) throws SlickException, IOException, JSONException {
		this.main = main;
		this.container = container;
		background = new Image("gui/background.png");
		
		battleSelector = new AMSelector(container, AMFonts.getArialRegular16(), (container.getWidth() - AMSelector.getWidth())/2, 50);
		
		JSONArray requestList = ServerConnection.getBattleList("sent", Player.getInstance().userID);
		
		String temp;
		JSONObject currentObject;
		for (int i = 0; i < requestList.length(); i++) {
			currentObject = requestList.getJSONObject(i);
			temp = currentObject.getString("player1Name") + " vs " + currentObject.getString("player2Name") + "\n";
			temp += (String) currentObject.get("startDate");
			battleSelector.addSelection(new StringPair(temp, currentObject.getString("battleKey")));
		}
		
		cancelButton = new MouseOverArea(container, new Image("gui/cancel_button.png"), 
				container.getWidth()/2-200, container.getHeight()-70,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						try {
							clickCancelButton();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SlickException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		cancelButton.setNormalColor(new Color(1,1,1,1.0f));
		cancelButton.setMouseOverColor(new Color(0.7f,0.7f,1,1.0f));
		
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
		cancelButton.render(container, graphics);
		backButton.render(container, graphics);
	}

	public void update(GameContainer container, int delta) {
		
	}
	
	private void clickCancelButton() throws IOException, SlickException, JSONException {
		StringPair content = battleSelector.getSelectedContent();
		if (content != null) {
			//System.out.println(content.getValue());
			ServerConnection.cancelBattle(Player.getInstance().userID, content.getValue());
			RequestsSentMenu battle = new RequestsSentMenu(container, main);
			main.setCurrentScreen(battle);
		}
	}
	
	private void clickBackButton() throws SlickException {
		main.openBattleMenu();
	}
}
