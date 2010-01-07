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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.geotools.feature.AttributeImpl;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.PropertyImpl;
import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
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
import eu.esdihumboldt.cst.transformer.AbstractCstFunction;
import eu.esdihumboldt.cst.transformer.exceptions.SpatialTypeNotSupportedException;
import eu.esdihumboldt.goml.omwg.Property;

/**
 * CstFunction for spatial type conversion.
 * 
 * 
 * @author Ulrich Schaeffler
 * @version $Id$
 */

public class SpatialTypeConversionFunction extends AbstractCstFunction {

	public static final String FROM = "FROM";
	public static final String TO = "TO";
	private Class<? extends Geometry> from;
	private Class<? extends Geometry> to;
	private Property sourceProperty = null;
	private Property targetProperty = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.esdihumboldt.cst.transformer.CstFunction#transform(org.geotools.feature
	 * .FeatureCollection)
	 */
	public FeatureCollection<? extends FeatureType, ? extends Feature> transform(
			FeatureCollection<? extends FeatureType, ? extends Feature> fc) {
		return null;
	}

	/**
	 * @see eu.esdihumboldt.cst.transformer.CstFunction#transform(org.opengis.feature.Feature,
	 *      org.opengis.feature.Feature)
	 */
	public Feature transform(Feature source, Feature target) {

		// FIXME: should be using sourceProperty/targetProperty
		Collection<org.opengis.feature.Property> c = new HashSet<org.opengis.feature.Property>();
		PropertyDescriptor pd = target.getDefaultGeometryProperty()
				.getDescriptor();

		GeometryFactory geomFactory = new GeometryFactory();
		Object newGeometry = null;

		// do the conversion
		if (this.from.equals(LineString.class)
				&& this.to.equals(MultiPoint.class)) {
			Geometry geom = (Geometry) source.getDefaultGeometryProperty()
					.getValue();
			Coordinate[] coords = geom.getCoordinates();
			newGeometry = geomFactory.createMultiPoint(coords);
			PropertyImpl p = new AttributeImpl(newGeometry,
					(AttributeDescriptor) pd, null);
			c.add(p);

		} else if (this.from.equals(Polygon.class)
				&& this.to.equals(MultiPoint.class)) {
			Geometry geom = (Geometry) source.getDefaultGeometryProperty()
					.getValue();
			Coordinate[] coords = geom.getCoordinates();
			newGeometry = geomFactory.createMultiPoint(coords);
			PropertyImpl p = new AttributeImpl(newGeometry,
					(AttributeDescriptor) pd, null);
			c.add(p);
		} else if (this.from.equals(MultiPolygon.class)
				&& this.to.equals(MultiPoint.class)) {
			Geometry geom = (Geometry) source.getDefaultGeometryProperty()
					.getValue();
			Coordinate[] coords = geom.getCoordinates();
			newGeometry = geomFactory.createMultiPoint(coords);
			PropertyImpl p = new AttributeImpl(newGeometry,
					(AttributeDescriptor) pd, null);
			c.add(p);
		} else if (this.from.equals(MultiLineString.class)
				&& this.to.equals(MultiPoint.class)) {
			Geometry geom = (Geometry) source.getDefaultGeometryProperty()
					.getValue();
			Coordinate[] coords = geom.getCoordinates();
			newGeometry = geomFactory.createMultiPoint(coords);
			PropertyImpl p = new AttributeImpl(newGeometry,
					(AttributeDescriptor) pd, null);
			c.add(p);
		} else if (this.from.equals(Polygon.class)
				&& this.to.equals(MultiLineString.class)) {
			Geometry geom = (Geometry) source.getDefaultGeometryProperty()
					.getValue();
			newGeometry = geom.getBoundary();
			PropertyImpl p = new AttributeImpl(newGeometry,
					(AttributeDescriptor) pd, null);
			c.add(p);
		} else if (this.from.equals(Polygon.class)
				&& this.to.equals(LineString.class)) {
			Geometry geom = (Geometry) source.getDefaultGeometryProperty()
					.getValue();
			Coordinate[] coords = geom.getCoordinates();
			newGeometry = geomFactory.createLineString(coords);
			PropertyImpl p = new AttributeImpl(newGeometry,
					(AttributeDescriptor) pd, null);
			c.add(p);
		} else if (this.from.equals(MultiPolygon.class)
				&& this.to.equals(MultiLineString.class)) {
			Geometry geom = (Geometry) source.getDefaultGeometryProperty()
					.getValue();
			newGeometry = geom.getBoundary();
			PropertyImpl p = new AttributeImpl(newGeometry,
					(AttributeDescriptor) pd, null);
			c.add(p);
		} else if (this.from.equals(MultiPolygon.class)
				&& this.to.equals(LineString.class)) {
			Geometry geom = (Geometry) source.getDefaultGeometryProperty()
					.getValue();
			Coordinate[] coords = geom.getCoordinates();
			newGeometry = geomFactory.createLineString(coords);
			PropertyImpl p = new AttributeImpl(newGeometry,
					(AttributeDescriptor) pd, null);
			c.add(p);
		}

		else {
			throw new SpatialTypeNotSupportedException(
					"Spatial type not supported");
		}

		target.setValue(c);
		return target;
	}

	@SuppressWarnings("unchecked")
	public boolean configure(Map<String, String> parametersValues) {
		try {
			this.from = (Class<? extends Geometry>) Class
					.forName(parametersValues.get(FROM));
			this.to = (Class<? extends Geometry>) Class
					.forName(parametersValues.get(TO));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return true;
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

	@Override
	protected void setParametersTypes(Map<String, Class<?>> parametersTypes) {
		parametersTypes.put(SpatialTypeConversionFunction.FROM, Geometry.class);
		parametersTypes.put(SpatialTypeConversionFunction.TO, Geometry.class);
		
	}
	
	
}
