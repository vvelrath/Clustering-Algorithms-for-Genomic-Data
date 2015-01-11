package edu.buffalo.cse601;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

public class K_Means {

	BufferedReader bufReader;
	String fName = null;
	String rowIDFilePath=null;
	int noOfClusters;
	List<ArrayList<Float>> genesList = new ArrayList<ArrayList<Float>>();
	List<ArrayList<Float>> clusterCentroids = new ArrayList<ArrayList<Float>>();
	HashMap<ArrayList<Float>, ArrayList<ArrayList<Float>>> centroidsAndPoints = new HashMap<ArrayList<Float>, ArrayList<ArrayList<Float>>>();
	HashMap<ArrayList<Float>, Integer> clusterId=new HashMap<ArrayList<Float>, Integer>();
	List<ArrayList<Float>> oldCentroids;
	int MAX_ITERATIONS;
TreeMap<Integer, ArrayList<Float>> labels=new TreeMap<Integer,ArrayList<Float>>();
	int iterations;
	public int count = 0;
	ArrayList<Integer> groundTruth=new ArrayList<Integer>();
	ArrayList<Integer> clusterResults=new ArrayList<Integer>();

	public K_Means(String filePath, int noOfClusters,String rowIDFilePath,int iterations) {
		fName = filePath;
		if(rowIDFilePath!=null){
		this.rowIDFilePath=rowIDFilePath;
		}
		if(iterations!=0){
			MAX_ITERATIONS=iterations;
		}
		else{
			MAX_ITERATIONS=50;
		}
		this.noOfClusters = noOfClusters;
		try {
			bufReader = new BufferedReader(new FileReader(fName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	void populateDataStrucs() {
		String line = null;
		try {
			ArrayList<Float> gene = null;
			while ((line = bufReader.readLine()) != null) {
				count++;
				gene = new ArrayList<Float>();
				String cols[] = line.split("\t");

				groundTruth.add(Integer.parseInt(cols[1]));
				
				for (int i = 2; i < cols.length; i++) {
					gene.add(Float.parseFloat(cols[i]));
				}
				genesList.add(gene);
			}
		
			bufReader.close();
			// generate random cluster centroids
			if(rowIDFilePath==null){
			Random r = new Random();
			int i1;
			for (int j = 0; clusterCentroids.size() < noOfClusters; j++) {
				i1 = r.nextInt(count);
				if (!clusterCentroids.contains(genesList.get(i1))) {
					clusterCentroids.add(genesList.get(i1));
				}
			}
			}else{
				String n=null;
				BufferedReader br=new BufferedReader(new FileReader(rowIDFilePath));
				while ((n = br.readLine()) != null) {
					clusterCentroids.add(genesList.get(Integer.parseInt(n)));
				}
				br.close();
				
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private void updateClusters() {
		List<Double> listOfDist = new ArrayList<Double>();
		HashMap<Double, Integer> distances = new HashMap<Double, Integer>();
		centroidsAndPoints.clear();

		for (int i = 0; i < genesList.size(); i++) {
			listOfDist.clear();
			distances.clear();
			for (int j = 0; j < noOfClusters; j++) {
				double dist = Utilities.getEuclideanDistance(genesList.get(i),
						clusterCentroids.get(j));
				distances.put(dist, j);
				listOfDist.add(dist);
			}

			Collections.sort(listOfDist);
			// get the index of the centroid with min distance
			int temp;
			temp = distances.get(listOfDist.get(0));
			labels.put(i, clusterCentroids.get(temp));
		
			if (!centroidsAndPoints.containsKey(clusterCentroids.get(temp))) {
				ArrayList<ArrayList<Float>> entities = new ArrayList<ArrayList<Float>>();
				entities.add(genesList.get(i));
				centroidsAndPoints.put(clusterCentroids.get(temp), entities);
			} else {
				ArrayList<ArrayList<Float>> entities = centroidsAndPoints
						.get(clusterCentroids.get(temp));
				entities.add(genesList.get(i));
				centroidsAndPoints.put(clusterCentroids.get(temp), entities);
			}
		}
	}

	void ClusterData() {
		populateDataStrucs();
		updateClusters();

		 iterations = 0;
		 while (!checkTermination(oldCentroids, iterations))
	 {
			// make a copy of old centroids
			oldCentroids = new ArrayList<ArrayList<Float>>(clusterCentroids);
			// get new centroids
			updateCentroids();
			updateClusters();

			iterations++;

		} 

	}
	
	boolean checkTermination(List<ArrayList<Float>> oldCentroids,int iterations){
		
		if(iterations>MAX_ITERATIONS)
			return true;
		
		List<ArrayList<Float>> list1=new ArrayList<ArrayList<Float>>(clusterCentroids);
		if(!(oldCentroids==null)){
		list1.removeAll(oldCentroids);
		if(list1.isEmpty())
		return true;
		return false;
		}
		else return false;
		
	}

	
	private void updateCentroids() {
		clusterCentroids.clear();
		
		for (ArrayList<Float> key : centroidsAndPoints.keySet()) {
			ArrayList<Float> clusterCenter=new ArrayList<Float>();
			float val;
			ArrayList<ArrayList<Float>> points=centroidsAndPoints.get(key);
			for(int j=0;j<points.get(0).size();j++){
				val=0;
			for(int i=0;i<points.size();i++){
				val+=points.get(i).get(j);
					
				}
			clusterCenter.add(val/points.size());
			}
			clusterCentroids.add(clusterCenter);
		}

	}
	
	private void assignClusterID(){
		ArrayList<Float> val;
		int i=1;
		for(int key:labels.keySet()){
			val=labels.get(key);
			if(!clusterId.containsKey(val)){
				clusterId.put(val, i);
				i++;
			}
		}
	}
	
	public void displayResults(){
		ClusterData();
		assignClusterID();
		System.out.println(iterations);
		Writer w;
		try {
			w = new FileWriter("C:/Users/Neeti/Desktop/3rd sem/Data Mining/project 2/ClusterResult_kmeans.txt");
			for(int key: labels.keySet()){
				clusterResults.add(clusterId.get(labels.get(key)));
				w.write(String.valueOf(clusterId.get(labels.get(key))));
				w.write("\r\n");
				System.out.println(key+" : "+clusterId.get(labels.get(key)));
			}
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ExternalValidation extValidation=new ExternalValidation(count,groundTruth,clusterResults);

		float res=extValidation.getCo_efficient();
		System.out.println("Rand Index------------"+res);

		float jaccard=extValidation.get_Jaccard_coEff();
		System.out.println("Jaccard co-efficient------------"+jaccard);
		
		InternalIndex internalInd = new InternalIndex(fName, clusterResults);
		System.out.println("Silihouette Coefficient------------"+internalInd.calculateSilhouetteCoefficient());
	
	}

}
