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

package eu.esdihumboldt.hale.io.gml.reader.internal;

import javax.xml.stream.XMLStreamReader;

import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.instance.model.impl.OInstance;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Utility methods for instances from {@link XMLStreamReader}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class StreamGmlInstance {

	/**
	 * Parses an instance with the given type from the given XML stream reader
	 * @param reader the XML stream reader
	 * @param type the definition of the instance type
	 *  
	 * @return the parsed instance
	 */
	public static Instance parseInstance(XMLStreamReader reader,
			TypeDefinition type) {
		OInstance instance = new OInstance(type);
		//FIXME instance: what about the issue with having an element and an attribute with the same name?
		//FIXME should the namespace therefore also be used for identifying the property?
		
		//TODO fill instance with values
		
		return instance;
	}

}
