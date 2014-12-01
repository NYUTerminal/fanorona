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


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO: Auto-generated Javadoc
/**
 * The class FanoronaLinkBoard impliments the FanoronaBoardInterface interface so 
 * that all methods that use a Fanorona board will be able to use an instance of this class.
 * 
 * The purpose of this class is to create and maintain a cyclical, non-weighted graph 
 * representing the Fanorona board, with each position in the 9x5 board being the nodes, and 
 * the paths between each position being the edges.
 * 
 * Part of maintaining the board is to process moves, save and restore from a previous state,
 * output the current state of the board, and decide the winner.
 */
public class FanoronaLinkBoard implements FanoronaBoardInterface {
	//StringBuilder log = new StringBuilder();
	/** The log of the last moves made on the board, to be used to create a coherent move-string. */
	StringBuilder sublog = new StringBuilder();
	
	/** The variable containing the coherent move-string created using the sublog variable. */
	String lastMove = null; // to let the server have a cleaned copy to send.
	
	/**
	 * The class LinkSpace represents each position/node on the board/graph and all
	 * the corresponding paths/edges the pieces can move along.
	 */
	private class LinkSpace {
		
		/** The color of the current piece in this position. */
		FanoronaColor color;
		
		/** The last piece that was here. */
		FanoronaColor last;
		
		/** The move index. */
		int moveIndex = -1;
		
		/** The x and y coordinates of the position that this LinkSpace represents. */
		final int x,y;
		
		/** The neighbors. */
		private ArrayList<LinkSpace> neighbors = null; // we can't really extract them yet anyway
		
		/**
		 * Instantiates a new LinkSpace.
		 * 
		 * @param c the c
		 * @param x the x
		 * @param y the y
		 */
		public LinkSpace(FanoronaColor c, int x, int y) {
			this.color = c;
			this.x = x;
			this.y = y;
		}
		
		/**
		 * Gets the color of the piece at this position.
		 * 
		 * @return the color
		 */
		public FanoronaColor getColor() {
			return color;
		}
		
		/**
		 * Saves the piece at this position.
		 */
		public void save() {
			// back everything up and reset temp variables
			last = color;
			moveIndex = -1;
		}
		
		/**
		 * Reverts the postion so it contains the last piece saved.
		 */
		public void revert() {
			// roll back changes and reset temp variables
			color = last;
			moveIndex = -1;
		}
		
		/**
		 * Gets the move index.
		 * 
		 * @return the move index
		 */
		public int getMoveIndex() {
			return moveIndex;
		}
		
		/**
		 * Sets the move index.
		 * 
		 * @param mi the new move index
		 */
		public void setMoveIndex(int mi) {
			moveIndex = mi;
		}
		
		/**
		 * The list of neighbors that can be reached from this position.
		 * 
		 * @return the iterable< link space>
		 */
		public Iterable<LinkSpace> neighbors() {
			if (neighbors == null) {
				neighbors = new ArrayList<LinkSpace>();
				if (((x+y) & 1) == 0) { 
					// has diagonals
					if (x > 0) {
						if (y > 0) neighbors.add(spaces[x-1][y-1]);
						if (y < 4) neighbors.add(spaces[x-1][y+1]);
					}
					if (x < 8) {
						if (y > 0) neighbors.add(spaces[x+1][y-1]);
						if (y < 4) neighbors.add(spaces[x+1][y+1]);
					}
				}
				if (x > 0) neighbors.add(spaces[x-1][y]);
				if (x < 8) neighbors.add(spaces[x+1][y]);
				if (y > 0) neighbors.add(spaces[x][y-1]);
				if (y < 4) neighbors.add(spaces[x][y+1]);
			}
			return neighbors;
		}
		
		/**
		 * Sets the color of the piece at this position.
		 * 
		 * @param color the new color
		 */
		public void setColor(FanoronaColor color) {
			this.color = color;
		}
	}
	
	/** Sets up the paths/edges of the board/graph and sets up the initial board state. */
	final LinkSpace[][] spaces = new LinkSpace[9][5];
	{
		for (int x = 0; x < 9; x ++) {
			for (int y = 0; y < 5; y++) {
				if (y > 2) // top 2 rows
					spaces[x][y] = new LinkSpace(FanoronaColor.Black,x,y);
				else if (y < 2) // bottom 2 rows
					spaces[x][y] = new LinkSpace(FanoronaColor.White,x,y);
				else if (x == 4) // middle row middle space
					spaces[x][y] = new LinkSpace(null,x,y);
				else if ((x & 1) == 1 && x < 4) // middle row odd left
					spaces[x][y] = new LinkSpace(FanoronaColor.White,x,y);
				else if ((x & 1) == 1 && x > 4) // middle row odd right
					spaces[x][y] = new LinkSpace(FanoronaColor.Black,x,y);
				else if (x < 4) // middle row even left
					spaces[x][y] = new LinkSpace(FanoronaColor.Black,x,y);
				else // even right
					spaces[x][y] = new LinkSpace(FanoronaColor.White,x,y);
			}
		}
	}
	
	/**
	 * Instantiates a new, empty fanorona link board.
	 */
	public FanoronaLinkBoard() {
		// empty.
	}
	
	/**
	 * Instantiates a new fanorona link board, setting it to the same state
	 * as another fanorona board.
	 * 
	 * @param b the original fanorona board this one is a copy of. 
	 */
	public FanoronaLinkBoard(FanoronaBoardInterface b) {
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 5; y++) {
				spaces[x][y].setColor(b.getCellColor(x,y));
			}
		}
		//log = new StringBuilder();
		//log.append(b.getLog());
	}
	

	/* (non-Javadoc)
	 * @see edu.alaska.uaa.cs401.fgm.FanoronaBoardInterface#getCellColor(int, int)
	 */
	public FanoronaColor getCellColor(int x, int y) {
		return spaces[x][y].getColor();
	}

	/**
	 * This method indicates if there is a capture move possible by this player, 
	 * anywhere on the board.
	 * 
	 * @param c the color of the player
	 * 
	 * @return true, if the player can make a capture
	 * @return false, if the player has no capture moves to make
	 */
	public boolean captureAvailable(FanoronaColor c) {
		// this function only needs to find captures available without following other
		// rules (this is only used on the first move in a series)
		// test for capture by withdrawal first
		for (LinkSpace[] sv : spaces) {
			for (LinkSpace s : sv) {
				for (LinkSpace n : s.neighbors()) {
					if (s.getColor() != c) continue; 
					// this is our piece 
					if (n.getColor() != c.opposite()) continue;
					// there's the opposite piece in the neighboring space
					int oppx = s.x -(n.x-s.x);
					int oppy = s.y -(n.y-s.y);
					if (oppx < 0 || oppy < 0 || oppx > 8 || oppy > 4) continue; 
					// the opposite space isn't off the board 
					if (spaces[oppx][oppy].getColor() == null) return true;
					// there's a blank space to withdraw into
				}
			}
		}
		for (LinkSpace[] sv : spaces) {
			for (LinkSpace s : sv) {
				for (LinkSpace n : s.neighbors()) {
					if (s.getColor() != c) continue; 
					// this is our piece
					if (n.getColor() != null) continue;
					// there's no piece in the neighboring space
					int thx = s.x + (2*(n.x-s.x));
					int thy = s.y + (2*(n.y-s.y));
					if (thx < 0 || thy < 0 || thx > 8 || thy > 4) continue; 
					// the opposite space isn't off the board 
					if (spaces[thx][thy].getColor() == c.opposite()) return true;
					// this piece is the correct color to advance to
				}
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see edu.alaska.uaa.cs401.fgm.FanoronaBoardInterface#getWinner()
	 */
	public FanoronaColor getWinner() {
		FanoronaColor winner = null;
		for (LinkSpace[] sv : spaces) {
			for (LinkSpace s : sv) {
				if (winner == null) winner = s.getColor(); // either null or the first encountered.
				else if (s.getColor() == null) continue;
				else if (s.getColor() != winner) return null;
			}
		}
		return winner; // either null if there are no pieces on the board or the winner
	}

	/**
	 * Saves the board's current state.
	 */
	private void save() {
		for (LinkSpace[] sv : spaces) {
			for (LinkSpace s : sv) {
				s.save();
			}
		}		
	}
	
	/**
	 * Reverts the board to the previous saved state.
	 */
	private void revert() {
		for (LinkSpace[] sv : spaces) {
			for (LinkSpace s : sv) {
				s.revert();
			}
		}		
	}
	
	/* (non-Javadoc)
	 * @see edu.alaska.uaa.cs401.fgm.FanoronaBoardInterface#processMove(java.lang.String, edu.alaska.uaa.cs401.fgm.FanoronaColor)
	 */
	public void processMove(String l, FanoronaColor currentMover)
			throws FanoronaMoveException {
    	if (l.trim().length() == 0) {
    		// just whitespace.  this means they're trying to pass.
    		// orrrrrrrrr it means they hit enter too many times.
    		// either way, return an error.  if they hit enter twice,
    		// trying to process it as their move would be incorrect
    		// anyway.
        	throw new FanoronaMoveException().setErrorText(String.format("passing is not allowed until after a move is made"));
        	// this is OUTSIDE of the try because we haven't saved yet.
        	// nothing is run after this throw.
    	}
    	
    	sublog.setLength(0);
    	
		try {
			save(); // save the current board state.  also, this will reset
			        // all of the movement processing metadata stored in
			        // each of the LinkSpaces.
			
			boolean paika = false; // if a paika has been performed
			int moveindex = 0;
			
			// pattern to process the moves.
			// this will pull out the first 5 tokens plus all remaining text.
			// it will also eat up any white space.
	        Pattern p = Pattern.compile("[ \t]*([a-zA-Z])([0-9]+)([0-9]+)([0-9]+)([0-9]+)(.*)");
	        while (true) {
	        	// each move in a string of moves must follow certain rules
	        	// the only rules which are submove specific are the rules
	        	// regarding paika vs capture and also passing on the move.
	        	// all other moves follow the same set of rules and so can
	        	// be processed in order without much regard for which move
	        	// in the string they happen to be.
	        	// for move specific rules, see near the end of this block.
	        	
	        	if (l.trim().length() == 0) break; // just whitespace so we're done.
	        	
	        	if (paika) {
	            	throw new FanoronaMoveException().setErrorText(String.format("Submove %d: further movement is not allowed after paika",moveindex));
	            }
	        	
	            Matcher m = p.matcher(l);
	            // get first 5 + rest
	            if (!m.matches()) { // match instead of find.
	            	// if the string wasn't whitespace and also doesn't match,
	            	// it's an error.
	            	throw new FanoronaMoveException().setErrorText(String.format("Submove %d: %s is not a valid movement",moveindex,l));
	            }
	            String type = m.group(1);
	            // from
	            int fx = Integer.parseInt(m.group(2));
	            int fy = Integer.parseInt(m.group(3));
	            // to
	            int tx = Integer.parseInt(m.group(4));
	            int ty = Integer.parseInt(m.group(5));
	            
	            if (sublog.length() > 0) sublog.append(' ');
	            sublog.append(String.format("%s%d%d%d%d",type,fx,fy,tx,ty));
	            
	            // print the move just for debugging/whatever
	            //System.out.format("%s: %s from %d,%d to %d,%d\n",currentMover,type,fx,fy,tx,ty);
	            
	            // check if the source space is on the board
	            if (fx < 0 || fx > 8)
	            	throw new FanoronaMoveException().setErrorText(String.format("Submove %d: source x must be 0 <= x <= 8",moveindex));
	            if (fy < 0 || fy > 5)
	            	throw new FanoronaMoveException().setErrorText(String.format("Submove %d: source y must be 0 <= x <= 4",moveindex));	            
	            
	            // it is so let's get a reference to it :D
	            LinkSpace source = spaces[fx][fy];
	            
	            // move must be made from the last piece used unless this is the first move 
	            if (source.getMoveIndex() == -1 && moveindex > 0)
	            	throw new FanoronaMoveException().setErrorText(String.format("Submove %d: a move continuation must be made from the last piece moved",moveindex));
	            
	            // check that the piece in the source space is our color.
	            // this won't have a null exception because there is a LinkSpace
	            // in every valid position.  There is not, however, necessarily
	            // a piece so source.getColor() might return null but source
	            // will never be null at this point.
	            if (source.getColor() != currentMover) {
	            	throw new FanoronaMoveException().setErrorText(String.format("Submove %d: source piece is of the wrong color",moveindex));
	            }
	            
	            LinkSpace destination = null;
	            
	            // see if the destination is a neighbor of source.
	            // (LinkSpace x).neighbors() returns all of the spaces which are
	            // linked to by x.
	            for (LinkSpace n : source.neighbors()) {
	            	if (n.x == tx && n.y == ty) {
	            		destination = n;
	            		break;
	            	}
	            }
	            // if destination is null, we didn't find it in the neighbors list
	            // so this is an invalid move.
	            if (destination == null) {
	            	throw new FanoronaMoveException().setErrorText(String.format("Submove %d: %d,%d doesn't neighbor with %d,%d",moveindex,fx,fy,tx,ty));
	            }

	            // the destination is occupied
	            if (destination.getColor() != null) {
	            	throw new FanoronaMoveException().setErrorText(String.format("Submove %d: %d,%d -> %d,%d is occupied",moveindex,fx,fy,tx,ty));
	            }

	            // the destination has been moved to in this submove already.
	            if (destination.getMoveIndex() != -1) {
	            	throw new FanoronaMoveException().setErrorText(String.format("Submove %d: %d,%d -> %d,%d: piece has already been on this spot",moveindex,fx,fy,tx,ty));
	            }
	            // this tests if the move is the second in a straight line.
	            // it must test for moveindex > 0 because if moveindex is 0, the
	            // surrounding pieces will have moveindex = -1 as a default value
	            // but also, if moveindex is 0, there's no reason for further
	            // processing since it can't be the second in a line of anything
	            // if it's the first.
	            if (moveindex > 0) {
	            	int oppx = (fx - (tx - fx)); // 33->22 (fx - (tx-fx)) = (3 - (2 - 3)) = 3 - (-1) = 4
	            	int oppy = (fy - (ty - fy));
	            	if (!(oppx > 8 || oppx < 0 || oppy > 4 || oppy < 0)) {
	            		if (spaces[oppx][oppy].getMoveIndex() == moveindex-1) {
	    	            	throw new FanoronaMoveException().setErrorText(String.format("Submove %d: %d,%d -> %d,%d: cannot move in a straight line on consecutive moves",moveindex,fx,fy,tx,ty));
	            		}
	            	} // W _ _ B W
	            }     //   2 1 0
	            
	            // all actual movement is done after all of these checks
	            // but if it's valid, the side effects of the movement are
	            // done INSIDE the checks.  thus for paika, it just checks
	            // if it's valid, for CbA, it removes the pieces beyond the
	            // movement and for CbW, it removes the ones that are being
	            // moved away from.  none of these if statements move the
	            // actual piece in question though.
	            // in CbA and CbW, the checks are roughly identical except for
	            // the x/ystep which holds the relative direction to continue
	            // moving the checker as it consumes pieces. (it will be {-1,0,1})
	            // the first check is whether the *first* space being consumed
	            // exists.
	            // the second check is whether the *first* space being consumed
	            // is of the opposite color of currentMover.
	            // after this, a loop is done which consumes the pieces in a
	            // straight line defined by tx,ty to (tx+xstep,ty+xstep) so long
	            // as the space being checked for consumption still exists
	            // and the space contains a piece of the opposite color to
	            // currentMover.
	            if (type.equalsIgnoreCase("M")) { // paika
	            	// can only be done on the first move.
	            	paika = true;
	            	if (moveindex == 0) {
	            		if (captureAvailable(currentMover)) {
	            			throw new FanoronaMoveException().setErrorText(String.format("Submove %d: Capture must be made when available on the first move in a series",moveindex));
	            		}
	            	} else {
            			throw new FanoronaMoveException().setErrorText(String.format("Submove %d: paika only available as the first move in a series",moveindex));	            		
	            	}
	            } else if (type.equalsIgnoreCase("C")) { // capture by advance
	            	int xstep = (tx-fx);
	            	int ystep = (ty-fy);
					int thx = fx + (2*xstep);
					int thy = fy + (2*ystep);
					if (thx > 8 || thx < 0 || thy > 4 || thy < 0) {
            			throw new FanoronaMoveException().setErrorText(String.format("Submove %d: cannot capture by advance because the space doesn't exist",moveindex));
					}
					if (spaces[thx][thy].getColor() != currentMover.opposite()) {
						throw new FanoronaMoveException().setErrorText(String.format("Submove %d: cannot capture by advance because the piece being captured isn't %s",moveindex,currentMover.opposite()));
					}
					while (true) {
						spaces[thx][thy].setColor(null);
						thx += xstep;
						thy += ystep;
						if (thx > 8 || thx < 0 || thy > 4 || thy < 0) break;
						if (spaces[thx][thy].getColor() != currentMover.opposite()) break;
					}
	            } else if (type.equalsIgnoreCase("W")) { // capture by withdrawal
	            	int xstep = -(tx-fx);
	            	int ystep = -(ty-fy);
	            	int oppx = (fx + xstep);
	            	int oppy = (fy + ystep);
					if (oppx > 8 || oppx < 0 || oppy > 4 || oppy < 0) {
            			throw new FanoronaMoveException().setErrorText(String.format("Submove %d: cannot capture by withdrawal because the space doesn't exist",moveindex));
					}
					if (spaces[oppx][oppy].getColor() != currentMover.opposite()) {
						throw new FanoronaMoveException().setErrorText(String.format("Submove %d: cannot capture by advance because the piece being captured isn't %s",moveindex,currentMover.opposite()));
					}
					while (true) {
						spaces[oppx][oppy].setColor(null);
						oppx += xstep;
						oppy += ystep;
						if (oppx > 8 || oppx < 0 || oppy > 4 || oppy < 0) break;
						if (spaces[oppx][oppy].getColor() != currentMover.opposite()) break;
					}
	            } else {
					throw new FanoronaMoveException().setErrorText(String.format("Submove %d: %s isn't a valid move type",moveindex,type));
	            }
	            
	            // actually move the piece.
    			destination.setColor(source.getColor());
    			source.setMoveIndex(moveindex++);
    			destination.setMoveIndex(moveindex);
    			source.setColor(null);
	            
    			// set the input string to be the remaining commands on the line.
	            l = m.group(6);
	        }
		} catch (FanoronaMoveException e) {
			// unwind protect.
			// if there was an error, revert the board and throw it back to the
			// server for further processing (sending "-"+e.getErrorText() to the client)
			revert();
			throw e;
		}
		//if (log.length() > 0) log.append('\n');
		//log.append(sublog);
		lastMove = sublog.toString();
	}

	/* (non-Javadoc)
	 * @see edu.alaska.uaa.cs401.fgm.FanoronaBoardInterface#getLastMove()
	 */
	public String getLastMove() {
		return lastMove;
	}
	
//	public String getLog() {
//		return log.toString();
//	}
	
	/* (non-Javadoc)
 * @see edu.alaska.uaa.cs401.fgm.FanoronaBoardInterface#stringBoard()
 */
public String stringBoard() {
		StringBuilder sb = new StringBuilder();
		for (int y = 4; y >= 0; y--) {
			for (int x = 0; x < 9; x++) {
				try {
					sb.append(spaces[x][y].getColor().getChar());
				} catch (NullPointerException npe) {
					sb.append(' ');
				}
			}
			sb.append('\n');
		}
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see edu.alaska.uaa.cs401.fgm.FanoronaBoardInterface#equals(edu.alaska.uaa.cs401.fgm.FanoronaBoardInterface)
	 */
	public boolean equals(FanoronaBoardInterface b) {
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 5; y++) {
				if (b.getCellColor(x,y) != getCellColor(x,y)) return false;
			}
		}
		return true;
	}

}
