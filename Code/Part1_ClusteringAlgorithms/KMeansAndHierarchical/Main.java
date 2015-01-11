package edu.buffalo.cse601;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String filePath=null;
		int noOfClusters=0;
		int iterations=0;
		String rowIDFilePath=null;
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-f")) {
				filePath=args[i+1];
			}else if((args[i].equals("-n"))) {
				noOfClusters=Integer.parseInt(args[i+1]);
				
			}else if((args[i].equals("-r"))) {
				rowIDFilePath=args[i+1];
				
			}
			else if((args[i].equals("-i"))) {
				iterations=Integer.parseInt(args[i+1]);
				
			}

	}
		K_Means kmeans=new K_Means(filePath,noOfClusters,rowIDFilePath,iterations);
		kmeans.displayResults();
		
		Hierarchical_Agglo hier_agglo = new Hierarchical_Agglo(filePath,noOfClusters);
		hier_agglo.displayResults();
	}	
}
