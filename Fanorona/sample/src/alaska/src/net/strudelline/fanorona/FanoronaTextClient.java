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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: Auto-generated Javadoc
/**
 * The Class FanoronaTextClient, a text-based client for the Fanorona board.
 */
public class FanoronaTextClient {
    
    /**
     * Column char from int.
     * 
     * @param i the i
     * 
     * @return the char
     */
    static char columnCharFromInt(int i) {
    	switch (i) {
    	case 0: return 'A';
    	case 1: return 'B';
    	case 2: return 'C';
    	case 3: return 'D';
    	case 4: return 'E';
    	case 5: return 'F';
    	case 6: return 'G';
    	case 7: return 'H';
    	case 8: return 'I';
    	}
    	return ' ';
    }	

    /**
     * Column number from string.
     * 
     * @param s the s
     * 
     * @return the int
     */
    static int columnNumberFromString(String s) {
    	if (s.equalsIgnoreCase("A")) return 0;
    	if (s.equalsIgnoreCase("B")) return 1;
    	if (s.equalsIgnoreCase("C")) return 2;
    	if (s.equalsIgnoreCase("D")) return 3;
    	if (s.equalsIgnoreCase("E")) return 4;
    	if (s.equalsIgnoreCase("F")) return 5;
    	if (s.equalsIgnoreCase("G")) return 6;
    	if (s.equalsIgnoreCase("H")) return 7;
    	if (s.equalsIgnoreCase("I")) return 8;
    	return -1;
    }
	
	/** The bsp. */
	static Pattern bsp = Pattern.compile("([ACcWMawm]) ([A-Ga-g])([1-5]) ([A-Ga-g])([1-5])");
	
	/**
	 * Convert single from board space.
	 * 
	 * @param s the s
	 * 
	 * @return the string
	 */
	static String convertSingleFromBoardSpace(String s) {
		Matcher m = bsp.matcher(s);
		if (m.matches()) {
			int fx = columnNumberFromString(m.group(2));
			int fy = Integer.parseInt(m.group(3));
			int tx = columnNumberFromString(m.group(4));
			int ty = Integer.parseInt(m.group(5));
		
			if (m.group(1).equalsIgnoreCase("a") || m.group(1).equalsIgnoreCase("c")) {
				return String.format("C %d %d %d %d",fx,fy,tx,ty);
			} else if (m.group(1).equalsIgnoreCase("w")) {
				return String.format("W %d %d %d %d",fx,fy,tx,ty);
			} else if (m.group(1).length() == 0 || m.group(1).equalsIgnoreCase("M")) {
				return String.format("M %d %d %d %d",fx,fy,tx,ty);
			}
		}
		return null;
	}
	
	/**
	 * Convert to board space.
	 * 
	 * @param l the l
	 * 
	 * @return the string
	 */
	static public String convertToBoardSpace(String l) {
		StringBuilder ret = new StringBuilder();
        Pattern p = Pattern.compile("[ \t]*([a-zA-Z])[ \t]+([0-9]+)[ \t]+([0-9]+)[ \t]+([0-9]+)[ \t]+([0-9]+)(.*)");
        while (true) {
        	Matcher m = p.matcher(l);
        	// get first 5 + rest
        	if (!m.matches()) { // match instead of find.
        		// if the string wasn't whitespace and also doesn't match,
        		// it's an error.
        		break;
        	}
        	String type = m.group(1);
        	// from
        	char fx = columnCharFromInt(Integer.parseInt(m.group(2)));
        	int fy = Integer.parseInt(m.group(3));
        	// to
        	char tx = columnCharFromInt(Integer.parseInt(m.group(4)));
        	int ty = Integer.parseInt(m.group(5));
        	if (type.startsWith("C")) {
        		ret.append(String.format(">>> A %c%d %c%d\n",fx,fy,tx,ty));
        	} else if (type.startsWith("W")) {
        		ret.append(String.format(">>> W %c%d %c%d\n",fx,fy,tx,ty));
        	} else {
        		ret.append(String.format(">>> M %c%d %c%d\n",fx,fy,tx,ty));
        	}
        	l = m.group(6);
        }
        return ret.toString();
	}
	
	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	static public void main(String[] args) throws IOException {
		Socket sock = new Socket();
		sock.connect(new InetSocketAddress("localhost",3266));
		BufferedReader sr = new BufferedReader(new InputStreamReader(sock.getInputStream(),"US-ASCII"));
		BufferedWriter sw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(),"US-ASCII"));
		BufferedReader ir = new BufferedReader(new InputStreamReader(System.in));
		System.out.format("Enter moves one line at a time ending with a . by itself.\n");
		System.out.format("Please enter your name: ");
		System.out.flush();
		String name = ir.readLine();
		sw.write(name+"\n");
		sw.flush();
		
		if (sr.readLine().startsWith("+black")) {
			System.out.println("You are black");
			System.out.flush();
			System.out.print(convertToBoardSpace(sr.readLine()));
			System.out.flush();
		} else {
			System.out.println("You are white");
			System.out.flush();			
		}
		while (true) {
			System.out.println("> ENTER YOUR MOVES (end with .)");
			System.out.flush();
			StringBuilder cmd = new StringBuilder();
			while (true) {
				String line = ir.readLine().trim();
				if (line.equals(".")) {
					System.out.println("> INPUT RECEIVED");
					System.out.flush();
					break;
				}
				String conversion = convertSingleFromBoardSpace(line);
				if (conversion == null) {
					System.out.println("> INVALID");
					System.out.flush();
				} else {
					if (cmd.length() > 0) cmd.append(" ");
					cmd.append(conversion);
				}
			}
			sw.write(cmd.toString()+"\n");
			sw.flush();
			System.out.format(">SR> %s\n",sr.readLine());
			System.out.flush();
			System.out.print(convertToBoardSpace(sr.readLine()));
			System.out.flush();
		}
	}
}
