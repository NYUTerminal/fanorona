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

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;

/**
 * The listener interface for receiving Action events.
 * The class that is interested in processing a desktopNotepadAction
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addActionListener<code> method. When
 * the Action event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ActionEvent
 */
public class DesktopNotepadActionListener implements ActionListener {
	
	/** The prefs. */
	Preferences prefs = Preferences.userNodeForPackage(this.getClass());

	/** The file. */
	File file = null;
	
	/**
	 * Instantiates a new desktop notepad action listener.
	 * 
	 * @param f the f
	 */
	DesktopNotepadActionListener(File f) {
		file = f;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		try {
			if (prefs.getBoolean("javaNotepad",false)) {
				throw new Exception();
			}
			Desktop d = java.awt.Desktop.getDesktop();
			d.edit(file);
			System.out.println("browse succeeded!");
		} catch (Exception e2) {
			new net.strudelline.sbfe.SBFE(file);
		}
	}
}
