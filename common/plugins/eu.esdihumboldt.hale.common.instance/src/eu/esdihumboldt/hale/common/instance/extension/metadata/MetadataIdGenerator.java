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

import java.util.UUID;

import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * UniqueID Generator for Instance Metadatas
 * @author Sebastian Reinhardt
 */
public class MetadataIdGenerator implements MetadataGenerator {

	
	/**
	 * generates a unique Identifier for an instance
	 * @param inst the instance
	 * @return an array with a unique ID inside
	 */
	@Override
	public Object[] generate(Instance inst) {
		return new Object[]{UUID.randomUUID().toString()};
	}
		
	



}
