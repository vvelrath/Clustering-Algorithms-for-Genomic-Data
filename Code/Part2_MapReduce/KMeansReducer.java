package com.hadoop.amithana.K_MeansMapReduce;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class KMeansReducer extends
		Reducer<Text, Text, Text, Text> {
	
	public void reduce(Text clusterData, Iterable<Text> dataPoints,
			Context context) throws IOException, InterruptedException {

		StringBuilder centroidStr=new StringBuilder();
		ArrayList<Float> gene = null;
		List<ArrayList<Float>> geneList=new ArrayList<ArrayList<Float>>();
		
		int NUM = 0;
		ArrayList<Float> newCentroid = new ArrayList<Float>();
		ArrayList<Float> oldCentroid = new ArrayList<Float>();
		
		String arr[] = clusterData.toString().split("\t");
				
		//construct old centroid
		for (int i = 0; i < arr.length; i++) {
			oldCentroid.add(Float.parseFloat(arr[i]));
		}		
		
		for (Text point : dataPoints) {
			NUM++;
			String pointStr = point.toString();
			gene = new ArrayList<Float>();
			String cols[] = pointStr.split("\t");

			//construct data point
			for (int i = 0; i < cols.length; i++) {
				gene.add(Float.parseFloat(cols[i]));
			}

			geneList.add(gene);
			
			//initialize new centroid if empty
			if (newCentroid.isEmpty()) {
				for (int k = 0; k < gene.size(); k++)
					newCentroid.add(0.0f);
			}
			float temp;
			
			//construct new centroid co-ordinates
			for (int m = 0; m < gene.size(); m++) {
				temp = newCentroid.get(m) + gene.get(m);
				newCentroid.set(m, temp);
			}

		}
		float temp2;

		for (int i = 0; i < newCentroid.size(); i++) {
			temp2 = newCentroid.get(i) / NUM;
			newCentroid.set(i, temp2);
			
			if(i==gene.size()-1){
			
				centroidStr.append(String.valueOf(temp2)).append("\n");
				}else{
					
					centroidStr.append(String.valueOf(temp2)).append("\t");
				}
		}
		context.write(new Text(centroidStr.toString()), new Text(geneList.toString()));
		
	}

}
