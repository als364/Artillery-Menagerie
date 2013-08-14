package edu.cornell.slicktest;

import java.awt.Point;
import java.util.ArrayList;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.MouseOverArea;

public class MainMenu implements Screen, ComponentListener {

	private Image background;
	private Panel playerPanel;
	private MouseOverArea battleButton;
	private MouseOverArea inventoryButton;
	private MouseOverArea armyButton;
	private MouseOverArea shopButton;
	private MouseOverArea tutorialButton;
	private Point[] unitPositions;
	private ClientEngine main;
	private MouseOverArea[] unitMouseOvers;
	private MouseOverArea rocket;
	private AMPopover popover;
	
	private Image blank;
	
	public MainMenu(GameContainer container, ClientEngine main) throws SlickException {
		this.main = main;
		background = new Image("gui/menu1.png");
		//background.setColor(Image.BOTTOM_RIGHT, 1f, 0.6f, 0.6f, 1f);
		blank = new Image("gui/blank.png");
		
		//playerPanel = new Panel(new Image("gui/player_panel.png"), 240, 20);
		int offsetY = 20;
		battleButton = new MouseOverArea(container, new Image("gui/battle_button.png"), 20, offsetY,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						try {
							clickBattleButton();
						} catch (SlickException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		battleButton.setMouseOverImage(new Image("gui/battle_hover_button.png"));
		//battleButton.setNormalColor(new Color(1,1,1,0.5f));
		//battleButton.setMouseOverColor(new Color(1,1,1,1.0f));
		
		inventoryButton = new MouseOverArea(container, new Image("gui/inventory_button.png"), 345, offsetY,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						clickInventoryButton();
					}
				});
		inventoryButton.setMouseOverImage(new Image("gui/inventory_hover_button.png"));
		//inventoryButton.setNormalColor(new Color(1,1,1,0.5f));
		//inventoryButton.setMouseOverColor(new Color(1,1,1,1.0f));
		
		armyButton = new MouseOverArea(container, new Image("gui/army_button.png"), 180, offsetY,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						ClientEngine.getInstance().openArmyScreen();
					}
				});
		armyButton.setMouseOverImage(new Image("gui/army_hover_button.png"));
		//armyButton.setNormalColor(new Color(1,1,1,0.5f));
		//armyButton.setMouseOverColor(new Color(1,1,1,1.0f));
		
		/*shopButton = new MouseOverArea(container, new Image("gui/shop_button.png"), offsetX, offsetY, 
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						ClientEngine.getInstance().openShopScreen();
					}
				});*/
		//shopButton.setNormalColor(new Color(1,1,1,0.5f));
		//shopButton.setMouseOverColor(new Color(1,1,1,1.0f));
		
		tutorialButton = new MouseOverArea(container, new Image("gui/tutorial_button.png"), 508, offsetY,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						ClientEngine.getInstance().openTutorialScreen();
					}
				});
		tutorialButton.setMouseOverImage(new Image("gui/tutorial_hover_button.png"));
		//tutorialButton.setNormalColor(new Color(1,1,1,0.5f));
		//tutorialButton.setMouseOverColor(new Color(1,1,1,1.0f));
		
		/*Point[] temp = {new Point(345,220), new Point(555, 220), new Point(345,420), new Point(555, 420)};
		unitPositions = temp;
		
		ArrayList<Unit> unitList = Player.getInstance().army.units;
		unitMouseOvers = new MouseOverArea[4];
		for (int i = 0; i < unitMouseOvers.length; i++) {
			unitMouseOvers[i] = new MouseOverArea(container, blank, temp[i].x-75, temp[i].y-75, 150, 150, this);
		}*/
		
		rocket = new MouseOverArea (container, new Image("gui/amrocket.png"), 38, 377);
	}

	public void render(GameContainer container, Graphics graphics) throws SlickException {
		background.draw();
		
		// Player panel

		//playerPanel.render();
		//Player player = Player.getInstance();
		//Color textColor = Color.black;
		//AMFonts.getArialBold16().drawString(260, 40, player.userID, textColor);
		//AMFonts.getArialBold16().drawString(260, 60, "Gold: " + player.money, textColor);
		//AMFonts.getArialBold16().drawString(260, 80, "Rank: " + player.rank, textColor);

		/*boolean mouseOver = false;
		Unit unit;
		for (int i = 0; i < player.army.units.size() && i < 4; i++) {
			if (i < player.army.units.size()) {
				unit = player.army.units.get(i);
				UnitRenderer.renderUnit(container, graphics, unit, unitPositions[i]);
				unitMouseOvers[i].render(container, graphics);
				if (unitMouseOvers[i].isMouseOver()) {
					if (!mouseOver) {
						popover = new AMPopover(container, unit.getDescription(), unitPositions[i].x, unitPositions[i].y);
					}
					mouseOver = true;
				}
			}
		}
		
		if (!mouseOver) {
			popover = null;
		}
		
		if (popover != null) {
			popover.draw(graphics);
		}*/
		
		
		// buttons
		battleButton.render(container, graphics);
		inventoryButton.render(container, graphics);
		armyButton.render(container, graphics);
		//shopButton.render(container, graphics);
		tutorialButton.render(container, graphics);	
		rocket.render(container,  graphics);
		
		Player player = Player.getInstance();
		/*Color textColor = Color.yellow;
		AMFonts.getArialBold16().drawString(260, 40, player.userID, textColor);
		AMFonts.getArialBold16().drawString(260, 60, "Gold: " + player.money, textColor);
		AMFonts.getArialBold16().drawString(260, 80, "Rank: " + player.rank, textColor);*/
	}
	
	public void update(GameContainer container, int delta) {
	}

	public void componentActivated(AbstractComponent arg0) {
		// TODO Auto-generated method stub
		
	}

	private void clickBattleButton() throws SlickException {
		//ClientEngine.getInstance().openBattleScreen();
		main.openBattleMenu();
	}
	
	private void clickInventoryButton() {
		//ClientEngine.getInstance().openInventoryScreen();
		main.openInventoryScreen();
	}
}
