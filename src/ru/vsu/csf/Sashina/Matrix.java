package ru.vsu.csf.Sashina;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Stack;

public class Matrix implements Graph{

    private boolean[][] adjMatrix = null;
    private int vCount = 0;
    private int eCount = 0;

    public Matrix(int vertexCount) {
        adjMatrix = new boolean[vertexCount][vertexCount];
        vCount = vertexCount;
    }

    public Matrix() {
        this(0);
    }

    public boolean[][] getAdjMatrix() {
        return adjMatrix;
    }

    @Override
    public int vertexCount() {
        return vCount;
    }

    @Override
    public int edgeCount() {
        return eCount;
    }

    @Override
    public void addEdge(int v1, int v2) {
        int maxV = Math.max(v1, v2);
        if (maxV >= vertexCount()) {
            adjMatrix = Arrays.copyOf(adjMatrix, maxV + 1);
            for (int i = 0; i <= maxV; i++) {
                adjMatrix[i] = i < vCount ? Arrays.copyOf(adjMatrix[i], maxV + 1) : new boolean[maxV + 1];
            }
            vCount = maxV + 1;
        }
        if (!adjMatrix[v1][v2]) {
            adjMatrix[v1][v2] = true;
            eCount++;
            // для наследников
            if (!(this instanceof DiGraph)) {
                adjMatrix[v2][v1] = true;
            }
        }
    }

    @Override
    public void removeEdge(int v1, int v2) {
        if (adjMatrix[v1][v2]) {
            adjMatrix[v1][v2] = false;
            eCount--;
            // для наследников
            if (!(this instanceof DiGraph)) {
                adjMatrix[v2][v1] = false;
            }
        }
    }

    @Override
    public Iterable<Integer> adjacencies(int v) {
        return new Iterable<Integer>() {
            Integer nextAdj = null;

            @Override
            public Iterator<Integer> iterator() {
                for (int i = 0; i < vCount; i++) {
                    if (adjMatrix[v][i]) {
                        nextAdj = i;
                        break;
                    }
                }

                return new Iterator<Integer>() {
                    @Override
                    public boolean hasNext() {
                        return nextAdj != null;
                    }

                    @Override
                    public Integer next() {
                        Integer result = nextAdj;
                        nextAdj = null;
                        for (int i = result + 1; i < vCount; i++) {
                            if (adjMatrix[v][i]) {
                                nextAdj = i;
                                break;
                            }
                        }
                        return result;
                    }
                };
            }
        };
    }

    // Перегрузка для быстродействия
    @Override
    public boolean isAdj(int v1, int v2) {
        return adjMatrix[v1][v2];
    }

    public int[] degree () {
        int[] inOut = new int[vCount];
        for (int i = 0; i < vCount; i++) {
            for (int j = 0; j < vCount; j++) {
                if (adjMatrix[i][j])
                    inOut[i] += 1;
            }
        }
        return inOut;
    }

    @Override
    public boolean existenceOfCycle () {
        int[] inOut = new int[vCount];
        boolean flag = true;
        for (int i = 0; i < vCount; i++) {
            for (int j = 0; j < vCount; j++) {
                if (adjMatrix[i][j])
                    inOut[i] += 1; //степень вершины i
            }
            if (i != 0 && inOut[i-1] == 0) //если компонент связности вершины равен 0
                flag = false;
        }
        //Эйлеров цикл существует тогда и только тогда, когда все вершины имеют четную степень
        for (int i = 0; i < vCount; i++) {
            if (inOut[i] % 2 == 1) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    @Override
    public String findCycle () {
        Stack<Integer> stack = new Stack<>();
        stack.push(0);
        StringBuilder s = new StringBuilder();
        while (!stack.empty()) {
            int j = stack.peek();
            boolean edge = false;
            for (int i = 0; i < vertexCount(); i++) {
                if (adjMatrix[j][i]) {
                    stack.push(i);
                    adjMatrix[j][i] = false;
                    adjMatrix[i][j] = false;
                    edge = true;
                    break;
                }
            }
            if (!edge) {
                stack.pop();
                s.append(j);
                s.append(" ");
            }
        }
        return s.toString();
    }
}
