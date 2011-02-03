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

package eu.esdihumboldt.hale.gmlwriter.impl.internal.geometry.writers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.geotools.feature.NameImpl;
import org.opengis.feature.type.Name;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.gmlwriter.impl.internal.GmlWriterUtil;
import eu.esdihumboldt.hale.gmlwriter.impl.internal.StreamGmlWriter;
import eu.esdihumboldt.hale.gmlwriter.impl.internal.geometry.DefinitionPath;
import eu.esdihumboldt.hale.gmlwriter.impl.internal.geometry.GeometryWriter;
import eu.esdihumboldt.hale.gmlwriter.impl.internal.geometry.PathElement;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Abstract geometry writer implementation
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 * @param <T> the geometry type
 */
public abstract class AbstractGeometryWriter<T extends Geometry> implements GeometryWriter<T> {
	
	private static final ALogger log = ALoggerFactory.getLogger(AbstractGeometryWriter.class);

	private final Class<T> geometryType;
	
	private final Set<Name> compatibleTypes = new HashSet<Name>();
	
	private final Set<Pattern> patterns = new HashSet<Pattern>();
	
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
	 * Add an encoding pattern.
	 * 
	 * @param pattern the pattern string
	 * @see Pattern#parse(String)
	 */
	public void addPattern(String pattern) {
		Pattern p = Pattern.parse(pattern);
		if (p.isValid()) {
			patterns.add(p);
		}
		else {
			log.warn("Ignoring invalid pattern: " + pattern);
		}
	}

	/**
	 * @see GeometryWriter#match(TypeDefinition, DefinitionPath, String)
	 */
	@Override
	public DefinitionPath match(TypeDefinition type, DefinitionPath basePath,
			String gmlNs) {
		// try to match each pattern
		for (Pattern pattern : patterns) {
			DefinitionPath path = pattern.match(type, basePath, gmlNs);
			if (path != null) {
				return path;
			}
		}
		
		return null;
	}
	
	/**
	 * Write coordinates into a posList or coordinates property
	 * 
	 * @param writer the XML stream writer 
	 * @param descendPattern the pattern to descend
	 * @param coordinates the coordinates to write
	 * @param elementType the type of the encompassing element
	 * @param gmlNs the GML namespace
	 * @throws XMLStreamException if an error occurs writing the coordinates
	 */
	protected static void descendAndwriteCoordinates(XMLStreamWriter writer, 
			Pattern descendPattern, Coordinate[] coordinates, 
			TypeDefinition elementType, String gmlNs) throws XMLStreamException {
		DefinitionPath path = descendPattern.match(elementType, new DefinitionPath(elementType), gmlNs);
		
		if (path.isEmpty()) {
			writeCoordinates(writer, coordinates, elementType, gmlNs);
			return;
		}
		
		Name name = GmlWriterUtil.getElementName(path.getLastType()); //XXX the element name used may be wrong, is this an issue?
		for (PathElement step : path.getSteps()) {
			// start elements
			name = step.getName();
			writer.writeStartElement(name.getNamespaceURI(), name.getLocalPart());
			// write eventual required ID
			StreamGmlWriter.writeRequiredID(writer, step.getType(), null, false);
		}
		
		// write geometry
		writeCoordinates(writer, coordinates, path.getLastType(), gmlNs);
		
		for (int i = 0; i < path.getSteps().size(); i++) {
			// end elements
			writer.writeEndElement();
		}
	}
	
	/**
	 * Write coordinates into a posList or coordinates property
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
		
		log.error("Unable to write coordinates to element of type " + 
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
		AttributeDefinition posAttribute = null;
		
		// check for DirectPositionListType
		for (AttributeDefinition att : elementType.getAttributes()) {
			if (att.getTypeName().equals(new NameImpl(gmlNs, "DirectPositionType"))) {
				posAttribute = att;
				break;
			}
		}
		
		if (posAttribute != null) {
			
			writer.writeStartElement(posAttribute.getNamespace(), posAttribute.getName());
			
			// write coordinates separated by spaces
			if (coordinates.length > 0) {
				Coordinate coordinate = coordinates[0];
				
				//XXX only supports 2D
				writer.writeCharacters(String.valueOf(coordinate.x));
				writer.writeCharacters(" ");
				writer.writeCharacters(String.valueOf(coordinate.y));
			}
			
			writer.writeEndElement();
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Write coordinates into a posList or (TODO) coordinates property
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
		AttributeDefinition listAttribute = null;
		
		// check for DirectPositionListType
		for (AttributeDefinition att : elementType.getAttributes()) {
			if (att.getTypeName().equals(new NameImpl(gmlNs, "DirectPositionListType"))) {
				listAttribute = att;
				break;
			}
		}
		
		if (listAttribute != null) {
			
			writer.writeStartElement(listAttribute.getNamespace(), listAttribute.getName());
			
			boolean first = true;
			// write coordinates separated by spaces
			for (Coordinate coordinate : coordinates) {
				if (first) {
					first = false;
				}
				else {
					writer.writeCharacters(" ");
				}
				
				//XXX only supports 2D
				writer.writeCharacters(String.valueOf(coordinate.x));
				writer.writeCharacters(" ");
				writer.writeCharacters(String.valueOf(coordinate.y));
			}
			
			writer.writeEndElement();
			return true;
		}
		else {
			return false;
		}
	}

}
