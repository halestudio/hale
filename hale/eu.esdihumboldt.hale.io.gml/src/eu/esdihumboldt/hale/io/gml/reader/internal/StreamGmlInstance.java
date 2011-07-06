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

import static com.google.common.base.Preconditions.checkState;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

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
import eu.esdihumboldt.hale.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.schema.model.constraint.property.ChoiceFlag;
import eu.esdihumboldt.hale.schema.model.constraint.type.HasValueFlag;

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
		parseProperties(reader, instance);

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
	 * @throws XMLStreamException if parsing the properties failed
	 */
	private static void parseProperties(XMLStreamReader reader,
			MutableGroup group) throws XMLStreamException {
		final MutableGroup topGroup = group;
		
		// attributes (usually only present in Instances)
		for (int i = 0; i < reader.getAttributeCount(); i++) {
			QName propertyName = reader.getAttributeName(i);
			//XXX might also be inside a group? currently every attribute group should be flattened
			// for group support there would have to be some other kind of handling than for elements, cause order doesn't matter for attributes  
			ChildDefinition<?> child = group.getDefinition().getChild(propertyName);
			if (child != null && child.asProperty() != null) {
				// add property value
				addSimpleProperty(group, child.asProperty(), reader.getAttributeValue(i));
			}
			else {
				log.warn(MessageFormat.format(
						"No property ''{0}'' found in type ''{1}'', value is ignored", 
						propertyName, group.getDefinition().getIdentifier()));
			}
		}
		
		Stack<MutableGroup> groups = new Stack<MutableGroup>();
		groups.push(topGroup);
				
		// elements
		int open = 1;
		while (open > 0 && reader.hasNext()) {
			int event = reader.next();
			switch (event) {
			case XMLStreamConstants.START_ELEMENT:
				PropertyDefinition property = determineProperty(groups, reader.getName());
				
				// get group object from stack
				group = groups.peek();
				
				if (property != null) {
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
					log.warn(MessageFormat.format(
							"No property ''{0}'' found in type ''{1}'', value is ignored", 
							reader.getLocalName(), topGroup.getDefinition().getIdentifier()));
				}
				
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
	 * Determine the property definition for the given property name.
	 * The given group stack will be updated so that the parent group object of 
	 * the property will be the top element on the stack. 
	 * @param groups the stack of the current group objects. The topmost element
	 *   is the current group object 
	 * @param propertyName the property name
	 * @return the property definition or <code>null</code> if none is found
	 */
	private static PropertyDefinition determineProperty(
			Stack<MutableGroup> groups, QName propertyName) {
		MutableGroup group = groups.peek();
		
		// preferred 1: property of a parent group
		//TODO
		
		// preferred 2: property of the current group
		ChildDefinition<?> child = group.getDefinition().getChild(propertyName);
		if (child != null && child.asProperty() != null && 
				allowAdd(group, child.asProperty())) {
			return child.asProperty();
		}
		
		// preferred 3: property of a sister group
		//TODO
		
		// fall-back: property of a sub-group
		//TODO
		
		return null;
	}

	/**
	 * Determines if another value of the given property may be added to the
	 * given group.
	 * @param group the group
	 * @param property the property
	 * @return if another property value may be added to the group
	 */
	private static boolean allowAdd(MutableGroup group,
			PropertyDefinition property) {
		DefinitionGroup def = group.getDefinition();
		
		if (def instanceof GroupPropertyDefinition) {
			// group property
			GroupPropertyDefinition groupDef = (GroupPropertyDefinition) def;
			
			if (groupDef.getConstraint(ChoiceFlag.class).isEnabled()) {
				// choice
				// a choice may only contain one of its properties
				for (QName propertyName : group.getPropertyNames()) {
					if (!propertyName.equals(property.getName())) {
						// other property is present -> may not add property value
						return false;
					}
				}
				// check cardinality
				return allowAddCheckCardinality(group, property);
			}
			else {
				// sequence, group(, attributeGroup)
				
				// check order
				if (!allowAddCheckOrder(group, property, groupDef.getDeclaredChildren())) {
					return false;
				}
				
				// check cardinality
				return allowAddCheckCardinality(group, property);
			}
		}
		else if (def instanceof TypeDefinition) {
			// type
			TypeDefinition typeDef = (TypeDefinition) def;
			
			// check order
			if (!allowAddCheckOrder(group, property, typeDef.getChildren())) {
				return false;
			}
			
			// check cardinality
			return allowAddCheckCardinality(group, property);
		}
		
		return false;
	}

	/**
	 * Determines if another value of the given property may be added to the
	 * given group based on values available in the group and the order
	 * of the given child definitions.
	 * @param group the group
	 * @param property the property
	 * @param children the child definitions
	 * @return if another property value may be added to the group based on the
	 *   values and the child definition order
	 */
	private static boolean allowAddCheckOrder(MutableGroup group,
			PropertyDefinition property, Collection<? extends ChildDefinition<?>> children) {
		boolean before = true;
		for (ChildDefinition<?> childDef : children) {
			if (childDef.getName().equals(property.getName())) {
				before = false;
			}
			else {
				// ignore XML attributes
				if (childDef.asProperty() != null && childDef.asProperty().getConstraint(XmlAttributeFlag.class).isEnabled()) {
					continue;
				}
				// ignore groups that contain no elements
				if (childDef.asGroup() != null && !hasElements(childDef.asGroup())) {
					continue;
				}
				
				if (before) {
					// child before the property
					// the property may only be added if all children before are valid in their cardinality
					Cardinality cardinality = null;
					if (childDef.asProperty() != null) {
						cardinality = childDef.asProperty().getConstraint(Cardinality.class);
					}
					else if (childDef.asGroup() != null) {
						cardinality = childDef.asGroup().getConstraint(Cardinality.class);
					}
					else {
						log.error("Unrecognized child definition.");
					}
					
					if (cardinality != null) {
						// check minimum
						long min = cardinality.getMinOccurs();
						if (min > 0 && min != Cardinality.UNBOUNDED) {
							Object[] values = group.getProperty(childDef.getName());
							int count = (values == null)?(0):(values.length);
							if (min > count) {
								return false;
							}
						}
					}
				}
				else {
					// child after the property
					// the property may only be added if there are no values for children after the property
					Object[] values = group.getProperty(childDef.getName());
					if (values != null && values.length > 0) {
						return false;
					}
				}
			}
		}
		
		// no fail -> allow add
		return true;
	}

	/**
	 * Determines if another value of the given property may be added to the
	 * given group based on the cardinality of the property.
	 * @param group the group
	 * @param property the property
	 * @return if another property value may be added to the group based on the
	 *   property cardinality
	 */
	private static boolean allowAddCheckCardinality(MutableGroup group,
			PropertyDefinition property) {
		Cardinality cardinality = property.getConstraint(Cardinality.class);
		
		// check maximum
		long max = cardinality.getMaxOccurs();
		if (max == Cardinality.UNBOUNDED) {
			return true; // add allowed in any case
		}
		else if (max <= 0) {
			return false; // add never allowed
		}
		
		Object[] values = group.getProperty(property.getName());
		if (values == null) {
			return true; // allowed because max is 1 or more
		}
		
		return values.length < max;
	}

	/**
	 * Determines if the given type has properties that are represented
	 * as XML elements.
	 * 
	 * @param group the type definition
	 * @return if the type is a complex type
	 */
	private static boolean hasElements(DefinitionGroup group) {
		return hasElementsOrAttributes(group, false, new HashSet<DefinitionGroup>());
	}
	
	private static boolean hasElementsOrAttributes(DefinitionGroup group,
			boolean attributes, Set<DefinitionGroup> tested) {
		if (tested.contains(group)) {
			// prevent cycles
			return false;
		}
		else {
			tested.add(group);
		}
		
		Collection<? extends ChildDefinition<?>> children;
		if (group instanceof TypeDefinition) {
			children = ((TypeDefinition) group).getChildren();
		}
		else {
			children = group.getDeclaredChildren();
		}
		
		for (ChildDefinition<?> child : children) {
			if (child.asProperty() != null) {
				if (child.asProperty().getConstraint(XmlAttributeFlag.class).isEnabled()) {
					if (attributes) {
						return true;
					}
				}
				else {
					if (!attributes) {
						return true;
					}
				}
			}
			else if (child.asGroup() != null) {
				if (hasElementsOrAttributes(child.asGroup(), attributes, tested)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Determines if the given type has properties that are represented
	 * as XML attributes.
	 * 
	 * @param group the type definition
	 * @return if the type has at least one XML attribute property
	 */
	private static boolean hasAttributes(DefinitionGroup group) {
		return hasElementsOrAttributes(group, true, new HashSet<DefinitionGroup>());
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
