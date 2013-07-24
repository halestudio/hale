/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */
package eu.esdihumboldt.hale.ui.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.widgets.Display;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

/**
 * Daemon thread that keeps an associated display.
 * 
 * @author Simon Templer
 */
public class DisplayThread extends Thread {

	// static

	private static final ALogger log = ALoggerFactory.getLogger(DisplayThread.class);

	private static DisplayThread instance;

	/**
	 * Get the display thread instance. On the first call the thread will be
	 * created and started, and a display created.
	 * 
	 * @return the display thread
	 */
	public static DisplayThread getInstance() {
		synchronized (DisplayThread.class) {
			if (instance == null) {
				instance = new DisplayThread();
				instance.start();
			}
		}
		return instance;
	}

	// non-static

	private final AtomicBoolean canceled = new AtomicBoolean(false);

	private final AtomicReference<Display> display = new AtomicReference<Display>();

	/**
	 * Default constructor
	 */
	protected DisplayThread() {
		super("dedicated-display"); //$NON-NLS-1$

		// this is a daemon thread
		setDaemon(true);
	}

	/**
	 * @see Thread#run()
	 */
	@Override
	public void run() {
		if (display.get() == null) {
			// initially create the display
			display.set(new Display());

			log.info("Created display for dedicated display thread");
		}

		while (!canceled.get()) {
			// XXX loop ok like this?!
			if (!display.get().readAndDispatch()) {
				try {
					sleep(100);
				} catch (InterruptedException e) {
					// ignore
				}
			}
		}
	}

	/**
	 * @return the display
	 */
	public Display getDisplay() {
		while (display.get() == null) {
			// wait for display to be set
			try {
				sleep(100);
			} catch (InterruptedException e) {
				// ingore
			}
		}
		return display.get();
	}

	/**
	 * Cancel the thread execution
	 */
	protected void cancel() {
		canceled.set(true);
	}

}
