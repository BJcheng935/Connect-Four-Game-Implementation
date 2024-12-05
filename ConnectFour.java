//Bungein J Cheng
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConnectFour {
    public static final int ROWS = 6; 
    public static final int COLS = 7;
    private static final char EMPTY = 'O';
    private static final char RED = 'R';
    private static final char YELLOW = 'Y';
    

    private char[][] board; // 6 rows, 7 columns
    private char currentPlayer; // 'R' or 'Y'
    private Random random; // For uniform random moves
    private String algorithm; // For printing purposes

    public static void main(String[] args) {
        try {
            if (args.length != 3) {
                System.out.println("Usage: java ConnectFour <input_file> <output_mode> <simulations>");
                return;
            }
    
            String inputFile = args[0]; // "input.txt"
            String outputMode = args[1]; // "Verbose" or "None"
            int simulations = Integer.parseInt(args[2]);
    
            ConnectFour game = new ConnectFour(); // Create a new ConnectFour game
            game.loadBoard(inputFile); // Load the board from the input file
    
            int selectedMove; // The selected move
            switch (game.algorithm) { // Run the selected algorithm
                case "UR":
                    selectedMove = game.uniformRandom();
                    break;
                case "PMCGS":
                    PMCGS pmcgs = new PMCGS(game, simulations, outputMode);
                    selectedMove = pmcgs.runPMCGS();
                    break;
                case "UCT":
                    UCT uct = new UCT(game, simulations, outputMode);
                    selectedMove = uct.runUCT();
                    break;
                default:
                    System.out.println("Unknown algorithm: " + game.algorithm);
                    return;
            }
    
            if (!outputMode.equals("None")) { // Print the selected move
                System.out.println("FINAL Move selected: " + (selectedMove + 1));
            }
        } catch (IOException e) { // Print an error message if the input file cannot be read
            System.out.println("Error reading input file: " + e.getMessage());
        } catch (NumberFormatException e) { // Print an error message if the simulations parameter is not an integer
            System.out.println("Error: Invalid number format for simulations parameter");
        } catch (Exception e) { // Print an error message if an unexpected error occurs
            System.out.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int uniformRandom() { // Uniform random move
        List<Integer> legalMoves = getLegalMoves(); // Get the list of legal moves
        if (legalMoves.isEmpty()) {// Check if the game is over
            return -1;
        }
        return legalMoves.get(random.nextInt(legalMoves.size())); // Get a random move
    }

    // Clone method for creating a copy of the game state
    public ConnectFour clone() {
        ConnectFour clonedGame = new ConnectFour();
        for (int i = 0; i < ROWS; i++) {
            System.arraycopy(this.board[i], 0, clonedGame.board[i], 0, COLS);
        }
        clonedGame.currentPlayer = this.currentPlayer;
        clonedGame.algorithm = this.algorithm;
        return clonedGame;
    }

    public ConnectFour() { // Constructor
        board = new char[ROWS][COLS]; // Create the board
        for (int i = 0; i < ROWS; i++) { // Initialize the board
            for (int j = 0; j < COLS; j++) {
                board[i][j] = EMPTY;
            }
        }
        random = new Random(); // Initialize the random number generator
    }

    public void loadBoard(String filename) throws IOException { // Load the board from a file
        BufferedReader reader = new BufferedReader(new FileReader(filename)); // Open the file
        algorithm = reader.readLine().trim(); // Read the algorithm
        currentPlayer = reader.readLine().charAt(0);
        
        for (int i = 0; i < ROWS; i++) { // Read the board
            String line = reader.readLine();
            for (int j = 0; j < COLS; j++) {
                board[i][j] = line.charAt(j);
            }
        }
        reader.close();
    }

    public List<Integer> getLegalMoves() { // Get the list of legal moves
        List<Integer> legalMoves = new ArrayList<>(); // Create a list to store the legal moves
        for (int col = 0; col < COLS; col++) { // Check if the column is empty
            if (board[0][col] == EMPTY) {
                legalMoves.add(col);
            }
        }
        return legalMoves;
    }

    public boolean makeMove(int col) {
        for (int row = ROWS - 1; row >= 0; row--) {
            if (board[row][col] == EMPTY) {
                board[row][col] = currentPlayer;
                // Switch players after move
                currentPlayer = (currentPlayer == RED) ? YELLOW : RED;
                return true;
            }
        }
        return false;
    }

    public boolean isTerminal() { // Check if the game is terminal
        // Check for a win
        if (checkWin(RED) || checkWin(YELLOW)) {
            return true;
        }
        
        // Check for a draw
        for (int col = 0; col < COLS; col++) {
            if (board[0][col] == EMPTY) {
                return false;
            }
        }
        return true;
    }

    private boolean checkWin(char player) { // Check for a win
        // Check horizontal
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS - 3; col++) {
                if (board[row][col] == player &&
                    board[row][col+1] == player &&
                    board[row][col+2] == player &&
                    board[row][col+3] == player) {
                    return true;
                }
            }
        }

        // Check vertical
        for (int row = 0; row < ROWS - 3; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == player &&
                    board[row+1][col] == player &&
                    board[row+2][col] == player &&
                    board[row+3][col] == player) {
                    return true;
                }
            }
        }

        // Check diagonal (top-left to bottom-right)
        for (int row = 0; row < ROWS - 3; row++) {
            for (int col = 0; col < COLS - 3; col++) {
                if (board[row][col] == player &&
                    board[row+1][col+1] == player &&
                    board[row+2][col+2] == player &&
                    board[row+3][col+3] == player) {
                    return true;
                }
            }
        }

        // Check diagonal (top-right to bottom-left)
        for (int row = 0; row < ROWS - 3; row++) {
            for (int col = 3; col < COLS; col++) {
                if (board[row][col] == player &&
                    board[row+1][col-1] == player &&
                    board[row+2][col-2] == player &&
                    board[row+3][col-3] == player) {
                    return true;
                }
            }
        }

        return false;
    }

    public int getGameResult() { // Get the game result
        if (checkWin(RED)) return -1; // Check for a win
        if (checkWin(YELLOW)) return 1; // Check for a loss
        return 0; // Check for a draw
    }

    public char[][] getBoard() {
        return board;
    }

   
}