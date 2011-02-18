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

import java.util.ArrayList;
import java.util.List;

import org.opengis.feature.Feature;

import com.iabcinc.jmep.Environment;
import com.iabcinc.jmep.Expression;
import com.iabcinc.jmep.XExpression;
import com.iabcinc.jmep.hooks.Constant;

import eu.esdihumboldt.cst.AbstractCstFunction;
import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.ComposedProperty;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;

/**
 * FIXME Add Type description.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class GenericMathFunction 
	extends AbstractCstFunction {
	
	public static final String EXPRESSION_PARAMETER_NAME = "math_expression";
	
	private String expression = null;
	private List<Property> variables = new ArrayList<Property>();
	private Property targetProperty = null;

	/**
	 * @see eu.esdihumboldt.cst.transformer.CstFunction#configure(eu.esdihumboldt.cst.align.ICell)
	 */
	public boolean configure(ICell cell) {
		for (IParameter ip : cell.getEntity1().getTransformation().getParameters()) {
			if (ip.getName().equals(GenericMathFunction.EXPRESSION_PARAMETER_NAME)) {
				this.expression = ip.getValue();
				break;
			}
		}
		if (this.expression == null) {
			throw new RuntimeException("The math_expression must be defined.");
		}
		if (cell.getEntity1() instanceof ComposedProperty) {
			for (Property p : ((ComposedProperty)cell.getEntity1()).getCollection()) {
				this.variables.add(p);
			}
		}
		else if (cell.getEntity1() instanceof Property) {
			this.variables.add((Property) cell.getEntity1());
		}
		
		this.targetProperty = (Property) cell.getEntity2();
		return true;
	}

	public Cell getParameters() {
		Cell parameterCell = new Cell();
	
		ComposedProperty  entity1 = new ComposedProperty(new About(""));
		
		// Setting of type condition for entity1
		List <String> entityTypes = new ArrayList <String>();
		entityTypes.add(String.class.getName());
		entityTypes.add(Number.class.getName());
		entity1.setTypeCondition(entityTypes);
		
		Property entity2 = new Property(new About(""));	
		
		// Setting of type condition for entity2
			// 	entity2 has same type conditions as entity1
		entity2.setTypeCondition(entityTypes);
		
		List<IParameter> params = new ArrayList<IParameter>();
		IParameter p = new Parameter(GenericMathFunction.EXPRESSION_PARAMETER_NAME, "");
		params.add(p);
		Transformation t = new Transformation();
		t.setParameters(params);
		
		entity1.setTransformation(t);		
		
		parameterCell.setEntity1(entity1);
		parameterCell.setEntity2(entity2);
		return parameterCell;
	}

	/**
	 * @see eu.esdihumboldt.cst.transformer.CstFunction#transform(org.opengis.feature.Feature, org.opengis.feature.Feature)
	 */
	public Feature transform(Feature source, Feature target) {
		try {
			Environment env = new Environment();
			for (Property inputVariable : this.variables) {
				Object value = source.getProperty(
						inputVariable.getLocalname()).getValue();
				Number number = Double.parseDouble(value.toString());
				env.addVariable(inputVariable.getLocalname(), 
						new Constant(number));
			}

			Expression ex = new Expression(this.expression, env);
			Object result = ex.evaluate();
			
			// inject result into target object
			org.opengis.feature.Property tProp = target.getProperty(
					this.targetProperty.getLocalname());
			if (tProp.getType().getBinding().equals(String.class)) {
				tProp.setValue(result.toString());	
			}
			else if (tProp.getType().getBinding().equals(Double.class)) {
				tProp.setValue(Double.parseDouble(result.toString()));	
			}
			else if (tProp.getType().getBinding().equals(Integer.class)) {
				tProp.setValue(Integer.parseInt(result.toString()));	
			}
			else {
				tProp.setValue(result);	
			}
		} catch (XExpression e) {
			throw new RuntimeException(e);
		}

		
		return target;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
