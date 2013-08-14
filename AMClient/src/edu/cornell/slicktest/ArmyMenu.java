package edu.cornell.slicktest;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.MouseOverArea;

public class ArmyMenu implements Screen, ComponentListener
{

	private Image background;
	private Image title;
	private Panel unitsPanel;
	private Panel armyPanel;
	private Panel armyUnitsPanel;
	private MouseOverArea unitsPanelLeftButton;
	private MouseOverArea unitsPanelRightButton;
	private MouseOverArea armyPanelLeftButton;
	private MouseOverArea armyPanelRightButton;
	private MouseOverArea backButton;
	private Image inuseSymbol;
	private Image blankSlot;
	
	private MouseOverArea useButton;
	private MouseOverArea unuseButton;
	private MouseOverArea trashButton;
	
	private final int unitSlotX = 150;
	private final int unitSlotY = 187;
	
	// Units panel control
	private final int unitsPanelRowCount = 2;
	private final int unitsPanelColumnCount = 2;
	private final int unitsPanelSize = 4;
	private int unitsPanelCount = 0;
	private int unitsPanelFinalCount;
	private final int armyX = 140;
	private final int armyY = 177;
	private MouseOverArea[][] armyButtons;
	
	// Army panel control
	private final int armyPanelRowCount = 2;
	private final int armyPanelColumnCount = 2;
	private final int armyPanelSize = 4;
	private int armyPanelCount = 0;
	private int armyPanelFinalCount;
	private final int loadoutX = 140;
	private final int loadoutY = 177;
	private MouseOverArea[][] loadoutButtons;
	
	// Item movement control
	private Unit selectedUnit;
	private int selectedX;
	private int selectedY;
	
	public ArmyMenu(GameContainer container) throws SlickException
	{
		background = new Image("gui/background.png");
		background.setColor(Image.BOTTOM_RIGHT, 1f, 0.6f, 0.6f, 1f);
		
		title = new Image("gui/army.png");
		title.setColor(Image.TOP_LEFT, 1f, 0.6f, 0.6f, 1f);
		
		int offsetY = 80;
		int panelOffsetY = 10;
		int leftOffsetX = 50;
		int rightOffsetX = 25;
		
		Player player = Player.getInstance();
		
		blankSlot = new Image("gui/empty_slot.png");
		
		// Items Panel
		
		unitsPanel = new Panel(new Image("gui/units_panel.png"), 20, offsetY);
		unitsPanelFinalCount = player.items.size()/unitsPanelSize;
	
		
		int xpos;
		int ypos;
		armyButtons = new MouseOverArea[unitsPanelRowCount][unitsPanelColumnCount];
		for (int i = 0; i < armyButtons.length; i++) {
			for (int j = 0; j < armyButtons[i].length; j++) {
				xpos = unitsPanel.getMinX()+unitSlotX*j+(unitSlotX-armyX)/2;
				ypos = unitsPanel.getMinY()+unitSlotY*i+(unitSlotY-armyY)/2;
				armyButtons[i][j] = new MouseOverArea(container, blankSlot, 
						xpos, ypos, this);
				//armyButtons[i][j].setNormalColor(Color.black);
			}
		}
		
		
		
		unitsPanelLeftButton = new MouseOverArea(container, new Image("gui/left_button.png"), 
				unitsPanel.getCenterX()-leftOffsetX, unitsPanel.getMaxY() + panelOffsetY,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						clickUnitPanelLeftButton();
					}
				});
		unitsPanelLeftButton.setNormalColor(new Color(1,1,1,0.5f));
		unitsPanelLeftButton.setMouseOverColor(new Color(1,1,1,1.0f));
		unitsPanelRightButton = new MouseOverArea(container, new Image("gui/right_button.png"), 
				unitsPanel.getCenterX()+rightOffsetX, unitsPanel.getMaxY() + panelOffsetY,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						clickUnitPanelRightButton();
					}
				});
		unitsPanelRightButton.setNormalColor(new Color(1,1,1,0.5f));
		unitsPanelRightButton.setMouseOverColor(new Color(1,1,1,1.0f));
		
		
		// Army Panel
		
		armyPanel = new Panel(new Image("gui/army_all_panel.png"), unitsPanel.getMaxX()+65, offsetY);
		armyPanelFinalCount = player.units.size() - 1;
		
		loadoutButtons = new MouseOverArea[armyPanelRowCount][armyPanelColumnCount];
		for (int i = 0; i < armyPanelRowCount; i++) {
			for (int j = 0; j < armyPanelColumnCount; j++) {
				xpos = armyPanel.getMinX()+unitSlotX*j+(unitSlotX-armyX)/2;
				ypos = armyPanel.getMinY()+unitSlotY*i+(unitSlotY-armyY)/2;
				loadoutButtons[i][j] = new MouseOverArea(container, blankSlot, 
						xpos, ypos, this);
				//armyButtons[i][j].setNormalColor(Color.black);
			}
		}
		
		armyPanelLeftButton = new MouseOverArea(container, new Image("gui/left_button.png"), 
				armyPanel.getCenterX()-leftOffsetX, armyPanel.getMaxY() + panelOffsetY,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						clickUnitPanelLeftButton();
					}
				});
		armyPanelLeftButton.setNormalColor(new Color(1,1,1,0.5f));
		armyPanelLeftButton.setMouseOverColor(new Color(1,1,1,1.0f));
		armyPanelRightButton = new MouseOverArea(container, new Image("gui/right_button.png"), 
				armyPanel.getCenterX()+rightOffsetX, armyPanel.getMaxY() + panelOffsetY,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						clickUnitPanelRightButton();
					}
				});
		armyPanelRightButton.setNormalColor(new Color(1,1,1,0.5f));
		armyPanelRightButton.setMouseOverColor(new Color(1,1,1,1.0f));
		
		/*armyUnitButtons = new MouseOverArea[armyUnitsPanelSize];
		for (int i = 0; i < armyUnitButtons.length; i++) {
			xpos = armyPanel.getMinX() + (unitSlotX-armyX)/2;
			ypos = armyPanel.getMinY() + unitSlotY*i + (unitSlotY-armyY)/2;
 			armyUnitButtons[i] = new MouseOverArea(container, blankSlot, 
						xpos, ypos, this);
		}*/
		
		backButton = new MouseOverArea(container, new Image("gui/back_button.png"), 
				container.getWidth()/2-50, container.getHeight()-70,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						ClientEngine.getInstance().openMainScreen();
					}
				});
		backButton.setNormalColor(new Color(1,1,1,0.5f));
		backButton.setMouseOverColor(new Color(1,1,1,1.0f));
		
		useButton = new MouseOverArea(container, new Image("gui/right_button.png"), 
				unitsPanel.getMaxX()+20, unitsPanel.getCenterY()-50,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						clickUseButton();
					}
				});
		useButton.setNormalColor(new Color(1,1,1,0.5f));
		useButton.setMouseOverColor(new Color(1,1,1,1.0f));
		
		unuseButton = new MouseOverArea(container, new Image("gui/left_button.png"), 
				unitsPanel.getMaxX()+20, unitsPanel.getCenterY()+25,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						clickUnuseButton();
					}
				});
		unuseButton.setNormalColor(new Color(1,1,1,0.5f));
		unuseButton.setMouseOverColor(new Color(1,1,1,1.0f));
		
		trashButton = new MouseOverArea(container, new Image("gui/trash_button.png"), 
				unitsPanel.getMinX(), unitsPanel.getMaxY()+10,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						clickTrashButton();
					}
				});
		trashButton.setNormalColor(new Color(1,1,1,0.5f));
		trashButton.setMouseOverColor(new Color(1,1,1,1.0f));
	}
	
	@Override
	public void render(GameContainer container, Graphics graphics)
			throws SlickException {
		// TODO Auto-generated method stub
		background.draw();
		title.draw(20, 20);
		unitsPanel.render();
		armyPanel.render();
		//unitItemsPanel.render();
		
		// buttons
		if (unitsPanelCount > 0) {
			unitsPanelLeftButton.render(container, graphics);
		}
		if  (unitsPanelCount < unitsPanelFinalCount) {
			unitsPanelRightButton.render(container, graphics);
		}
		
		if (armyPanelCount > 0) {
			armyPanelLeftButton.render(container, graphics);
		}
		if (armyPanelCount < armyPanelFinalCount) {
			armyPanelRightButton.render(container, graphics);
		}
		
		backButton.render(container, graphics);
		inuseSymbol = new Image("gui/used.png");
		
		useButton.render(container, graphics);
		unuseButton.render(container, graphics);
		trashButton.render(container, graphics);
		
		Player player = Player.getInstance();
		
		// draw units
		Image unitImage;
		Unit unit;
		int rowNum;
		int colomnNum;
		int xpos;
		int ypos;
		int unitsPanelInit = unitsPanelCount*unitsPanelSize;
		for (int i = unitsPanelInit; i < (unitsPanelCount+1)*unitsPanelSize; i++) {
			rowNum = (i-unitsPanelInit)/unitsPanelColumnCount;
			colomnNum = (i-unitsPanelInit)%unitsPanelColumnCount;
			if (i < player.units.size()) {
				unit = player.units.get(i);
				Point frameSize = SpriteSheetInfo.getUnitSpriteSheetFrameSize(unit.factoryKey);
				SpriteSheet unitSheet = new SpriteSheet(unit.equippedAttributes.spritesheetPath, frameSize.x, frameSize.y);
				unitImage = unitSheet.getSprite(0, 0);
				//unitImage = unit.spriteSheet.getSprite(0, 0).getScaledCopy(armySize, armySize);
				xpos = unitsPanel.getMinX()+unitSlotX*colomnNum+(unitSlotX-armyX)+50;
				ypos = unitsPanel.getMinY()+unitSlotY*rowNum+(unitSlotY-armyY)+80;
				Point centerPoint = new Point(xpos, ypos);
				UnitRenderer.renderUnit(container,  graphics, unit, centerPoint);
				//armyButtons[rowNum][colomnNum].setNormalImage(unitImage);
				//armyButtons[rowNum][colomnNum].setMouseOverImage(unitImage);
				if (unit.inuse) {
					inuseSymbol.draw(xpos+30, ypos+30);
				}
			}
			else {
				armyButtons[rowNum][colomnNum].setNormalImage(blankSlot);
				armyButtons[rowNum][colomnNum].setMouseOverImage(blankSlot);
			}
			
		}
		
		for (int i = 0; i < armyButtons.length; i++) {
			for (int j = 0; j < armyButtons[i].length; j++) {
				armyButtons[i][j].render(container, graphics);
				if (armyButtons[i][j].isMouseOver()) {
					int unitIndex = i*unitsPanelColumnCount + j + unitsPanelSize*unitsPanelCount;
					if (player.units.size() > unitIndex) {
						unit = player.units.get(unitIndex);
						String desc = unit.getDescription();
						int centerX = armyButtons[i][j].getX() + armyButtons[i][j].getWidth();
						int centerY = armyButtons[i][j].getY() + armyButtons[i][j].getHeight();
						drawPopover(container, graphics, desc, centerX, centerY);
						
					}
				}
			}
		}
		
		// draw army
		ArrayList<Unit> army = player.army.units;
		//Point centerPoint = new Point(armyPanel.getMinX()+175, armyPanel.getMinY()+100);
		//UnitRenderer.renderUnit(container, graphics, army, centerPoint);
		//String unitDesc = army.getDescription();
		//AMFonts.getArialBold16().drawString(armyPanel.getMinX()+85, armyPanel.getMinY()+210, unitDesc, Color.black);
		
		// draw army loadout
		//DIS HEER AM POSITIONING PROBLEM
		int count = 0;
		for (int i = 0; i < armyPanelColumnCount; i++) 
		{
			for(int j = 0; j < armyPanelRowCount; j++)
			{
				if (count < army.size()) 
				{
					unit = army.get(count);
					xpos = armyPanel.getMinX()+unitSlotX*i+(unitSlotX-armyX)/2+55;
					ypos = armyPanel.getMinY()+unitSlotY*j+(unitSlotY-armyY)/2+85;
					Point centerPoint = new Point(xpos, ypos);
					UnitRenderer.renderUnit(container, graphics, unit, centerPoint);
					//armyUnitButtons[i].setNormalImage(unitImage);
					//armyUnitButtons[i].setMouseOverImage(unitImage);
					count++;
				}
				else 
				{
					loadoutButtons[i][j].setNormalImage(blankSlot);
					loadoutButtons[i][j].setMouseOverImage(blankSlot);
					count++;
				}
			}
		}
		
		for (int i = 0; i < armyPanelColumnCount; i++) 
		{
			for (int j = 0; j < armyPanelRowCount; j++)
			{
				loadoutButtons[i][j].render(container, graphics);
				if (loadoutButtons[i][j].isMouseOver()) 
				{
					if (army.size() > i) {
						unit = army.get(i);
						String desc = unit.getDescription();
						int centerX = loadoutButtons[i][j].getX() + loadoutButtons[i][j].getWidth()/2;
						int centerY = loadoutButtons[i][j].getY() + loadoutButtons[i][j].getHeight()/2;
						drawPopover(container, graphics, desc, centerX, centerY);
					}
				}
			}
		}
		
		// Draw selection box
		
		if (selectedUnit != null) {
			graphics.setColor(Color.yellow);
			graphics.drawRect(selectedX, selectedY, armyX, armyY);
		}
		
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException, IOException, JSONException,
			InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentActivated(AbstractComponent component) {
		// TODO Auto-generated method stub
		
		// Check items panel slots
		for (int i = 0; i < armyButtons.length; i++) {
			for (int j = 0; j < armyButtons[i].length; j++) {
				if (armyButtons[i][j] == component) {
					int index = unitsPanelCount*unitsPanelSize + i*unitsPanelColumnCount + j;
					if (Player.getInstance().items.size() > index) {
						selectedUnit = Player.getInstance().units.get(index);
						selectedX = armyButtons[i][j].getX();
						selectedY = armyButtons[i][j].getY();
					}
					else {
						selectedUnit = null;
					}
					return;
				}
			}
		}
	
		// Check unit items slots
		for (int i = 0; i < armyPanelColumnCount; i++) 
		{
			for(int j = 0; j < armyPanelRowCount; j++)
			{
				if (loadoutButtons[i][j] == component) 
				{
					Unit unit = Player.getInstance().units.get(armyPanelCount);
					if (unit.equipments.size() > i) 
					{
						selectedUnit = unit;
						selectedX = loadoutButtons[i][j].getX();
						selectedY = loadoutButtons[i][j].getY();
					}
					else 
					{
						selectedUnit = null;
					}
					return;
				}
			}
		}
	}
	
	private void drawPopover(GameContainer container, Graphics graphics, String desc, int centerX, int centerY) throws SlickException {
		int space = 10;
		int width = AMFonts.getArialBold16().getWidth(desc) + space;
		int height = AMFonts.getArialBold16().getHeight(desc) + space;
		int xpos;
		int ypos;
		if (centerY > container.getHeight()/2) {
			ypos = centerY - height;
		}
		else {
			ypos = centerY;
		}
		xpos = centerX - width/2;
		if (xpos < 0) {
			xpos = 0;
		}
		else  if (xpos + width > container.getWidth()) {
			xpos -= xpos + width - container.getWidth();
		}
		Color color = new Color(0.9f, 0.9f, 0, 0.7f);
		graphics.setColor(Color.yellow);
		graphics.drawRect(xpos, ypos, width, height);
		graphics.setColor(color);
		graphics.fillRect(xpos, ypos, width, height);
		AMFonts.getArialBold16().drawString(xpos + space/2, ypos + space/2, desc, Color.black);
	}
	
	// Button event listeners
	
	public void clickItemPanelLeftButton() {
		if (unitsPanelCount > 0) {
			unitsPanelCount--;
			selectedUnit = null;
		}
	}
	
	public void clickItemPanelRightButton() {
		if (unitsPanelCount < unitsPanelFinalCount) {
			unitsPanelCount++;
			selectedUnit = null;
		}
	}
	
	public void clickUnitPanelLeftButton() {
		if (armyPanelCount > 0) {
			armyPanelCount--;
			selectedUnit = null;
		}
	}
	
	public void clickUnitPanelRightButton() {
		if (armyPanelCount < armyPanelFinalCount) {
			armyPanelCount++;
			selectedUnit = null;
		}
	}
	
	public void clickUseButton() {
		if (selectedUnit != null) {
			Player.getInstance().army.units.add(selectedUnit);
			selectedUnit.inuse = true;
		}
	}
	
	public void clickUnuseButton() {
		if (selectedUnit != null) {
			Player.getInstance().army.units.remove(selectedUnit);
			selectedUnit.inuse = false;
		}
	}
	
	public void clickTrashButton() {
		if (selectedUnit != null) {
			Player player = Player.getInstance();
			if (player.army.units.contains(selectedUnit)) 
			{
				player.army.units.remove(selectedUnit);
			}
			player.units.remove(selectedUnit);
			selectedUnit = null;
		}
	}

}
