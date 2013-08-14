package edu.cornell.slicktest;

import org.jbox2d.common.Vec2;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;

import edu.cornell.slicktest.Enums.Direction;
import edu.cornell.slicktest.Enums.UnitState;

public class BattleInputHandler {
	
	private double deltaMovement = 0;
	private int overLapCount = 0;
	
	public BattleInputHandler() {
		
	}
	
	public void handleUnitControl(GameContainer container, int delta, Ground ground, Unit unit) {
		
		UnitState state = unit.getUnitState();
		Rectangle unitRectangle = unit.getUnitRectangle();
		float x = unitRectangle.getX();
		float y = unitRectangle.getY();
		
		if (container.getInput().isKeyDown(Input.KEY_UP)||
				container.getInput().isKeyDown(Input.KEY_W)) {
			if (state != UnitState.JUMP && state != UnitState.FALL) {
				unit.setUnitState(UnitState.JUMP);
				unit.startJump();
			}
		}
		if (container.getInput().isKeyDown(Input.KEY_LEFT)||
				container.getInput().isKeyDown(Input.KEY_A)) {
			//unit.body().applyLinearImpulse(new Vec2(-unit.body().m_mass, 0), unit.body().getPosition());
			unit.body.setLinearVelocity(new Vec2 (-40, unit.body.getLinearVelocity().y));
			x -= delta*0.1;
			deltaMovement += delta*0.1;
			if (x < 0) {
				x = 0;
			}
			if (state == UnitState.STAND) {
				unit.setUnitState(UnitState.WALK);
			}
			unit.setUnitDirection(Direction.LEFT);
		}
		
		if (container.getInput().isKeyDown(Input.KEY_RIGHT)||
				container.getInput().isKeyDown(Input.KEY_D)) {
			//unit.body().applyLinearImpulse(new Vec2(unit.body().m_mass, 0), unit.body().getPosition());
			unit.body.setLinearVelocity(new Vec2 (40, unit.body.getLinearVelocity().y));
			x += delta*0.1;
			deltaMovement += delta*0.1;
			if (x > container.getWidth() - unit.getUnitRectangle().getWidth()) {
				x = container.getWidth() - unit.getUnitRectangle().getWidth();
			}
			if (state == UnitState.STAND) {
				unit.setUnitState(UnitState.WALK);
			}
			unit.setUnitDirection(Direction.RIGHT);
		}
		if (!(container.getInput().isKeyDown(Input.KEY_UP) || container.getInput().isKeyDown(Input.KEY_LEFT) || container.getInput().isKeyDown(Input.KEY_RIGHT)
				|| container.getInput().isKeyDown(Input.KEY_W)|| container.getInput().isKeyDown(Input.KEY_A)|| container.getInput().isKeyDown(Input.KEY_D))) {
			if (state != UnitState.JUMP && state != UnitState.FALL && state != UnitState.SHOOT ) {
				unit.setUnitState(UnitState.STAND);
			}
		}
		
		if (!(container.getInput().isKeyDown(Input.KEY_UP) || container.getInput().isKeyDown(Input.KEY_LEFT) || container.getInput().isKeyDown(Input.KEY_RIGHT)
				|| container.getInput().isKeyDown(Input.KEY_W)|| container.getInput().isKeyDown(Input.KEY_A)|| container.getInput().isKeyDown(Input.KEY_D))) {
			unit.body.setLinearVelocity(new Vec2 (0, unit.body.getLinearVelocity().y));
		}
		
		// unit.setUnitPosition(x, y);
	}
	
	public double getDeltaMovement() {
		return deltaMovement;
	}
	
	public void resetDeltaMovement() {
		deltaMovement = 0;
	}
	
	
	public BattleMouseClickResult handleMouseClick(GameContainer container, Army currentArmy) {
		int currentUnitNumber = -1;
		boolean mouseClicked = container.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON);
		double mouseX = container.getInput().getMouseX();
		double mouseY = container.getInput().getMouseY();
		boolean unitSelected = false;
		int numOfOverlap = 0;
		if (mouseClicked) {
			for (int i = 0; i < currentArmy.units.size(); i++) {
				//System.out.println("Check Containing Unit");
				Unit unit = currentArmy.units.get(i);
				Rectangle rect = unit.getUnitRectangle();
				//System.out.println(rect.toString());
				if (rect.contains((float)mouseX, (float)mouseY)) {
					//System.out.println("Containing Unit");
					//currentUnitNumber = i;
					unitSelected = true;
					numOfOverlap++;
				}
			}
			if (numOfOverlap <= 1) {
				overLapCount = 0;
			}
			else {
				overLapCount++;
			}
		}
		
		if (mouseClicked) {
			int innerOverlapCount = 0;
			for (int i = 0; i < currentArmy.units.size(); i++) {
				Unit unit = currentArmy.units.get(i);
				Rectangle rect = unit.getUnitRectangle();
				if (rect.contains((float)mouseX, (float)mouseY)) {
					if (overLapCount%numOfOverlap == innerOverlapCount) {
						//System.out.println("Overlap");
						currentUnitNumber = i;
						break;
					}
					innerOverlapCount++;	
				}
			}
		}
		return new BattleMouseClickResult(unitSelected, currentUnitNumber, mouseClicked, mouseX, mouseY);
	}
	
}
