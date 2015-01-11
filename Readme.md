Clustering Algorithms for Genomic Data
=======================

### Team members

Vivekanandh Vel Rathinam (vvelrath@buffalo.edu), Amitha Narasimha Murthy (amithana@buffalo.edu), 
Neeti Narayan (neetinar@buffalo.edu)

### Description

The objective of this project is to implement three clustering algorithms: K-means, Hierarchical Agglomerative with Single Link (Min), and density-based clustering. For each clustering algorithm, we validate our clustering results with
• External index (Rand Index, Jaccard Coefficient)
• Internal index (Silhouette)
• Visualize using PCA (Principal Component Analysis)
We have set up a single-node Hadoop cluster for implementing MapReduce K-means.

### Accuracy and Result analysis

• K-Means
	
	Accuracy for cho.txt:
	External index (using Rand Index): 80.74%
	External index (using Jaccard Coefficient): 39.28%
	Internal index (using Silhouette): 24.02%
	
	Accuracy for iyer.txt:
	External index (using Rand Index): 78.72%
	External index (using Jaccard Coefficient): 29.47%
	Internal index (using Silhouette): 25.59 %
	
	We observed that initializing with different centroids would give us different results. This is because K means algorithm is inherently sensitive to initial centroids. But we also observed that there is no definite formula for a good result with respect to initial centroids.
	
• Hierarchical Agglomerative (with Single Link – Min)

	Accuracy for cho.txt:
	External index (using Rand Index): 24.02%
	External index (using Jaccard Coefficient): 22.83%
	Internal index (using Silhouette): 24.27%
	
	Accuracy for iyer.txt:
	External index (using Rand Index): 18.82%
	External index (using Jaccard Coefficient): 15.82%
	Internal index (using Silhouette): 62.92%
		
	With single link (Min), we find two “closest” clusters to merge in each step. For cho.txt, 382 data points are assigned to one cluster, remaining 4 data points form 4 individual clusters. This is the effect of using MIN as the approach to define cluster distance. On changing to MAX, we obtained better results.
	Also, we performed normalization on the data. But, it did not make any significant difference. Non-outliers and outliers are not distinguished when applying the clustering algorithm. All records are considered as our input.
	
• DBSCAN Clustering

	Accuracy for cho.txt:
	External index (using Rand Index): 70.15%
	External index (using Jaccard Coefficient): 28.27 %
	Internal index (using Silhouette): 5.79%
	
	Accuracy for iyer.txt:
	External index (using Rand Index): 68.12%
	External index (using Jaccard Coefficient): 29.02%
	Internal index (using Silhouette): 40.84%
	
	Epsilon of 3 gives very good performance for clustering the data set. We tried and tested our algorithm with many epsilon values. Epsilon values lesser than 2 and greater than 4 resulted in 30 or more clusters and 2 clusters respectively which is bad.
	
	The number of Minimum points and the Epsilon values are proportional to each other as we can see from the table i.e. Clusters cannot be formed with more points and lesser diameter (epsilon)
	
• K-Means using Map-Reduce

	Accuracy for cho.txt:
	External index (using Rand Index): 80.82%
	External index (using Jaccard Coefficient): 43.00%
	Internal index (using Silhouette): 24.23%

	Accuracy for iyer.txt:
	External index (using Rand Index): 73.02%
	External index (using Jaccard Coefficient): 32.99%
	Internal index (using Silhouette): 48.03%

	We observed that the results of the non-parallel K-Means and parallel K-Means are very similar. However, for these particular datasets, MapReduce implementation of K -Means took longer time than the non-parallel K-means which leads to our conclusion that MapReduce is efficient only for large datasets.