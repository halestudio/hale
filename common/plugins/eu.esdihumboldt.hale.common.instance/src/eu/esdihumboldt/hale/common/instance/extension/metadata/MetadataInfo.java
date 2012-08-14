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


import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.ExtensionUtil;
import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension.Identifiable;

/**
 * Represents a declared Metadata Info
 * @author Sebastian Reinhardt
 */
public class MetadataInfo  implements Identifiable {

	private final String key;
	private final String label;
	private final String description;
	private final Class<? extends MetadataGenerator> generator;
	
	
	/**
	 * Create a metadata object from a configuration element.
	 * @param key the data key
	 * @param conf the configuration element
	 */
	@SuppressWarnings("unchecked")
	public MetadataInfo(String key, IConfigurationElement conf){
		super();
		
		this.key = key;
		this.label = conf.getAttribute("label");
		this.description = conf.getAttribute("description");
		this.generator = (Class<? extends MetadataGenerator>) ExtensionUtil.loadClass(conf, "generator");
	}
		
		
	/**
	 * @see de.cs3d.util.eclipse.extension.simple.IdentifiableExtension.Identifiable#getId()
	 */
	@Override
	public String getId() {
		return key;
	}


	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}


	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @return the generator class
	 */
	public Class<? extends MetadataGenerator> getGenerator() {
		return generator;
	}

}
