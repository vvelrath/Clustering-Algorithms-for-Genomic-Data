package com.hadoop.amithana.K_MeansMapReduce;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class KMeansDriver {
	static int MAX_ITERATIONS=100;
	static String fname="cho.txt";
	static String centroidsFname="/centroids_For_cho.txt";
	static boolean isCho=true;
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException,
			InterruptedException, ClassNotFoundException {

		Path inputPath = new Path(args[0]);
		if(args.length==3){
		isCho=Boolean.parseBoolean(args[2]);
		}
		
		int iter = 0;
		
		if(!isCho){
			centroidsFname="/centroids_For_iyer.txt";
			fname="iyer.txt";
		}

		Path outPath = null;
		
		boolean converged=false;
		while (!converged) {

			Configuration conf = new Configuration();
			 
			ArrayList<String> oldCentroidStr = new ArrayList<String>();
			ArrayList<String> newCentroidStr = new ArrayList<String>();
			FileSystem hdfs = FileSystem.get(conf);
			try {
				if (iter == 0) {
					
					Path centroidPath = new Path("/user/hduser/input/kmeans/centroids" + centroidsFname);
					conf.set("centroid_path", centroidPath.toString());
			
				} else {

					Path newCentroidFile = new Path(args[1] + "_" + (iter - 1)
							+ "/" + "part-r-00000");

					FSDataInputStream fsin = hdfs.open(newCentroidFile);
					DataInputStream in = new DataInputStream(fsin);

					Path newCentWritePath = new Path(
							"/user/hduser/input/kmeans/newCentroids/newCentroids_cho.txt");
					conf.set("centroid_path", newCentWritePath.toString());
					
					if (hdfs.exists(newCentWritePath))
						hdfs.delete(newCentWritePath, true);

					FSDataOutputStream out = hdfs.create(newCentWritePath);
					BufferedWriter bufWriter = new BufferedWriter(
							new OutputStreamWriter(out));

					BufferedReader bufReader = new BufferedReader(
							new InputStreamReader(in));
					String line = null;
					while ((line = bufReader.readLine()) != null) {
				
						bufWriter.write(line + "\n");
						oldCentroidStr.add(line);
						bufReader.readLine();
					}
					bufWriter.flush();
					bufWriter.close();
					bufReader.close();
					in.close();
					fsin.close();
					out.close();
					
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			outPath = new Path(args[1] + "_" + iter);
			// Create job
			Job job = new Job(conf, "K-Means");
			job.setJarByClass(KMeansDriver.class);

			// Setup MapReduce
			job.setMapperClass(KMeansMapper.class);
			job.setReducerClass(KMeansReducer.class);

			if (hdfs.exists(outPath))
				hdfs.delete(outPath, true);
hdfs.close();
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);

			// Specify key / value
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);

			// Input
			FileInputFormat.addInputPath(job, inputPath);
			job.setInputFormatClass(TextInputFormat.class);

			// Output
			FileOutputFormat.setOutputPath(job, outPath);
			job.setOutputFormatClass(TextOutputFormat.class);

			// Execute job
			job.waitForCompletion(true);
				
			Path newCentroidPath = new Path(
					args[1] + "_" + (iter)
					+ "/" + "part-r-00000");
			
			FileSystem fs=FileSystem.get(conf);
			FSDataInputStream fsinreader = fs.open(newCentroidPath);
			DataInputStream in = new DataInputStream(fsinreader);

			BufferedReader bufReader = new BufferedReader(
					new InputStreamReader(in));
			String line = null;
			while ((line = bufReader.readLine()) != null) {
				newCentroidStr.add(line);
				bufReader.readLine();
			}
			bufReader.close();
			in.close();
			fsinreader.close();
			fs.close();
			
			 converged= checkTermination(oldCentroidStr,newCentroidStr,iter);
			System.out.println("Iteration== " + iter);
			iter++;
		}
		System.out.println("-----------------Final Iterations-----= " + iter);
		
		Path result = new Path(args[1] + "_" +(iter - 1) + "/"+"part-r-00000");
		
		postProcess(result);
		
		
	}
	
	private static void postProcess(Path result) {
		Configuration conf=new Configuration();
	
		FSDataInputStream fsinreader;
		try {
			FileSystem fs=FileSystem.get(conf);
			fsinreader = fs.open(result);
			DataInputStream in = new DataInputStream(fsinreader);

			BufferedReader localBuf = new BufferedReader(new FileReader("/home/amitha/Desktop"+"/"+fname));
			
			BufferedReader bufReader = new BufferedReader(
					new InputStreamReader(in));
			String line = null;
			HashMap<String,Integer> clusterIDMap=new HashMap<String,Integer>();
			int i=1;
			while ((line = bufReader.readLine()) != null) {
				clusterIDMap.put(bufReader.readLine(),i);	
				i++;
			}
			bufReader.close();
			//process input file
			 BufferedWriter bWriter=new BufferedWriter(new FileWriter("/home/amitha/Desktop/iyerArrayList.txt"));
			 
			ArrayList<Integer> groundTruth=new ArrayList<Integer>();
			ArrayList<Integer> clusterResults=new ArrayList<Integer>();
			ArrayList<Float> gene = null;int count=0;
			while ((line = localBuf.readLine()) != null) {
				count++;
				gene = new ArrayList<Float>();
				String cols[] = line.split("\t");

				groundTruth.add(Integer.parseInt(cols[1]));
				
				for (int j = 2; j < cols.length; j++) {
					gene.add(Float.parseFloat(cols[j]));
				}
				
				for(String key:clusterIDMap.keySet()){
					if(key.contains(gene.toString())){
						clusterResults.add(clusterIDMap.get(key));
						 bWriter.write(clusterIDMap.get(key)+"\n");
						 break;
						}
					
				}
			}
			 bWriter.close();
					
			ExternalValidation rand=new	ExternalValidation(count,groundTruth,clusterResults);
			float res=rand.getCo_efficient();
			System.out.println("Rand Index----------------"+res);
			float jaccard=rand.get_Jaccard_coEff();
			System.out.println("Jaccard co-efficient------------"+jaccard);
			InternalIndex internalInd = new InternalIndex("/home/amitha/Desktop"+"/"+fname, clusterResults);
			System.out.println("Silhouette Coefficient-------"+(internalInd.calculateSilhouetteCoefficient())*100);
			
			localBuf.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	static boolean  checkTermination(ArrayList<String> oldCentroids,ArrayList<String> newCentroids,int iterations){
		
		if(oldCentroids==null)
			return false;
		
		if(iterations>MAX_ITERATIONS)
			return true;
		Collections.sort(oldCentroids);
		Collections.sort(newCentroids);
		return oldCentroids.equals(newCentroids);
				
	}

}
