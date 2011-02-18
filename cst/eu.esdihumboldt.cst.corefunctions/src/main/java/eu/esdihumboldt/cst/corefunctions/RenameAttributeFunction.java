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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.convert.ConversionException;
import org.apache.commons.convert.Converter;
import org.apache.commons.convert.Converters;
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
import eu.esdihumboldt.cst.CstFunction;
import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.Resource;
import eu.esdihumboldt.tools.FeatureInspector;

/**
 * CstFunction for attribute renaming, i.e. the copying of attributes of the 
 * same type from a source to a target property.
 * 
 * @author Thorsten Reitz, Jan Jezek, Simon Templer
 * @version $Id: RenameAttributeFunction.java 2418 2009-12-22 11:35:12Z jjezek $ 
 */
public class RenameAttributeFunction 
	extends AbstractCstFunction {
	
	/**
	 * SourceEntiy
	 */
	private Property sourceEntity;
	
	/**
	 * TargetEntity
	 */
	private Property targetEntity;

	/**
	 * This transform implementation can copy any literal attribute value 
	 * (Strings, Numbers).
	 * 
	 * @see CstFunction#transform(Feature, Feature)
	 */
	@SuppressWarnings("unchecked")
	public Feature transform(Feature source, Feature target) {
		org.opengis.feature.Property sourceProperty = FeatureInspector.getProperty(source, this.sourceEntity.getAbout(), true);
		//XXX instead of creating the source property if it is not present, just skip the transformation?
		
		org.opengis.feature.Property targetProperty = FeatureInspector.getProperty(target, this.targetEntity.getAbout(), true);
		
		Class<?> bindingSource = sourceProperty.getType().getBinding();
		Class<?> bindingTarget = targetProperty.getType().getBinding();
		
		Object value = sourceProperty.getValue();
		
		if (value != null) {
			if (bindingTarget.isAssignableFrom(bindingSource)) {
				// do a direct copy if the target is directly assignable from the source type
				// value mustn't be changed
			}
			else if (bindingTarget.isAssignableFrom(value.getClass())) {
				// do a direct copy if the target is directly assignable from the source value
				// value mustn't be changed
			}
			else {
				Converter<Object, Object> converter;
				try {
					converter = (Converter<Object, Object>) Converters.getConverter(bindingSource, bindingTarget);
				} catch (ClassNotFoundException e) {
					// no converter could be found
					converter = null;
				}
				
				boolean success = false;
				if (converter != null) {
					try {
						value = converter.convert(value);
						success = true;
					} catch (ConversionException e) {
						// ignore
					}
				}
				
				if (!success) {
					// fall back to internal conversion (as used before)
					
					if (Geometry.class.isAssignableFrom(bindingSource) 
							&& Geometry.class.isAssignableFrom(bindingTarget)) {
						// geometry conversion
						value = convertSpatialType(bindingSource, bindingTarget, 
								(Geometry) value);
					}
					else if (bindingSource.equals(String.class) 
							&& bindingTarget.equals(Integer.class)) {
						// convert string to int
						value = Integer.parseInt(value.toString());
					}
					else if (bindingSource.equals(String.class) 
							&& bindingTarget.equals(Long.class)) {
						// convert string to long
						value = Long.parseLong(value.toString());
					}
					else if (bindingSource.equals(String.class) 
							&& bindingTarget.equals(Float.class)) {
						// convert string to float
						value = Float.parseFloat(value.toString());
					}
					else if (bindingSource.equals(String.class) 
							&& bindingTarget.equals(Double.class)) {
						// convert string to double
						value = Double.parseDouble(value.toString());
					}
					else if (bindingSource.equals(String.class) 
							&& bindingTarget.equals(BigDecimal.class)) {
						value = new BigDecimal(Double.parseDouble(value.toString()));
					}
					else if (bindingSource.equals(Float.class) 
									|| bindingSource.equals(Double.class) 
									|| bindingSource.equals(Integer.class) 
									|| bindingSource.equals(Long.class)
									|| bindingSource.equals(BigInteger.class) 
							&& bindingTarget.equals(BigDecimal.class)) {
						value = new BigDecimal(Double.parseDouble(value.toString()));
					}
					else if (bindingTarget.equals(String.class)) {
						// convert to string
						value = value.toString();
					}
					else if (value instanceof Date && bindingTarget.isAssignableFrom(Timestamp.class)) {
						// Date to Timestamp
						value = new Timestamp(((Date) value).getTime());
					}
					else {
						throw new UnsupportedOperationException("For the given source (" 
								+ bindingSource.getName() +
								") and target (" + bindingTarget.getName() 
								+ ") attribute bindings, this rename function " +
								"cannot be used.");
					}
				}
			}
		}
		
		// set the target property
		targetProperty.setValue(value);
		
		return target;
	}

	/**
	 * @see CstFunction#configure(ICell)
	 */
	public boolean configure(ICell cell) {
		this.sourceEntity = (Property) cell.getEntity1();
		this.targetEntity = (Property) cell.getEntity2();
		/*for (IParameter p : cell.getEntity1().getTransformation().getParameters()) {
			if (p.getName().equals(NESTED_ATTRIBUTE_PATH)) {
				if (p.getValue() != null && !p.getValue().equals("")) {
					this.nestedAttributePath = p.getValue();
				}
			}
		}*/
		return true;
	}
	
	/**
	 * @see CstFunction#getParameters()
	 */
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
	
	/**
	 * @param bindingSource
	 * @param bindingTarget
	 * @param geom
	 * @return newGeometry
	 */
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

	@Override
	public String getDescription() {
		return "CstFunction for attribute renaming, i.e. the copying of attributes of the same type from a source to a target property.";
	}
	
}
