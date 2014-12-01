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
package edu.alaska.uaa.cs401.fgm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.prefs.Preferences;

// TODO: Auto-generated Javadoc
/**
 * The class FanoronaServer, a thread-capable class designed to create an instance of
 * FanoronaLinkBoard and FanoronaServerFrame, open receiving/sending sockets, and 
 * reading the server's log into the appropriate log file. 
 */
public class FanoronaServer extends Thread {
	//FanoronaBoardInterface board = new FanoronaLinkBoard();
	/** The board. */
	FanoronaBoardInterface board = null;

    /** The preferences. */
    Preferences prefs = Preferences.userNodeForPackage(this.getClass());
	
	/** The default board displayer setup. */
	private final FanoronaBoardDisplayerInterface DEFAULT_DISPLAYER = new FanoronaBoardDisplayerInterface() {
		public void update() {
		}
		public boolean requestNewLogFileDirectory() {
			return false;
		}
		public void addLogFile(String n,File f) {			
		}
	};
	
	/** The board displayer. */
	FanoronaBoardDisplayerInterface displayer = DEFAULT_DISPLAYER; 
	
	/** The port to listen at. */
	private short port = 3266;
	
	/** The instance of the socket. */
	private ServerSocket sock;
	
	/** The name of the black player. */
	private String blackname = "";
	
	/** The name of the white player. */
	private String whitename = "";
	
	/** The server's status array. */
	private ArrayList<String> status = new ArrayList<String>();
	
	/** The message prioritie's tree mapping, used to make sure messages are processed in the correct order and way. */
	private Map<StatusPriority,Set<String> > msgpriorities = new TreeMap<StatusPriority,Set<String> >();
	
	/** The game user ID. */
	private UUID gameUUID = null;
	
	/** The total games the server has to host. If this is less than 1, the server will host an unlimited number 
	 * of games, other wise it will only host the a specific number of games and then quit.*/
	private int totalGames = -1;
	
	/** The maximum number of board states that can be saved in the list of previous board states. */
	private final int CYCLE_MAX = 10;
	
	/** The minimum number of board states that can be saved in the list of previous board states. */
	private final int CYCLE_MIN = 5;
	
	/** The list of previous board states. */
	private List<FanoronaBoardInterface> previousBoards = new LinkedList<FanoronaBoardInterface>();
	
	/** The gamelog, a list of all moves that were made in the game. */
	private List<String> gamelog = new ArrayList<String>();
	
	/** The default port for the server to listen at. */
	private final short DEFAULT_PORT = 3266; // IANA registered NS CFG Server
											 // -OR- this might be FANO on a telephone.
											 // IANA ain't got nothin' on telephones!
	/** The boolean trigger to force a 'game over' state. */
 	private boolean forceGameOver;

	/** The socket to be used for listening and speaking to player a. */
	private Socket socka;
	
	/** The socket to be used for listening and speaking to player b. */
	private Socket sockb;	

	/**
	 * The enumerated variable StatusPriority, used to determine if the server is in debug mode or high-status, which is normal running mode.
	 */
	public enum StatusPriority {
		
		/** The DEBUG. */
		DEBUG,
		
		/** The HIGH. */
		HIGH;
	}
	
	{
		for (StatusPriority p : StatusPriority.values()) {
			msgpriorities.put(p, new HashSet<String>());
		}
		status.add("Initializing");
	}
	
	/**
	 * Sets the status on a given message.
	 * 
	 * @param status the status
	 * @param p the p
	 */
	public void setStatus(String status,StatusPriority p) {
		msgpriorities.get(p).add(status);
		this.status.add(status);
		displayer.update();
	}
	
	/**
	 * Gets all of the messages of at least given priority.
	 * 
	 * @param min the min
	 * 
	 * @return the status
	 */
	public String getStatus(StatusPriority min) {
		String ret;
		ret = "No status available";
		for (int i = status.size()-1; i >= 0; i--) {
			ret = status.get(i); //if there's nothing else, we'll end up returning
								 // "Initializing"
			for (StatusPriority p : StatusPriority.values()) {
				if (p.ordinal() >= min.ordinal()) {
					if (msgpriorities.get(p).contains(ret)) return ret;
				}
			}
		}
		return ret;
	}
	
	/**
	 * Gets the array of status messages.
	 * 
	 * @param min the min
	 * 
	 * @return the status array
	 */
	public Iterable<String> getStatusArray(StatusPriority min) {
		ArrayList<String> ret = new ArrayList<String>(status.size());
		for (String msg : status) {
			for (StatusPriority p : StatusPriority.values()) {
				if (p.ordinal() >= min.ordinal()) {
					// this is above the min.
					if (msgpriorities.get(p).contains(msg)) {
						// and contains the message in question.  add it to ret
						ret.add(msg);
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * Instantiates a new fanorona server.
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public FanoronaServer() throws IOException {
//		port = -1;
//		sock = new ServerSocket();
//		try {
//			sock.setReuseAddress(true); // no TIME_WAIT kthx.
//		} catch (SocketException e) {
//			System.err.format("Failed to set SO_REUSEADDR.  This is non-fatal but beware\nthat the server may not be restarted on the same port for a short time.\n");
//		} 
	}
	
	/**
	 * Sets the port for the server to listen at.
	 * 
	 * @param port the port
	 * 
	 * @return the fanorona server
	 */
	public synchronized FanoronaServer setPort(short port) {
		while (true) {
			this.port = port;
			try {
				sock.bind(new InetSocketAddress(this.port)); // wildcard address
				break;
			} catch (IOException e) {
				// do nothing.  bind failed.  try another port!
			}
			this.port++;
		}
		return this;
	}
	
	/**
	 * Sets the server to shutdown after n games.
	 * 
	 * @param x the x
	 * 
	 * @return the fanorona server
	 */
	public FanoronaServer setShutdownAfterNGames(int x) {
		// if this isn't called, it will default to never shut down.
		totalGames = x;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		port=3266;
		try {
			sock = new ServerSocket(prefs.getInt("port", 3266),512);
			setStatus(String.format("Listening on port %d",prefs.getInt("port",3266)),StatusPriority.HIGH);
			//sock.bind(new InetSocketAddress(3266));
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			setStatus("Failed to bind to port",StatusPriority.HIGH);
			return;
		}
		try {
			sock.setReuseAddress(true); // no TIME_WAIT kthx.
		} catch (SocketException e) {
			setStatus("Failed to set SO_REUSEADDR.  This is non-fatal but beware that the server may not be restarted on the same port for a short time.",StatusPriority.HIGH);
		} 
		int iter = 0;
		while (totalGames-- != 0) {
			if (totalGames < 0) totalGames = -1; // run forever.
			System.out.format("Server running iteration #%d\n",++iter);
			socka = null;
			BufferedWriter aw = null;
			BufferedReader ar = null;
			sockb = null;
			BufferedWriter bw = null;
			BufferedReader br = null;
			
			BufferedReader blackr = null;
			BufferedReader whiter = null;
			BufferedWriter blackw = null;
			BufferedWriter whitew = null;
			try {
				setStatus("Waiting for connection 1",StatusPriority.HIGH);
				socka = sock.accept();
				aw = new BufferedWriter(new OutputStreamWriter(socka.getOutputStream()));
				ar = new BufferedReader(new InputStreamReader(socka.getInputStream()));
				setStatus("Waiting for connection 2",StatusPriority.HIGH);
				sockb = sock.accept();
				bw = new BufferedWriter(new OutputStreamWriter(sockb.getOutputStream()));
				br = new BufferedReader(new InputStreamReader(sockb.getInputStream()));
				setStatus("Waiting for clients to initialize",StatusPriority.HIGH);
				board = new FanoronaLinkBoard();

				gamelog.clear();
				
				if (prefs.getBoolean("randomSides", false)) {
					if (Math.random() < .5) {
						blackr = ar;
						blackw = aw;
						whiter = br;
						whitew = bw;
					} else {
						blackr = br;
						blackw = bw;
						whiter = ar;
						whitew = aw;
					}
				} else {
					blackr = br;
					blackw = bw;
					whiter = ar;
					whitew = aw;
				}
				// we have two sockets!  let's start the game!!!!!!! ZOMG
				
				gameUUID = java.util.UUID.randomUUID();
				whitename = whiter.readLine();
				whitew.write("+white "+gameUUID.toString()+"\n");
				whitew.flush();
				blackname = blackr.readLine();
				blackw.write("+black "+gameUUID.toString()+"\n");
				blackw.flush();

				setStatus(String.format("W:%s vs B:%s",whitename,blackname),StatusPriority.HIGH);
				
				displayer.update();
				
				FanoronaColor currentMover = FanoronaColor.White;
				forceGameOver = false;
				previousBoards.clear();
				
				// this is a prefs getInt without a setInt anywhere
				// to allow for changing in the future if we need it.
				int timeoutSeconds = prefs.getInt("timeoutSeconds", 20);
				
				while (board.getWinner() == null) {
					// save the current board state in the previousBoards list
					previousBoards.add(new FanoronaLinkBoard(board));
					while (previousBoards.size() > CYCLE_MAX)
						previousBoards.remove(0); 
					
					setStatus("Getting moves for "+currentMover,StatusPriority.DEBUG);
					BufferedReader cr = null;
					BufferedWriter cw = null;
//					BufferedReader or = null;
					BufferedWriter ow = null;
					switch (currentMover) {
					case White:
						cr = whiter;
						cw = whitew;
//						or = blackr;
						ow = blackw;
						break;
					case Black:
						cr = blackr;
						cw = blackw;
//						or = whiter;
						ow = whitew;
						break;
					}

					String l = null;
					outer: while (true) {
						try {
							boolean timeoutReported = false;
							long startTime = System.currentTimeMillis();
							final BufferedReader fcr = cr;
							final Object errorObject = new Object();
							final BlockingQueue<Object> q = new SynchronousQueue<Object>();
							Runnable r = new Runnable() {
								public void run() {
									try {
										String ret = fcr.readLine();
										q.put(ret);
									} catch (Throwable e) {
										while (true) {
											try {
												q.put(errorObject);
												break;
											} catch (InterruptedException e1) {
											}
										}
									}
								}
							};
							Thread reader = new Thread(r);
							while (true) {
								try {
									reader.start();
									//l=cr.readLine();
									Object retobj = q.take();
									if (retobj == errorObject) l = null;
									else l = (String) retobj;
									if (((System.currentTimeMillis() - startTime) / 1000) > timeoutSeconds) {
										setStatus(String.format("%s has exceeded the time limit",currentMover.toString()),StatusPriority.HIGH);
										timeoutReported = true;
									}
									break;
								} catch (InterruptedException e) {
									if (forceGameOver) {
										l = "";
										reader.interrupt();
										System.out.println("interrupted");
										break outer;
									}
								}
								System.out.println("restarting the read loop");
							}
								
							if (l == null) throw new IOException();
							String lastMove = l;
							synchronized(board) {
								board.processMove(l,currentMover);
								lastMove = board.getLastMove();
							}
							//display who's move it was in the log
							// also display over time
							gamelog.add(currentMover.toString() + "'s move: " + lastMove + (timeoutReported?" [over time limit]":""));
							setStatus(String.format("Moves for %s succeeded!\n",currentMover),StatusPriority.DEBUG);
							break;
						} catch (FanoronaMoveException e) {
							setStatus(String.format("received an invalid move from %s: %s",currentMover,e.getErrorText()),StatusPriority.DEBUG);
							cw.write("-"+e.getErrorText()+"\n");
							cw.flush();
						}
					}
					displayer.update();

					FanoronaColor winner = null;
					synchronized(board) {
						winner = board.getWinner();
					}
					boolean gameOver = false;
					if (forceGameOver) {
						gameOver = true;
					} else if (winner != null) {
						gameOver=true;
					} else if (previousBoards.size() >= CYCLE_MIN){
						// if there aren't this many, there's no reason
						// to even check.
						int cn = 0;
						for (FanoronaBoardInterface b : previousBoards) {
							if (cn++ < CYCLE_MIN) {
								if (b.equals(board)) {
									gameOver=true;
									System.out.format("zomg found a cycle!\n");
									break;
								}
							}
						}
					}
					System.out.format("Searched %d previous boards\n",previousBoards.size());
					if (gameOver) {
						ow.write("-");
						cw.write("-\n");
						cw.flush();
					} else {
						ow.write("+");
						cw.write("+\n");
						cw.flush();
					}
					ow.write(l+"\n");
					ow.flush();
					System.out.format("%s: %s\n",currentMover,l);
					currentMover = currentMover.opposite();
					if (gameOver) break;
				}				
			} catch (Exception e) {
				try {
					whitew.write("-An unrecoverable error occured.\n");
				} catch (IOException e1) {
				}
				try {
					blackw.write("-An unrecoverable error occured.\n");
				} catch (IOException e1) {
				}
				System.err.format("An unrecoverable error occured.\n");
				setStatus("An unrecoverable error occured during the game.",StatusPriority.HIGH);
				e.printStackTrace();
			} finally {
				try {
					socka.close();
				} catch (IOException e1) {
				}
				try {
					sockb.close();
				} catch (IOException e1) {
				}
			}
			FanoronaColor winner = null;
			synchronized (board) {
				winner = board.getWinner();
			}
			if (winner == null) {
				setStatus(String.format("GameID: %s W:%s vs B:%s; draw",gameUUID,whitename,blackname),StatusPriority.HIGH);
			} else {
				setStatus(String.format("GameID: %s W:%s vs B:%s; %s won",gameUUID,whitename,blackname,winner),StatusPriority.HIGH);
			}
			String logdir = prefs.get("loggingDirectory", null);
			if (logdir != null) {
				File outfile = new File(logdir,gameUUID.toString()+".txt");
				while (true) {
					try {
						BufferedWriter fw = new BufferedWriter(new FileWriter(outfile));
						if (winner == null) {
							fw.write(String.format("GameID: %s W:%s vs B:%s; draw",gameUUID,whitename,blackname));
						} else {
							fw.write(String.format("GameID: %s W:%s vs B:%s; %s won",gameUUID,whitename,blackname,winner));
						}
						fw.newLine();
						fw.write(getLog());
						fw.flush();
						fw.close();
						displayer.addLogFile(String.format("%s vs %s [%s]",whitename,blackname,gameUUID.toString()),outfile);
						setStatus(String.format("Log saved to %s",outfile.toString()),StatusPriority.HIGH);
						break;
					} catch (IOException e) {
						setStatus(String.format("Failed to save log in %s"),StatusPriority.HIGH);
						if (!displayer.requestNewLogFileDirectory()) {
							setStatus(String.format("No new directory provided... logging is disabled."),StatusPriority.HIGH);
							prefs.remove("loggingDirectory");
							break;
						}
						logdir = prefs.get("loggingDirectory", null);
					}
				}
			}
			displayer.update();
		}
		try {
			sock.close();
		} catch (IOException e) {
		}
	}
	
	/**
	 * Returns the log in the form of a String.
	 * 
	 * @return the log
	 */
	public String getLog() {
		StringBuilder sb = new StringBuilder();
		String sep = System.getProperty("line.separator");
		for (String s : gamelog) {
			sb.append(s);
			sb.append(sep);
		}
		return sb.toString();
	}
	
	/**
	 * Gets the game user ID.
	 * 
	 * @return the game uuid
	 */
	public String getGameUUID(){
		return this.gameUUID.toString();
	}
	
	/**
	 * Gets the board state.
	 * 
	 * @return the board
	 */
	public FanoronaBoardInterface getBoard() {
		if (board == null) return null;
		synchronized(board) {
			return new FanoronaLinkBoard(board);
		}
	}

	/**
	 * Sets the displayer, the visible portion of the server.
	 * 
	 * @param fanoronaServerFrame the new displayer
	 */
	public void setDisplayer(FanoronaServerFrame fanoronaServerFrame) {
		// on update, this should be used to call out an update.
		// TEST: try it and see if it updates.
		if (fanoronaServerFrame==null) {
			displayer=DEFAULT_DISPLAYER;
		} else {
			this.displayer = fanoronaServerFrame;
		}
		displayer.update();
	}

	/**
	 * Gets the port number the server is listening at.
	 * 
	 * @return the port
	 */
	public short getPort() {
		if (this.port == -1) {
			setPort(DEFAULT_PORT);
		}
		return port;
	}

	/**
	 * Forces a 'game over' state, such as when a game develops a loop.
	 */
	public void forceGameOver() {
		this.forceGameOver = true;
		this.interrupt();
	}
}
