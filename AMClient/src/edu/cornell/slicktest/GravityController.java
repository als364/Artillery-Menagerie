package edu.cornell.slicktest;

import java.awt.Point;
import java.util.ArrayList;

import org.newdawn.slick.Image;

import edu.cornell.slicktest.Enums.UnitState;

public class GravityController 
{
	 Ground ground;
	 int width;
	 int height;
	 
	 public GravityController(Ground g)
	 {
		 ground = g;
		 width = ground.getWidth();
		 height = ground.getHeight();
	 }
	 
	 public ArrayList<Army> updateGravity(ArrayList<Army> armies)
	 {
		 for(Army army: armies)
		 {
			 for(Unit unit : army.units)
			 {
				 if(!onGround(unit))
				 {
					 int nextGround = nextGround(unit);
					 if (unit.getUnitState() != UnitState.JUMP && unit.getUnitState() != UnitState.FALL) {
						 if((nextGround(unit) - unit.getUnitRectangle().getMinY()) < unit.gravity)
						 {
							 unit.setUnitPositionY(nextGround);
						 }
						 else
						 {
							 unit.setUnitPositionY(unit.getUnitRectangle().getY() + unit.gravity);
							 unit.gravity++;
						 }
					 }
				 }
			 }
		 }
		 return armies;
	 }
	 
	 private boolean onGround(Unit unit)
	 {
		 boolean[] points = ground.isGround[(int) unit.getUnitRectangle().getCenterX()];
		 return(points[(int) (unit.getUnitRectangle().getMaxY()-1)]);
	 }
	 
	 private int nextGround(Unit unit)
	 {
		 boolean[] points = ground.isGround[(int) unit.getUnitRectangle().getCenterX()];
		 int counter = (int) unit.getUnitRectangle().getMaxY();
		 while(!points[counter])
		 {
			 counter++;
		 }
		 counter++;
		 return counter;
	 }
}
