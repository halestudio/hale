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

package eu.esdihumboldt.hale.io.gml.writer.impl.internal.geometry.writers;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.geotools.feature.NameImpl;
import org.opengis.feature.type.Name;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.hale.io.gml.writer.impl.internal.geometry.GeometryWriter;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * {@link Polygon} writer for GML 2 type polygons
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class LegacyPolygonWriter extends AbstractGeometryWriter<Polygon> {

	/**
	 * Default constructor
	 */
	public LegacyPolygonWriter() {
		super(Polygon.class);
		
		// compatible types to serve as entry point
		addCompatibleType(new NameImpl("http://www.opengis.net/gml", "PolygonType")); //$NON-NLS-1$ //$NON-NLS-2$
		
		// patterns for matching inside compatible types
		addBasePattern("*"); // matches any compatible type element //$NON-NLS-1$
		
		// verification patterns
		addVerificationPattern("*/outerBoundaryIs/LinearRing"); // both exterior //$NON-NLS-1$
		addVerificationPattern("*/innerBoundaryIs/LinearRing"); // and interior elements must be present //$NON-NLS-1$
	}

	/**
	 * @see GeometryWriter#write(XMLStreamWriter, Geometry, TypeDefinition, Name, String)
	 */
	@Override
	public void write(XMLStreamWriter writer, Polygon polygon,
			TypeDefinition elementType, Name elementName, String gmlNs)
			throws XMLStreamException {
		// write exterior ring
		LineString exterior = polygon.getExteriorRing();
		descendAndWriteCoordinates(writer, Pattern.parse("*/outerBoundaryIs/LinearRing"),  //$NON-NLS-1$
				exterior.getCoordinates(), elementType, elementName, gmlNs);
		
		// write interior rings
		for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
			LineString interior = polygon.getInteriorRingN(i);
			descendAndWriteCoordinates(writer, Pattern.parse("*/innerBoundaryIs/LinearRing"),  //$NON-NLS-1$
					interior.getCoordinates(), elementType, elementName, gmlNs);
		}
	}

}
