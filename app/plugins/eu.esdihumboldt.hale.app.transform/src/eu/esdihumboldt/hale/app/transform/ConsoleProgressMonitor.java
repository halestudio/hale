/*
 * Copyright (c) 2014 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.app.transform;

import java.text.MessageFormat;
import java.util.Date;

import org.eclipse.core.runtime.IProgressMonitor;

import com.google.common.base.Objects;

/**
 * Progress monitor that gives status updates on the console.
 * 
 * @author Simon Templer
 */
public class ConsoleProgressMonitor implements IProgressMonitor, ConsoleConstants {
	
	private static final String PROG_PREFIX = MSG_PREFIX + "-- ";

	private static final long TRIGGER_CLOCK_MS = 500;
	
	private String mainTaskName;
	
	private String currentTask;
	
	private int totalWork;
	
	private int worked = 0;
	
	private boolean canceled = false;
	
	private long lastTrigger = -1;

	@Override
	public void beginTask(String name, int totalWork) {
		this.mainTaskName = name;
		this.totalWork = totalWork;
		
//		System.out.println(PROG_PREFIX + "Starting task: " + mainTaskName);
	}
	
	private void trigger(boolean force) {
		long now = new Date().getTime();
		
		if (force || lastTrigger < 0 || now - lastTrigger > TRIGGER_CLOCK_MS) {
			if (totalWork > 0 && totalWork != UNKNOWN) {
				System.out.println(PROG_PREFIX + MessageFormat.format((currentTask == null) ? ("{0} - {1,number,percent}")
						: ("{0} - {1,number,percent} - {2}"), mainTaskName, (float) worked
						/ (float) totalWork, currentTask));
			}
			else {
				if (currentTask != null) {
//					System.out.println(PROG_PREFIX + mainTaskName + ": " + currentTask);
					System.out.println(PROG_PREFIX + currentTask);
				}
				else {
					System.out.println(PROG_PREFIX + mainTaskName);
				}
			}
			
			lastTrigger = now;
		}
	}

	@Override
	public void done() {
		trigger(true);
//		System.out.println(PROG_PREFIX + "Finished task: " + mainTaskName);
	}

	@Override
	public void internalWorked(double work) {
		// ignore
	}

	@Override
	public boolean isCanceled() {
		return canceled;
	}

	@Override
	public void setCanceled(boolean value) {
		canceled = value;
	}

	@Override
	public void setTaskName(String name) {
		if (!Objects.equal(mainTaskName, name)) {
			mainTaskName = name;
			trigger(false);
		}
	}

	@Override
	public void subTask(String name) {
		if (!Objects.equal(currentTask, name)) {
			currentTask = name;
			trigger(false);
		}
	}

	@Override
	public void worked(int work) {
		if (totalWork != UNKNOWN && work > 0) {
			worked += work;
			trigger(false);
		}
	}

}
