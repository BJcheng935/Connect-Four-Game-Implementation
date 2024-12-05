//Bungein J Cheng
import java.util.InputMismatchException;
import java.util.Scanner;

public class Player {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ConnectFour game = new ConnectFour();
        boolean isPlayerTurn = true;  // Player plays first

        System.out.println("Select AI opponent:");// Select AI opponent
        System.out.println("1. UCT (500 simulations)");
        System.out.println("2. UCT (10000 simulations)");
        int choice = scanner.nextInt();
        int simulations = choice == 1 ? 500 : 10000; // 500 or 10000 simulations

        while (!game.isTerminal()) { // While the game is not over
            printBoard(game.getBoard());
            System.out.println("Current player: " + (isPlayerTurn ? "Player (Yellow)" : "AI (Red)"));
            
            int move; // Move chosen by player or AI
            if (isPlayerTurn) { // Player's turn
                move = getMov(scanner, game);
            } 
            else { // AI's turn
                System.out.println("AI is thinking..."); 
                UCT ai = new UCT(game, simulations, "None"); // Run UCT algorithm
                move = ai.runUCT(); // Select best move
                System.out.println("AI plays column " + (move + 1));
            }

            game.makeMove(move); // Make the move
            isPlayerTurn = !isPlayerTurn; // Switch player
        }

        printBoard(game.getBoard());// Print final board and result
        int result = game.getGameResult();
        if (result == 1 && !isPlayerTurn) { // Check result
            System.out.println("You win!");
        } 
        else if (result == -1 && isPlayerTurn) { 
            System.out.println("AI wins!");
        } 
        else {
            System.out.println("It's a draw!");
        }
        
        scanner.close();
    }

    private static void printBoard(char[][] board) { // Print the board
        System.out.println("\n 1 2 3 4 5 6 7  (column numbers)");
        for (int i = 0; i < ConnectFour.ROWS; i++) { 
            System.out.print("|");
            for (int j = 0; j < ConnectFour.COLS; j++) {
                System.out.print(board[i][j] + "|");
            }
            System.out.println();
        }
        System.out.println("-".repeat(15));
    }

    private static int getMov(Scanner scanner, ConnectFour game) { // Get move
        while (true) { // Loop until a valid move is chosen
            System.out.print("Your move (1-7): ");
            try { // Try to get a valid move
                int col = scanner.nextInt() - 1;
                if (col >= 0 && col < 7 && game.getLegalMoves().contains(col)) { // Check if move is valid
                    return col;
                }
                System.out.println("Invalid move. Column must be between 1-7 and not full.");
            } catch (InputMismatchException e) { // If input is not an integer
                System.out.println("Please enter a number between 1 and 7.");
                scanner.nextLine(); // Clear the invalid input
            }
        }
    }
}