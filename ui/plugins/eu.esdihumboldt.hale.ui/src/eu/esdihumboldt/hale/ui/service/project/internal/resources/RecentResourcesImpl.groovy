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

import javax.xml.parsers.DocumentBuilderFactory

import org.eclipse.core.runtime.content.IContentType
import org.joda.time.DateTime
import org.w3c.dom.Element

import com.google.common.base.Predicate
import com.google.common.collect.SortedSetMultimap
import com.google.common.collect.TreeMultimap

import de.fhg.igd.slf4jplus.ALogger
import de.fhg.igd.slf4jplus.ALoggerFactory
import eu.esdihumboldt.hale.common.core.io.ImportProvider
import eu.esdihumboldt.hale.common.core.io.project.DOMProjectHelper
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfigurationResource
import eu.esdihumboldt.hale.common.core.io.project.model.Resource
import eu.esdihumboldt.hale.ui.service.project.RecentResources
import eu.esdihumboldt.util.Pair
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.xml.DOMBuilder
import groovy.xml.XmlUtil
import groovy.xml.dom.DOMCategory


/**
 * Holds information on recent resources.
 * 
 * @author Simon Templer
 */
@CompileStatic
class RecentResourcesImpl implements RecentResources {

	private static final ALogger log = ALoggerFactory.getLogger(RecentResourcesImpl)

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
		if (uri != null && uri.absolute) {
			// only resources with absolute URIs can be saved
			synchronized (locations) {
				SortedSet<Timestamped<URI>> locs = locations.get(contentTypeId)

				/*
				 * Check if there already is the same location (with older timestamp)
				 * and remove it.
				 */
				locs.find { Timestamped<URI> it ->
					it.value == uri
				}.each { Timestamped<IOConfiguration> it ->
					locs.remove(it)
				}

				locs << new Timestamped(uri)
				if (locs.size() > maxStoreResources) {
					locs.remove(locs.last())
				}
			}
		}
	}

	@Override
	public void addResource(Resource resource) {
		if (resource.absoluteSource == null) {
			// only resources with absolute URIs can be saved
			return
		}

		if (resource.actionId) {
			synchronized (configs) {
				SortedSet<Timestamped<IOConfiguration>> confs = configs.get(resource.actionId)

				/*
				 * Check if there already are configuration for the same source
				 * and remove them. Always absolute sources are stored.
				 */
				String source = resource.absoluteSource.toString()
				confs.find { Timestamped<IOConfiguration> it ->
					String confSource = it.value.getProviderConfiguration().get(ImportProvider.PARAM_SOURCE).as(String)

					source == confSource
				}.each { Timestamped<IOConfiguration> it ->
					confs.remove(it)
				}

				confs << new Timestamped(resource.copyConfiguration(true))
				if (confs.size() > maxStoreResources) {
					confs.remove(confs.last())
				}
			}
		}

		// add with absolute URI
		addResource(resource.contentType.id, resource.absoluteSource)
	}

	@Override
	public List<Pair<URI, IContentType>> getRecent(Iterable<? extends IContentType> contentTypes, Predicate<URI> accept) {
		SortedSet<Timestamped<Pair<URI, IContentType>>> all = new TreeSet()
		synchronized (locations) {
			contentTypes.each { IContentType ct ->
				// collect accepted locations with content type
				locations.get(ct.id).each { Timestamped<URI> it ->
					if (accept == null || accept.apply(it.value)) {
						all << new Timestamped(new Pair(it.value, ct), it.stamp)
					}
				}
			}
		}

		// just take the newest X
		List<Pair<URI, IContentType>> result = []
		int index = 0
		for (Timestamped<Pair<URI, IContentType>> element : all) {
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
	public List<Pair<URI, IContentType>> getRecent(Iterable<? extends IContentType> contentTypes,
			boolean restrictToFiles) {
		getRecent(contentTypes, new Predicate<URI>() {
					@Override
					boolean apply(URI uri) {
						'file' == uri.scheme
					}
				});
	}

	@Override
	public List<Resource> getRecent(String actionId) {
		List<Resource> result = []
		synchronized (configs) {
			configs.get(actionId).each { Timestamped<IOConfiguration> it ->
				result << new IOConfigurationResource(it.value, null)
			}
		}

		if (result.size() > maxReturnResources) {
			result = result.subList(0, maxReturnResources)
		}

		result
	}


	@CompileStatic(TypeCheckingMode.SKIP)
	void load(InputStream input) {
		def builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
		Element root = builder.parse(input).getDocumentElement()

		use (DOMCategory) {
			// locations
			synchronized (locations) {
				root.location.each {
					try {
						// create locations entry
						String contentType = it.contentType.text()
						URI uri = URI.create(it.uri.text())
						DateTime stamp = DateTime.parse(it.timestamp.text())

						locations.put(contentType, new Timestamped(uri, stamp))
					} catch (Exception e) {
						log.error 'Failed to load recent resource location', e
					}
				}
			}


			// configurations
			synchronized (configs) {
				root.resource.each{
					try {
						DateTime stamp = DateTime.parse(it.timestamp.text())
						IOConfiguration config = DOMProjectHelper.configurationFromDOM(it.configuration[0])

						configs.put(config.actionId, new Timestamped<IOConfiguration>(config, stamp))
					} catch (Exception e) {
						log.error 'Failed to load recent resource configuration', e
					}
				}
			}
		}
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	void save(OutputStream out) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance()
		def root = DOMBuilder.newInstance().'recent-resources' {
			// locations
			synchronized (locations) {
				locations.entries().each { entry ->
					location {
						contentType(entry.key)
						uri(entry.value.value)
						timestamp(entry.value.stamp.toString())
					}
				}
			}

			// configurations
			synchronized (configs) {
				configs.entries().each { entry ->
					Element resNode = resource {
						timestamp(entry.value.stamp.toString())
					}

					def configNode = DOMProjectHelper.configurationToDOM(entry.value.value)
					resNode.appendChild(resNode.ownerDocument.adoptNode(configNode)?:resNode.ownerDocument.importNode(configNode, true))
				}
			}
		}

		XmlUtil.serialize(root, out)
	}
}
