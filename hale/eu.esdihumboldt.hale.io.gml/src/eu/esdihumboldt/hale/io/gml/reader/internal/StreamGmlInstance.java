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

import java.text.MessageFormat;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.instance.model.MutableGroup;
import eu.esdihumboldt.hale.instance.model.MutableInstance;
import eu.esdihumboldt.hale.instance.model.impl.OInstance;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;
import eu.esdihumboldt.hale.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.schema.model.constraint.type.HasValueFlag;

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
	 * Parses an instance with the given type from the given XML stream reader.
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
		
		// instance properties
		parseProperties(reader, instance, type);

		// instance value
		if (type.getConstraint(HasValueFlag.class).isEnabled()) {
			//FIXME check for xsi:nil!
			//XXX or use reader.hasText()?
			
			// try to get text value
			String value = reader.getElementText();
			if (value != null) {
				instance.setValue(convertSimple(type, value));
			}
		}
		
		//TODO add geometry as a GeometryProperty value where applicable
		
		return instance;
	}

	/**
	 * Populates an instance or group with its properties based on the given
	 * XML stream reader.
	 * @param reader the XML stream reader
	 * @param group the group to populate with properties
	 * @param definition the associated definition
	 * @throws XMLStreamException if parsing the properties failed
	 */
	private static void parseProperties(XMLStreamReader reader,
			MutableGroup group, DefinitionGroup definition) throws XMLStreamException {
		// attributes (usually only present in Instances)
		for (int i = 0; i < reader.getAttributeCount(); i++) {
			QName propertyName = reader.getAttributeName(i);
			//XXX might also be inside a group? currently every attribute group should be flattened
			// for group support there would have to be some other kind of handling than for elements, cause order doesn't matter for attributes  
			ChildDefinition<?> child = definition.getChild(propertyName);
			if (child != null && child.asProperty() != null) {
				// add property value
				addSimpleProperty(group, child.asProperty(), reader.getAttributeValue(i));
			}
			else {
				log.warn(MessageFormat.format(
						"No property ''{0}'' found in type ''{1}'', value is ignored", 
						propertyName, definition.getIdentifier()));
			}
		}
				
		// elements
		int open = 1;
		while (open > 0 && reader.hasNext()) {
			int event = reader.next();
			switch (event) {
			case XMLStreamConstants.START_ELEMENT:
				// get child
				ChildDefinition<?> child = definition.getChild(reader.getName());
				
				//XXX
				
				if (child != null) {
					PropertyDefinition property = child.asProperty();
					checkNotNull(property);
					
					//TODO check also namespace?
					if (hasElements(property.getPropertyType())) {
						// use an instance as value
						group.addProperty(property.getName(), 
								parseInstance(reader, property.getPropertyType()));
					}
					else {
						if (hasAttributes(property.getPropertyType())) {
							// no elements but attributes
							// use an instance as value, it will be assigned an instance value if possible
							group.addProperty(property.getName(), 
									parseInstance(reader, property.getPropertyType()));
						}
						else {
							// no elements and no attributes
							// use simple value
							String value = reader.getElementText();
							if (value != null) {
								addSimpleProperty(group, property, value);
							}
						}
					}
				}
				else {
					//TODO search in groups for property
					//XXX remember a last group (hierarchy?) and keep it open for following elements?
				}
//							else {
//								log.warn(MessageFormat.format(
//										"No property ''{0}'' found in type ''{1}'', value is ignored", 
//										reader.getLocalName(), type.getDisplayName()));
//							}
				
				if (reader.getEventType() != XMLStreamConstants.END_ELEMENT) {
					// only increase open if the current event is not already the end element (because we used getElementText)
					open++;
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				open--;
				break;
			}
		}
	}

	/**
	 * Determines if the given type has properties that are represented
	 * as XML elements.
	 * 
	 * @param type the type definition
	 * @return if the type is a complex type
	 */
	private static boolean hasElements(TypeDefinition type) {
		for (ChildDefinition<?> child : type.getChildren()) {
			if (child.asProperty() != null) {
				if (!child.asProperty().getConstraint(XmlAttributeFlag.class).isEnabled()) {
					return true;
				}
			}
			else if (child.asGroup() != null) {
				//FIXME what about groups?!
			}
		}
		
		return false;
	}
	
	/**
	 * Determines if the given type has properties that are represented
	 * as XML attributes.
	 * 
	 * @param type the type definition
	 * @return if the type has at least one XML attribute property
	 */
	private static boolean hasAttributes(TypeDefinition type) {
		for (ChildDefinition<?> child : type.getChildren()) {
			if (child.asProperty() != null) {
				if (child.asProperty().getConstraint(XmlAttributeFlag.class).isEnabled()) {
					return true;
				}
			}
			else if (child.asGroup() != null) {
				//XXX what about attribute groups
				// groups are ignored for now as attribute groups are flattened (and there doesn't seem to be a case where this doesn't work)
			}
		}
		
		return false;
	}

	/**
	 * Adds a property value to the given instance. The property value will
	 * be converted appropriately.
	 * 
	 * @param group the instance
	 * @param property the property
	 * @param value the property value as specified in the XML
	 */
	private static void addSimpleProperty(MutableGroup group,
			PropertyDefinition property, String value) {
		Object val = convertSimple(property.getPropertyType(), value);
		group.addProperty(property.getName(), val);
	}

	/**
	 * Convert a string value from a XML simple type to the binding defined
	 * by the given type.
	 * 
	 * @param type the type associated with the value
	 * @param value the value
	 * @return the converted object
	 */
	private static Object convertSimple(TypeDefinition type,
			String value) {
		// TODO Auto-generated method stub
		//FIXME for now no conversion
		return value;
	}

}
