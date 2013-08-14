package edu.cornell.slicktest;

import java.awt.Container;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

import edu.cornell.slicktest.Enums.*;

public class Physics {
	
	private int worldWidth;
	private int worldHeight;
	
	private boolean[][] collisionBoard; 
	private boolean[][] collisionUnits;
	
	private Ground ground;
	private Image foreground;
	private ImageBuffer foregroundBuffer;
	
	private TerrainController terrainController;
	
	Projectile projectile;
	
	public Physics(){
		
	}
	
	public Physics(int worldWidth, int worldHeight, Image foreground) {
		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
		this.foreground = foreground;
		collisionBoard = new boolean[worldWidth][worldHeight];
		collisionUnits = new boolean[worldWidth][worldHeight];
		
		foregroundBuffer = new ImageBuffer(foreground.getWidth(), foreground.getHeight());
		for(int i = 0; i < foreground.getWidth(); i++){
			for(int j = 0; j < foreground.getHeight(); j++){
				Color col = foreground.getColor(i, j);
				foregroundBuffer.setRGBA(i, j, col.getRed(), 
						col.getGreen(), col.getBlue(), col.getAlpha());
				
				collisionBoard[i][j] = col.getAlpha() != 0;
			}
		}
		

		//terrainController = new TerrainController(ground);
		
		projectile = null;
	}
	
	public Image getUpdatedForeground() {
		return foreground;
	}
	
	public Ground getGround() {
		return ground;
	}
	

	
	public void updateCollisionUnits(ArrayList<Army> armies){
		collisionUnits = new boolean[worldWidth][worldHeight];
		
		for (Army army : armies) {
			for(Unit unit : army.units){
				Rectangle unitRectangle = unit.getUnitRectangle();
				for(int i = (int) unitRectangle.getX(); i < unitRectangle.getMaxX(); i++){
					for(int j = (int) unitRectangle.getY(); j < unitRectangle.getMaxY(); j++){
						if(i >= 0 && j >= 0 && i < collisionUnits.length && j < collisionUnits[0].length)
						collisionUnits[i][j] = true;
					}
				}
			}
		}
	}
	
	public void updateCollisionBoard(int x, int y, double rad){
		int r = (int)rad;
		Point2D point = new Point2D.Float(x, y);
		for(int i = Math.max(x - r, 0); (i < x + r) && (i < worldWidth); i++){
			for(int j = Math.max(y - r, 0); (j < y + r) && (j < worldHeight); j++){
				if(point.distance(i, j) < rad){
					collisionBoard[i][j] = false;
					Color col = foreground.getColor(i, j);
					foregroundBuffer.setRGBA(i, j, col.getRed(), col.getGreen(), col.getBlue(), 0);
				}
			}
		}
		foreground = foregroundBuffer.getImage();
		//ground.setForeground(foreground);
	}
	
	public ProjectedPath projectedPath(double mouseX, double mouseY, Rectangle unitRectangle){
		double apexY;
		double apexX;
		double baseX;
		double baseY;
		float tempX = unitRectangle.getX();
		float tempY = unitRectangle.getY();
		if(mouseX == tempX){
			tempX++;
		}
		if(mouseY == tempY){
			tempY++;
		}
		if(mouseY < tempY){
			apexY = mouseY;
			apexX = mouseX;
			baseX = tempX;
			baseY = tempY;
		}else{
			apexY = tempY;
			apexX = tempX;
			baseX = mouseX;
			baseY = mouseY;
		}
		
		int dir = tempX < mouseX ? 1: -1;
		
		double a = (baseY - apexY)/(Math.pow(baseX, 2.0) - 2*baseX*apexX + Math.pow(apexX, 2.0));
		double b = -2*a*apexX;
		double c = apexY + a*Math.pow(apexX, 2.0);
		
		int collisionY = 0;
		int currentX;
		if(dir == -1){
			currentX = (int)Math.round(unitRectangle.getX()) - 10;
		}else{
			currentX = (int)Math.round(unitRectangle.getX());
		}
		boolean collided = false;
		boolean collidedWithUnit = false;
		int collisionX;	
		Rectangle rect = unitRectangle;
		while(!collided && currentX < worldWidth && currentX >= 0){
			double currentY = a*Math.pow((double)currentX, 2.0) + b*(double)currentX + c;
			double nextY = a*Math.pow((double)(currentX + dir), 2.0) + b*(double)(currentX + dir) + c;
			for(int iterY = (int)Math.floor(currentY); (iterY < Math.ceil(nextY) && iterY < worldHeight && iterY >= 0); iterY++){
				if((collisionBoard[currentX][iterY] || collisionUnits[currentX][iterY]) && !collided && !rect.contains(currentX, iterY)){
					if (collisionUnits[currentX][iterY]) {
						collidedWithUnit = true;
					}
					collided = true;
					collisionY = iterY;
					
				}
			}
			currentX += dir;
		}
		if(!collided){
			collisionX = currentX;
		}else{
			collisionX = currentX;
		}
		
		double[][] toReturn = new double[10][2];
		int delta = (collisionX - (int)Math.round(unitRectangle.getX()))/9;
		for(int i = 0; i < 10; i++){
			toReturn[i][0] = unitRectangle.getX() + i*delta;
			toReturn[i][1] = a*Math.pow(toReturn[i][0], 2.0) + b*toReturn[i][0] + c;
		}
		return new ProjectedPath(toReturn, collided, collidedWithUnit, collisionX, collisionY);
	}
	
	public void terrainExplosion(int radius, int x, int y) {
		//foreground = terrainController.explode(40, x, y, foreground);
	}
	
	public void unitExplosion(ArrayList<Body> bodies, Vec2 impact, float force, float radius){
		for(Body b : bodies){
			Vec2 dir = b.getPosition().sub(impact);
			float dist = dir.length();
			dir.normalize();
			b.applyForce(dir.mul(force*(radius/dist)*(radius/dist)), b.getPosition());
		}
	}
	
	public float getInitY(float initX, int h){
		float toReturn = 10;  // Maybe use a raycast to find this?
		while(toReturn < (worldHeight - h - 1) && !collisionBoard[(int)initX][(int)toReturn + h + 1]){
			toReturn++;
		}
		return toReturn;
	}	
	
	public void updateCollisionTerrain(Vec2 center, MSTerrain _terrain)
    {
		Projectile p = projectile;
		byte value = 1;
        // for (float by = -(float)p.getSplashRadius(); by < p.getSplashRadius(); by += 0.1f)
		for (float by = -(float)40; by < 40; by += 0.1f)
        {
            // for (float bx = -(float)p.getSplashRadius(); bx < p.getSplashRadius(); bx += 0.1f)
            for (float bx = -(float)40; bx < 40; bx += 0.1f)
            {
                // if ((bx * bx) + (by * by) < p.getSplashRadius() * p.getSplashRadius())
            	if ((bx * bx) + (by * by) < 1600)
                {
                    float ax = bx + center.x;
                    float ay = by + center.y;
                    _terrain.ModifyTerrain(new Vec2(ax, ay), value);
                }
            }
        }
		_terrain.RegenerateTerrain();
    }

	
	//Projectile Handling Methods
	
	public void createProjectile(double x1, double y1, double x2, double y2, boolean apex_is_shooter, Direction dir, Unit shooter,int collision_x, int collision_y) {
		projectile = new Projectile(x1, y1, x2, y2, apex_is_shooter, dir, shooter,collision_x, collision_y);
	}
	
	public ProjectileState updateProjectile(int delta) {		
		return projectile.updatePosition(delta);
	}
	
	public Point getProjectilePositionPoint() {
		if (projectile == null) {
			return null;
		}
		else {
			return new Point((int)projectile.getX(), (int)projectile.getY());
		}
	}
	
	public Point getProjectileCollisionPoint() {
		return new Point((int)projectile.getCollisionX(), (int)projectile.getCollisionY());
	}
	
	public double getProjectileSplashRadius() {
		return projectile.getSplashRadius();
	}
	
	public Physics clone(){
		Physics toReturn = new Physics();
		
		for(int i = 0; i < collisionBoard.length; i++){
			for(int j = 0; j < collisionBoard[i].length; j++){
				toReturn.collisionBoard[i][j] = collisionBoard[i][j];
				toReturn.collisionUnits[i][j] = collisionUnits[i][j];
			}
		}
		
		toReturn.foreground = foreground.copy();
		toReturn.foregroundBuffer = new ImageBuffer(foreground.getWidth(), foreground.getHeight());
		toReturn.ground = new Ground(toReturn.foreground);
		toReturn.terrainController = new TerrainController(toReturn.ground);
		toReturn.worldHeight = worldHeight;
		toReturn.worldWidth = worldWidth;
		
		return toReturn;
	}
}
