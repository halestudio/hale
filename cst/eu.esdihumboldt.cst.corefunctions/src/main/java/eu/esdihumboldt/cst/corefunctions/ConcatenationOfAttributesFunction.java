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

import eu.esdihumboldt.cst.CstFunction;
import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.ComposedProperty;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;

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
	public static final String INTERNALSEPERATOR = "--!-split-!--";
	
	/**
	 * The name of the parameter for the separator.
	 */
	public static final String SEPERATOR = "seperator";
	
	/**
	 * The name of the parameter for the concatenation.
	 */
	public static final String CONCATENATION = "concatenation";
	
	/**
	 * 
	 */
	private String seperator;
	
	/**
	 * 
	 */
	private String concatenation;
	
	/**
	 * 
	 */
	private String targetPropertyname;

	/**
	 * @param cell
	 * @return true
	 * @see eu.esdihumboldt.cst.CstFunction#configure(eu.esdihumboldt.cst.align.ICell)
	 */
	@Override
	public boolean configure(ICell cell) {
		
		Transformation t = (Transformation) cell.getEntity1().getTransformation();
		this.seperator = t.getParameterMap().get(SEPERATOR).getValue();
		this.concatenation = t.getParameterMap().get(CONCATENATION).getValue();
		
		this.targetPropertyname = ((Property)cell.getEntity2()).getLocalname();
		return true;
	}

	/**
	 * @return the parameterCell
	 * @see eu.esdihumboldt.cst.CstFunction#getParameters()
	 */
	@Override
	public ICell getParameters() {

		Cell parameterCell = new Cell();
		
		ComposedProperty composedEntity1 = new ComposedProperty(new About(""));
		Property entity1 = new Property(new About(""));
		
		// Setting of type condition for entity1
		List <String> entityTypes = new ArrayList <String>();
		entityTypes.add(Number.class.getName());
		entityTypes.add(String.class.getName());
		entity1.setTypeCondition(entityTypes);
		
		Property entity2 = new Property(new About(""));
		 
		// Setting of type condition for entity2
		entityTypes = new ArrayList <String>();
		entityTypes.add(String.class.getName());
		entity2.setTypeCondition(entityTypes);
		
		List<IParameter> params = new ArrayList<IParameter>();
		Transformation t = new Transformation();
		IParameter seperator = new Parameter(ConcatenationOfAttributesFunction.SEPERATOR , "");
		IParameter concatenation = new Parameter(ConcatenationOfAttributesFunction.CONCATENATION , "");
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
	 * @param source
	 * @param target
	 * @return target
	 * @see eu.esdihumboldt.cst.CstFunction#transform(org.opengis.feature.Feature, org.opengis.feature.Feature)
	 */
	@Override
	public Feature transform(Feature source, Feature target) {
		String[] concat = this.concatenation.split(INTERNALSEPERATOR);
		String finalConcatString = "";
		boolean firstElement = true;
		for (String thisElement : concat) {
			org.opengis.feature.Property p = source.getProperty(thisElement);
			if (!firstElement) {
				finalConcatString += this.seperator;
			}
			
			if (p != null) {
				if (p.getValue() != null) {
					finalConcatString += p.getValue().toString();
				}
				else {
					finalConcatString += "";
				}
			}
			else {
				finalConcatString += thisElement;
			}
			firstElement = false;
		}
		target.getProperty(this.targetPropertyname).setValue(finalConcatString);
		return target;
	}

}
