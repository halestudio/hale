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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.osgi.service.prefs.PreferencesService;

import com.google.common.base.Splitter;

import de.fhg.igd.osgi.util.OsgiUtils;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.ui.service.project.RecentProjectsService;

/**
 * This service saves a list of recently opened files.
 * 
 * @author Michel Kraemer
 * @author Simon Templer
 */
public class RecentProjectsServiceImpl implements RecentProjectsService {

	private static final ALogger log = ALoggerFactory.getLogger(RecentProjectsServiceImpl.class);

	/**
	 * @see eu.esdihumboldt.hale.ui.service.project.RecentProjectsService.Entry
	 * @author Kai Schwierczek
	 */
	public static class EntryImpl implements Entry {

		private final String file;
		private final String projectName;

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

		@Override
		public String getFile() {
			return file;
		}

		@Override
		public String getProjectName() {
			return projectName;
		}

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

	private static final String CONFIG_PROPERTY = "hale.recentProjects"; //$NON-NLS-1$
	private static final String ENC = "UTF-8"; //$NON-NLS-1$

	/**
	 * @see RecentProjectsService#add(String, String)
	 */
	@Override
	public void add(String file, String projectName) {
		if (file != null) {
			CircularFifoBuffer buffer = restoreState();

			if (projectName == null)
				projectName = "";
			Entry entry = new EntryImpl(file, projectName);
			Iterator<?> i = buffer.iterator();
			while (i.hasNext()) {
				Entry rfe = (Entry) i.next();
				if (entry.equals(rfe)) {
					i.remove();
					break;
				}
			}
			buffer.add(entry);

			saveState(buffer);
		}
	}

	@Override
	public Entry[] getRecentFiles() {
		CircularFifoBuffer buffer = restoreState();

		Entry[] result = new Entry[buffer.size()];
		int i = 0;
		for (Object o : buffer)
			result[i++] = (Entry) o;
		return result;
	}

	private CircularFifoBuffer restoreState() {
		PreferencesService prefs = OsgiUtils.getService(PreferencesService.class);

		CircularFifoBuffer buffer = new CircularFifoBuffer(MAX_FILES);
		String configString = prefs.getSystemPreferences().get(CONFIG_PROPERTY, "");

		List<String> parts = Splitter.on(' ').splitToList(configString);

		buffer.clear();
		for (int i = 0; i < parts.size() - 1; i += 2) {
			try {
				String name = URLDecoder.decode(parts.get(i), ENC);
				String filename = URLDecoder.decode(parts.get(i + 1), ENC);

				Entry entry = new EntryImpl(filename, name);
				buffer.add(entry);
			} catch (UnsupportedEncodingException e) {
				log.error(ENC + "? That's supposed to be an encoding?", e);
			}
		}
		return buffer;
	}

	private void saveState(CircularFifoBuffer buffer) {
		PreferencesService prefs = OsgiUtils.getService(PreferencesService.class);

		StringBuilder configString = new StringBuilder();
		boolean first = true;
		for (Object o : buffer) {
			try {
				Entry entry = (Entry) o;
				if (first)
					first = false;
				else
					configString.append(' ');
				configString.append(URLEncoder.encode(entry.getProjectName(), ENC));
				configString.append(' ');
				configString.append(URLEncoder.encode(entry.getFile(), ENC));
			} catch (UnsupportedEncodingException e) {
				log.error(ENC + "? That's supposed to be an encoding?", e);
			}
		}

		prefs.getSystemPreferences().put(CONFIG_PROPERTY, configString.toString());
	}
}
