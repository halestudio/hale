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

package eu.esdihumboldt.hale.server.projects.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.headless.EnvironmentManager;
import eu.esdihumboldt.hale.common.headless.TransformationEnvironment;
import eu.esdihumboldt.hale.server.projects.ProjectScavenger;
import eu.esdihumboldt.hale.server.projects.ScavengerException;
import eu.esdihumboldt.hale.server.projects.impl.internal.ProjectHandler;

/**
 * Scans for projects in a directory. Manages if projects are active and in that
 * case publishes them as {@link TransformationEnvironment}s to an
 * {@link EnvironmentManager}.
 * 
 * @author Simon Templer
 */
public class ProjectScavengerImpl implements ProjectScavenger {

	/**
	 * Project identifier in one project mode.
	 */
	public static final String DEFAULT_PROJECT_ID = "project";

	private static final ALogger log = ALoggerFactory.getLogger(ProjectScavengerImpl.class);

	private final EnvironmentManager environments;

	private final File huntingGrounds;

	private final Map<String, ProjectHandler> projects = new HashMap<String, ProjectHandler>();

	private final Set<String> reserved = new HashSet<String>();

	/**
	 * Create a scavenger instance.
	 * 
	 * @param environments the environments manager to populate
	 * @param scavengeLocation the location to scan, if the location does not
	 *            exist or is not accessible, a default location inside the
	 *            platform instance location is used
	 */
	public ProjectScavengerImpl(EnvironmentManager environments, File scavengeLocation) {
		this.environments = environments;

		if (scavengeLocation == null || !scavengeLocation.exists()) {
			// use default location
			Location location = Platform.getInstanceLocation();
			if (location != null) {
				try {
					File instanceLoc = new File(location.getURL().toURI());
					scavengeLocation = new File(instanceLoc, "projects");
					if (!scavengeLocation.exists()) {
						scavengeLocation.mkdirs();
					}
				} catch (Exception e) {
					log.error(
							"Unable to determine instance location, can't initialize project scavenger.",
							e);
					scavengeLocation = null;
				}
				huntingGrounds = scavengeLocation;
			}
			else {
				log.error("No instance location, can't initialize project scavenger.");
				huntingGrounds = null;
			}
		}
		else {
			huntingGrounds = scavengeLocation;
		}

		if (huntingGrounds != null) {
			log.info("Projects location is " + huntingGrounds.getAbsolutePath());
		}

		triggerScan();
	}

	/**
	 * @see ProjectScavenger#triggerScan()
	 */
	@Override
	public void triggerScan() {
		synchronized (projects) {
			if (huntingGrounds != null) {
				if (huntingGrounds.isDirectory()) {
					// scan for sub-directories
					Set<String> foundIds = new HashSet<String>();
					File[] projectDirs = huntingGrounds.listFiles(new FileFilter() {

						@Override
						public boolean accept(File pathname) {
							// accept non-hidden directories
							return pathname.isDirectory() && !pathname.isHidden();
						}
					});

					for (File projectDir : projectDirs) {
						String projectId = projectDir.getName();
						foundIds.add(projectId);
						if (!projects.containsKey(projectId)) {
							// project configuration not loaded yet
							ProjectHandler handler;
							try {
								handler = loadProjectHandler(projectDir, null, projectId);
								projects.put(projectId, handler);
							} catch (IOException e) {
								log.error("Error creating project handler", e);
							}
						}
						else {
							// update existing project
							projects.get(projectId).update(environments);
						}
					}

					Set<String> removed = new HashSet<String>(projects.keySet());
					removed.removeAll(foundIds);

					// deal with projects that have been removed
					for (String projectId : removed) {
						ProjectHandler handler = projects.remove(projectId);
						if (handler != null) {
							// remove active environment
							environments.removeEnvironment(projectId);
						}
					}
				}
				else {
					// one project mode
					if (!projects.containsKey(DEFAULT_PROJECT_ID)) {
						// project configuration not loaded yet
						ProjectHandler handler;
						try {
							handler = loadProjectHandler(huntingGrounds.getParentFile(),
									huntingGrounds.getName(), DEFAULT_PROJECT_ID);
							projects.put(DEFAULT_PROJECT_ID, handler);
						} catch (IOException e) {
							log.error("Error creating project handler", e);
						}
					}
					else {
						// update existing project
						projects.get(DEFAULT_PROJECT_ID).update(environments);
					}
				}
			}
		}
	}

	/**
	 * Load a project handler.
	 * 
	 * @param projectFolder the project folder
	 * @param projectFileName the name of the project file in that folder, may
	 *            be <code>null</code> if unknown
	 * @param projectId the project identifier
	 * @return the project handler
	 * @throws IOException if the project configuration cannot be accessed
	 */
	protected ProjectHandler loadProjectHandler(File projectFolder, String projectFileName,
			String projectId) throws IOException {
		ProjectHandler config = new ProjectHandler(projectFolder, projectFileName, projectId);
		config.update(environments);
		return config;
	}

	@Override
	public File reserveProjectId(String projectId) throws ScavengerException {
		if (!allowAddProject()) {
			throw new ScavengerException("Adding a project not allowed.");
		}

		// trigger a scan to be up-to-date
		triggerScan();

		// TODO check if projectId is valid

		synchronized (projects) {
			if (projects.containsKey(projectId)) {
				// there already is a project with that ID
				throw new ScavengerException("Project ID already taken.");
			}
			if (reserved.contains(projectId)) {
				// the project ID is already reserved
				throw new ScavengerException("Project ID already reserved.");
			}
			reserved.add(projectId);
			File projectFolder = new File(huntingGrounds, projectId);
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

	/**
	 * @see ProjectScavenger#releaseProjectId(String)
	 */
	@Override
	public void releaseProjectId(String projectId) {
		if (!allowAddProject()) {
			return;
		}

		synchronized (projects) {
			if (reserved.contains(projectId)) {
				reserved.remove(projectId);

				// delete directoy
				try {
					FileUtils.deleteDirectory(new File(huntingGrounds, projectId));
				} catch (IOException e) {
					log.error("Error deleting project directory content", e);
				}
			}
		}
	}

	@Override
	public boolean allowAddProject() {
		return huntingGrounds.isDirectory();
	}

	@Override
	public Set<String> getProjects() {
		synchronized (projects) {
			return new TreeSet<String>(projects.keySet());
		}
	}

	@Override
	public Status getStatus(String projectId) {
		synchronized (projects) {
			ProjectHandler project = projects.get(projectId);
			if (project == null) {
				return Status.NOT_AVAILABLE;
			}
			else {
				return project.getStatus();
			}
		}
	}

	@Override
	public File getLoadReports(String projectId) {
		synchronized (projects) {
			ProjectHandler project = projects.get(projectId);
			if (project != null) {
				return project.getLoadReportFile();
			}
		}

		return null;
	}

	/**
	 * @see ProjectScavenger#getInfo(String)
	 */
	@Override
	public ProjectInfo getInfo(String projectId) {
		synchronized (projects) {
			ProjectHandler project = projects.get(projectId);
			if (project != null) {
				return project.getProjectInfo();
			}
		}
		return null;
	}

	@Override
	public void activate(String projectId) {
		synchronized (projects) {
			ProjectHandler project = projects.get(projectId);
			if (project != null) {
				project.activate(environments);
			}
		}
	}

	@Override
	public void deactivate(String projectId) {
		synchronized (projects) {
			ProjectHandler project = projects.get(projectId);
			if (project != null) {
				project.deactivate(environments);
			}
		}
	}

}
