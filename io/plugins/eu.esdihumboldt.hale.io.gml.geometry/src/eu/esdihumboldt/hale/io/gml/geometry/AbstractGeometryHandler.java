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

package eu.esdihumboldt.hale.io.gml.geometry;

import java.util.Collections;
import java.util.Set;

import javax.xml.namespace.QName;

import org.locationtech.jts.geom.GeometryFactory;

/**
 * Base class for geometry handlers.
 * 
 * @author Simon Templer
 */
public abstract class AbstractGeometryHandler implements GeometryHandler, GMLConstants {

	private Set<QName> supportedTypes;

	private static final GeometryFactory factory = new GeometryFactory();

	/**
	 * @see GeometryHandler#getSupportedTypes()
	 */
	@Override
	public Set<QName> getSupportedTypes() {
		if (supportedTypes == null) {
			supportedTypes = Collections.unmodifiableSet(initSupportedTypes());
		}
		return supportedTypes;
	}

	/**
	 * Create the set of supported types.
	 * 
	 * @return the set of supported type names
	 */
	protected abstract Set<? extends QName> initSupportedTypes();

	/**
	 * Get a geometry factory instance.
	 * 
	 * @return the geometry factory
	 */
	protected GeometryFactory getGeometryFactory() {
		// XXX instead retrieve from a service?
		return factory;
	}

}
