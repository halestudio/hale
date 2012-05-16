/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
	 * @param crsDef
	 *            The coordinate reference system definition
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
			geometries.add(new DefaultGeometryProperty<Geometry>(defaultCrsDef,
					(Geometry) value));
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
