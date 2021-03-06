package Game;

import java.util.ArrayList;
import java.util.List;

/*
 * To hold the x,y coordinated of a piece.
 */
public class Point {

	public int x;

	public int y;

	private boolean isBlack;

	public Point(int a, int b) {
		x = a;
		y = b;
	}

	public int[] getPosition() {
		int[] position = new int[2];
		position[0] = x;
		position[1] = y;
		return position;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isBlack() {
		return isBlack;
	}

	public void setBlack(boolean isBlack) {
		this.isBlack = isBlack;
	}

	public void movePieceToLeft(int minRow) {
		// Do check the col restriction.
		if (this.x < minRow) {
			this.x = this.x + 1;
		}
	}

	public void movePieceToRight(int maxRow) {
		if (y < maxRow) {
		}
	}

	public void movePieceToUp(int maxCol) {
		if (y < maxCol) {
		}
	}

	public void movePieceToDown(int minCol) {
		if (y > minCol) {
		}
	}

	public boolean isValidPoint(int maxRow, int maxCol) {
		if (this.x >= maxCol || this.y >= maxRow || this.x < 0 || this.y < 0)
			return false;
		return true;
	}

	/**
	 * removes all out of board points as well and returns effective points
	 * 
	 * @param currentPoint
	 * @param maxRow
	 * @param maxCol
	 * @return
	 */
	public static List<Point> getAdjPoints(Point currentPoint) {
		BoardProperties instance = BoardProperties.getInstance();
		int maxRow = instance.getMAX_ROW();
		int maxCol = instance.getMAX_COL();
		List<Point> adjacentPoints = new ArrayList<Point>();
		adjacentPoints.add(new Point(currentPoint.x, currentPoint.y + 1));
		adjacentPoints.add(new Point(currentPoint.x, currentPoint.y - 1));
		adjacentPoints.add(new Point(currentPoint.x + 1, currentPoint.y));
		adjacentPoints.add(new Point(currentPoint.x - 1, currentPoint.y));
		// diagonals
		if (isDiagonalMovesPossible(currentPoint)) {
			adjacentPoints
					.add(new Point(currentPoint.x + 1, currentPoint.y + 1));
			adjacentPoints
					.add(new Point(currentPoint.x + 1, currentPoint.y - 1));
			adjacentPoints
					.add(new Point(currentPoint.x - 1, currentPoint.y + 1));
			adjacentPoints
					.add(new Point(currentPoint.x - 1, currentPoint.y - 1));
		}
		List<Point> updatedAdjList = new ArrayList<Point>();
		for (Point p : adjacentPoints) {
			if (p.isValidPoint(maxRow, maxCol)) {
				updatedAdjList.add(p);
			}
		}
		return updatedAdjList;
	}

	private static Boolean isDiagonalMovesPossible(Point currentPoint) {// {{{
		// (odd,odd) or (even,even)
		return (currentPoint.x % 2 == 1 && currentPoint.y % 2 == 1)
				|| (currentPoint.x % 2 == 0 && currentPoint.y % 2 == 0);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + ", isBlack=" + isBlack + "]";
	}

}
