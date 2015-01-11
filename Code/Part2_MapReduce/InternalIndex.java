package com.hadoop.amithana.K_MeansMapReduce;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InternalIndex {
	
	String fileName = null;
	List<Integer> clusterResults = null;
	BufferedReader bufReader = null;
	int num_genes = 0;
	double[][] distance_matrix = null;
	Map<Integer, ArrayList<Integer>> clusterMap = null;
	
	//Constructor for initializing the distance matrix
	public InternalIndex(String fileName, ArrayList<Integer> clusterResults){
		this.fileName = fileName;
		this.clusterResults = clusterResults;
		this.clusterMap = new HashMap<Integer, ArrayList<Integer>>(); 
		
		try {
			bufReader = new BufferedReader(new FileReader(fileName));
			
			//Finding the number of genes in a file to initializing the distance matrix
			while (bufReader.readLine() != null) num_genes++;
			distance_matrix = new double[num_genes][num_genes];
			
			bufReader = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Populating the distance matrix
	public void populateDistanceMatrix(){
		
		ArrayList<ArrayList<Float>> genesList = new ArrayList<ArrayList<Float>>(num_genes);
		
		String line = null;
		
		//Reading the file and populating the data structure
		try {
			ArrayList<Float> gene = null;
			while ((line = bufReader.readLine()) != null) {
				gene = new ArrayList<Float>();
				String cols[] = line.split("\t");
				
				for (int i = 2; i < cols.length; i++) {
					gene.add(Float.parseFloat(cols[i]));
				}
				genesList.add(gene);
			}
		
			bufReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(int i = 0; i < num_genes; i++){
			ArrayList<Float> gene1 = genesList.get(i);
			for(int j = 0; j < num_genes; j++){
				ArrayList<Float> gene2 = genesList.get(j);
				distance_matrix[i][j] = Utilities.getEuclideanDistance(gene1, gene2);
			}
		}
	}
	
	//Initializing the map with clusters
	public void initializeMapWithClusters(){
		for(int i = 0;i < clusterResults.size(); i++){
			ArrayList<Integer> genesInCluster = null;
			int cluster = clusterResults.get(i);
			
			if(cluster == -1)
				continue;
			
			if(clusterMap.get(cluster) == null){
				genesInCluster = new ArrayList<Integer>();
			}else{
				genesInCluster = clusterMap.get(cluster);
			}
			
			genesInCluster.add(i);
			
			clusterMap.put(cluster, genesInCluster);
		}
	}
	
	//Calculate the silhouette coefficient
	public double calculateSilhouetteCoefficient(){
		
		double S_All_genes = 0;
		double B_gene_cluster = 0;
		double A_gene = 0;
		double B_gene = 0;
		int cluster = 0;
		ArrayList<Integer> genesInCluster = null;
		
		//Populating the distance matrix
		populateDistanceMatrix();
		
		//Populate the map with clusters
		initializeMapWithClusters();
		
		//Iterating through the each of the gene to find A(gene) and B(gene)
		for(int i = 0;i < clusterResults.size(); i++){
			cluster = clusterResults.get(i);
			
			if(cluster == -1) continue;
				
			//Calculating A_gene
			A_gene = 0;
			genesInCluster = clusterMap.get(cluster);

			for(int j = 0;j < genesInCluster.size(); j++){
				int geneID = genesInCluster.get(j);
				A_gene += distance_matrix[i][geneID];
			}
			if(genesInCluster.size() > 1)
				A_gene = A_gene / (genesInCluster.size() - 1);
			
			//Calculating B_gene
			B_gene = 0;
			Set<Integer> clusterKeys = clusterMap.keySet();
			Iterator<Integer> clusterKeys_irtr = clusterKeys.iterator();
			
			while(clusterKeys_irtr.hasNext()){
				int other_cluster = clusterKeys_irtr.next();
				B_gene_cluster = 0;
				
				//B_gene is for calculating the minimum distance with other clusters
				if(other_cluster == cluster) continue;
				
				ArrayList<Integer> genesInOtherCluster = clusterMap.get(other_cluster);
				for(int j = 0;j < genesInOtherCluster.size(); j++){
					int geneID = genesInOtherCluster.get(j);
					B_gene_cluster += distance_matrix[i][geneID];
				}
				B_gene_cluster = B_gene_cluster / genesInOtherCluster.size();

				if(B_gene == 0)
					B_gene = B_gene_cluster;
				else
					B_gene = Math.min(B_gene, B_gene_cluster);
			}
			
			//Calculating S_gene
			double S_gene = (B_gene - A_gene)/Math.max(A_gene, B_gene);
			S_All_genes = S_All_genes + S_gene;
			//System.out.println(genesInCluster.size()+" "+A_gene+" "+B_gene+" "+S_All_genes + " "+ num_genes);
		}
		return S_All_genes/num_genes;
	}
}
