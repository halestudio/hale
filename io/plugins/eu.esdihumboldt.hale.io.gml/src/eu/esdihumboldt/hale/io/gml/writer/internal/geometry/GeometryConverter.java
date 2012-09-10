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

package eu.esdihumboldt.hale.io.gml.writer.internal.geometry;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Converts a geometry to another kind of geometry
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 * @param <S> the source geometry type
 * @param <T> the target geometry type
 */
public interface GeometryConverter<S extends Geometry, T extends Geometry> {

	/**
	 * Get the target geometry type
	 * 
	 * @return the target geometry type
	 */
	public Class<T> getTargetType();

	/**
	 * Get the source geometry type
	 * 
	 * @return the source geometry type
	 */
	public Class<S> getSourceType();

	/**
	 * Convert the given geometry
	 * 
	 * @param geometry the source geometry
	 * 
	 * @return the converted geometry
	 */
	public T convert(S geometry);

	/**
	 * Determines if there is a loss of information when converting the given
	 * geometry
	 * 
	 * @param geometry the source geometry
	 * 
	 * @return if there would be a loss of information
	 */
	public boolean lossOnConversion(S geometry);

}
