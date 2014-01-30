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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.ReadableDuration;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.headless.WorkspaceService;
import eu.esdihumboldt.util.PlatformUtil;
import eu.esdihumboldt.util.PropertiesFile;

/**
 * Default implementation of the {@link WorkspaceService}.
 * 
 * @author Simon Templer
 */
public class WorkspaceServiceImpl implements WorkspaceService {

	/**
	 * Prefix of property names that are part of the external workspace
	 * settings.
	 */
	private static final String PROPERTY_SETTING_PREFIX = "setting_";

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
			workspacesDir = PlatformUtil.getInstanceLocation();
			if (workspacesDir != null) {
				if (!workspacesDir.exists()) {
					workspacesDir.mkdirs();
				}
			}
			else {
				log.error("No instance location, can't initialize workspace service.");
			}
		}
		parentDir = workspacesDir;

		if (parentDir != null) {
			log.info("Workspaces location is " + parentDir.getAbsolutePath());
		}
	}

	/**
	 * @see WorkspaceService#leaseWorkspace(ReadableDuration)
	 */
	@Override
	public String leaseWorkspace(ReadableDuration duration) {
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

		log.info("Leasing workspace " + workspace.getName() + " until " + leaseEnd.toString());

		return workspace.getName();
	}

	/**
	 * @see WorkspaceService#getWorkspaceFolder(String)
	 */
	@Override
	public File getWorkspaceFolder(String id) throws FileNotFoundException {
		if (id == null || id.isEmpty()) {
			throw new FileNotFoundException("Invalid workspace ID");
		}

		File workspace = new File(parentDir, id);
		if (!workspace.exists() || !configFile(workspace).exists()) {
			throw new FileNotFoundException("Workspace does not exist");
		}
		return workspace;
	}

	/**
	 * @see WorkspaceService#getLeaseEnd(String)
	 */
	@Override
	public DateTime getLeaseEnd(String workspaceId) throws IOException {
		PropertiesFile config = getConfiguration(workspaceId);
		String leaseEnd = config.getProperty(PROPERTY_LEASE_END);
		return DateTime.parse(leaseEnd);
	}

	private PropertiesFile getConfiguration(String workspaceId) throws IOException {
		File configFile = configFile(getWorkspaceFolder(workspaceId));
		if (configFile.exists()) {
			return new PropertiesFile(configFile);
		}

		throw new FileNotFoundException("Workspace configuration file missing");
	}

	/**
	 * @see WorkspaceService#getSettings(String)
	 */
	@Override
	public Map<String, String> getSettings(String workspaceId) throws IOException {
		PropertiesFile config;
		synchronized (this) { // TODO better locking, read/write, based in
								// workspaceId?!
			config = getConfiguration(workspaceId);
		}
		@SuppressWarnings("unchecked")
		Enumeration<String> enProps = (Enumeration<String>) config.propertyNames();

		Map<String, String> result = new HashMap<String, String>();
		while (enProps.hasMoreElements()) {
			String property = enProps.nextElement();
			if (property.startsWith(PROPERTY_SETTING_PREFIX)) {
				result.put(property.substring(PROPERTY_SETTING_PREFIX.length()),
						config.getProperty(property));
			}
		}

		return result;
	}

	/**
	 * @see WorkspaceService#set(String, String, String)
	 */
	@Override
	public void set(String workspaceId, String setting, String value) throws IOException {
		synchronized (this) { // TODO better locking, read/write, based on
								// workspaceId?!
			PropertiesFile config = getConfiguration(workspaceId);

			String key = PROPERTY_SETTING_PREFIX + setting;

			if (value == null) {
				config.remove(key);
			}
			else {
				config.setProperty(key, value);
			}

			config.save();
		}
	}

	/**
	 * @see WorkspaceService#deleteWorkspace(String)
	 */
	@Override
	public void deleteWorkspace(String id) {
		try {
			File workspace = getWorkspaceFolder(id);

			// delete folder
			FileUtils.deleteDirectory(workspace);
			// delete configuration file
			configFile(workspace).delete();

			log.info("Removed workspace " + workspace.getName() + ".");
		} catch (IOException e) {
			log.error("Error deleting workspace folder", e);
		}
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
						log.info("Removed workspace " + candidate.getName()
								+ " after lease expired.");
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
