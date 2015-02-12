package Game;

import java.util.ArrayList;
import java.util.List;

public class Controller {

	private final int smallBoard = 3;
	private final int largeBoard = 5;
	private List<int[][]> boardStateTransition = new ArrayList<int[][]>();
	BoardProperties bP = BoardProperties.getInstance();

	public Controller() {

	}

	public List<int[][]> getBoardStateTransition() {
		return boardStateTransition;
	}

	public void setBoardStateTransition(List<int[][]> boardStateTransition) {
		this.boardStateTransition = boardStateTransition;
	}

	public Color getOtherColor(String input) {
		if (input.equals(Color.WHITE.toString()))
			return Color.BLACK;
		else
			return Color.WHITE;
	}

	public int getColorInInt(Color input) {
		if (input.equals(Color.WHITE))
			return +1;
		else
			return -1;
	}

	/*
	 * Initialization function to initialize the board as per the user
	 * requirements . This method takes sizeOfBoard u want to start and
	 * playerColor as input .
	 */
	public int[][] initializeBoard(int sizeOfBoard, String playerColor) {
		if (sizeOfBoard == 1) {
			bP.setMAX_COL(smallBoard);
			bP.setMAX_ROW(smallBoard);
			// bP.setCompColor(getOtherColor(playerColor));
			// bP.setPlayerColor(getOtherColor(bP.getCompColor().toString()));

		} else {
			bP.setMAX_COL(largeBoard);
			bP.setMAX_ROW(largeBoard);

		}
		if (playerColor.equals(Color.WHITE.toString())) {
			bP.setPlayerColor(Color.WHITE);
			bP.setCompColor(getOtherColor(playerColor));
		} else {
			bP.setPlayerColor(Color.BLACK);
			bP.setCompColor(getOtherColor(playerColor));
		}
		/*
		 * Place pieces accordingly on the board based one the location .Blacks
		 * are represented by -1 and Whites are represented by +1
		 */
		int[][] boardState = new int[bP.getMAX_ROW()][bP.getMAX_COL()];
		boolean flip = true;
		for (int i = 0; i < bP.getMAX_ROW(); i++) {
			for (int j = 0; j < bP.getMAX_COL(); j++) {
				int mid = bP.getMAX_ROW() / 2;
				if (i < mid) {
					boardState[i][j] = -1;
				} else if (i > mid) {
					boardState[i][j] = +1;
				} else if (i == mid && i != j) {
					if (flip) {
						boardState[i][j] = -1;
					} else {
						boardState[i][j] = +1;
					}
					if (flip)
						flip = false;
					else
						flip = true;
				} else if (i == mid && i == j) {
					boardState[i][j] = 0;
				}
			}
		}
		bP.setBoardState(boardState);
		return boardState;
	}

	// Evaluate Function :
	/*
	 * It gives the difference between number of white coins and black coins .
	 */
	public int evaluateFunction(int[][] gameState) {
		int value = 0;
		if (BoardProperties.getInstance().getPlayerColor().equals(Color.WHITE)) {
			for (int x = 0; x < bP.getMAX_ROW(); x++) {
				for (int y = 0; y < bP.getMAX_COL(); y++) {
					if (gameState[x][y] == 1)
						value++;
					if (gameState[x][y] == -1)
						value--;
				}
			}
		} else {
			for (int x = 0; x < bP.getMAX_ROW(); x++) {
				for (int y = 0; y < bP.getMAX_COL(); y++) {
					if (gameState[x][y] == -1)
						value++;
					if (gameState[x][y] == 1)
						value--;
				}
			}
		}
		return value;
	}

	/*
	 * This method checks whether any one of the color pieces are empty on the
	 * board.
	 */
	public boolean checkGameOver(int[][] boardState) {
		int blackPieces = 0;
		int whitePieces = 0;
		for (int i = 0; i < this.bP.getMAX_ROW(); i++) {
			for (int j = 0; j < this.bP.getMAX_COL(); j++) {
				if (boardState[i][j] == -1) {
					blackPieces++;
				} else if (boardState[i][j] == +1) {
					whitePieces++;
				}
			}
		}
		if (whitePieces == 0 && blackPieces != 0) {
			System.out.println("Black Pieces Player won");
		}
		if (blackPieces == 0 && whitePieces != 0) {
			System.out.println("White Pieces Player won");
		}
		if (whitePieces == 0 | blackPieces == 0) {
			return true;
		} else
			return false;

	}

	/*
	 * private void placePiecesOnBoard() {
	 * 
	 * }
	 */

	/*
	 * To print the current state of the board .It represents Black Pieces with
	 * 'X' and White Pieces with 'O'
	 */
	public void printCurrestStateOfBoard(int[][] boardState) {
		for (int i = 0; i < bP.getMAX_ROW(); i++) {
			for (int j = 0; j < bP.getMAX_COL(); j++) {
				if (boardState[i][j] > 0) {
					System.out.print(" W ");
					// System.out.print(" W"+"("+i+","+j+") ");
				} else if (boardState[i][j] == 0) {
					System.out.print(" - ");
					// System.out.print(" #"+"("+i+","+j+") ");
				} else if (boardState[i][j] < 0) {
					System.out.print(" B ");
					// System.out.print(" B"+"("+i+","+j+") ");
				}
			}
			System.out.println("\n");
		}
	}

	/*
	 * Engine logic to compute moves simultaniously . After Player move it will
	 * trigger alpha beta to get the next possible move and make changes to the
	 * board .Along with that it checks for all validations . Right move or not
	 * .Valid turn or not .
	 */

	public int[][] makeAMove(int[][] boardState, Color playerColor, Move move,
			boolean isApproachMove) {
		if (move.isOneStepMove(boardState,move,isApproachMove)) {
			// Kill computer pieces and apply alpha beta.
			// Human move so kill color will be comp color.
			updatedBoard(boardState, move, isApproachMove, bP.getCompColor());
			System.out.println("After Human Move: \n");
			printCurrestStateOfBoard(boardState);
			if (checkGameOver(boardState)) {
				System.out.println("************ Game Over ***********");
				return null;
			}

			AlphaBetaPruning ab = new AlphaBetaPruning();
			Move nextPossibleMove = ab.getNextPossibleMove(boardState);
			// Comp move so kill color would be player color
			boolean isApproachMoveForComputer = nextPossibleMove.typeOfMove == 1 ? true
					: false;
			int[][] updatedBoard = updatedBoard(boardState, nextPossibleMove,
					isApproachMoveForComputer, playerColor);
			System.out.println("After Computer Move: \n");
			printCurrestStateOfBoard(boardState);
			if (checkGameOver(boardState)) {
				System.out.println("************ Game Over ***********");
				return null;
			}
			// BoardProperties.getInstance().setBoardState(updatedBoard);
			return updatedBoard;
		} else {
			System.out
					.println("Invalid Move.Please Make a valid move.Please read game rules !!");
		}

		return null;
	}

	/*
	 * After we get the next possible move made by computer or move made by
	 * player , this method makes the corresponding changes to the board . Ex:
	 * Killing pieces,Moving piece,updating the board, saving the board state in
	 * singleton instance.
	 */

	public int[][] updatedBoard(int[][] boardState, Move move,
			boolean isApproachMove, Color killColor) {
		int colorToKill = (killColor.equals(Color.BLACK)) ? -1 : 1;
		// TODO Auto-generated method stub
		List<Point> killedPieces = move.getKilledPieces(boardState,
				isApproachMove, colorToKill);

		for (Point killPoint : killedPieces) {
			boardState[killPoint.getX()][killPoint.getY()] = 0;
		}
		boardState[move.startPointX][move.startPointY] = 0;
		if (killColor.equals(Color.BLACK))
			boardState[move.endPointX][move.endPointY] = +1;
		else
			boardState[move.endPointX][move.endPointY] = -1;

		// BoardProperties.getInstance().setBoardState(boardState);

		boardStateTransition.add(mapValues(boardState));
		/*
		 * boardState = move.killPiecesAndUpdateBoard(boardState,
		 * isApproachMove, getColorInInt(colorToKill));
		 */
		return boardState;
	}

	private int[][] mapValues(int[][] boardState) {
		int size = boardState[0].length;
		int[][] nextGameState = new int[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				nextGameState[i][j] = boardState[i][j];
			}
		}
		return nextGameState;
	}

}
