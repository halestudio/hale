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

package eu.esdihumboldt.hale.io.gml.geometry.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.ElementType;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;
import eu.esdihumboldt.hale.io.gml.geometry.AbstractGeometryHandler;
import eu.esdihumboldt.hale.io.gml.geometry.FixedConstraintsGeometryHandler;
import eu.esdihumboldt.hale.io.gml.geometry.Geometries;
import eu.esdihumboldt.hale.io.gml.geometry.GeometryHandler;
import eu.esdihumboldt.hale.io.gml.geometry.GeometryNotSupportedException;

/**
 * Generic geometry handler for AbstractGeometryType.
 * @author Simon Templer
 */
public class GenericGeometryHandler extends FixedConstraintsGeometryHandler {
	
	private static final String ABSTRACT_GEOMETRY_TYPE = "AbstractGeometryType";
	
	/**
	 * @see AbstractGeometryHandler#initSupportedTypes()
	 */
	@Override
	protected Set<? extends QName> initSupportedTypes() {
		Set<QName> types = new HashSet<QName>();
		
		types.add(new QName(NS_GML, ABSTRACT_GEOMETRY_TYPE));
		types.add(new QName(NS_GML_32, ABSTRACT_GEOMETRY_TYPE));
		
		return types;
	}

	/**
	 * @see FixedConstraintsGeometryHandler#initConstraints()
	 */
	@Override
	protected Collection<? extends TypeConstraint> initConstraints() {
		Collection<TypeConstraint> constraints = new ArrayList<TypeConstraint>(3);
		
		// binding is collection, as we can't be sure that all contained geometries share the same CRS
		constraints.add(Binding.get(Collection.class));
		// set element type binding to GeometryProperty
		constraints.add(ElementType.get(GeometryProperty.class));
		// geometry binding is Geometry, as we can't narrow it down further
		constraints.add(GeometryType.get(Geometry.class));
		
		return constraints;
	}

	/**
	 * @see GeometryHandler#createGeometry(Instance)
	 */
	@Override
	public Object createGeometry(Instance instance)
			throws GeometryNotSupportedException {
		Geometries geoms = Geometries.getInstance();
		//TODO use geometry handlers to read geometries of sub-types
		//TODO extract this to abstract geometry handler as default?
		
		// TODO Auto-generated method stub
		return null;
	}

}
