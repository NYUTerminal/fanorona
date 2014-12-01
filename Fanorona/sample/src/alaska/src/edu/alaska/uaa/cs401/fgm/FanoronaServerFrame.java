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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import net.strudelline.fanorona.FanoronaGameBoardFrame;
import net.strudelline.fanorona.FanoronaJoaAI;
import net.strudelline.fanorona.FanoronaRandomAI;


// TODO: Auto-generated Javadoc
/**
 * The Class FanoronaServerFrame.
 * 
 * @author James AND MATT!! YAY
 * 
 * Tests
 * 
 * When the UI opens, a server should be spawned capable of accepting two TCP connections
 * which will then manipulate the internal game board which will also display on the screen
 * whenever a change is made.  THE END
 * oh it'll say "white wins" or "black wins" when one wins.
 * 
 * Both board styles:
 * Select Fancy Board
 * verify that the fancy board loads
 * unselect Fancy Board
 * verify that the simple board loads
 * 
 * Random Sides:
 * Unselect Random Sides
 * Start a game with todd first and random second
 * do this ten times and verify that todd is white each time
 * Select Random Sides
 * Start a game with todd first and random second
 * do this ten times and verify that todd is not white each time
 * 
 * Setting Saving:
 * Unselect all settings
 * Close application
 * Load application and verify all settings are cleared
 * For each setting:
 * select setting
 * close application
 * open application
 * verify setting is selected
 * unselect setting
 * close application
 * verify setting is cleared
 */

public class FanoronaServerFrame extends JFrame implements FanoronaBoardDisplayerInterface {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 959419514198549359L;
	
	/** The frame width. */
	private final int FRAMEW = 500;
	
	/** The frame height. */
	private final int FRAMEH = 500;
	
	/** The main panel. */
	final JPanel mainPanel = new JPanel(new BorderLayout());
	
	/** The text pane. */
	final JTextPane textPane = new JTextPane();
	
	/** The scroller on the side of the text pane. */
	private JScrollPane scroller = new JScrollPane(textPane);
	//final JTextPane boardPane = new JTextPane(); // for the pieces
	/** The board pane, where the board and pieces are shown. */
	FanoronaGameBoardFrame boardPane = null;// = new FanoronaGameBoardFrame();
	
	/** The menu bar. */
	private JMenuBar menuBar = new JMenuBar();
	
	/** The tool bar. */
	private JToolBar toolBar = new JToolBar();
	
	/** The close item. */
	private JMenuItem closeItem = new JMenuItem("Close");
	
	/** The file menu. */
	private JMenu fileMenu = new JMenu("File");
	
	/** The options menu. */
	private JMenu optionsMenu = new JMenu("Options");
	
	/** The log files menu. */
	private JMenu logFilesMenu = new JMenu("Open Log");
	
	/** The fancy board menu item. */
	private JMenuItem fancyBoardMenuItem = new JCheckBoxMenuItem("Fancy Board");
	
	/** The java notepad menu item. */
	private JMenuItem javaNotepadMenuItem = new JCheckBoxMenuItem("Java Notepad");
	
	/** The random sides menu item. */
	private JMenuItem randomSidesMenuItem = new JCheckBoxMenuItem("Random Sides");
	
	/** The change log directory. */
	private JMenuItem changeLogDirectory = new JMenuItem("Change Logging Directory");
	
	/** The main sub panel. */
	private JPanel mainSubPanel = new JPanel (new BorderLayout());

	/** The board. */
	FanoronaBoardInterface board = new FanoronaLinkBoard();
	
	/** The server. */
	FanoronaServer server = null;
	
	/** The prefs. */
	Preferences prefs = Preferences.userNodeForPackage(this.getClass());

	/**
	 * Instantiates a new fanorona server frame.
	 */
	public FanoronaServerFrame()
	{	

		while (true) {
			String logdir = prefs.get("loggingDirectory", null);
			if (logdir == null) {
				JOptionPane.showMessageDialog(this, "Please select a directory to save game log files to.","Welcome!", JOptionPane.INFORMATION_MESSAGE);
				if (!showLoggingDirectoryDialog()) {
					JOptionPane.showMessageDialog(this, "The server will continue but saving log files will be disabled.","Warning", JOptionPane.WARNING_MESSAGE);
					prefs.remove("loggingDirectory");
				}
				continue;
			}
			if (!new File(logdir).canWrite()) {
				System.out.format("Couldn't write to %s\n", logdir);
				JOptionPane.showMessageDialog(this, "The directory chosen to save log files cannot be written to.  Please choose another.","Non-writable logging directory", JOptionPane.ERROR_MESSAGE);
				if (!showLoggingDirectoryDialog()) {
					JOptionPane.showMessageDialog(this, "The server will continue but saving log files will be disabled.","Warning", JOptionPane.WARNING_MESSAGE);
					prefs.remove("loggingDirectory");
				}
			} else {
				break;
			}
		}

		setTitle("Fanorona Server");
		setSize(FRAMEW, FRAMEH);
		//ensure the server will close when someone clicks the "red" X
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		//add a menu bar
		setJMenuBar(menuBar);
		//add a file menu 
		menuBar.add(fileMenu);
		//add open log file item to the file menu bar
		fileMenu.add(logFilesMenu);
		fileMenu.add(changeLogDirectory);
		fileMenu.addSeparator();
		//add a close item to the file menu bar
		fileMenu.add(closeItem);


		// add close listen to closeItem.
		//when click will ensure the application close, and does not continue to run
		closeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event)
			{
				System.exit(DISPOSE_ON_CLOSE);
			}
		});


		changeLogDirectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showLoggingDirectoryDialog();
			}
		});
		//add action listener to open the log file for the game
		//will open notepad with a new file if the specified log file does not exist.


		//boardPane.setFont(new Font("Courier", Font.PLAIN,14));
		//boardPane.setMinimumSize(new Dimension(11,7));
		//boardPane.
		boardPane = new FanoronaGameBoardFrame();
		//Container contentPane = getContentPane();
		textPane.setText("Welcome!");
		//contentPane.add(textPane, "Center");
		mainSubPanel.add(scroller,"Center");
		mainSubPanel.add(boardPane,"North");
		mainPanel.add(mainSubPanel,"Center");
		//mainPanel.add(scroller,BorderLayout.CENTER);
		//mainPanel.add(boardPane,BorderLayout.NORTH);
		setContentPane(mainPanel);
		//boardPane.setEditable(false);
		textPane.setEditable(false);

		JButton randomAI = new JButton("start random bot");
		randomAI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FanoronaRandomAI ai = new FanoronaRandomAI("localhost",3266);
				new Thread(ai).start();
			}			
		});
		//add random bot button to toolbar
		toolBar.add(randomAI);

		JButton joaAI = new JButton("start todd");
		joaAI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FanoronaJoaAI ai = new FanoronaJoaAI("localhost",3266);
				new Thread(ai).start();
			}			
		});
		//add "tood" ai button to toolbar
		toolBar.add(joaAI);

		JButton endGame = new JButton("draw game");
		endGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				server.forceGameOver();
			}
		});
		toolBar.add(endGame);


		//add toolbar to the panel
		mainPanel.add(toolBar,prefs.get("toolBarPosition","South"));
		toolBar.setOrientation(prefs.getInt("toolBarOrientation", JToolBar.HORIZONTAL));
		//set the toolBar to dockable
		toolBar.setFloatable(true);
		setMinimumSize(new Dimension(300,400));

		toolBar.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						LayoutManager lm = mainPanel.getLayout();
						if (! (lm instanceof BorderLayout)) {
							System.err.println("it's not a borderlayout!  whaaatt");
							return;
						}
						BorderLayout bl = (BorderLayout)lm;
						if (toolBar == bl.getLayoutComponent(BorderLayout.SOUTH)) {
							//System.err.println("Toolbar moved to South");
							prefs.put("toolBarPosition", "South");
							prefs.putInt("toolBarOrientation", toolBar.getOrientation());
						} else if (toolBar == bl.getLayoutComponent(BorderLayout.NORTH)) {
							prefs.put("toolBarPosition","North");
							prefs.putInt("toolBarOrientation", toolBar.getOrientation());
						} else if (toolBar == bl.getLayoutComponent(BorderLayout.EAST)) {
							prefs.put("toolBarPosition","East");
							prefs.putInt("toolBarOrientation", toolBar.getOrientation());
						} else if (toolBar == bl.getLayoutComponent(BorderLayout.WEST)) {
							prefs.put("toolBarPosition","West");
							prefs.putInt("toolBarOrientation", toolBar.getOrientation());
						} else {
						}

					}
				});
			}});
		
		fancyBoardMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prefs.putBoolean("useFancyBoard", fancyBoardMenuItem.isSelected());
				boardPane.repaint();			
			}
		});
		
		boolean useFancyBoard = prefs.getBoolean("useFancyBoard",false);
		fancyBoardMenuItem.setSelected(useFancyBoard);
		prefs.putBoolean("useFancyBoard", useFancyBoard);
		
		javaNotepadMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prefs.putBoolean("javaNotepad", javaNotepadMenuItem.isSelected());
			}
		});
		
		boolean ujn = prefs.getBoolean("javaNotepad",false);
		javaNotepadMenuItem.setSelected(ujn);
		prefs.putBoolean("javaNotepad", ujn);
		
		randomSidesMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prefs.putBoolean("randomSides", randomSidesMenuItem.isSelected());
			}
		});
		
		boolean randomSides = prefs.getBoolean("randomSides",false);
		randomSidesMenuItem.setSelected(randomSides);
		prefs.putBoolean("randomSides", randomSides);
		
		menuBar.add(optionsMenu);
		optionsMenu.add(randomSidesMenuItem);
		optionsMenu.addSeparator();
		optionsMenu.add(fancyBoardMenuItem);
		optionsMenu.add(javaNotepadMenuItem);
	}

	/**
	 * Show the logging directory dialog.
	 * 
	 * @return true, if successful
	 */
	private boolean showLoggingDirectoryDialog() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File(prefs.get("loggingDirectory", ".")));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		switch (chooser.showSaveDialog(this)) {
		case JFileChooser.APPROVE_OPTION:
			prefs.put("loggingDirectory", chooser.getSelectedFile().getAbsolutePath());
			return true;
		case JFileChooser.CANCEL_OPTION:
			return false;
		default:
			return false;
		}
	}

	/**
	 * Sets the server.
	 * 
	 * @param server the server
	 * 
	 * @return the fanorona server frame
	 */
	public FanoronaServerFrame setServer(FanoronaServer server) {
		this.server = server;
		server.setDisplayer(this);
		return this;
	}

	/**
	 * Joins the status messages together to be placed in the text pane.
	 * 
	 * @param lin the lin
	 * @param joiner the joiner
	 * 
	 * @return the string
	 */
	private static <T> String join(Collection<T> lin,String joiner) {
		LinkedList<T> l = new LinkedList<T>();
		l.addAll(lin);
		StringBuilder bb = new StringBuilder();
		while (l.size() > 1) {
			bb.append(l.remove(0).toString());
			bb.append(joiner);
		}
		bb.append(l.get(0));
		return bb.toString();
	}

	/**
	 * Uses the other join() function to put the status messages together to be placed in the text pane.
	 * 
	 * @param lin the lin
	 * @param joiner the joiner
	 * 
	 * @return the string
	 */
	private static <T> String join(Iterable<T> lin,String joiner) {
		LinkedList<T> l = new LinkedList<T>();
		for (T i : lin) l.add(i);
		return join(l,joiner);
	}

	/* (non-Javadoc)
	 * @see edu.alaska.uaa.cs401.fgm.FanoronaBoardDisplayerInterface#update()
	 */
	public void update() {
		try {
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					textPane.setText(join(server.getStatusArray(FanoronaServer.StatusPriority.HIGH),"\n"));
					boardPane.setBoard(server.getBoard());
				}
			});
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 */
	static public void main(String[] args) {
		final FanoronaServerFrame f;

		try { // try and set the LAF to windows.  stupid butt ugly metal.
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
		}


		try {
			FanoronaServer s = new FanoronaServer();
			s.setDaemon(true);
			s.start();

			f = new FanoronaServerFrame().setServer(s);
			// K constant
			// K kinetic motion of fanorona pieces as I destroy game
			// K^12 <-- big number
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					f.setVisible(true); // can't do this from the main thread.
				}
			});
		} catch (IOException e) {
			System.err.format("Unable to start server!\n");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.alaska.uaa.cs401.fgm.FanoronaBoardDisplayerInterface#requestNewLogFileDirectory()
	 */
	public boolean requestNewLogFileDirectory() {
		return showLoggingDirectoryDialog();	
		// if the file fails to save, this should pop up a new message requesting a new
		// filename
	}

	/* (non-Javadoc)
	 * @see edu.alaska.uaa.cs401.fgm.FanoronaBoardDisplayerInterface#addLogFile(java.lang.String, java.io.File)
	 */
	public void addLogFile(final String prettyName, final File f) {
		try {
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					while (logFilesMenu.getItemCount() > 9)
						logFilesMenu.remove(0);
					JMenuItem openLogItem = new JMenuItem(prettyName);
					logFilesMenu.add(openLogItem);
					
					try {
						openLogItem.addActionListener(new DesktopNotepadActionListener(f));
					} catch (Throwable e) {
						openLogItem.addActionListener(new ProcessNotepadActionListener(f));
					}
				}
			});
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
