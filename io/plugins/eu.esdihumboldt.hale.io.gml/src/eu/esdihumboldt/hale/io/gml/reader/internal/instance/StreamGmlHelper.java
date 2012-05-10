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

package eu.esdihumboldt.hale.io.gml.reader.internal.instance;

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
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.MutableGroup;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.OInstance;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AugmentedValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.io.gml.geometry.constraint.GeometryFactory;
import eu.esdihumboldt.hale.io.gml.internal.simpletype.SimpleTypeUtil;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;

/**
 * Utility methods for instances from {@link XMLStreamReader}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class StreamGmlHelper {

	private static final ALogger log = ALoggerFactory
			.getLogger(StreamGmlHelper.class);

	/**
	 * Parses an instance with the given type from the given XML stream reader.
	 * 
	 * @param reader
	 *            the XML stream reader, the current event must be the start
	 *            element of the instance
	 * @param type
	 *            the definition of the instance type
	 * @param indexInStream
	 *            the index of the instance in the stream or <code>null</code>
	 * @param strict
	 *            if associating elements with properties should be done
	 *            strictly according to the schema, otherwise a fall-back is
	 *            used trying to populate values also on invalid property paths
	 * @return the parsed instance
	 * @throws XMLStreamException
	 *             if parsing the instance failed
	 */
	public static Instance parseInstance(XMLStreamReader reader,
			TypeDefinition type, Integer indexInStream, boolean strict)
			throws XMLStreamException {
		checkState(reader.getEventType() == XMLStreamConstants.START_ELEMENT);

		// XXX get srsDimension from the upper geometry
		String dimension = reader.getAttributeValue(null, "srsDimension");

		MutableInstance instance;
		if (indexInStream == null) {
			instance = new OInstance(type, null); // not necessary to associate
													// data set
		} else {
			instance = new StreamGmlInstance(type, indexInStream);
		}

		// instance properties
		parseProperties(reader, instance, strict);

		// instance value
		if (type.getConstraint(HasValueFlag.class).isEnabled()) {
			// FIXME check for xsi:nil!
			// XXX or use reader.hasText()?

			// try to get text value
			String value = reader.getElementText();
			if (value != null) {
				instance.setValue(convertSimple(type, value));
			}
		}

		// augmented value XXX should this be an else if?
		if (type.getConstraint(AugmentedValueFlag.class).isEnabled()) {
			// add geometry as a GeometryProperty value where applicable
			GeometryFactory geomFactory = type
					.getConstraint(GeometryFactory.class);
			Object geomValue;

			// the default value for the srsDimension
			int defaultValue = 2;

			if (dimension != null)
				geomValue = geomFactory.createGeometry(instance,
						Integer.parseInt(dimension));
			
			// if srsDimension is not set
			else
				geomValue = geomFactory.createGeometry(instance, defaultValue);
			if (geomValue != null) {
				instance.setValue(geomValue);
			}
		}

		return instance;
	}

	/**
	 * Populates an instance or group with its properties based on the given XML
	 * stream reader.
	 * 
	 * @param reader
	 *            the XML stream reader
	 * @param group
	 *            the group to populate with properties
	 * @param strict
	 *            if associating elements with properties should be done
	 *            strictly according to the schema, otherwise a fall-back is
	 *            used trying to populate values also on invalid property paths
	 * @throws XMLStreamException
	 *             if parsing the properties failed
	 */
	private static void parseProperties(XMLStreamReader reader,
			MutableGroup group, boolean strict) throws XMLStreamException {
		final MutableGroup topGroup = group;

		// attributes (usually only present in Instances)
		for (int i = 0; i < reader.getAttributeCount(); i++) {
			QName propertyName = reader.getAttributeName(i);
			// XXX might also be inside a group? currently every attribute group
			// should be flattened
			// for group support there would have to be some other kind of
			// handling than for elements, cause order doesn't matter for
			// attributes
			ChildDefinition<?> child = group.getDefinition().getChild(
					propertyName);
			if (child != null && child.asProperty() != null) {
				// add property value
				addSimpleProperty(group, child.asProperty(),
						reader.getAttributeValue(i));
			} else {
				log.warn(MessageFormat
						.format("No property ''{0}'' found in type ''{1}'', value is ignored",
								propertyName, group.getDefinition()
										.getIdentifier()));
			}
		}

		Stack<MutableGroup> groups = new Stack<MutableGroup>();
		groups.push(topGroup);

		// elements
		if (hasElements(group.getDefinition())) {
			int open = 1;
			while (open > 0 && reader.hasNext()) {
				int event = reader.next();
				switch (event) {
				case XMLStreamConstants.START_ELEMENT:
					// determine property definition, allow fall-back to
					// non-strict mode
					GroupProperty gp = GroupUtil.determineProperty(groups,
							reader.getName(), !strict);
					if (gp != null) {
						// update the stack from the path
						groups = gp.getPath().getAllGroups(strict);
						// get group object from stack
						group = groups.peek();

						PropertyDefinition property = gp.getProperty();

						if (hasElements(property.getPropertyType())) {
							// use an instance as value
							group.addProperty(
									property.getName(),
									parseInstance(reader,
											property.getPropertyType(), null,
											strict));
						} else {
							if (hasAttributes(property.getPropertyType())) {
								// no elements but attributes
								// use an instance as value, it will be assigned
								// an instance value if possible
								group.addProperty(
										property.getName(),
										parseInstance(reader,
												property.getPropertyType(),
												null, strict));
							} else {
								// no elements and no attributes
								// use simple value
								String value = reader.getElementText();
								if (value != null) {
									addSimpleProperty(group, property, value);
								}
							}
						}
					} else {
						log.warn(MessageFormat
								.format("No property ''{0}'' found in type ''{1}'', value is ignored",
										reader.getLocalName(), topGroup
												.getDefinition()
												.getIdentifier()));
					}

					if (reader.getEventType() != XMLStreamConstants.END_ELEMENT) {
						// only increase open if the current event is not
						// already the end element (because we used
						// getElementText)
						open++;
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					open--;
					break;
				}
			}
		}
	}

	/**
	 * Determines if the given type has properties that are represented as XML
	 * elements.
	 * 
	 * @param group
	 *            the type definition
	 * @return if the type is a complex type
	 */
	static boolean hasElements(DefinitionGroup group) {
		return hasElementsOrAttributes(group, false,
				new HashSet<DefinitionGroup>());
	}

	private static boolean hasElementsOrAttributes(DefinitionGroup group,
			boolean attributes, Set<DefinitionGroup> tested) {
		if (tested.contains(group)) {
			// prevent cycles
			return false;
		} else {
			tested.add(group);
		}

		Collection<? extends ChildDefinition<?>> children = DefinitionUtil
				.getAllChildren(group);

		for (ChildDefinition<?> child : children) {
			if (child.asProperty() != null) {
				if (child.asProperty().getConstraint(XmlAttributeFlag.class)
						.isEnabled()) {
					if (attributes) {
						return true;
					}
				} else {
					if (!attributes) {
						return true;
					}
				}
			} else if (child.asGroup() != null) {
				if (hasElementsOrAttributes(child.asGroup(), attributes, tested)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Determines if the given type has properties that are represented as XML
	 * attributes.
	 * 
	 * @param group
	 *            the type definition
	 * @return if the type has at least one XML attribute property
	 */
	static boolean hasAttributes(DefinitionGroup group) {
		return hasElementsOrAttributes(group, true,
				new HashSet<DefinitionGroup>());
	}

	/**
	 * Adds a property value to the given instance. The property value will be
	 * converted appropriately.
	 * 
	 * @param group
	 *            the instance
	 * @param property
	 *            the property
	 * @param value
	 *            the property value as specified in the XML
	 */
	private static void addSimpleProperty(MutableGroup group,
			PropertyDefinition property, String value) {
		Object val = convertSimple(property.getPropertyType(), value);
		group.addProperty(property.getName(), val);
	}

	/**
	 * Convert a string value from a XML simple type to the binding defined by
	 * the given type.
	 * 
	 * @param type
	 *            the type associated with the value
	 * @param value
	 *            the value
	 * @return the converted object
	 */
	private static Object convertSimple(TypeDefinition type, String value) {
		return SimpleTypeUtil.convertFromXml(value, type);
	}

}
