package net.solace.loader.plugins.arceuuslibrary.domain;

import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;

public class LibraryGraph {

    private static final int N = 12;
    private static final int REACHES_NEGATIVE_CYCLE = -1;
    private static boolean solved;
    private static double[][] graph = new double[N][N];
    private static double[][] dist = new double[N][N];
    // Precomputed paths
    private static Integer[][] next = {
            {0, 1, 2, 3, 4, 1, 2, 3, 4, 1, 4, 3},
            {0, 1, 2, 0, 0, 5, 2, 0, 0, 5, 5, 0},
            {0, 1, 2, 3, 0, 1, 6, 3, 0, 1, 0, 3},
            {0, 0, 2, 3, 0, 0, 2, 7, 0, 0, 7, 7},
            {0, 0, 0, 0, 4, 0, 0, 0, 8, 8, 8, 8},
            {1, 1, 1, 1, 1, 5, 1, 1, 9, 9, 9, 9},
            {2, 2, 2, 2, 2, 2, 6, 2, 2, 2, 2, 2},
            {3, 3, 3, 3, 3, 3, 3, 7, 11, 11, 11, 11},
            {4, 4, 4, 4, 4, 9, 4, 11, 8, 9, 10, 11},
            {5, 5, 5, 5, 8, 5, 5, 8, 8, 9, 10, 8},
            {8, 9, 8, 11, 8, 9, 8, 11, 8, 9, 10, 11},
            {7, 7, 7, 7, 8, 8, 7, 7, 8, 8, 10, 11},
    };

    public static WorldPoint getNextWayPoint(WorldPoint origin, WorldPoint destination) {
        if (Room.isSameLibraryRoom(origin, destination)) {
            return destination;
        }
        int nextRoom = getNextWaypoint(Room.getRoomByWorldPoint(origin), Room.getRoomByWorldPoint(destination));
        if (Room.isRoomSameFloor(nextRoom, origin)) {
            return Room.getRoom(nextRoom).getWorldPoint();
        }
        boolean isGoingUp = origin.getPlane() < Room.getRoom(nextRoom).getWorldPoint().getPlane();
        Stair nextRoomStair = Room.getStair(Room.getRoomByWorldPoint(origin), isGoingUp);
        if (nextRoomStair == null) {
            return null;
        }
        return nextRoomStair.getWorldPoint();
    }

    public static void reset() {
        createGraph();
        dist = new double[N][N];
        next = new Integer[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (graph[i][j] != POSITIVE_INFINITY) {
                    next[i][j] = j;
                }
                dist[i][j] = graph[i][j];
            }
        }
    }

    // Floyd-warshall to calculate shortest paths between all rooms
    public static void solve() {
        if (solved) {
            return;
        }

        for (int k = 0; k < N; k++) {
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        next[i][j] = next[i][k];
                    }
                }
            }
        }

        // Check for negative cycles
        for (int k = 0; k < N; k++) {
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = NEGATIVE_INFINITY;
                        next[i][j] = REACHES_NEGATIVE_CYCLE;
                    }
                }
            }
        }

        solved = true;
    }

    public static void printNext() {
        StringBuilder s = new StringBuilder();
        for (Integer[] nexts : next) {
            s.append("{ ").append(Arrays.toString(nexts)).append(" },");
        }
        System.out.println(s.toString());
    }

    public static List<Integer> getShortestPath(int start, int end) {
        //solve();
        List<Integer> path = new ArrayList<>();
        if (dist[start][end] == POSITIVE_INFINITY) {
            return path;
        }
        int at = start;
        for (; at != end; at = next[at][end]) {
            if (at == REACHES_NEGATIVE_CYCLE) {
                return null;
            }
            path.add(at);
        }
        if (next[at][end] == REACHES_NEGATIVE_CYCLE) {
            return null;
        }
        path.add(end);
        return path;
    }

    public static int getNextWaypoint(int start, int end) {
        //solve();
        return next[start][end];
    }

    public static void createGraph() {
        graph = new double[N][N];
        for (int i = 0; i < N; i++) {
            Arrays.fill(graph[i], POSITIVE_INFINITY);
            graph[i][i] = 0;
        }
        addEdge(Room.TNW.getIndex(), Room.TNE.getIndex());
        addEdge(Room.TNW.getIndex(), Room.TSW.getIndex());
        addEdge(Room.TNW.getIndex(), Room.TC.getIndex());
        addEdge(Room.TSW.getIndex(), Room.TC.getIndex());
        addEdge(Room.TC.getIndex(), Room.TNE.getIndex());
        addEdge(Room.TNW.getIndex(), Room.MNW.getIndex());
        addEdge(Room.TNE.getIndex(), Room.MNE.getIndex());
        addEdge(Room.TSW.getIndex(), Room.MSW.getIndex());
        addEdge(Room.TC.getIndex(), Room.MC.getIndex());
        addEdge(Room.MNW.getIndex(), Room.BNW.getIndex());
        addEdge(Room.MNE.getIndex(), Room.BNE.getIndex());
        addEdge(Room.MSW.getIndex(), Room.BSW.getIndex());
        addEdge(Room.BNW.getIndex(), Room.BNE.getIndex());
        addEdge(Room.BNW.getIndex(), Room.BSW.getIndex());
        addEdge(Room.BNW.getIndex(), Room.BC.getIndex());
        addEdge(Room.BNE.getIndex(), Room.BC.getIndex());
        addEdge(Room.BC.getIndex(), Room.BSW.getIndex());
    }

    public static void addEdge(int i, int j) {
        // Undirected
        graph[i][j] = 1;
        graph[j][i] = 1;
    }
}