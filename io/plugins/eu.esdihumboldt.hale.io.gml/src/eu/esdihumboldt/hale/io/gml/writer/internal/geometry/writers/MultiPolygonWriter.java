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
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.Descent;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.GeometryWriter;

/**
 * Writes {@link MultiPolygon}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class MultiPolygonWriter extends AbstractGeometryWriter<MultiPolygon> {
	
	private final PolygonWriter polygonWriter = new PolygonWriter();

	/**
	 * Default constructor
	 */
	public MultiPolygonWriter() {
		super(MultiPolygon.class);
		
		// compatible types to serve as entry point
		addCompatibleType(new QName(Pattern.GML_NAMESPACE_PLACEHOLDER, 
				"MultiPolygonType")); //$NON-NLS-1$
		addCompatibleType(new QName(Pattern.GML_NAMESPACE_PLACEHOLDER, 
				"CompositeSurfaceType")); //$NON-NLS-1$
		
		// patterns for matching inside compatible types
		addBasePattern("**/polygonMember"); //$NON-NLS-1$
		addBasePattern("**/surfaceMember"); //$NON-NLS-1$
		
		// verification patterns (from PolygonWriter)
		addVerificationPattern("*/Polygon/exterior/LinearRing"); // both exterior //$NON-NLS-1$
		addVerificationPattern("*/Polygon/interior/LinearRing"); // and interior elements must be present for contained polygons //$NON-NLS-1$
	}

	/**
	 * @see GeometryWriter#write(XMLStreamWriter, Geometry, TypeDefinition, QName, String)
	 */
	@Override
	public void write(XMLStreamWriter writer, MultiPolygon geometry,
			TypeDefinition elementType, QName elementName, String gmlNs)
			throws XMLStreamException {
		for (int i = 0; i < geometry.getNumGeometries(); i++) {
			if (i > 0) {
				writer.writeStartElement(elementName.getNamespaceURI(), elementName.getLocalPart());
			}
			
			Descent descent = descend(writer, Pattern.parse("*/Polygon"),  //$NON-NLS-1$
					elementType, elementName, gmlNs, false);
			
			Polygon poly = (Polygon) geometry.getGeometryN(i);
			polygonWriter.write(
					writer, 
					poly, 
					descent.getPath().getLastType(), 
					descent.getPath().getLastElement().getName(), 
					gmlNs);
			
			descent.close();
			
			if (i < geometry.getNumGeometries() - 1) {
				writer.writeEndElement();
			}
		}
	}

}
