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

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;

import eu.esdihumboldt.commons.goml.align.Cell;
import eu.esdihumboldt.commons.goml.align.Entity;
import eu.esdihumboldt.commons.goml.oml.ext.Parameter;
import eu.esdihumboldt.commons.goml.oml.ext.Transformation;
import eu.esdihumboldt.commons.goml.omwg.FeatureClass;
import eu.esdihumboldt.commons.goml.rdf.About;
import eu.esdihumboldt.commons.goml.rdf.DetailedAbout;
import eu.esdihumboldt.commons.tools.FeatureInspector;
import eu.esdihumboldt.specification.cst.AbstractCstFunction;
import eu.esdihumboldt.specification.cst.CstFunction;
import eu.esdihumboldt.specification.cst.align.ICell;
import eu.esdihumboldt.specification.cst.align.ext.IParameter;

/**
 * This function will populate the nilReason attribute of any properties that 
 * have not yet been assigned a value.
 *
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class NilReasonFunction extends AbstractCstFunction {
	
	/**
	 * Name of the NilReasonType parameter
	 */
	public static final String PARAMETER_NIL_REASON_TYPE = "NilReasonType"; //$NON-NLS-1$
	
	private String nilReason = null;
	
	private Entity onEntity = null; 

	/**
	 * @see CstFunction#configure(ICell)
	 */
	public boolean configure(ICell cell) {
		this.onEntity = (Entity) cell.getEntity2();
		for(IParameter ip : cell.getEntity2().getTransformation().getParameters()) {
			if (ip.getName().equals(PARAMETER_NIL_REASON_TYPE)) {
				nilReason = ip.getValue();
			}
		}
		
		return true;
	}

	/**
	 * @see CstFunction#getParameters()
	 */
	public Cell getParameters() {
		Cell parameterCell = new Cell();	
				
		eu.esdihumboldt.commons.goml.omwg.Property entity2 = 
			new eu.esdihumboldt.commons.goml.omwg.Property(new About("")); //$NON-NLS-1$
		// Setting of type condition for entity2
		List <String> entity2Types = new ArrayList <String>();
		entity2Types.add(com.vividsolutions.jts.geom.Geometry.class.getName());
		entity2Types.add(org.opengis.geometry.Geometry.class.getName());
		entity2Types.add(String.class.getName());
		entity2Types.add(Number.class.getName());
		entity2Types.add(Boolean.class.getName());
		entity2Types.add(Date.class.getName());
		entity2Types.add(Collection.class.getName());
		entity2.setTypeCondition(entity2Types);
	
		Transformation t = new Transformation();
		List<IParameter> params = new ArrayList<IParameter>(); 
			
		Parameter p   = 
			new Parameter(PARAMETER_NIL_REASON_TYPE,""); //$NON-NLS-1$
		
		params.add(p);		
		entity2.setTransformation(t);	
		parameterCell.setEntity2(entity2);
		return parameterCell;
	}

	/**
	 * @see CstFunction#transform(Feature, Feature)
	 */
	public Feature transform(Feature source, Feature target) {
		if (nilReason != null) {
			if (onEntity instanceof eu.esdihumboldt.commons.goml.omwg.Property) {
				// get the property value
				Object value = FeatureInspector.getPropertyValue(target, onEntity.getAbout(), null);
				
				if (value == null) {
					// set nilReason on property
					FeatureInspector.setPropertyValue(
							target, 
							new DetailedAbout(onEntity.getAbout().getAbout() + DetailedAbout.PROPERTY_DELIMITER + "nilReason", true),  //$NON-NLS-1$
							nilReason.toString());
				}
			}
			else if (onEntity instanceof FeatureClass) {
				// apply on all properties
				//TODO check name?
				
				for (Property property : target.getProperties()) { //XX does this really get all properties? or only the existing ones?
					if (property.getValue() == null) {
						String propertyName = property.getName().getLocalPart();
						
						List<String> properties = new ArrayList<String>();
						properties.add(propertyName);
						properties.add("nilReason"); //$NON-NLS-1$
						
						FeatureInspector.setPropertyValue(target, properties, nilReason.toString());
					}
				}
			}
		}
		
		return target;
	}

	@Override
	public String getDescription() {
		return Messages.getString("NilReasonFunction.1"); //$NON-NLS-1$
	}
	
}
