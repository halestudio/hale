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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.osgi.service.datalocation.Location;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.project.ProjectReader;
import eu.esdihumboldt.hale.common.headless.EnvironmentManager;
import eu.esdihumboldt.hale.common.headless.TransformationEnvironment;
import eu.esdihumboldt.hale.server.projects.ProjectScavenger;
import eu.esdihumboldt.hale.server.projects.impl.internal.ProjectConfig;

/**
 * Scans for projects in a directory. Manages if projects are active and in that
 * case publishes them as {@link TransformationEnvironment}s to an
 * {@link EnvironmentManager}.
 * 
 * @author Simon Templer
 */
public class ProjectScavengerImpl implements ProjectScavenger {

	private static final ALogger log = ALoggerFactory.getLogger(ProjectScavengerImpl.class);

	private final EnvironmentManager environments;

	private final File huntingGrounds;

	private final Map<String, ProjectConfig> projects = new HashMap<String, ProjectConfig>();

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

		triggerScan();
	}

	/**
	 * @see ProjectScavenger#triggerScan()
	 */
	@Override
	public void triggerScan() {
		if (huntingGrounds != null) {
			if (huntingGrounds.isDirectory()) {
				// scan for sub-directories
				// TODO
			}
			else {
				// one project mode
				// TODO
			}
		}
	}

	/**
	 * Get the supported file extensions for projects.
	 * 
	 * @return the set of file extensions (without leading dot)
	 */
	protected Set<String> getSupportedExtensions() {
		Collection<IOProviderDescriptor> providers = HaleIO
				.getProviderFactories(ProjectReader.class);

		// collect supported content types
		Set<String> supportedExtensions = new HashSet<String>();
		for (IOProviderDescriptor factory : providers) {
			for (IContentType type : factory.getSupportedTypes()) {
				String[] extensions = type.getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
				if (extensions != null) {
					for (String ext : extensions) {
						supportedExtensions.add(ext);
					}
				}
			}
		}

		return supportedExtensions;
	}

	@Override
	public Set<String> getProjects() {
		synchronized (projects) {
			return new TreeSet<String>(projects.keySet());
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.server.projects.ProjectScavenger#getStatus(java.lang.String)
	 */
	@Override
	public Status getStatus(String projectId) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.server.projects.ProjectScavenger#activate(java.lang.String)
	 */
	@Override
	public void activate(String projectId) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see eu.esdihumboldt.hale.server.projects.ProjectScavenger#deactivate(java.lang.String)
	 */
	@Override
	public void deactivate(String projectId) {
		// TODO Auto-generated method stub

	}

}
