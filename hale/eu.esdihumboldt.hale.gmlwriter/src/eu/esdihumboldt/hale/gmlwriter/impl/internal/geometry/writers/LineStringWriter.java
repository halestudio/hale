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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.geotools.feature.NameImpl;
import org.opengis.feature.type.Name;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

import eu.esdihumboldt.hale.gmlwriter.impl.internal.geometry.GeometryWriter;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Writer for {@link LineString}s 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class LineStringWriter extends AbstractGeometryWriter<LineString> {

	/**
	 * Default constructor
	 */
	public LineStringWriter() {
		super(LineString.class);
		
		// compatible types to serve as entry point
		addCompatibleType(new NameImpl("LineStringType")); //$NON-NLS-1$
		
		// patterns for matching inside compatible types
		addBasePattern("*"); //$NON-NLS-1$
	}

	/**
	 * @see GeometryWriter#write(XMLStreamWriter, Geometry, TypeDefinition, Name, String)
	 */
	@Override
	public void write(XMLStreamWriter writer, LineString geometry,
			TypeDefinition elementType, Name elementName, String gmlNs)
			throws XMLStreamException {
		writeCoordinates(writer, geometry.getCoordinates(), elementType, gmlNs);
	}

}
