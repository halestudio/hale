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
import java.util.Arrays;
import java.util.List;

import org.opengis.feature.Feature;

import eu.esdihumboldt.cst.CstFunction;
import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.ComposedProperty;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.DetailedAbout;
import eu.esdihumboldt.tools.FeatureInspector;

/**
 * ConcatenationOfAttributesFunction
 * @author Stefan Gessner
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class ConcatenationOfAttributesFunction implements CstFunction{
	
	/**
	 * The name of the parameter for the separator.
	 */
	public static final String INTERNALSEPERATOR = "--!-split-!--"; //$NON-NLS-1$
	
	/**
	 * The name of the parameter for the separator.
	 */
	public static final String SEPERATOR = "seperator"; //$NON-NLS-1$
	
	/**
	 * The name of the parameter for the concatenation.
	 */
	public static final String CONCATENATION = "concatenation"; //$NON-NLS-1$
	
	/**
	 * 
	 */
	private String seperator;
	
	/**
	 * 
	 */
	private String concatenation;
	
	/**
	 * The target property
	 */
	private Property targetProperty;

	/**
	 * @see CstFunction#configure(ICell)
	 */
	@Override
	public boolean configure(ICell cell) {
		
		Transformation t = (Transformation) cell.getEntity1().getTransformation();
		this.seperator = t.getParameterMap().get(SEPERATOR).getValue();
		this.concatenation = t.getParameterMap().get(CONCATENATION).getValue();
		
		this.targetProperty = (Property)cell.getEntity2();
		return true;
	}

	/**
	 * @see CstFunction#getParameters()
	 */
	@Override
	public ICell getParameters() {

		Cell parameterCell = new Cell();
		
		ComposedProperty composedEntity1 = new ComposedProperty(new About("")); //$NON-NLS-1$
		Property entity1 = new Property(new About("")); //$NON-NLS-1$
		
		// Setting of type condition for entity1
		List <String> entityTypes = new ArrayList <String>();
		entityTypes.add(Number.class.getName());
		entityTypes.add(String.class.getName());
		entity1.setTypeCondition(entityTypes);
		
		Property entity2 = new Property(new About("")); //$NON-NLS-1$
		 
		// Setting of type condition for entity2
		entityTypes = new ArrayList <String>();
		entityTypes.add(String.class.getName());
		entity2.setTypeCondition(entityTypes);
		
		List<IParameter> params = new ArrayList<IParameter>();
		Transformation t = new Transformation();
		IParameter seperator = new Parameter(ConcatenationOfAttributesFunction.SEPERATOR , ""); //$NON-NLS-1$
		IParameter concatenation = new Parameter(ConcatenationOfAttributesFunction.CONCATENATION , ""); //$NON-NLS-1$
		params.add(seperator);
		params.add(concatenation);
		t.setParameters(params);
		composedEntity1.setTransformation(t);		
		
		composedEntity1.getCollection().add(entity1);
		composedEntity1.getCollection().add(entity2);
		parameterCell.setEntity1(composedEntity1);	
		parameterCell.setEntity2(entity2);
		
		return parameterCell;
	}

	/**
	 * @see CstFunction#transform(Feature, Feature)
	 */
	@Override
	public Feature transform(Feature source, Feature target) {
		String[] concat = this.concatenation.split(INTERNALSEPERATOR);
		String finalConcatString = ""; //$NON-NLS-1$
		for (String thisElement : concat) {
			String[] properties = thisElement.split(String.valueOf(DetailedAbout.PROPERTY_DELIMITER));
			Object value = FeatureInspector.getPropertyValue(source, Arrays.asList(properties), thisElement);
			
			if (finalConcatString.length() > 0) {
				finalConcatString += this.seperator;
			}
			
			if (value != null) {
				finalConcatString += value.toString();
			}
			else {
//				finalConcatString += ""; //$NON-NLS-1$
				// treat variable name as value
				//FIXME should be further improved to check on the schema information if there is any property defined
				finalConcatString += thisElement;
			}
		}
		
		FeatureInspector.setPropertyValue(target, targetProperty.getAbout(), finalConcatString);
		
		return target;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
