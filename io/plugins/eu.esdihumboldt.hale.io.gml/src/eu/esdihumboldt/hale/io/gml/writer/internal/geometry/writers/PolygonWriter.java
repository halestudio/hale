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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.GeometryWriter;

/**
 * {@link Polygon} writer 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class PolygonWriter extends AbstractGeometryWriter<Polygon> {

	/**
	 * Default constructor
	 */
	public PolygonWriter() {
		super(Polygon.class);
		
		// compatible types to serve as entry point
		addCompatibleType(new QName(null, "PolygonType")); //$NON-NLS-1$
		
		// patterns for matching inside compatible types
		addBasePattern("*"); // matches any compatible type element //$NON-NLS-1$
		
		// verification patterns
		addVerificationPattern("*/exterior/LinearRing"); // both exterior //$NON-NLS-1$
		addVerificationPattern("*/interior/LinearRing"); // and interior elements must be present //$NON-NLS-1$
	}

	/**
	 * @see GeometryWriter#write(XMLStreamWriter, Geometry, TypeDefinition, QName, String)
	 */
	@Override
	public void write(XMLStreamWriter writer, Polygon polygon,
			TypeDefinition elementType, QName elementName, String gmlNs)
			throws XMLStreamException {
		// write exterior ring
		LineString exterior = polygon.getExteriorRing();
		descendAndWriteCoordinates(writer, Pattern.parse("*/exterior/LinearRing"),  //$NON-NLS-1$
				exterior.getCoordinates(), elementType, elementName, gmlNs, false);
		
		// write interior rings
		for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
			LineString interior = polygon.getInteriorRingN(i);
			descendAndWriteCoordinates(writer, Pattern.parse("*/interior/LinearRing"),  //$NON-NLS-1$
					interior.getCoordinates(), elementType, elementName, gmlNs, false);
		}
	}

}
