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

package eu.esdihumboldt.hale.io.gml.writer.internal.geometry;

import java.text.DecimalFormat;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.locationtech.jts.geom.Geometry;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Geometry writer interface. A geometry holds information about compatibility
 * and encoding patterns for a certain geometry type.
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @param <T> the geometry type
 */
public interface GeometryWriter<T extends Geometry> {

	/**
	 * Get the geometry type represented by the writer
	 * 
	 * @return the geometry type
	 */
	public Class<T> getGeometryType();

	/**
	 * Get the compatible types' names for the geometry type that can be handled
	 * by this writer. The compatible types serve as entry points for the
	 * matching.
	 * 
	 * @return the type names, a <code>null</code> namespace in a name
	 *         references the GML namespace
	 */
	public Set<QName> getCompatibleTypes();

	/**
	 * Matches the type against the encoding patterns.
	 * 
	 * @param type the type definition
	 * @param basePath the definition path
	 * @param gmlNs the GML namespace
	 * 
	 * @return the new path if there is a match, <code>null</code> otherwise
	 */
	public DefinitionPath match(TypeDefinition type, DefinitionPath basePath, String gmlNs);

	/**
	 * Write a geometry.
	 * 
	 * At this point we can assume that the wrapping element matches one of the
	 * base patterns. The corresponding element name and its type definition are
	 * given.
	 * 
	 * @param writer the XML stream writer
	 * @param geometry the geometry to write
	 * @param elementType the last type definition in the matching path
	 * @param elementName the corresponding element name
	 * @param gmlNs the GML namespace
	 * @param decimalFormatter a decimal formatter to format geometry
	 *            coordinates
	 * @throws XMLStreamException if an error occurs writing the geometry
	 */
	public void write(XMLStreamWriter writer, T geometry, TypeDefinition elementType,
			QName elementName, String gmlNs, DecimalFormat decimalFormatter)
					throws XMLStreamException;

	/**
	 * Determines if a geometry is valid to be written with the writer.
	 * 
	 * @param geometry the geometry to test
	 * @return <code>true</code> if the
	 */
	public boolean accepts(Geometry geometry);

}
