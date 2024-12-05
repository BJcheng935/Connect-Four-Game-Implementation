//Bungein J Cheng
//Algorithm:Pure Monte Carlo Game Search(PMCGS)
import java.util.*;

public class PMCGS {
    private ConnectFour game;
    private Random random;
    private int simulations;
    private String outputMode;

    public PMCGS(ConnectFour game, int simulations, String outputMode) {
        this.game = game;
        this.random = new Random();
        this.simulations = simulations;
        this.outputMode = outputMode;
    }

    public int runPMCGS() {
        List<Integer> legalMoves = game.getLegalMoves();
        if (legalMoves.isEmpty()) {
            return -1;
        }

        double[] moveValues = new double[ConnectFour.COLS];
        int[] moveCounts = new int[ConnectFour.COLS];

        // Run simulations
        for (int i = 0; i < simulations; i++) {
            ConnectFour simGame = game.clone();
            int move = legalMoves.get(random.nextInt(legalMoves.size()));
            
            // Make the selected move
            simGame.makeMove(move);

            // Get result from current player's perspective
            int result = simulateRandomPlayout(simGame);
            
            // Update statistics
            moveValues[move] += result;
            moveCounts[move]++;

            if (outputMode.equals("Verbose")) {
                System.out.println("\nSimulation " + (i + 1) + ":");
                System.out.println("Move selected: " + (move + 1));
                System.out.println("Result: " + result);
                System.out.println("Updated values:");
                for (int j = 0; j < ConnectFour.COLS; j++) {
                    if (moveCounts[j] > 0) {
                        System.out.println("Column " + (j + 1) + ": " + moveValues[j] + "/" + moveCounts[j]);
                    }
                }
            }
        }

        // Select best move based on average values
        int bestMove = -1;
        double bestValue = Double.NEGATIVE_INFINITY;

        if (outputMode.equals("Verbose") || outputMode.equals("Brief")) {
            System.out.println("\nFinal move values:");
        }

        for (int i = 0; i < ConnectFour.COLS; i++) {
            if (moveCounts[i] > 0) {
                double value = moveValues[i] / moveCounts[i];
                if (outputMode.equals("Verbose") || outputMode.equals("Brief")) {
                    System.out.println("Column " + (i + 1) + ": " + String.format("%.2f", value));
                }
                // For the current player, higher values are better
                if (value > bestValue) {
                    bestValue = value;
                    bestMove = i;
                }
            } else if (outputMode.equals("Verbose") || outputMode.equals("Brief")) {
                System.out.println("Column " + (i + 1) + ": Null");
            }
        }

        return bestMove;
    }

    private int simulateRandomPlayout(ConnectFour simGame) {
        int moveCount = 0;
        while (!simGame.isTerminal() && moveCount < 100) { // Added move limit to prevent infinite games
            List<Integer> legalMoves = simGame.getLegalMoves();
            if (legalMoves.isEmpty()) {
                break;
            }
            int move = legalMoves.get(random.nextInt(legalMoves.size()));
            simGame.makeMove(move);
            moveCount++;

            if (outputMode.equals("Verbose")) {
                System.out.println("Playout move: " + (move + 1));
            }
        }

        int result = simGame.getGameResult();
        if (outputMode.equals("Verbose")) {
            System.out.println("TERMINAL NODE VALUE: " + result);
        }
        return result;
    }
}