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

package eu.esdihumboldt.hale.ui.service.project.internal.resources

import org.eclipse.core.runtime.content.IContentType

import com.google.common.collect.SortedSetMultimap
import com.google.common.collect.TreeMultimap

import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfigurationResource
import eu.esdihumboldt.hale.common.core.io.project.model.Resource
import eu.esdihumboldt.hale.ui.service.project.RecentResources
import groovy.transform.CompileStatic


/**
 * Holds information on recent resources.
 * 
 * @author Simon Templer
 */
@CompileStatic
class RecentResourcesImpl implements RecentResources {

	/**
	 * Maximum number of resources to store per content type or action.
	 */
	int maxStoreResources = 8

	/**
	 * Maximum number of resources to return.
	 */
	int maxReturnResources = 16

	/**
	 * Maps content type IDs to resource locations.
	 */
	private SortedSetMultimap<String, Timestamped<URI>> locations = TreeMultimap.create()

	/**
	 * Maps action IDs to resource configurations.
	 */
	private SortedSetMultimap<String, Timestamped<IOConfiguration>> configs = TreeMultimap.create()

	@Override
	public void addResource(String contentTypeId, URI uri) {
		synchronized (locations) {
			SortedSet<Timestamped<URI>> locs = locations.get(contentTypeId)
			locs << new Timestamped(uri)
			if (locs.size() > maxStoreResources) {
				locs.remove(locs.last())
			}
		}
	}

	@Override
	public void addResource(Resource resource) {
		if (resource.actionId) {
			synchronized (configs) {
				SortedSet<Timestamped<IOConfiguration>> confs = configs.get(resource.actionId)
				confs << new Timestamped(resource.copyConfiguration())
				if (confs.size() > maxStoreResources) {
					confs.remove(confs.last())
				}
			}
		}

		addResource(resource.contentType.id, resource.source)
	}

	@Override
	public List<URI> getRecent(Iterable<? extends IContentType> contentTypes,
			boolean restrictToFiles) {
		SortedSet<Timestamped<URI>> all = new TreeSet()
		synchronized (locations) {
			contentTypes.each { IContentType ct ->
				all.addAll(locations.get(ct.id))
			}
		}

		if (restrictToFiles) {
			all.retainAll { Timestamped<URI> it ->
				'file' == it.value.scheme
			}
		}

		List<URI> result = []
		int index = 0
		for (Timestamped<URI> element : all) {
			if (index < maxReturnResources) {
				result << element.value
			}
			else {
				break
			}

			index++
		}

		result
	}

	@Override
	public List<Resource> getRecent(String actionId) {
		List<Resource> result = []
		synchronized (configs) {
			configs.get(actionId).each { Timestamped<IOConfiguration> it ->
				result << new IOConfigurationResource(it.value)
			}
		}

		if (result.size() > maxReturnResources) {
			result = result.subList(0, maxReturnResources)
		}

		result
	}
}
