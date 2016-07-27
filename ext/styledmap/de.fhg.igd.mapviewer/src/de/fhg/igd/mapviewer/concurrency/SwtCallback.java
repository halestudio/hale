/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */
package de.fhg.igd.mapviewer.concurrency;

import org.eclipse.swt.widgets.Display;

/**
 * Callback that executes in the SWT display thread.
 * 
 * @author Simon Templer
 * @param <R> the job result type
 */
public abstract class SwtCallback<R> implements Callback<R> {

	private final Display display;

	/**
	 * Creates a SWT call-back
	 */
	public SwtCallback() {
		this(null);
	}

	/**
	 * Creates a SWT call-back
	 * 
	 * @param display the display of the call-back thread
	 */
	public SwtCallback(final Display display) {
		if (display != null)
			this.display = display;
		else
			this.display = Display.getCurrent();
	}

	/**
	 * @see Callback#done(java.lang.Object)
	 */
	@Override
	public void done(final R result) {
		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				finished(result);
			}
		});
	}

	/**
	 * Called then job is done
	 * 
	 * @param result the job result
	 */
	protected abstract void finished(R result);

	/**
	 * @see Callback#failed(java.lang.Throwable)
	 */
	@Override
	public void failed(final Throwable e) {
		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				error(e);
			}
		});
	}

	/**
	 * Called when the job failed
	 * 
	 * @param e the error
	 */
	protected abstract void error(Throwable e);

}
