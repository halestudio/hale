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

package eu.esdihumboldt.hale.io.gml.writer.internal.geometry.converters;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.GeometryConverter;

/**
 * Abstract geometry converter implementation providing access to a
 * {@link GeometryFactory}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 * @param <S> the source geometry type
 * @param <T> the target geometry type
 */
public abstract class AbstractGeometryConverter<S extends Geometry, T extends Geometry> implements
		GeometryConverter<S, T> {

	/**
	 * The geometry factory
	 */
	protected static final GeometryFactory geomFactory = new GeometryFactory();

	private final Class<S> sourceType;

	private final Class<T> targetType;

	/**
	 * Constructor
	 * 
	 * @param sourceType the source geometry type
	 * @param targetType the target geometry type
	 */
	public AbstractGeometryConverter(Class<S> sourceType, Class<T> targetType) {
		super();
		this.sourceType = sourceType;
		this.targetType = targetType;
	}

	/**
	 * @see GeometryConverter#getSourceType()
	 */
	@Override
	public Class<S> getSourceType() {
		return sourceType;
	}

	/**
	 * @see GeometryConverter#getTargetType()
	 */
	@Override
	public Class<T> getTargetType() {
		return targetType;
	}

}
