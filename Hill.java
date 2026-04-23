import lejos.nxt.*;
import lejos.robotics.navigation.*;

// Node definition 
class Node {
    char name;
    int x, y;

    Node(char n, int x, int y) {
        this.name = n;
        this.x = x;
        this.y = y;
    }
}

public class Hill {

    final int MAX = 8;

    Node nodes[] = new Node[MAX];

    // adjacency matrix
    int graph[][] = {
        //A B C D E F G H
        {0,1,1,1,0,0,0,0}, // A
        {1,0,1,1,0,0,0,0}, // B
        {1,1,0,0,1,1,0,0}, // C
        {1,1,0,0,1,0,1,1}, // D
        {0,0,1,1,0,1,1,0}, // E
        {0,0,1,0,1,0,0,0}, // F
        {0,0,0,1,1,0,0,1}, // G
        {0,0,0,1,0,0,1,0}  // H
    };

    boolean visited[] = new boolean[MAX];

    Node path[] = new Node[20];
    int pathLength = 0;

    DifferentialPilot pilot;
    NavPathController nav;

    public static void main(String[] args) {

        Hill ob = new Hill();
        ob.setupNodes();

        int start = 0; // A
        int goal = 4;  // E

        ob.search(start, goal);

        // Initialize robot
        ob.pilot = new DifferentialPilot(5.6, 16.0, Motor.B, Motor.C);
        ob.nav = new NavPathController(ob.pilot);

        // Move along path
        for (int i = 0; i < ob.pathLength; i++) {
            Node n = ob.path[i];

            LCD.clear();
            LCD.drawString("Next: " + n.name, 0, 0);
            LCD.drawString("X:" + n.x + " Y:" + n.y, 0, 1);
            LCD.drawString("Press ENTER", 0, 3);

            Button.ENTER.waitForPressAndRelease();

            ob.nav.goTo(n.x, n.y);
        }

        LCD.clear();
        LCD.drawString("Arrived", 0, 3);
        Button.ENTER.waitForPressAndRelease();
    }

    void setupNodes() {
        nodes[0] = new Node('A', 0, 0);
        nodes[1] = new Node('B', 40, 80);
        nodes[2] = new Node('C', 60, 80);
        nodes[3] = new Node('D', 0, 80);
        nodes[4] = new Node('E', 0, 120);
        nodes[5] = new Node('F', 60, 120);
        nodes[6] = new Node('G', -40, 80);
        nodes[7] = new Node('H', -40, 40);
    }

    int dist(int a, int b) {
        int dx = nodes[a].x - nodes[b].x;
        int dy = nodes[a].y - nodes[b].y;
        return (int)Math.sqrt(dx*dx + dy*dy);
    }

    void search(int start, int goal) {

        int current = start;

        path[pathLength++] = nodes[current];
        visited[current] = true;

        while (current != goal) {

            int next = -1;
            int bestDist = 99999;

            for (int i = 0; i < MAX; i++) {
                if (graph[current][i] == 1 && !visited[i]) {

                    int d = dist(i, goal);

                    if (d < bestDist) {
                        bestDist = d;
                        next = i;
                    }
                }
            }

            if (next == -1) {
                LCD.clear();
                LCD.drawString("Stuck!", 0, 0);
                Button.ENTER.waitForPressAndRelease();
                return;
            }

            current = next;
            visited[current] = true;
            path[pathLength++] = nodes[current];
        }
    }
}