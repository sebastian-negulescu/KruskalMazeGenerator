import javax.swing.*;
import java.awt.*;

public class Draw {
    Maze maze;
    Solve soln;
    Draw(Maze maze, Solve soln) {
        this.maze = maze;
        this.soln = soln;
    }

    int step = 10;

    void create() {
        JFrame frame = new JFrame() {
            private static final long serialVersionUID = 1L;

            public void paint(Graphics g) {
                g.setColor(Color.BLACK);
                g.drawRect(100, 100, maze.width * maze.step, maze.height * maze.step);
                for (int i = 0; i < maze.mazeEdges.size(); i ++) {
                    int x1 = maze.mazeNodeCoord.get(maze.mazeEdges.get(i).get(0)).get(0) * step + step / 2;
                    int y1 = maze.mazeNodeCoord.get(maze.mazeEdges.get(i).get(0)).get(1) * step + step / 2;
                    int x2 = maze.mazeNodeCoord.get(maze.mazeEdges.get(i).get(1)).get(0) * step + step / 2;
                    int y2 = maze.mazeNodeCoord.get(maze.mazeEdges.get(i).get(1)).get(1) * step + step / 2;
                    g.drawLine(x1 + 100, y1 + 100, x2 + 100, y2 + 100);
                }

                // draw path
                g.setColor(Color.GREEN);
                for (int i = 0; i < soln.solvePath.size() - 1; i ++) {
                    int x1 = maze.mazeNodeCoord.get(soln.solvePath.get(i)).get(0) * step + step / 2;
                    int y1 = maze.mazeNodeCoord.get(soln.solvePath.get(i)).get(1) * step + step / 2;
                    int x2 = maze.mazeNodeCoord.get(soln.solvePath.get(i + 1)).get(0) * step + step / 2;
                    int y2 = maze.mazeNodeCoord.get(soln.solvePath.get(i + 1)).get(1) * step + step / 2;
                    g.drawLine(x1 + 100, y1 + 100, x2 + 100, y2 + 100);
                }
            }
        };

        frame.setSize(200 + maze.width * maze.step, 200 + maze.height * maze.step);
        frame.setTitle("Maze");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);
    }
}
