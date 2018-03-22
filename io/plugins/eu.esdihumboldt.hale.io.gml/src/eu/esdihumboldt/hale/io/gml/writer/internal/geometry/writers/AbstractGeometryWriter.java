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

package eu.esdihumboldt.hale.io.gml.writer.internal.geometry.writers;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.Descent;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.GeometryWriter;
import eu.esdihumboldt.util.format.DecimalFormatUtil;

/**
 * Abstract geometry writer implementation
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 * @param <T> the geometry type
 */
public abstract class AbstractGeometryWriter<T extends Geometry> extends AbstractPathMatcher
		implements GeometryWriter<T> {

	private static final ALogger log = ALoggerFactory.getLogger(AbstractGeometryWriter.class);

	private final Class<T> geometryType;

	private final Set<QName> compatibleTypes = new HashSet<QName>();

	/**
	 * The attribute type names supported for writing coordinates with
	 * {@link #writeCoordinates(XMLStreamWriter, Coordinate[], TypeDefinition, String, DecimalFormat)}
	 * or
	 * {@link #descendAndWriteCoordinates(XMLStreamWriter, Pattern, Coordinate[], TypeDefinition, QName, String, boolean, DecimalFormat)}
	 * .
	 * 
	 * Use for validating end-points.
	 */
	private final static Set<String> SUPPORTED_COORDINATES_TYPES = Collections
			.unmodifiableSet(new HashSet<String>(Arrays.asList("DirectPositionType", //$NON-NLS-1$
					"DirectPositionListType", "CoordinatesType"))); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * Constructor
	 * 
	 * @param geometryType the geometry type
	 */
	public AbstractGeometryWriter(Class<T> geometryType) {
		super();
		this.geometryType = geometryType;
	}

	/**
	 * @see GeometryWriter#getCompatibleTypes()
	 */
	@Override
	public Set<QName> getCompatibleTypes() {
		return Collections.unmodifiableSet(compatibleTypes);
	}

	/**
	 * Add a compatible type. A {@link Pattern#GML_NAMESPACE_PLACEHOLDER}
	 * namespace references the GML namespace.
	 * 
	 * @param typeName the type name
	 */
	public void addCompatibleType(QName typeName) {
		compatibleTypes.add(typeName);
	}

	/**
	 * @see GeometryWriter#getGeometryType()
	 */
	@Override
	public Class<T> getGeometryType() {
		return geometryType;
	}

	/**
	 * Verify the verification end point. After reaching the end-point of a
	 * verification pattern this method is called with the
	 * {@link TypeDefinition} of the end-point to assure the needed structure is
	 * present (e.g. a DirectPositionListType element). If no verification
	 * pattern is present the end-point of the matched base pattern will be
	 * verified. The default implementation checks for properties with any of
	 * the types supported for writing coordinates.
	 * 
	 * @see #SUPPORTED_COORDINATES_TYPES
	 * 
	 * @param endPoint the end-point type definition
	 * 
	 * @return if the end-point is valid for writing the geometry
	 */
	@Override
	protected boolean verifyEndPoint(TypeDefinition endPoint) {
		for (PropertyDefinition attribute : DefinitionUtil.getAllProperties(endPoint)) {
			// XXX is this enough? or must groups be handled explicitly?
			if (SUPPORTED_COORDINATES_TYPES
					.contains(attribute.asProperty().getPropertyType().getName().getLocalPart())) {
				// a valid property was found
				return true;
			}
		}

		return false;
	}

	/**
	 * Write coordinates into a posList or coordinates property
	 * 
	 * @param writer the XML stream writer
	 * @param descendPattern the pattern to descend
	 * @param coordinates the coordinates to write
	 * @param elementType the type of the encompassing element
	 * @param elementName the encompassing element name
	 * @param gmlNs the GML namespace
	 * @param unique if the path's start element cannot be repeated
	 * @param decimalFormatter a decimal formatter to format geometry
	 *            coordinates
	 * @throws XMLStreamException if an error occurs writing the coordinates
	 */
	protected static void descendAndWriteCoordinates(XMLStreamWriter writer, Pattern descendPattern,
			Coordinate[] coordinates, TypeDefinition elementType, QName elementName, String gmlNs,
			boolean unique, DecimalFormat decimalFormatter) throws XMLStreamException {
		Descent descent = descend(writer, descendPattern, elementType, elementName, gmlNs, unique);

		// write geometry
		writeCoordinates(writer, coordinates, descent.getPath().getLastType(), gmlNs,
				decimalFormatter);

		descent.close();
	}

	/**
	 * Write coordinates into a pos, posList or coordinates property
	 * 
	 * @param writer the XML stream writer
	 * @param coordinates the coordinates to write
	 * @param elementType the type of the encompassing element
	 * @param gmlNs the GML namespace
	 * @param decimalFormatter a decimal formatter to format geometry
	 *            coordinates
	 * @throws XMLStreamException if an error occurs writing the coordinates
	 */
	protected static void writeCoordinates(XMLStreamWriter writer, Coordinate[] coordinates,
			TypeDefinition elementType, String gmlNs, DecimalFormat decimalFormatter)
			throws XMLStreamException {
		if (coordinates.length > 1) {
			if (writeList(writer, coordinates, elementType, gmlNs, decimalFormatter)) {
				return;
			}
		}

		if (writePos(writer, coordinates, elementType, gmlNs, null, decimalFormatter)) {
			return;
		}

		if (coordinates.length <= 1) {
			if (writeList(writer, coordinates, elementType, gmlNs, decimalFormatter)) {
				return;
			}
		}

		log.error("Unable to write coordinates to element of type " + //$NON-NLS-1$
				elementType.getDisplayName());
	}

	/**
	 * Write coordinates into a pos property
	 * 
	 * @param writer the XML stream writer
	 * @param coordinates the coordinates to write
	 * @param elementType the type of the encompassing element
	 * @param gmlNs the GML namespace
	 * @param posName the name of the desired DirectPositionType property, or
	 *            <code>null</code> if any
	 * @param decimalFormatter a decimal formatter to format geometry
	 *            coordinates or <code>null</code> to use
	 *            <code>Double.toString()</code>
	 * @return if writing the coordinates was successful
	 * @throws XMLStreamException if an error occurs writing the coordinates
	 */
	protected static boolean writePos(XMLStreamWriter writer, Coordinate[] coordinates,
			TypeDefinition elementType, String gmlNs, String posName,
			DecimalFormat decimalFormatter) throws XMLStreamException {
		PropertyDefinition posAttribute = null;

		// check for DirectPositionType
		// XXX is this enough? or must groups be handled?
		for (PropertyDefinition att : DefinitionUtil.getAllProperties(elementType)) {
			if (att.getPropertyType().getName().equals(new QName(gmlNs, "DirectPositionType"))) { //$NON-NLS-1$
				// found a property with DirectPositionType
				if (posName == null || posName.equals(att.getName().getLocalPart())) {
					posAttribute = att;
					break;
				}
			}
		}

		// TODO support for CoordType

		if (posAttribute != null) {
			// TODO possibly write repeated positions
			writer.writeStartElement(posAttribute.getName().getNamespaceURI(),
					posAttribute.getName().getLocalPart());

			// write coordinates separated by spaces
			if (coordinates.length > 0) {
				Coordinate coordinate = coordinates[0];

				writer.writeCharacters(
						DecimalFormatUtil.applyFormatter(coordinate.x, decimalFormatter));
				writer.writeCharacters(" "); //$NON-NLS-1$
				writer.writeCharacters(
						DecimalFormatUtil.applyFormatter(coordinate.y, decimalFormatter));
				if (!Double.isNaN(coordinate.z)) {
					writer.writeCharacters(" "); //$NON-NLS-1$
					writer.writeCharacters(
							DecimalFormatUtil.applyFormatter(coordinate.z, decimalFormatter));
				}
			}

			writer.writeEndElement();
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Write coordinates into a posList or coordinates property
	 * 
	 * @param writer the XML stream writer
	 * @param coordinates the coordinates to write
	 * @param elementType the type of the encompassing element
	 * @param gmlNs the GML namespace
	 * @param decimalFormatter a decimal formatter to format geometry
	 *            coordinates
	 * @return if writing the coordinates was successful
	 * @throws XMLStreamException if an error occurs writing the coordinates
	 */
	private static boolean writeList(XMLStreamWriter writer, Coordinate[] coordinates,
			TypeDefinition elementType, String gmlNs, DecimalFormat decimalFormatter)
			throws XMLStreamException {
		PropertyDefinition listAttribute = null;
		String delimiter = " "; //$NON-NLS-1$
		String setDelimiter = " "; //$NON-NLS-1$

		// check for DirectPositionListType
		for (PropertyDefinition att : DefinitionUtil.getAllProperties(elementType)) {
			// XXX is this enough? or must groups be handled explicitly?
			if (att.getPropertyType().getName()
					.equals(new QName(gmlNs, "DirectPositionListType"))) { //$NON-NLS-1$
				listAttribute = att;
				break;
			}
		}

		if (listAttribute == null) {
			// check for CoordinatesType
			for (PropertyDefinition att : DefinitionUtil.getAllProperties(elementType)) {
				if (att.getPropertyType().getName().equals(new QName(gmlNs, "CoordinatesType"))) { //$NON-NLS-1$
					listAttribute = att;
					delimiter = ","; //$NON-NLS-1$
					break;
				}
			}
		}

		if (listAttribute != null) {
			writer.writeStartElement(listAttribute.getName().getNamespaceURI(),
					listAttribute.getName().getLocalPart());

			boolean first = true;
			// write coordinates separated by spaces
			for (Coordinate coordinate : coordinates) {
				if (first) {
					first = false;
				}
				else {
					writer.writeCharacters(setDelimiter);
				}

				writer.writeCharacters(
						DecimalFormatUtil.applyFormatter(coordinate.x, decimalFormatter));
				writer.writeCharacters(delimiter);
				writer.writeCharacters(
						DecimalFormatUtil.applyFormatter(coordinate.y, decimalFormatter));
				if (!Double.isNaN(coordinate.z)) {
					writer.writeCharacters(delimiter);
					writer.writeCharacters(
							DecimalFormatUtil.applyFormatter(coordinate.z, decimalFormatter));
				}
			}

			writer.writeEndElement();
			return true;
		}
		else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean accepts(Geometry geometry) {
		return geometryType.isInstance(geometry) && checkValid((T) geometry);
	}

	/**
	 * Check if the given geometry is valid to be written by this writer.
	 * 
	 * @param geometry the geometry to check
	 * @return if the geometry is valid, the default implementation just returns
	 *         <code>true</code>
	 */
	protected boolean checkValid(T geometry) {
		// override me
		return true;
	}

}
