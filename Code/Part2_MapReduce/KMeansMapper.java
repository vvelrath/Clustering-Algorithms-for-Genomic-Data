package com.hadoop.amithana.K_MeansMapReduce;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class KMeansMapper extends Mapper<LongWritable, Text, Text, Text> {

	 List<ArrayList<Float>> genesList = new ArrayList<ArrayList<Float>>();
	 public static List<String> clusterCentroids = new ArrayList<String>();
		  
	 
	@Override
    protected void setup(Context context) throws IOException,
                    InterruptedException {
          
		super.setup(context);
            try {
            	clusterCentroids.clear();
           
            	Configuration conf = context.getConfiguration();
            	Path cachedFile = new Path(conf.get("centroid_path"));            
            	String line = null;
            	FileSystem fs = FileSystem.get(context.getConfiguration());
            	
            	FSDataInputStream fsin = fs.open(cachedFile);
                DataInputStream in = new DataInputStream(fsin);
                BufferedReader bufReader = new BufferedReader(new InputStreamReader(in));
                
    			while ((line = bufReader.readLine()) != null) {
    				clusterCentroids.add(line);
    			}
    				bufReader.close();
    				in.close();
    				
            } catch (FileNotFoundException e) {
    			e.printStackTrace();
    		}
    }
	
	 @Override
	    protected void map(LongWritable key, Text value, Context context)
	                    throws IOException, InterruptedException {
		 List<Double> listOfDist = new ArrayList<Double>();
			HashMap<Double, Integer> distances = new HashMap<Double, Integer>();
						
			String dataPoint=value.toString();
			String cols[] = dataPoint.split("\t");
			ArrayList<Float> gene =new ArrayList<Float>();
			StringBuilder geneStr=new StringBuilder();
			for (int i = 2; i < cols.length; i++) {
				gene.add(Float.parseFloat(cols[i]));
				if(i==cols.length-1){
				
					geneStr.append(cols[i]);
				}else{
				
				geneStr.append(cols[i]).append("\t");
				}
			}
			
				listOfDist.clear();
				distances.clear();
						
			
				ArrayList<Float> clustCentroid=new ArrayList<Float>();
				for (int j = 0; j < clusterCentroids.size(); j++) {
					clustCentroid.clear();
					String arr[] = clusterCentroids.get(j).split("\t");
					
					for (int i = 0; i < arr.length; i++) {
						clustCentroid.add(Float.parseFloat(arr[i]));
					}
					
					double dist = Utilities.getEuclideanDistance(gene,
							clustCentroid);
					distances.put(dist, j);
					listOfDist.add(dist);
				}

				Collections.sort(listOfDist);
				Integer	clusterIndex = distances.get(listOfDist.get(0));
				
				 Text geneWord = new Text();
				 Text clusterWord=new Text();
				 
				 geneWord.set(geneStr.toString());
				
				 clusterWord.set(clusterCentroids.get(clusterIndex));
				
				 context.write(clusterWord,geneWord);
							
			
	 }
		 
	 }
	

