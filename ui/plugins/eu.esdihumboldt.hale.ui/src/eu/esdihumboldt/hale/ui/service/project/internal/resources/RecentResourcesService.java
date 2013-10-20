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

import java.net.URI;
import java.util.List;

import org.eclipse.core.runtime.content.IContentType;

import eu.esdihumboldt.hale.common.core.io.project.model.Resource;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.ProjectServiceAdapter;
import eu.esdihumboldt.hale.ui.service.project.RecentResources;

/**
 * Recent resources service.
 * 
 * @author Simon Templer
 */
public class RecentResourcesService implements RecentResources {

	private final RecentResourcesImpl rs;

	/**
	 * Constructor.
	 * 
	 * @param ps the project service
	 */
	public RecentResourcesService(ProjectService ps) {
		super();
		this.rs = new RecentResourcesImpl();

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
	}

	@Override
	public void addResource(Resource resource) {
		rs.addResource(resource);
	}

	@Override
	public List<URI> getRecent(Iterable<? extends IContentType> contentTypes,
			boolean restrictToFiles) {
		return rs.getRecent(contentTypes, restrictToFiles);
	}

	@Override
	public List<Resource> getRecent(String actionId) {
		return rs.getRecent(actionId);
	}

}
