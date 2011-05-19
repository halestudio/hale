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

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.instance.model.MutableInstance;
import eu.esdihumboldt.hale.instance.model.impl.OInstance;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

import static com.google.common.base.Preconditions.*;

/**
 * Utility methods for instances from {@link XMLStreamReader}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class StreamGmlInstance {
	
	private static final ALogger log = ALoggerFactory.getLogger(StreamGmlInstance.class);

	/**
	 * Parses an instance with the given type from the given XML stream reader
	 * @param reader the XML stream reader, the current event must be the start
	 *   element of the instance
	 * @param type the definition of the instance type
	 *  
	 * @return the parsed instance
	 * @throws XMLStreamException if parsing the instance failed
	 */
	public static Instance parseInstance(XMLStreamReader reader,
			TypeDefinition type) throws XMLStreamException {
		checkState(reader.getEventType() == XMLStreamConstants.START_ELEMENT);
		
		MutableInstance instance = new OInstance(type);
		//FIXME instance: what about the issue with having an element and an attribute with the same name?
		//FIXME should the namespace therefore also be used for identifying the property?
		
		//TODO fill instance with values
		
		// attributes
		for (int i = 0; i < reader.getAttributeCount(); i++) {
			AttributeDefinition property = type.getAttribute(reader.getAttributeLocalName(i));
			if (property != null) {
				//TODO check also namespace?
				// add property value
				addSimpleProperty(instance, property, reader.getAttributeValue(i));
			}
			else {
				log.warn("No property ''{0}'' found in type ''{1}'', value is ignored", 
						reader.getAttributeLocalName(i), type.getDisplayName());
			}
		}
		
		if (hasElements(type)) {
			// elements
			int open = 1;
			while (open > 0 && reader.hasNext()) {
				int event = reader.nextTag();
				switch (event) {
				case XMLStreamConstants.START_ELEMENT:
					// get property
					AttributeDefinition property = type.getAttribute(reader.getLocalName());
					if (property != null) {
						//TODO check also namespace?
						instance.addProperty(property.getName(), 
								parseInstance(reader, property.getAttributeType())); //XXX use both namespace and local name???
					}
					else {
						log.warn("No property ''{0}'' found in type ''{1}'', value is ignored", 
								reader.getLocalName(), type.getDisplayName());
					}
					
					open++;
					break;
				case XMLStreamConstants.END_ELEMENT:
					open--;
					break;
				}
			}
		}
		else {
			// try to get text value
			String value = reader.getElementText();
//			FIXME addSimpleProperty(instance, ...);
		}
		
		return instance;
	}

	/**
	 * @param type
	 * @return
	 */
	private static boolean hasElements(TypeDefinition type) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Adds a property value to the given instance. The property value will
	 * be converted appropriately.
	 * 
	 * @param instance the instance
	 * @param property the property
	 * @param value the property value as specified in the XML
	 */
	private static void addSimpleProperty(MutableInstance instance,
			AttributeDefinition property, String value) {
		//TODO Object val = convertSimple(property.getAttributeType(), value);
		//FIXME for now only added as string
		instance.addProperty(property.getName(), value); //XXX use both namespace and local name???
	}

}
