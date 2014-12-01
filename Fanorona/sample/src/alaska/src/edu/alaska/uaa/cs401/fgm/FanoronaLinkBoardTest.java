/*
 * Copyright (c) 2009, Jeremiah Lewis, Matthew Rykaczewski, James Andariese
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 * 
 * * Neither the name of the University of Alaska, Anchorage nor the
 *   names of its contributors may be used to endorse or promote
 *   products derived from this software without specific prior written
 *   permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY  FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
/**
 * 
 */
package edu.alaska.uaa.cs401.fgm;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The class FanoronaLinkBoardTest, created to use JUnit to test each method
 * in the FanoronaLinkBoard class.
 * 
 * @author James
 */

// this will run a full, correct game.
//FanoronaBoardInterface fb = new FanoronaLinkBoard();
//fb.processMove("C 4 1 4 2",FanoronaColor.White);
//fb.processMove("W 5 3 4 4 C 4 4 4 3",FanoronaColor.Black);
//fb.processMove("C 7 0 7 1",FanoronaColor.White);
//fb.processMove("C 3 3 4 2 C 4 2 4 1 W 4 1 5 1",FanoronaColor.Black);
//fb.processMove("C 3 2 3 3 W 3 3 4 2 W 4 2 4 1 W 4 1 3 1 W 3 1 4 0",FanoronaColor.White);
//fb.processMove("C 5 2 5 1 W 5 1 4 1 W 4 1 4 2 C 4 2 3 1 W 3 1 3 2 C 3 2 2 2 C 2 2 1 1 W 1 1 1 2",FanoronaColor.Black);
//fb.processMove("C 8 2 7 3 W 7 3 6 2 W 6 2 6 1",FanoronaColor.White);
//fb.processMove("M 1 2 1 1",FanoronaColor.Black);
//fb.processMove("M 6 1 7 1",FanoronaColor.White);
//fb.processMove("M 2 3 3 3",FanoronaColor.Black);
//fb.processMove("M 7 1 7 0",FanoronaColor.White);
//fb.processMove("M 8 3 7 3",FanoronaColor.Black);
//fb.processMove("M 7 0 8 0",FanoronaColor.White);
//fb.processMove("M 7 3 7 2",FanoronaColor.Black);
//fb.processMove("M 8 0 8 1",FanoronaColor.White);
//fb.processMove("M 7 2 7 1",FanoronaColor.Black);
//fb.processMove("M 8 1 8 0",FanoronaColor.White);
//fb.processMove("W 7 1 6 2",FanoronaColor.Black);


public class FanoronaLinkBoardTest {

	/**
	 * Sets the up before class.
	 * 
	 * @throws java.lang.Exception 	 * @throws Exception the exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * Tear down after class.
	 * 
	 * @throws java.lang.Exception 	 * @throws Exception the exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * Sets up the instance of the FanoronaLinkBoard class.
	 * 
	 * @throws java.lang.Exception 	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Tear down the instance of the FanoronaLinkBoard class.
	 * 
	 * @throws java.lang.Exception 	 * @throws Exception the exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Test method for {@link edu.alaska.uaa.cs401.fgm.FanoronaLinkBoard#printBoard()}.
	 */
	@Test
	public void testBoardColors() {
		FanoronaBoardInterface fb = new FanoronaLinkBoard();

		System.out.println(fb.stringBoard());
		
		class Tuple {
			public int x,y;
			public FanoronaColor q;
			Tuple(FanoronaColor q,int x,int y) {
				this.q = q; // expected value
				this.x = x; // x
				this.y = y; // y
			}
		}
		ArrayList<Tuple> al = new ArrayList<Tuple>();
		for (int x = 0; x < 8; x++) {
			al.add(new Tuple(FanoronaColor.White,x,0));
			al.add(new Tuple(FanoronaColor.White,x,1));
			if (x == 4) {
				al.add(new Tuple(null,x,2));
			} else if ( x < 4) {
				al.add(new Tuple(((x & 1) == 1)?FanoronaColor.White:FanoronaColor.Black,x,2));
			} else {
				al.add(new Tuple(((x & 1) == 0)?FanoronaColor.White:FanoronaColor.Black,x,2));
			}
			al.add(new Tuple(FanoronaColor.Black,x,3));
			al.add(new Tuple(FanoronaColor.Black,x,4));
		}
		for (Tuple q : al) {
			FanoronaColor retq = fb.getCellColor(q.x, q.y);
			if (retq != q.q) fail(String.format("Invalid piece at %d,%d expected %s got %s",q.x,q.y,q.q,retq));
		}
	}
	
	//@Test
	// no logging in the board anymore.
//	public void testLogging() throws Exception {
//
//		try {
//			FanoronaBoardInterface fb = new FanoronaLinkBoard();
//			fb.processMove("C4142",FanoronaColor.White);
//			fb.processMove("W5344 C4443",FanoronaColor.Black);
//			fb.processMove("C7071",FanoronaColor.White);
//			fb.processMove("C3342 C4241 W4151",FanoronaColor.Black);
//			fb.processMove("C3233 W3342 W4241 W4131 W3140",FanoronaColor.White);
//			fb.processMove("C5251 W5141 W4142 C4231 W3132 C3222 C2211 W1112",FanoronaColor.Black);
//			fb.processMove("C8273 W7362 W6261",FanoronaColor.White);
//			fb.processMove("M1211",FanoronaColor.Black);
//			fb.processMove("M6171",FanoronaColor.White);
//			fb.processMove("M2333",FanoronaColor.Black);
//			fb.processMove("M7170",FanoronaColor.White);
//			fb.processMove("M8373",FanoronaColor.Black);
//			fb.processMove("M7080",FanoronaColor.White);
//			fb.processMove("M7372",FanoronaColor.Black);
//			fb.processMove("M8081",FanoronaColor.White);
//			fb.processMove("M7271",FanoronaColor.Black);
//			fb.processMove("M8180",FanoronaColor.White);
//			fb.processMove("W7162",FanoronaColor.Black);
//			if (!fb.getLog().equals("White's move: C4142\nBlack's move: W5344 C4443\nWhite's move: C7071\nBlack's move: C3342 C4241 W4151\nWhite's move: C3233 W3342 W4241 W4131 W3140\nBlack's move: C5251 W5141 W4142 C4231 W3132 C3222 C2211 W1112\nWhite's move: C8273 W7362 W6261\nBlack's move: M1211\nWhite's move: M6171\nBlack's move: M2333\nWhite's move: M7170\nBlack's move: M8373\nWhite's move: M7080\nBlack's move: M7372\nWhite's move: M8081\nBlack's move: M7271\nWhite's move: M8180\nBlack's move: W7162"))
//				fail("Log is invalid.");
//
//		} catch (FanoronaMoveException e) {
//			System.err.println("FanoronaMoveException: "+e.getErrorText());
//			throw e;
//		}
//	}
	
	
	/**
	 * Tests a full game of Fanorona.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testFullGame() throws Exception {
		//FanoronaBoardInterface fb = new FanoronaLinkBoard();
//		String blackcommands = "W 5 3 4 4 C 4 4 4 3\nC 3 3 4 2 C 4 2 4 1 W 4 1 5 1\nC 5 2 5 1 W 5 1 4 1 W 4 1 4 2 C 4 2 3 1 W 3 1 3 2 C 3 2 2 2 C 2 2 1 1 W 1 1 1 2\nM 1 2 1 1\nM 2 3 3 3\nM 8 3 7 3\nM 7 3 7 2\nM 7 2 7 1\nW 7 1 6 2";
//		String whitecommands = "C 4 1 4 2\nC 7 0 7 1\nC 3 2 3 3 W 3 3 4 2 W 4 2 4 1 W 4 1 3 1 W 3 1 4 0\nC 8 2 7 3 W 7 3 6 2 W 6 2 6 1\nM 6 1 7 1\nM 7 1 7 0\nM 7 0 8 0\nM 8 0 8 1\nM 8 1 8 0";
//		String[] wca = whitecommands.split("\n");
//		String[] bca = blackcommands.split("\n");
		try {
			FanoronaBoardInterface fb = new FanoronaLinkBoard();
			fb.processMove("C4142",FanoronaColor.White);
			fb.processMove("W5344 C4443",FanoronaColor.Black);
			fb.processMove("C7071",FanoronaColor.White);
			fb.processMove("C3342 C4241 W4151",FanoronaColor.Black);
			fb.processMove("C3233 W3342 W4241 W4131 W3140",FanoronaColor.White);
			fb.processMove("C5251 W5141 W4142 C4231 W3132 C3222 C2211 W1112",FanoronaColor.Black);
			fb.processMove("C8273 W7362 W6261",FanoronaColor.White);
			fb.processMove("M1211",FanoronaColor.Black);
			fb.processMove("M6171",FanoronaColor.White);
			fb.processMove("M2333",FanoronaColor.Black);
			fb.processMove("M7170",FanoronaColor.White);
			fb.processMove("M8373",FanoronaColor.Black);
			fb.processMove("M7080",FanoronaColor.White);
			fb.processMove("M7372",FanoronaColor.Black);
			fb.processMove("M8081",FanoronaColor.White);
			fb.processMove("M7271",FanoronaColor.Black);
			fb.processMove("M8180",FanoronaColor.White);
			fb.processMove("W7162",FanoronaColor.Black);
//			for (int i = 0; fb.getWinner() == null; i++) {
//				fb.processMove(wca[i],FanoronaColor.White);
//				System.out.println(fb.stringBoard());
//				if (fb.getWinner() != null) {
//					if (fb.getWinner() != FanoronaColor.White)
//						fail("Black won on white's turn.");
//					break;
//				}
//				fb.processMove(bca[i],FanoronaColor.Black);
//				System.out.println(fb.stringBoard());
//				if (fb.getWinner() != null) {
//					if (fb.getWinner() != FanoronaColor.Black)
//						fail("White won on black's turn.");
//					break;
//				}
//			}
		} catch (FanoronaMoveException e) {
			System.err.println("FanoronaMoveException: "+e.getErrorText());
		}
	}
	
	/**
	 * Test to move from one position to the same position.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testMoveToSame() throws Exception {
		FanoronaBoardInterface fb = new FanoronaLinkBoard();
		try {
			fb.processMove("C4141", FanoronaColor.White);
			fail("Move was invalid but passed");
		} catch (FanoronaMoveException e) {
		}
	}
	
	/**
	 * Test to move from one position to an occupied position.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testMoveToOccupied() throws Exception {
		FanoronaBoardInterface fb = new FanoronaLinkBoard();
		try {
			fb.processMove("C4131", FanoronaColor.White);
			fail("Move was invalid but passed");
		} catch (FanoronaMoveException e) {
		}
	}
	
	/**
	 * Test to move a piece that is not theirs.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testMoveWrongPiece() throws Exception {
		FanoronaBoardInterface fb = new FanoronaLinkBoard();
		try {
			fb.processMove("C4342", FanoronaColor.White);
			fail("Move was invalid but passed");
		} catch (FanoronaMoveException e) {
		}
	}
	
	/**
	 * Test to move a piece too far.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testMoveTooFar() throws Exception {
		// this is actually move to non-neighboring...
		FanoronaBoardInterface fb = new FanoronaLinkBoard();
		try {
			fb.processMove("C0042", FanoronaColor.White);
			fail("Move was invalid but passed");
		} catch (FanoronaMoveException e) {
		}
	}
	
	/**
	 * Test to move a piece off the board.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testMoveOffBoard() throws Exception {
		// this is actually move to non-neighboring...
		FanoronaBoardInterface fb = new FanoronaLinkBoard();
		try {
			fb.processMove("C8090", FanoronaColor.White);
			fail("Move was invalid but passed");
		} catch (FanoronaMoveException e) {
		}
	}
	
	/**
	 * Test to perform an invalid capture by withdrawal.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testInvalidCaptureByWithdrawal() throws Exception {
		// this is actually move to non-neighboring...
		FanoronaBoardInterface fb = new FanoronaLinkBoard();
		try {
			fb.processMove("W4142", FanoronaColor.White);
			fail("Move was invalid but passed");
		} catch (FanoronaMoveException e) {
		}
	}
	
	/**
	 * Test to perform a movement continuation using a different piece (invalid move).
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testMovementContinuationFromDifferentPieceNotAllowed() throws Exception {
		FanoronaBoardInterface fb = new FanoronaLinkBoard();
		fb.processMove("C4142",FanoronaColor.White);
		fb.processMove("W5344 C4443",FanoronaColor.Black);
		fb.processMove("C7071",FanoronaColor.White);
		try {
			fb.processMove("C6362 W5253",FanoronaColor.Black);
			fail("Move was invalid but passed");
		} catch (FanoronaMoveException e) {
		}
	}
	
	/**
	 * Test for a paika, which is not allowed.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testPaikaNotAllowed() throws Exception {
		FanoronaBoardInterface fb = new FanoronaLinkBoard();
		fb.processMove("C4142",FanoronaColor.White);
		fb.processMove("W5344 C4443",FanoronaColor.Black);
		fb.processMove("C7071",FanoronaColor.White);
		fb.processMove("C3342 C4241 W4151",FanoronaColor.Black);
		fb.processMove("C3233 W3342 W4241 W4131 W3140",FanoronaColor.White);
		fb.processMove("C5251 W5141 W4142 C4231 W3132 C3222 C2211 W1112",FanoronaColor.Black);
		fb.processMove("C8273 W7362 W6261",FanoronaColor.White);
		fb.processMove("M1211",FanoronaColor.Black);
		fb.processMove("M6171",FanoronaColor.White);
		fb.processMove("M2333",FanoronaColor.Black);
		fb.processMove("M7170",FanoronaColor.White);
		fb.processMove("M8373",FanoronaColor.Black);
		fb.processMove("M7080",FanoronaColor.White);
		fb.processMove("M7372",FanoronaColor.Black);
		fb.processMove("M8081",FanoronaColor.White);
		fb.processMove("M7271",FanoronaColor.Black);
		fb.processMove("M8180",FanoronaColor.White);
		//fb.processMove("W 7 1 6 2",FanoronaColor.Black);

		try {
			fb.processMove("M7162", FanoronaColor.Black);
			fail("Move was invalid but passed");
		} catch (FanoronaMoveException e) {
		}
	}

	/**
	 * Test for an invalid move in a straight line two moves in a row.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testInvalidMoveStraightLine() throws Exception {
		// this one will test if a move 
		FanoronaBoardInterface fb = new FanoronaLinkBoard();
		fb.processMove("C3242",FanoronaColor.White);
		fb.processMove("C2232",FanoronaColor.Black);

		try {
			fb.processMove("W6252 C5242", FanoronaColor.White);
			fail("Move was invalid but passed");
		} catch (FanoronaMoveException e) {
		}
	}
	
	//@Test
	/**
	 * Test for a valid move in a (semi)straight line.
	 * 
	 * @throws Exception the exception
	 */
	public void testValidMoveStraightLine() throws Exception {
		// this one will test if a move 
		FanoronaBoardInterface fb = new FanoronaLinkBoard();
		fb.processMove("C3242",FanoronaColor.White);
		fb.processMove("C2232",FanoronaColor.Black);
		fb.processMove("C4142",FanoronaColor.White);
		fb.processMove("C2322",FanoronaColor.Black);
		fb.processMove("C4243",FanoronaColor.White);
		fb.processMove("C2221",FanoronaColor.Black);
		fb.processMove("C3141",FanoronaColor.White);
		fb.processMove("C2333",FanoronaColor.Black);
		fb.processMove("C6252",FanoronaColor.White);
		fb.processMove("C7372",FanoronaColor.Black);
		fb.processMove("C1222 C2223",FanoronaColor.White);
		fb.processMove("C6362",FanoronaColor.Black);
		fb.processMove("C8273",FanoronaColor.White);
		fb.processMove("C1312",FanoronaColor.Black);
		fb.processMove("W5242 W4231 C3132 C3222",FanoronaColor.White);
		// this move is in a straight line with itself but with one move
		// separating.
	}
	
	/**
	 * Test to try moving to a prior space.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testMoveToPriorSpace() throws Exception {
		FanoronaBoardInterface fb = new FanoronaLinkBoard();
		fb.processMove("C3242",FanoronaColor.White);
		try {
			fb.processMove("C2232 C3222",FanoronaColor.Black);
			fail("Move was invalid but passed");
		} catch (FanoronaMoveException e) {
		}		
	}
	
	/**
	 * Test to perform an invalid capture by advancing.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testInvalidCaptureByAdvancing() throws Exception {
		FanoronaBoardInterface fb = new FanoronaLinkBoard();
		fb.processMove("C4142",FanoronaColor.White);

		try {
			fb.processMove("C3343", FanoronaColor.Black);
			fail("Move was invalid but passed");
		} catch (FanoronaMoveException e) {
		}
	}
	
	/**
	 * Test to see if the two boards are equal.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testBoardEquals() throws Exception {
		FanoronaBoardInterface fb = new FanoronaLinkBoard();
		FanoronaBoardInterface fb2 = new FanoronaLinkBoard();
		fb.processMove("C3242",FanoronaColor.White);
		if (fb.equals(fb2)) fail("Boards are not equal");
		fb2.processMove("C3242",FanoronaColor.White);
		if (!fb.equals(fb2)) fail("Boards are equal");
	}
}
