/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 *
 * Componet     : cst
 * 	 
 * Classname    : eu.esdihumboldt.cst.transformer/SpatialTypeConversionTransformer.java 
 * 
 * Author       : Ulrich Schaeffler
 * 
 * Created on   : Aug, 2009
 *
 */

package eu.esdihumboldt.cst.transformer.impl;


import java.util.Collection;
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

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.transformer.AbstractCstFunction;

/**
 * 
 * @author Ulrich Schaeffler 
 * @version $Id$ 
 */

public class CentroidFunction extends AbstractCstFunction {

	
	/* (non-Javadoc)
	 * @see eu.esdihumboldt.cst.transformer.CstFunction#transform(org.geotools.feature.FeatureCollection)
	 */
	public FeatureCollection<? extends FeatureType, ? extends Feature> transform(
			FeatureCollection<? extends FeatureType, ? extends Feature> fc) {
		return null;
	}

	
	/**
	 * @see eu.esdihumboldt.cst.transformer.CstFunction#transform(org.opengis.feature.Feature, org.opengis.feature.Feature)
	 */
	public Feature transform(Feature source, Feature target) {
		Collection<org.opengis.feature.Property> c = new HashSet<org.opengis.feature.Property>();
		PropertyDescriptor pd = target.getDefaultGeometryProperty().getDescriptor();
		GeometryFactory geomFactory = new GeometryFactory();
		Geometry geom = (Geometry)source.getDefaultGeometryProperty().getValue();
		Coordinate[] coords = geom.getCoordinates();
		//get Centroid from old geom and store in new geom
		Object newGeometry = geom.getCentroid();
		PropertyImpl p = new AttributeImpl(newGeometry, (AttributeDescriptor) pd, null);	
		c.add(p);
		target.setValue(c);
		return target;
	}

	/**
	 * @see eu.esdihumboldt.cst.transformer.CstFunction#configure(eu.esdihumboldt.cst.align.ICell)
	 */
	public boolean configure(ICell cell) {
		// No Parameters needed -> return false
		return false;
	}
	
	@Override
	protected void setParametersTypes(Map<String, Class<?>> parameters) {
		//No parameters needed so leaving empty		
	}
}
