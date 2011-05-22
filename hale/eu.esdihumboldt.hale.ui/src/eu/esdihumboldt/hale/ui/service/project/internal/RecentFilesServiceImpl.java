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
 * @author Michel Kraemer
 */
public class RecentFilesServiceImpl implements RecentFilesService {
	/**
	 * The maximum number of files in the history
	 */
	public static final int MAX_FILES = 6;
	
	/**
	 * Tag for data sources stored in a memento
	 */
	private static final String TAG_FILE = "file"; //$NON-NLS-1$
	private static final String TAG_NAME = "name"; //$NON-NLS-1$
	
	/**
	 * A circular buffer which saves the recent files
	 */
	private CircularFifoBuffer _buffer = new CircularFifoBuffer(MAX_FILES);
	
	/**
	 * @see RecentFilesService#add(String)
	 */
	@Override
	public void add(String file) {
		if (file != null) {
			Iterator<?> i = _buffer.iterator();
			while (i.hasNext()) {
				String ofile = (String)i.next();
				if (file.equals(ofile)) {
					i.remove();
					break;
				}
			}
			_buffer.add(file);
		}
	}
	
	/**
	 * @see RecentFilesService#getRecentFiles()
	 */
	@Override
	public String[] getRecentFiles() {
		String[] result = new String[_buffer.size()];
		int i = 0;
		for (Object o : _buffer) {
			String file = (String)o;
			result[i++] = file;
		}
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
					String file = restoreFile(dsm);
					if (file != null) {
						_buffer.add(file);
					}
				}
			}
		}
		return Status.OK_STATUS;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.project.RecentFilesService#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public IStatus saveState(IMemento memento) {
		for (Object o : _buffer) {
			String file = (String)o;
			IMemento c = memento.createChild(TAG_FILE);
			saveFile(file, c);
		}
		return Status.OK_STATUS;
	}
	
	/**
	 * Restores a file name from a given memento
	 * @param memento the memento
	 * @return the file name
	 */
	private String restoreFile(IMemento memento) {
		return memento.getString(TAG_NAME);
	}
	
	/**
	 * Saves a file name to the given memento
	 * @param file the file name to save
	 * @param memento the memento
	 */
	private void saveFile(String file, IMemento memento) {
		memento.putString(TAG_NAME, file);
	}
}
