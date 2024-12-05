//Bungein J Cheng
import java.util.*;
class Node {
    int visits;
    double value;
    Node parent;
    List<Node> children;
    int move;

    public Node(Node parent, int move) {
        this.parent = parent;
        this.move = move;
        this.children = new ArrayList<>();
        this.visits = 0;
        this.value = 0;
    }

    public boolean isFullyExpanded(ConnectFour game) {
        return children.size() == game.getLegalMoves().size();
    }
}

public class UCT {
    private ConnectFour game;
    private Random random;
    private int simulations;
    private String outputMode;
    private static final double C = Math.sqrt(2);

    public UCT(ConnectFour game, int simulations, String outputMode) {
        this.game = game;
        this.random = new Random();
        this.simulations = simulations;
        this.outputMode = outputMode;
    }

    public int runUCT() {
        Node root = new Node(null, -1);

       
        for (int i = 0; i < simulations; i++) { //run simulations
            ConnectFour simGame = game.clone(); //create a copy of the game
            Node node = treePolicy(root, simGame); //run the tree policy
            double result = defaultPolicy(simGame); //get the default policy
            backpropagate(node, result);

            if (outputMode.equals("Verbose")) { //print info
                System.out.println("\nSimulation " + (i + 1) + ":");
                printNodeInfo(root);
                System.out.println();
            }
        }

        
        Node bestChild = getBestChild(root, 0);//select best move based on average value 
        
        //print final values if in Verbose or Brief mode
        if (outputMode.equals("Verbose") || outputMode.equals("Brief")) {
            System.out.println("\nFinal values:");
            for (Node child : root.children) { //print final values
                double value = child.visits > 0 ? child.value / child.visits : 0; //calculate value
                System.out.println("Column " + (child.move + 1) + ": " + String.format("%.2f", value));
            }
        }

        return bestChild != null ? bestChild.move : game.getLegalMoves().get(0);
    }

    private Node treePolicy(Node node, ConnectFour simGame) { //run the tree policy
        while (!simGame.isTerminal()) { //while the game is not over
            if (!node.isFullyExpanded(simGame)) { //if the node is not fully expanded
                return expand(node, simGame); //expand the node
            } 
            else { //if the node is fully expanded
                node = getBestChild(node, C);
                if (node == null) { //if the node is null
                    return node;
                }
                simGame.makeMove(node.move); //make the move
            }
        }
        return node;
    }

    private Node expand(Node node, ConnectFour simGame) { //expand the node
        List<Integer> legalMoves = simGame.getLegalMoves(); //get the legal moves
        List<Integer> untriedMoves = new ArrayList<>(legalMoves); //create a list of untried moves
        
        // Remove moves that already have child nodes
        for (Node child : node.children) {
            untriedMoves.remove(Integer.valueOf(child.move));
        }

        if (untriedMoves.isEmpty()) { //if there are no untried moves
            return node;
        }

        int move = untriedMoves.get(random.nextInt(untriedMoves.size())); //get a random move
        Node newNode = new Node(node, move); //create a new node
        node.children.add(newNode); //add the new node to the children
        simGame.makeMove(move); //make the move

        if (outputMode.equals("Verbose")) { //print info
            System.out.println("NODE ADDED");
        }

        return newNode;
    }

    private Node getBestChild(Node node, double c) { //get the best child
        Node bestChild = null; //set the best child to null
        double bestValue = Double.NEGATIVE_INFINITY;

        for (Node child : node.children) { //for each child
            double ucbValue;//handle division by zero and calculate UCB value
            if (child.visits == 0) { //if the child has not been visited
                ucbValue = Double.POSITIVE_INFINITY;
            } 
            else { //if the child has been visited
                ucbValue = child.value / child.visits;
                if (c > 0) { //only add exploration term if c > 0
                    ucbValue += c * Math.sqrt(2 * Math.log(node.visits) / child.visits);
                }
            }

            if (outputMode.equals("Verbose") && c > 0) { //print UCB value
                System.out.println("UCB value for move " + (child.move + 1) + ": " + String.format("%.3f", ucbValue));
            }

            if (ucbValue > bestValue) { //if the UCB value is greater than the best value
                bestValue = ucbValue; //update the best value
                bestChild = child;  //update the best child
            }
        }

        if (outputMode.equals("Verbose") && bestChild != null) {  //print move selected
            System.out.println("Move selected: " + (bestChild.move + 1));
        }

        return bestChild;
    }

    private double defaultPolicy(ConnectFour simGame) {  //get the default policy
        while (!simGame.isTerminal()) {  //while the game is not over
            List<Integer> legalMoves = simGame.getLegalMoves();  //get the legal moves
            int move = legalMoves.get(random.nextInt(legalMoves.size()));  //get a random move
            simGame.makeMove(move);

            if (outputMode.equals("Verbose")) {  //print info
                System.out.println("Move selected: " + (move + 1));
            }
        }

        int result = simGame.getGameResult();  //get the result of the game
        if (outputMode.equals("Verbose")) {  //print info
            System.out.println("TERMINAL NODE VALUE: " + result);
        }
        return result;
    }

    private void backpropagate(Node node, double result) {
        while (node != null) {
            node.visits++;
            node.value -= result;  // Changed from += to -=
            result = -result;      // Flip the result for the parent node
            node = node.parent;
        }
    }

    private void printNodeInfo(Node node) {  //print the node info
        System.out.println("wi: " + node.value);  
        System.out.println("ni: " + node.visits);
        for (Node child : node.children) {  //print the child values
            double value = child.visits > 0 ? child.value / child.visits : 0;
            System.out.println("Column " + (child.move + 1) + ": " + String.format("%.2f", value));
        }
    }
}