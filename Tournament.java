//Bungein J Cheng
import java.util.List;

public class Tournament {
    private static final int GAMES_PER_MATCH = 100;
    private static final String[] ALGORITHMS = {"UR", "PMCGS-500", "PMCGS-10000", "UCT-500", "UCT-10000"};
    private int[][] wins;

    public Tournament() {  
        wins = new int[ALGORITHMS.length][ALGORITHMS.length];
    }

    public void runTournament() { //run tournament
        System.out.println("Starting tournament:\n");
        int totalGames = ALGORITHMS.length * (ALGORITHMS.length - 1) * GAMES_PER_MATCH;
        int gamesPlayed = 0;
        
        for (int i = 0; i < ALGORITHMS.length; i++) { //play games
            for (int j = 0; j < ALGORITHMS.length; j++) { 
                if (i != j) { 
                    System.out.printf("\nPlaying %s vs %s\n", ALGORITHMS[i], ALGORITHMS[j]); 
                    wins[i][j] = playMatch(ALGORITHMS[i], ALGORITHMS[j]); 
                    gamesPlayed += GAMES_PER_MATCH;
                    System.out.printf("Progress: %d/%d games completed (%.1f%%)\n", 
                        gamesPlayed, totalGames, (gamesPlayed * 100.0) / totalGames);
                }
            }
        }
        printResults(); //get results
    }

    private int playMatch(String algo1, String algo2) { 
        int firstPlayerWins = 0;
        String[] algo1Parts = algo1.split("-"); //get algorithm parts
        String[] algo2Parts = algo2.split("-"); 

        for (int game = 0; game < GAMES_PER_MATCH; game++) {  //play 100 games
            ConnectFour gameState = new ConnectFour(); //create new game
            boolean isFirstPlayerTurn = true; //set first player
            boolean gameOver = false; //check if game is over

            while (!gameState.isTerminal() && !gameOver) {//play until game is over
                String[] currentAlgoParts = isFirstPlayerTurn ? algo1Parts : algo2Parts;
                int move = getAlgoMove(currentAlgoParts, gameState.clone()); 

                
                List<Integer> legalMoves = gameState.getLegalMoves();//check for invalid moves
                if (move < 0 || move >= ConnectFour.COLS || !legalMoves.contains(move)) {
                    gameOver = true;
                    if (!isFirstPlayerTurn) {//check if game is over
                        firstPlayerWins++;
                    }
                    continue;
                }

                gameState.makeMove(move); //make move
                isFirstPlayerTurn = !isFirstPlayerTurn; //switch players
            }

            if (!gameOver && gameState.isTerminal()) {// Check final game state
                int result = gameState.getGameResult();
                if (result == -1) { // Red first player wins
                    firstPlayerWins++;
                } 
            }//  result == 1 means Yellow second player wins, 0 means draw

            if (game % 10 == 0) {// Print progress every 10 games
                String status = firstPlayerWins > game/2 ? "leading" : "trailing";
                System.out.printf("Game %d: %s (%d/%d)\n",game, status, firstPlayerWins, game + 1);
                    
            }
        }
        return firstPlayerWins;
    }

    private int getAlgoMove(String[] algoParts, ConnectFour game) { 
        String algoName = algoParts[0]; //get algorithm name
        int simulations = algoParts.length > 1 ? Integer.parseInt(algoParts[1]) : 0;

        try { //try to get move
            switch (algoName) { 
                case "UR":
                    return game.uniformRandom();
                case "PMCGS":
                    return new PMCGS(game, simulations, "None").runPMCGS();
                case "UCT":
                    return new UCT(game, simulations, "None").runUCT();
                default:
                    System.err.println("Unknown algorithm: " + algoName);
                    return -1;
            }
        } catch (Exception e) {
            System.err.printf("Error in %s: %s\n", algoName, e.getMessage());
            return -1;
        }
    }

    private void printResults() { // print results
        System.out.println("\nTournament Results:");
        System.out.println("Winning percentages (row vs column):\n");
        
        System.out.printf("%-12s", "");// Print header
        for (String algo : ALGORITHMS) {
            System.out.printf("%-12s", algo);
        }
        System.out.println("\n" + "-".repeat(12 * (ALGORITHMS.length + 1)));
    
        for (int i = 0; i < ALGORITHMS.length; i++) {// Print results in a chart
            System.out.printf("%-12s", ALGORITHMS[i]); // Print algorithm name
            for (int j = 0; j < ALGORITHMS.length; j++) { // Print win percentage
                if (i == j) {
                    System.out.printf("%-12s", "-"); // Print "-" for diagonal
                } 
                else { // Print win percentage
                    double winPercentage = (wins[i][j] * 100.0) / GAMES_PER_MATCH;
                    System.out.printf("%-12.1f", winPercentage);
                }
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Tournament tournament = new Tournament();
        tournament.runTournament();
    }
}