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
package eu.esdihumboldt.hale.rcp.wizards.functions.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.transformer.impl.FilterTransformer;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.rcp.utils.ModelNavigationViewHelper;
import eu.esdihumboldt.hale.rcp.utils.ModelNavigationViewHelper.SelectionType;

/**
 * This {@link Wizard} is used to invoke a Renaming Transformer for the Source
 * Feature Type
 * 
 * @author Anna Pitaev, Logica; Simon Templer, Fraunhofer IGD
 * @version $Id$
 */
public class FilterWizard extends Wizard implements INewWizard {

	private static Logger _log = Logger.getLogger(FilterWizard.class);

	FilterWizardMainPage mainPage;
	FilterWizardSecondPage secondPage;

	/**
	 * Constructor
	 */
	public FilterWizard() {
		super();
		this.mainPage = new FilterWizardMainPage("Configure Filter Expression",
				"Configure Filter Expression");
		this.secondPage = new FilterWizardSecondPage(
				"Configure Filter Expression", "Configure Filter Expression");
		super.setWindowTitle("Configure Function");
		super.setNeedsProgressMonitor(true);

	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		_log.debug("Wizard.canFinish: " + this.secondPage.isPageComplete());
		return true;
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		Cell c = new Cell();
		Entity entity1 = ModelNavigationViewHelper.getEntity(SelectionType.SOURCE);
		Transformation t = new Transformation();
		t.setLabel(FilterTransformer.class.getName());

		List<IParameter> parameters = new ArrayList<IParameter>();
		parameters.add(new Parameter(FilterTransformer.CQL_PARAMETER, secondPage.buildCQL()));
		//parameters.add(new Parameter(FilterTransformer.CQL_PARAMETER, "OBJNR = 'BU500E6'"));
		//parameters.add(new Parameter(FilterTransformer.CQL_PARAMETER, "LEVEL < 2"));
		t.setParameters(parameters);

		entity1.setTransformation(t);
		c.setEntity1(entity1);
		c.setEntity2(entity1);
		
		AlignmentService alservice = (AlignmentService) PlatformUI
				.getWorkbench().getService(AlignmentService.class);
		// store transformation in AS
		alservice.addOrUpdateCell(c);
		
		// changing the background color in the schema viewer is now done in the
		// ModelNavigationViewLabelProvider class

		_log.debug("Transformation finished");
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		_log.debug("in init..");

	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		super.addPages();
		addPage(secondPage);
	}

}
