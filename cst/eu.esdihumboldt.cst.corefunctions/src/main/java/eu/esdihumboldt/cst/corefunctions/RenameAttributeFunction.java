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

import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.geotools.feature.FeatureImpl;
import org.opengis.feature.Feature;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.cst.AbstractCstFunction;
import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.Resource;

/**
 * CstFunction for attribute renaming, i.e. the copying of attributes of the 
 * same type from a source to a target property.
 * 
 * @author Thorsten Reitz, Jan Jezek
 * @version $Id: RenameAttributeFunction.java 2418 2009-12-22 11:35:12Z jjezek $ 
 */
public class RenameAttributeFunction 
	extends AbstractCstFunction {

	/**
	 * Constant for the nestedAttributePath parameter that this function uses.
	 */
	public static final String NESTED_ATTRIBUTE_PATH = "nestedAttributePath";
	
	private String oldName;
	private String newName;
	private String nestedAttributePath = null;

	/**
	 * This transform implementation can copy any literal attribute value 
	 * (Strings, Numbers).
	 */
	public Feature transform(Feature source, Feature target) {
		
		Class<?> bindingSource = source.getProperty(this.oldName).getDescriptor()
									.getType().getBinding();
		
		Class<?> bindingTarget = String.class;
		
		org.opengis.feature.Property targetProperty = target.getProperty(this.newName);
		
		if (nestedAttributePath != null) {
			String[] nestedAttributeNames = this.nestedAttributePath.split("::"); 
			// check whether nested attributes have already been created. if not, create.
			Feature nestedFeature = target;
			for (String attributeName : nestedAttributeNames) {
				if (nestedFeature instanceof FeatureImpl) {
					if (nestedFeature.getProperty(attributeName) != null) {
						// nested Feature was already created.
						org.opengis.feature.Property nestedProperty = 
							nestedFeature.getProperty(attributeName);
						if (nestedProperty.getValue() != null 
								&& nestedProperty.getType().getBinding().equals(Collection.class)) {
							nestedFeature = (Feature) ((Collection<?>) 
									nestedProperty.getValue()).iterator().next();
						}
						else {
							break;
						}
					}
				}
				else {
					// break if it's not a complex property (i.e. a Feature), since we can't go down anymore
					break;
				}
				
			}
			targetProperty = nestedFeature.getProperty(nestedAttributeNames[nestedAttributeNames.length - 1]);
			bindingTarget = targetProperty.getDescriptor()
										.getType().getBinding();
		}
		else {
			bindingTarget = target.getProperty(this.newName).getDescriptor()
										.getType().getBinding();
		}
		
		// only do a direct copy if the two Properties have equal bindings.
		if (bindingSource.equals(bindingTarget)) {
			targetProperty.setValue(
					source.getProperty(this.oldName).getValue());
		}
		else if (Geometry.class.isAssignableFrom(bindingSource) 
				&& Geometry.class.isAssignableFrom(bindingTarget)) {
			Object value = this.convertSpatialType(
					bindingSource, bindingTarget, 
					(Geometry) source.getProperty(this.oldName).getValue());
			targetProperty.setValue(value);
		}
		else if (bindingSource.equals(Integer.class) 
				&& bindingTarget.equals(Integer.class)) {
			Integer value = Integer.parseInt(source.getProperty(
					this.oldName).getValue().toString());
			targetProperty.setValue(value);
		}
		else if (bindingSource.equals(String.class) 
				&& bindingTarget.equals(Long.class)) {
			Long value = Long.parseLong(source.getProperty(
					this.oldName).getValue().toString());
			targetProperty.setValue(value);
		}
		else if (bindingSource.equals(String.class) 
				&& bindingTarget.equals(Float.class)) {
			Float value = Float.parseFloat(source.getProperty(
					this.oldName).getValue().toString());
			targetProperty.setValue(value);
		}
		else if (bindingSource.equals(String.class) 
				&& bindingTarget.equals(Double.class)) {
			Double value = Double.parseDouble(source.getProperty(
					this.oldName).getValue().toString());
			targetProperty.setValue(value);
		}
		else if (bindingTarget.equals(String.class) && 
				(bindingSource.equals(Float.class) 
						|| bindingSource.equals(Double.class) 
						|| bindingSource.equals(Integer.class) 
						|| bindingSource.equals(Long.class)
						|| bindingSource.equals(BigInteger.class))) {
			targetProperty.setValue(
					source.getProperty(this.oldName).getValue());
		}
		else {
			throw new UnsupportedOperationException("For the given source (" 
					+ bindingSource.getName() +
					") and target (" + bindingTarget.getName() 
					+ ") attribute bindings, this rename function " +
					"cannot be used.");
		}
		
		return target;
	}

	public boolean configure(ICell cell) {
		this.oldName = ((Property)cell.getEntity1()).getLocalname();
		this.newName = ((Property)cell.getEntity2()).getLocalname();
		for (IParameter p : cell.getEntity1().getTransformation().getParameters()) {
			if (p.getName().equals(NESTED_ATTRIBUTE_PATH)) {
				if (p.getValue() != null && !p.getValue().equals("")) {
					this.nestedAttributePath = p.getValue();
				}
			}
		}
		return true;
	}
	
	public Cell getParameters() {
		Cell parameterCell = new Cell();
		
		Property entity1 = new Property(new About(""));
		// Setting of type condition for entity1
		List <String> entityTypes = new ArrayList <String>();
		entityTypes.add(com.vividsolutions.jts.geom.Geometry.class.getName());
		entityTypes.add(org.opengis.geometry.Geometry.class.getName());
		entityTypes.add(String.class.getName());
		entityTypes.add(Number.class.getName());
		entityTypes.add(Boolean.class.getName());
		entityTypes.add(Date.class.getName());
		entityTypes.add(Collection.class.getName());
		entity1.setTypeCondition(entityTypes);

		Property entity2 = new Property(new About(""));
		// Setting of type condition for entity2
			// 	entity2 has same type conditions as entity1
		entity2.setTypeCondition(entityTypes);
		
		Transformation t =  new Transformation(
				new Resource(getClass().getName()));		
		
		entity1.setTransformation(t);		
		
		parameterCell.setEntity1(entity1);
		parameterCell.setEntity2(entity2);
		return parameterCell;
	}
	
	private Geometry convertSpatialType(Class<?> bindingSource, Class<?> bindingTarget, Geometry geom) {
		GeometryFactory geomFactory = new GeometryFactory();
		Geometry newGeometry = null;
		if (bindingSource.equals(Point.class)
				&& bindingTarget.equals(MultiPoint.class)) { // Point -> MultiPoint
			newGeometry = geomFactory.createMultiPoint(geom.getCoordinates());
		} else if (bindingSource.equals(LineString.class)
				&& bindingTarget.equals(MultiPoint.class)) { // LineString -> Multipoint
			newGeometry = geomFactory.createMultiPoint(geom.getCoordinates());
		} else if (bindingSource.equals(Polygon.class)
				&& bindingTarget.equals(MultiPoint.class)) { // Polygon -> Multipoint
			newGeometry = geomFactory.createMultiPoint(geom.getCoordinates());
		} else if (bindingSource.equals(MultiPolygon.class)
				&& bindingTarget.equals(MultiPoint.class)) { // MultiPolygon -> Multipoint
			newGeometry = geomFactory.createMultiPoint(geom.getCoordinates());
		} else if (bindingSource.equals(MultiLineString.class)
				&& bindingTarget.equals(MultiPoint.class)) { // MultiLineString -> Multipoint
			newGeometry = geomFactory.createMultiPoint(geom.getCoordinates());
		} else if (bindingSource.equals(Polygon.class)
				&& bindingTarget.equals(MultiLineString.class)) { // Polygon -> MultiLineString
			newGeometry = geom.getBoundary();
		} else if (bindingSource.equals(Polygon.class)
				&& bindingTarget.equals(LineString.class)) { // Polygon -> LineString
			newGeometry = geomFactory.createLineString(geom.getCoordinates());
		} else if (bindingSource.equals(MultiPolygon.class)
				&& bindingTarget.equals(MultiLineString.class)) { // MultiPolygon -> MultiLineString
			newGeometry = geom.getBoundary();
		} else if (bindingSource.equals(MultiPolygon.class)
				&& bindingTarget.equals(LineString.class)) { // MultiPolygon -> LineString
			newGeometry = geomFactory.createLineString(geom.getCoordinates());
		}
		else {
			throw new RuntimeException(
					"Spatial type conversion from " + bindingSource.getName() 
					+ " to " + bindingTarget.getName() + " is not supported.");
		}
		return newGeometry;
	}
	
}
