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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.GeometryWriter;
import eu.esdihumboldt.util.geometry.CurveHelper;

/**
 * {@link MultiLineString} writer
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class CurveWriter extends AbstractGeometryWriter<MultiLineString> {

	private static final ALogger log = ALoggerFactory.getLogger(CurveWriter.class);

	/**
	 * Default constructor
	 */
	public CurveWriter() {
		super(MultiLineString.class);

		// compatible types to serve as entry point
		addCompatibleType(new QName(Pattern.GML_NAMESPACE_PLACEHOLDER, "CurveType")); //$NON-NLS-1$

		// patterns for matching inside compatible types
		addBasePattern("**/LineStringSegment"); //$NON-NLS-1$
	}

	/**
	 * @see GeometryWriter#write(XMLStreamWriter, Geometry, TypeDefinition,
	 *      QName, String)
	 */
	@Override
	public void write(XMLStreamWriter writer, MultiLineString geometry, TypeDefinition elementType,
			QName elementName, String gmlNs) throws XMLStreamException {
		// reorder segments
		geometry = CurveHelper.combineCurve(geometry, geometry.getFactory(), false);

		for (int i = 0; i < geometry.getNumGeometries(); i++) {
			if (i > 0) {
				writer.writeStartElement(elementName.getNamespaceURI(), elementName.getLocalPart());
			}

			LineString line = (LineString) geometry.getGeometryN(i);
			writeCoordinates(writer, line.getCoordinates(), elementType, gmlNs);

			if (i < geometry.getNumGeometries() - 1) {
				writer.writeEndElement();
			}
		}
	}

	@Override
	protected boolean checkValid(MultiLineString geometry) {
		// check if segments are connected
		boolean valid = CurveHelper.combineCurve(geometry, geometry.getFactory(), true) != null;
		if (!valid) {
			log.warn("Geometry cannot be encoded as curve, because it is not continuous.");
		}
		return valid;
	}
}
