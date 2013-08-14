package edu.cornell.slicktest;

public class ProjectedPath {
	public double[][] matrix;
	
	// true if this path collides with a unit
	public boolean collidedWithUnit;
	// true if this path collides with anything
	public boolean collided;
	
	// the x coordinate that this projectile should never pass
	public int collision_x;
	public int collision_y;
	public ProjectedPath (double[][] matrix, boolean collided, boolean collidedWithUnit, int x, int y) {
		this.matrix = matrix;
		this.collided = collided;
		this.collidedWithUnit = collidedWithUnit;
		this.collision_x = x;
		this.collision_y = y;
	}
}
