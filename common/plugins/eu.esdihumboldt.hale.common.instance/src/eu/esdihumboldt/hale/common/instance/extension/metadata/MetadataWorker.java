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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.instance.extension.metadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;

/**
 * Worker Class to generate metadatas from extensionpoints into Instances this
 * class will first generate all Data-Generators and save them for use as long
 * as instantiated. Every generator will then get the given instance and
 * generate the data from it, then the data is put into the instance
 * 
 * @author Sebastian Reinhardt
 */
public class MetadataWorker {

	private final Map<String, MetadataGenerator> generators;
	private final MetadataInfoExtension ext;

	private static final ALogger log = ALoggerFactory.getLogger(MetadataWorker.class);

	/**
	 * constructor for the metadataworker, instantiates all possible metadata
	 * generators
	 */
	public MetadataWorker() {
		generators = new HashMap<String, MetadataGenerator>();
		ext = MetadataInfoExtension.getInstance();

		for (MetadataInfo meta : ext.getElements()) {
			try {
				if (meta.getGenerator() != null) {
					generators.put(meta.getId(), meta.getGenerator().newInstance());
				}
			} catch (InstantiationException e) {
				log.error("Error instantiating metadata generators", e);
			} catch (IllegalAccessException e) {
				log.error("Error accessing metadata generators", e);
			}
		}

	}

	/**
	 * generates the data and puts it into the instance
	 * 
	 * @param instance the given instance
	 */
	public void generate(MutableInstance instance) {
		for (Entry<String, MetadataGenerator> entry : generators.entrySet()) {
			Object[] data = entry.getValue().generate(instance);
			instance.setMetaData(entry.getKey(), data);
		}
	}

	/**
	 * generates the data and puts it into a collection of instances
	 * 
	 * @param instances the given instances
	 */
	public void generate(Collection<MutableInstance> instances) {
		for (MutableInstance instance : instances) {
			for (Entry<String, MetadataGenerator> entry : generators.entrySet()) {
				Object[] data = entry.getValue().generate(instance);
				instance.setMetaData(entry.getKey(), data);
			}
		}
	}

}
