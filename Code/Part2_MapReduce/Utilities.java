package com.hadoop.amithana.K_MeansMapReduce;

import java.util.ArrayList;

public class Utilities {
	
	static double getEuclideanDistance(ArrayList<Float> point1,ArrayList<Float> point2){
		double dist=0;
		for(int i=0;i<point1.size();i++){
			double diffSquare=Math.pow((point1.get(i)-point2.get(i)),2);
			dist+=diffSquare;
		}
		return Math.sqrt(dist);
		
	}

}