package edu.cornell.slicktest;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.MouseOverArea;

public class BattlePopUp {
	private Image background1;
	private Image background3;
	private Image background2;
	private Image current;
	private boolean needToClose;
	
	public BattlePopUp(GameContainer container, String image1, String image2, String image3) throws SlickException {
		background1 = new Image(image1);
		background2 = new Image(image2);
		background3 = new Image(image3);
		current = background1;
		needToClose = false;
	}
	
	public void draw(int mouseX, int mouseY) {
		checkMouse(mouseX, mouseY);
		current.draw();
	}
	
	public int checkMouse(int mouseX, int mouseY) {
		if (mouseX > 215 && mouseX < 300 && mouseY > 255 && mouseY < 290) {
			current = background2;
			return 1;
		}
		if (mouseX > 365 && mouseX < 475 && mouseY > 255 && mouseY < 290) {
			current = background3;
			return 2;
		}
		
		return 0;
	}
}
