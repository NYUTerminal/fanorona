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


import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The class FanoronaServerTest, a class to use JUnit to test every method FanoronaBoard class.
 */
public class FanoronaServerTest {

	/**
	 * Sets up before the class.
	 * 
	 * @throws Exception the exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * Tears down after class.
	 * 
	 * @throws Exception the exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * TODO Sets up the testing class.
	 * 
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Tears down the testing class.
	 * 
	 * @throws Exception the exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Testing the server connections and logging.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testServerConnectionsAndLogging() throws Exception {
		FanoronaServer fs = new FanoronaServer().setShutdownAfterNGames(1);
		short port = fs.getPort();
		fs.start();
		for (int i = 0; i < 100; i++) {
			Thread.sleep(10);
			Thread.yield();
		}
		String whitecommands = "      C4142                    :C7071:C3233 W3342 W4241 W4131 W3140:C8273 W7362 W6261:M6171:M7170:M7080:M8081:M8180:".replaceAll(":", System.getProperty("line.separator"));
		String blackcommands = "W5344 C4443:C3342 C4241 W4151:C5251 W5141 W4142 C4231 W3132 C3222 C2211 W1112:M1211:M2333:M8373:M7372:M7271:W7162:".replaceAll(":", System.getProperty("line.separator"));
//		String whitecommands = "C 4 1 4 2\nC 7 0 7 1\nC 3 2 3 3 W 3 3 4 2 W 4 2 4 1 W 4 1 3 1 W 3 1 4 0\nC 8 2 7 3 W 7 3 6 2 W 6 2 6 1\nM 6 1 7 1\nM 7 1 7 0\nM 7 0 8 0\nM 8 0 8 1\nM 8 1 8 0\n";
//		String blackcommands = "W 5 3 4 4 C 4 4 4 3\nC 3 3 4 2 C 4 2 4 1 W 4 1 5 1\nC 5 2 5 1 W 5 1 4 1 W 4 1 4 2 C 4 2 3 1 W 3 1 3 2 C 3 2 2 2 C 2 2 1 1 W 1 1 1 2\nM 1 2 1 1\nM 2 3 3 3\nM 8 3 7 3\nM 7 3 7 2\nM 7 2 7 1\nW 7 1 6 2\n";

		System.out.println("cheese");
		Socket socka = new Socket("127.0.0.1",port);
		System.out.println("blah");
		Socket sockb = new Socket("127.0.0.1",port);
		System.out.println("arf");
		final BufferedReader ar = new BufferedReader(new InputStreamReader(socka.getInputStream(),"US-ASCII"));
		final BufferedReader br = new BufferedReader(new InputStreamReader(sockb.getInputStream(),"US-ASCII"));
		final OutputStreamWriter aw = new OutputStreamWriter(socka.getOutputStream(),"US-ASCII");
		final OutputStreamWriter bw = new OutputStreamWriter(sockb.getOutputStream(),"US-ASCII");
		
		aw.write("ted\n");aw.flush();
		bw.write("bob\n");bw.flush();

		final String ac;
		final String bc;
		String x = ar.readLine();
		System.out.println("Game ID: "+x.split(" ")[1]);
		if (x.startsWith("+black")) {
			ac = blackcommands;
			bc = whitecommands;
			x = br.readLine();
			if (!x.startsWith("+white")) {
				fail("both players are black!?");
			}
		} else {
			ac = whitecommands;
			bc = blackcommands;
			x = br.readLine();
			if (!x.startsWith("+black")) {
				fail("both players are white!?");
			}			
		}
		
		Thread at = new Thread(new Runnable() {
			public void run() {
				System.out.println("sending-A!");
				for (int i = 0; i < ac.length(); i++) {
					try {
						aw.write(ac,i,1);
						aw.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.err.println("failed to send in thread-A");
					}
				}
				System.out.flush();
			}
		});
		Thread bt = new Thread(new Runnable() {
			public void run() {
				System.out.println("sending-B!");
				for (int i = 0; i < bc.length(); i++) {
					try {
						bw.write(bc,i,1);
						bw.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.err.println("failed to send in thread-B");
					}
				}
				System.out.println("done sending-B!");
				System.out.flush();
			}
		});
		at.start();
		bt.start();
		at.join();
		bt.join();
		//		if (x.startsWith("+black")) {
//			aw.write(blackcommands);aw.flush();
//			bw.write(whitecommands);bw.flush();
//			if (!x.startsWith("+white")) {
//				fail("both players are black?!");
//			}
//		} else {
//			aw.write(whitecommands);aw.flush();
//			bw.write(blackcommands);bw.flush();
//			x = br.readLine();
//			if (!x.startsWith("+black")) {
//				fail("both players are white?!");
//			}
//		}
		fs.join();
		
		System.out.println(fs.getBoard().stringBoard());
		if (fs.getBoard().getWinner() != FanoronaColor.Black) fail ("Black won...");
		if (!fs.getLog().equals("White's move: C4142#Black's move: W5344 C4443#White's move: C7071#Black's move: C3342 C4241 W4151#White's move: C3233 W3342 W4241 W4131 W3140#Black's move: C5251 W5141 W4142 C4231 W3132 C3222 C2211 W1112#White's move: C8273 W7362 W6261#Black's move: M1211#White's move: M6171#Black's move: M2333#White's move: M7170#Black's move: M8373#White's move: M7080#Black's move: M7372#White's move: M8081#Black's move: M7271#White's move: M8180#Black's move: W7162#".replaceAll("#", System.getProperty("line.separator"))))
			fail("Log is invalid. "+fs.getLog());
	}
	
	
	
	
	/**
	 * Testing the server cycle detection.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testServerCycleDetection() throws Exception {
		FanoronaServer fs = new FanoronaServer().setShutdownAfterNGames(1);
		short port = fs.getPort();
		fs.start();
		for (int i = 0; i < 100; i++) {
			Thread.sleep(10);
			Thread.yield();
		}
		String whitecommands = "C4142:C7071:C3233 W3342 W4241 W4131 W3140:C8273 W7362 W6261:M6171:M7161:M6171:M7161:M6171:M7161:M6171:M7161:M6171:M7161:M6171:M7161:M6171:M7161:M6171:M7161:".replaceAll(":", System.getProperty("line.separator"));
		String blackcommands = "W5344 C4443:C3342 C4241 W4151:C5251 W5141 W4142 C4231 W3132 C3222 C2211 W1112:M1211            :M2333:M3323:M2333:M3323:M2333:M3323:M2333:M3323:M2333:M3323:M2333:M3323:M2333:M3323:M2333:M3323:".replaceAll(":", System.getProperty("line.separator"));
//		String whitecommands = "C 4 1 4 2\nC 7 0 7 1\nC 3 2 3 3 W 3 3 4 2 W 4 2 4 1 W 4 1 3 1 W 3 1 4 0\nC 8 2 7 3 W 7 3 6 2 W 6 2 6 1\nM 6 1 7 1\nM 7 1 7 0\nM 7 0 8 0\nM 8 0 8 1\nM 8 1 8 0\n";
//		String blackcommands = "W 5 3 4 4 C 4 4 4 3\nC 3 3 4 2 C 4 2 4 1 W 4 1 5 1\nC 5 2 5 1 W 5 1 4 1 W 4 1 4 2 C 4 2 3 1 W 3 1 3 2 C 3 2 2 2 C 2 2 1 1 W 1 1 1 2\nM 1 2 1 1\nM 2 3 3 3\nM 8 3 7 3\nM 7 3 7 2\nM 7 2 7 1\nW 7 1 6 2\n";
		
		System.out.println("cheese");
		Socket socka = new Socket("127.0.0.1",port);
		System.out.println("blah");
		Socket sockb = new Socket("127.0.0.1",port);
		System.out.println("arf");
		final BufferedReader ar = new BufferedReader(new InputStreamReader(socka.getInputStream(),"US-ASCII"));
		final BufferedReader br = new BufferedReader(new InputStreamReader(sockb.getInputStream(),"US-ASCII"));
		final OutputStreamWriter aw = new OutputStreamWriter(socka.getOutputStream(),"US-ASCII");
		final OutputStreamWriter bw = new OutputStreamWriter(sockb.getOutputStream(),"US-ASCII");
		
		aw.write("ted\n");aw.flush();
		bw.write("bob\n");bw.flush();

		final String ac;
		final String bc;
		String x = ar.readLine();
		System.out.println("Game ID: "+x.split(" ")[1]);
		if (x.startsWith("+black")) {
			ac = blackcommands;
			bc = whitecommands;
			x = br.readLine();
			if (!x.startsWith("+white")) {
				fail("both players are black!?");
			}
		} else {
			ac = whitecommands;
			bc = blackcommands;
			x = br.readLine();
			if (!x.startsWith("+black")) {
				fail("both players are white!?");
			}			
		}
		
		Thread at = new Thread(new Runnable() {
			public void run() {
				System.out.println("sending-A!");
				for (int i = 0; i < ac.length(); i++) {
					try {
						aw.write(ac,i,1);
						aw.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.err.println("failed to send in thread-A");
					}
				}
				System.out.flush();
			}
		});
		Thread bt = new Thread(new Runnable() {
			public void run() {
				System.out.println("sending-B!");
				for (int i = 0; i < bc.length(); i++) {
					try {
						bw.write(bc,i,1);
						bw.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.err.println("failed to send in thread-B");
					}
				}
				System.out.println("done sending-B!");
				System.out.flush();
			}
		});
		at.start();
		bt.start();
		at.join();
		bt.join();
		//		if (x.startsWith("+black")) {
//			aw.write(blackcommands);aw.flush();
//			bw.write(whitecommands);bw.flush();
//			if (!x.startsWith("+white")) {
//				fail("both players are black?!");
//			}
//		} else {
//			aw.write(whitecommands);aw.flush();
//			bw.write(blackcommands);bw.flush();
//			x = br.readLine();
//			if (!x.startsWith("+black")) {
//				fail("both players are white?!");
//			}
//		}
		fs.join();
		
		System.out.println(fs.getBoard().stringBoard());
		if (fs.getBoard().getWinner() != null) fail(String.format("This game was a draw but it says %s won!",fs.getBoard().getWinner()));
	}
}
