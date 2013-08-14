package edu.cornell.slicktest;

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

public class BattleMenu implements Screen {
	
	private ClientEngine main;
	private GameContainer container;
	
	private Image background;
	private Image title;
	private MouseOverArea newBattleButton;
	private MouseOverArea requestsSentButton;
	private MouseOverArea requestsReceivedButton;
	private MouseOverArea ongoingBattlesButton;
	private MouseOverArea trainingButton;
	private MouseOverArea newRandomBattleButton;
	private MouseOverArea randomBattleRequestsButton;
	private MouseOverArea backButton;
	private AMPopUp popUp;
	
	
	
	public BattleMenu(GameContainer container, ClientEngine main) throws SlickException {
		this.main = main;
		this.container = container;
		background = new Image("gui/background.png");
		title = new Image("gui/battle.png");
		
		
		newBattleButton = new MouseOverArea(container, new Image("gui/new_battle_button.png"), 
				20, 60,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						try {
							clickNewBattleButton();
						} catch (SlickException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		newBattleButton.setNormalColor(new Color(1,1,1,1.0f));
		newBattleButton.setMouseOverColor(new Color(0.7f,0.7f,1,1.0f));
		requestsSentButton = new MouseOverArea(container, new Image("gui/sent_requests_button.png"), 
				20, 120,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						try {
							clickRequestsSentButton();
						} catch (SlickException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		requestsSentButton.setNormalColor(new Color(1,1,1,1.0f));
		requestsSentButton.setMouseOverColor(new Color(0.7f,0.7f,1,1.0f));
		requestsReceivedButton = new MouseOverArea(container, new Image("gui/received_requests_button.png"), 
				20, 180,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						try {
							clickRequestsReceivedButton();
						} catch (SlickException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		requestsReceivedButton.setNormalColor(new Color(1,1,1,1.0f));
		requestsReceivedButton.setMouseOverColor(new Color(0.7f,0.7f,1,1.0f));
		trainingButton = new MouseOverArea(container, new Image("gui/training_button.png"), 
				20, 240,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						try {
							clickTrainingButton();
						} catch (SlickException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
				});
		trainingButton.setNormalColor(new Color(1,1,1,1.0f));
		trainingButton.setMouseOverColor(new Color(0.7f,0.7f,1,1.0f));
		ongoingBattlesButton = new MouseOverArea(container, new Image("gui/ongoing_battles_button.png"), 
				20, 300,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						try {
							clickOngoingBattlesButton();
						} catch (SlickException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
				});
		ongoingBattlesButton.setNormalColor(new Color(1,1,1,1.0f));
		ongoingBattlesButton.setMouseOverColor(new Color(0.7f,0.7f,1,1.0f));
		newRandomBattleButton = new MouseOverArea(container, new Image("gui/new_random_battle_button.png"), 
				20, 360,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						try {
							clickNewRandomBattleButton();
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
		newRandomBattleButton.setNormalColor(new Color(1,1,1,1.0f));
		newRandomBattleButton.setMouseOverColor(new Color(0.7f,0.7f,1,1.0f));
		randomBattleRequestsButton = new MouseOverArea(container, new Image("gui/random_battle_requests_button.png"), 
				20, 420,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						try {
							clickRandomBattleRequestsButton();
						} catch (SlickException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		randomBattleRequestsButton.setNormalColor(new Color(1,1,1,1.0f));
		randomBattleRequestsButton.setMouseOverColor(new Color(0.7f,0.7f,1,1.0f));
		backButton = new MouseOverArea(container, new Image("gui/back_button_blue.png"), 
				container.getWidth()/2-50, container.getHeight()-70,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						clickBackButton();
					}
				});
		backButton.setNormalColor(new Color(1,1,1,1.0f));
		backButton.setMouseOverColor(new Color(0.7f,0.7f,1,1.0f));
		
	}

	@Override
	public void render(GameContainer container, Graphics graphics)
			throws SlickException {
		// TODO Auto-generated method stub
		background.draw(0, 0);
		title.draw(20, 20);
		newBattleButton.render(container, graphics);
		requestsSentButton.render(container, graphics);
		requestsReceivedButton.render(container, graphics);
		ongoingBattlesButton.render(container, graphics);
		trainingButton.render(container, graphics);
		newRandomBattleButton.render(container, graphics);
		randomBattleRequestsButton.render(container, graphics);
		backButton.render(container, graphics);
		if (popUp != null) {
			popUp.draw(container, graphics);
		}
		
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException, IOException, JSONException,
			InterruptedException {
		// TODO Auto-generated method stub
		if (popUp != null && popUp.needToClose()) {
			popUp = null;
		}
	}
	
	//Button click
	public void clickNewBattleButton() throws SlickException, IOException, JSONException {
		NewFriendBattleMenu battle = new NewFriendBattleMenu(container, main);
		main.setCurrentScreen(battle);
	}
	
	public void clickRequestsSentButton() throws SlickException, IOException, JSONException {
		RequestsSentMenu battle = new RequestsSentMenu(container, main);
		main.setCurrentScreen(battle);
	}
	
	public void clickRequestsReceivedButton() throws SlickException, IOException, JSONException {
		RequestsReceivedMenu battle = new RequestsReceivedMenu(container, main);
		main.setCurrentScreen(battle);
	}
	
	public void clickOngoingBattlesButton() throws SlickException, IOException, JSONException, InterruptedException {
		/*
		BattleScreen battle = new BattleScreen(main, false);
		battle.init(container);
		main.setCurrentScreen(battle);
		*/
		OngoingBattlesMenu battle = new OngoingBattlesMenu(container, main);
		main.setCurrentScreen(battle);
		
	}
	
	public void clickTrainingButton() throws SlickException, IOException, JSONException, InterruptedException {
		BattleScreen battle = new BattleScreen(main, true);
		battle.init(container);
		main.setCurrentScreen(battle);
	}
	
	public void clickNewRandomBattleButton() throws IOException, JSONException, SlickException {
		String battleKey = ServerConnection.newBattle(Player.getInstance().userID);
		ServerConnection.joinBattle(battleKey, Player.getInstance().userID, Player.getInstance().army.createArmyJSON().toString());
		popUp = new AMPopUp(container, "Battle request is sent");
	}
	
	public void clickRandomBattleRequestsButton() throws SlickException, IOException, JSONException {
		RandomBattleRequestsMenu battle = new RandomBattleRequestsMenu(container, main);
		main.setCurrentScreen(battle);
	}
	
	public void clickBackButton() {
		main.openMainScreen();
	}
	
}
