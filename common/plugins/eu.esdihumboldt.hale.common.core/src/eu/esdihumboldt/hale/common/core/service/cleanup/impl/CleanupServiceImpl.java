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

package eu.esdihumboldt.hale.common.core.service.cleanup.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.service.cleanup.Cleanup;
import eu.esdihumboldt.hale.common.core.service.cleanup.CleanupContext;
import eu.esdihumboldt.hale.common.core.service.cleanup.CleanupService;

/**
 * Default cleanup service implementation.
 * 
 * @author Simon Templer
 */
public class CleanupServiceImpl implements CleanupService {

	private static final ALogger log = ALoggerFactory.getLogger(CleanupServiceImpl.class);

	private final Multimap<CleanupContext, File> tmpFiles = HashMultimap.create();

	private final Multimap<CleanupContext, Cleanup> cleaners = HashMultimap.create();

	private void delete(File file) {
		if (file.exists()) {
			try {
				if (file.isDirectory()) {
					FileUtils.deleteDirectory(file);
					log.debug("Deleted temporary directory " + file);
				}
				else {
					if (file.delete()) {
						log.debug("Deleted temporary file " + file);
					}
				}
			} catch (Exception e) {
				log.warn("Error deleting temporary file " + file, e);
			}
		}
	}

	@Override
	public void addCleaner(CleanupContext context, Cleanup cleaner) {
		synchronized (cleaners) {
			cleaners.put(context, cleaner);
		}
	}

	@Override
	public void addTemporaryFiles(CleanupContext context, File... files) {
		synchronized (tmpFiles) {
			for (File file : files) {
				tmpFiles.put(context, file);
			}
		}
	}

	private <T> Collection<T> take(CleanupContext context, Multimap<CleanupContext, T> elements) {
		switch (context) {
		case APPLICATION:
			// all elements
			Collection<T> res1 = new ArrayList<T>(elements.values());
			elements.clear();
			return res1;
		default:
			Collection<T> res2 = new ArrayList<T>(elements.get(context));
			elements.removeAll(context);
			return res2;
		}
	}

	private void cleanup(CleanupContext context) {
		// cleaners
		synchronized (cleaners) {
			// run matching cleaners
			Collection<Cleanup> cl = take(context, cleaners);
			for (Cleanup cleaner : cl) {
				try {
					cleaner.cleanUp();
				} catch (Exception e) {
					log.error("Error performing cleanup operation", e);
				}
			}
		}

		// temporary files
		synchronized (tmpFiles) {
			// delete matching files
			Collection<File> files = take(context, tmpFiles);
			for (File file : files) {
				delete(file);
			}
		}
	}

	@Override
	public void triggerProjectCleanup() {
		cleanup(CleanupContext.PROJECT);
	}

	/**
	 * Trigger clean up of all resources.
	 */
	public void triggerApplicationCleanup() {
		log.info("Performing application clean up...");
		cleanup(CleanupContext.APPLICATION);
	}
}
