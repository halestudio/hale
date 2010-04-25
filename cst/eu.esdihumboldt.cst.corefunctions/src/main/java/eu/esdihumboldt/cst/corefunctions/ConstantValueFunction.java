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
import org.opengis.feature.type.PropertyDescriptor;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.AbstractCstFunction;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;

/**
 * CST Function to set default
 * attribute target values.
 *
 * @author Ulrich Schaeffler, Anna Pitaev
 * @partner 14 / TUM, 04 / Logica
 * @version $Id$ 
 */
public class ConstantValueFunction extends AbstractCstFunction {
	
	public static final String DEFAULT_VALUE_PARAMETER_NAME = "defaultValue";
	private Object defaultValue = null;
	private Property targetProperty = null;

	/**
	 * @see eu.esdihumboldt.cst.transformer.CstFunction#configure(eu.esdihumboldt.cst.align.ICell)
	 */
	public boolean configure(ICell cell) {
		for (IParameter ip : cell.getEntity2().getTransformation().getParameters()) {
			if (ip.getName().equals(ConstantValueFunction.DEFAULT_VALUE_PARAMETER_NAME)) {
				this.defaultValue = ip.getValue();
			}
		}
		this.targetProperty = (Property) cell.getEntity2();
		return true;
	}
	
	public Cell getParameters() {
		Cell parameterCell = new Cell();	
		Property entity2 = new Property(new About(""));
		
		// Setting of type condition for entity2
		List <String> entity2Types = new ArrayList <String>();
		entity2Types.add("java.lang.String");
		entity2Types.add("java.lang.Number");
		entity2Types.add("java.lang.Boolean");
		entity2Types.add("java.util.Date");		
		entity2.setTypeCondition(entity2Types);
		
				
		Transformation t = new Transformation();
		List<IParameter> params = new ArrayList<IParameter>(); 
		Parameter p = new Parameter(ConstantValueFunction.DEFAULT_VALUE_PARAMETER_NAME,"");
		params.add(p);
		t.setParameters(params);
		entity2.setTransformation(t);
		parameterCell.setEntity2(entity2);
		return parameterCell;
	}

	/**
	 * @see eu.esdihumboldt.cst.transformer.CstFunction#transform(org.opengis.feature.Feature, org.opengis.feature.Feature)
	 */
	public Feature transform(Feature source, Feature target) {
		PropertyDescriptor pd = target.getProperty(
				this.targetProperty.getLocalname()).getDescriptor();
		
		org.opengis.feature.Property p = target.getProperty(this.targetProperty.getLocalname());
		
		if (pd.getType().getBinding().isPrimitive()) {
			
			if (pd.getType().getBinding().equals(Integer.class)){
				p.setValue((Integer)this.defaultValue);
			}
			else if (pd.getType().getBinding().equals(Short.class)){
				p.setValue((Short)this.defaultValue);
			}
			else if (pd.getType().getBinding().equals(Double.class)){
				p.setValue((Double)this.defaultValue);
			}
			else if (pd.getType().getBinding().equals(Long.class)){
				p.setValue((Long)this.defaultValue);
			}
			else if (pd.getType().getBinding().equals(Float.class)){
				p.setValue((Float)this.defaultValue);
			}
			else if (pd.getType().getBinding().equals(Boolean.class)){
				p.setValue((Boolean)this.defaultValue);
			}
			else if (pd.getType().getBinding().equals(Byte.class)){
				p.setValue((Byte)this.defaultValue);
			}
			else {
				p.setValue((Character)this.defaultValue);
			}

		}
		else if (pd.getType().getBinding().equals(String.class)){
			p.setValue(this.defaultValue.toString());
		}
		
		return target;
	}


}
