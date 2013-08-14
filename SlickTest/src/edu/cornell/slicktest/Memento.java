package edu.cornell.slicktest;

import java.util.ArrayList;

import org.jbox2d.common.Vec2;
import org.newdawn.slick.geom.Rectangle;

import edu.cornell.slicktest.Enums.UnitState;

public class Memento 
{
	protected ArrayList<Army> armies;
	protected Physics worldState;
	protected Vec2[] unit_positions;
	protected int[] unit_health;
	protected int[] army_energies;
	
	public Memento(ArrayList<Army> armies, Physics physics)
	{
		this.armies = new ArrayList<Army>();
		for(Army army : armies){
			this.armies.add(army);
		}
		worldState = physics.clone();
	}
	
	public Memento(ArrayList<Army> armies){
		int numUnits = 0;
		for(Army army : armies){
			numUnits += army.units.size();
		}
		unit_positions = new Vec2[numUnits];
		unit_health = new int[numUnits];
		army_energies = new int[armies.size()];
		for(int i = 0; i < armies.get(0).units.size(); i++){
			for(int j = 0; j < armies.size(); j++){
				unit_positions[j*armies.get(0).units.size() + i] = new Vec2(armies.get(j).units.get(i).getUnitRectangle().getX(), armies.get(j).units.get(i).getUnitRectangle().getY());
				unit_health[j*armies.get(0).units.size() + i] = armies.get(j).units.get(i).getCurrentHealth();
				if(i == 0)
				army_energies[j] = armies.get(j).currentEnergy;
			}
		}
	}
	
	public String toString2()
	{
		return "Memento: Energy 0: " + armies.get(0).currentEnergy + ", Energy 1: " + armies.get(1).currentEnergy;
	}
	
	public String toString()
	{
		return "Memento: Energy 0: " + army_energies[0] + ", Energy 1: " + army_energies[1];
	}
}
