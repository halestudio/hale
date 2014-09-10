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

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap

import de.fhg.igd.slf4jplus.ALogger
import de.fhg.igd.slf4jplus.ALoggerFactory
import eu.esdihumboldt.hale.common.core.service.cleanup.Cleanup
import eu.esdihumboldt.hale.common.core.service.cleanup.CleanupContext
import eu.esdihumboldt.hale.common.core.service.cleanup.CleanupService
import eu.esdihumboldt.util.PlatformUtil
import groovy.transform.CompileStatic

/**
 * Default cleanup service implementation.
 * 
 * @author Simon Templer
 */
@CompileStatic
class CleanupServiceImpl implements CleanupService {

	private static final ALogger log = ALoggerFactory.getLogger(CleanupServiceImpl)

	private static final String ENC = 'UTF-8'

	/*
	 * XXX Deactivated persisting the temporary file list, because this will
	 * cause issues when starting multiple instances of HALE (deleting files
	 * that the other running instance needs).
	 * FIXME Would be great if we found another way here, maybe by saving
	 * some kind of valid-until for temporary files.
	 */
	private static final boolean PERSIST = false

	private final File persistTmpFiles

	private final Multimap<CleanupContext, File> tmpFiles = HashMultimap.create()

	private final Multimap<CleanupContext, Cleanup> cleaners = HashMultimap.create()

	CleanupServiceImpl() {
		if (PERSIST) {
			// temporary files location
			File instanceLoc = PlatformUtil.getInstanceLocation()
			persistTmpFiles = instanceLoc == null ? null : new File(instanceLoc, 'tmpFileList.txt')

			if (instanceLoc == null) {
				log.warn('Instance location could not be determined, unable to persist temporary file names', (Throwable) null)
			}

			// startup cleanup

			// read file list from previous executions
			if (persistTmpFiles?.exists()) {
				persistTmpFiles.eachLine(ENC) { String fileName ->
					// and delete them if possible
					delete(new File(fileName))
				}
				persistTmpFiles.delete()
			}
		}
	}

	private void delete(File file) {
		if (file.exists()) {
			try {
				if (file.isDirectory()) {
					if (file.deleteDir()) {
						log.debug("Deleted temporary directory $file", (Throwable) null)
					}
				}
				else {
					if (file.delete()) {
						log.debug("Deleted temporary file $file", (Throwable) null)
					}
				}
			} catch (Exception e) {
				log.warn("Error deleting temporary file $file", e)
			}
		}
	}

	@Override
	void addCleaner(CleanupContext context, Cleanup cleaner) {
		synchronized (cleaners) {
			cleaners.put(context, cleaner)
		}
	}

	@Override
	void addTemporaryFiles(CleanupContext context, File... files) {
		synchronized (tmpFiles) {
			files.each { File file ->
				tmpFiles.put(context, file)
			}

			// persist list of overall temporary files
			persistFiles()
		}
	}

	private void persistFiles() {
		if (PERSIST && persistTmpFiles != null) {
			String fileList = tmpFiles.values().collect { File file ->
				file.getAbsolutePath()
			}.join('\n')
			try {
				persistTmpFiles.setText(fileList, ENC)
			} catch (Exception e) {
				log.error("Failed to updated temporary files list in $persistTmpFiles", e)
			}
		}
	}

	private <T> Collection<T> take(CleanupContext context, Multimap<CleanupContext, T> elements) {
		switch (context) {
			case CleanupContext.APPLICATION:
			// all elements
				Collection<T> result = new ArrayList<T>(elements.values())
				elements.clear()
				return result
			default:
				Collection<T> result = new ArrayList<T>(elements.get(context))
				elements.removeAll(context)
				return result
		}
	}

	protected void cleanup(CleanupContext context) {
		// cleaners
		synchronized (cleaners) {
			// run matching cleaners
			Collection<Cleanup> cl = take(context, cleaners)
			for (Cleanup cleaner : cl) {
				try {
					cleaner.cleanUp()
				} catch (Exception e) {
					log.error('Error performing cleanup operation', e)
				}
			}
		}

		// temporary files
		synchronized (tmpFiles) {
			// delete matching files
			Collection<File> files = take(context, tmpFiles)
			for (File file : files) {
				delete(file)
			}

			// update persistant list of temporary files
			persistFiles()
		}
	}

	@Override
	void triggerProjectCleanup() {
		cleanup(CleanupContext.PROJECT)
	}

	void triggerApplicationCleanup() {
		log.info('Performing application clean up...', (Throwable) null)
		cleanup(CleanupContext.APPLICATION)
	}
}
