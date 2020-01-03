import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Maze {
    int step = 10; // 1 unit = 10 px
    int width;
    int height;

    public ArrayList<ArrayList<Integer>> mazeEdges;
    public ArrayList<ArrayList<Integer>> mazeNodeCoord;

    public Maze(int width, int height) { // specify maze width, height (in units)
        this.width = width; // initializes the width
        this.height = height; // initializes the height

        // create the maze
        this.gen();
    }

    private void gen() {
        // create maze first with units, then go to pixels

        // creates a coordinate list based on the width and height of the maze
        ArrayList<ArrayList<Integer>> nodeCoord = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < this.width; i ++) {
            for (int j = 0; j < this.height; j ++) {
                ArrayList<Integer> currCoord = new ArrayList<Integer>();
                currCoord.add(i);
                currCoord.add(j);
                nodeCoord.add(currCoord);
            }
        }

        ArrayList<ArrayList<Integer>> ners = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < width; i ++) {
            for (int j = 0; j < height; j ++) {
                ArrayList<Integer> currNers = new ArrayList<Integer>();
                if (j - 1 >= 0) {
                    currNers.add(j - 1 + i * height);
                }
                if (i + 1 < width) {
                    currNers.add(j + (i + 1) * height);
                }
                if (j + 1 < height) {
                    currNers.add(j + 1 + i * height);
                }
                if (i - 1 >= 0) {
                    currNers.add(j + (i - 1) * height);
                }
                ners.add(currNers);
            }
        }

        // list of potential edges
        ArrayList<ArrayList<Integer>> potEdges = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < nodeCoord.size(); i ++) { // 0 - 24
            for (int j : ners.get(i)) { // neighbour of vertex
                ArrayList<Integer> cPotEdge = new ArrayList<Integer>();
                if (i < j) {
                    cPotEdge.add(i);
                    cPotEdge.add(j);
                    potEdges.add(cPotEdge);
                }
            }
        }

        // "sort" the edges -- shuffling them since all the same weight
        ArrayList<ArrayList<Integer>> shEdges = new ArrayList<ArrayList<Integer>>();
        int len = potEdges.size();
        Random generator = new Random();
        for (int i = 0; i < len; i ++) {
            int ind = generator.nextInt(potEdges.size());
            shEdges.add(potEdges.remove(ind));
        }

        int[] ptr = new int[width * height];
        int[] mxd = new int[width * height]; // maximum depth
        Arrays.fill(ptr, -1);
        Arrays.fill(mxd, 0);
        ArrayList<ArrayList<Integer>> edges = new ArrayList<>();

        for (int i = 0; edges.size() != (nodeCoord.size() - 1); i ++) {
            // pick an edge
            ArrayList<Integer> cEdge = shEdges.get(i);
            int v0 = cEdge.get(0); // from
            int v1 = cEdge.get(1); // to

            // check for cycle
            int r0 = this.findRoot(v0, ptr);
            int r1 = this.findRoot(v1, ptr);

            ArrayList<Integer> edge = new ArrayList<>();
            edge.add(v0);
            edge.add(v1);

            if (r0 != r1) {
                // add it
                edges.add(edge);
                // figure which root has a bigger max depth
                if (mxd[r0] >= mxd[r1]) {
                    ptr[r1] = r0;
                    mxd[r0] += mxd[r1] + 1;
                    // add mxd of r1 + 1 to r0
                } else if (mxd[r0] < mxd[r1]) {
                    ptr[r0] = r1;
                    mxd[r1] += mxd[r0] + 1;
                    // add mxd of r0 + 1 to r1
                }
            }
        }

        mazeEdges = new ArrayList<>(edges);
        mazeNodeCoord = new ArrayList<>(nodeCoord);
    }

    private int findRoot(int v, int[] arr) {
        if (arr[v] == -1)
            return v;
        else
            return findRoot(arr[v], arr);
    }
}
