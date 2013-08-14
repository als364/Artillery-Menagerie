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

public class RequestsReceivedMenu implements Screen {
	private Image background;
	private GameContainer container;
	private ClientEngine main;
	private AMSelector battleSelector;
	private MouseOverArea acceptButton;
	private MouseOverArea rejectButton;
	private MouseOverArea backButton;
	
	public RequestsReceivedMenu(GameContainer container, ClientEngine main) throws SlickException, IOException, JSONException {
		this.main = main;
		this.container = container;
		background = new Image("gui/background.png");
		
		battleSelector = new AMSelector(container, AMFonts.getArialRegular16(), (container.getWidth() - AMSelector.getWidth())/2, 50);
		
		JSONArray requestList = ServerConnection.getBattleList("received", Player.getInstance().userID);
		
		String temp;
		JSONObject currentObject;
		for (int i = 0; i < requestList.length(); i++) {
			currentObject = requestList.getJSONObject(i);
			temp = currentObject.getString("player1Name") + " vs " + currentObject.getString("player2Name") + "\n";
			temp += (String) currentObject.get("startDate");
			battleSelector.addSelection(new StringPair(temp, currentObject.getString("battleKey")));
		}
		
		acceptButton = new MouseOverArea(container, new Image("gui/accept_button.png"), 
				container.getWidth()/2-250, container.getHeight()-70,
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
		
		rejectButton = new MouseOverArea(container, new Image("gui/reject_button.png"), 
				container.getWidth()/2-50, container.getHeight()-70,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						try {
							clickRejectButton();
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
		rejectButton.setNormalColor(new Color(1,1,1,1.0f));
		rejectButton.setMouseOverColor(new Color(0.7f,0.7f,1,1.0f));
		
		backButton = new MouseOverArea(container, new Image("gui/back_button_blue.png"), 
				container.getWidth()/2+150, container.getHeight()-70,
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
		rejectButton.render(container, graphics);
		backButton.render(container, graphics);
	}

	public void update(GameContainer container, int delta) {
		
	}
	
	private void clickAcceptButton() throws IOException, JSONException, SlickException {
		StringPair content = battleSelector.getSelectedContent();
		if (content != null) {
			ServerConnection.joinBattle(content.getValue(), Player.getInstance().userID, Player.getInstance().army.createArmyJSON().toString());
			RequestsReceivedMenu battle = new RequestsReceivedMenu(container, main);
			main.setCurrentScreen(battle);
		}
	}
	
	private void clickRejectButton() throws IOException, SlickException, JSONException {
		StringPair content = battleSelector.getSelectedContent();
		if (content != null) {
			ServerConnection.cancelBattle(Player.getInstance().userID, content.getValue());
			RequestsReceivedMenu battle = new RequestsReceivedMenu(container, main);
			main.setCurrentScreen(battle);
		}
	}
	
	private void clickBackButton() throws SlickException {
		main.openBattleMenu();
	}
}
