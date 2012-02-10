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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.xml.namespace.QName;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;

/**
 * TODO Type description
 * @author Simon Templer
 */
public class GeometryUtil {
	
	/**
	 * Get all geometries of an instance.
	 * @param instance the instance
	 * @return the geometries or an empty collection if there are none
	 */
	public static Collection<GeometryProperty<?>> getAllGeometries(Instance instance) {
		return getGeometries(instance, new ArrayList<QName>());
	}
	
	/**
	 * Get the default geometry of an instance.
	 * @param instance the instance
	 * @param path the property path to start the search at, a <code>null</code>
	 *   will yield no geometries
	 * @return the default geometries or an empty collection if there is none
	 */
	public static Collection<GeometryProperty<?>> getGeometries(Instance instance, 
			List<QName> path) {
		Collection<GeometryProperty<?>> geometries = new ArrayList<GeometryProperty<?>>();
		if (path == null) {
			return geometries;
		}
		
		// descend path and return the geometries found
		Queue<Group> parents = new LinkedList<Group>();
		parents.add(instance);
		
		for (int i = 0; i < path.size(); i++) {
			QName name = path.get(i);
			
			Queue<Group> children = new LinkedList<Group>();
			for (Group parent : parents) {
				Object[] values = parent.getProperty(name);
				if (values != null) {
					for (Object value : values) {
						if (value instanceof Group) {
							children.add((Group) value);
						}
						
						if (value instanceof Instance) {
							value = ((Instance) value).getValue();
						}
						
						if (value != null && !(value instanceof Group) 
								&& i == path.size() - 1) {
							// detect geometry values at end of path
							// as they are not searched later on
							Collection<GeometryProperty<?>> geoms = getGeometryProperties(value);
							geometries.addAll(geoms);
						}
					}
				}
			}
			
			// prepare for next step
			parents = children;
		}
		
		// early exit #1
		if (!geometries.isEmpty()) {
			// if there already are geometries, return them and don't search any further
			//XXX is this OK in all cases?
			return geometries;
		}
		
		// now we have groups/instances at the end of the path collected in parents
		// search in those groups/instances for additional geometries
		while (!parents.isEmpty()) {
			Group parent = parents.poll();
			
			// add values contained in the instance
			Collection<GeometryProperty<?>> geoms = getGeometryProperties(parent);
			if (!geoms.isEmpty()) {
				geometries.addAll(geoms);
				// early exit #2
				// don't check the children
				//XXX is this OK in all cases?
			}
			else {
				// check children for geometries
				for (QName name : parent.getPropertyNames()) {
					Object[] values = parent.getProperty(name);
					if (values != null) {
						for (Object value : values) {
							if (value instanceof Group) {
								// check group later on
								parents.add((Group) value);
							}
							else {
								// add geometries for value
								geometries.addAll(getGeometryProperties(value));
							}
						}
					}
				}
			}
		}
		
		return geometries;
	}

	/**
	 * Try to get/create geometry properties from a property value.
	 * @param value the property value, e.g. a {@link Geometry}, 
	 *   {@link GeometryProperty}, a {@link Collection} or {@link Instance}
	 * @return the geometry properties or an empty list if none could be 
	 *   created
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Collection<GeometryProperty<?>> getGeometryProperties(Object value) {
		if (value instanceof Instance) {
			value = ((Instance) value).getValue();
		}
		
		if (value != null) {
			Collection<GeometryProperty<?>> result = new ArrayList<GeometryProperty<?>>();
			if (value instanceof GeometryProperty) {
				// return the encountered GeometryProperty
				result.add((GeometryProperty) value);
			}
			if (value instanceof Geometry) {
				// create a GeometryProperty wrapping the geometry
				//XXX any way to determine a CRS?
				GeometryProperty prop = new DefaultGeometryProperty(
						null, (Geometry) value);
				result.add(prop);
			}
			if (value instanceof Collection<?>) {
				// add results from collection values
				for (Object subValue : ((Iterable<?>) value)) {
					result.addAll(getGeometryProperties(subValue));
				}
			}
			return result;
		}
		
		return Collections.emptyList();
	}

}
