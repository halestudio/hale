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
package eu.esdihumboldt.hale.rcp.wizards.functions.core.math;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.commons.goml.align.Entity;
import eu.esdihumboldt.commons.goml.oml.ext.Parameter;
import eu.esdihumboldt.commons.goml.oml.ext.Transformation;
import eu.esdihumboldt.commons.goml.rdf.Resource;
import eu.esdihumboldt.cst.corefunctions.GenericMathFunction;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.Messages;
import eu.esdihumboldt.hale.ui.model.functions.AbstractSingleComposedCellWizard;
import eu.esdihumboldt.hale.ui.model.functions.AlignmentInfo;
import eu.esdihumboldt.specification.cst.align.ICell;
import eu.esdihumboldt.specification.cst.align.ext.IParameter;

/**
 * Generic math function wizard
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class GenericMathFunctionWizard extends
		AbstractSingleComposedCellWizard {
	
	private MathFunctionPage mainPage;

	/**
	 * @see AbstractSingleComposedCellWizard#AbstractSingleComposedCellWizard(AlignmentInfo)
	 */
	public GenericMathFunctionWizard(AlignmentInfo selection) {
		super(selection);
	}

	/**
	 * @see AbstractSingleComposedCellWizard#init()
	 */
	@Override
	protected void init() {
		ICell cell = getResultCell();
		
		String expression = null;
		
		// init expression from cell
		if (cell.getEntity1().getTransformation() != null) {
			List<IParameter> parameters = cell.getEntity1().getTransformation().getParameters();
			
			if (parameters != null) {
				Iterator<IParameter> it = parameters.iterator();
				while (it.hasNext() && expression == null) {
					IParameter param = it.next();
					if (param.getName().equals(
							GenericMathFunction.EXPRESSION_PARAMETER_NAME)) {
						expression = param.getValue();
					}
				}
			}
		}
		
		mainPage = new MathFunctionPage(
				"main", Messages.GenericMathFunctionWizard_1, null); //$NON-NLS-1$
		
		mainPage.setInitialExpression(expression);
	}

	/**
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		ICell cell = getResultCell();
		
		Transformation t = new Transformation();
		t.setService(new Resource(GenericMathFunction.class.getName()));
		t.getParameters().add(
				new Parameter(
						GenericMathFunction.EXPRESSION_PARAMETER_NAME, 
						mainPage.getExpression()));
		
		((Entity) cell.getEntity1()).setTransformation(t);
		
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
