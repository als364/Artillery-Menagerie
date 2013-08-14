package edu.cornell.slicktest;

import java.util.ArrayList;

public class UsefulFunctions {
	public static double doubleMin(ArrayList<Double> dists){
		double min = Double.MAX_VALUE;
		for(double dist :dists){
			min = Math.min(min, dist);
		}
		return min;
	}
}
