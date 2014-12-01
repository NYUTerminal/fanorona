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
package net.strudelline.fanorona;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import edu.alaska.uaa.cs401.fgm.FanoronaBoardInterface;
import edu.alaska.uaa.cs401.fgm.FanoronaColor;
import edu.alaska.uaa.cs401.fgm.FanoronaLinkBoard;
import edu.alaska.uaa.cs401.fgm.FanoronaMoveException;

// TODO: Auto-generated Javadoc
/**
 * The Class FanoronaJoaAI, a minimalistic greedy AI.
 */
public class FanoronaJoaAI implements Runnable {
	
	/** The port. */
	private int port;
	
	/** The host. */
	private String host;
	
	/** The sock. */
	private Socket sock;
	
	/** The sr. */
	private BufferedReader sr;
	
	/** The sw. */
	private BufferedWriter sw;
	
	/** The color. */
	private FanoronaColor color = null;
	
	/** The board. */
	FanoronaBoardInterface board = new FanoronaLinkBoard();
	
	/** The rng. */
	Random rng = new Random();

	/**
	 * Instantiates a new fanorona joa ai.
	 * 
	 * @param host the host
	 * @param port the port
	 */
	public FanoronaJoaAI(String host,int port) {
		this.host = host;
		this.port = port;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			sock = new Socket();
			sock.connect(new InetSocketAddress(host,port));
			sr = new BufferedReader(new InputStreamReader(sock.getInputStream(),"US-ASCII"));
			sw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(),"US-ASCII"));
			switch (rng.nextInt(5)) {
			default:
				sw.write("todd\n");
			break;
			}
			sw.flush();
			String line = sr.readLine();
			if (line.startsWith("+black")) {
				color = FanoronaColor.Black;
			} else {
				color = FanoronaColor.White;
				makeAMove();
			}
			while (true) {
				line = sr.readLine();
				if (line == null) break;
				if (line.startsWith("-")) {
					try {
						board.processMove(line.substring(1),color.opposite());
					} catch (FanoronaMoveException e) {
						System.err.println("Server sent an invalid move: "+line);
					}
					System.out.format("%s won.", board.getWinner());
					break;
				} else {
					try {
						board.processMove(line.substring(1),color.opposite());
					} catch (FanoronaMoveException e) {
						System.err.println("Server sent an invalid move: "+line);
					}				
				}
				makeAMove();
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	//	private boolean onBoard(int x, int y) {
	//		if (x < 0) return false;
	//		if (x > 8) return false;
	//		if (y < 0) return false;
	//		if (y > 4) return false;
	//		return true;
	//	}

	/**
	 * All possible board states.
	 * 
	 * @param board the board
	 * @param side the side
	 * 
	 * @return the map< string, fanorona link board>
	 */
	public Map<String,FanoronaLinkBoard> allPossibleBoardStates(FanoronaLinkBoard board, FanoronaColor side) {
		TreeMap<String,FanoronaLinkBoard> tries = new TreeMap<String,FanoronaLinkBoard>();
		String move = "";
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 5; y++) {
				tryMoves(move, tries,board,x,y,side);
			}
		}
		return tries;
	}

	/**
	 * Try moves.
	 * 
	 * @param priorMoves the prior moves
	 * @param tries the tries
	 * @param board the board
	 * @param x the x
	 * @param y the y
	 * @param side the side
	 */
	public void tryMoves(String priorMoves, TreeMap<String,FanoronaLinkBoard> tries, FanoronaLinkBoard board, int x, int y, FanoronaColor side) {
		// if this is our side moving:
		// for each possible move, try the opponent's moves assuming we ended here.
		// otherwise:
		// try and continue the moves.  if we get to a dead end, save the snapshot
		//    and the move string that's led to it.
		for (int dx = -1; dx < 2; dx++) {
			for (int dy = -1; dy < 2; dy++) {
				for (String mt : new String [] {"M","C","W"}) {
					String move = String.format("%s%d%d%d%d",mt,x,y,x+dx,y+dy);
					FanoronaLinkBoard nb = new FanoronaLinkBoard(board);
					String newMove = priorMoves + " " + move;
					try {
						nb.processMove(newMove,side);
						tries.put(newMove,nb);
						tryMoves(newMove, tries,board, x+dx,y+dy,side);
					} catch (FanoronaMoveException e) {
					}
				}
			}
		}
	}
	//	
	//	public void tryAllMoves(TreeMap<String,FanoronaLinkBoard> tries, FanoronaLinkBoard board, FanoronaColor side) {
	//		for (int x = 0; x < 9; x++) {
	//			for (int y = 0; y < 5; y++) {
	//				tryMoves(tries, new FanoronaLinkBoard(board),x,y,side);
	//			}
	//		}
	//	}

	/**
	 * Count color.
	 * 
	 * @param b the b
	 * @param color the color
	 * 
	 * @return the int
	 */
	private int countColor(FanoronaBoardInterface b,FanoronaColor color) {
		int ret = 0;
		for (int x=0; x < 9; x++) {
			for (int y=0; y < 5; y++) {
				if (b.getCellColor(x,y) == color) ret++;
			}
		}
		return ret;
	}

	/**
	 * Gets the best move least opposite.
	 * 
	 * @return the best move least opposite
	 */
	public String getBestMoveLeastOpposite() {
		int ocolor = 5000;
		//		int mcolor = 0;
		String move = null;
		Map<String,FanoronaLinkBoard> states = allPossibleBoardStates(new FanoronaLinkBoard(board), color);
		for (String m : states.keySet()) {
			FanoronaLinkBoard b = states.get(m);
			//			int nmc = countColor(b,color);
			int noc = countColor(b,color.opposite());
			if (noc < ocolor) {
				move = m;
				ocolor = noc;
			}
		}
		move = move.trim();
		System.out.format("Selected %s out of %d possible\n",move,states.size());
		return move;
	}

	//	public String getBestMoveMostOwn() {
	//		int ocolor = 5000;
	//		int mcolor = 0;
	//		String move = null;
	//		Map<String,FanoronaLinkBoard> states = allPossibleBoardStates(new FanoronaLinkBoard(board), color);
	//		for (String m : states.keySet()) {
	//			FanoronaLinkBoard b = states.get(m);
	//			int nmc = countColor(b,color);
	//			int noc = countColor(b,color.opposite());
	//			if (nmc > mcolor) {
	//				move = m;
	//				mcolor = nmc;
	//			}
	//		}
	//		System.out.format("Selected %s out of %d possible\n",move,states.size());
	//		return move;
	//	}

	/**
	 * Gets the best move most own.
	 * 
	 * @return the best move most own
	 */
	public String getBestMoveMostOwn() {
		int ocolor = 5000;
		int mcolor = 0;
		String move = null;
		Map<String,FanoronaLinkBoard> states = allPossibleBoardStates(new FanoronaLinkBoard(board), color);
		for (String m : states.keySet()) {
			FanoronaLinkBoard b = states.get(m);
			Map<String,FanoronaLinkBoard> newstates = allPossibleBoardStates(new FanoronaLinkBoard(b), color.opposite());
			int nmc = 5000;
			for (String k : newstates.keySet()) {
				int tc = countColor(newstates.get(k),color);
				if (tc < nmc) nmc = tc;
			}
			int noc = countColor(b,color.opposite());
			if (nmc > mcolor) {
				move = m;
				mcolor = nmc;
				ocolor = noc;
			}
			if (nmc == mcolor) { // tie breaker :D
				if (noc < ocolor) {
					move = m;
					mcolor = nmc;
					ocolor = noc;
				}
			}
		}
		move = move.trim();
		System.out.format("Selected %s out of %d possible\n",move,states.size());
		return move;
	}

	/**
	 * Make a move.
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void makeAMove() throws IOException {
		String move = getBestMoveMostOwn();
		try {
			board.processMove(move, color);
		} catch (FanoronaMoveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Catastrophic failure in the AI!  omg");
			System.exit(1);
			return;
		}
		sw.write(move+"\n");
		sw.flush();
		String line = sr.readLine();
		if (line.startsWith("-")) { 
			System.out.println("server says: " + line.substring(1));
			System.out.format("%s won\n",board.getWinner());
			//			System.exit(1);
		}
	}
	
	/**
	 * The main method.
	 * 
	 * @param argv the arguments
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] argv) throws IOException {
		FanoronaJoaAI ai = new FanoronaJoaAI("localhost",3266);
		ai.run();
	}
}
