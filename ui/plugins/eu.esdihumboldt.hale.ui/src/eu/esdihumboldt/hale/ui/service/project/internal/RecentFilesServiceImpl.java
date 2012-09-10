/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.service.project.internal;

import java.util.Iterator;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IMemento;

import eu.esdihumboldt.hale.ui.service.project.RecentFilesService;

/**
 * This service saves a list of recently opened files.
 * 
 * @author Michel Kraemer
 */
public class RecentFilesServiceImpl implements RecentFilesService {

	/**
	 * @see eu.esdihumboldt.hale.ui.service.project.RecentFilesService.Entry
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
		 * @see eu.esdihumboldt.hale.ui.service.project.RecentFilesService.Entry#getFile()
		 */
		@Override
		public String getFile() {
			return file;
		}

		/**
		 * @see eu.esdihumboldt.hale.ui.service.project.RecentFilesService.Entry#getProjectName()
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
	 * @see RecentFilesService#add(String, String)
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
	 * @see RecentFilesService#getRecentFiles()
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
	 * @see RecentFilesService#restoreState(IMemento)
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
	 * @see RecentFilesService#saveState(IMemento)
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
