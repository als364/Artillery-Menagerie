package edu.cornell.slicktest;

public class Enums {
	public enum UnitState
	{
		STAND, JUMP, FLY, WALK, FALL, SHOOT
	}
	
	public enum Direction
	{
		LEFT, RIGHT
	}
	
	public enum AbilityType
	{
		PROJECTILE_DAMAGE, PROJECTILE_HEAL, HEAL, TRAP
	}
	
	public enum ProjectileState
	{
		INAIR, HITGROUND, HITUNIT
	}
	
	// For Factory
	
	public enum Units
	{
		DEFAULT, SPACEMARINE, CENTAUR, UNICORN, FAIRY, ROBOT, AI, ALIEN, DRAGON
	}
	
	public enum Items
	{
		DEFAULT, ROCKET_LAUNCHER
	}
	
	public enum Abilities
	{
		DEFAULT, BOMB
	}
	
	public enum Screens
	{
		MAIN, INVENTORY, BATTLE
	}
}
