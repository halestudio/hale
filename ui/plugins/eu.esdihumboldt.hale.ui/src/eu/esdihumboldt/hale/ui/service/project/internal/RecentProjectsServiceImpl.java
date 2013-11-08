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

package eu.esdihumboldt.hale.ui.service.project.internal;

import java.util.Iterator;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IMemento;

import eu.esdihumboldt.hale.ui.service.project.RecentProjectsService;

/**
 * This service saves a list of recently opened files.
 * 
 * @author Michel Kraemer
 */
public class RecentProjectsServiceImpl implements RecentProjectsService {

	/**
	 * @see eu.esdihumboldt.hale.ui.service.project.RecentProjectsService.Entry
	 * @author Kai Schwierczek
	 */
	public static class EntryImpl implements Entry {

		private String file;
		private String projectName;

		/**
		 * Creates an entry with the given data.
		 * 
		 * @param file the file name
		 * @param projectName the project name
		 */
		private EntryImpl(String file, String projectName) {
			this.file = file;
			this.projectName = projectName;
		}

		/**
		 * Restores an entry from a given memento.
		 * 
		 * @param memento the memento
		 */
		private EntryImpl(IMemento memento) {
			file = memento.getString(TAG_NAME);
			projectName = memento.getString(TAG_PROJECT_NAME);
		}

		/**
		 * @see eu.esdihumboldt.hale.ui.service.project.RecentProjectsService.Entry#getFile()
		 */
		@Override
		public String getFile() {
			return file;
		}

		/**
		 * @see eu.esdihumboldt.hale.ui.service.project.RecentProjectsService.Entry#getProjectName()
		 */
		@Override
		public String getProjectName() {
			return projectName;
		}

		/**
		 * Saves an entry to the given memento.
		 * 
		 * @param memento the memento
		 */
		private void saveEntry(IMemento memento) {
			memento.putString(TAG_NAME, file);
			memento.putString(TAG_PROJECT_NAME, projectName);
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj != null && obj instanceof Entry) {
				Entry entry = (Entry) obj;
				// projectName can change, it's the same entry.
				if (this.file == null)
					return entry.getFile() == null;
				else
					return this.file.equals(entry.getFile());
			}
			return false;
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			if (file == null)
				return 0;
			return file.hashCode();
		}
	}

	/**
	 * The maximum number of files in the history
	 */
	public static final int MAX_FILES = 6;

	/**
	 * Tag for data sources stored in a memento
	 */
	private static final String TAG_FILE = "file"; //$NON-NLS-1$
	private static final String TAG_NAME = "name"; //$NON-NLS-1$
	private static final String TAG_PROJECT_NAME = "projectName"; //$NON-NLS-1$

	/**
	 * A circular buffer which saves the recent files
	 */
	private CircularFifoBuffer _buffer = new CircularFifoBuffer(MAX_FILES);

	/**
	 * @see RecentProjectsService#add(String, String)
	 */
	@Override
	public void add(String file, String projectName) {
		if (file != null) {
			if (projectName == null)
				projectName = "";
			Entry entry = new EntryImpl(file, projectName);
			Iterator<?> i = _buffer.iterator();
			while (i.hasNext()) {
				Entry rfe = (Entry) i.next();
				if (entry.equals(rfe)) {
					i.remove();
					break;
				}
			}
			_buffer.add(entry);
		}
	}

	/**
	 * @see RecentProjectsService#getRecentFiles()
	 */
	@Override
	public Entry[] getRecentFiles() {
		Entry[] result = new Entry[_buffer.size()];
		int i = 0;
		for (Object o : _buffer)
			result[i++] = (Entry) o;
		return result;
	}

	/**
	 * @see RecentProjectsService#restoreState(IMemento)
	 */
	@Override
	public IStatus restoreState(IMemento memento) {
		if (memento != null) {
			IMemento[] dsms = memento.getChildren(TAG_FILE);
			if (dsms != null) {
				for (IMemento dsm : dsms) {
					Entry entry = new EntryImpl(dsm);
					if (entry.getFile() != null)
						_buffer.add(entry);
				}
			}
		}
		return Status.OK_STATUS;
	}

	/**
	 * @see RecentProjectsService#saveState(IMemento)
	 */
	@Override
	public IStatus saveState(IMemento memento) {
		for (Object o : _buffer) {
			EntryImpl entry = (EntryImpl) o;
			IMemento c = memento.createChild(TAG_FILE);
			entry.saveEntry(c);
		}
		return Status.OK_STATUS;
	}
}
