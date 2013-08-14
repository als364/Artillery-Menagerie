package edu.cornell.slicktest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.json.JSONException;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.MouseOverArea;

public class AMSelector implements ComponentListener{
	
	private Image selectionSlotImage;
	private ArrayList<MouseOverArea> mouseOverAreas;
	private int selected;
	private ArrayList<StringPair> contents;
	private GameContainer container;
	private AngelCodeFont font;
	private int xpos;
	

	private int ypos;
	private static int width = 520;
	private static int height = 310;
	
	private int[] xOffsets = {0, 270};
	private int[] yOffsets = {0, 80, 160, 240};
	
	private int pageSize = 8;
	private int currentPage = 0;
	
	private MouseOverArea previousPageButton;
	private MouseOverArea nextPageButton;
	
	public AMSelector(GameContainer container, AngelCodeFont font, int xpos, int ypos) throws SlickException {
		this.container = container;
		mouseOverAreas = new ArrayList<MouseOverArea>();
		contents = new ArrayList<StringPair>();
		selectionSlotImage = new Image("gui/selectionSlot.png");
		selected = -1;
		
		this.font = font;
		this.xpos = xpos;
		this.ypos = ypos;
		
		currentPage = 0;
		
		// Page control buttons
		int controlButtonYOffset = 10;
		int controlButtonSpace = 60;
		previousPageButton = new MouseOverArea(container, new Image("gui/left_button.png"), 
				xpos, ypos + height + controlButtonYOffset,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						clickPreviousPageButton();
					}
				});
		previousPageButton.setNormalColor(new Color(1,1,1,0.5f));
		previousPageButton.setMouseOverColor(new Color(1,1,1,1.0f));
		nextPageButton = new MouseOverArea(container, new Image("gui/right_button.png"), 
				xpos+controlButtonSpace, ypos + height + controlButtonYOffset,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) {
						clickNextPageButton();
					}
				});
		nextPageButton.setNormalColor(new Color(1,1,1,0.5f));
		nextPageButton.setMouseOverColor(new Color(1,1,1,1.0f));
	}

	public void addSelection(StringPair content) {
		int num = mouseOverAreas.size();
		int x = xpos + xOffsets[num%2];
		int y = ypos + yOffsets[num%pageSize/2];
		MouseOverArea moa = new MouseOverArea(container, selectionSlotImage, x, y, this);
		mouseOverAreas.add(moa);
		contents.add(content);
	}
	
	public StringPair getSelectedContent() {
		if (selected != -1 && selected < contents.size()) {
			return contents.get(selected);
		}
		else {
			return null;
		}
	}
	
	public void render(GameContainer container, Graphics graphics) throws SlickException {
		
		int offset = 5;
		for (int i = currentPage*8; i < contents.size() && i < currentPage+pageSize; i++) {
			int x = xpos + xOffsets[i%2];
			int y = ypos + yOffsets[i%pageSize/2];
			mouseOverAreas.get(i).render(container, graphics);
			font.drawString(x+offset, y+offset, contents.get(i).getKey(), Color.black);
		}
		
		
		if (selected != -1) {
			graphics.setColor(Color.yellow);
			int x = xpos + xOffsets[selected%2];
			int y = ypos + yOffsets[selected%pageSize/2];
			graphics.drawRect(x, y, selectionSlotImage.getWidth(), selectionSlotImage.getHeight());
		}
		
		if (hasPreviousPage()) {
			previousPageButton.render(container, graphics);
		}
		if (hasNextPage()) {
			nextPageButton.render(container, graphics);
		}
	}
	
	public boolean hasPreviousPage() {
		return currentPage != 0;
	}
	
	public boolean hasNextPage() {
		return mouseOverAreas.size() > (currentPage+1)*pageSize;
	}
	
	public void goToPreviousPage() {
		if (currentPage > 0) {
			currentPage--;
		}
	}
	
	public void gotToNextPage() {
		if (currentPage < mouseOverAreas.size()%pageSize) {
			currentPage++;
		}
	}
 	
	public static int getWidth() {
		return width;
	}
	
	public static int getHeight() {
		return height;
	}
	
	public int getXpos() {
		return xpos;
	}

	public int getYpos() {
		return ypos;
	}

	public void componentActivated(AbstractComponent component) {
		for (int i = 0; i < mouseOverAreas.size(); i++) {
			if (mouseOverAreas.get(i) == component) {
				selected = i;
			}
		}
	}
	
	private void clickPreviousPageButton() {
		goToPreviousPage();
		selected = -1;
	}
	
	private void clickNextPageButton() {
		gotToNextPage();
		selected = -1;
	}
}
