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

package eu.esdihumboldt.util.scavenger;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.util.Pair;

/**
 * Scans for folder resources in a specific location or references a single
 * resource.
 * 
 * @param <T> the resource reference type
 * @author Simon Templer
 */
public abstract class AbstractResourceScavenger<T> implements ResourceScavenger<T> {

	/**
	 * Resource identifier in one resource mode.
	 */
	public static final String DEFAULT_RESOURCE_ID = "resource";

	private static final ALogger log = ALoggerFactory.getLogger(AbstractResourceScavenger.class);

	private final File huntingGrounds;

	private final Map<String, T> resources = new HashMap<>();

	private final Set<String> reserved = new HashSet<>();

	/**
	 * Create a scavenger instance. Subclass constructors should call
	 * {@link #triggerScan()} after to initialize the scavenger.
	 * 
	 * @param scavengeLocation the location to scan, if the location does not
	 *            exist or is not accessible, a default location inside the
	 *            platform instance location is used
	 * @param instanceLocPath the instance location sub-path to use if the
	 *            scavengeLocation is invalid or <code>null</code>, may be
	 *            <code>null</code> if the platform instance location should not
	 *            be used as fall-back
	 */
	public AbstractResourceScavenger(File scavengeLocation, String instanceLocPath) {
		if (scavengeLocation == null || !scavengeLocation.exists() && instanceLocPath != null) {
			// use default location
			Location location = Platform.getInstanceLocation();
			if (location != null) {
				try {
					File instanceLoc = new File(URI.create(location.getURL().toString()
							.replaceAll(" ", "%20")));
					scavengeLocation = new File(instanceLoc, instanceLocPath);
					if (!scavengeLocation.exists()) {
						scavengeLocation.mkdirs();
					}
				} catch (Exception e) {
					log.error(
							"Unable to determine instance location, can't initialize resource scavenger.",
							e);
					scavengeLocation = null;
				}
				huntingGrounds = scavengeLocation;
			}
			else {
				log.error("No instance location, can't initialize resource scavenger.");
				huntingGrounds = null;
			}
		}
		else {
			huntingGrounds = scavengeLocation;
		}

		if (huntingGrounds != null) {
			log.info("Resources location is " + huntingGrounds.getAbsolutePath());
		}

//		triggerScan();
	}

	/**
	 * @see ResourceScavenger#triggerScan()
	 */
	@Override
	public void triggerScan() {
		synchronized (resources) {
			if (huntingGrounds != null) {
				if (huntingGrounds.isDirectory()) {
					// scan for sub-directories
					Set<String> foundIds = new HashSet<String>();
					File[] resourceDirs = huntingGrounds.listFiles(new FileFilter() {

						@Override
						public boolean accept(File pathname) {
							// accept non-hidden directories
							return pathname.isDirectory() && !pathname.isHidden();
						}
					});

					for (File resourceDir : resourceDirs) {
						String resourceId = resourceDir.getName();
						foundIds.add(resourceId);
						if (!resources.containsKey(resourceId)) {
							// resource reference not loaded yet
							T reference;
							try {
								reference = loadReference(resourceDir, null, resourceId);
								resources.put(resourceId, reference);
								onAdd(reference, resourceId);
							} catch (IOException e) {
								log.error("Error creating resource reference", e);
							}
						}
						else {
							// update existing resource
							updateResource(resources.get(resourceId), resourceId);
						}
					}

					Set<String> removed = new HashSet<String>(resources.keySet());
					removed.removeAll(foundIds);

					// deal with resources that have been removed
					for (String resourceId : removed) {
						T reference = resources.remove(resourceId);
						if (reference != null) {
							// remove active environment
							onRemove(reference, resourceId);
						}
					}
				}
				else {
					// one project mode
					if (!resources.containsKey(DEFAULT_RESOURCE_ID)) {
						// project configuration not loaded yet
						T reference;
						try {
							reference = loadReference(huntingGrounds.getParentFile(),
									huntingGrounds.getName(), DEFAULT_RESOURCE_ID);
							resources.put(DEFAULT_RESOURCE_ID, reference);
							onAdd(reference, DEFAULT_RESOURCE_ID);
						} catch (IOException e) {
							log.error("Error creating project handler", e);
						}
					}
					else {
						// update existing project
						updateResource(resources.get(DEFAULT_RESOURCE_ID), DEFAULT_RESOURCE_ID);
					}
				}
			}
		}
	}

	/**
	 * Called when a resource has been added, either when adding the resource on
	 * the first scan or if it was added afterwards.
	 * 
	 * @param reference the resource reference
	 * @param resourceId the resource identifier
	 */
	protected abstract void onAdd(T reference, String resourceId);

	/**
	 * Called when a resource has been removed.
	 * 
	 * @param reference the resource reference
	 * @param resourceId the resource identifier
	 */
	protected abstract void onRemove(T reference, String resourceId);

	/**
	 * Called when an existing resource is visited during a scan.
	 * 
	 * @param reference the resource reference to update
	 * @param resourceId the resource identifier
	 */
	protected abstract void updateResource(T reference, String resourceId);

	/**
	 * Load a resource reference.
	 * 
	 * @param resourceFolder the resource folder
	 * @param resourceFileName the name of the resource file in that folder, may
	 *            be <code>null</code> if unknown
	 * @param resourceId the resource identifier
	 * @return the resource reference
	 * @throws IOException if the resource cannot be accessed or loaded
	 */
	protected abstract T loadReference(File resourceFolder, String resourceFileName,
			String resourceId) throws IOException;

	@Override
	public File reserveResourceId(String resourceId) throws ScavengerException {
		if (!allowAddResource()) {
			throw new ScavengerException("Adding a resource not allowed.");
		}

		// trigger a scan to be up-to-date
		triggerScan();

		// TODO check if resourceId is valid

		synchronized (resources) {
			if (resources.containsKey(resourceId)) {
				// there already is a project with that ID
				throw new ScavengerException("Project ID already taken.");
			}
			if (reserved.contains(resourceId)) {
				// the project ID is already reserved
				throw new ScavengerException("Project ID already reserved.");
			}
			reserved.add(resourceId);
			File projectFolder = new File(huntingGrounds, resourceId);
			if (!projectFolder.exists()) {
				// try creating the directory
				try {
					projectFolder.mkdir();
				} catch (Exception e) {
					throw new ScavengerException("Could not create project directory", e);
				}
			}
			if (!projectFolder.exists()) {
				throw new ScavengerException("Could not create project directory");
			}
			return projectFolder;
		}
	}

	@Override
	public Pair<String, File> reserveResource(String desiredId) throws ScavengerException {
		if (!allowAddResource()) {
			throw new ScavengerException("Adding a resource not allowed.");
		}

		// trigger a scan to be up-to-date
		triggerScan();

		synchronized (resources) {
			// normalize desired identifier
			if (desiredId != null) {
				// replace spaces by -
				desiredId = desiredId.replaceAll("\\s+", "-");
				// remove all non-word characters (except -)
				desiredId = desiredId.replaceAll("[^a-zA-Z0-9_\\-]", "");
				// might be run on windows
				desiredId = desiredId.toLowerCase();

				if (desiredId.isEmpty()) {
					// empty string not allowed
					desiredId = null;
				}
			}

			String id;
			if (desiredId != null && !resources.containsKey(desiredId)
					&& !reserved.contains(desiredId)) {
				// desired ID is OK
				id = desiredId;
			}
			else {
				if (desiredId != null) {
					// try postfix
					int num = 2;
					String testId = desiredId + num;
					while (resources.containsKey(testId) || reserved.contains(testId)) {
						testId = desiredId + (++num);
					}
					id = testId;
				}
				else {
					// try numeric identifiers
					int num = 1;
					String testId = String.valueOf(num);
					while (resources.containsKey(testId) || reserved.contains(testId)) {
						testId = String.valueOf(++num);
					}
					id = testId;
				}
			}

			reserved.add(id);
			File projectFolder = new File(huntingGrounds, id);
			if (!projectFolder.exists()) {
				// try creating the directory
				try {
					projectFolder.mkdir();
				} catch (Exception e) {
					throw new ScavengerException("Could not create project directory", e);
				}
			}
			if (!projectFolder.exists()) {
				throw new ScavengerException("Could not create project directory");
			}
			return new Pair<String, File>(id, projectFolder);
		}
	}

	/**
	 * @see ResourceScavenger#releaseResourceId(String)
	 */
	@Override
	public void releaseResourceId(String resourceId) {
		if (!allowAddResource()) {
			return;
		}

		synchronized (resources) {
			if (reserved.contains(resourceId)) {
				reserved.remove(resourceId);

				deleteResource(resourceId);
			}
		}
	}

	@Override
	public void deleteResource(String resourceId) {
		if (!allowAddResource()) {
			return;
		}

		T removed = null;
		synchronized (resources) {
			// delete directory
			File dir = new File(huntingGrounds, resourceId);
			if (dir.exists()) {
				try {
					FileUtils.deleteDirectory(new File(huntingGrounds, resourceId));
				} catch (IOException e) {
					log.error("Error deleting resource directory content", e);
				}
			}

			// removed resource ref
			if (resources.containsKey(resourceId)) {
				removed = resources.remove(resourceId);
			}
		}
		if (removed != null) {
			onRemove(removed, resourceId);
		}
	}

	@Override
	public boolean allowAddResource() {
		return huntingGrounds.isDirectory();
	}

	@Override
	public Set<String> getResources() {
		synchronized (resources) {
			return new TreeSet<String>(resources.keySet());
		}
	}

	@Override
	public T getReference(String resourceId) {
		return resources.get(resourceId);
	}

	@Override
	public File getHuntingGrounds() {
		return huntingGrounds;
	}

}
