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

public class OngoingBattlesMenu implements Screen {
	
	private GameContainer container;
	private Image background;
	
	private ClientEngine main;
	private AMSelector battleSelector;
	private MouseOverArea enterButton;
	private MouseOverArea cancelButton;
	private MouseOverArea backButton;
	
	public OngoingBattlesMenu(GameContainer container, ClientEngine main) throws SlickException, IOException, JSONException {
		this.container = container;
		this.main = main;
		background = new Image("gui/background.png");
		
		battleSelector = new AMSelector(container, AMFonts.getArialRegular16(), (container.getWidth() - AMSelector.getWidth())/2, 50);
		
		JSONArray requestList = ServerConnection.getBattleList("ongoing", Player.getInstance().userID);
		
		String temp;
		JSONObject currentObject;
		for (int i = 0; i < requestList.length(); i++) {
			
			currentObject = requestList.getJSONObject(i);
			System.out.println(currentObject);
			temp = currentObject.getString("player1Name") + " vs " + currentObject.getString("player2Name") + "\n";
			temp += (String) currentObject.get("startDate") + "\n";
			if (currentObject.getString("player1UserID").equals(Player.getInstance().userID)) {
				if (currentObject.getInt("turn") % 2 == 0 || currentObject.getInt("turn") % 2 < 0) {
					temp += "Your turn";
				}
				else {
					temp += "Opponent's turn";
				}
			}
			else if (currentObject.getString("player2UserID").equals(Player.getInstance().userID)) {
				if (currentObject.getInt("turn") % 2 == 0 || currentObject.getInt("turn") % 2 < 0) {
					temp += "Opponent's turn";
				}
				else {
					temp += "Your turn";
				}
			}
			battleSelector.addSelection(new StringPair(temp, currentObject.getString("battleKey")));
		}
		
		enterButton = new MouseOverArea(container, new Image("gui/enter_battle_button.png"), 
				container.getWidth()/2-250, container.getHeight()-70,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						try {
							clickEnterButton();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SlickException e) {
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
				});
		enterButton.setNormalColor(new Color(1,1,1,1.0f));
		enterButton.setMouseOverColor(new Color(0.7f,0.7f,1,1.0f));
		
		cancelButton = new MouseOverArea(container, new Image("gui/cancel_button.png"), 
				container.getWidth()/2-50, container.getHeight()-70,
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
		enterButton.render(container, graphics);
		cancelButton.render(container, graphics);
		backButton.render(container, graphics);
	}

	public void update(GameContainer container, int delta) {
		
	}
	
	private void clickEnterButton() throws IOException, SlickException, JSONException, InterruptedException {
		StringPair content = battleSelector.getSelectedContent();
		if (content != null) {
			System.out.println("Enter the battle");
			BattleScreen battle = new BattleScreen(main, false);
			battle.resumeBattle(container, battleSelector.getSelectedContent().getValue());
			main.setCurrentScreen(battle);
		}
	}
	
	private void clickCancelButton() throws IOException, SlickException, JSONException {
		StringPair content = battleSelector.getSelectedContent();
		if (content != null) {
			ServerConnection.cancelBattle(Player.getInstance().userID, content.getValue());
			OngoingBattlesMenu battle = new OngoingBattlesMenu(container, main);
			main.setCurrentScreen(battle);
		}
	}
	
	private void clickBackButton() throws SlickException {
		main.openBattleMenu();
	}
}
