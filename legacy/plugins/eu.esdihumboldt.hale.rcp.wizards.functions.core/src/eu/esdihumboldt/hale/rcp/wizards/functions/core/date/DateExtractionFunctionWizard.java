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
package eu.esdihumboldt.hale.rcp.wizards.functions.core.date;

import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.commons.goml.align.Entity;
import eu.esdihumboldt.commons.goml.oml.ext.Parameter;
import eu.esdihumboldt.commons.goml.oml.ext.Transformation;
import eu.esdihumboldt.commons.goml.rdf.Resource;
import eu.esdihumboldt.cst.corefunctions.DateExtractionFunction;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.Messages;
import eu.esdihumboldt.hale.ui.model.functions.AbstractSingleComposedCellWizard;
import eu.esdihumboldt.hale.ui.model.functions.AlignmentInfo;
import eu.esdihumboldt.specification.cst.align.ICell;


/**
 * Wizard for the {@link DateExtractionFunction}.
 * 
 * @author Ulrich Schaeffler
 * @partner 14 / TUM
 */
public class DateExtractionFunctionWizard extends AbstractSingleComposedCellWizard {

	private DateExtractionFunctionWizardPage mainPage;
	
	/**
	 * @see AbstractSingleComposedCellWizard#AbstractSingleComposedCellWizard(AlignmentInfo)
	 */
	public DateExtractionFunctionWizard(AlignmentInfo selection) {
		super(selection);
	}
	
	/**
	 * @see AbstractSingleComposedCellWizard#init()
	 */
	@Override
	protected void init() {
		this.mainPage = new DateExtractionFunctionWizardPage(
			Messages.DateExtractionFunctionWizard_0); 
		super.setWindowTitle(Messages.DateExtractionFunctionWizard_1); 
		super.setNeedsProgressMonitor(true);
	}

	/**
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		ICell cell = getResultCell();
		
		Transformation transformation = new Transformation();
		transformation.setService(new Resource(DateExtractionFunction.class.getName()));
		transformation.getParameters().add(
				new Parameter(
						DateExtractionFunction.DATE_FORMAT_SOURCE, 
						mainPage.getSourceFormat()));
		transformation.getParameters().add(
				new Parameter(
						DateExtractionFunction.DATE_FORMAT_TARGET, 
						mainPage.getTargetFormat()));
		((Entity) cell.getEntity1()).setTransformation(transformation);
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
