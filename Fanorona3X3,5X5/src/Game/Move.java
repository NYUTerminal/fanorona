package Game;

import java.util.ArrayList;
import java.util.List;

/*
 * This class holds the start and end of any move . 
 * Along with the coordinates it also contains type of kill whether it is forward or withdrawal move.
 * 
 */
public class Move {

	public int startPointX;
	public int startPointY;
	public int endPointX;
	public int endPointY;
	public int valueOfMove;

	final int BLACKPIECE = -1;
	final int WHITEPIECE = 1;
	final int EMPTYSPOT = 0;
	// 1 if forward move -1 if withdrawal move
	public int typeOfMove;

	public Move(int startX, int startY, int endX, int endY, int typeMove) {

		this.startPointX = startX;
		this.startPointY = startY;
		this.endPointX = endX;
		this.endPointY = endY;
		valueOfMove = 0;
		this.typeOfMove = typeMove;
	}

	/*
	 * public String typeOfMove(){ int xDiff = endPointX - startPointX; int
	 * yDiff = endPointY - startPointY; // if (xDiff == 0 && yDiff > 0) { return
	 * "approach"; }if(xDiff == 0 && yDiff < 0){
	 * 
	 * }if(yDiff == 0 && xDiff > 0){ return "approach"; }if(yDiff == 0 && xDiff
	 * < 0){
	 * 
	 * }if(yDiff > 0 && xDiff > 0){ return "approach"; }if(yDiff < 0 && xDiff <
	 * 0){
	 * 
	 * }if(yDiff < 0 && xDiff > 0){
	 * 
	 * }if(yDiff > 0 && xDiff < 0){
	 * 
	 * } return null; }
	 */

	// Constructor
	public Move() {

		startPointX = 0;
		startPointY = 0;
		endPointX = 0;
		endPointY = 0;
		valueOfMove = 0;
	}

	// Copy constructor
	public Move(Move other) {

		this.startPointX = other.startPointX;
		this.startPointY = other.startPointY;
		this.endPointX = other.endPointX;
		this.endPointY = other.endPointY;
		this.valueOfMove = other.valueOfMove;
		this.typeOfMove = other.typeOfMove;
	}

	public int[][] newMove(int[][] gameBoard) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * This is an utility function to check whether every move made by player is
	 * valid move or not.
	 * 
	 * Every Move should be one step only. There should not be any piece in the
	 * destination .
	 */
	public boolean isOneStepMove(int[][] boardState, Move move,
			boolean isApproachMove) {

		// TODO Need to add destination move is zero or not .
		int xDiff = endPointX - startPointX;
		int yDiff = endPointY - startPointY;
		// Check player is moving correct piece or not
		if (boardState[startPointX][startPointY] != BoardProperties
				.getInstance().getPlayerColorInInt()) {
			return false;
		}
		BoardProperties instance = BoardProperties.getInstance();
		List<Point> killedPieces = getKilledPieces(
				boardState,
				isApproachMove,1);
		if (!killedPieces.isEmpty() && boardState[endPointX+1][endPointY+1] == 1) {
			System.out.println("Kill Moves are possible but making an pika move");
			return false;
		}

		// Player move is going to empty spot or not
		if (-2 < xDiff && xDiff < 2 && -2 < yDiff && yDiff < 2
				&& boardState[endPointX][endPointY] == 0) {
			return true;
		}
		return false;
	}

	/*
	 * This method will
	 */
	public List<Move> killMoves(int[][] boardState) {
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
					// approach kill check
					int xKillApproach = adjPoint.x + adjPoint.x
							- compPosition.x;
					int yKillApproach = adjPoint.y + adjPoint.y
							- compPosition.y;
					// Withdrawal kill check
					int xKillWithDrawal = adjPoint.x - adjPoint.x
							- compPosition.x;
					int yKillWithDrawal = adjPoint.y - adjPoint.y
							- compPosition.y;
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

	public List<Move> pikaMoves(int[][] boardState) {
		BoardProperties boardProperties = BoardProperties.getInstance();
		Color compColor = boardProperties.getCompColor();
		int turnColor = (compColor.equals(Color.BLACK)) ? -1 : 1;
		List<Point> currentTurnPositions = new ArrayList<Point>();
		int maxCol = boardProperties.getMAX_COL();
		int maxRow = boardProperties.getMAX_ROW();
		getCurrentPositions(boardState, turnColor, currentTurnPositions,
				maxCol, maxRow);
		List<Move> effectivePikaMoves = new ArrayList<Move>();
		List<Point> effectiveAdjPoints = new ArrayList<Point>();
		for (Point compPosition : currentTurnPositions) {
			List<Point> adjPoints = Point.getAdjPoints(compPosition);
			for (Point adjPoint : adjPoints) {
				if (boardState[adjPoint.getX()][adjPoint.getY()] == 0) {
					// approach kill check
					int xKillApproach = adjPoint.x + adjPoint.x
							- compPosition.x;
					int yKillApproach = adjPoint.y + adjPoint.y
							- compPosition.y;
					// Withdrawal kill check
					int xKillWithDrawal = adjPoint.x - adjPoint.x
							- compPosition.x;
					int yKillWithDrawal = adjPoint.y - adjPoint.y
							- compPosition.y;
					if (isOnTheBoard(maxCol, maxRow, xKillApproach,
							yKillApproach)
							&& (boardState[xKillApproach][yKillApproach] == 0 || boardState[xKillApproach][yKillApproach] == turnColor)
							&& isOnTheBoard(maxCol, maxRow, xKillWithDrawal,
									yKillWithDrawal)
							&& (boardState[xKillWithDrawal][yKillWithDrawal] == 0 || boardState[xKillWithDrawal][yKillWithDrawal] == turnColor)) {
						effectiveAdjPoints.add(adjPoint);
						effectivePikaMoves.add(new Move(compPosition.getX(),
								compPosition.getY(), adjPoint.getX(), adjPoint
										.getY(), 1));
					}
				}
			}
		}
		return effectivePikaMoves;
	}

	private boolean isOnTheBoard(int maxCol, int maxRow, int xKillApproach,
			int yKillApproach) {
		return xKillApproach >= 0 && xKillApproach < maxRow
				&& yKillApproach >= 0 && yKillApproach < maxCol;
	}

	/*
	 * This method will return the coordinates of the pieces killed by the
	 * current move . Takes Board State , kill type is approach or withdrawal
	 * move and which color Pieces should be killed .
	 */
	public List<Point> getKilledPieces(int[][] boardState,
			boolean isApproachMove, int colorToKill) {
		int xDiff = endPointX - startPointX;
		int yDiff = endPointY - startPointY;
		BoardProperties boardInstance = BoardProperties.getInstance();
		int maxY = boardInstance.getMAX_COL();
		int maxX = boardInstance.getMAX_ROW();
		// moved up and Approach or withdrawal capture.
		int tempX = 0;
		int tempY = 0;
		List<Point> piecesKilled = new ArrayList<Point>();
		if (isApproachMove) {
			tempX = endPointX;
			tempY = endPointY;
			tempX = tempX + xDiff;
			tempY = tempY + yDiff;
		} else {
			tempX = startPointX;
			tempY = startPointY;
			tempX = tempX - xDiff;
			tempY = tempY - yDiff;
		}
		/*
		 * Go one step every time and kill piece if the colorToKill is in the
		 * coordinates . Direction of KIll : UP Approach and Withdrawal moves
		 */
		if ((xDiff == 0 && yDiff > 0) || (xDiff == 0 && yDiff < 0)) {
			while (tempY < maxY && tempY >= 0) {
				if (boardState[tempX][tempY] == colorToKill) {
					piecesKilled.add(new Point(tempX, tempY));
					if (isApproachMove) {
						tempX = tempX + xDiff;
						tempY = tempY + yDiff;
					} else {
						tempX = tempX - xDiff;
						tempY = tempY - yDiff;
					}
				} else {
					return piecesKilled;
				}
			}
		}

		/*
		 * Go one step every time and kill piece if the colorToKill is in the
		 * coordinates . Direction of Kill : Horizontal Approach and Withdrawal
		 * moves
		 */
		else if ((yDiff == 0 && xDiff > 0) || (yDiff == 0 && xDiff < 0)) {
			while (tempX < maxX && tempX >= 0) {
				if (boardState[tempX][tempY] == colorToKill) {
					piecesKilled.add(new Point(tempX, tempY));
					if (isApproachMove) {
						tempX = tempX + xDiff;
						tempY = tempY + yDiff;
					} else {
						tempX = tempX - xDiff;
						tempY = tempY - yDiff;
					}
				} else {
					return piecesKilled;
				}
			}
		}
		/*
		 * Go one step every time and kill piece if the colorToKill is in the
		 * coordinates . Direction of Kill : Right Cross Up and down Approach
		 * and Withdrawal moves
		 */
		else if ((yDiff > 0 && xDiff > 0) || (yDiff < 0 && xDiff < 0)) {
			while (tempX < maxX && tempX >= 0 && tempY < maxY && tempY >= 0) {
				if (boardState[tempX][tempY] == colorToKill) {
					piecesKilled.add(new Point(tempX, tempY));
					if (isApproachMove) {
						tempX = tempX + xDiff;
						tempY = tempY + yDiff;
					} else {
						tempX = tempX - xDiff;
						tempY = tempY - yDiff;
					}
				} else {
					return piecesKilled;
				}
			}
		}
		/*
		 * Go one step every time and kill piece if the colorToKill is in the
		 * coordinates . Direction of Kill : Left cross up and down Approach and
		 * Withdrawal moves
		 */
		else if ((yDiff < 0 && xDiff > 0) || (yDiff > 0 && xDiff < 0)) {

			while (tempX < maxX && tempX >= 0 && tempY < maxY && tempY >= 0) {
				if (boardState[tempX][tempY] == colorToKill) {
					piecesKilled.add(new Point(tempX, tempY));
					if (isApproachMove) {
						tempX = tempX + xDiff;
						tempY = tempY + yDiff;
					} else {
						tempX = tempX - xDiff;
						tempY = tempY - yDiff;
					}
				} else {
					return piecesKilled;
				}
			}
		}
		return piecesKilled;
	}

	/*
	 * Given a board State it kills the pieces based on the current move and
	 * Updates the Board appropriately .
	 */
	public int[][] killPiecesAndUpdateBoard(int[][] boardState) {
		// Consider all AI kills are approach kills
		int colorToKill;
		boolean isApproachMove = true;
		if (boardState[startPointX][startPointY] == WHITEPIECE) {
			colorToKill = -1;
		} else {
			colorToKill = +1;
		}
		/*
		 * Calls above getKilledPieces method to get the co ordinates of the
		 * pieces to kill
		 */
		List<Point> killedPieces = getKilledPieces(boardState, isApproachMove,
				colorToKill);
		boardState[this.startPointX][this.startPointY] = 0;
		if (colorToKill == -1)
			boardState[this.endPointX][this.endPointY] = +1;
		else
			boardState[this.endPointX][this.endPointY] = -1;
		for (Point killPoint : killedPieces) {
			boardState[killPoint.getX()][killPoint.getY()] = 0;
		}
		return boardState;
	}

	@Override
	public String toString() {
		return "Move [startPointX=" + startPointX + ", startPointY="
				+ startPointY + ", endPointX=" + endPointX + ", endPointY="
				+ endPointY + ", valueOfMove=" + valueOfMove + ", BLACKPIECE="
				+ BLACKPIECE + ", WHITEPIECE=" + WHITEPIECE + ", EMPTYSPOT="
				+ EMPTYSPOT + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Move && ((Move) obj).startPointX == this.startPointX
				&& ((Move) obj).startPointY == this.startPointY
				&& ((Move) obj).endPointX == this.endPointX
				&& ((Move) obj).endPointY == this.endPointY) {
			return true;
		}
		return false;
	}

	/*
	 * HashCode to decide two Move Objects are same or not .
	 */
	@Override
	public int hashCode() {
		return this.startPointX + this.endPointX + this.startPointY
				+ this.endPointY;
	}

}
