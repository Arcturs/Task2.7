package ru.vsu.csf.Sashina;

public interface Graph {

    int vertexCount();

    int edgeCount();

    void addEdge(int v1, int v2);

    void removeEdge(int v1, int v2);

    Iterable<Integer> adjacencies(int v);

    default boolean isAdj(int v1, int v2) {
        for (Integer adj : adjacencies(v1)) {
            if (adj == v2) {
                return true;
            }
        }
        return false;
    }

    boolean existenceOfCycle();

    String findCycle();
}
