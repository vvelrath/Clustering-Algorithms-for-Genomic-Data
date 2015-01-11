package edu.buffalo.cse601;

import java.util.ArrayList;
import java.util.Map;

public class ExternalValidation {

    int[][] C;
    int[][] P;
    int SS;
    int DD;
    int SD;
    int DS;
    int sizeOfData;
    ArrayList<Integer> groundTruth;
    ArrayList<Integer> clusterResults;
    float rand;
    float jaccard;

    public ExternalValidation(int size, ArrayList<Integer> groundTruth,
            ArrayList<Integer> clusterResults) {

        C = new int[size][size];
        P = new int[size][size];
        SS = 0;
        DD = 0;
        SD = 0;
        DS = 0;
        sizeOfData = size;
        this.groundTruth = groundTruth;
        this.clusterResults = clusterResults;
    }

    void populateMatrix() {
        for (int i = 0; i < sizeOfData; i++) {
            for (int j = i; j < sizeOfData; j++) {
                if (clusterResults.get(i) == clusterResults.get(j))
                    C[i][j] = C[j][i] = 1;
                else
                    C[i][j] = C[j][i] = 0;

                if (groundTruth.get(i) == groundTruth.get(j))
                    P[i][j] = P[j][i] = 1;
                else
                    P[i][j] = P[j][i] = 0;
            }
        }

    }

    public float getCo_efficient() {
        populateMatrix();
        for (int i = 0; i < sizeOfData; i++) {
            for (int j = 0; j < sizeOfData; j++) {
                if (C[i][j] == P[i][j]) {
                    if (C[i][j] == 1)
                        SS++;
                    else
                        DD++;
                }else
                {
                    if(C[i][j] == 1 && P[i][j] == 0)
                        SD++;
                    else if(C[i][j] == 0 && P[i][j] == 1)
                        DS++;
                }
            }
        }

        rand =(float) (SS+DD) / (SS+SD+DS+DD);
        return rand*100;
    }

    public float get_Jaccard_coEff(){
        jaccard=(float)(SS)/(SS+SD+DS);
        return jaccard*100;

    }

}
