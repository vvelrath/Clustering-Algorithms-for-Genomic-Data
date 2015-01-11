# To change this license header, choose License Headers in Project Properties.
# To change this template file, choose Tools | Templates
# and open the template in the editor.

cluster_rslt_matrix = []
ground_truth_matrix = []
SS = 0
DD = 0
SD = 0
DS = 0

def populateMatrix(num_genes, cluster_results, ground_truth):
    global ground_truth_matrix
    global cluster_rslt_matrix
    cluster_rslt_matrix = [[0 for j in range(num_genes)] for i in range(num_genes)]
    ground_truth_matrix = [[0 for j in range(num_genes)] for i in range(num_genes)]
    for i in range(num_genes):
        for j in range(i,num_genes):
            if cluster_results[i] == cluster_results[j]:
                cluster_rslt_matrix[i][j] = cluster_rslt_matrix[j][i] = 1
            else:
                cluster_rslt_matrix[i][j] = cluster_rslt_matrix[j][i] = 0
                
            if ground_truth[i][1] == ground_truth[j][1]:
                ground_truth_matrix[i][j] = ground_truth_matrix[j][i] = 1
            else:
                ground_truth_matrix[i][j] = ground_truth_matrix[j][i] = 0            


def getRandIndex(num_genes, cluster_results, ground_truth):
    global ground_truth_matrix
    global cluster_rslt_matrix
    global SS
    global DD
    global SD
    global DS

    populateMatrix(num_genes, cluster_results, ground_truth)
    
    for i in range(num_genes):
        for j in range(num_genes):
            if cluster_rslt_matrix[i][j] == ground_truth_matrix[i][j]:
                if cluster_rslt_matrix[i][j] == 1:
                    SS = SS + 1
                else:
                    DD = DD + 1
            else:
                if cluster_rslt_matrix[i][j] == 1 and ground_truth_matrix[i][j] == 0:
                    SD = SD + 1
                else:
                    DS = DS + 1
    rand = (float) (SS+DD) / (SS+SD+DS+DD)
    return rand*100


def getJaccardCoefficient():
    global SS
    global DD
    global SD
    global DS
    jaccard = (float) (SS) / (SS+SD+DS)
    return jaccard*100