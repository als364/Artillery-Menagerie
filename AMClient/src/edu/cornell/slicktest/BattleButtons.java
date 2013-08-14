package edu.cornell.slicktest;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class BattleButtons {
	
	//Buttons
	private Image battleButtons1;
	private Image battleButtons2;
	private Image battleButtons3;
	private Image battleButtons4;
	private Image battleButtons5;
	
	private Image current;
	
	public BattleButtons() throws SlickException {
		battleButtons1 = new Image("gui/button1.png");
		battleButtons2 = new Image("gui/button2.png");
		battleButtons3 = new Image("gui/button3.png");
		battleButtons4 = new Image("gui/button4.png");
		battleButtons5 = new Image("gui/button5.png");
		current = battleButtons1;
	}
	
	public void draw() {
		current.draw();
	}
	
	public int checkMouseIn(int xpos, int ypos) {
		if (ypos > 445 ) {
			if (xpos < 180) {
				current = battleButtons2;
				return 2;
			}
			else if (xpos < 230) {
				current = battleButtons1;
				return 1;
			}
			else if (xpos < 390) {
				current = battleButtons3;
				return 3;
			}
			else if (xpos < 430) {
				current = battleButtons1;
				return 1;
			}
			else if (xpos < 560) {
				current = battleButtons4;
				return 4;
			}
			else if (xpos < 570) {
				current = battleButtons1;
				return 1;
			}
			else if (xpos < 680) {
				current = battleButtons5;
				return 5;
			}
		}
		current = battleButtons1;
		return 0;
	}
}
