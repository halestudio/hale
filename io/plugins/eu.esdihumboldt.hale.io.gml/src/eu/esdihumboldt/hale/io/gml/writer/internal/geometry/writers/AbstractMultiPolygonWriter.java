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
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.Descent;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.GeometryWriter;

/**
 * Base class for writing {@link MultiPolygon}s, compatible types need to be
 * added.
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractMultiPolygonWriter extends AbstractGeometryWriter<MultiPolygon> {

	private final PolygonWriter polygonWriter = new PolygonWriter();

	/**
	 * Default constructor
	 */
	public AbstractMultiPolygonWriter() {
		super(MultiPolygon.class);

		// patterns for matching inside compatible types
		addBasePattern("**/polygonMember"); //$NON-NLS-1$
		addBasePattern("**/surfaceMember"); //$NON-NLS-1$

		// verification patterns (from PolygonWriter)
		addVerificationPattern("*/Polygon/exterior/LinearRing"); // both //$NON-NLS-1$
																	// exterior
		addVerificationPattern("*/Polygon/interior/LinearRing"); // and //$NON-NLS-1$
																	// interior
																	// elements
																	// must be
																	// present
																	// for
																	// contained
																	// polygons
	}

	/**
	 * @see GeometryWriter#write(XMLStreamWriter, Geometry, TypeDefinition,
	 *      QName, String, DecimalFormat)
	 */
	@Override
	public void write(XMLStreamWriter writer, MultiPolygon geometry, TypeDefinition elementType,
			QName elementName, String gmlNs, DecimalFormat decimalFormatter)
					throws XMLStreamException {
		for (int i = 0; i < geometry.getNumGeometries(); i++) {
			if (i > 0) {
				writer.writeStartElement(elementName.getNamespaceURI(), elementName.getLocalPart());
			}

			Descent descent = descend(writer, Pattern.parse("*/Polygon"), //$NON-NLS-1$
					elementType, elementName, gmlNs, false);

			Polygon poly = (Polygon) geometry.getGeometryN(i);
			polygonWriter.write(writer, poly, descent.getPath().getLastType(),
					descent.getPath().getLastElement().getName(), gmlNs, decimalFormatter);

			descent.close();

			if (i < geometry.getNumGeometries() - 1) {
				writer.writeEndElement();
			}
		}
	}

}
