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
package eu.esdihumboldt.hale.rcp.wizards.functions.classification;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IValueExpression;
import eu.esdihumboldt.cst.transformer.impl.ClassificationMappingFunction;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.oml.ext.ValueExpression;
import eu.esdihumboldt.goml.omwg.ComparatorType;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.omwg.Restriction;
import eu.esdihumboldt.goml.rdf.Resource;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;

/**
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 *
 */
public class ClassificationMappingFunctionWizard extends
		AbstractSingleCellWizard {
	
	private ClassificationMappingPage mainPage;

	/**
	 * @see AbstractSingleCellWizard#AbstractSingleCellWizard(AlignmentInfo)
	 */
	public ClassificationMappingFunctionWizard(AlignmentInfo selection) {
		super(selection);
	}

	/**
	 * @see AbstractSingleCellWizard#init()
	 */
	@Override
	protected void init() {
		ICell cell = getResultCell();
		
		Property sourceProperty = (Property) cell.getEntity1();
		Property targetProperty = (Property) cell.getEntity2();
		
		Transformation t = new Transformation();
		t.setService(new Resource(ClassificationMappingFunction.class.toString()));
		sourceProperty.setTransformation(t);
		
		List<Restriction> sourceRestrictions = sourceProperty.getValueCondition();
		List<Restriction> targetRestrictions = targetProperty.getValueCondition();
		
		Map<String, Set<String>> classifications = new TreeMap<String, Set<String>>();
		
		if (sourceRestrictions != null && targetRestrictions != null && !sourceRestrictions.isEmpty() && !targetRestrictions.isEmpty()) {
			for (Restriction restriction : targetRestrictions) {
				if (restriction.getComparator().equals(ComparatorType.ONE_OF)
						&& restriction.getValue() != null && restriction.getValue().size() == 1) {
					BigInteger seqId = restriction.getSeq();
					String className = restriction.getValue().get(0).getLiteral();
					Restriction sourceRestriction = null; 
					
					// find corresponding source restriction
					Iterator<Restriction> it = sourceRestrictions.iterator();
					while (it.hasNext() && sourceRestriction == null) {
						Restriction candidate = it.next();
						if (candidate.getSeq().equals(seqId)) {
							sourceRestriction = candidate;
						}
					}
					
					// add classification
					if (sourceRestriction != null && sourceRestriction.getValue() != null
							&& !sourceRestriction.getValue().isEmpty()) {
						Set<String> valueSet = new TreeSet<String>();
						
						for (IValueExpression value : sourceRestriction.getValue()) {
							valueSet.add(value.getLiteral());
						}
						
						classifications.put(className, valueSet);
					}
				}
			}
		}
		
		mainPage = new ClassificationMappingPage("main", "Classification", null);
		mainPage.addClassifications(classifications);
	}

	/**
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		Map<String, Set<String>> classifications = mainPage.getClassifications();
		
		ICell cell = getResultCell();
		
		Property sourceProperty = (Property) cell.getEntity1();
		Property targetProperty = (Property) cell.getEntity2();
		
		long seq = 1;
		
		List<Restriction> sourceValueConditions = new ArrayList<Restriction>();
		List<Restriction> targetValueConditions = new ArrayList<Restriction>();
		
		for (Entry<String, Set<String>> classification : classifications.entrySet()) {
			BigInteger seqId = BigInteger.valueOf(seq++);
			
			// source restriction
			List<IValueExpression> valueExpressions = new ArrayList<IValueExpression>();
			for (String value : classification.getValue()) {
				valueExpressions.add(new ValueExpression(value));
			}
			Restriction r = new Restriction(sourceProperty, valueExpressions);
			r.setSeq(seqId);
			r.setComparator(ComparatorType.ONE_OF);
			
			sourceValueConditions.add(r);
			
			// target restriction
			valueExpressions = new ArrayList<IValueExpression>();
			valueExpressions.add(new ValueExpression(classification.getKey()));
			r = new Restriction(targetProperty, valueExpressions);
			r.setSeq(seqId);
			r.setComparator(ComparatorType.ONE_OF);
			
			targetValueConditions.add(r);
		}
		
		sourceProperty.setValueCondition(sourceValueConditions);
		targetProperty.setValueCondition(targetValueConditions);
		
		return true;
	}

	/**
	 * @see Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		
		addPage(mainPage);
	}

}
