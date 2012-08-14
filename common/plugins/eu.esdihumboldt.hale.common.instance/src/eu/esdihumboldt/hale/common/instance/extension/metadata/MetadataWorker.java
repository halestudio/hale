/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.instance.extension.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;

/**
 * Worker Class to generate metadatas from extensionpoints into Instances
 * this class will first generate all Data-Generators and save them for use as long as instantiated.
 * Every generator will then get the given instance and generate the data from it, then the data
 * is put into the instance
 * @author Sebastian Reinhardt
 */
public class MetadataWorker {

	Collection<Instance> instances;
	Map<String, MetadataGenerator> generators;
	MetadataInfoExtension ext;
	
	private static final ALogger log = ALoggerFactory.getLogger(MetadataWorker.class);
	
	/**
	 * constructor for the metadataworker, instantiates all possible metadata generators
	 */
	public MetadataWorker(){
		instances = new ArrayList<Instance>();
		generators = new HashMap<String, MetadataGenerator>();
		ext = MetadataInfoExtension.getInstance();
		
		
		for(MetadataInfo meta : ext.getElements()){
			try {
				generators.put(meta.getId(), meta.getGenerator().newInstance());
			} catch (InstantiationException e) {
				log.error("Error instantiating metadata generators", e);
			} catch (IllegalAccessException e) {
				log.error("Error accessing metadata generators", e);
			}
		}
		
	}
	
	/**
	 * generates the data and puts it into the instance
	 * @param instance the given instance
	 */
	public void generate(MutableInstance instance){
		for(String key : generators.keySet()){
			Object[] data = generators.get(key).generate(instance);
			
			instance.setMetaData(key, data);
			
		}
	}
	
	/**
	 * generates the data and puts it into a collection of instances
	 * @param instances the gien instances
	 */
	public void generate(Collection<MutableInstance> instances){
		for(MutableInstance instance : instances){
			for(String key : generators.keySet()){
				Object[] data = generators.get(key).generate(instance);
				
				instance.setMetaData(key, data);
				
			}
		}
		
	}
	
	
	
	
}
