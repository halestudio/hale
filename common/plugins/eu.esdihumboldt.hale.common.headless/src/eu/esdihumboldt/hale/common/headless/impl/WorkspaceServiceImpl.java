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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.headless.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;
import org.joda.time.DateTime;
import org.joda.time.ReadableDuration;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.headless.WorkspaceService;
import eu.esdihumboldt.util.PropertiesFile;

/**
 * Default implementation of the {@link WorkspaceService}.
 * 
 * @author Simon Templer
 */
public class WorkspaceServiceImpl implements WorkspaceService {

	/**
	 * Suffix of the configuration file for a workspace folder, that is placed
	 * alongside it in {@link #parentDir}.
	 */
	private static final String CONFIG_FILE_SUFFIX = ".lease";

	/**
	 * The property in the configuration file that states the end date of the
	 * workspace lease time.
	 */
	private static final String PROPERTY_LEASE_END = "leaseEnd";

	private static final ALogger log = ALoggerFactory.getLogger(WorkspaceServiceImpl.class);

	private final File parentDir;

	/**
	 * Create a workspace service instance.
	 * 
	 * @param workspacesDir the base directory for workspaces, if the location
	 *            does not exist or is not accessible, a default location inside
	 *            the platform instance location is used
	 */
	public WorkspaceServiceImpl(File workspacesDir) {
		if (workspacesDir == null || !workspacesDir.exists()) {
			// use default location
			Location location = Platform.getInstanceLocation();
			if (location != null) {
				try {
					File instanceLoc = new File(location.getURL().toURI());
					workspacesDir = new File(instanceLoc, "workspaces");
					if (!workspacesDir.exists()) {
						workspacesDir.mkdirs();
					}
				} catch (Exception e) {
					log.error(
							"Unable to determine instance location, can't initialize workspace service.",
							e);
					workspacesDir = null;
				}
				parentDir = workspacesDir;
			}
			else {
				log.error("No instance location, can't initialize workspace service.");
				parentDir = null;
			}
		}
		else {
			parentDir = workspacesDir;
		}

		if (parentDir != null) {
			log.info("Workspaces location is " + parentDir.getAbsolutePath());
		}
	}

	/**
	 * @see WorkspaceService#leaseWorkspace(ReadableDuration)
	 */
	@Override
	public File leaseWorkspace(ReadableDuration duration) {
		File workspace = newFolder();

		DateTime leaseEnd = DateTime.now().plus(duration);

		File configFile = configFile(workspace);

		PropertiesFile props;
		try {
			props = new PropertiesFile(configFile);
			props.setProperty(PROPERTY_LEASE_END, leaseEnd.toString());
			props.save();
		} catch (IOException e) {
			throw new IllegalStateException("Can't write to workspace folder", e);
		}

		return workspace;
	}

	/**
	 * Triggers the service scanning for workspace folders where the lease time
	 * has ended and deletes them.
	 */
	public void trigger() {
		for (File candidate : parentDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return !file.isHidden() && file.isDirectory() && configFile(file).exists();
			}

		})) {
			// retrieve lease end time
			DateTime leaseEnd = null;
			try {
				PropertiesFile configFile = new PropertiesFile(configFile(candidate));
				leaseEnd = DateTime.parse(configFile.getProperty(PROPERTY_LEASE_END));
			} catch (Exception e) {
				log.error("Failed to retrieve workspace folder lease end time.", e);
			}

			if (leaseEnd != null) {
				if (leaseEnd.isBeforeNow()) {
					// delete folder
					try {
						FileUtils.deleteDirectory(candidate);
					} catch (IOException e) {
						log.error("Error deleting workspace folder", e);
					}

					if (candidate.exists()) {
						log.error("Failed to delete workspace folder, leaving it for next time.");
					}
					else {
						configFile(candidate).delete();
					}
				}
			}
		}
	}

	private File configFile(File workspace) {
		return new File(workspace.getParentFile(), workspace.getName() + CONFIG_FILE_SUFFIX);
	}

	private synchronized File newFolder() {
		File folder = null;
		while (folder == null || folder.exists()) {
			folder = new File(parentDir, UUID.randomUUID().toString());
		}
		folder.mkdirs();
		return folder;
	}

}
