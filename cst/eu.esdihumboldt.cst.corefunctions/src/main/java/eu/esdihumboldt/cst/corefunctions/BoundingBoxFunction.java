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
 * Classname    : eu.esdihumboldt.cst.transformer/BoundingBoxFunction.java 
 * 
 * Author       : Josef Bezdek
 * 
 * Created on   : Dec, 2009
 *
 */

package eu.esdihumboldt.cst.corefunctions;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.geotools.feature.AttributeImpl;
import org.geotools.feature.PropertyImpl;
import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.PropertyDescriptor;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.AbstractCstFunction;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;

public class BoundingBoxFunction extends AbstractCstFunction {
	

	public Feature transform(Feature source, Feature target) {
		Collection<org.opengis.feature.Property> c = new HashSet<org.opengis.feature.Property>();
		PropertyDescriptor pd = target.getDefaultGeometryProperty().getDescriptor();
		
		Geometry geom = (Geometry)source.getDefaultGeometryProperty().getValue();
		Object newGeometry = geom.getEnvelope();
		
		PropertyImpl p = new AttributeImpl(newGeometry, (AttributeDescriptor) pd, null);	
		c.add(p);
		target.setValue(c);
		return target;
	}

	public boolean configure(ICell cell) {
		// No Parameters needed -> return false
		return false;
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
			// 	entity2 has same conditions as entity1
		entity2.setTypeCondition(entityTypes);
		
		parameterCell.setEntity1(entity1);
		parameterCell.setEntity2(entity2);
		return parameterCell;
	}

	@Override
	public String getDescription() {
		return "";
	}
		
}

