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
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.MouseOverArea;

public class InventoryMenu implements Screen, ComponentListener {
	
	private Image background;
	private Image title;
	private Panel itemsPanel;
	private Panel unitPanel;
	private MouseOverArea itemsPanelLeftButton;
	private MouseOverArea itemsPanelRightButton;
	private MouseOverArea unitPanelLeftButton;
	private MouseOverArea unitPanelRightButton;
	private MouseOverArea backButton;
	private Image equippedSymbol;
	private Image blankSlot;
	
	private MouseOverArea equipButton;
	private MouseOverArea unequipButton;
	private MouseOverArea trashButton;
	
	private final int itemSlotSize = 75;
	
	// Items panel control
	private final int itemsPanelRowCount = 5;
	private final int itemsPanelColumnCount = 4;
	private final int itemsPanelSize = 20;
	private int itemsPanelCount = 0;
	private int itemPanelFinalCount;
	private final int itemSize = 65;;
	private MouseOverArea[][] itemButtons;
	
	// Unit panel control
	private int unitPanelCount = 0;
	private int unitPanelFinalCount;
	private final int unitItemsPanelSize = 5;
	private MouseOverArea[] unitItemButtons;
	
	// Item movement control
	private Item selectedItem;
	private int selectedX;
	private int selectedY;
	
	private ClientEngine main;
	
	private AMPopover popover;
	
	public InventoryMenu(GameContainer container, ClientEngine main) throws SlickException {
		this.main = main;
		background = new Image("gui/background.png");
		background.setColor(Image.BOTTOM_RIGHT, 1f, 0.6f, 0.6f, 1f);
		
		title = new Image("gui/inventory.png");
		//title.setColor(Image.TOP_LEFT, 1f, 0.6f, 0.6f, 1f);
		
		int offsetY = 60;
		int panelOffsetY = 10;
		int leftOffsetX = 50;
		int rightOffsetX = 25;
		
		Player player = Player.getInstance();
		
		blankSlot = new Image("gui/empty_slot.png");
		
		// Items Panel
		
		itemsPanel = new Panel(new Image("gui/items_panel.png"), 20, offsetY);
		itemPanelFinalCount = player.items.size()/itemsPanelSize;
		
		int xpos;
		int ypos;
		itemButtons = new MouseOverArea[itemsPanelRowCount][itemsPanelColumnCount];
		for (int i = 0; i < itemButtons.length; i++) {
			for (int j = 0; j < itemButtons[i].length; j++) {
				xpos = itemsPanel.getMinX()+itemSlotSize*j+(itemSlotSize-itemSize)/2;
				ypos = itemsPanel.getMinY()+itemSlotSize*i+(itemSlotSize-itemSize)/2;
				itemButtons[i][j] = new MouseOverArea(container, blankSlot, 
						xpos, ypos, this);
			}
		}
		
		itemsPanelLeftButton = new MouseOverArea(container, new Image("gui/left_button.png"), 
				itemsPanel.getCenterX()-leftOffsetX, itemsPanel.getMaxY() + panelOffsetY,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						clickItemPanelLeftButton();
					}
				});
		itemsPanelLeftButton.setNormalColor(new Color(1,1,1,1.0f));
		itemsPanelLeftButton.setMouseOverColor(new Color(0.7f,0.7f,1,1.0f));
		
		itemsPanelRightButton = new MouseOverArea(container, new Image("gui/right_button.png"), 
				itemsPanel.getCenterX()+rightOffsetX, itemsPanel.getMaxY() + panelOffsetY,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						clickItemPanelRightButton();
					}
				});
		itemsPanelRightButton.setNormalColor(new Color(1,1,1,1.0f));
		itemsPanelRightButton.setMouseOverColor(new Color(0.7f,0.7f,1,1.0f));
		
		// Unit Panel
		
		unitPanel = new Panel(new Image("gui/unit_all_panel.png"), itemsPanel.getMaxX()+65, offsetY);
		unitPanelFinalCount = player.units.size() - 1;
		
		unitPanelLeftButton = new MouseOverArea(container, new Image("gui/left_button.png"), 
				unitPanel.getCenterX()-leftOffsetX, unitPanel.getMaxY() + panelOffsetY,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						clickUnitPanelLeftButton();
					}
				});
		unitPanelLeftButton.setNormalColor(new Color(1,1,1,1.0f));
		unitPanelLeftButton.setMouseOverColor(new Color(0.7f,0.7f,1,1.0f));
		unitPanelRightButton = new MouseOverArea(container, new Image("gui/right_button.png"), 
				unitPanel.getCenterX()+rightOffsetX, unitPanel.getMaxY() + panelOffsetY,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						clickUnitPanelRightButton();
					}
				});
		unitPanelRightButton.setNormalColor(new Color(1,1,1,1.0f));
		unitPanelRightButton.setMouseOverColor(new Color(0.7f,0.7f,1,1.0f));
		
		unitItemButtons = new MouseOverArea[unitItemsPanelSize];
		for (int i = 0; i < unitItemButtons.length; i++) {
			xpos = unitPanel.getMinX() + (itemSlotSize-itemSize)/2;
			ypos = unitPanel.getMinY() + itemSlotSize*i + (itemSlotSize-itemSize)/2;
 			unitItemButtons[i] = new MouseOverArea(container, blankSlot, 
						xpos, ypos, this);
		}
		
		backButton = new MouseOverArea(container, new Image("gui/back_button.png"), 
				container.getWidth()/2-50, container.getHeight()-70,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						clickBackButton();
					}
				});
		backButton.setNormalColor(new Color(1,1,1,1.0f));
		backButton.setMouseOverColor(new Color(0.7f,0.7f,1,1.0f));
		
		equipButton = new MouseOverArea(container, new Image("gui/right_button.png"), 
				itemsPanel.getMaxX()+20, itemsPanel.getCenterY()-50,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						try {
							clickEquipButton();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		equipButton.setNormalColor(new Color(1,1,1,1.0f));
		equipButton.setMouseOverColor(new Color(0.7f,0.7f,1,1.0f));
		
		unequipButton = new MouseOverArea(container, new Image("gui/left_button.png"), 
				itemsPanel.getMaxX()+20, itemsPanel.getCenterY()+25,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						try {
							clickUnequipButton();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		unequipButton.setNormalColor(new Color(1,1,1,1.0f));
		unequipButton.setMouseOverColor(new Color(0.7f,0.7f,1,1.0f));
		
		trashButton = new MouseOverArea(container, new Image("gui/trash_button.png"), 
				itemsPanel.getMinX(), itemsPanel.getMaxY()+5,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						try {
							clickTrashButton();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		trashButton.setNormalColor(new Color(1,1,1,0.5f));
		trashButton.setMouseOverColor(new Color(1,1,1,1.0f));
		trashButton.setNormalColor(new Color(1,1,1,1.0f));
		trashButton.setMouseOverColor(new Color(0.7f,0.7f,1,1.0f));
	}
	
	public void render(GameContainer container, Graphics graphics) throws SlickException {
		background.draw();
		title.draw(20, 20);
		itemsPanel.render();
		unitPanel.render();
		//unitItemsPanel.render();
		
		// buttons
		if (itemsPanelCount > 0) {
			itemsPanelLeftButton.render(container, graphics);
		}
		if  (itemsPanelCount < itemPanelFinalCount) {
			itemsPanelRightButton.render(container, graphics);
		}
		
		if (unitPanelCount > 0) {
			unitPanelLeftButton.render(container, graphics);
		}
		if (unitPanelCount < unitPanelFinalCount) {
			unitPanelRightButton.render(container, graphics);
		}
		
		backButton.render(container, graphics);
		equippedSymbol = new Image("gui/equipped.png");
		
		equipButton.render(container, graphics);
		unequipButton.render(container, graphics);
		trashButton.render(container, graphics);
		
		Player player = Player.getInstance();
		
		// draw items
		Image itemImage;
		Item item;
		int rowNum;
		int colomnNum;
		int xpos;
		int ypos;
		int itemsPanelInit = itemsPanelCount*itemsPanelSize;
		for (int i = itemsPanelInit; i < (itemsPanelCount+1)*itemsPanelSize; i++) {
			rowNum = (i-itemsPanelInit)/itemsPanelColumnCount;
			colomnNum = (i-itemsPanelInit)%itemsPanelColumnCount;
			if (i < player.items.size()) {
				item = player.items.get(i);
				itemImage = item.spriteSheet.getSprite(0, 0).getScaledCopy(itemSize, itemSize);
				xpos = itemsPanel.getMinX()+itemSlotSize*colomnNum+(itemSlotSize-itemSize)/2;
				ypos = itemsPanel.getMinY()+itemSlotSize*rowNum+(itemSlotSize-itemSize)/2;
				itemButtons[rowNum][colomnNum].setNormalImage(itemImage);
				itemButtons[rowNum][colomnNum].setMouseOverImage(itemImage);
				if (item.equipped) {
					equippedSymbol.draw(xpos+30, ypos+30);
				}
			}
			else {
				itemButtons[rowNum][colomnNum].setNormalImage(blankSlot);
				itemButtons[rowNum][colomnNum].setMouseOverImage(blankSlot);
			}
			
		}
		
		boolean mouseOver = false;
		for (int i = 0; i < itemButtons.length; i++) {
			for (int j = 0; j < itemButtons[i].length; j++) {
				itemButtons[i][j].render(container, graphics);
				if (itemButtons[i][j].isMouseOver()) {
					int itemIndex = i*itemsPanelColumnCount + j + itemsPanelSize*itemsPanelCount;
					if (player.items.size() > itemIndex) {
						item = player.items.get(itemIndex);
						String desc = item.getDescription();
						int centerX = itemButtons[i][j].getX() + itemButtons[i][j].getWidth()/2;
						int centerY = itemButtons[i][j].getY() + itemButtons[i][j].getHeight()/2;
						//drawPopover(container, desc, centerX, centerY);
						if (!mouseOver) {
							popover = new AMPopover(container, desc, centerX, centerY);
						}
						mouseOver = true;
					}
				}
			}
		}
		
		if (!mouseOver) {
			popover = null;
		}
		
		if (popover != null) {
			popover.draw(graphics);
		}
		
		// draw unit
		Unit unit = player.units.get(unitPanelCount);
		Point centerPoint = new Point(unitPanel.getMinX()+175, unitPanel.getMinY()+100);
		UnitRenderer.renderUnit(container, graphics, unit, centerPoint);
		String unitDesc = unit.getDescription();
		AMFonts.getArialBold16().drawString(unitPanel.getMinX()+85, unitPanel.getMinY()+210, unitDesc, Color.black);
		
		// draw unit equipments
		
		for (int i = 0; i < unitItemsPanelSize; i++) {
			if (i < unit.equipments.size()) {
				item = unit.equipments.get(i);
				itemImage = item.spriteSheet.getSprite(0, 0).getScaledCopy(65, 65);
				xpos = unitPanel.getMinX()+(itemSlotSize-itemSize)/2;
				ypos = unitPanel.getMinY()+itemSlotSize*i+(itemSlotSize-itemSize)/2;
				unitItemButtons[i].setNormalImage(itemImage);
				unitItemButtons[i].setMouseOverImage(itemImage);
			}
			else {
				unitItemButtons[i].setNormalImage(blankSlot);
				unitItemButtons[i].setMouseOverImage(blankSlot);
			}
		}
		
		for (int i = 0; i < unitItemButtons.length; i++) {
			unitItemButtons[i].render(container, graphics);
			if (unitItemButtons[i].isMouseOver()) {
				if (unit.equipments.size() > i) {
					item = unit.equipments.get(i);
					String desc = item.getDescription();
					int centerX = unitItemButtons[i].getX() + unitItemButtons[i].getWidth()/2;
					int centerY = unitItemButtons[i].getY() + unitItemButtons[i].getHeight()/2;
					drawPopover(container, graphics, desc, centerX, centerY);
				}
			}
		}
		
		// Draw selection box
		
		if (selectedItem != null) {
			graphics.setColor(Color.yellow);
			graphics.drawRect(selectedX, selectedY, 65, 65);
		}
	}
	
	public void update(GameContainer container, int delta) {
	}

	public void componentActivated(AbstractComponent component) {
		
		// Check items panel slots
		for (int i = 0; i < itemButtons.length; i++) {
			for (int j = 0; j < itemButtons[i].length; j++) {
				if (itemButtons[i][j] == component) {
					int index = itemsPanelCount*itemsPanelSize + i*itemsPanelColumnCount + j;
					if (Player.getInstance().items.size() > index) {
						selectedItem = Player.getInstance().items.get(index);
						selectedX = itemButtons[i][j].getX();
						selectedY = itemButtons[i][j].getY();
					}
					else {
						selectedItem = null;
					}
					return;
				}
			}
		}
	
		// Check unit items slots
		for (int i = 0; i < unitItemButtons.length; i++) {
			if (unitItemButtons[i] == component) {
				Unit unit = Player.getInstance().units.get(unitPanelCount);
				if (unit.equipments.size() > i) {
					selectedItem = unit.equipments.get(i);
					selectedX = unitItemButtons[i].getX();
					selectedY = unitItemButtons[i].getY();
				}
				else {
					selectedItem = null;
				}
				return;
			}
		}
	}
	
	// Popover
	
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
	
	private void clickItemPanelLeftButton() {
		if (itemsPanelCount > 0) {
			itemsPanelCount--;
			selectedItem = null;
		}
	}
	
	private void clickItemPanelRightButton() {
		if (itemsPanelCount < itemPanelFinalCount) {
			itemsPanelCount++;
			selectedItem = null;
		}
	}
	
	private void clickUnitPanelLeftButton() {
		if (unitPanelCount > 0) {
			unitPanelCount--;
			selectedItem = null;
		}
	}
	
	private void clickUnitPanelRightButton() {
		if (unitPanelCount < unitPanelFinalCount) {
			unitPanelCount++;
			selectedItem = null;
		}
	}
	
	private void clickEquipButton() throws IOException, JSONException {
		if (selectedItem != null) {
			Player.getInstance().units.get(unitPanelCount).equipItem(selectedItem);
			main.updatePlayerToServer();
		}
	}
	
	private void clickUnequipButton() throws IOException, JSONException {
		if (selectedItem != null) {
			Player.getInstance().units.get(unitPanelCount).removeItem(selectedItem);
			main.updatePlayerToServer();
		}
	}
	
	private void clickTrashButton() throws IOException, JSONException {
		if (selectedItem != null) {
			Player player = Player.getInstance();
			if (selectedItem.equipped) {
				for (Unit unit : player.units) {
					unit.removeItem(selectedItem);
				}
			}
			player.items.remove(selectedItem);
			selectedItem = null;
			main.updatePlayerToServer();
		}
	}
	
	private void clickBackButton() {
		main.openMainScreen();
	}
}
