import lejos.nxt.*;
import lejos.robotics.navigation.*;
import lejos.nxt.Motor;

public class DFS {

    // -------------------------
    // Tree Node with coordinates
    // -------------------------
    static class Node {
        String name;
        int x, y;
        Node left, right;

        Node(String name, int x, int y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }
    }

    // -------------------------
    // Fields
    // -------------------------
    Node root;
    Node[] path;       // array of nodes for the final route
    int pathLength;    // number of nodes in path

    DifferentialPilot pilot;
    NavPathController nav;

    // -------------------------
    // Setup tree
    // -------------------------
    void setupTree() {
    	// Define nodes and their coordinates
        Node A = new Node("A", 0, 0);       
        Node B = new Node("B", 20, 20);   
        Node C = new Node("C", -20, 20);  
        Node D = new Node("D", 40, 40);   
        Node E = new Node("E", 0, 40);     
        Node F = new Node("F", 20, 60);   
        Node G = new Node("G", -20, 60);  
        Node H = new Node("H", 0, 80);     
        Node I = new Node("I", -40, 80);  
        Node J = new Node("J", 20, 100);   
        Node K = new Node("K", -20, 100);  
        Node L = new Node("L", 40, 120);   
        Node M = new Node("M", 0, 120); 

        // Connect tree
        root = A;
        A.left = B;
        A.right = C;
        B.left = D;
        B.right = E;
        E.left = F;
        E.right = G;
        G.left = H;
        G.right = I;
        H.left = J;
        H.right = K;
        J.left = L;
        J.right = M;
    }

    // -------------------------
    // DFS to find path to target
    // -------------------------
    boolean findPath(Node node, String target, Node[] route, int depth) {
        if (node == null) return false;

        route[depth] = node;

        if (node.name.equals(target)) {
            pathLength = depth + 1; // store number of nodes in path
            return true;
        }

        if (findPath(node.left, target, route, depth + 1)) return true;
        if (findPath(node.right, target, route, depth + 1)) return true;

        return false; // backtrack
    }

    // -------------------------
    // Main
    // -------------------------
    public static void main(String[] args) {

        DFS ob = new DFS();
        String targetNode = "E";
        // Phase 1: Build tree and find path
        ob.setupTree();
        ob.path = new Node[20]; // max tree depth
        boolean found = ob.findPath(ob.root, targetNode, ob.path, 0);

        if (!found) {
            LCD.clear();
            LCD.drawString("Target not found", 0, 3);
            Button.ENTER.waitForPressAndRelease();
            return;
        }

        // Phase 2: Initialize robot
        ob.pilot = new DifferentialPilot(5.6, 16.0, Motor.B, Motor.C);
        ob.nav = new NavPathController(ob.pilot);
        
        /**
        // Print the sequence
        System.out.println("Path");
        for (int i = 0; i < ob.pathLength; i++){
        	Node n = ob.path[i];
        	if (n.name != targetNode) 
        		System.out.print(n.name + " > ");
        	else
        		System.out.print(n.name);
        }
        */
        // Move along path
        for (int i = 0; i < ob.pathLength; i++) {
            Node n = ob.path[i];

            LCD.clear();
            LCD.drawString("Next: " + n.name, 0, 0);
            LCD.drawString("X: " + n.x + " Y: " + n.y, 0, 2);
            LCD.drawString("Press ENTER", 0, 4);
            Button.ENTER.waitForPressAndRelease();

            ob.nav.goTo(n.x, n.y);
        }

        LCD.clear();
        LCD.drawString("Arrived at target", 0, 3);
        Button.ENTER.waitForPressAndRelease();
    }
}