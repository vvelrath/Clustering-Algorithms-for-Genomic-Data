# To change this license header, choose License Headers in Project Properties.
# To change this template file, choose Tools | Templates
# and open the template in the editor.
import numpy as np

def getSilhouetteCoefficient(num_genes, num_clusters, cluster_list, dist_list):
    S_gene = [0 for i in range(num_genes)]
    for i in range(num_genes):
        A_gene = 0
        B_gene = 0
        cluster_i = cluster_list[i]
        cumulated_dist = [0 for j in range(num_clusters)]
        noofpeers_incluster = [0 for j in range(num_clusters)]
        
        if cluster_i == -1:
            continue
        
        for j in range(num_genes):
            cluster_j = cluster_list[j]
            if cluster_j == -1:
                continue
            cumulated_dist[cluster_j - 1] = cumulated_dist[cluster_j - 1] + dist_list[i][j]
            noofpeers_incluster[cluster_j - 1] = noofpeers_incluster[cluster_j - 1] + 1
        
        #Setting A_gene
        if noofpeers_incluster[cluster_i - 1] > 1:
            A_gene = float(cumulated_dist[cluster_i - 1])/float(noofpeers_incluster[cluster_i - 1] - 1)
        cumulated_dist[cluster_i - 1] = 1000
        
        #Setting B_gene
        for j in range(num_clusters):
            cumulated_dist[j] = float(cumulated_dist[j])/float(noofpeers_incluster[j])
        B_gene = float(min(cumulated_dist))
        
        #Setting S_gene
        S_gene[i] = (B_gene - A_gene)/np.maximum(A_gene, B_gene)
        
    return sum(S_gene)/num_genes