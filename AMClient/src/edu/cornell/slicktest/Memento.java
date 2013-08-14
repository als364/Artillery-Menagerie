package edu.cornell.slicktest;

import java.util.ArrayList;

import org.jbox2d.common.Vec2;
import org.newdawn.slick.geom.Rectangle;

import edu.cornell.slicktest.Enums.UnitState;

public class Memento 
{
	protected Physics worldState;
	protected ArrayList<Vec2[]> unit_positions;
	protected ArrayList<int[]> unit_health;
	protected int[] army_energies;
	protected ArrayList<boolean[]> unit_done;
	protected ArrayList<boolean[]> unit_dirty;
	protected int mementoCount = 0;
	
	/*
	public Memento(ArrayList<Army> armies, Physics physics)
	{
		this.armies = new ArrayList<Army>();
		for(Army army : armies){
			this.armies.add(army);
		}
		worldState = physics.clone();
		physics.revertDirty();
	}
	*/
	
	public Memento(ArrayList<Army> armies, Physics physics){
		int numUnits = 0;
		for(Army army : armies)
		{
			numUnits += army.units.size();
		}
		unit_positions = new ArrayList<Vec2[]>();
		unit_health = new ArrayList<int[]>();
		army_energies = new int[armies.size()];
		unit_done = new ArrayList<boolean[]>();
		unit_dirty = new ArrayList<boolean[]>();
		for(int i = 0; i < armies.size(); i++)
		{
			/*for(int j = 0; j < armies.size(); j++)
			{
				int index = j*armies.get(0).units.size() + i;
				if (index < unit_positions.length) {
					unit_positions[index] = new Vec2(armies.get(j).units.get(i).getUnitRectangle().getX(), armies.get(j).units.get(i).getUnitRectangle().getY());
					unit_health[index] = armies.get(j).units.get(i).getCurrentHealth();
				}
				if(i == 0)
				{
					army_energies[j] = armies.get(j).currentEnergy;
				}
			}*/
			army_energies[i] = armies.get(i).currentEnergy;
			int[] thisArmyHealth = new int[armies.get(i).units.size()];
			Vec2[] thisArmyPosition = new Vec2[thisArmyHealth.length];
			boolean[] thisArmyDone = new boolean[thisArmyHealth.length];
			boolean[] thisArmyDirty = new boolean[thisArmyHealth.length];
			for(int j = 0; j < armies.get(i).units.size(); j++)
			{
				thisArmyHealth[j] = armies.get(i).units.get(j).getCurrentHealth();
				thisArmyPosition[j] = new Vec2(armies.get(i).units.get(j).getUnitRectangle().getX(), 
											   armies.get(i).units.get(j).getUnitRectangle().getY());
				thisArmyDone[j] = armies.get(i).done.get(j);
				thisArmyDirty[j] = armies.get(i).units.get(j).getDirt();
			}
			unit_positions.add(thisArmyPosition);
			unit_health.add(thisArmyHealth);
			unit_done.add(thisArmyDone);
			unit_dirty.add(thisArmyDirty);
		}
		worldState = physics.clone();
		physics.revertDirty();
	}
	/*
	public String toString2()
	{
		return "Memento: Energy 0: " + armies.get(0).currentEnergy + ", Energy 1: " + armies.get(1).currentEnergy;
	}
	*/
	public String toString()
	{
		return "Memento: Energy 0: " + army_energies[0] + ", Energy 1: " + army_energies[1];
	}
}
