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

import org.opengis.feature.Feature;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import eu.esdihumboldt.commons.goml.align.Cell;
import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.commons.goml.rdf.About;
import eu.esdihumboldt.commons.tools.FeatureInspector;
import eu.esdihumboldt.specification.cst.AbstractCstFunction;
import eu.esdihumboldt.specification.cst.CstFunction;
import eu.esdihumboldt.specification.cst.align.ICell;


/**
 * 
 * @author Ulrich Schaeffler 
 * @partner 14 / TUM
 * @version $Id$ 
 */
public class CentroidFunction extends AbstractCstFunction {
	private Property sourceProperty = null;
	private Property targetProperty = null;

	
	/**
	 * @see CstFunction#transform(Feature, Feature)
	 */
	public Feature transform(Feature source, Feature target) {		
		Geometry geom = (Geometry) FeatureInspector.getPropertyValue(source, sourceProperty.getAbout(), null);
		if (geom != null) {
			//get Centroid from old geom and store in new geom
			Point newGeometry = geom.getCentroid();
			FeatureInspector.setPropertyValue(target, targetProperty.getAbout(), newGeometry);
		}

		return target;
	}

	/**
	 * @see CstFunction#configure(ICell)
	 */
	public boolean configure(ICell cell) {
		this.sourceProperty = (Property) cell.getEntity1();
		this.targetProperty = (Property) cell.getEntity2();
		return true;
	}
	
	/**
	 * @see CstFunction#getParameters()
	 */
	public Cell getParameters() {
		Cell parameterCell = new Cell();
		Property entity1 = new Property(new About("")); //$NON-NLS-1$
		
		// Setting of type condition for entity1
		List <String> entityTypes = new ArrayList <String>();
		entityTypes.add(com.vividsolutions.jts.geom.Geometry.class.getName());
		entityTypes.add(org.opengis.geometry.Geometry.class.getName());
		entity1.setTypeCondition(entityTypes);
		
		Property entity2 = new Property(new About("")); //$NON-NLS-1$

		// Setting of type condition for entity2
		entityTypes = new ArrayList <String>();
		entityTypes.add(com.vividsolutions.jts.geom.Point.class.getName());
		entityTypes.add(org.opengis.geometry.primitive.Point.class.getName());
		entityTypes.add(com.vividsolutions.jts.geom.Geometry.class.getName());
		entity2.setTypeCondition(entityTypes);
				
		parameterCell.setEntity1(entity1);
		parameterCell.setEntity2(entity2);
		return parameterCell;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
