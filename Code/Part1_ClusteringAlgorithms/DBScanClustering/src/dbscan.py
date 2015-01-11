# To change this license header, choose License Headers in Project Properties.
# To change this template file, choose Tools | Templates
# and open the template in the editor.
import csv
import math
import RandIndex
import InternalIndex
import numpy as np
import sys
import os

dist_list = []
nextClusterNo = 0

#Parsing Arguments
len_of_argv = len(sys.argv)
for a in range(len_of_argv):
    if sys.argv[a] == '-f':
        fileName = str(sys.argv[a + 1])
    elif sys.argv[a] == '-e':    
        eps = float(sys.argv[a + 1])
    elif sys.argv[a] == '-m':
        minpts = int(sys.argv[a + 1])
    else:
        continue



#Function for computing the euclidean distance
def computeEuclideanDistance(gene1, gene2):
    noofattrs = len(gene1)
    dist = 0.0
    for i in range(noofattrs):
        dist = dist + math.pow((float(gene1[i]) - float(gene2[i])),2)
    return math.sqrt(dist)    

#Function for finding the neighbouring points within eps
def regionQuery(geneID, dist_thresh):
    global num_genes
    global dist_list
    a = []
    for i in range(num_genes):
        if i != geneID and dist_list[geneID][i] < dist_thresh:
          a.append([i,float(dist_thresh/2.0)])
    return a

#Function for computing the pairwise distances between all the genes
def computeDistanceMatrix():
    global dist_list
    global expr_values
    global num_genes
    
    dist_list = [[0 for j in range(num_genes)] for i in range(num_genes)]
    
    for i in range(num_genes):
        for j in range(num_genes):
            gene1_expr_values = expr_values[i]
            gene2_expr_values = expr_values[j]
            dist_list[i][j] = computeEuclideanDistance(gene1_expr_values[2:], gene2_expr_values[2:])
    

#Function for expanding the cluster
def expandCluster(geneID, neighbours):
    global nextClusterNo
    nextClusterNo = nextClusterNo + 1        
    cluster_list[geneID] = nextClusterNo
    
    for k in range(len(neighbours)):
        if visited_list[neighbours[k][0]] == 0:
            visited_list[neighbours[k][0]] = 1
            nebr_of_nebr = regionQuery(neighbours[k][0],neighbours[k][1])
            if len(nebr_of_nebr) >= minpts:
                neighbours.extend(nebr_of_nebr)
                
        if cluster_list[neighbours[k][0]] == 0:
            cluster_list[neighbours[k][0]] = nextClusterNo
    
#Main code starts    
with open(os.path.join(fileName)) as file:
    reader = csv.reader(file, delimiter="\t")
    expr_values = list(reader)
    num_genes = len(expr_values)

#Initialized the visited list and cluster list
visited_list = [0 for j in range(num_genes)]
cluster_list = [0 for j in range(num_genes)]

#Computing the pairwise distances
computeDistanceMatrix()

#Iterating through all the unvisted points
for i in range(num_genes):
    if visited_list[i] == 0:
        visited_list[i] = 1
        neighbours = regionQuery(i,eps)
        if len(neighbours) < minpts:
            cluster_list[i] = -1
        else:
            expandCluster(i, neighbours)
            
#Computing the accuracy
match_clusters = 0
for i in range(num_genes):
    if cluster_list[i] == int(expr_values[i][1]):
        match_clusters = match_clusters + 1
accuracy = float(match_clusters)/float(num_genes) * 100

#Computing the rand index
rand = RandIndex.getRandIndex(num_genes, cluster_list, expr_values)

#Computing the Jaccard Coefficient
jaccard = RandIndex.getJaccardCoefficient();

#Saving the cluster results in a file for PCA
np.savetxt(fileName.split('.')[0]+'_dbscan_results.txt', cluster_list, fmt='%d')

#Finding the silihouette coefficient(Internal Index)
silcoefficient = InternalIndex.getSilhouetteCoefficient(num_genes, nextClusterNo, cluster_list, dist_list)

print "File:", fileName
print "Number of Clusters:", nextClusterNo
print "Rand Index:", rand
print "Jaccard Coefficient:", jaccard
print "Silihouette Coefficient:", silcoefficient