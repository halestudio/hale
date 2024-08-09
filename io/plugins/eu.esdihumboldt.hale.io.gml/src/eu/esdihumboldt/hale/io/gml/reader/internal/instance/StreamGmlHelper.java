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

package eu.esdihumboldt.hale.io.gml.reader.internal.instance;

import static com.google.common.base.Preconditions.checkState;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.locationtech.jts.geom.Geometry;

import com.google.common.collect.Iterables;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.instance.geometry.CRSProvider;
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.MutableGroup;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AugmentedValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.io.gml.geometry.constraint.GeometryFactory;
import eu.esdihumboldt.hale.io.gml.internal.simpletype.SimpleTypeUtil;
import eu.esdihumboldt.hale.io.gml.reader.internal.StreamGmlReader;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlMixedFlag;

/**
 * Utility methods for instances from {@link XMLStreamReader}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class StreamGmlHelper {

	private static final ALogger log = ALoggerFactory.getLogger(StreamGmlHelper.class);

	/**
	 * Parses an instance with the given type from the given XML stream reader.
	 * 
	 * @param reader the XML stream reader, the current event must be the start
	 *            element of the instance
	 * @param type the definition of the instance type
	 * @param indexInStream the index of the instance in the stream or
	 *            <code>null</code>
	 * @param strict if associating elements with properties should be done
	 *            strictly according to the schema, otherwise a fall-back is
	 *            used trying to populate values also on invalid property paths
	 * @param srsDimension the dimension of the instance or <code>null</code>
	 * @param crsProvider CRS provider in case no CRS is specified, may be
	 *            <code>null</code>
	 * @param parentType the type of the topmost instance
	 * @param propertyPath the property path down from the topmost instance, may
	 *            be <code>null</code>
	 * @param allowNull if a <code>null</code> result is allowed
	 * @param ignoreNamespaces if parsing of the XML instances should allow
	 *            types and properties with namespaces that differ from those
	 *            defined in the schema
	 * @param ioProvider the I/O Provider to get value
	 * @return the parsed instance, may be <code>null</code> if allowNull is
	 *         <code>true</code>
	 * @throws XMLStreamException if parsing the instance failed
	 */
	public static Instance parseInstance(XMLStreamReader reader, TypeDefinition type,
			Integer indexInStream, boolean strict, AtomicInteger srsDimension,
			CRSProvider crsProvider, TypeDefinition parentType, List<QName> propertyPath,
			boolean allowNull, boolean ignoreNamespaces, IOProvider ioProvider)
			throws XMLStreamException {

		return parseInstance(reader, type, indexInStream, strict, srsDimension, crsProvider,
				parentType, propertyPath, allowNull, ignoreNamespaces, ioProvider, null);
	}

	/**
	 * Parses an instance with the given type from the given XML stream reader.
	 * 
	 * @param reader the XML stream reader, the current event must be the start
	 *            element of the instance
	 * @param type the definition of the instance type
	 * @param indexInStream the index of the instance in the stream or
	 *            <code>null</code>
	 * @param strict if associating elements with properties should be done
	 *            strictly according to the schema, otherwise a fall-back is
	 *            used trying to populate values also on invalid property paths
	 * @param srsDimension the dimension of the instance or <code>null</code>
	 * @param crsProvider CRS provider in case no CRS is specified, may be
	 *            <code>null</code>
	 * @param parentType the type of the topmost instance
	 * @param propertyPath the property path down from the topmost instance, may
	 *            be <code>null</code>
	 * @param allowNull if a <code>null</code> result is allowed
	 * @param ignoreNamespaces if parsing of the XML instances should allow
	 *            types and properties with namespaces that differ from those
	 *            defined in the schema
	 * @param ioProvider the I/O Provider to get value
	 * @param crs The <code>CRSDefinition</code> to use for geometries
	 * @return the parsed instance, may be <code>null</code> if allowNull is
	 *         <code>true</code>
	 * @throws XMLStreamException if parsing the instance failed
	 */
	public static Instance parseInstance(XMLStreamReader reader, TypeDefinition type,
			Integer indexInStream, boolean strict, AtomicInteger srsDimension,
			CRSProvider crsProvider, TypeDefinition parentType, List<QName> propertyPath,
			boolean allowNull, boolean ignoreNamespaces, IOProvider ioProvider, CRSDefinition crs)
			throws XMLStreamException {

		if (srsDimension == null) {
			// Initialize srsDimension here w/ dummy value to be passed down
			// when parsing the instance. That way "srsDimension" attributes
			// at the coordinate level (e.g. "pos", "posList" etc.) can be
			// evaluated and its valued passed back up to the level where
			// the geometry object is created.
			srsDimension = new AtomicInteger(-1);
		}

		checkState(reader.getEventType() == XMLStreamConstants.START_ELEMENT);
		if (propertyPath == null) {
			propertyPath = Collections.emptyList();
		}

		String dim = reader.getAttributeValue(null, "srsDimension");
		if (dim != null) {
			srsDimension.set(Integer.parseInt(dim));
		}

		// extract additional settings from I/O provider
		boolean suppressParsingGeometry = ioProvider
				.getParameter(StreamGmlReader.PARAM_SUPPRESS_PARSE_GEOMETRY)
				.as(Boolean.class, false);

		MutableInstance instance;
		if (indexInStream == null) {
			// not necessary to associate data set
			instance = new DefaultInstance(type, null);
		}
		else {
			instance = new StreamGmlInstance(type, indexInStream);
		}

		// If the current instance has an srsName attribute, try to resolve the
		// corresponding CRS and pass it down the hierarchy and use it for
		// nested geometries that don't have their own srsName.
		CRSDefinition lastCrs = crs;
		String srsName = reader.getAttributeValue(null, "srsName");
		if (srsName != null) {
			lastCrs = CodeDefinition.tryResolve(srsName);

			if (lastCrs == null && crsProvider != null) {
				// In case the srsName value could not be resolved to a CRS, try
				// to resolve the CRS via the crsProvider.
				CRSDefinition unresolvedCrs = new CodeDefinition(srsName);
				CRSDefinition resolvedCrs = crsProvider.getCRS(parentType, propertyPath,
						unresolvedCrs);

				// Only use resolvedCrs if crsProvider did not merely return
				// unresolvedCrs unchanged
				if (resolvedCrs != null && !resolvedCrs.equals(unresolvedCrs)) {
					lastCrs = resolvedCrs;
				}
			}

			// If the provided CRS could not be resolved, it will be ignored
			// here silently, so that use cases that don't need the CRS will not
			// fail.
		}

		boolean mixed = type.getConstraint(XmlMixedFlag.class).isEnabled();

		if (!mixed) {
			// mixed types are treated special (see else)

			// check if xsi:nil attribute is there and set to true
			String nilString = reader.getAttributeValue(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
					"nil");
			boolean isNil = nilString != null && "true".equalsIgnoreCase(nilString);

			// instance properties
			parseProperties(reader, instance, strict, srsDimension, crsProvider, lastCrs,
					parentType, propertyPath, false, ignoreNamespaces, ioProvider);

			// nil instance w/o properties
			if (allowNull && isNil && Iterables.isEmpty(instance.getPropertyNames())) {
				// no value should be created
				/*
				 * XXX returning null here then results in problems during
				 * adding other properties to the parent group, as mandatory
				 * elements are expected to appear, and it will warn about
				 * possible invalid data loaded
				 */
//				return null;
			}

			// instance value
			if (!hasElements(type)) {
				/*
				 * Value can only be determined if there are no documents,
				 * because otherwise elements have already been processed in
				 * parseProperties and we are already past END_ELEMENT.
				 */
				if (type.getConstraint(HasValueFlag.class).isEnabled()) {
					// try to get text value
					String value = reader.getElementText();
					if (!isNil && value != null) {
						instance.setValue(convertSimple(type, value));
					}
				}
			}
		}
		else {
			/*
			 * XXX For a mixed type currently ignore elements and parse only
			 * attributes and text.
			 */

			// instance properties (attributes only)
			parseProperties(reader, instance, strict, srsDimension, crsProvider, lastCrs,
					parentType, propertyPath, true, ignoreNamespaces, ioProvider);

			// combined text
			String value = readText(reader);
			if (value != null) {
				instance.setValue(convertSimple(type, value));
			}
		}

		// augmented value XXX should this be an else if?
		if (!suppressParsingGeometry && type.getConstraint(AugmentedValueFlag.class).isEnabled()) {
			// add geometry as a GeometryProperty value where applicable
			GeometryFactory geomFactory = type.getConstraint(GeometryFactory.class);
			Object geomValue = null;

			// the default value for the srsDimension
			int defaultValue = 2;

			try {
				if (srsDimension.get() != -1) {
					geomValue = geomFactory.createGeometry(instance, srsDimension.get(),
							ioProvider);
				}
				else {
					// srsDimension is not set
					geomValue = geomFactory.createGeometry(instance, defaultValue, ioProvider);
				}
			} catch (Exception e) {
				/*
				 * Catch IllegalArgumentException that e.g. occurs if a linear
				 * ring has to few points. NullPointerExceptions may occur
				 * because an internal geometry could not be created.
				 * 
				 * XXX a problem is that these messages will not appear in the
				 * report
				 */
				log.error("Error creating geometry", e);
			}

			if (geomValue != null && crsProvider != null && propertyPath != null) {
				// check if CRS are set, and if not, try determining them using
				// the CRS provider
				Collection<?> values;
				if (geomValue instanceof Collection) {
					values = (Collection<?>) geomValue;
				}
				else {
					values = Collections.singleton(geomValue);
				}

				List<Object> resultVals = new ArrayList<Object>();
				for (Object value : values) {
					if (value instanceof Geometry || (value instanceof GeometryProperty<?>
							&& ((GeometryProperty<?>) value).getCRSDefinition() == null)) {
						// try to resolve value of srsName attribute

						if (lastCrs == null) {
							lastCrs = crsProvider.getCRS(parentType, propertyPath, lastCrs);
						}

						if (lastCrs != null) {
							Geometry geom = (value instanceof Geometry) ? ((Geometry) value)
									: (((GeometryProperty<?>) value).getGeometry());
							resultVals.add(new DefaultGeometryProperty<Geometry>(lastCrs, geom));
							continue;
						}
					}

					resultVals.add(value);
				}

				if (resultVals.size() == 1) {
					geomValue = resultVals.get(0);
				}
				else {
					geomValue = resultVals;
				}
			}

			if (geomValue != null) {
				instance.setValue(geomValue);
			}
		}

		return instance;
	}

	/**
	 * Read the text value of the current element from the stream. The stream is
	 * expected to be at {@link XMLStreamConstants#START_ELEMENT}. For mixed
	 * content elements the text content is concatenated and the elements
	 * ignored.
	 * 
	 * FIXME different handling for mixed types?
	 * 
	 * @param reader the XML stream reader
	 * @return the element text
	 * @throws XMLStreamException if an error occurs reading from the stream or
	 *             the element ends prematurely
	 */
	private static String readText(XMLStreamReader reader) throws XMLStreamException {
		checkState(reader.getEventType() == XMLStreamConstants.START_ELEMENT);

		int eventType = reader.next();
		StringBuilder content = new StringBuilder();
		int openElements = 0;

		while (openElements > 0 || eventType != XMLStreamConstants.END_ELEMENT) {
			if (eventType == XMLStreamConstants.CHARACTERS || eventType == XMLStreamConstants.CDATA
					|| eventType == XMLStreamConstants.SPACE
					|| eventType == XMLStreamConstants.ENTITY_REFERENCE) {
				content.append(reader.getText());
			}
			else if (eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
					|| eventType == XMLStreamConstants.COMMENT) {
				// skipping
			}
			else if (eventType == XMLStreamConstants.END_DOCUMENT) {
				throw new XMLStreamException(
						"unexpected end of document when reading element text content");
			}
			else if (eventType == XMLStreamConstants.START_ELEMENT) {
				openElements++;
			}
			else if (eventType == XMLStreamConstants.END_ELEMENT) {
				openElements--;
			}
			else {
				// ignore
//				throw new XMLStreamException("Unexpected event type " + eventType, reader.getLocation());
			}
			eventType = reader.next();
		}

		checkState(reader.getEventType() == XMLStreamConstants.END_ELEMENT);

		return content.toString();
	}

	/**
	 * Populates an instance or group with its properties based on the given XML
	 * stream reader.
	 * 
	 * @param reader the XML stream reader
	 * @param group the group to populate with properties
	 * @param strict if associating elements with properties should be done
	 *            strictly according to the schema, otherwise a fall-back is
	 *            used trying to populate values also on invalid property paths
	 * @param srsDimension the dimension of the instance or <code>null</code>
	 * @param crsProvider CRS provider in case no CRS is specified, may be
	 *            <code>null</code>
	 * @param crs CRS definition to use if the property contains a geometry that
	 *            does not carry its own srsName attribute
	 * @param parentType the type of the topmost instance
	 * @param propertyPath the property path down from the topmost instance
	 * @param onlyAttributes if only attributes should be parsed
	 * @param ignoreNamespaces if parsing of the XML instances should allow
	 *            types and properties with namespaces that differ from those
	 *            defined in the schema
	 * @param ioProvider the I/O Provider to get value
	 * @throws XMLStreamException if parsing the properties failed
	 */
	private static void parseProperties(XMLStreamReader reader, MutableGroup group, boolean strict,
			AtomicInteger srsDimension, CRSProvider crsProvider, CRSDefinition crs,
			TypeDefinition parentType, List<QName> propertyPath, boolean onlyAttributes,
			boolean ignoreNamespaces, IOProvider ioProvider) throws XMLStreamException {
		final MutableGroup topGroup = group;

		// attributes (usually only present in Instances)
		for (int i = 0; i < reader.getAttributeCount(); i++) {
			QName propertyName = reader.getAttributeName(i);
			// XXX might also be inside a group? currently every attribute group
			// should be flattened
			// for group support there would have to be some other kind of
			// handling than for elements, cause order doesn't matter for
			// attributes
			ChildDefinition<?> child = GroupUtil.findChild(group.getDefinition(), propertyName,
					ignoreNamespaces);
			if (child != null && child.asProperty() != null) {
				// add property value
				addSimpleProperty(group, child.asProperty(), reader.getAttributeValue(i));
			}
			else {
				// suppress warnings for xsi attributes (e.g. xsi:nil)
				boolean suppress = XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI
						.equals(propertyName.getNamespaceURI());

				if (!suppress) {
					log.warn(MessageFormat.format(
							"No property ''{0}'' found in type ''{1}'', value is ignored",
							propertyName, group.getDefinition().getIdentifier()));
				}
			}
		}

		Stack<MutableGroup> groups = new Stack<MutableGroup>();
		groups.push(topGroup);

		// elements
		if (!onlyAttributes && hasElements(group.getDefinition())) {
			int open = 1;
			while (open > 0 && reader.hasNext()) {
				int event = reader.next();
				switch (event) {
				case XMLStreamConstants.START_ELEMENT:
					// determine property definition, allow fall-back to
					// non-strict mode
					GroupProperty gp = GroupUtil.determineProperty(groups, reader.getName(),
							!strict, ignoreNamespaces);
					if (gp != null) {
						// update the stack from the path
						groups = gp.getPath().getAllGroups(strict);
						// get group object from stack
						group = groups.peek();

						PropertyDefinition property = gp.getProperty();
						List<QName> path = new ArrayList<QName>(propertyPath);
						path.add(property.getName());

						if (hasElements(property.getPropertyType())) {
							// use an instance as value
							Instance inst = parseInstance(reader, property.getPropertyType(), null,
									strict, srsDimension, crsProvider, parentType, path, true,
									ignoreNamespaces, ioProvider, crs);
							if (inst != null) {
								group.addProperty(property.getName(), inst);
							}
						}
						else {
							if (hasAttributes(property.getPropertyType())) {
								// no elements but attributes
								// use an instance as value, it will be assigned
								// an instance value if possible
								Instance inst = parseInstance(reader, property.getPropertyType(),
										null, strict, srsDimension, crsProvider, parentType, path,
										true, ignoreNamespaces, ioProvider);
								if (inst != null) {
									group.addProperty(property.getName(), inst);
								}
							}
							else {
								// no elements and no attributes
								// use simple value
								String value = readText(reader);
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
	 * @param group the type definition
	 * @return if the type is a complex type
	 */
	static boolean hasElements(DefinitionGroup group) {
		return hasElementsOrAttributes(group, false, new HashSet<DefinitionGroup>());
	}

	private static boolean hasElementsOrAttributes(DefinitionGroup group, boolean attributes,
			Set<DefinitionGroup> tested) {
		if (tested.contains(group)) {
			// prevent cycles
			return false;
		}
		else {
			tested.add(group);
		}

		Collection<? extends ChildDefinition<?>> children = DefinitionUtil.getAllChildren(group);

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
	 * Determines if the given type has properties that are represented as XML
	 * attributes.
	 * 
	 * @param group the type definition
	 * @return if the type has at least one XML attribute property
	 */
	static boolean hasAttributes(DefinitionGroup group) {
		return hasElementsOrAttributes(group, true, new HashSet<DefinitionGroup>());
	}

	/**
	 * Adds a property value to the given instance. The property value will be
	 * converted appropriately.
	 * 
	 * @param group the instance
	 * @param property the property
	 * @param value the property value as specified in the XML
	 */
	private static void addSimpleProperty(MutableGroup group, PropertyDefinition property,
			String value) {
		Object val = convertSimple(property.getPropertyType(), value);
		group.addProperty(property.getName(), val);
	}

	/**
	 * Convert a string value from a XML simple type to the binding defined by
	 * the given type.
	 * 
	 * @param type the type associated with the value
	 * @param value the value
	 * @return the converted object
	 */
	private static Object convertSimple(TypeDefinition type, String value) {
		return SimpleTypeUtil.convertFromXml(value, type);
	}

}
