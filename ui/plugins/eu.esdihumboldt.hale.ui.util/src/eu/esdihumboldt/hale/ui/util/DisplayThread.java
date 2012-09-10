/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
		if (instance == null) {
			instance = new DisplayThread();
			instance.start();
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
