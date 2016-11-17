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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.GeometryWriter;

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
		addCompatibleType(new QName("http://www.opengis.net/gml", "PolygonType")); //$NON-NLS-1$ //$NON-NLS-2$

		// patterns for matching inside compatible types
		addBasePattern("*"); // matches any compatible type element //$NON-NLS-1$

		// verification patterns
		addVerificationPattern("*/outerBoundaryIs/LinearRing"); // both exterior //$NON-NLS-1$
		addVerificationPattern("*/innerBoundaryIs/LinearRing"); // and interior elements must be present //$NON-NLS-1$
	}

	/**
	 * @see GeometryWriter#write(XMLStreamWriter, Geometry, TypeDefinition,
	 *      QName, String, DecimalFormat )
	 */
	@Override
	public void write(XMLStreamWriter writer, Polygon polygon, TypeDefinition elementType,
			QName elementName, String gmlNs, DecimalFormat decimalFormatter) throws XMLStreamException {
		// write exterior ring
		LineString exterior = polygon.getExteriorRing();
		descendAndWriteCoordinates(writer, Pattern.parse("*/outerBoundaryIs/LinearRing"), //$NON-NLS-1$
				exterior.getCoordinates(), elementType, elementName, gmlNs, false, decimalFormatter);

		// write interior rings
		for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
			LineString interior = polygon.getInteriorRingN(i);
			descendAndWriteCoordinates(writer, Pattern.parse("*/innerBoundaryIs/LinearRing"), //$NON-NLS-1$
					interior.getCoordinates(), elementType, elementName, gmlNs, false, decimalFormatter);
		}
	}

}
