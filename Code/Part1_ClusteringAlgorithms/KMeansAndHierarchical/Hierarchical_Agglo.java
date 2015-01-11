package edu.buffalo.cse601;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hierarchical_Agglo {
	static int k;
	static ArrayList<Integer> groundTruth = new ArrayList<Integer>();
	static String fName = null;
	
	public Hierarchical_Agglo(String filePath, int noOfClusters) {
		fName = filePath;
		k = noOfClusters;
	}

	public static void displayResults() {
		ArrayList<ArrayList<Float>> all_rows = readFromFile();
		double[][] dis_matrix = distanceMatrix(all_rows);
		int[] close_cluster = findCloseCluster(dis_matrix);
		System.out.println("Order in which clusters are merged:");
		mergeClusters(dis_matrix, close_cluster);
	}

	//get each gene from the file
	static ArrayList<ArrayList<Float>> readFromFile() {
		ArrayList<ArrayList<Float>> all_rows = new ArrayList<ArrayList<Float>>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fName));
			String line = reader.readLine();
			ArrayList<Float> gene = null;
			
			while(line!=null) {
				String row[] = line.split("\t");
				groundTruth.add(Integer.parseInt(row[1]));
				gene = new ArrayList<Float>();
				for(String data:row)
					gene.add(Float.valueOf(data));
				all_rows.add(gene);
				line = reader.readLine();
			}
			
			/*
			//normalize data
			while(line!=null) {
				String row[] = line.split("\t");
				groundTruth.add(Integer.parseInt(row[1]));
				gene = new ArrayList<Float>();
				float[] features = new float[row.length];
				float sum = 0;
				float p = 0;
				for(int i=0;i<row.length;i++) {
					features[i] = Float.valueOf(row[i]);
					sum = sum + features[i];
				}
				float avg = sum/Float.valueOf(features.length);
				for(int i=0;i<row.length;i++) {
					p = p + (avg - features[i]);
					p = (float) Math.pow(p, 2);
				}
				float var = p/Float.valueOf(features.length);
				for(int i=0;i<row.length;i++) {
					features[i] = (features[i] - avg)/var;
					gene.add(features[i]);
				}
				all_rows.add(gene);
				line = reader.readLine();
			}
			*/
			
		}
		catch(IOException e) {
			
		}
		return all_rows;
	}
	
	//each data point be a cluster
	static double[][] distanceMatrix(ArrayList<ArrayList<Float>> all_rows) {
		int n = all_rows.size();
		//compute the distance matrix
		double[][] dis_matrix = new double[n][n];
		for(int i=0;i<n;i++) {
			for(int j=0;j<n;j++) {
				if(i == j) 
					dis_matrix[i][j] = -1;
				else 
					dis_matrix[i][j] = findDistance(all_rows.get(i),all_rows.get(j));
			}
		}
		//System.out.println("initial " + dis_matrix[80][87]);
		return dis_matrix;
	}
	
	//find the nearest cluster for each data point
	private static int[] findCloseCluster(double[][] dis_matrix) {
		int n = dis_matrix.length;
		//for cluster i, min_dis[i] indicates the closest cluster
		int[] close_cluster = new int[n];
		close_cluster[0] = 1;
		for(int i=0;i<n;i++) {
			for(int j=0;j<n;j++) {
				if(i != j) {
					if(dis_matrix[i][j] < dis_matrix[i][close_cluster[i]]) 
						close_cluster[i] = j;
				}
			}
		}
		return close_cluster;
	}
	
	//compute the euclidean distance between two clusters
	static double findDistance(ArrayList<Float> gene1, ArrayList<Float> gene2) {
		int n = gene1.size();
		//Float dis_sum = new Float("0");
		double dis_sum = 0.0;
		for(int i=2;i<n;i++) {
			double diff = gene1.get(i) - gene2.get(i);
			dis_sum = dis_sum + (diff*diff);
		}
		double euclidean_distance = Math.sqrt(dis_sum);
		return  euclidean_distance;
	}
	
	//merge 2 closest clusters and update distance matrix
	static void mergeClusters(double[][] dis_matrix, int[] close_cluster) {		
		
		int n = dis_matrix.length;
		int[][] cluster_matrix = new int[n][n];
		ArrayList<ArrayList<Integer>> bigList = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> orderOfClustering = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> kclusters = new ArrayList<ArrayList<Integer>>();
		
		for(int i=0;i<n;i++) {
			ArrayList<Integer> innerList = new ArrayList<Integer>();
			innerList.add(i);
			bigList.add(innerList);
			ArrayList<Integer> arr = new ArrayList<Integer>();
			arr.add(0);
			orderOfClustering.add(arr);
		}
		
		for(int i=0;i<n;i++) {
			int counter = 0;
			
			//count the number of cluster formed
			for(int j=0;j<n;j++) {
				if(bigList.get(j).contains(-1)) 
					counter++;
			}
			
			if(counter >= n - k) 
				break;
			
			//find the closest cluster pair
			int row_index1 = 0, row_index2 = 0;
			for(int j = 0; j < n; j++) {
				if (dis_matrix[j][close_cluster[j]] < dis_matrix[row_index1][close_cluster[row_index1]]) 
					if(dis_matrix[j][close_cluster[j]] != -1)
						row_index1 = j;
			}
				
			row_index2 = close_cluster[row_index1];
			
			//store the order of clustering
			bigList.get(row_index1).addAll(bigList.get(row_index2));
			bigList.get(row_index2).add(-1);
			orderOfClustering.set(i, bigList.get(row_index1));
			System.out.println(orderOfClustering.get(i));
			
			//update the distance matrix
			for(int j = 0; j < n; j++) { 
				if ((dis_matrix[row_index2][j] < dis_matrix[row_index1][j]))
					if((dis_matrix[row_index2][j] != -1))
						dis_matrix[row_index1][j] = dis_matrix[row_index2][j];
			}
			
			for(int j=0;j<n;j++) {
				dis_matrix[j][row_index1] = dis_matrix[row_index1][j];
			}
			
			for (int j = 0; j < n; j++) {
				dis_matrix[row_index2][j] = -1;
				dis_matrix[j][row_index2] = -1;
			}
			
			close_cluster[row_index1]  = 0;
			if(row_index1 == 0) 
				close_cluster[row_index1] = 1;
			for (int j = 0; j < n; j++) {
				if (close_cluster[j] == row_index2) 
					close_cluster[j] = row_index1;
				while(dis_matrix[row_index1][close_cluster[row_index1]] == -1) {
					if(close_cluster[row_index1] < n-1)
						close_cluster[row_index1] = close_cluster[row_index1] + 1;
					else
						close_cluster[row_index1] = 0;
				}
				if (dis_matrix[row_index1][j] < dis_matrix[row_index1][close_cluster[row_index1]])
					if(dis_matrix[row_index1][j]!=-1)
						close_cluster[row_index1] = j;
			}			
		}
		
		ArrayList<Integer> clusterResult = new ArrayList<Integer>(n);
		for (int i = 0; i < n; i++) {
			clusterResult.add(0);
		}
		ArrayList<ArrayList<Integer>> clusters = new ArrayList<ArrayList<Integer>>();
		
		
		//each cluster is an arraylist
		for(int i=0;i<n;i++) {
			if(!bigList.get(i).contains(-1)) {
				ArrayList<Integer> each_cluster = new ArrayList<Integer>(bigList.get(i));
				clusters.add(each_cluster);				
			}
		}
		
		try {
			//write the clusterResult to a txt file - for PCA
			Writer w = new FileWriter("C:/Users/Neeti/Desktop/3rd sem/Data Mining/project 2/ClusterResult_hier.txt");
			//format input for RandIndex
			for(int i=0;i<clusters.size();i++) {
				ArrayList<Integer> each_cluster = clusters.get(i);
				for(int j=0;j<each_cluster.size();j++) {
					clusterResult.set(each_cluster.get(j), i+1);
					w.write(String.valueOf(i+1));
					w.write("\r\n");
				}
			}
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
				
				
		for(int i=n-k+1;i<n;i++) {
			//find the closest cluster pair
			int row_index1 = 0, row_index2;
			for(int j = 0; j < n; j++) {
				if (dis_matrix[j][close_cluster[j]] < dis_matrix[row_index1][close_cluster[row_index1]]) 
					if(dis_matrix[j][close_cluster[j]] != -1)
						row_index1 = j;
			}
			row_index2 = close_cluster[row_index1];
			
			//store the order of clustering
			bigList.get(row_index1).addAll(bigList.get(row_index2));
			bigList.get(row_index2).add(-1);
			orderOfClustering.set(i, bigList.get(row_index1));
			System.out.println(orderOfClustering.get(i));
			
			if(i == n-1) 
				break;
			
			//update the distance matrix
			for(int j = 0; j < n; j++) { 
				if ((dis_matrix[row_index2][j] < dis_matrix[row_index1][j]) && (dis_matrix[row_index2][j] != -1)) {
					dis_matrix[row_index1][j] = dis_matrix[row_index2][j];
				}
			}
			
			for(int j=0;j<n;j++) {
				dis_matrix[j][row_index1] = dis_matrix[row_index1][j];
			}
			
			for (int j = 0; j < n; j++) {
				dis_matrix[row_index2][j] = -1;
				dis_matrix[j][row_index2] = -1;
			}
			
			close_cluster[row_index1]  = 0;
			if(row_index1 == 0) 
				close_cluster[row_index1] = 1;
			for (int j = 0; j < n; j++) {
				if (close_cluster[j] == row_index2) 
					close_cluster[j] = row_index1;
				while(dis_matrix[row_index1][close_cluster[row_index1]] == -1) {
					if(close_cluster[row_index1] < n-1)
						close_cluster[row_index1] = close_cluster[row_index1] + 1;
					else
						close_cluster[row_index1] = 0;
				}
				if (dis_matrix[row_index1][j] < dis_matrix[row_index1][close_cluster[row_index1]])
					if(dis_matrix[row_index1][j]!=-1)
						close_cluster[row_index1] = j;
			}	
		}
		
		System.out.println("\n" + k + " clusters:");
		//k individual clusters and size
		for(int i=0;i<clusters.size();i++) {
			System.out.print(clusters.get(i).size() + ": ");
			System.out.println(clusters.get(i));
		}

		//external index: Rand Index and Jaccard Coefficient
		ExternalValidation extValidation=new ExternalValidation(n,groundTruth,clusterResult);

		float res=extValidation.getCo_efficient();
		System.out.println("Rand Index------------"+res);

		float jaccard=extValidation.get_Jaccard_coEff();
		System.out.println("Jaccard co-efficient------------"+jaccard);
		
		InternalIndex internalInd = new InternalIndex(fName, clusterResult);
		System.out.println("Silihouette Coefficient------------"+internalInd.calculateSilhouetteCoefficient());
	}
}
