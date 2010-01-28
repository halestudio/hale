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

package eu.esdihumboldt.cst.corefunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;
import com.vividsolutions.jts.geom.Geometry;
import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.transformer.AbstractCstFunction;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.Resource;




/**
 * 
 * @author Ulrich Schaeffler 
 * @partner 14 / TUM
 * @version $Id$ 
 */

public class CentroidFunction extends AbstractCstFunction {
	Property sourceProperty = null;
	Property targetProperty = null;

	
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
		Geometry geom = (Geometry)source.getProperty(
				this.sourceProperty.getLocalname()).getValue();
		//get Centroid from old geom and store in new geom
		Object newGeometry = geom.getCentroid();
		((SimpleFeature)target).setAttribute(this.targetProperty.getLocalname(),newGeometry);

		return target;
	}

	/**
	 * @see eu.esdihumboldt.cst.transformer.CstFunction#configure(eu.esdihumboldt.cst.align.ICell)
	 */
	public boolean configure(ICell cell) {
		this.sourceProperty = (Property) cell.getEntity1();
		this.targetProperty = (Property) cell.getEntity2();
		return true;
	}


	@Override
	protected void setParametersTypes(Map<String, Class<?>> parametersTypes) {
		// TODO Auto-generated method stub
		
	}
	
	public Cell getParameters() {
		Cell parameterCell = new Cell();
		Property entity1 = new Property(new About(""));
		
		List <String> entity1Types = new ArrayList <String>();
		entity1Types.add("com.vividsolutions.jts.geom.Geometry.class");
		entity1.setTypeCondition(entity1Types);
		
		Property entity2 = new Property(new About(""));

		List <String> entity2Types = new ArrayList<String>();
		entity2Types.add("com.vividsolutions.jts.geom.Geometry.class");
		entity2.setTypeCondition(entity2Types);
				
		parameterCell.setEntity1(entity1);
		parameterCell.setEntity2(entity2);
		return parameterCell;
	}
	
	
}
