package edu.cornell.slicktest;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

public class PhysicsUnit extends Body {

	public PhysicsUnit(BodyDef def, World world){
		super(def, world);
	}	
}
