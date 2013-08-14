package edu.cornell.slicktest;


import org.newdawn.slick.geom.Rectangle;

import edu.cornell.slicktest.Enums.*;

public class BattleStates {
	
	protected AttributeSet currentAttributes;
	protected UnitState state;
	protected Direction direction;
	protected Rectangle rectangle;
	protected float jumpPeek;
	
	public BattleStates(AttributeSet initialAttribute) {
		currentAttributes = new AttributeSet(initialAttribute);
		state = UnitState.STAND;
		direction = Direction.RIGHT;
		rectangle = new Rectangle(0, 0, 0, 0);
	}
	
	public BattleStates(AttributeSet initialAttribute, UnitState initialState, Direction initialDirection, Rectangle initialRectangle) {
		currentAttributes = new AttributeSet(initialAttribute);
		state = initialState;
		direction = initialDirection;
		rectangle = initialRectangle;
	}
	
	public BattleStates clone(){
		BattleStates toReturn = new BattleStates(currentAttributes, state, direction, rectangle);
		toReturn.rectangle = new Rectangle(rectangle.getMinX(), rectangle.getMinY(), rectangle.getMaxX(), rectangle.getMaxY());
		
		return toReturn;
	}
}
