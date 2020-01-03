import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;

public class Solve {
    ArrayList<Integer> solvePath;

    Solve(Maze maze) {
        // bfs
        // arrayList treeNers go over edges and set bidirectional tree ners
        // hashset of visiteds
        // queue of fringes
        // array backpointers (-1)
        // push start node to fringes
        // A. pop a node from fringes
        // check if node was visited. If not, add to visited
        // check if node is final node. If not, add treeNers[node] to fringes update backpointer of new fringe node
        // go to step A with node from fringes
        // if it is a final node, collect path with backpointers

        ArrayList<ArrayList<Integer>> treeNers = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < maze.mazeNodeCoord.size(); i ++) {
            treeNers.add(new ArrayList<Integer>());
        }
        for (int i = 0; i < maze.mazeEdges.size(); i ++) {
            int v0 = maze.mazeEdges.get(i).get(0);
            int v1 = maze.mazeEdges.get(i).get(1);
            treeNers.get(v0).add(v1);
            treeNers.get(v1).add(v0);
        }

        HashSet<Integer> visiteds = new HashSet<Integer>();
        PriorityQueue<Integer> fringes = new PriorityQueue<Integer>();

        int[] bckPtr = new int[maze.width * maze.height];
        Arrays.fill(bckPtr, -1);

        fringes.add(0);
        int endNode = (maze.width * maze.height) - 1;

        for (;fringes.size() > 0;) {
            int node = fringes.remove();
            visiteds.add(node);
            if (node == endNode) {
                break;
            } else {
                for (int i : treeNers.get(node)) {
                    if (!visiteds.contains(i)) {
                        fringes.add(i);
                        bckPtr[i] = node;
                    }
                }
            }

        }

        ArrayList<Integer> path = new ArrayList<Integer>();
        int ind = endNode;
        path.add(endNode);
        while (bckPtr[ind] != -1) {
            path.add(bckPtr[ind]);
            ind = bckPtr[ind];
        }

        solvePath = new ArrayList<Integer>(path);
    }
}
