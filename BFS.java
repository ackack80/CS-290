import lejos.nxt.*;
import lejos.robotics.navigation.*;
import lejos.nxt.Motor;
import java.util.*;

public class BFS {

    static class Node {
        String name;
        int x, y;
        Node left, right;
        boolean skip;  // Similar to flight skip

        Node(String name, int x, int y) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.skip = false;
        }
    }

    Node root;
    Node[] path;
    int pathLength;

    DifferentialPilot pilot;
    NavPathController nav;

    Stack<Node> btStack = new Stack<>(); // backtrack stack

    // -------------------------
    // Build tree
    // -------------------------
    void setupTree() {
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

        root = A;
        A.left = B;  A.right = C;
        B.left = D;  B.right = E;
        E.left = F;  E.right = G;
        G.left = H;  G.right = I;
        H.left = J;  H.right = K;
        J.left = L;  J.right = M;
    }

    // -------------------------
    // Find next child that hasn't been skipped
    // -------------------------
    Node find(Node node) {
        if (node.left != null && !node.left.skip) {
            node.left.skip = true;
            return node.left;
        }
        if (node.right != null && !node.right.skip) {
            node.right.skip = true;
            return node.right;
        }
        return null;
    }

    // -------------------------
    // Check if direct connection exists
    // -------------------------
    boolean match(Node from, String target) {
        if ((from.left != null && from.left.name.equals(target) && !from.left.skip)
            || (from.right != null && from.right.name.equals(target) && !from.right.skip)) {
            return true;
        }
        return false;
    }

    // -------------------------
    // BFS search with backtracking stack
    // -------------------------
    void isTarget(Node from, String target) {
        Stack resetStack = new Stack();
        Node next;

        // Direct match from current node
        if (from.left != null && from.left.name.equals(target) && !from.left.skip) {
            from.left.skip = true;
            btStack.push(from);
            btStack.push(from.left); //  real node
            return;
        }

        if (from.right != null && from.right.name.equals(target) && !from.right.skip) {
            from.right.skip = true;
            btStack.push(from);
            btStack.push(from.right); //  real node
            return;
        }

        // Explore children
        while ((next = find(from)) != null) {
            resetStack.push(next);

            // Check if child connects to target
            if (next.left != null && next.left.name.equals(target) && !next.left.skip) {
                btStack.push(from);
                btStack.push(next);
                btStack.push(next.left); //  real node
                return;
            }

            if (next.right != null && next.right.name.equals(target) && !next.right.skip) {
                btStack.push(from);
                btStack.push(next);
                btStack.push(next.right); //  real node
                return;
            }
        }

        // Reset skip flags
        while (!resetStack.empty()) {
            Node n = (Node) resetStack.pop();
            n.skip = false;
        }

        // Backtracking
        next = find(from);
        if (next != null) {
            btStack.push(from);
            isTarget(next, target);
        } else if (!btStack.empty()) {
            Node back = (Node) btStack.pop();
            isTarget(back, target);
        }
    }

    // -------------------------
    // Extract path from stack
    // -------------------------
    void buildPath() {
        Stack rev = new Stack();

        while (!btStack.empty()) {
            rev.push(btStack.pop());
        }

        pathLength = rev.size();
        path = new Node[pathLength];

        for (int i = 0; i < pathLength; i++) {
            path[i] = (Node) rev.pop();
        }
    }

    // -------------------------
    // Main
    // -------------------------
    public static void main(String[] args) {
        BFS ob = new BFS();
        String targetNode = "I";

        ob.setupTree();
        ob.path = new Node[20];

        ob.isTarget(ob.root, targetNode);
        ob.buildPath();

        // Initialize robot
        ob.pilot = new DifferentialPilot(5.6, 16.0, Motor.B, Motor.C);
        ob.nav = new NavPathController(ob.pilot);

        // Move along path
        for (int i = 0; i < ob.pathLength; i++) {
            Node n = ob.path[i];
            LCD.clear();
            LCD.drawString("Next: " + n.name, 0, 0);
            LCD.drawString("Press ENTER", 0, 2);
            Button.ENTER.waitForPressAndRelease();
            ob.nav.goTo(n.x, n.y);
        }

        LCD.clear();
        LCD.drawString("Arrived at target", 0, 3);
        Button.ENTER.waitForPressAndRelease();
    }
}