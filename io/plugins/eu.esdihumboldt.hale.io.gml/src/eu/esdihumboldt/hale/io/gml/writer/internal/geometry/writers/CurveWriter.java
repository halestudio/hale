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
import com.vividsolutions.jts.geom.MultiLineString;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.GeometryWriter;

/**
 * {@link MultiLineString} writer
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class CurveWriter extends
		AbstractGeometryWriter<MultiLineString> {

	/**
	 * Default constructor
	 */
	public CurveWriter() {
		super(MultiLineString.class);
		
		// compatible types to serve as entry point
		addCompatibleType(new QName(null, "CurveType")); //$NON-NLS-1$
		
		// patterns for matching inside compatible types
		addBasePattern("**/LineStringSegment"); //$NON-NLS-1$
	}

	/**
	 * @see GeometryWriter#write(XMLStreamWriter, Geometry, TypeDefinition, QName, String)
	 */
	@Override
	public void write(XMLStreamWriter writer, MultiLineString geometry,
			TypeDefinition elementType, QName elementName, String gmlNs) 
			throws XMLStreamException {
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

}
