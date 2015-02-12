package Test;

import java.util.List;
import java.util.Scanner;

import Game.BoardProperties;
import Game.Controller;
import Game.Move;

public class PlayFanorona {

	private static Scanner in;

	public static void main(String[] args) {
		Controller controller = new Controller();
		in = new Scanner(System.in);
		String playerColor = "WHITE";
		System.out
				.println("Enter the player color in caps (WHITE or BLACK): \n");
		playerColor = in.nextLine();
		String difficult;
		System.out.println("difficulty of the program (easy,medium,hard): \n");
		difficult = in.nextLine();
		if (difficult.equals("medium")) {
			BoardProperties.getInstance().setMaxDepthOfMinMax(100);
		} else if (difficult.equals("hard")) {
			BoardProperties.getInstance().setMaxDepthOfMinMax(300);
		} else {
			BoardProperties.getInstance().setMaxDepthOfMinMax(10);
		}
		int[][] boardState = controller.initializeBoard(0, playerColor);
		System.out
				.println(" ^^^^^^^^^^^^^^^^^^^^ GAME START- Initial Board Set up ^^^^^^^^^^^^^^^^^^^^^^^^^^");
		controller.printCurrestStateOfBoard(BoardProperties.getInstance()
				.getBoardState());
		while (!controller.checkGameOver(boardState)) {
			// Enter your move comma seperated .Ex if you want to move piece
			// from 3,3 coordinate to 2,2 and type of kill is approach kill +1
			// withdrawal kill -1 ,your input should be in the below format
			// 3,3,2,2,-1 -- withdrawal kill
			// 3,3,2,2,1 -- forward kill
			System.out
					.println("Enter your move (startX,startY,endX,endY,appraoch = +1 Or Withdrawlmove -1) :");
			System.out.println("\n");
			String nextHumanMove = in.nextLine();
			try {
				String[] split = nextHumanMove.split(",");
				System.out.println("You want to move piece from (" + split[0]
						+ "," + split[1] + ") to (" + split[2] + "," + split[3]
						+ ") and type of move is : " + split[4] + "");
				Integer startX = Integer.valueOf(split[0]);
				Integer startY = Integer.valueOf(split[1]);
				Integer endX = Integer.valueOf(split[2]);
				Integer endY = Integer.valueOf(split[3]);
				Integer typeOfMove = Integer.valueOf(split[4]);
				boolean typeOfMoveBoolean = typeOfMove.equals(+1) ? true
						: false;
				int[][] nextBoardState = controller.makeAMove(boardState,
						BoardProperties.getInstance().getPlayerColor(),
						new Move(startX, startY, endX, endY, typeOfMove),
						typeOfMoveBoolean);
				if (nextBoardState != null) {
					boardState = nextBoardState;
				}
			} catch (Exception exc) {
				System.out.println("Please enter valid input please: ");
			}

		}
		System.out
				.println("---------------------------- HERE YOU GO THE BOARD TRANSITIONS ARE : : -------------------");
		List<int[][]> boardStateTransitions = controller
				.getBoardStateTransition();
		for (int[][] boardTransition : boardStateTransitions) {
			controller.printCurrestStateOfBoard(boardTransition);
			System.out.println("\n**************************\n");
		}
	}

}
