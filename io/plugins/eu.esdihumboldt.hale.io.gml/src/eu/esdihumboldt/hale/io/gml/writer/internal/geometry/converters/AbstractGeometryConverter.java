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

package eu.esdihumboldt.hale.io.gml.writer.internal.geometry.converters;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

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
