package edu.cornell.slicktest;

import org.jbox2d.common.Vec2;
import org.newdawn.slick.Image;

public class Level {

	protected Vec2[] startingPositions;
	protected Image foregroundImage;
	protected Image backgroundImage;
	
	public Level(Vec2[] sp, Image fi, Image bi){
		startingPositions = sp;
		foregroundImage = fi;
		backgroundImage = bi;
	}
	
	public Image getForeground(){
		return foregroundImage;
	}
	
	public Image getBackground(){
		return backgroundImage;
	}
	
	public Vec2[] getStarts(){
		return startingPositions;
	}
	
}
