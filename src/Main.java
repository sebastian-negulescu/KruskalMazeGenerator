import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

import javax.swing.JFrame;

public class Main{

    Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        //Graphics g = new Graphics();

        Main run = new Main();
        //run.mazeKrus2();

        Maze maze = new Maze(50, 50);
        Solve solve = new Solve(maze);
        Draw draw = new Draw(maze, solve);
        draw.create();

    }

    void mazeKrus2() {
        // make graph. 10 x 10 grid
        int width = 10;
        int height = 10;
        int step = 10;

        ArrayList<ArrayList<Integer>> nodeCoord = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < width; i ++) {
            for (int j = 0; j < height; j ++) {
                ArrayList<Integer> currCoord = new ArrayList<Integer>();
                currCoord.add(i * step + step / 2);
                currCoord.add(j * step + step / 2);
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
        ArrayList<ArrayList<Integer>> edges = new ArrayList<ArrayList<Integer>>();

        for (int i = 0; edges.size() != (nodeCoord.size() - 1); i ++) {
            // pick an edge
            ArrayList<Integer> cEdge = shEdges.get(i);
            int v0 = cEdge.get(0);
            int v1 = cEdge.get(1);

            // check for cycle
            int r0 = findRoot(v0, ptr);
            int r1 = findRoot(v1, ptr);

            ArrayList<Integer> edge = new ArrayList<Integer>();
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
                }/* else { // equal depth
					mxd[r0] += mxd[r0] + 1;
					ptr[r1] = r0;
				}*/
            }
        }

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
        for (int i = 0; i < nodeCoord.size(); i ++) {
            treeNers.add(new ArrayList<Integer>());
        }
        for (int i = 0; i < edges.size(); i ++) {
            int v0 = edges.get(i).get(0);
            int v1 = edges.get(i).get(1);
            treeNers.get(v0).add(v1);
            treeNers.get(v1).add(v0);
        }

        HashSet<Integer> visiteds = new HashSet<Integer>();
        PriorityQueue<Integer> fringes = new PriorityQueue<Integer>();

        int[] bckPtr = new int[width * height];
        Arrays.fill(bckPtr, -1);

        fringes.add(0);
        int endNode = (width * height) - 1;

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

        JFrame frame = new JFrame() {
            /**
             *
             */
            private static final long serialVersionUID = 1L;

            public void paint(Graphics g) {
                g.setColor(Color.BLACK);
                g.drawRect(100, 100, width * step, height * step);
                for (int i = 0; i < edges.size(); i ++) {
                    int x1 = nodeCoord.get(edges.get(i).get(0)).get(0);
                    int y1 = nodeCoord.get(edges.get(i).get(0)).get(1);
                    int x2 = nodeCoord.get(edges.get(i).get(1)).get(0);
                    int y2 = nodeCoord.get(edges.get(i).get(1)).get(1);
                    g.drawLine(x1 + 100, y1 + 100, x2 + 100, y2 + 100);
                }

                // draw path
                g.setColor(Color.GREEN);
                for (int i = 0; i < path.size() - 1; i ++) {
                    int x1 = nodeCoord.get(path.get(i)).get(0);
                    int y1 = nodeCoord.get(path.get(i)).get(1);
                    int x2 = nodeCoord.get(path.get(i + 1)).get(0);
                    int y2 = nodeCoord.get(path.get(i + 1)).get(1);
                    g.drawLine(x1 + 100, y1 + 100, x2 + 100, y2 + 100);
                }
            }
        };

        frame.setSize(200 + width * step, 200 + height * step);
        frame.setTitle("Maze");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);
    }

    int findRoot(int v, int[] arr) {
        if (arr[v] == -1) {
            return v;
        } else {
            return findRoot(arr[v], arr);
        }
    }

    void mazeKrus1() {
        // make graph. 10 x 10 grid
        int width = 10;
        int height = 10;
        int step = 10;

        ArrayList<ArrayList<Integer>> nodeCoord = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < width; i ++) {
            for (int j = 0; j < height; j ++) {
                ArrayList<Integer> currCoord = new ArrayList<Integer>();
                currCoord.add(i * step + step / 2);
                currCoord.add(j * step + step / 2);
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

        int[] ptr = new int[100];
        Arrays.fill(ptr, -1);
        ArrayList<ArrayList<Integer>> edges = new ArrayList<ArrayList<Integer>>();

        for (;edges.size() != (nodeCoord.size() - 1);) {
            // pick a random vertex position
            int cVert = (int) (Math.random() * 100);
            // get the edge
            ArrayList<Integer> potVert = new ArrayList<Integer>(ners.get(cVert));
            // picks one of the neighbors
            int eVert = potVert.get((int) (Math.random() * potVert.size()));

            // check for cycle
            int r1 = findRoot(cVert, ptr);
            int r2 = findRoot(eVert, ptr);

            ArrayList<Integer> edge = new ArrayList<Integer>();
            edge.add(cVert);
            edge.add(eVert);

            if (r1 != r2) {
                // add it
                edges.add(edge);
                // union the subtrees
                ptr[r1] = r2;
            }
        }

        JFrame frame = new JFrame() {
            public void paint(Graphics g) {
                g.setColor(Color.BLACK);
                g.drawRect(100, 100, width * step, height * step);
                for (int i = 0; i < edges.size(); i ++) {
                    int x1 = nodeCoord.get(edges.get(i).get(0)).get(0);
                    int y1 = nodeCoord.get(edges.get(i).get(0)).get(1);
                    int x2 = nodeCoord.get(edges.get(i).get(1)).get(0);
                    int y2 = nodeCoord.get(edges.get(i).get(1)).get(1);
                    g.drawLine(x1 + 100, y1 + 100, x2 + 100, y2 + 100);
                }
            }
        };

        frame.setSize(200 + width * step, 200 + height * step);
        frame.setTitle("Maze");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);
    }

    HashMap<String, Integer> cVals = new HashMap<String, Integer>();

    void cDyn() {
        int n = sc.nextInt();
        int k = sc.nextInt();

        if (n < k) {
            System.out.println("Don't try me");
        } else {
            System.out.println(choose(n, k));
        }
    }

    int choose(int n, int k) {

        if (k == n || k == 0) {
            return 1;
        } else {
            if (cVals.containsKey(n + " " + k)) {
                return cVals.get(n + " " + k);
            } else {
                cVals.put(n + " " + k, choose(n - 1, k) + choose(n - 1, k - 1));
                return cVals.get(n + " " + k);
            }
        }

    }

    void fibDyn() {
        int n = sc.nextInt();
        System.out.println(dyn(n));
    }

    HashMap<String, Integer> fibVals = new HashMap<String, Integer>();

    int dyn(int n) {

        if (n == 1 || n == 2) {
            return 1;
        } else {
            boolean exists = false;

            if (fibVals.containsKey(n + " ")) {
                exists = true;
            }

            if (exists) {
                return fibVals.get(n + " ");
            } else {
                int val1 = dyn(n - 1);
                int val2 = dyn(n - 2);
                fibVals.put(n + " ", val1 + val2);
                return fibVals.get(n + " ");
            }
        }

    }

    void mazePrim() {
        //Init graph
        int width = 5 * 16, height = 5 * 9, step = 10, thres = 2 * (width + height);

        ArrayList<ArrayList<Integer>> nodeCoord = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < width; i ++) {
            for (int j = 0; j < height; j ++) {
                ArrayList<Integer> currCoord = new ArrayList<Integer>();
                currCoord.add(i * step + step / 2);
                currCoord.add(j * step + step / 2);
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

        //Gen span tree
        ArrayList<ArrayList<Integer>> spanTr = new ArrayList<ArrayList<Integer>>();
        HashSet<Integer> visNdIds = new HashSet<Integer>();
        HashSet<ArrayList<Integer>> fringeEds = new HashSet<ArrayList<Integer>>();

        int start = height - 1;

        visNdIds.add(start);

        for (int k = 0; k < ners.get(start).size();
             k ++) {
            ArrayList<Integer> crtEd = new ArrayList<Integer>();
            crtEd.add(start);
            crtEd.add(k);
            fringeEds.add(crtEd);
        }

        Random generator = new Random(System.nanoTime());

        for (;fringeEds.size() != 0;) {
            //Pick a random edge
            int ran = generator.nextInt(fringeEds.size());

            //find its target node
            ArrayList<Integer> crtEdge = element(fringeEds, ran);
            spanTr.add(crtEdge);
            int tat = ners.get(crtEdge.get(0)).get(crtEdge.get(1));

            //Remove from fringeSet edges that go into target node
            HashSet<ArrayList<Integer>> upd = new HashSet<ArrayList<Integer>>();
            Iterator<ArrayList<Integer>> iter = fringeEds.iterator();
            for (int i = 0; i < fringeEds.size(); i ++) {
                ArrayList<Integer> ce2 = iter.next();
                int tat2 = ners.get(ce2.get(0)).get(ce2.get(1));
                if (tat2 == tat) {
                    upd.add(ce2);
                }
            }
            fringeEds.removeAll(upd);

            //Add to fringeSet other edges from target node to outside HashSet
            upd = new HashSet<ArrayList<Integer>>();
            for (int i = 0; i < ners.get(tat).size(); i ++) {
                if (!visNdIds.contains(ners.get(tat).get(i))) {
                    ArrayList<Integer> ce3 = new ArrayList<Integer>();
                    ce3.add(tat);
                    ce3.add(i);
                    upd.add(ce3);
                }
            }

            fringeEds.addAll(upd);

            //Add the target node to the hashSet
            visNdIds.add(tat);

        }

        //Draw walls
        ArrayList<ArrayList<Boolean>> taken = new ArrayList<ArrayList<Boolean>>();
        for (int i = 0; i < ners.size(); i ++) {
            ArrayList<Boolean> fill = new ArrayList<Boolean>();
            for (int j = 0 ; j < ners.get(i).size(); j ++) {
                fill.add(false);
            }
            taken.add(fill);
        }
        for (int i = 0 ; i < spanTr.size(); i ++) {
            int s = spanTr.get(i).get(0);
            int t = ners.get(s).get(spanTr.get(i).get(1));
            taken.get(s).set(spanTr.get(i).get(1), true);
            for (int j = 0; j < ners.get(t).size(); j ++) {
                if (ners.get(t).get(j) == s) {
                    taken.get(t).set(j, true);
                    break;
                }
            }
        }

        JFrame frame = new JFrame() {
            public void paint(Graphics g) {
                g.setColor(Color.BLACK);
                g.drawRect(100, 100, width * step, height * step);
                for (int i = 0; i < ners.size(); i ++) {
                    for (int j = 0 ; j < ners.get(i).size(); j ++) {
                        if (!taken.get(i).get(j)) {
                            int t = ners.get(i).get(j);
                            int xs = nodeCoord.get(i).get(0);
                            int ys = nodeCoord.get(i).get(1);
                            int xt = nodeCoord.get(t).get(0);
                            int yt = nodeCoord.get(t).get(1);

                            if (xs == xt) { //Vertical
                                g.drawLine(xs - step / 2 + 100, (ys + yt) / 2 + 100, xs + step / 2 + 100, (ys + yt) / 2 + 100);
                            }
                            if (ys == yt) { //Horizontal
                                g.drawLine((xs + xt) / 2 + 100, ys - step / 2 + 100, (xs + xt) / 2 + 100, ys + step / 2 + 100);
                            }
                        }
                    }
                }

                int startNode = height - 1;
                int endNode = height * (width - 1);

                ArrayList<Integer> q = new ArrayList<Integer>();
                q.add(startNode);

                HashSet<Integer> disc = new HashSet<Integer>();
                disc.add(startNode);

                HashMap<Integer, Integer> prevNodes = new HashMap<Integer, Integer>();

                for (;q.size() != 0;) {
                    int t = q.remove(0);
                    for (int i = 0; i < ners.get(t).size(); i ++) {
                        int p = ners.get(t).get(i);
                        if (taken.get(t).get(i) && !disc.contains(p)) {
                            q.add(p);
                            disc.add(p);
                            prevNodes.put(p, t);
                            if (p == endNode) {
                                break;
                            }
                        }
                    }
                    if (disc.contains(endNode)) {
                        break;
                    }
                }

                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.GREEN);
                g2.setStroke(new BasicStroke(step / 2));

                for (int temp = endNode; temp != startNode;) {
                    int prev = prevNodes.get(temp);
                    int x1 = nodeCoord.get(temp).get(0);
                    int y1 = nodeCoord.get(temp).get(1);
                    int x2 = nodeCoord.get(prev).get(0);
                    int y2 = nodeCoord.get(prev).get(1);
                    g2.drawLine(x1 + 100, y1 + 100, x2 + 100, y2 + 100);
                    temp = prev;
                }

            }
        };

        frame.setSize(200 + width * step, 200 + height * step);
        frame.setTitle("Maze");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);
    }

    ArrayList<Integer> element(HashSet<ArrayList<Integer>> fringeEds, int n) {
        Iterator<ArrayList<Integer>> iter =  fringeEds.iterator();

        //Assume n >= 0 && n < size
        for (int i = 0; i < n - 1; i ++) {
            iter.next();
        }

        return iter.next();
    }

    ArrayList<ArrayList<Integer>> ners = new ArrayList<ArrayList<Integer>>();
    ArrayList<Integer> targets = new ArrayList<Integer>();

    void graphTrav() {

        int n = sc.nextInt();
        int e = sc.nextInt();
        int t = sc.nextInt();

        for (int i = 0; i < t; i ++) {
            targets.add(sc.nextInt());
        }

        for (int i = 0; i < n; i ++) {
            ners.add(new ArrayList<Integer>());
        }

        int a;
        int b;

        for (int i = 0; i < e; i ++) {
            a = sc.nextInt();
            b = sc.nextInt();
            ners.get(a).add(b);
        }

        reachNodesIter(targets.get(0));

    }

    void reachNodesRec(int source, HashSet<Integer> disc) {
        //If seen, return
        //If not
        //Visit
        //Reach neighbors
        if (!disc.contains(source)) {
            disc.add(source);
            System.out.print("" + source + " ");
            for(int i = 0; i < ners.get(source).size(); i ++) {
                reachNodesRec(ners.get(source).get(i), disc);
            }
        }

    }

    void reachNodesIter(int source) {
        HashSet<Integer> disc = new HashSet<Integer>();
        List<Integer> nodeStack = new ArrayList<Integer>();

        nodeStack.add(source);
        disc.add(source);

        for (;!nodeStack.isEmpty();) {

            int n = nodeStack.remove(0);
            System.out.print("" + n + " ");

            for(int i = 0; i < ners.get(n).size(); i ++) {
                int m = ners.get(n).get(i);
                if (!disc.contains(m)) {
                    nodeStack.add(m);
                    disc.add(m);
                }
            }
        }
    }

}
