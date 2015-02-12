package Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * This class is a singleton instance to hold the global variables like max size of the board .
 * What color does the player choose.
 * It also records all the steps that took place till the game over . To see how the game proceeded from beginning to end.
 */
public class BoardProperties {

	private int MAX_ROW;

	private int MAX_COL;

	private Color playerColor;

	private Color compColor;

	private static BoardProperties boardProperties;

	private int[][] boardState;
	
	private int maxDepthOfMinMax;

	private List<int[][]> boardStateTransition = new ArrayList<int[][]>();

	private BoardProperties() {

	}

	public void addBoardState(int[][] boardState) {
		boardStateTransition.add(boardState);
	}

	public List<int[][]> getBoardStateTransitions() {
		return boardStateTransition;
	}

	public synchronized static BoardProperties getInstance() {
		if (boardProperties == null)
			boardProperties = new BoardProperties();
		return boardProperties;
	}

	public int getMAX_ROW() {
		return MAX_ROW;
	}

	public void setMAX_ROW(int mAX_ROW) {
		MAX_ROW = mAX_ROW;
	}

	public int getMAX_COL() {
		return MAX_COL;
	}

	public void setMAX_COL(int mAX_COL) {
		MAX_COL = mAX_COL;
	}

	public Color getPlayerColor() {
		return playerColor;
	}

	public void setPlayerColor(Color playerColor) {
		this.playerColor = playerColor;
	}

	public Color getCompColor() {
		return compColor;
	}

	public void setCompColor(Color compColor) {
		this.compColor = compColor;
	}

	/*
	 * public int[][] getBoardState() { return boardState; }
	 * 
	 * public void setBoardState(int[][] boardState) { this.boardState =
	 * boardState; }
	 */

	public int getPlayerColorInInt() {
		if (this.playerColor.equals(Color.BLACK)) {
			return -1;
		} else {
			return +1;
		}
	}

	public int getColorInInt(Color input) {
		if (input.equals(Color.WHITE))
			return +1;
		else
			return -1;
	}

	@Override
	public String toString() {
		return "BoardProperties [MAX_ROW=" + MAX_ROW + ", MAX_COL=" + MAX_COL
				+ ", playerColor=" + playerColor + ", compColor=" + compColor
				+ ", boardState=" + Arrays.toString(boardState)
				+ ", maxDepthOfMinMax=" + maxDepthOfMinMax
				+ ", boardStateTransition=" + boardStateTransition + "]";
	}

	public int[][] getBoardState() {
		return boardState;
	}

	public void setBoardState(int[][] boardState) {
		this.boardState = boardState;
	}

	public int getMaxDepthOfMinMax() {
		return maxDepthOfMinMax;
	}

	public void setMaxDepthOfMinMax(int maxDepthOfMinMax) {
		this.maxDepthOfMinMax = maxDepthOfMinMax;
	}
}
