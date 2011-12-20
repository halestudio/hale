/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.io.gml.writer.internal.geometry.writers;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.opengis.feature.type.Name;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.Descent;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.GeometryWriter;

/**
 * Abstract geometry writer implementation
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 * @param <T> the geometry type
 */
public abstract class AbstractGeometryWriter<T extends Geometry> 
		extends AbstractPathMatcher implements GeometryWriter<T> {
	
	private static final ALogger log = ALoggerFactory.getLogger(AbstractGeometryWriter.class);

	private final Class<T> geometryType;
	
	private final Set<Name> compatibleTypes = new HashSet<Name>();
	
	/**
	 * The attribute type names supported for writing coordinates with
	 * {@link #writeCoordinates(XMLStreamWriter, Coordinate[], TypeDefinition, String)} or
	 * {@link #descendAndWriteCoordinates(XMLStreamWriter, Pattern, Coordinate[], TypeDefinition, Name, String, boolean)}.
	 * 
	 * Use for validating end-points.
	 */
	private final static Set<String> SUPPORTED_COORDINATES_TYPES = Collections.unmodifiableSet(
			new HashSet<String>(Arrays.asList("DirectPositionType",  //$NON-NLS-1$
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
	public Set<Name> getCompatibleTypes() {
		return Collections.unmodifiableSet(compatibleTypes);
	}
	
	/**
	 * Add a compatible type. A <code>null</code> namespace references the GML
	 * namespace.
	 * 
	 * @param typeName the type name
	 */
	public void addCompatibleType(Name typeName) {
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
	 * verification pattern this method is called with the {@link TypeDefinition}
	 * of the end-point to assure the needed structure is present (e.g. a
	 * DirectPositionListType element). If no verification pattern is present
	 * the end-point of the matched base pattern will be verified.
	 * The default implementation checks for properties with any of the types
	 * supported for writing coordinates.
	 * @see #SUPPORTED_COORDINATES_TYPES
	 * 
	 * @param endPoint the end-point type definition 
	 *  
	 * @return if the end-point is valid for writing the geometry
	 */
	@Override
	protected boolean verifyEndPoint(TypeDefinition endPoint) {
		//FIXME
//		for (AttributeDefinition attribute : endPoint.getAttributes()) {
//			if (SUPPORTED_COORDINATES_TYPES.contains(attribute.getTypeName().getLocalPart())) {
//				// a valid property was found
//				return true;
//			}
//		}
		
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
	 * @throws XMLStreamException if an error occurs writing the coordinates
	 */
	protected static void descendAndWriteCoordinates(XMLStreamWriter writer, 
			Pattern descendPattern, Coordinate[] coordinates, 
			TypeDefinition elementType, QName elementName, String gmlNs, boolean unique) throws XMLStreamException {
		Descent descent = descend(writer, descendPattern, elementType, 
				elementName, gmlNs, unique);
		
		// write geometry
		writeCoordinates(writer, coordinates, descent.getPath().getLastType(), gmlNs);
		
		descent.close();
	}
	
	/**
	 * Write coordinates into a pos, posList or coordinates property
	 * 
	 * @param writer the XML stream writer 
	 * @param coordinates the coordinates to write
	 * @param elementType the type of the encompassing element
	 * @param gmlNs the GML namespace
	 * @throws XMLStreamException if an error occurs writing the coordinates
	 */
	protected static void writeCoordinates(XMLStreamWriter writer, 
			Coordinate[] coordinates, TypeDefinition elementType, 
			String gmlNs) throws XMLStreamException {
		if (coordinates.length > 1) {
			if (writeList(writer, coordinates, elementType, gmlNs)) {
				return;
			}
		}
		
		if (writePos(writer, coordinates, elementType, gmlNs)) {
			return;
		}
		
		if (coordinates.length <= 1) {
			if (writeList(writer, coordinates, elementType, gmlNs)) {
				return;
			}
		}
		
		log.error("Unable to write coordinates to element of type " +  //$NON-NLS-1$
				elementType.getDisplayName());
	}
	
	/**
	 * Write coordinates into a pos property
	 * 
	 * @param writer the XML stream writer 
	 * @param coordinates the coordinates to write
	 * @param elementType the type of the encompassing element
	 * @param gmlNs the GML namespace
	 * @return if writing the coordinates was successful
	 * @throws XMLStreamException if an error occurs writing the coordinates
	 */
	private static boolean writePos(XMLStreamWriter writer,
			Coordinate[] coordinates, TypeDefinition elementType, String gmlNs) throws XMLStreamException {
		//FIXME
//		AttributeDefinition posAttribute = null;
//		
//		// check for DirectPositionType
//		for (AttributeDefinition att : elementType.getAttributes()) {
//			if (att.getTypeName().equals(new NameImpl(gmlNs, "DirectPositionType"))) { //$NON-NLS-1$
//				posAttribute = att;
//				break;
//			}
//		}
//		
//		//TODO support for CoordType
//		
//		if (posAttribute != null) {
//			//TODO possibly write repeated positions
//			writer.writeStartElement(posAttribute.getNamespace(), posAttribute.getName());
//			
//			// write coordinates separated by spaces
//			if (coordinates.length > 0) {
//				Coordinate coordinate = coordinates[0];
//				
//				writer.writeCharacters(String.valueOf(coordinate.x));
//				writer.writeCharacters(" "); //$NON-NLS-1$
//				writer.writeCharacters(String.valueOf(coordinate.y));
//				if (!Double.isNaN(coordinate.z)) {
//					writer.writeCharacters(" "); //$NON-NLS-1$
//					writer.writeCharacters(String.valueOf(coordinate.z));
//				}
//			}
//			
//			writer.writeEndElement();
//			return true;
//		}
//		else {
			return false;
//		}
	}

	/**
	 * Write coordinates into a posList or coordinates property
	 * 
	 * @param writer the XML stream writer 
	 * @param coordinates the coordinates to write
	 * @param elementType the type of the encompassing element
	 * @param gmlNs the GML namespace
	 * @return if writing the coordinates was successful
	 * @throws XMLStreamException if an error occurs writing the coordinates
	 */
	private static boolean writeList(XMLStreamWriter writer,
			Coordinate[] coordinates, TypeDefinition elementType, String gmlNs) throws XMLStreamException {
		//FIXME
//		AttributeDefinition listAttribute = null;
//		String delimiter = " "; //$NON-NLS-1$
//		String setDelimiter = " "; //$NON-NLS-1$
//		
//		// check for DirectPositionListType
//		for (AttributeDefinition att : elementType.getAttributes()) {
//			if (att.getTypeName().equals(new NameImpl(gmlNs, "DirectPositionListType"))) { //$NON-NLS-1$
//				listAttribute = att;
//				break;
//			}
//		}
//		
//		if (listAttribute == null) {
//			// check for CoordinatesType
//			for (AttributeDefinition att : elementType.getAttributes()) {
//				if (att.getTypeName().equals(new NameImpl(gmlNs, "CoordinatesType"))) { //$NON-NLS-1$
//					listAttribute = att;
//					delimiter = ","; //$NON-NLS-1$
//					break;
//				}
//			}
//		}
//		
//		if (listAttribute != null) {
//			
//			writer.writeStartElement(listAttribute.getNamespace(), listAttribute.getName());
//			
//			boolean first = true;
//			// write coordinates separated by spaces
//			for (Coordinate coordinate : coordinates) {
//				if (first) {
//					first = false;
//				}
//				else {
//					writer.writeCharacters(setDelimiter);
//				}
//				
//				writer.writeCharacters(String.valueOf(coordinate.x));
//				writer.writeCharacters(delimiter);
//				writer.writeCharacters(String.valueOf(coordinate.y));
//				if (!Double.isNaN(coordinate.z)) {
//					writer.writeCharacters(delimiter);
//					writer.writeCharacters(String.valueOf(coordinate.z));
//				}
//			}
//			
//			writer.writeEndElement();
//			return true;
//		}
//		else {
			return false;
//		}
	}

}
