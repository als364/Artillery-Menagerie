package edu.cornell.slicktest;
import java.lang.*;

import org.newdawn.slick.SpriteSheet;
import edu.cornell.slicktest.Enums.*;

public class Projectile 
{

	private final double PROJECTILE_VELOCITY = 0.6;
	private double speed;
	private double currentX;
	private double currentY;
	private Direction direction;
	private double apexX;
	private double apexY;
	private double baseX;
	private double baseY;
	
	private double splashRadius = 40.0;
	
	private double a;
	private double b;
	private double c;
	
	private double dir;

	private Unit shotBy;
	
	int collision_x;
	int collision_y;
	
	// this is going to be given two points, cursor/shooter position and apex of curve.
	// Second point will always be the apex.  The first point will be one of two
	// things:  where the cursor is, or where the firer is.  The former case occurs
	// when the firer is the apex of the parabola.
	public Projectile(double x1, double y1, double x2, double y2, boolean apex_is_shooter, Direction dir, Unit shooter,int collision_x, int collision_y){
		
		if(x1 == x2){
			x1++;
		}
		
		if(y1 == y2){
			y1++;
		}
		
		direction = dir;
	
		if (y2 < y1){
			apexX = x2;
			apexY = y2;
			baseX = x1;
			baseY = y1;
		} else{
			apexX = x1;
			apexY = y1;
			baseX = x2;
			baseY = y2;
		}
		/*if (apex_is_shooter){
			currentX = x2;
			currentY = y2;
		}
		else{
			currentX = x1;
			currentY = y1;
		}*/
		
		/*if((apex_is_shooter && currentX < baseX) || (!apex_is_shooter && currentX < apexX)){
			this.dir = 1.0;
		}else{
			this.dir = -1.0;
		}
		*/
		this.dir = x1 < x2 ? 1: -1;
		if(this.dir == -1.0){
			currentX = (int)Math.round(x1) - 10;
		}else{
			currentX = (int)Math.round(x1);
		}
		currentY = y1;
		
		a = (baseY - apexY)/(Math.pow(baseX, 2.0) - 2*baseX*apexX + Math.pow(apexX, 2.0));
		b = -2*a*apexX;
		c = apexY + a*Math.pow(apexX, 2.0);
		
		double init_deriv = 2*a*currentX + b;
		double init_angle = Math.atan2(init_deriv, 1.0);
		speed = PROJECTILE_VELOCITY*Math.cos(init_angle);
		
		shotBy = shooter;
		
		this.collision_x = collision_x;
		this.collision_y = collision_y;
	}	
	
	public double getX(){
		return currentX;
	}
	
	public double getY(){
		return currentY;
	}
		
	public double getAngle() {
		double deriv = 2*a*Math.pow(currentX, 2.0) + b;
		return Math.atan2(deriv, 1.0);
	}
	
	public double getSplashRadius(){
		return splashRadius;
	}
	
	public int getCollisionX(){
		return collision_x;
	}
	
	public int getCollisionY(){
		return collision_y;
	}
	
	public ProjectileState updatePosition(int delta){
		
		double next_x = currentX + (double)delta*speed*dir;
		double next_y = a*Math.pow(next_x, 2.0) + b*next_x + c;
		currentX = next_x;
		currentY = next_y;
		boolean colliding;
		if(dir == -1.0){
			colliding = currentX <= this.collision_x;
		}
		else{
			colliding = currentX >= this.collision_x;
		}
		
		// Hit Unit
		if (colliding) {
			return ProjectileState.HITUNIT;
		}
		
		return ProjectileState.INAIR;
		
	}
}
