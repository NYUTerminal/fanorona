package Game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class AlphaBetaPruning {

	private int depthReached = 0;

	/*
	 * When it is computers turn it has to predict the best possible move from
	 * the current state . This Method triggers the ALPHA - BETA algorithm to
	 * get the best possible move.
	 */
	public Move getNextPossibleMove(int[][] boardState) {
		// returns a Move which is 2 pts: start & end
		int min = 0;
		int max = 0;
		AlphaBetaPruning alphaBetaPruning = new AlphaBetaPruning();
		/*
		 * Check if there are any kill moves possible . If no kill moves do a
		 * pika move.
		 */
		List<Move> captureMoves = getValidKillMoves(boardState);
		if (captureMoves.size() != 0) {
			return alphaBetaPruning.startMinMaxAlgo(boardState, captureMoves);
		} else {
			Random rand = new Random();
			int randomNum = 0;
			/*
			 * Select a random pika move from the pika move list
			 */
			List<Move> paikaMoves = getValidPikaMoves(boardState);
			if (paikaMoves.size() > 1) {
				max = paikaMoves.size() - 1;
				randomNum = rand.nextInt(max - min + 1) + min;
			} else if (paikaMoves.size() == 1) {
				randomNum = 0;
			}
			if (!paikaMoves.isEmpty()) {
				return null;
			}
			Move bestMove = paikaMoves.get(randomNum);
			return bestMove;
		}
	}

	/*
	 * MinMax method to get the next best move by selecting the move with
	 * maximum value associated with it.
	 */
	private Move startMinMaxAlgo(int[][] boardState, List<Move> possibleMoveList) {
		int[][] nextGameState = new int[boardState[0].length][boardState[0].length];
		int value, maxValue = Integer.MIN_VALUE;
		Move bestMove = new Move();

		// possibleMoveList = getValidKillMoves(boardState);
		if (possibleMoveList.size() != 0)
			bestMove = new Move(possibleMoveList.get(0));

		for (Move possibleMove : possibleMoveList) {

			mapValues(boardState, nextGameState);
			// nextGameState = (int[][]) boardState.clone();
			nextGameState = possibleMove
					.killPiecesAndUpdateBoard(nextGameState);
			// nextGameState = possibleMove.newMove(boardState);
			value = minMove(nextGameState, BoardProperties.getInstance()
					.getMaxDepthOfMinMax(), maxValue, Integer.MAX_VALUE);
			if (value > maxValue) {
				// System.out.println ("Max value : " + value +
				// " at depth : 0");
				maxValue = value;
				bestMove = new Move(possibleMove);
			}
		}
		return bestMove;
	}

	/*
	 * Max method to maximize the value of the tree at the node
	 */
	private int maxMove(int[][] boardState, int depth, int alpha, int beta) {
		Controller engine = new Controller();
		if (checkGameOver(boardState) || depth <= 0) {
			if (depthReached < Integer.MAX_VALUE - depth) {
				depthReached = Integer.MAX_VALUE - depth;
			}
			return engine.evaluateFunction(boardState);
		}
		int[][] nextGameState = new int[boardState[0].length][boardState[0].length];
		int value;
		List<Move> possibleMoveList = getValidKillMoves(boardState);
		// Return if there are no more possible moves and depth havent reached
		// and one of the player has to move pika move .So return evaluate
		// function
		/*
		 * if (possibleMoveList.isEmpty()) { return
		 * engine.evaluateFunction(boardState); }
		 */
		for (Move possibleMove : possibleMoveList) {
			mapValues(boardState, nextGameState);
			// nextGameState = (int[][]) boardState.clone();
			nextGameState = possibleMove
					.killPiecesAndUpdateBoard(nextGameState);
			value = minMove(nextGameState, depth - 1, alpha, beta);

			if (value > alpha) {
				alpha = value;
			}

			if (alpha > beta) {
				return beta;
			}

		}
		return alpha;
	}

	private int minMove(int[][] boardState, int depth, int alpha, int beta) {
		Controller engine = new Controller();
		if (checkGameOver(boardState) || depth <= 0) {
			if (depthReached < Integer.MAX_VALUE - depth) {
				depthReached = Integer.MAX_VALUE - depth;
			}
			return engine.evaluateFunction(boardState);
		}

		int[][] nextGameState = new int[boardState[0].length][boardState[0].length];
		int value;
		List<Move> possibleMoveList = getValidKillMoves(boardState);
		// Return if there are no more possible moves and depth havent reached
		// and one of the player has to move pika move .So return evaluate
		// function
		/*
		 * if (possibleMoveList.isEmpty()) { return
		 * engine.evaluateFunction(boardState); }
		 */
		// System.out.println ("Min node at depth : " + depth + " with alpha : "
		// + alpha + " beta : " + beta);

		for (Move possibleMove : possibleMoveList) {

			// nextGameState = (int[][]) boardState.clone();
			mapValues(boardState, nextGameState);
			nextGameState = possibleMove.killPiecesAndUpdateBoard(boardState);
			// nextGameState = possibleMove.newMove(boardState);
			value = maxMove(nextGameState, depth - 1, alpha, beta);

			if (value < beta) {
				beta = value;
				// System.out.println("Min value : " + value + " at depth : "
				// + depth);
			}

			if (beta < alpha) {
				// System.out.println("Min value with pruning : " + alpha
				// + " at depth : " + depth);
				return alpha;
			}
		}

		// System.out.println("Min value selected : " + beta + " at depth : "
		// + depth);
		return beta;
	}

	/*
	 * To check whether the game has reached end state or not . Straight forward
	 * implementation.We can check for the game draw scnearios as well .
	 */
	public boolean checkGameOver(int[][] boardState) {
		BoardProperties bP = BoardProperties.getInstance();
		int blackPieces = 0;
		int whitePieces = 0;
		for (int i = 0; i < bP.getMAX_ROW(); i++) {
			for (int j = 0; j < bP.getMAX_COL(); j++) {
				if (boardState[i][j] == -1) {
					blackPieces++;
				} else if (boardState[i][j] == +1) {
					whitePieces++;
				}
			}
		}
		if (whitePieces == 0 | blackPieces == 0) {
			return true;
		} else
			return false;

	}

	/*
	 * To get All the kill moves possible at the current state .All direction
	 * kills . Records all moves which can at least make one kill of opponent
	 * pieces.
	 */
	private List<Move> getValidKillMoves(int[][] boardState) {
		BoardProperties boardProperties = BoardProperties.getInstance();
		Color compColor = boardProperties.getCompColor();
		int turnColor = (compColor.equals(Color.BLACK)) ? -1 : 1;
		int killColor = (compColor.equals(Color.BLACK)) ? +1 : -1;
		List<Point> currentTurnPositions = new ArrayList<Point>();
		int maxCol = boardProperties.getMAX_COL();
		int maxRow = boardProperties.getMAX_ROW();
		getCurrentPositions(boardState, turnColor, currentTurnPositions,
				maxCol, maxRow);
		List<Move> effectiveKillMoves = new ArrayList<Move>();
		List<Point> effectiveAdjPoints = new ArrayList<Point>();
		for (Point compPosition : currentTurnPositions) {
			List<Point> adjPoints = Point.getAdjPoints(compPosition);
			for (Point adjPoint : adjPoints) {
				if (boardState[adjPoint.getX()][adjPoint.getY()] == 0) {
					// approach kill check.To check from current position
					// approach kill is possible or not.
					int xKillApproach = adjPoint.x + adjPoint.x
							- compPosition.x;
					int yKillApproach = adjPoint.y + adjPoint.y
							- compPosition.y;
					// Withdrawal kill check.To check from current position
					// withdrawal kill is possible or not.
					int xKillWithDrawal = compPosition.x
							- (adjPoint.x - compPosition.x);
					int yKillWithDrawal = compPosition.y
							- (adjPoint.y - compPosition.y);
					if (isOnTheBoard(maxCol, maxRow, xKillApproach,
							yKillApproach)
							&& boardState[xKillApproach][yKillApproach] == killColor) {
						effectiveAdjPoints.add(adjPoint);
						effectiveKillMoves.add(new Move(compPosition.getX(),
								compPosition.getY(), adjPoint.getX(), adjPoint
										.getY(), 1));
					} else if (isOnTheBoard(maxCol, maxRow, xKillWithDrawal,
							yKillWithDrawal)
							&& boardState[xKillWithDrawal][yKillWithDrawal] == killColor) {
						effectiveAdjPoints.add(adjPoint);
						effectiveKillMoves.add(new Move(compPosition.getX(),
								compPosition.getY(), adjPoint.getX(), adjPoint
										.getY(), -1));

					}
				}
			}
		}
		return effectiveKillMoves;
	}

	/*
	 * Method to give all pika moves possible at the situation . returns all
	 * possible moves from which there is no possibility of kills.
	 */
	private List<Move> getValidPikaMoves(int[][] boardState) {
		BoardProperties boardProperties = BoardProperties.getInstance();
		Color compColor = boardProperties.getCompColor();
		int turnColor = (compColor.equals(Color.BLACK)) ? -1 : 1;
		List<Point> currentTurnPositions = new ArrayList<Point>();
		int maxCol = boardProperties.getMAX_COL();
		int maxRow = boardProperties.getMAX_ROW();
		getCurrentPositions(boardState, turnColor, currentTurnPositions,
				maxCol, maxRow);
		HashSet<Move> effectivePikaSet = new HashSet<Move>();
		List<Point> effectiveAdjPoints = new ArrayList<Point>();
		for (Point compPosition : currentTurnPositions) {
			List<Point> adjPoints = Point.getAdjPoints(compPosition);
			for (Point adjPoint : adjPoints) {
				if (boardState[adjPoint.getX()][adjPoint.getY()] == 0) {
					// approach kill check.To check from current position
					// approach kill is possible or not.
					int xKillApproach = adjPoint.x + adjPoint.x
							- compPosition.x;
					int yKillApproach = adjPoint.y + adjPoint.y
							- compPosition.y;
					// Withdrawal kill check.To check from current position
					// approach kill is possible or not.
					int xKillWithDrawal = adjPoint.x - adjPoint.x
							- compPosition.x;
					int yKillWithDrawal = adjPoint.y - adjPoint.y
							- compPosition.y;

					if ((!isOnTheBoard(maxCol, maxRow, xKillApproach,
							yKillApproach) && isOnTheBoard(maxCol, maxRow,
							adjPoint.x, adjPoint.y))
							|| (isOnTheBoard(maxCol, maxRow, xKillWithDrawal,
									yKillWithDrawal) && isOnTheBoard(maxCol,
									maxRow, adjPoint.x, adjPoint.y))) {
						effectiveAdjPoints.add(adjPoint);
						effectivePikaSet.add(new Move(compPosition.getX(),
								compPosition.getY(), adjPoint.getX(), adjPoint
										.getY(), 1));
						continue;
					}

					if (isOnTheBoard(maxCol, maxRow, xKillApproach,
							yKillApproach)
							&& (boardState[xKillApproach][yKillApproach] == 0 || boardState[xKillApproach][yKillApproach] == turnColor)) {
						effectiveAdjPoints.add(adjPoint);
						effectivePikaSet.add(new Move(compPosition.getX(),
								compPosition.getY(), adjPoint.getX(), adjPoint
										.getY(), 1));

					}
					if (isOnTheBoard(maxCol, maxRow, xKillWithDrawal,
							yKillWithDrawal)
							&& (boardState[xKillWithDrawal][yKillWithDrawal] == 0 || boardState[xKillWithDrawal][yKillWithDrawal] == turnColor)) {
						effectiveAdjPoints.add(adjPoint);
						effectivePikaSet.add(new Move(compPosition.getX(),
								compPosition.getY(), adjPoint.getX(), adjPoint
										.getY(), -1));
					}
				}
			}
		}
		// Set to remove any duplicates
		List<Move> effectivePikaList = new ArrayList<Move>();
		for (Move move : effectivePikaSet) {
			effectivePikaList.add(move);
		}
		return effectivePikaList;
	}

	/*
	 * to check whether piece moved is on the board or not .
	 */
	private boolean isOnTheBoard(int maxCol, int maxRow, int xKillApproach,
			int yKillApproach) {
		return xKillApproach >= 0 && xKillApproach < maxRow
				&& yKillApproach >= 0 && yKillApproach < maxCol;
	}

	private void mapValues(int[][] boardState, int[][] nextGameState) {
		int loopSize = boardState[0].length;
		for (int i = 0; i < loopSize; i++) {
			for (int j = 0; j < loopSize; j++) {
				nextGameState[i][j] = boardState[i][j];
			}
		}
	}

	/*
	 * To get points of all the pieces on the board to calculate kill and pika
	 * moves
	 */
	private void getCurrentPositions(int[][] boardState, int turnColor,
			List<Point> currentTurnPositions, int maxCol, int maxRow) {
		for (int i = 0; i < maxCol; i++) {
			for (int j = 0; j < maxRow; j++) {
				if (boardState[i][j] == turnColor) {
					currentTurnPositions.add(new Point(i, j));
				}
			}
		}
	}
}
