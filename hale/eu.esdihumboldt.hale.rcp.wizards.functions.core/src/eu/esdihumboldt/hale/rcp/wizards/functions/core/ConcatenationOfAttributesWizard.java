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
package eu.esdihumboldt.hale.rcp.wizards.functions.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.align.ext.ITransformation;
import eu.esdihumboldt.cst.corefunctions.ConcatenationOfAttributesFunction;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.ComposedProperty;
import eu.esdihumboldt.goml.rdf.Resource;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleComposedCellWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;


/**
 * ConcatenationOfAttributesWizard
 * @author Stefan Gessner
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class ConcatenationOfAttributesWizard extends AbstractSingleComposedCellWizard {

	/**
	 * The main page
	 */
	private ConcatenationOfAttributesWizardPage mainPage;
	
	/**
	 * @see AbstractSingleComposedCellWizard#AbstractSingleComposedCellWizard(AlignmentInfo)
	 */
	public ConcatenationOfAttributesWizard(AlignmentInfo selection) {
		super(selection);
	}

	/**
	 * 
	 * @see AbstractSingleComposedCellWizard#init()
	 */
	@Override
	protected void init() {
		Cell cell = getResultCell();
		
		String separator = null;
		String concatenation = null;
		
		ITransformation trans = cell.getEntity1().getTransformation();
		if (trans != null) {
			List<IParameter> parameters = cell.getEntity1().getTransformation().getParameters();
			for (IParameter param : parameters) {
				if (param.getName().equals(ConcatenationOfAttributesFunction.SEPERATOR)) {
					separator = param.getValue();
				}
				else if (param.getName().equals(ConcatenationOfAttributesFunction.CONCATENATION)) {
					concatenation = param.getValue();
				}
			}
		}
		
		this.mainPage = new ConcatenationOfAttributesWizardPage(Messages.ConcatenationOfAttributesWizard_0);
		this.mainPage.setDescription(Messages.ConcatenationOfAttributesWizard_1) ;
		super.setWindowTitle(Messages.ConcatenationOfAttributesWizard_2);
		
		if (separator != null) {
			mainPage.setSeparator(separator);
		}
		
		if (concatenation != null) {
			String[] parts = concatenation.split(ConcatenationOfAttributesFunction.INTERNALSEPERATOR);
			mainPage.setParts(Arrays.asList(parts));
		}
	}

	/**
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		ICell cell = super.getResultCell();

		Transformation t = new Transformation();
		t.setService(new Resource(ConcatenationOfAttributesFunction.class
				.getName()));
		List<IParameter> parameters = new ArrayList<IParameter>();
		parameters.add(new Parameter(
				ConcatenationOfAttributesFunction.SEPERATOR, this.mainPage
						.getSeperatorText().getText()));

		String temp = "";
		int i = 0;
		for (String line : this.mainPage.getListViewer().getList().getItems()) {
			temp = temp + line;
			if (i < this.mainPage.getListViewer().getList().getItemCount() - 1) {
				temp = temp
						+ ConcatenationOfAttributesFunction.INTERNALSEPERATOR;
			}
		}
		parameters.add(new Parameter(
				ConcatenationOfAttributesFunction.CONCATENATION, temp));
		t.setParameters(parameters);
		((ComposedProperty) cell.getEntity1()).setTransformation(t);

		return true;
	}
	
	/**
	 * @see Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		
		addPage(this.mainPage);
	}

}
