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

/**
 * The interface for the Fanorona Board itself, ensuring that all the 
 * different types of boards have a common way of interacting with them.
 */
public interface FanoronaBoardInterface {

	/**
	 * Gets the cell color at a given position.
	 * 
	 * @param x the x
	 * @param y the y
	 * 
	 * @return the cell color
	 */
	public abstract FanoronaColor getCellColor(int x, int y);

	/**
	 * String board.
	 * 
	 * @return the string
	 */
	public abstract String stringBoard();

	/**
	 * Gets the winner.
	 * 
	 * @return the winner
	 */
	public abstract FanoronaColor getWinner();

	/**
	 * Processes this move, by this player.
	 * 
	 * @param l the string representing this player's move.
	 * @param currentMover the current player
	 * 
	 * @throws FanoronaMoveException the fanorona move exception
	 */
	public abstract void processMove(String l, FanoronaColor currentMover)
			throws FanoronaMoveException;

	//public abstract String getLog();

	/**
	 * Tests to see if the indicated board is the same as this board.
	 * 
	 * @param b the board interface
	 * 
	 * @return true, if successful
	 */
	public abstract boolean equals(FanoronaBoardInterface b);

	/**
	 * Gets the last move made.
	 * 
	 * @return the last move
	 */
	public abstract String getLastMove();
	
}
