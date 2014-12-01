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
import java.util.Random;

import edu.alaska.uaa.cs401.fgm.FanoronaBoardInterface;
import edu.alaska.uaa.cs401.fgm.FanoronaColor;
import edu.alaska.uaa.cs401.fgm.FanoronaLinkBoard;
import edu.alaska.uaa.cs401.fgm.FanoronaMoveException;

// TODO: Auto-generated Javadoc
/**
 * The Class FanoronaRandomAI, an AI that makes random moves, and only ever makes one capture move in any given move sequence.
 */
public class FanoronaRandomAI implements Runnable {
	
	/** The sock. */
	private Socket sock;
	
	/** The sr. */
	private BufferedReader sr;
	
	/** The sw. */
	private BufferedWriter sw;
	
	/** The port. */
	private int port = 3266;
	
	/** The host. */
	private String host = "localhost";
	
	/** The color. */
	private FanoronaColor color = null;
	
	/** The board. */
	FanoronaBoardInterface board = new FanoronaLinkBoard();
	
	/** The rng. */
	Random rng = new Random();

	/**
	 * Instantiates a new fanorona random ai.
	 * 
	 * @param host the host
	 * @param port the port
	 */
	public FanoronaRandomAI(String host,int port) {
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
			case 0:
				sw.write("ned\n");
				break;
			case 1:
				sw.write("ted\n");
				break;
			case 2:
				sw.write("fred\n");
				break;
			case 3:
				sw.write("zed\n");
				break;
			default:
				sw.write("jed\n");
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
		} catch (IOException e) {
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
	 * Random spaces.
	 * 
	 * @return the string
	 */
	private String randomSpaces() {
		StringBuilder sb = new StringBuilder();
		while (true) {
			if (rng.nextInt(3) == 0) return sb.toString();
			sb.append(' ');
		}
	}
	
	/**
	 * Make a move.
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void makeAMove() throws IOException {
		int xoff = rng.nextInt(9);
		int yoff = rng.nextInt(5);
		for (int xstep = 0; xstep < 9; xstep++) {
			int x = (xoff + xstep) % 9;
			for (int ystep = 0; ystep < 5; ystep++) {
				int y = (yoff + ystep) % 5;

				for (int dx = -1; dx < 2; dx++) {
					for (int dy = -1; dy < 2; dy++) {
						for (String mt : new String [] {"M","C","W"}) {
							String move = String.format("%s%d%d%d%d",mt,x,y,x+dx,y+dy); 
							try {
								board.processMove(move, color);
								sw.write(randomSpaces()+move+randomSpaces()+"\n");
								sw.flush();
								if (sr.readLine().startsWith("-")) { 
									System.out.println("server says game over!");
								}
								return;
							} catch (FanoronaMoveException e) {
							}
						}
					}
				}
			}
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
		FanoronaRandomAI ai = new FanoronaRandomAI("localhost",3266);
		ai.run();
	}
}
