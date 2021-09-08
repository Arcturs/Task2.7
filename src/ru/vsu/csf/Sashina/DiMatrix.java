package ru.vsu.csf.Sashina;

import java.util.Stack;

public class DiMatrix extends Matrix implements DiGraph{

    public DiMatrix(int vertexCount) {
        super(vertexCount);
    }

    public DiMatrix() {
        this(0);
    }

    public boolean existenceOfCycle () {
        int[][] inOut = new int[vertexCount()][2];
        boolean[][] adjMatrix = getAdjMatrix();
        boolean flag = true;
        for (int i = 0; i < vertexCount(); i++) {
            for (int j = 0; j < vertexCount(); j++) {
                if (adjMatrix[i][j])
                    inOut[i][0] += 1;
                if (adjMatrix[j][i])
                    inOut[i][1] += 1;
            }
            if (i != 0 && inOut[i-1][0] == 0 && inOut[i-1][1] == 0)
                flag = false;
        }
        for (int i = 0; i < vertexCount(); i++) {
            if (inOut[i][0] != inOut[i][1])
                flag = false;
        }
        return flag;
    }

    public String findCycle () {
        StringBuilder s = new StringBuilder();
        boolean[][] adjMatrix = getAdjMatrix();
        Stack<Integer> stack = new Stack<>();
        Stack<Integer> cycle = new Stack<>();
        int v = 0;
        int [] degreeV = degree();
        stack.push(v);
        while (!stack.empty()) {
           if (degreeV[v] != 0) {
               stack.push(v);
               int i = 0;
               while (!adjMatrix[v][i]) {
                   i++;
               }
               degreeV[v]--;
               adjMatrix[v][i] = false;
               v = i;
           } else {
               cycle.add(v);
               v = stack.peek();
               stack.pop();
           }
        }
        while (!cycle.empty()) {
            s.append(cycle.pop());
            s.append(" ");
        }
        return s.toString();
    }
}
