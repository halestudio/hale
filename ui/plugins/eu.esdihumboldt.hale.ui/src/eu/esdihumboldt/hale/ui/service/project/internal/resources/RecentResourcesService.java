/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.service.project.internal.resources;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.jobs.Job;

import com.google.common.base.Predicate;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.project.model.Resource;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.ProjectServiceAdapter;
import eu.esdihumboldt.hale.ui.service.project.RecentResources;
import eu.esdihumboldt.util.Pair;
import eu.esdihumboldt.util.PlatformUtil;

/**
 * Recent resources service.
 * 
 * @author Simon Templer
 */
public class RecentResourcesService implements RecentResources {

	private static final ALogger log = ALoggerFactory.getLogger(RecentResourcesService.class);

	private final RecentResourcesImpl rs;

	private final File recentResourcesFile;

	/**
	 * Constructor.
	 * 
	 * @param ps the project service
	 */
	public RecentResourcesService(ProjectService ps) {
		super();
		this.rs = new RecentResourcesImpl();

		File instanceLoc = PlatformUtil.getInstanceLocation();
		if (instanceLoc != null) {
			recentResourcesFile = new File(instanceLoc, "recent-resources.xml");
		}
		else {
			log.warn("Instance location could not be determined, no recent resources will be loaded");
			recentResourcesFile = null;
		}

		if (recentResourcesFile != null && recentResourcesFile.exists()) {
			try (InputStream in = new BufferedInputStream(new FileInputStream(recentResourcesFile))) {
				rs.load(in);
			} catch (Exception e) {
				log.error("Failed to load recent resources", e);
			}
		}

		ps.addListener(new ProjectServiceAdapter() {

			@Override
			public void resourceAdded(String actionId, Resource resource) {
				addResource(resource);
			}

		});
	}

	@Override
	public void addResource(String contentTypeId, URI uri) {
		rs.addResource(contentTypeId, uri);

		saveInJob();
	}

	@Override
	public void addResource(Resource resource) {
		rs.addResource(resource);

		saveInJob();
	}

	@Override
	public List<Pair<URI, IContentType>> getRecent(Iterable<? extends IContentType> contentTypes,
			boolean restrictToFiles) {
		return rs.getRecent(contentTypes, restrictToFiles);
	}

	@Override
	public List<Resource> getRecent(String actionId) {
		return rs.getRecent(actionId);
	}

	@Override
	public List<Pair<URI, IContentType>> getRecent(Iterable<? extends IContentType> contentTypes,
			Predicate<URI> accept) {
		return rs.getRecent(contentTypes, accept);
	}

	/**
	 * Save the current configuration to file asynchronously in a job.
	 */
	protected void saveInJob() {
		Job saveJob = new Job("Save recent resources configuration") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Save recent resources configuration file",
						IProgressMonitor.UNKNOWN);
				save();
				monitor.done();

				return Status.OK_STATUS;
			}
		};
		saveJob.setSystem(true);

		saveJob.schedule(100);
	}

	/**
	 * Save the recent resources.
	 */
	protected void save() {
		if (recentResourcesFile != null) {
			try (OutputStream out = new BufferedOutputStream(new FileOutputStream(
					recentResourcesFile))) {
				rs.save(out);
			} catch (Exception e) {
				log.error("Failed to save recent resource to file", e);
			}
		}
	}

}
