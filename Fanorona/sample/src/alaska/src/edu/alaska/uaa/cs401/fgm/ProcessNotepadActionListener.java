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
/**
 * 
 */
package edu.alaska.uaa.cs401.fgm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;

/**
 * The listener interface for receiving Action events.
 * The class that is interested in processing a processNotepadAction
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>ActionListener<code> method. When
 * the Action event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ActionEvent
 */
class ProcessNotepadActionListener implements ActionListener {
	
	/** The f. */
	private final File f;
	
	/** The prefs. */
	Preferences prefs = Preferences.userNodeForPackage(this.getClass());

	/**
	 * Instantiates a new process notepad action listener.
	 * 
	 * @param f the f
	 */
	ProcessNotepadActionListener(File f) {
		this.f = f;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event)
	{
		// this might be better off in a JFrame
		// since it's so simple and notepad doesn't
		// run in anything but windows.  It could always
		// have an "Open in notepad" button too...

		String[] cmd = {"notepad" , f.toString()};
		try{
			if (prefs.getBoolean("javaNotepad", false))
				throw new Exception();
			Runtime runtime = Runtime.getRuntime();
			@SuppressWarnings("unused")
			Process proc = runtime.exec(cmd);
		} catch(Exception e2){
			//e.printStackTrace();
			new net.strudelline.sbfe.SBFE(f);
		}
	}
}
