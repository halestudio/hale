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

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.GeometryWriter;

/**
 * {@link Point} writer
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class PointWriter extends AbstractGeometryWriter<Point> {

	/**
	 * Default constructor
	 */
	public PointWriter() {
		super(Point.class);

		// compatible types to serve as entry point
		addCompatibleType(new QName(Pattern.GML_NAMESPACE_PLACEHOLDER, "PointType")); //$NON-NLS-1$

		// patterns for matching inside compatible types
		addBasePattern("*"); // matches any compatible type //$NON-NLS-1$
								// element
	}

	/**
	 * @see GeometryWriter#write(XMLStreamWriter, Geometry, TypeDefinition,
	 *      QName, String, DecimalFormat)
	 */
	@Override
	public void write(XMLStreamWriter writer, Point geometry, TypeDefinition elementType,
			QName elementName, String gmlNs, DecimalFormat decimalFormatter)
					throws XMLStreamException {
		writeCoordinates(writer, geometry.getCoordinates(), elementType, gmlNs, decimalFormatter);
	}

}
