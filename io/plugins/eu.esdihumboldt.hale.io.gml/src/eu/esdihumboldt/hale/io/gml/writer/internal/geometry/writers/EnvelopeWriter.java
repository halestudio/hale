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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Writes geometries as Envelope.
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.6
 */
public class EnvelopeWriter extends AbstractGeometryWriter<Geometry> {

	private static final ALogger log = ALoggerFactory.getLogger(EnvelopeWriter.class);

	/**
	 * Default constructor
	 */
	public EnvelopeWriter() {
		super(Geometry.class);

		// compatible types to serve as entry point
		addCompatibleType(new QName(Pattern.GML_NAMESPACE_PLACEHOLDER, "EnvelopeType")); //$NON-NLS-1$

		// patterns for matching inside compatible types
		addBasePattern("*"); // matches any compatible type element //$NON-NLS-1$

		// verification patterns
		// none yet - this one commented out as example
//		addVerificationPattern("*/exterior/LinearRing"); 
	}

	@Override
	public void write(XMLStreamWriter writer, Geometry geom, TypeDefinition elementType,
			QName elementName, String gmlNs) throws XMLStreamException {
		// write envelope
		Envelope envelope = geom.getEnvelopeInternal();
		if (!envelope.isNull()) {
			writePos(writer,
					new Coordinate[] { new Coordinate(envelope.getMinX(), envelope.getMinY()) },
					elementType, gmlNs, "lowerCorner");
			writePos(writer,
					new Coordinate[] { new Coordinate(envelope.getMaxX(), envelope.getMaxY()) },
					elementType, gmlNs, "upperCorner");
		}
		else {
			log.error("Could not write empty envelope.");
		}
	}

}
