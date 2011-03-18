/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
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
import java.util.List;

import org.opengis.feature.Feature;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.cst.AbstractCstFunction;
import eu.esdihumboldt.cst.CstFunction;
import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.tools.FeatureInspector;

/**
 * {@link CstFunction} to calculate the area of a given geometry.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class CalculateArea extends AbstractCstFunction {
	private Property sourceProperty = null;
	private Property targetProperty = null;
	
	@Override
	public Feature transform(Feature source, Feature target) {
		Geometry geom = (Geometry) FeatureInspector.getPropertyValue(source, sourceProperty.getAbout(), null);
		if (geom != null) {
			Double area = geom.getArea();
			FeatureInspector.setPropertyValue(target, targetProperty.getAbout(), area);
		}

		return target;
	}

	@Override
	public boolean configure(ICell cell) {
		this.sourceProperty = (Property) cell.getEntity1();
		this.targetProperty = (Property) cell.getEntity2();
		return true;
	}

	@Override
	public ICell getParameters() {
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
		entityTypes.add(java.lang.Double.class.getName());
		entityTypes.add(java.lang.Float.class.getName());
		entity2.setTypeCondition(entityTypes);
				
		parameterCell.setEntity1(entity1);
		parameterCell.setEntity2(entity2);
		return parameterCell;
	}

	@Override
	public String getDescription() {
		return Messages.getString("CalculateArea.2"); //$NON-NLS-1$
	}

}
