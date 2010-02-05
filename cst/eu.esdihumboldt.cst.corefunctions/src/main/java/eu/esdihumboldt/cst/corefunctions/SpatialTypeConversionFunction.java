/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.cst.corefunctions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.geotools.feature.AttributeImpl;
import org.geotools.feature.PropertyImpl;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.PropertyDescriptor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.AbstractCstFunction;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;

/**
 * CstFunction for spatial type conversion.
 * 
 * 
 * @author Ulrich Schaeffler
 * @version $Id$
 */

public class SpatialTypeConversionFunction 
	extends AbstractCstFunction {

	public static final String FROM = "FROM";
	public static final String TO = "TO";
	private Class<? extends Geometry> from;
	private Class<? extends Geometry> to;
	private Property sourceProperty = null;
	private Property targetProperty = null;

	/**
	 * @see eu.esdihumboldt.cst.transformer.CstFunction#transform(org.opengis.feature.Feature,
	 *      org.opengis.feature.Feature)
	 */
	public Feature transform(Feature source, Feature target) {

		// FIXME: should be using sourceProperty/targetProperty
		Collection<org.opengis.feature.Property> c = new HashSet<org.opengis.feature.Property>();
		Geometry geom = null;
		try {
			geom = (Geometry)((SimpleFeature)source).getProperty(
					this.sourceProperty.getLocalname()).getValue();
		}
		catch (Exception ex) {}
		finally {
			if (geom == null) {
				geom = (Geometry) source.getDefaultGeometryProperty().getValue();
			}
		}
		PropertyDescriptor pd_target = null;
		try {
			pd_target = ((SimpleFeature)target).getProperty(
					this.targetProperty.getLocalname()).getDescriptor();
		}
		catch (Exception ex) {}
		finally {
			if (pd_target == null) {
				pd_target = target.getDefaultGeometryProperty().getDescriptor();
			}
		}
		
		if (pd_target == null || geom == null) {
			throw new NullPointerException("Incorrect parameters given.");
		}
		
		GeometryFactory geomFactory = new GeometryFactory();
		Object newGeometry = null;

		// do the conversion
		if (this.from.equals(LineString.class)
				&& this.to.equals(MultiPoint.class)) {
			Coordinate[] coords = geom.getCoordinates();
			newGeometry = geomFactory.createMultiPoint(coords);
			PropertyImpl p = new AttributeImpl(newGeometry,
					(AttributeDescriptor) pd_target, null);
			c.add(p);

		} else if (this.from.equals(Polygon.class)
				&& this.to.equals(MultiPoint.class)) {
			Coordinate[] coords = geom.getCoordinates();
			newGeometry = geomFactory.createMultiPoint(coords);
			PropertyImpl p = new AttributeImpl(newGeometry,
					(AttributeDescriptor) pd_target, null);
			c.add(p);
		} else if (this.from.equals(MultiPolygon.class)
				&& this.to.equals(MultiPoint.class)) {
			Coordinate[] coords = geom.getCoordinates();
			newGeometry = geomFactory.createMultiPoint(coords);
			PropertyImpl p = new AttributeImpl(newGeometry,
					(AttributeDescriptor) pd_target, null);
			c.add(p);
		} else if (this.from.equals(MultiLineString.class)
				&& this.to.equals(MultiPoint.class)) {
			Coordinate[] coords = geom.getCoordinates();
			newGeometry = geomFactory.createMultiPoint(coords);
			PropertyImpl p = new AttributeImpl(newGeometry,
					(AttributeDescriptor) pd_target, null);
			c.add(p);
		} else if (this.from.equals(Polygon.class)
				&& this.to.equals(MultiLineString.class)) {
			newGeometry = geom.getBoundary();
			PropertyImpl p = new AttributeImpl(newGeometry,
					(AttributeDescriptor) pd_target, null);
			c.add(p);
		} else if (this.from.equals(Polygon.class)
				&& this.to.equals(LineString.class)) {
			Coordinate[] coords = geom.getCoordinates();
			newGeometry = geomFactory.createLineString(coords);
			PropertyImpl p = new AttributeImpl(newGeometry,
					(AttributeDescriptor) pd_target, null);
			c.add(p);
		} else if (this.from.equals(MultiPolygon.class)
				&& this.to.equals(MultiLineString.class)) {
			newGeometry = geom.getBoundary();
			PropertyImpl p = new AttributeImpl(newGeometry,
					(AttributeDescriptor) pd_target, null);
			c.add(p);
		} else if (this.from.equals(MultiPolygon.class)
				&& this.to.equals(LineString.class)) {
			Coordinate[] coords = geom.getCoordinates();
			newGeometry = geomFactory.createLineString(coords);
			PropertyImpl p = new AttributeImpl(newGeometry,
					(AttributeDescriptor) pd_target, null);
			c.add(p);
		}

		else {
			throw new RuntimeException(
					"Spatial type not supported");
		}

		target.setValue(c);
		return target;
	}

	/**
	 * @see eu.esdihumboldt.cst.transformer.CstFunction#configure(eu.esdihumboldt.cst.align.ICell)
	 */
	@SuppressWarnings("unchecked")
	public boolean configure(ICell cell) {

		for (IParameter ip : cell.getEntity1().getTransformation()
				.getParameters()) {
			try {
				if (ip.getName().equals(SpatialTypeConversionFunction.FROM)) {
					this.from = (Class<? extends Geometry>) Class.forName(ip
							.getValue());
				} else if (ip.getName()
						.equals(SpatialTypeConversionFunction.TO)) {
					this.to = (Class<? extends Geometry>) Class.forName(ip
							.getValue());
				}
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		if (this.from == null || this.to == null) {
			throw new RuntimeException(
					"The from and to Geometry classes must be defined.");
		}
		this.sourceProperty = (Property) cell.getEntity1();
		this.targetProperty = (Property) cell.getEntity2();
		return true;
	}

	public Cell getParameters() {
		Cell parameterCell = new Cell();	
		Property entity1 = new Property(new About(""));
		
		// Setting of type condition for entity1
		List <String> entityTypes = new ArrayList <String>();
		entityTypes.add(com.vividsolutions.jts.geom.Geometry.class.getName());
		entityTypes.add(org.opengis.geometry.Geometry.class.getName());
		entity1.setTypeCondition(entityTypes);
		
		Property entity2 = new Property(new About(""));
		 
		// Setting of type condition for entity2
			// 	entity2 has same type conditions as entity1
		entity2.setTypeCondition(entityTypes);
		
		Transformation t = new Transformation();
		List<IParameter> params = new ArrayList<IParameter>(); 
			
		Parameter p_to   = 
			new Parameter(SpatialTypeConversionFunction.TO,
					com.vividsolutions.jts.geom.Geometry.class.getName());
		Parameter p_from = 
			new Parameter(SpatialTypeConversionFunction.FROM,
					com.vividsolutions.jts.geom.Geometry.class.getName());
		
		params.add(p_to);
		params.add(p_from);
		t.setParameters(params);
		entity1.setTransformation(t);
		parameterCell.setEntity1(entity1);
		parameterCell.setEntity2(entity2);
		return parameterCell;
	}
	
}
