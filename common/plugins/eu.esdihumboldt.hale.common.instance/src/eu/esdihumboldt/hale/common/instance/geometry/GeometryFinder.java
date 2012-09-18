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

package eu.esdihumboldt.hale.common.instance.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.common.instance.helper.InstanceTraversalCallback;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;

/**
 * Class to find geometries
 * 
 * @author Kevin Mais
 */
public class GeometryFinder implements InstanceTraversalCallback {

	private List<GeometryProperty<?>> geometries = new ArrayList<GeometryProperty<?>>();

	private CRSDefinition defaultCrsDef = null;

	/**
	 * Constructor for GeometryFinder with the
	 * CoordinateReferenceSystemDefinition
	 * 
	 * @param crsDef The coordinate reference system definition
	 */
	public GeometryFinder(CRSDefinition crsDef) {
		defaultCrsDef = crsDef;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.helper.InstanceTraversalCallback#visit(eu.esdihumboldt.hale.common.instance.model.Instance,
	 *      javax.xml.namespace.QName)
	 */
	@Override
	public boolean visit(Instance instance, QName name) {
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.helper.InstanceTraversalCallback#visit(eu.esdihumboldt.hale.common.instance.model.Group,
	 *      javax.xml.namespace.QName)
	 */
	@Override
	public boolean visit(Group group, QName name) {
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.helper.InstanceTraversalCallback#visit(java.lang.Object,
	 *      javax.xml.namespace.QName)
	 */
	@Override
	public boolean visit(Object value, QName name) {

		if (value instanceof Collection<?>) {
			boolean found = false;
			// traverse all collection elements
			for (Object element : ((Collection<?>) value)) {
				found = found || !visit(element, name);
			}
			return !found;
		}

		if (value instanceof GeometryProperty<?>) {
			geometries.add(((GeometryProperty<?>) value));
			// stop traversion afterwards, as there will be only parts of the
			// geometry as children
			return false;
		}
		if (value instanceof Geometry) {
			geometries.add(new DefaultGeometryProperty<Geometry>(defaultCrsDef, (Geometry) value));
			// stop traversion afterwards, as there will be only parts of the
			// geometry as children
			return false;
		}

		return true;
	}

	/**
	 * @return the geometries
	 */
	public List<GeometryProperty<?>> getGeometries() {
		return geometries;
	}

}
