package edu.cornell.slicktest;

import java.util.ArrayList;

public class Army
{
	protected ArrayList<Unit> units;
	protected ArrayList<Boolean> done;
	protected int maxHealth;
	protected int currentHealth;
	protected int maxEnergy;
	protected int currentEnergy;
	protected int side;
	
	public Army(){
		
	}
	
	public Army(ArrayList<Unit> unitlist, int energyPool)
	{
		units = unitlist;
		maxHealth = 0;
		currentHealth = 0;
		done = new ArrayList<Boolean>();
		for(int i = 0; i < units.size(); i++)
		{
			maxHealth += units.get(i).getMaxHealth();
			done.add(false);
		}
		currentHealth += maxHealth;
		maxEnergy = energyPool;
	}
	
	public void refreshEnergy() 
	{
		currentEnergy = maxEnergy;
		
		for (int i = 0; i < done.size(); i++) {
			done.set(i, false);
		}
	}
	
	public boolean isLost() {
		return units.size() == 0;
	}
	
	public Army clone(){
		Army toReturn = new Army();
		
		
		
		return toReturn;
	}
}
