import lejos.nxt.*;
import lejos.robotics.navigation.*;

class Node {
    int id;
    int x, y;

    Node(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }
}

public class Dijkstra {

    final int MAX = 9;
    final int INF = 99999;

    Node nodes[] = new Node[MAX];

    // adjacency matrix 
    int graph[][] = {
        //0 1 2 3 4 5 6 7 8
        {0,1,1,1,0,0,0,0,0}, // 0
        {1,0,1,1,0,0,0,0,0}, // 1
        {1,1,0,0,1,1,1,1,1}, // 2
        {1,1,0,0,1,1,0,0,0}, // 3
        {0,0,1,1,0,1,0,0,0}, // 4
        {0,0,1,1,1,0,0,1,1}, // 5
        {0,0,1,0,0,0,0,1,0}, // 6
        {0,0,1,0,0,1,1,0,1}, // 7
        {0,0,1,0,0,1,0,1,0}  // 8
    };

    int dist[] = new int[MAX];
    int prev[] = new int[MAX];
    boolean visited[] = new boolean[MAX];

    Node path[] = new Node[20];
    int pathLength = 0;

    DifferentialPilot pilot;
    NavPathController nav;

    public static void main(String[] args) {

        Dijkstra ob = new Dijkstra();
        ob.setupNodes();

        int start = 0; // origin
        int goal = 8;  // target

        ob.compute(start);
        ob.buildPath(start, goal);

        // Initialize robot
        ob.pilot = new DifferentialPilot(5.6, 16.0, Motor.B, Motor.C);
        ob.nav = new NavPathController(ob.pilot);

        // Move along path
        for (int i = 0; i < ob.pathLength; i++) {
            Node n = ob.path[i];

            LCD.clear();
            LCD.drawString("Node: " + n.id, 0, 0);
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
        nodes[0] = new Node(0, 0, 0);
        nodes[1] = new Node(1, 40, 80);
        nodes[2] = new Node(2, 0, 80);
        nodes[3] = new Node(3, 60, 80);
        nodes[4] = new Node(4, 60, 120);
        nodes[5] = new Node(5, 0, 120);
        nodes[6] = new Node(6, -40, 40);
        nodes[7] = new Node(7, -40, 800);
        nodes[8] = new Node(8, -40, 120);
    }

    // Euclidean distance as edge weight
    int weight(int i, int j) {
        int dx = nodes[i].x - nodes[j].x;
        int dy = nodes[i].y - nodes[j].y;
        return (int)Math.sqrt(dx*dx + dy*dy);
    }

    void compute(int start) {

        for (int i = 0; i < MAX; i++) {
            dist[i] = INF;
            prev[i] = -1;
            visited[i] = false;
        }

        dist[start] = 0;

        for (int count = 0; count < MAX; count++) {

            int u = minDistance();
            if (u == -1) break;

            visited[u] = true;

            for (int v = 0; v < MAX; v++) {
                if (!visited[v] && graph[u][v] == 1) {

                    int alt = dist[u] + weight(u, v);

                    if (alt < dist[v]) {
                        dist[v] = alt;
                        prev[v] = u;
                    }
                }
            }
        }
    }

    int minDistance() {
        int min = INF;
        int index = -1;

        for (int i = 0; i < MAX; i++) {
            if (!visited[i] && dist[i] < min) {
                min = dist[i];
                index = i;
            }
        }
        return index;
    }

    void buildPath(int start, int goal) {

        int temp[] = new int[MAX];
        int len = 0;

        int current = goal;

        while (current != -1) {
            temp[len++] = current;
            current = prev[current];
        }

        // reverse into final path
        for (int i = len - 1; i >= 0; i--) {
            path[pathLength++] = nodes[temp[i]];
        }
    }
}