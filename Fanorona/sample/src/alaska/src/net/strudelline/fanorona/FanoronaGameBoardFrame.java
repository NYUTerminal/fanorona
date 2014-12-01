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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.alaska.uaa.cs401.fgm.FanoronaBoardInterface;
import edu.alaska.uaa.cs401.fgm.FanoronaColor;

// TODO: Auto-generated Javadoc
/**
 * The Class FanoronaGameBoardFrame, creating the graphical portion of the Fanorona board.
 */
public class FanoronaGameBoardFrame extends JPanel implements MouseListener {
	// widths used to setup a basic board if the graphical board can't
	// be loaded
	/** The Constant BASIC WIDTH. */
	private static final int BASICWIDTH = 500*2;
	
	/** The Constant BASIC HEIGHT. */
	private static final int BASICHEIGHT = 274*2;
	
	/** The Constant BASIC LINE SIZE. */
	private static final float BASICLINESIZE = 3f;
	
	/** The flag to use the fancy board. */
	private boolean useFancyBoard = false;
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The board's buffered image. */
	private BufferedImage boardbimg;

	/** The array of black icons. */
	BufferedImage[] blackicons = new BufferedImage[45];
	
	/** The array of white icons. */
	BufferedImage[] whiteicons = new BufferedImage[45];
	
	/** The number of black icons available. */
	int blackiconsavailable = 0;
	
	/** The number of white icons available. */
	int whiteiconsavailable = 0;
	
	/** The PNG positions. */
	double[][][] pngpositions = {	
		{{0.0875,0.8889},{0.1883,0.8957},{0.2930,0.8875},{0.3945,0.8848},{0.5016,0.8875},{0.6078,0.8808},{0.7180,0.8835},{0.8242,0.8835},{0.9224,0.8817}},
		{{0.0891,0.6816},{0.1914,0.6843},{0.2914,0.6789},{0.3945,0.6789},{0.5023,0.6721},{0.6086,0.6707},{0.7156,0.6694},{0.8227,0.6762},{0.9203,0.6775}},
		{{0.0891,0.4892},{0.1906,0.4878},{0.2922,0.4892},{0.3977,0.4892},{0.5008,0.4878},{0.6102,0.4878},{0.7148,0.4892},{0.8195,0.4851},{0.9203,0.4770}},
		{{0.0891,0.3117},{0.1891,0.3117},{0.2922,0.3049},{0.3969,0.3089},{0.5008,0.3062},{0.6070,0.2995},{0.7133,0.2981},{0.8227,0.3049},{0.9211,0.2981}},
		{{0.0898,0.1260},{0.1883,0.1220},{0.2953,0.1260},{0.3992,0.1233},{0.5023,0.1206},{0.6070,0.1206},{0.7109,0.1206},{0.8211,0.1179},{0.9188,0.1179}},
	};

	/** The basic positions. */
	double[][][] basicpositions = {
			{{0.10,0.83},{0.20,0.83},{0.30,0.83},{0.40,0.83},{0.50,0.83},{0.60,0.83},{0.70,0.83},{0.80,0.83},{0.90,0.83}},
			{{0.10,0.67},{0.20,0.67},{0.30,0.67},{0.40,0.67},{0.50,0.67},{0.60,0.67},{0.70,0.67},{0.80,0.67},{0.90,0.67}},
			{{0.10,0.50},{0.20,0.50},{0.30,0.50},{0.40,0.50},{0.50,0.50},{0.60,0.50},{0.70,0.50},{0.80,0.50},{0.90,0.50}},
			{{0.10,0.33},{0.20,0.33},{0.30,0.33},{0.40,0.33},{0.50,0.33},{0.60,0.33},{0.70,0.33},{0.80,0.33},{0.90,0.33}},
			{{0.10,0.17},{0.20,0.17},{0.30,0.17},{0.40,0.17},{0.50,0.17},{0.60,0.17},{0.70,0.17},{0.80,0.17},{0.90,0.17}},
	};
	
	/** The positions. */
	double[][][] positions = basicpositions;
	
	/** The board. */
	private FanoronaBoardInterface board = null;
	
	/** The preferences. */
	private Preferences prefs = Preferences.userNodeForPackage(edu.alaska.uaa.cs401.fgm.FanoronaServer.class);
	
	/**
	 * Instantiates a new fanorona game board frame.
	 */
	public FanoronaGameBoardFrame() {
		useFancyBoard = prefs.getBoolean("useFancyBoard", false);
		loadGraphics();
		setPreferredSize(new Dimension(500,274));
		this.addMouseListener(this);
	}
	
	/**
	 * Loads the graphics.
	 */
	private void loadGraphics() {
		blackiconsavailable = 0;
		whiteiconsavailable = 0;
		URLClassLoader loader = (URLClassLoader)this.getClass().getClassLoader();
		try {
			if (!useFancyBoard) throw new IOException();
			URL url = loader.findResource("net/strudelline/fanorona/media/gameboard.png");
			if (url == null) throw new IOException();
			boardbimg = ImageIO.read(url);
			positions = pngpositions;
		} catch (IOException e) {
			// ok, loading the board failed. let's draw one real quick... haha
			boardbimg = new BufferedImage(BASICWIDTH,BASICHEIGHT,BufferedImage.TYPE_4BYTE_ABGR);
			// oh you thought I was joking.
			positions = basicpositions;
			Graphics2D g = boardbimg.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setBackground(Color.WHITE);
			g.setStroke(new BasicStroke(BASICLINESIZE,BasicStroke.CAP_ROUND,BasicStroke.JOIN_BEVEL));
			g.clearRect(0, 0, BASICWIDTH, BASICHEIGHT);
			g.setColor(Color.BLACK);
			for (int x = 0; x < 8; x++) {
				for (int y = 0; y < 4; y++) {
					g.drawLine((int)(BASICWIDTH*positions[y][x][0]),(int)(BASICHEIGHT*positions[y][x][1]),
							(int)(BASICWIDTH*positions[y][x+1][0]),(int)(BASICHEIGHT*positions[y][x+1][1]));					
					g.drawLine((int)(BASICWIDTH*positions[y][x][0]),(int)(BASICHEIGHT*positions[y][x][1]),
							(int)(BASICWIDTH*positions[y+1][x][0]),(int)(BASICHEIGHT*positions[y+1][x][1]));					
				}
			}
			for (int x = 0; x < 8; x++) {
				int y = 4;
				g.drawLine((int)(BASICWIDTH*positions[y][x][0]),(int)(BASICHEIGHT*positions[y][x][1]),
						(int)(BASICWIDTH*positions[y][x+1][0]),(int)(BASICHEIGHT*positions[y][x+1][1]));
			}
			for (int y = 0; y < 4; y++) {
				int x = 8;
				g.drawLine((int)(BASICWIDTH*positions[y][x][0]),(int)(BASICHEIGHT*positions[y][x][1]),
						(int)(BASICWIDTH*positions[y+1][x][0]),(int)(BASICHEIGHT*positions[y+1][x][1]));					
			}
			
			// starting the diagonals.
			g.setStroke(new BasicStroke(BASICLINESIZE,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL));

			for (int y = 0; y < 4; y+=2) {//  /
				for (int x = 0; x < 8; x+=2) {
					g.drawLine((int)(BASICWIDTH*positions[y][x][0]),(int)(BASICHEIGHT*positions[y][x][1]),
							(int)(BASICWIDTH*positions[y+1][x+1][0]),(int)(BASICHEIGHT*positions[y+1][x+1][1]));
				}
			}
			for (int y = 1; y < 4; y+=2) {//  /
				for (int x = 1; x < 8; x+=2) {
					g.drawLine((int)(BASICWIDTH*positions[y][x][0]),(int)(BASICHEIGHT*positions[y][x][1]),
							(int)(BASICWIDTH*positions[y+1][x+1][0]),(int)(BASICHEIGHT*positions[y+1][x+1][1]));
				}
			}
			for (int y = 4; y > 0 ; y-=2) {//  /
				for (int x = 0; x < 8; x+=2) {
					g.drawLine((int)(BASICWIDTH*positions[y][x][0]),(int)(BASICHEIGHT*positions[y][x][1]),
							(int)(BASICWIDTH*positions[y-1][x+1][0]),(int)(BASICHEIGHT*positions[y-1][x+1][1]));
				}
			}
			for (int y = 3; y > 0; y-=2) {//  /
				for (int x = 1; x < 8; x+=2) {
					g.drawLine((int)(BASICWIDTH*positions[y][x][0]),(int)(BASICHEIGHT*positions[y][x][1]),
							(int)(BASICWIDTH*positions[y-1][x+1][0]),(int)(BASICHEIGHT*positions[y-1][x+1][1]));
				}
			}
		}
		
		if (positions != basicpositions) {
			for (int i = 0; i < 45; i++) {
				URL url = loader.findResource(String.format("net/strudelline/fanorona/media/black %d.png",i+1));
				if (url == null) break;
				try {
					blackicons[i] = ImageIO.read(url);
				} catch (IOException e) {
					break;
				};
				blackiconsavailable++;
			}
			for (int i = 0; i < 45; i++) {
				URL url = loader.findResource(String.format("net/strudelline/fanorona/media/white %d.png",i+1));
				if (url == null) break;
				try {
					whiteicons[i] = ImageIO.read(url);
				} catch (IOException e) {
					break;
				};
				whiteiconsavailable++;
			}
		}
		
		if (blackiconsavailable == 0) {
			blackicons[0] = new BufferedImage(100,100,BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g = blackicons[0].createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setStroke(new BasicStroke(BASICLINESIZE));
			g.setBackground(new Color(0,0,0,0));
			g.clearRect(0, 0, 100, 100);
			g.setColor(Color.BLACK);
			g.fillOval((int)BASICLINESIZE, (int)(BASICLINESIZE), (int)(100-BASICLINESIZE*2), (int)(100-BASICLINESIZE*2));
			blackiconsavailable++;
		}
		
		if (whiteiconsavailable == 0) {
			whiteicons[0] = new BufferedImage(100,100,BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g = whiteicons[0].createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setStroke(new BasicStroke(BASICLINESIZE));
			g.setBackground(new Color(0,0,0,0));
			g.clearRect(0, 0, 100, 100);
			g.setColor(Color.WHITE);
			g.fillOval((int)BASICLINESIZE, (int)(BASICLINESIZE), (int)(100-BASICLINESIZE*2), (int)(100-BASICLINESIZE*2));
			g.setColor(Color.BLACK);
			g.drawOval((int)BASICLINESIZE, (int)(BASICLINESIZE), (int)(100-BASICLINESIZE*2), (int)(100-BASICLINESIZE*2));
			whiteiconsavailable++;
		}
		
		System.out.format("Found %d black and %d white\n",blackiconsavailable,whiteiconsavailable);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent arg0) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent arg0) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent arg0) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent arg0) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent arg0) {
		System.out.format("%.4f,%.4f\n",(double)arg0.getX()/((double)getWidth()),(double)arg0.getY()/((double)getHeight()));
	}
	
	/**
	 * Sets the board.
	 * 
	 * @param b the new board
	 */
	public void setBoard(FanoronaBoardInterface b) {
		this.board = b;
		repaint();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g1d) {
		// first check and see if fancyboard has changed and, if so, reload
		// the graphics
		
		if (useFancyBoard != prefs.getBoolean("useFancyBoard", false)) {
			useFancyBoard = !useFancyBoard; // it's boolean.
			loadGraphics();
		}
		
		Graphics2D g = (Graphics2D) g1d; //need this for bilinear interpolation.
		int width = getWidth();
		int height = getHeight();

		double windowaspect = ((double)width)/((double)height);
		// as height increases, aspect decreases

		double imgwidth = boardbimg.getWidth();
		double imgheight = boardbimg.getHeight();
		double aspect = imgwidth/imgheight;
		this.setPreferredSize(new Dimension(width,(int)((double)width / aspect)));		

		if (windowaspect < aspect) { // too much height
			// let's try adding width
			//this.setPreferredSize(new Dimension((int)((double)height * aspect),height));
			height = (int) (((double)width) / aspect);
		} else { // too much width
			// let's try adding height
			//this.setPreferredSize(new Dimension(width,(int)((double)width / aspect)));
			width = (int) (((double)height) * aspect);
		}

		
		
		
		/*
		 * This area is setting the size of the icons.  Change bubblesize to affect change.
		 */
		double bubblesize = .12;
		int bubblewidth = (int) (width * bubblesize);
		int bubbleheight = (int) (height * bubblesize);
		if (bubblewidth > bubbleheight) {
			bubblewidth = bubbleheight;
		} else {
			bubbleheight = bubblewidth;
		}

		// less ugly, less speed.  but still not as bad as bicubic :D
		// HOLY COW is nearest neighbor ugly though!
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		g.clearRect(0, 0, getWidth(), getHeight());
		g.drawImage(boardbimg, 0, 0, width, height, 0, 0, boardbimg.getWidth(), boardbimg.getHeight(), this);

		int hbw = bubblewidth >> 1;
		int hbh = bubbleheight >> 1;
		
		if (board != null) {
			for (int x = 0; x < 9;x++) {
				for (int y = 0; y < 5; y++) {
					int midx = (int) ((double)width * (positions[y][x][0]));
					int midy = (int) ((double)height * (positions[y][x][1]));

					FanoronaColor cell = board.getCellColor(x,y);
					if (cell != null) {
						switch (cell) {
						case White:
							if (whiteiconsavailable < 1) {
								g.setColor(Color.white);
								g.fillOval(midx-hbw, midy-hbh, bubblewidth, bubbleheight);
							} else {
								BufferedImage icon = whiteicons[(x*y)%whiteiconsavailable];
								g.drawImage(icon, midx-hbw, midy-hbh, midx+hbw, midy+hbh, 0, 0, icon.getWidth(), icon.getHeight(), this);
							}
							break;
						case Black:
							if (blackiconsavailable < 1) {
								g.setColor(Color.black);
								g.fillOval(midx-hbw,midy-hbh,bubblewidth,bubbleheight);
							} else {
								BufferedImage icon = blackicons[(x*y)%blackiconsavailable];
								g.drawImage(icon, midx-hbw, midy-hbh, midx+hbw, midy+hbh, 0, 0, icon.getWidth(), icon.getHeight(), this);
							}
							break;
						}
					}
				}
			}
		}
	}
	
	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 */
	static public void main(String args[]) {
		final JFrame frame = new JFrame();
		frame.setTitle("Fanorona Board Test");
		frame.setSize(450, 500);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		FanoronaGameBoardFrame bp = new FanoronaGameBoardFrame();
		frame.add(bp,"Center");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
				frame.pack();
			}
		});
		//frame.pack();
	}
}
